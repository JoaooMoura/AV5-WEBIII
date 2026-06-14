package com.autobots.apiveiculos.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;

import com.autobots.apiveiculos.entidades.Usuario;

public interface UsuarioRepositorio extends JpaRepository<Usuario, Long> {
}
