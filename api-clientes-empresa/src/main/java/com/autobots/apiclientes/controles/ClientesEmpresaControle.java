package com.autobots.apiclientes.controles;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.autobots.apiclientes.servicos.ClientesEmpresaServico;
import com.autobots.apiclientes.dto.UsuarioAv5Dto;

@RestController
@RequestMapping("/interno/empresas")
public class ClientesEmpresaControle {
	private final ClientesEmpresaServico clientesEmpresaServico;

	public ClientesEmpresaControle(ClientesEmpresaServico clientesEmpresaServico) {
		this.clientesEmpresaServico = clientesEmpresaServico;
	}

	@GetMapping("/{empresaId}/clientes")
	public List<UsuarioAv5Dto> listarClientes(
			@PathVariable Long empresaId,
			@RequestHeader(value = "Authorization", required = false) String autorizacao) {
		return clientesEmpresaServico.listar(empresaId, autorizacao);
	}
}
