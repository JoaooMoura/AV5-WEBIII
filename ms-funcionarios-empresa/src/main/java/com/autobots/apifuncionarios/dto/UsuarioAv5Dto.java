package com.autobots.apifuncionarios.dto;

import java.util.Set;

import com.autobots.apifuncionarios.entidades.Documento;
import com.autobots.apifuncionarios.entidades.Email;
import com.autobots.apifuncionarios.entidades.Endereco;
import com.autobots.apifuncionarios.entidades.Telefone;
import com.autobots.apifuncionarios.enumeracoes.PerfilUsuario;

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
