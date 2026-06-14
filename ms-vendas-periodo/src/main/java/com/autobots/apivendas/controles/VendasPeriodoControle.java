package com.autobots.apivendas.controles;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.autobots.apivendas.servicos.VendasPeriodoServico;
import com.autobots.apivendas.dto.VendaAv5Dto;

@RestController
@RequestMapping("/interno/empresas")
public class VendasPeriodoControle {
	private final VendasPeriodoServico vendasPeriodoServico;

	public VendasPeriodoControle(VendasPeriodoServico vendasPeriodoServico) {
		this.vendasPeriodoServico = vendasPeriodoServico;
	}

	@GetMapping("/{empresaId}/vendas/itens")
	public List<VendaAv5Dto> listarVendas(
			@PathVariable Long empresaId,
			@RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE) LocalDate inicio,
			@RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE) LocalDate fim,
			@RequestHeader(value = "Authorization", required = false) String autorizacao) {
		return vendasPeriodoServico.listar(empresaId, inicio, fim, autorizacao);
	}
}
