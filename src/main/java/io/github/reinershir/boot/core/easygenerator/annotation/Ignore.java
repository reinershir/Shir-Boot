package io.github.reinershir.boot.core.easygenerator.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Add Dialog extension  
 * 
 * Set for Class
 * 
 * @author Ray
 * @author inthinkcolor@gmail.com
 * @author easyproject.cn
 * @since 1.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value={ElementType.FIELD})
public @interface Ignore{
}
