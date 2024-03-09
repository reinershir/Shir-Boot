package io.github.reinershir.ai.entity;

import java.util.List;

import com.unfbx.chatgpt.entity.chat.Message;

import lombok.Data;

@Data
public class WebRequestMessage {

	private String prompt;
	
	private List<Message> messages;
	
	private String model;
}