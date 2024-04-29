package io.github.reinershir.ai.controller;

import java.io.IOException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.lark.oapi.Client;
import com.lark.oapi.core.utils.Jsons;
import com.lark.oapi.service.im.v1.enums.MsgTypeEnum;
import com.lark.oapi.service.im.v1.enums.ReceiveIdTypeEnum;
import com.lark.oapi.service.im.v1.model.CreateMessageReq;
import com.lark.oapi.service.im.v1.model.CreateMessageReqBody;
import com.lark.oapi.service.im.v1.model.CreateMessageResp;
import com.lark.oapi.service.im.v1.model.ext.MessageText;
import com.unfbx.chatgpt.entity.chat.Message;

import io.github.reinershir.ai.entity.EventEntity;
import io.github.reinershir.ai.entity.PullMessageEntity;
import io.github.reinershir.ai.entity.Rs;
import io.github.reinershir.ai.entity.WebRequestMessage;
import io.github.reinershir.ai.service.ChatGPTService;
import io.github.reinershir.ai.tools.Decrypt;
import io.github.reinershir.auth.core.support.AuthorizeManager;
import io.github.reinershir.auth.entity.TokenInfo;
import io.github.reinershir.boot.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@RestController
@Schema(description =  "ChatGPT请求接口")
@RequestMapping({"/gpt/chat"})
@Slf4j
public class ChatController {

	@Autowired
	ChatGPTService chatGPTService;
	@Autowired(required = false)
	AuthorizeManager authorizeManager;
	//TODO simpl cache
	private Map<String,SseEmitter> cache = new LinkedHashMap<>();
	ExecutorService executorService = Executors.newFixedThreadPool(48);
	
	@Value("${thirdParty.feishu.appId}")
	private String appId;
	@Value("${thirdParty.feishu.appSecret}")
	private String appSecret;
	@Value("${thirdParty.feishu.encryptKey}")
	private String encryptKey;
	
	@Operation(summary = "请求chatGPT对话（单次）", description = "请求chatGPT对话（单次）")
	@GetMapping("")
	public Result<String> askOne(@RequestParam("question") String question,HttpServletRequest request){
		return Result.ok(chatGPTService.chat(question, request.getSession().getId(), "gpt-3.5-Trube"));
	}
	
