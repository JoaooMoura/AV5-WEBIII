package com.autobots.apicatalogo.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ItemCatalogoAv5Dto {
	private Long id;
	private String tipo;
	private Date cadastro;
	private String nome;
	private String descricao;
	private double valor;
	private Long quantidade;
}
