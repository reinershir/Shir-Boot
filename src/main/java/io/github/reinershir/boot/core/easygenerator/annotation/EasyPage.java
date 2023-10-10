package io.github.reinershir.boot.core.easygenerator.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(value={ElementType.TYPE})
public @interface EasyPage{
	/**
	 * 页面类型，默认为  PageType.NONE
	 * @return 
	 */
	PageType value() default PageType.NONE;
}
