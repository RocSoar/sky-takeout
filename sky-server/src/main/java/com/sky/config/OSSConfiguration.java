package com.sky.config;

import com.sky.properties.AliOSSProperties;
import com.sky.utils.AliOSSUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 配置类, 用于创建AliOSSUtil对象
 */
@Slf4j
@Configuration
public class OSSConfiguration {

    //将方法的返回值对象交给IOC容器管理,成为IOC容器的bean对象, 从而可以直接被依赖注入
    //通过@Bean注解的name/value属性可以指定bean的名字, 如果未指定, 默认是方法名
    @Bean
    @ConditionalOnMissingBean //当不存在该类型的bean,才会将该bean加入IOC容器中,默认判断当前返回值的类型,也可指定类型(value属性)或名称(name属性)
    public AliOSSUtil aliOSSUtil(AliOSSProperties aliOSSProperties) { //spring可直接从IOC容器中注入所需的bean对象到方法形参
        log.info("创建阿里云文件上传工具类对象...");
        return new AliOSSUtil(aliOSSProperties.getEndpoint(), aliOSSProperties.getBucketName());
    }
}
