package com.autobots.apiclientes.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;

import com.autobots.apiclientes.entidades.Usuario;

public interface UsuarioRepositorio extends JpaRepository<Usuario, Long> {
}
