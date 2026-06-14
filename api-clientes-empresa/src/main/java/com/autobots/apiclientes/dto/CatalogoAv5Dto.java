package com.autobots.apiclientes.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CatalogoAv5Dto {
	private Long empresaId;
	private List<ItemCatalogoAv5Dto> itens;
}
