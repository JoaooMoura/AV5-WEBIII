package com.autobots.apiclientes.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;

import com.autobots.apiclientes.entidades.Empresa;

public interface EmpresaRepositorio extends JpaRepository<Empresa, Long> {
}
