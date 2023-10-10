package io.github.reinershir.boot.core.easygenerator.annotation;

public enum PageType {
	/**
	 * 无需直接编辑，默认方式
	 * 自动添加以下类注解
	 * @EasyAddDialog
	 * @EasyEditDialog
	 */
	NONE,
	/**
	 * 行编辑模式(暂不可用)
	 */
	ROW_EDIT,
	/**
	 * 列编辑模式(暂不可用)
	 */
	CELL_EDIT,
	/**
	 * 表单编辑模式，单行数据(暂不可用)
	 */
	FORM_EDIT;
}
