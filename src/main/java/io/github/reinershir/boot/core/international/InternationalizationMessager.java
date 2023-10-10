package io.github.reinershir.boot.core.international;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import io.github.reinershir.boot.exception.BaseException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InternationalizationMessager {

	private Properties properties;
	private static InternationalizationMessager instance;
	
	public InternationalizationMessager() throws IOException {
		String defaultPath = "i18n/message.properties";
		String path = "i18n/message_"+LocaleContextHolder.getLocale().getLanguage().toLowerCase()+"_"+LocaleContextHolder.getLocale().getCountry()+".properties";
		
		Resource resource = new ClassPathResource(path);
	    try {
			this.properties = PropertiesLoaderUtils.loadProperties(resource);
		} catch (FileNotFoundException e) {
			this.properties = PropertiesLoaderUtils.loadProperties(new ClassPathResource(defaultPath));
			log.warn("Unable to retrieve internationalization configuration path :{}, will attempt to use default configuration path.",path);
	    	if(properties==null) {
	    		throw new BaseException("Can not find internationalization properties");
	    	}
		}
	}
	
	public static synchronized InternationalizationMessager getInstance() {
			if(InternationalizationMessager.instance==null) {
				try {
					InternationalizationMessager.instance = new InternationalizationMessager();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		return InternationalizationMessager.instance;
		
	}
	
	public String getMessage(String code) {
		return this.properties.getProperty(code);
	}
}
