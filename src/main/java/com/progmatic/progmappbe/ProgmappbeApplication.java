package com.progmatic.progmappbe;

import com.progmatic.progmappbe.entities.BaseEntity;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

@EntityScan(
        basePackageClasses = {BaseEntity.class, Jsr310JpaConverters.class}
)
@SpringBootApplication
public class ProgmappbeApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProgmappbeApplication.class, args);
	}
}
