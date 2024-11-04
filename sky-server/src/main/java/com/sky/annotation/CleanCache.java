package com.sky.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义注解, 用于标识某个方法需要清理redis中的缓存
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CleanCache {
    // 清理redis中的key时要匹配的模式
    String pattern();
}
