package io.github.reinershir.boot.core.easygenerator.generator;

public class QueryGenerator implements freemarker.template.TemplateModel{

	public String generateAnnotation(String opreation) {
		StringBuffer anno = new StringBuffer("@QueryRule(QueryRuleEnum.");
		switch(opreation) {
		case ">":
			anno.append("GT");
			break;
		case "<":
			anno.append("LT");
			break;
		case "=":
			anno.append("EQ");
			break;
		case ">=":
			anno.append("GE");
			break;
		case "IN":
			anno.append("IN");
			break;
		case "LEFT_LIKE":
			anno.append("LEFT_LIKE");
			break;
		case "RIGHT_LIKE":
			anno.append("RIGHT_LIKE");
			break;
		case "LIKE":
			anno.append("LIKE");
			break;
		case "IS_NULL":
			anno.append("EMPTY");
			break;
		case "NOT_EMPTY":
			anno.append("NOT_EMPTY");
			break;
		case "!=":
			anno.append("NE");
			break;
		case "NOT_IN":
			anno.append("NOT_IN");
			break;
		case "HIDDEN":
			anno.append("HIDDEN");
			break;
		}
		anno.append(")");
		return anno.toString();
	}
}
