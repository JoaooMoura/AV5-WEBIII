package com.autobots.apivendas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan("com.autobots.apivendas.entidades")
@EnableJpaRepositories("com.autobots.apivendas.repositorios")
public class ApiVendasPeriodoApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiVendasPeriodoApplication.class, args);
	}
}
