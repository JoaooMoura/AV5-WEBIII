package com.autobots.automanager.adaptadores;

import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.autobots.automanager.entidades.CredencialUsuarioSenha;
import com.autobots.automanager.entidades.Usuario;

@SuppressWarnings("serial")
public class UserDetailsImpl implements UserDetails {
	private final Usuario usuario;
	private final CredencialUsuarioSenha credencial;

	public UserDetailsImpl(Usuario usuario, CredencialUsuarioSenha credencial) {
		this.usuario = usuario;
		this.credencial = credencial;
	}

	public Long getId() {
		return usuario.getId();
	}

	public Usuario getUsuario() {
		return usuario;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return usuario.getPerfis().stream()
				.map(perfil -> new SimpleGrantedAuthority("ROLE_" + perfil.name()))
				.collect(Collectors.toSet());
	}

	@Override
	public String getPassword() {
		return credencial.getSenha();
	}

	@Override
	public String getUsername() {
		return credencial.getNomeUsuario();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return !credencial.isInativo();
	}
}
