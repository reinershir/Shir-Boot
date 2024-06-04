package io.github.reinershir.ai.service.impl;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.alibaba.fastjson2.JSON;
import com.unfbx.chatgpt.OpenAiClient;
import com.unfbx.chatgpt.OpenAiStreamClient;
import com.unfbx.chatgpt.entity.chat.ChatCompletion;
import com.unfbx.chatgpt.entity.chat.ChatCompletionResponse;
import com.unfbx.chatgpt.entity.chat.Message;
import com.unfbx.chatgpt.entity.common.DeleteResponse;
import com.unfbx.chatgpt.entity.completions.Completion;
import com.unfbx.chatgpt.entity.files.File;
import com.unfbx.chatgpt.entity.files.UploadFileResponse;
import com.unfbx.chatgpt.entity.fineTune.FineTuneDeleteResponse;
import com.unfbx.chatgpt.entity.fineTune.job.FineTuneJob;
import com.unfbx.chatgpt.entity.fineTune.job.FineTuneJobListResponse;
import com.unfbx.chatgpt.entity.fineTune.job.FineTuneJobResponse;
import com.unfbx.chatgpt.entity.images.Image;
import com.unfbx.chatgpt.entity.images.ImageResponse;
import com.unfbx.chatgpt.entity.images.Item;
import com.unfbx.chatgpt.sse.ConsoleEventSourceListener;
import com.unfbx.chatgpt.utils.TikTokensUtil;

import io.github.reinershir.ai.entity.WebRequestMessage;
import io.github.reinershir.ai.model.ChatHistory;
import io.github.reinershir.ai.service.ChatHistoryService;
import io.github.reinershir.ai.service.listeners.ApikeyUpdateListener;
import io.github.reinershir.ai.websocket.MessageHepler;
import io.github.reinershir.ai.websocket.OnCompletionEvebntListener;
import io.github.reinershir.ai.websocket.listener.OpenAISSEEventSourceListener;
import io.github.reinershir.boot.contract.DictionaryCodes;
import io.github.reinershir.boot.service.DictionaryService;
import io.github.reinershir.boot.service.listener.DictionaryListenerHandler;
import io.reactivex.Single;
import jakarta.websocket.Session;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.sse.EventSourceListener;

@Service
@Slf4j
public class ChatGPTServiceImpl  implements io.github.reinershir.ai.service.ChatGPTService {
	
	MessageHepler messageHepler;
    OpenAiStreamClient streamClient;
    OpenAiClient openAiClient;
    private String model;
    OkHttpClient httpClient;

	@Autowired
	ChatHistoryService chatHistoryService;
	DictionaryListenerHandler dictionaryListenerHandler;
    
    @Autowired
    public ChatGPTServiceImpl(MessageHepler messageHepler,DictionaryService dictionaryService,
    		DictionaryListenerHandler dictionaryListenerHandler,@Value("${spring.profiles.active}") String profiles) {
    	this.dictionaryListenerHandler = dictionaryListenerHandler;
    	this.messageHepler=messageHepler;
    	this.model="gpt-3.5-turbo-0613";
    	int reqTimeout = Integer.parseInt(dictionaryService.getValueByCode(DictionaryCodes.OPENAI_REQUEST_TIMEOUT));
    	String apiKey = dictionaryService.getValueByCode(DictionaryCodes.OPENAI_API_KEY);
    	List<String> apiKeys;
    	if(StringUtils.hasText(apiKey)&&apiKey.indexOf(",")!=-1) {
    		apiKeys = Arrays.asList(apiKey.split(","));
    	}else {
    		apiKeys = new ArrayList<>();
    		apiKeys.add(apiKey);
    	}
    	if("dev".equals(profiles)) {
    		httpClient= new OkHttpClient
	                .Builder()
	                .proxy(new Proxy(Proxy.Type.HTTP,new InetSocketAddress("127.0.0.1", 10809)))
	                //.addInterceptor(httpLoggingInterceptor)//自定义日志输出
	                .connectTimeout(reqTimeout, TimeUnit.SECONDS)//自定义超时时间
	                .writeTimeout(reqTimeout, TimeUnit.SECONDS)//自定义超时时间
	                .readTimeout(reqTimeout, TimeUnit.SECONDS)//自定义超时时间
	                .build();
		}else {
			httpClient= new OkHttpClient
	                .Builder()
	                //.addInterceptor(httpLoggingInterceptor)//自定义日志输出
	                .connectTimeout(reqTimeout, TimeUnit.SECONDS)//自定义超时时间
	                .writeTimeout(reqTimeout, TimeUnit.SECONDS)//自定义超时时间
	                .readTimeout(reqTimeout, TimeUnit.SECONDS)//自定义超时时间
	                .build();
		}
    	
    	
    	streamClient = OpenAiStreamClient.builder()
						.okHttpClient(httpClient)
				       .apiKey(apiKeys)
				       .build();
    	openAiClient = OpenAiClient.builder()
				        .apiKey(apiKeys)
				        //.keyStrategy(new KeyRandomStrategy())
				        .okHttpClient(httpClient)
				        .build();
    	//监听字典变更事件
    	dictionaryListenerHandler.addListener(DictionaryCodes.OPENAI_API_KEY.name(), new ApikeyUpdateListener(this));
    }
    
