package com.autobots.apifuncionarios.controles;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.autobots.apifuncionarios.servicos.FuncionariosEmpresaServico;
import com.autobots.apifuncionarios.dto.UsuarioAv5Dto;

@RestController
@RequestMapping("/interno/empresas")
public class FuncionariosEmpresaControle {
	private final FuncionariosEmpresaServico funcionariosEmpresaServico;

	public FuncionariosEmpresaControle(FuncionariosEmpresaServico funcionariosEmpresaServico) {
		this.funcionariosEmpresaServico = funcionariosEmpresaServico;
	}

	@GetMapping("/{empresaId}/funcionarios")
	public List<UsuarioAv5Dto> listarFuncionarios(
			@PathVariable Long empresaId,
			@RequestHeader(value = "Authorization", required = false) String autorizacao) {
		return funcionariosEmpresaServico.listar(empresaId, autorizacao);
	}
}
