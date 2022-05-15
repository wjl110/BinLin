package com.show;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import tk.mybatis.spring.annotation.MapperScan;
@SpringBootApplication
@MapperScan(basePackages="com.show.mapper")
@ComponentScan(basePackages= {"com.show","org.n3r.idworker"})
@EnableAsync
@Configuration
/**
 * 
 * @author 916202420@qq.com
 * 创建时间 2022年5月15日 中午12:00:00
 */
public class Application {
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
	
}
