package com.unocode.slowme;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class SlowMeApplication {

	public static void main(String[] args) {
		SpringApplication.run(SlowMeApplication.class, args);
	}

}
