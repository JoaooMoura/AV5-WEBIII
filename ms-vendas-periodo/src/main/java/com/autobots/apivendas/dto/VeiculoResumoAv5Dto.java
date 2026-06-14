package com.autobots.apivendas.dto;

import com.autobots.apivendas.enumeracoes.TipoVeiculo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class VeiculoResumoAv5Dto {
	private Long id;
	private TipoVeiculo tipo;
	private String modelo;
	private String placa;
	private UsuarioResumoAv5Dto proprietario;
}
