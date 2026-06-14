package com.autobots.apifuncionarios.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;

import com.autobots.apifuncionarios.entidades.Usuario;

public interface UsuarioRepositorio extends JpaRepository<Usuario, Long> {
}
