package com.autobots.apicatalogo.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;

import com.autobots.apicatalogo.entidades.Empresa;

public interface EmpresaRepositorio extends JpaRepository<Empresa, Long> {
}
