package com.autobots.automanager.modelo;

import java.util.List;

import org.springframework.stereotype.Component;

import com.autobots.automanager.entidades.Cliente;

@Component
public class ClienteSelecionador {
	public Cliente selecionar(List<Cliente> clientes, long id) {
		for (Cliente cliente : clientes) {
			if (cliente.getId() != null && cliente.getId().equals(id)) {
				return cliente;
			}
		}
		return null;
	}
}
