package io.github.reinershir.ai.controller;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.unfbx.chatgpt.entity.chat.Message;

import io.github.reinershir.ai.entity.WebRequestMessage;
import io.github.reinershir.ai.service.ChatGPTService;
import io.github.reinershir.auth.core.support.AuthorizeManager;
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
		
		chatGPTService.chatCompletions(requestDTO, request.getSession().getId(),StringUtils.hasText(requestDTO.getModel())?
				requestDTO.getModel():"gpt-3.5-turbo", sseEmitter);
		return Result.ok();
	}
}
