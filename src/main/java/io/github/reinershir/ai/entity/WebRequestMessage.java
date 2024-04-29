package io.github.reinershir.ai.entity;

import java.util.List;

import com.unfbx.chatgpt.entity.chat.Message;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "ai chat request body")
public class WebRequestMessage {

	private String prompt;
	
	private List<Message> messages;
	
	private String model;
	
	private String mask;
	
	@Schema(description = "chat session id")
	private String sessionId;
	
	@Schema(description = "enable chat history")
	private Boolean enableContext=true;
}