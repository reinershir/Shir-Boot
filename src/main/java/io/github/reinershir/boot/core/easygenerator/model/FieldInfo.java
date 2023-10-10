package io.github.reinershir.boot.core.easygenerator.model;

public class FieldInfo {

	private String name;
	private String javaType;
	private String comment;
	//对应的数据库字段名称
	private String columnName;
	private boolean isPrimaryKey = false;
	private String defaultValue;
	/**
	 * 是否允许空值
	 */
	private boolean isNull = true;
	
	public FieldInfo(String name,String javaType, Integer columnLength) {
		super();
		this.name = name;
		this.javaType = javaType;
		this.columnLength = columnLength;
	}
	/**
	 * 字段长度
	 */
	private Integer columnLength;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getJavaType() {
		return javaType;
	}
	public void setJavaType(String javaType) {
		this.javaType = javaType;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	public String getColumnName() {
		return columnName;
	}
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}
	public boolean isPrimaryKey() {
		return isPrimaryKey;
	}
	public void setPrimaryKey(boolean isPrimaryKey) {
		this.isPrimaryKey = isPrimaryKey;
	}
	public Integer getColumnLength() {
		return columnLength;
	}
	public void setColumnLength(Integer columnLength) {
		this.columnLength = columnLength;
	}
	public boolean getIsNull() {
		return isNull;
	}
	public void setIsNull(boolean isNull) {
		this.isNull = isNull;
	}
	public String getDefaultValue() {
		return defaultValue;
	}
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	
	
}
