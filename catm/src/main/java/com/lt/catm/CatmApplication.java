package com.lt.catm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;


@SpringBootApplication
public class CatmApplication {

	public static void main(String[] args) {
		SpringApplication.run(CatmApplication.class, args);
	}

}

