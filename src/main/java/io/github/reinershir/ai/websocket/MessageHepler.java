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

@Component
@Data
public class MessageHepler {

	Map<String,List<Message>> historyMsg = new LinkedHashMap<>();
    private Map<String,Message> personalityMap = new LinkedHashMap<>();
    
	public void cacheContext(String replyContent,String prompt,String openId) {
    	List<Message> message = historyMsg.get(openId);
    	//记录历史会话
        if(StringUtils.hasText(replyContent)) {
        	//Message = msgMap.getHistoryMsg().get(openId);
        	if(message==null) {
        		message = new ArrayList<>();
        		Message personality = personalityMap.get(openId);
        		if(personality!=null) {
        			message.add(personality);
        		}
        	}else if(message.size()>=30) {
        		message = message.subList(15, message.size());
        		Message personality = personalityMap.get(openId);
        		if(personality!=null) {
        			message.add(personality);
        		}
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

        	for (int i = message.size()-1; i >=0; i--) {
        		Message msg = message.get(i);
        		int tokens = TikTokensUtil.tokens(model, allMessage);
        		int nextTokens = TikTokensUtil.tokens(model, msg.getContent());
        		if((tokens+nextTokens)<4096) {
            		Message m = Message.builder().role(msg.getRole()).content(msg.getContent()).build();
            		allMessage.add(m);
        		}else {
        			break;
        		}
			}
        }
        return allMessage;
    }
	   
//	   public void setPersonality(String personalityId,String openId) {
//	       Personality p = personalityService.getById(personalityId);
//	       if(p!=null) {
//	    	   Message userMsg = new Message();
//	    	   userMsg.setRole(Message.Role.USER);
//	    	   userMsg.setMessage(p.getContent());
//	    	   log.info("session id:{},设置人设名称：{}",openId,p.getName());
//	    	   personalityMap.put(openId, userMsg);
//	    	   if(personalityMap.size()>50000) {
//	    		   //TODO 无使用自动清除，按未使用时间清除
//	    		   Log.warn("人设缓存到达限制，将自动清理.");
//	    		   personalityMap.clear();
//	    	   }
//	    	   
//	       }
//	   }
}
