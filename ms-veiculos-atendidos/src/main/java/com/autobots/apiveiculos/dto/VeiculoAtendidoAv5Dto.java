package com.autobots.apiveiculos.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class VeiculoAtendidoAv5Dto {
	private VeiculoResumoAv5Dto veiculo;
	private List<VendaAv5Dto> vendas;
}
