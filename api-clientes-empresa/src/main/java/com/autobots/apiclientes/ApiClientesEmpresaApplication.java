package com.autobots.apiclientes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan("com.autobots.apiclientes.entidades")
@EnableJpaRepositories("com.autobots.apiclientes.repositorios")
public class ApiClientesEmpresaApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiClientesEmpresaApplication.class, args);
	}
}
