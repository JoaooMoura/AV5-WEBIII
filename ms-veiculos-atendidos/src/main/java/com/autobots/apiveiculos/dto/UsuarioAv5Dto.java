package com.autobots.apiveiculos.dto;

import java.util.Set;

import com.autobots.apiveiculos.entidades.Documento;
import com.autobots.apiveiculos.entidades.Email;
import com.autobots.apiveiculos.entidades.Endereco;
import com.autobots.apiveiculos.entidades.Telefone;
import com.autobots.apiveiculos.enumeracoes.PerfilUsuario;

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
