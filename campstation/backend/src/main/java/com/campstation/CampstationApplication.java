package com.campstation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class CampstationApplication {

    public static void main(String[] args) {
        SpringApplication.run(CampstationApplication.class, args);
    }
}
