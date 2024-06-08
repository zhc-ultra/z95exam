package com.zj.z95exam;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 项目启动入口
 */
// 标识Spring 容器的启动类
@SpringBootApplication
// Mapper 扫描路径
@MapperScan("com.zj.z95exam.mapper")

@EnableScheduling
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
public class Z95ExamApplication {
    public static void main(String[] args) {
        // 启动Spring 容器
        SpringApplication.run(Z95ExamApplication.class, args);
    }
}