package com.autobots.apiveiculos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan("com.autobots.apiveiculos.entidades")
@EnableJpaRepositories("com.autobots.apiveiculos.repositorios")
public class ApiVeiculosAtendidosApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiVeiculosAtendidosApplication.class, args);
	}
}
