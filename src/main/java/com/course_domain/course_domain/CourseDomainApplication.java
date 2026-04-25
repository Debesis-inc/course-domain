package com.course_domain.course_domain;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@SpringBootApplication
@EnableMongoAuditing
public class CourseDomainApplication {

	public static void main(String[] args) {
		SpringApplication.run(CourseDomainApplication.class, args);
	}

}
