package com.autobots.apiclientes.dto;

import java.util.Set;

import com.autobots.apiclientes.entidades.Documento;
import com.autobots.apiclientes.entidades.Email;
import com.autobots.apiclientes.entidades.Endereco;
import com.autobots.apiclientes.entidades.Telefone;
import com.autobots.apiclientes.enumeracoes.PerfilUsuario;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UsuarioAv5Dto {
	private Long id;
	private String nome;
	private String nomeSocial;
	private Set<PerfilUsuario> perfis;
	private Set<Documento> documentos;
	private Set<Telefone> telefones;
	private Endereco endereco;
	private Set<Email> emails;
}
