package com.autobots.apicatalogo.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;

import com.autobots.apicatalogo.entidades.Usuario;

public interface UsuarioRepositorio extends JpaRepository<Usuario, Long> {
}
