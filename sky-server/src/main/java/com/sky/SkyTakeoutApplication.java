package com.sky;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
//@EnableTransactionManagement //开启注解方式的事务管理, (新版本不用加这个注解也能使用事务管理)
@Slf4j
public class SkyTakeoutApplication {
    public static void main(String[] args) {
        SpringApplication.run(SkyTakeoutApplication.class, args);
        log.info("server started");
    }
}
