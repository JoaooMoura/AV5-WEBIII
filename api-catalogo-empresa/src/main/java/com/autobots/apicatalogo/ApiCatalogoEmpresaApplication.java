package com.autobots.apicatalogo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan("com.autobots.apicatalogo.entidades")
@EnableJpaRepositories("com.autobots.apicatalogo.repositorios")
public class ApiCatalogoEmpresaApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiCatalogoEmpresaApplication.class, args);
	}
}
