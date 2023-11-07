package io.github.reinershir.boot.controller;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.reinershir.auth.annotation.OptionType;
import io.github.reinershir.auth.annotation.Permission;
import io.github.reinershir.auth.annotation.PermissionMapping;
import io.github.reinershir.auth.core.model.Menu;
import io.github.reinershir.boot.common.Result;
import io.github.reinershir.boot.core.easygenerator.generator.EasyAutoModule;
import io.github.reinershir.boot.core.easygenerator.generator.MicroSMSCodeGenerator;
import io.github.reinershir.boot.core.easygenerator.model.FieldInfo;
import io.github.reinershir.boot.core.easygenerator.model.GenerateInfo;
import io.github.reinershir.boot.core.international.IMessager;
import io.github.reinershir.boot.dto.req.CodeGenerateDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@RequestMapping("development")
@RestController
@Tag(description = "Development management",name = "Development management")
@PermissionMapping(value="DEVELOPMENT")
public class DevelopmentController {
	
	@Autowired
	MicroSMSCodeGenerator codeGenerator;
	
	@Permission(name = "Get Table columns",value = OptionType.LIST)
	@Operation(summary = "Get Table columns",description = "Get Table columns")
	@Parameter(name = "tableName",required = false,description = "Parent menu id")
	@GetMapping("/codeGenerate/{tableName}/columns")
	public Result<List<FieldInfo>> getColumns(@PathVariable(value="tableName",required = true) String tableName){
		try {
			return Result.ok(codeGenerator.getTableFields(tableName));
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
			return Result.failed(IMessager.getMessageByCode("message.error"));
		}                                                   
	}

	@Permission(name = "Code generate",value = OptionType.LIST)
	@Operation(summary = "Code generate",description = "Generate front-end and back-end codes")
	@PostMapping("/codeGenerate/codes")
	public Result<String> generate(@RequestBody @Validated CodeGenerateDTO dto){
		GenerateInfo generateInfo = new GenerateInfo(dto.getTableName(),dto.getModelName(),dto.getModelDescription());
		generateInfo.setFieldInfos(dto.getFieldInfos());
		try {
			codeGenerator.generateModel(dto.getPackageName(), dto.getPackageName()+".model", EasyAutoModule.values(),generateInfo);
			
			//TODO 将生成好的代码打包成ZIP并下载
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return Result.failed(IMessager.getMessageByCode("message.error"));
		}
		return Result.ok();                                                   
	}
}
