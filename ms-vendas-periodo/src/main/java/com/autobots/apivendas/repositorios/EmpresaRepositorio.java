package com.autobots.apivendas.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;

import com.autobots.apivendas.entidades.Empresa;

public interface EmpresaRepositorio extends JpaRepository<Empresa, Long> {
}
