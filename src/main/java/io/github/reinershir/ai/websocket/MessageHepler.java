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
    
	public void cacheContext(String replyContent,String prompt,String openId,String model) {
    	List<Message> message = historyMsg.get(openId);
    	int tokens = CollectionUtils.isEmpty(message)?0:TikTokensUtil.tokens(model, message);
    	//记录历史会话
        if(StringUtils.hasText(replyContent)) {
        	//Message = msgMap.getHistoryMsg().get(openId);
        	if(message==null) {
        		message = new ArrayList<>();
//        		Message personality = personalityMap.get(openId);
//        		if(personality!=null) {
//        			message.add(personality);
//        		}
        	}else if(tokens>=3496) {
        		// 如果快要超过则清除一半的对话记录
        		message = message.subList(message.size()/2, message.size());
//        		Message personality = personalityMap.get(openId);
//        		if(personality!=null) {
//        			message.add(personality);
//        		}
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
        	for (int i = 0; i <message.size(); i++) {
        		Message msg = message.get(i);
        		int tokens = TikTokensUtil.tokens(model, allMessage);
        		int nextTokens = TikTokensUtil.tokens(model, msg.getContent());
        		if((tokens+nextTokens)<4096) {
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