    public String chat(String prompt,String openId,String model){
    	model = StringUtils.hasText(model)?model:this.model;
        List<Message> allMessage = messageHepler.getCacheContextByOpenId(openId,model,null);
        //最新GPT-3.5-Turbo模型
        Message message = Message.builder().role(Message.Role.USER).content(prompt).build();
        allMessage.add(message);
        log.info("发送问题：{}，openId:{}",JSON.toJSONString(message),openId);
        log.info("附带的历史记录：{}",JSON.toJSONString(allMessage));
        ChatCompletion chatCompletion = ChatCompletion.builder().model(model)
        		.messages(allMessage).build();
        ChatCompletionResponse chatCompletionResponse = openAiClient.chatCompletion(chatCompletion);
        StringBuffer sb = new StringBuffer();

        chatCompletionResponse.getChoices().forEach(e -> {
            sb.append(e.getMessage().getContent());
        });
        
        //缓存上下文
        messageHepler.cacheContext(sb.toString(), prompt, openId,model);
        
        return sb.toString();
    }
    

	@Override
	public void chatCompletions(String prompt, String openId,Session session, String model) {
		List<Message> allMessage = messageHepler.getCacheContextByOpenId(openId,model,null);
        
        //最新GPT-3.5-Turbo模型
        Message message = Message.builder().role(Message.Role.USER).content(prompt).build();
        allMessage.add(message);
        log.info("以stream发送问题：{}，openId:{}",JSON.toJSONString(message),openId);
        log.info("附带的历史记录：{}",JSON.toJSONString(allMessage));
        
        EventSourceListener eventSourceListener =  session !=null ? new OnCompletionEvebntListener(session) : new ConsoleEventSourceListener();
        ChatCompletion chatCompletion = ChatCompletion.builder().model(StringUtils.hasText(model)?model:this.model)
        		.messages(allMessage).build();
        streamClient.streamChatCompletion(chatCompletion, eventSourceListener);
	}

	@Override
	public void chatCompletions(WebRequestMessage request, String openId,String model,Session session, String chatPerson) {
			//此方法是用于前端附带历史消息方法，所以该方法只会获取到设置人设时配置的历史消息，而不是真实的历史消息缓存
			List<Message> allMessage = messageHepler.getCacheContextByOpenId(openId,model,request.getSessionId());
			if(!CollectionUtils.isEmpty(request.getMessages())) {
				request.getMessages().forEach(msg->{
					Message m = Message.builder().role(msg.getRole()).content(msg.getContent()).build();
	        		allMessage.add(m);
				});
			}
			log.info("send question to chatGPT：{}，openId:{}",JSON.toJSONString(request.getPrompt()),openId);
	        log.info("history ：{}",JSON.toJSONString(allMessage));
	        Message message = Message.builder().role(Message.Role.USER).content(request.getPrompt()).build();
	        allMessage.add(message);
	        
	        EventSourceListener eventSourceListener =  session !=null ? new OnCompletionEvebntListener(session) : new ConsoleEventSourceListener();
	        ChatCompletion chatCompletion = ChatCompletion.builder().model(StringUtils.hasText(model)?model:this.model)
	        		.messages(allMessage).build();
	        streamClient.streamChatCompletion(chatCompletion, eventSourceListener);
		
	}

	@Override
	public List<Item> generateImage(String prompt) {
		 ImageResponse imageResponse = openAiClient.genImages(Image.builder().prompt(prompt).size("512x512").n(1).build());
		return imageResponse.getData();
	}




	@Override
	public List<File> getModelFileList() {
		return openAiClient.files();
	}


	@Override
	public List<com.unfbx.chatgpt.entity.models.Model> getModelList() {
		return openAiClient.models();
	}


