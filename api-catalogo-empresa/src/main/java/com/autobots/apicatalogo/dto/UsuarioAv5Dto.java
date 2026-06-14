package com.autobots.apicatalogo.dto;

import java.util.Set;

import com.autobots.apicatalogo.entidades.Documento;
import com.autobots.apicatalogo.entidades.Email;
import com.autobots.apicatalogo.entidades.Endereco;
import com.autobots.apicatalogo.entidades.Telefone;
import com.autobots.apicatalogo.enumeracoes.PerfilUsuario;

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
