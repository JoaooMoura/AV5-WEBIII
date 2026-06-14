package com.autobots.apifuncionarios;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan("com.autobots.apifuncionarios.entidades")
@EnableJpaRepositories("com.autobots.apifuncionarios.repositorios")
public class ApiFuncionariosEmpresaApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiFuncionariosEmpresaApplication.class, args);
	}
}
