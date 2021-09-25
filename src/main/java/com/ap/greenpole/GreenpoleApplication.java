package com.ap.greenpole;

import co.elastic.apm.attach.ElasticApmAttacher;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class  GreenpoleApplication {

	public static void main(String[] args) {
		ElasticApmAttacher.attach();
		SpringApplication.run(GreenpoleApplication.class, args);
	}

}
