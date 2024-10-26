package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * 自定义切面, 实现数据库公共字段自动填充逻辑
 */
@Slf4j
@Component
@Aspect
public class AutoFillAspect {

    // 切入点表达式: mapper包下的所有的接口/类下的所有的方法, 不限制参数
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointCut() {
    }

    //    前置通知: 在目标方法运行前被执行
    // 在通知中进行数据库公共字段的填充
    @Before("autoFillPointCut()")
    public void autoFill(JoinPoint joinPoint) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
//        获取到当前被拦截的方法上的数据库操作类型
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();//方法签名对象
        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class);//获得方法上的注解对象
        OperationType operationType = autoFill.value(); //获得数据库操作类型

//        获取到当前被拦截的方法的参数---实体对象
        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0) {
            return;
        }
//        约定方法的第一个参数必须为实体对象
        Object entity = args[0];
        Class<?> entityClass = entity.getClass();

        LocalDateTime now = LocalDateTime.now();
        // 获取在当前线程局部变量中存储的用户id
        Long currentId = BaseContext.getCurrentId();

//        根据不同的操作类型, 为对应的属性提供反射来赋值
        switch (operationType) {
            case INSERT -> {
                log.info("开始新增操作公共字段自动填充...");
//                通过反射来获取对应方法
                Method setCreateTime = entityClass.getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
                Method setCreateUser = entityClass.getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
                Method setUpdateTime = entityClass.getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = entityClass.getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
//              通过暴力反射来强制访问
                setCreateTime.setAccessible(true);
                setCreateUser.setAccessible(true);
                setUpdateTime.setAccessible(true);
                setUpdateUser.setAccessible(true);
//                通过反射来为实体的属性赋值
                setCreateTime.invoke(entity, now);
                setCreateUser.invoke(entity, currentId);
                setUpdateTime.invoke(entity, now);
                setUpdateUser.invoke(entity, currentId);
            }
            case UPDATE -> {
                log.info("开始更新操作公共字段自动填充...");
//                通过反射来获取对应方法
                Method setUpdateTime = entityClass.getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = entityClass.getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
//              通过暴力反射来强制访问
                setUpdateTime.setAccessible(true);
                setUpdateUser.setAccessible(true);
//                通过反射来为实体的属性赋值
                setUpdateTime.invoke(entity, now);
                setUpdateUser.invoke(entity, currentId);
            }
        }
    }

}
