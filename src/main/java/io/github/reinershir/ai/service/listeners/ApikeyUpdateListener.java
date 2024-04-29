package io.github.reinershir.ai.service.listeners;

import org.springframework.util.StringUtils;

import io.github.reinershir.ai.service.ChatGPTService;
import io.github.reinershir.boot.model.Dictionary;
import io.github.reinershir.boot.service.listener.DictionaryListener;

public class ApikeyUpdateListener implements DictionaryListener{
	
	private ChatGPTService chatGPTService;

	public ApikeyUpdateListener(ChatGPTService chatGPTService) {
		this.chatGPTService = chatGPTService;
	}
	@Override
	public void callback(Dictionary dic) {
		if(dic!=null&&StringUtils.hasText(dic.getValue())) {
			chatGPTService.updateApiKeys(dic.getValue());
		}
	}

}
