package com.gg.gong9;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class Gong9Application {

    public static void main(String[] args) {
        SpringApplication.run(Gong9Application.class, args);
    }

}
