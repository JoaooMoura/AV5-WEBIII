package com.autobots.apicatalogo.controles;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.autobots.apicatalogo.servicos.CatalogoEmpresaServico;
import com.autobots.apicatalogo.dto.CatalogoAv5Dto;

@RestController
@RequestMapping("/interno/empresas")
public class CatalogoEmpresaControle {
	private final CatalogoEmpresaServico catalogoEmpresaServico;

	public CatalogoEmpresaControle(CatalogoEmpresaServico catalogoEmpresaServico) {
		this.catalogoEmpresaServico = catalogoEmpresaServico;
	}

	@GetMapping("/{empresaId}/catalogo")
	public CatalogoAv5Dto listarCatalogo(
			@PathVariable Long empresaId,
			@RequestHeader(value = "Authorization", required = false) String autorizacao) {
		return catalogoEmpresaServico.listar(empresaId, autorizacao);
	}
}
