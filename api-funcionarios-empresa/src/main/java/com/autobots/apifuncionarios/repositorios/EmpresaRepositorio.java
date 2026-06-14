package com.autobots.apifuncionarios.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;

import com.autobots.apifuncionarios.entidades.Empresa;

public interface EmpresaRepositorio extends JpaRepository<Empresa, Long> {
}
