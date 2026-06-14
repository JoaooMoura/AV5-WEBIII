package com.autobots.apivendas.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;

import com.autobots.apivendas.entidades.Usuario;

public interface UsuarioRepositorio extends JpaRepository<Usuario, Long> {
}
