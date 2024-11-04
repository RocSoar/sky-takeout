package com.sky.aspect;

import com.sky.annotation.CleanCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;

@Slf4j
@Aspect
@Component
@SuppressWarnings("all")
@RequiredArgsConstructor
public class CleanCacheAspect {
    private final RedisTemplate redisTemplate;

    @Pointcut("execution(* com.sky.controller..*(..)) && @annotation(com.sky.annotation.CleanCache)")
    public void cleanCachePointCut() {
    }

    @AfterReturning("cleanCachePointCut()")
    public void cleanCache(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();//方法签名对象
        CleanCache cleanCache = signature.getMethod().getAnnotation(CleanCache.class);//获得方法上的注解对象
        String pattern = cleanCache.pattern(); //获得要匹配的模式
        log.info("正在删除redis中的缓存...匹配模式: {}", pattern);

//        删除所有匹配该模式的key
        Set keys = redisTemplate.keys(pattern);
        redisTemplate.delete(keys);
    }
}
