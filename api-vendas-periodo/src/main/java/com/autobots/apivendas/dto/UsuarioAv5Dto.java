package com.autobots.apivendas.dto;

import java.util.Set;

import com.autobots.apivendas.entidades.Documento;
import com.autobots.apivendas.entidades.Email;
import com.autobots.apivendas.entidades.Endereco;
import com.autobots.apivendas.entidades.Telefone;
import com.autobots.apivendas.enumeracoes.PerfilUsuario;

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
