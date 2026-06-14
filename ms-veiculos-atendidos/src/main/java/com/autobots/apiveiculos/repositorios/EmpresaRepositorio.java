package com.autobots.apiveiculos.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;

import com.autobots.apiveiculos.entidades.Empresa;

public interface EmpresaRepositorio extends JpaRepository<Empresa, Long> {
}
