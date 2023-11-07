//package io.github.reinershir.boot;
//
//
//import io.github.reinershir.boot.core.easygenerator.generator.EasyAutoModule;
//import io.github.reinershir.boot.core.easygenerator.generator.MicroSMSCodeGenerator;
//import io.github.reinershir.boot.core.easygenerator.model.DatabaseInfo;
//import io.github.reinershir.boot.core.easygenerator.model.GenerateInfo;
//
///**
// * Code generate examp
// * @date  
// * @author ReinerShir
// *
// */
//public class CodeGenerator {
//
//	public static void main(String[] args){
//		new MicroSMSCodeGenerator().generateModel(new DatabaseInfo("jdbc:mysql://43.156.78.227:3306/thesis_db?allowMultiQueries=true&serverTimezone=GMT%2B8&useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&useSSL=false",
//				"root","paper123456","com.mysql.cj.jdbc.Driver"), 
//				"io.github.reinershir.boot.model", "io.github.reinershir.boot",
//				new EasyAutoModule[]{EasyAutoModule.MODEL,EasyAutoModule.MYBATIS_MAPPER_INTERFACE,EasyAutoModule.SERVICE_INTERFACE,
//						EasyAutoModule.SERVICE_IMPLEMENTS,EasyAutoModule.CONTROLLER_CLASS},
//				new GenerateInfo("SUGGESTIONS", "Suggestions","投诉建议"));
//	}
//	
//}  
//
//
