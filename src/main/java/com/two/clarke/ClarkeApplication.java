package com.two.clarke;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class ClarkeApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(ClarkeApplication.class, args);
		context.getBean(OracleAQService.class).sendMessageJMT(); // <-- here
		context.getBean(OracleAQService.class).sendMessage(); // <-- here
	}
}
