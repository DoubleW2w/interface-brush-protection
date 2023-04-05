package com.doublew2w.interfaceBrushProtection.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author DoubleW2w
 * @description
 * @created 2023/4/5 14:54
 * @project interface-brush-protection
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AccessLimit {

    /**
     * 秒
     * @return 多少秒内
     */
    long second() default 5L;

    /**
     * 最大访问次数
     * @return 最大访问次数
     */
    long maxTime() default 3L;

    /**
     * 禁用时长，单位/秒
     * @return 禁用时长
     */
    long forbiddenTime() default 120L;
}
