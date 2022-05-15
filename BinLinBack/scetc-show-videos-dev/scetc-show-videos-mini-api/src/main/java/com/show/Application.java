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
 * 创建时间：2018年6月11日 下午4:00:56
 */
//该项目启用war包部署，启动时候需要额外配置tomcat环境
//http://localhost:8080/swagger-ui.html
public class Application {
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
	
}
