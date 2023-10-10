package io.github.reinershir.boot.core.easygenerator.generator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import io.github.reinershir.boot.core.easygenerator.model.DatabaseInfo;
import io.github.reinershir.boot.core.easygenerator.model.EntityInfo;
import io.github.reinershir.boot.core.easygenerator.model.FieldInfo;
import io.github.reinershir.boot.core.easygenerator.model.GenetateInfo;
import io.github.reinershir.boot.core.easygenerator.utils.FieldUtils;

/**
 * 生成实体类
 * 
 * @author ReinerShir
 *
 */
public class MicroSMSCodeGenerator {

	private final String TEMPLATE_DIR = "/templates/sm";

	private String javaModuleFolder = null;

	//mapper存放前置路径
	private String resourcesFolder = null;
	
	private String baseControllerImport = null;

	String basePath = System.getProperty("user.dir") + "/src/main/java/";

	public void generateModel(DatabaseInfo databaseInfo, String modelPackage, String commonPath,
			EasyAutoModule[] modules,GenetateInfo... genetateInfo) {
		baseControllerImport = commonPath+".controller";
		javaModuleFolder = basePath + commonPath.replaceAll("\\.", "/");// replace
		resourcesFolder = basePath + commonPath.replaceAll("\\.", "/") + "/mapper";

		for (GenetateInfo g : genetateInfo) {
			String tableName = g.getTableName();
			String modelName = g.getModelName();
			Map<String, Object> map = new HashMap<>();
			// 新加代码 =================
			// 连接数据库获取字段表名=================================================
			if (databaseInfo != null) {
				String primaryKeyColumnName = null;
				// 声明Connection对象
				Connection con = null;
				// 驱动程序名
				String driver = databaseInfo.getDriver();
				// URL指向要访问的数据库名mydata
				String url = databaseInfo.getUrl();
				// MySQL配置时的用户名
				String user = databaseInfo.getName();
				// MySQL配置时的密码
				String password = databaseInfo.getPassword();
				List<FieldInfo> fieldInfoList = new ArrayList<>();
				// 遍历查询结果集
				try {
					// 加载驱动程序
					Class.forName(driver);
					// 1.getConnection()方法，连接MySQL数据库！！
					con = DriverManager.getConnection(url, user, password);
					if (!con.isClosed())
						System.out.println("Succeeded connecting to the Database!");
					// 2.创建statement类对象，用来执行SQL语句！！
					Statement statement = con.createStatement();
					DatabaseMetaData metaData = con.getMetaData();
					ResultSet tableRet = metaData.getTables(null, "%", tableName, new String[] { "TABLE" });
					ResultSet colRet = metaData.getColumns(null, "%", tableName, "%");
					String catalog = con.getCatalog();
					ResultSet primaryKeyResultSet = metaData.getPrimaryKeys(catalog, null, tableName);
					while (primaryKeyResultSet.next()) {
						primaryKeyColumnName = primaryKeyResultSet.getString("COLUMN_NAME");
					}
					while (colRet.next()) {
						String columnName = colRet.getString("COLUMN_NAME");
						Integer columnLength = colRet.getInt("COLUMN_SIZE");
						String columnType = colRet.getString("TYPE_NAME");
						String comment = null;
						String defaultValue = null;
						boolean isNull = true;
						//获取字段注释
						ResultSet rs = statement.executeQuery("show full columns from " + tableName);
						while (rs.next()) {
							if (columnName.equals(rs.getString("Field"))) {
								comment = rs.getString("Comment");
								String canBeNull = rs.getString("Null");
								isNull = !"NO".equalsIgnoreCase(canBeNull);
								defaultValue = rs.getString("Default");
								break;
							}
						}
						//数据库字段类型
						int dt = colRet.getInt("DATA_TYPE");
						String javaType = "String";
						switch (dt) {
						case Types.BIGINT:
							javaType = "Long";
							break;
						case Types.INTEGER:
						case Types.SMALLINT:
						case Types.TINYINT:
							javaType = "Integer";
							break;
						case Types.FLOAT:
						case Types.REAL:
							javaType = "Float";
							break;
						case Types.DOUBLE:
							javaType = "Double";
							break;
						case Types.DATE:
						case Types.TIMESTAMP:
						case Types.TIMESTAMP_WITH_TIMEZONE:
							javaType = "Date";
							break;
						default:
							break;
						}
						FieldInfo f = new FieldInfo(FieldUtils.camelName(columnName), javaType,columnLength);
						f.setComment(comment);
						f.setColumnName(columnName);
						f.setIsNull(isNull);
						f.setDefaultValue(defaultValue);
						if (columnName.equals(primaryKeyColumnName)) {
							System.out.println("primary Key is "+primaryKeyColumnName);
							f.setPrimaryKey(true);
						}
						fieldInfoList.add(f);
					}
					//将从数据库中查询到的字段信息放入，用于生成Mapper
					g.setFieldInfos(fieldInfoList);
					colRet.close();
					primaryKeyResultSet.close();
					con.close();
					
				} catch (ClassNotFoundException e) {
					// 数据库驱动类异常处理
					System.out.println("Sorry,can`t find the Driver!");
					e.printStackTrace();
				} catch (SQLException e) {
					// 数据库连接失败异常处理
					e.printStackTrace();
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				} finally {
					try {
						if (con != null)
							con.close();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}
			
//			try {
//				classs.add(Class.forName(modelPackage+".model."+modelName+".java"));
//			} catch (ClassNotFoundException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		}
		System.out.println("Start generate...");
		try {
			generatorAll(commonPath,modelPackage,modules,genetateInfo);
			System.out.println("Code generation successful! Please refresh your project.");
		} catch (TemplateException | IOException e) {
			e.printStackTrace();
		}
		
		// return classs;

		// =======================================分割线============================
	}
	
	/**
	 * 生成所有
	 * 
	 * @param allClass
	 * @param modules
	 * @throws TemplateException
	 * @throws IOException
	 */
	private void generatorAll(String commonPath, String modulePackage, EasyAutoModule[] modules,GenetateInfo... genetateInfo) throws TemplateException, IOException {
		// TODO
		String mds = "#";
		if (modules == null) {
			mds = "#controller#criteria#dao#mapper#page#service#serviceImpl#";
		} else {
			for (int i = 0; i < modules.length; i++) {
				mds += modules[i].value + "#";
			}
		}
		generatorFile(commonPath,modulePackage, mds,genetateInfo);
	}

	/**
	 * 生成文件
	 * 
	 * @param allClass
	 * @param modules
	 * @throws TemplateException
	 * @throws IOException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void generatorFile(String commonPath, String modulePackage,
			String modules,GenetateInfo... genetateInfo) throws TemplateException, IOException {
		for (GenetateInfo g : genetateInfo) {
			/* Create a data-model */
			Map root = new HashMap();
			// mapper中resultType 包路径
			String pkgName = commonPath;
			// 模块
			String module = modulePackage;
			if (module.contains(".")) {
				module = module.substring(module.lastIndexOf(".") + 1);
			}
			String modelName = g.getModelName();
			String table = g.getTableName();
			String className = g.getModelName();

			// ========== EasyId
			// 主键ID
			String oid = null;
			//主键java属性名
			String oidColumn = null;
			String idClassName = null;
			// ========== EasyId

			// ========== Criteria
			// 条件属性数据列表
			Set<String> propertys = new LinkedHashSet<String>();
			// 需要额外导入的包列表
			Set<String> imports = new LinkedHashSet<String>();
			// ========== Criteria
			// 表格列显示信息
			Set<String> autos = new LinkedHashSet<String>();
			//查找主键
			for (FieldInfo fieldInfo : g.getFieldInfos()) {
				String fieldName = fieldInfo.getName();
				if (!fieldName.equals("serialVersionUID")) {
					if (fieldInfo.isPrimaryKey()) {
						oid = fieldName;
						oidColumn = fieldInfo.getColumnName();
						idClassName=fieldInfo.getJavaType();
					}
					
						
					//如果为空使用属性名生成 modiffy by xh
					String label = fieldName;
					String field = fieldName;
					String inputField = field;
					String mybatisColumn = fieldInfo.getColumnName();
					Integer myBatisLegnth =  fieldInfo.getColumnLength();
					// field#inputField#label#required#show#inputShow#editable#updateAble
					/*
					 * field, 0 inputField, 1 label, 2 required, 3 show, 4
					 * inputShow, 5 updateAble, 6 inputClass, 7
					 * mybatisColumn, 8 java type 9
					 */
					// TODO
					autos.add(field + "#" + inputField + "#" + label + "#" + "false" + "#" + "true"
							+ "#" + "true" + "#" + "true" + "#" + "inputClass" + "#"
							+ mybatisColumn + "#" + myBatisLegnth + "#" + fieldInfo.getJavaType());


					String simpleTypeName = fieldInfo.getJavaType();

					String importPack = simpleTypeName;
					if (simpleTypeName.equals("Date")) {
						importPack = "java.util.";
					}

					// type#field#queryConditionName#like#label
					// propertys.add("Integer#empno#empno#=");
					// propertys.add("String#ename#ename#like");
					// propertys.add("Double#sal#sal#=");
					// propertys.add("Integer#deptno#dept.deptno#=");
					propertys.add(importPack + "#" + field + "#" + "queryConditionName" + "#" + "like" + "#" + label);
					
				}
			}
			//freemarker用变量
			root.put("table",g.getTableName());
			root.put("modelDescription",g.getModelDescription());
			root.put("fieldInfos",g.getFieldInfos());
			root.put("idClassName", idClassName);
			root.put("commonPath", commonPath);
			root.put("pkgName", pkgName);
			root.put("Imports", imports);
			root.put("Propertys", propertys);
			root.put("Oid", oid);
			root.put("oidColumn", oidColumn);
			
			root.put("Autos", autos);
			root.put("Propertys", propertys);
			root.put("Module", module);
			root.put("Imports", imports);
			root.put("ClassName", g.getModelName());
			root.put("baseControllerImport", baseControllerImport);
			root.put("modelPackage", modulePackage);
			
			//root.put("idType", idClass.getSimpleName()); //ID类型

			// 配置
			Configuration cfg = new Configuration(Configuration.VERSION_2_3_32);
			cfg.setSharedVariable("messages", new InternationalizationDirectiveModel());
			cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
			cfg.setDefaultEncoding("UTF-8");
			// cfg.setDirectoryForTemplateLoading(new File(folder));
			cfg.setClassForTemplateLoading(MicroSMSCodeGenerator.class, TEMPLATE_DIR);

			Template template = null;
			String fileName = null;
			String criteriaFolder = null;
			File dir = null;
			FileWriter out = null;

			
			if(modules.contains("model")) {
				EntityInfo entityInfo = new EntityInfo(commonPath,modelName, g.getFieldInfos());
				Map<String,Object> map = new HashMap<>();
				map.put("entityInfo", entityInfo);
				map.put("commonPath", commonPath);
				map.put("modelPackage", modulePackage);
				map.put("ClassName",modelName);
				map.put("tableName",table);
				System.out.println("Generate Model...");
				// 生成model
				// cfg.setDirectoryForTemplateLoading(new File(folder));
				template = cfg.getTemplate("model.tpl");
				fileName = modelName + ".java";
				criteriaFolder = javaModuleFolder + "/model/";
				System.out.println("model path："+criteriaFolder);
				dir = new File(criteriaFolder);
				if (!dir.exists()) {
					dir.mkdirs();
				}
				out = new FileWriter(criteriaFolder + fileName);
				template.process(map, out);
				template.setAutoFlush(true);
			}

			// XXXDAO
			if (modules.contains("dao")) {
				template = cfg.getTemplate("dao.tpl");
				fileName = className + "Mapper.java";
				String daoFolder = basePath + commonPath.replaceAll("\\.", "/") + "/mapper/";
				dir = new File(daoFolder);
				if (!dir.exists()) {
					dir.mkdirs();
				}
				out = new FileWriter(daoFolder + fileName);
				template.process(root, out);
				out.close();
			}

			// XXXDAO.xml Mapper
			if (modules.contains("mapper")) {
				template = cfg.getTemplate("mapper.tpl");
				fileName = className + "Mapper.xml";
				criteriaFolder = resourcesFolder + "/";// + "/" + module + "/";
				System.out.println("mapper path :" + criteriaFolder);
				dir = new File(criteriaFolder);
				if (!dir.exists()) {
					dir.mkdirs();
				}
				out = new FileWriter(criteriaFolder + fileName);
				template.process(root, out);
				out.close();
			}

			// XXXService
			
			if (modules.contains("service")) { 
				template = cfg.getTemplate("service.tpl");
				fileName = className + "Service.java"; 
				criteriaFolder = javaModuleFolder +
				"/service/"; dir = new File(criteriaFolder); 
				if (!dir.exists()) {
					dir.mkdirs(); 
				}
				out = new FileWriter(criteriaFolder + fileName);
				template.process(root, out); out.close(); 
			}
			 

			// XXXServiceImpl
			if (modules.contains("serviceImpl")) {
				template = cfg.getTemplate("serviceImpl.tpl");
				fileName = className + "ServiceImpl.java";
				criteriaFolder = javaModuleFolder + "/service/impl/";
				dir = new File(criteriaFolder);
				if (!dir.exists()) {
					dir.mkdirs();
				}
				out = new FileWriter(criteriaFolder + fileName);
				template.process(root, out);
				out.close();
			}

			// XXXAction
			if (modules.contains("controller")) {
				template = cfg.getTemplate("controller.tpl");
				fileName = className + "Controller.java";
				criteriaFolder = javaModuleFolder + "/controller/";
				dir = new File(criteriaFolder);
				if (!dir.exists()) {
					dir.mkdirs();
				}
				out = new FileWriter(criteriaFolder + fileName);
				template.process(root, out);
				out.close();
			}
			
			if (modules.contains("dataController")) {
				template = cfg.getTemplate("dataController.tpl");
				fileName = className + "Controller.java";
				criteriaFolder = javaModuleFolder + "/controller/";
				dir = new File(criteriaFolder);
				if (!dir.exists()) {
					dir.mkdirs();
				}
				out = new FileWriter(criteriaFolder + fileName);
				template.process(root, out);
				out.close();
			}
		}
			
	}

}