	@Operation(summary = "connect chatGPT server", description = "connect chatGPT server")
	@GetMapping(value="connect",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public SseEmitter connect( HttpServletRequest request) {
		String token = request.getHeader("Access-Token");
		//默认30秒超时,设置为0L则永不超时
        SseEmitter sseEmitter = new SseEmitter(0l);
        sseEmitter.onError(
                throwable -> {
                    try {
                        log.info("[{}]连接异常,{}", request.getSession().getId(), throwable.toString());
                        sseEmitter.send(SseEmitter.event()
                                .id(token)
                                .name("error！")
                                .data(Message.builder().content("error！").build())
                                .reconnectTime(3000));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        );
        try {
            sseEmitter.send(SseEmitter.event().reconnectTime(5000));
        } catch (IOException e) {
            e.printStackTrace();
        }
        cache.put(token, sseEmitter);
        log.info("[{}]创建sse连接成功！", token);
        return sseEmitter;
	}
	
	@Operation(summary = "OPENAI chatGPT stream", description = "OPENAI chatGPT stream")
	@PostMapping
	public Result<String> chat(@RequestBody WebRequestMessage requestDTO,HttpServletRequest request){
		String token = request.getHeader("Access-Token");
		SseEmitter sseEmitter = cache.get(token);
		log.info("sse =======================> {}",sseEmitter);
		if(sseEmitter==null) {
			log.warn("无法获取到sse对象，TOKEN:{}",token);
		}
		TokenInfo tokenInfo = authorizeManager.getTokenInfo(request);
		Long userId = null;
		if(tokenInfo!=null) {
			userId = Long.parseLong(tokenInfo.getUserId());
		}else {
			userId= 0l;
		}
		if(!StringUtils.hasText( requestDTO.getSessionId())) {
			requestDTO.setSessionId(request.getSession().getId());
		}
		chatGPTService.chatCompletions(requestDTO, 
				StringUtils.hasText(requestDTO.getSessionId())?requestDTO.getSessionId():request.getSession().getId(), sseEmitter,userId);
		return Result.ok();
	}
	
	
	
	/**
     * 功能描述: 用户提问OpenAi，返回答案
     */
    @Operation(summary = "feishu提问接口", description = "feishu提问接口")
    @RequestMapping(path = "/pull/message", method = RequestMethod.POST)
    public Rs<Object> openAiChat(@RequestBody PullMessageEntity pullMessage) {
        log.info("pull message --> " + com.alibaba.fastjson2.JSON.toJSONString(pullMessage));
        
        //第一次需要验证加密
        if(StringUtils.hasText(pullMessage.getEncrypt())) {
        	Decrypt d = new Decrypt(encryptKey);
        	try {
        		//返回解密后的内容
        		JSONObject json = JSONObject.parse(d.decrypt(pullMessage.getEncrypt()));
        		log.info("解密后参数：{}",json);
        		//返回解密后的字符串
        		pullMessage.setChallenge(json.getString("challenge"));
			} catch (Exception e) {
				 pullErrorMessage(pullMessage, e.getMessage());
	             log.error(e.getMessage());
			} 
        	
        }
        executorService.submit(() -> {
            System.out.println("异步线程 =====> 开始 =====> " + new Date());
            try {
                getRs(pullMessage);
            } catch (Exception e) {
                pullErrorMessage(pullMessage, e.getMessage());
                log.error(e.getMessage());
            }
            System.out.println("异步线程 =====> 结束 =====> " + new Date());
        });
        return new Rs<>(200, "成功", null, pullMessage.getChallenge());
    }

    private void pullErrorMessage(PullMessageEntity pullMessage, String errorMessage) {
        String chatId = pullMessage.getEvent().getMessage().getChat_id(); //聊天Id
        String replyContentFormat = errorMessage.replaceAll("\n", " ")
                .replaceAll("\"", "");
        System.out.println(replyContentFormat);
        // 构建client
        Client client = Client.newBuilder(appId, appSecret).build();

        // 使用Builder模式构建请求对象
        CreateMessageReq req = CreateMessageReq.newBuilder()
                .receiveIdType(ReceiveIdTypeEnum.CHAT_ID.getValue())
                .createMessageReqBody(CreateMessageReqBody.newBuilder()
                        .receiveId(chatId)
                        .msgType(MsgTypeEnum.MSG_TYPE_TEXT.getValue())
                        .content(MessageText.newBuilder()
                                .text("ChatGPT机器人摆烂了，请稍后再试～!  ")
                                .build())
                        .build())
                .build();

        try {
            // 发起请求，并处理结果
            CreateMessageResp resp = client.im().message().create(req);
            if (resp.getCode() != 0) {
                System.out.println(String.format("code:%d,msg:%s,err:%s"
                        , resp.getCode(), resp.getMsg(), Jsons.DEFAULT.toJson(resp.getError())));
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @Nullable
    private void getRs(PullMessageEntity pullMessage) throws Exception {
        EventEntity event = pullMessage.getEvent();

        String textContent = JSON.parseObject(event.getMessage().getContent()).getString("text");

        String openId = event.getSender().getSender_id().getOpen_id(); //用户Id
        String replyContent = chatGPTService.chat(textContent, openId,null);
        String chatId = event.getMessage().getChat_id(); //聊天Id

        String replyContentFormat = replyContent.replaceAll("\n", "\\\\n")
                .replaceAll("\"", "");
        System.out.println(replyContentFormat);
        // 构建client
        Client client = Client.newBuilder(appId, appSecret).build();

        // 使用Builder模式构建请求对象
        CreateMessageReq req = CreateMessageReq.newBuilder()
                .receiveIdType(ReceiveIdTypeEnum.CHAT_ID.getValue())
                .createMessageReqBody(CreateMessageReqBody.newBuilder()
                        .receiveId(chatId)
                        .msgType(MsgTypeEnum.MSG_TYPE_TEXT.getValue())
                        .content(MessageText.newBuilder()
                                .atUser(openId, "chat-gpt")
                                .text(replyContentFormat)
                                .build())
                        .build())
                .build(); 

        // 发起请求，并处理结果
        CreateMessageResp resp = client.im().message().create(req);
        if (resp.getCode() != 0) {
            log.error(String.format("code:%d,msg:%s,err:%s"
                    , resp.getCode(), resp.getMsg(), Jsons.DEFAULT.toJson(resp.getError())));
        }
    }
}
