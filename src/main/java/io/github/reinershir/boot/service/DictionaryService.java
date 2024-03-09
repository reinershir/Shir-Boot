package io.github.reinershir.boot.service;
import com.baomidou.mybatisplus.extension.service.IService;

import io.github.reinershir.boot.contract.DictionaryCodes;
import io.github.reinershir.boot.model.Dictionary;

/**
 * Dictionary Service, generated by Shir Boot EasyGenerator
 * @author Shir-Boot
 * @version 1.0
 *
 */
public interface DictionaryService extends IService<Dictionary>{
	
	public String getValueByCode(DictionaryCodes code);
}