	@Override
	public FineTuneJobResponse createFineTuning(String fileId) {
		FineTuneJob fineTune = FineTuneJob.builder().trainingFile(fileId).model(this.model).build();
		return openAiClient.fineTuneJob(fineTune);
	}


	@Override
	public FineTuneJobResponse cancelFineTuning(String fineTuneId) {
		return openAiClient.cancelFineTuneJob(fineTuneId);
	}


	@Override
	public UploadFileResponse uploadFile(java.io.File file) {
		return openAiClient.uploadFile(file);
	}


	@Override
	public DeleteResponse deleteFile(String fileId) {
		return openAiClient.deleteFile(fileId);
	}


	@Override
	public FineTuneDeleteResponse deleteFineTuning(String fineTuneId) {
		return openAiClient.deleteFineTuneModel(fineTuneId);
	}


	@Override
	public FineTuneJobListResponse<FineTuneJobResponse> fineTunings() {
		return openAiClient.fineTuneJobs(null,99999);
	}


	@Override
	public String getFileContent(String fileId) throws IOException {
		Single<ResponseBody> fileContent = openAiClient.getOpenAiApi().retrieveFileContent(fileId);
		return fileContent.blockingGet().string();
	}


	@Override
	public FineTuneDeleteResponse deleteFineTuneModel(String model) {
		return openAiClient.deleteFineTuneModel(model);
	}


	@Override
	public void completions(String question, String model) {
		log.info("streaming... prompt：{}",question);
        
        EventSourceListener eventSourceListener = new ConsoleEventSourceListener();
        Completion chatCompletion = Completion.builder().model(StringUtils.hasText(model)?model:this.model)
        		.prompt(question).maxTokens(null).build();
        streamClient.streamCompletions(chatCompletion, eventSourceListener);
	}


	@Override
	public void chatCompletions(WebRequestMessage request, String openId,SseEmitter sseEmitter,Long userId) {
		//put mask to chat history
		if(StringUtils.hasText(request.getMask())) {
			messageHepler.setMask(request.getMask(), openId);
		}
		String model = StringUtils.hasText(request.getModel())?request.getModel():this.model;
		List<Message> allMessage = request.getEnableContext() ? messageHepler.getCacheContextByOpenId(openId,model,request.getSessionId()): new ArrayList<>();
        Message message = Message.builder().role(Message.Role.USER).content(request.getPrompt()).build();
        allMessage.add(message);
        log.info("以stream发送问题：{}，openId:{}",JSON.toJSONString(message),openId);
        log.info("附带的历史记录：{}",JSON.toJSONString(allMessage));
        
        //save chat history
        EventSourceListener eventSourceListener = new OpenAISSEEventSourceListener(sseEmitter,messages->{
        	if(request.getEnableContext()) {
        		//cache 
        		messageHepler.cacheContext(messages.toString(), request.getPrompt(), openId,model);
        	}
        	if(!StringUtils.hasText(request.getModel())) {
        		request.setModel(model);
        	}
        	saveChatHistory(request, messages, userId!=null?userId:0l, allMessage);
        });
        ChatCompletion chatCompletion = ChatCompletion.builder().model(model)
        		.messages(allMessage).build();
        streamClient.streamChatCompletion(chatCompletion, eventSourceListener);
	}
	
	private void saveChatHistory(WebRequestMessage request,StringBuffer message,Long userId,List<Message> allMessage) {
		allMessage.add(Message.builder().role(Message.Role.SYSTEM).content(message.toString()).build());
		//TODO 尚不支持gpt-4o计算方式
		int tokenCount = TikTokensUtil.tokens(request.getModel().toLowerCase().startsWith("gpt-4o")?"gpt-4":request.getModel(),allMessage);
		chatHistoryService.save(new ChatHistory(request.getPrompt(), message.toString(), request.getMask(), userId,Float.valueOf(tokenCount),request.getSessionId(), new Date()));
	}

	@Override
	public void updateApiKeys(String apiKey) {
		streamClient = null;
		openAiClient = null;
		List<String> apiKeys;
		if(StringUtils.hasText(apiKey)&&apiKey.indexOf(",")!=-1) {
    		apiKeys = Arrays.asList(apiKey.split(","));
    	}else {
    		apiKeys = new ArrayList<>();
    		apiKeys.add(apiKey);
    	}
		streamClient = OpenAiStreamClient.builder()
				.okHttpClient(httpClient)
		       .apiKey(apiKeys)
		       .build();
		openAiClient = OpenAiClient.builder()
				        .apiKey(apiKeys)
				        .okHttpClient(httpClient)
				        .build();
		log.info("chatGPT apikeys updated");
	}
	
}
