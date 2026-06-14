package com.autobots.automanager.adaptadores;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.autobots.automanager.entidades.Credencial;
import com.autobots.automanager.entidades.CredencialUsuarioSenha;
import com.autobots.automanager.entidades.Usuario;
import com.autobots.automanager.repositorios.UsuarioRepositorio;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
	private final UsuarioRepositorio usuarioRepositorio;

	public UserDetailsServiceImpl(UsuarioRepositorio usuarioRepositorio) {
		this.usuarioRepositorio = usuarioRepositorio;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		for (Usuario usuario : usuarioRepositorio.findAll()) {
			if (usuario.getCredenciais() == null) {
				continue;
			}

			for (Credencial credencial : usuario.getCredenciais()) {
				if (credencial instanceof CredencialUsuarioSenha) {
					CredencialUsuarioSenha usuarioSenha = (CredencialUsuarioSenha) credencial;

					if (!usuarioSenha.isInativo() && usuarioSenha.getNomeUsuario().equals(username)) {
						return new UserDetailsImpl(usuario, usuarioSenha);
					}
				}
			}
		}
		throw new UsernameNotFoundException(username);
	}
}
