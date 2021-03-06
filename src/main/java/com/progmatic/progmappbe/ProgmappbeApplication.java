package com.progmatic.progmappbe;

import com.progmatic.progmappbe.entities.BaseEntity;
import com.progmatic.progmappbe.helpers.sourceevaluator.MyClassloader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

@SpringBootApplication
public class ProgmappbeApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProgmappbeApplication.class, args);
	}
}
