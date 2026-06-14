package com.autobots.apivendas.dto;

import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class VendaAv5Dto {
	private Long id;
	private String identificacao;
	private Date cadastro;
	private UsuarioResumoAv5Dto cliente;
	private UsuarioResumoAv5Dto funcionario;
	private VeiculoResumoAv5Dto veiculo;
	private List<ItemCatalogoAv5Dto> servicos;
	private List<ItemCatalogoAv5Dto> mercadorias;
	private double valorTotal;
}
