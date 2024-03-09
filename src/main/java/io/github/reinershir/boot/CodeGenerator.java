package io.github.reinershir.boot;


import java.util.Arrays;

import io.github.reinershir.boot.core.easygenerator.generator.EasyAutoModule;
import io.github.reinershir.boot.core.easygenerator.generator.MicroSMSCodeGenerator;
import io.github.reinershir.boot.core.easygenerator.model.DatabaseInfo;
import io.github.reinershir.boot.core.easygenerator.model.FieldInfo;
import io.github.reinershir.boot.core.easygenerator.model.GenerateInfo;

/**
 * Code generate examp
 * @date  
 * @author ReinerShir
 *
 */
public class CodeGenerator {

	public static void main(String[] args) throws Exception{
		FieldInfo f = new FieldInfo();
		f.setName("requestUri");
		f.setOperation("like");
		GenerateInfo g = new GenerateInfo("dictionary", "Dictionary","dictionary");
		g.setFieldInfos(Arrays.asList(f));
		new MicroSMSCodeGenerator(new DatabaseInfo("jdbc:mysql://localhost:3306/shir-boot?allowMultiQueries=true&serverTimezone=GMT%2B8&useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&useSSL=false&allowPublicKeyRetrieval=true",
				"root","123456","com.mysql.cj.jdbc.Driver")).generateModel(null,"io.github.reinershir.boot.model", "io.github.reinershir.boot",
						new EasyAutoModule[]{EasyAutoModule.MODEL,EasyAutoModule.MYBATIS_MAPPER_INTERFACE,EasyAutoModule.SERVICE_INTERFACE,
								EasyAutoModule.SERVICE_IMPLEMENTS,EasyAutoModule.CONTROLLER_CLASS,EasyAutoModule.PAGE},
						g);
				
	}
	
}  


