package com.show.controller;

import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("")
@Api(tags = { "欢迎接口" })
public class HelloWorldController {
	
	@GetMapping("/hello")
	public String Hello() {
		return "Hello Spring Boot~";
	}
	@GetMapping("/")
	public String index() {
		return "Welcome to use show-api";
	}
	
}
