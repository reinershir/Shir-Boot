package io.github.reinershir.ai.websocket;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.unfbx.chatgpt.entity.chat.Message;
import com.unfbx.chatgpt.utils.TikTokensUtil;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Component
@Data
@Slf4j
public class MessageHepler {

	Map<String,List<Message>> historyMsg = new LinkedHashMap<>();
    private Map<String,Message> personalityMap = new LinkedHashMap<>();
    
    private int getTokenLimitByModel(String model) {
    	if(model.toLowerCase().startsWith("gpt-4")|| model.toLowerCase().equals("gpt-3.5-turbo-16k")) {
    		return 1280000;
    	}
    	return 2048;
    }
    
	public void cacheContext(String replyContent,String prompt,String openId,String model) {
    	List<Message> message = historyMsg.get(openId);
    	int tokens = CollectionUtils.isEmpty(message)?0:TikTokensUtil.tokens(model, message);
    	int nextTokens = TikTokensUtil.tokens(model, prompt);
    	int limit = getTokenLimitByModel(model);
    	//记录历史会话
        if(StringUtils.hasText(replyContent)) {
        	if(message==null) {
        		message = new ArrayList<>();
        	}else if((tokens+nextTokens)>=limit) {
        		log.info("记录到达上限:{},openid:{}",(tokens=nextTokens),openId);
        		if(nextTokens<limit) {
        			Message userMsg = new Message();
                	userMsg.setRole(Message.Role.USER.getName());
                	userMsg.setContent(prompt);
                	message.clear();
                	message.add(userMsg);
                	historyMsg.put(openId, message);
                	return;
        		}
        		//如果都超过了，直接清除
        		message = null;
            	historyMsg.remove(openId);
        		return;
        	}
        	Message userMsg = new Message();
        	userMsg.setRole(Message.Role.USER.getName());
        	userMsg.setContent(prompt);
        	Message systemMsg = new Message();
        	systemMsg.setRole(Message.Role.SYSTEM.getName());
        	systemMsg.setContent(replyContent);
        	message.add(userMsg);
        	message.add(systemMsg);
        	historyMsg.put(openId, message);
        }
    }

   public List<Message> getCacheContextByOpenId(String openId,String model){
    	List<Message> message = historyMsg.get(openId);
        List<Message> allMessage = new ArrayList<>();
        Message personality = personalityMap.get(openId);
 		if(personality!=null) {
 			Message m = Message.builder().role(personality.getRole()).content(personality.getContent()).build();
 			allMessage.add(m);
 		}
 		
        if(!CollectionUtils.isEmpty(message)) {
        	int tokens = TikTokensUtil.tokens(model, message);
        	int limit = getTokenLimitByModel(model);
        	for (int i = message.size()-1; i >=0; i--) {
         		Message msg = message.get(i);
        		int nextTokens = TikTokensUtil.tokens(model, msg.getContent());
        		tokens+=nextTokens;
        		if(tokens<limit) {
            		// Message m = Message.builder().role(msg.getRole()).content(msg.getContent()).build();
            		allMessage.add(msg);
        		}else {
        			break;
        		}
			}
        }
        return allMessage;
    }
   
	   
   public void setMask(String mask,String openId) {
       if(StringUtils.hasText(mask)) {
    	   Message userMsg = new Message();
    	   userMsg.setRole(Message.Role.USER.getName());
    	   userMsg.setContent(mask);
    	   log.info("session id:{}, ,mask prompt ：{}",openId,mask);
    	   personalityMap.put(openId, userMsg);
    	   if(personalityMap.size()>50000) {
    		   log.warn("人设缓存到达限制，将自动清理.");
    		   personalityMap.clear();
    	   }
    	   
       }
   }
}
