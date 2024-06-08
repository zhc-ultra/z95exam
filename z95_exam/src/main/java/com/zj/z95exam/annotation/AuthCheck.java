package com.zj.z95exam.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 权限校验
 *
 * @Target(ElementType.METHOD) -> 注解的作用目标，表示该注解只能用于方法上
 * @Retention(RetentionPolicy.RUNTIME) -> 注解的保留策略，表示该注解在运行时仍然可用
 **/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthCheck {

    /**
     * 必须有某个角色
     *
     * @return
     */
    String mustRole() default "";
}

