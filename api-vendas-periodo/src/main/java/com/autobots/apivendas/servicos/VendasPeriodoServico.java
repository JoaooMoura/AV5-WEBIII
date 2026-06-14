package com.autobots.apivendas.servicos;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.autobots.apivendas.dto.VendaAv5Dto;
import com.autobots.apivendas.servicos.ConsultaEmpresaServico;
import com.autobots.apivendas.servicos.MapeadorAv5;

@Service
public class VendasPeriodoServico {
	private final ConsultaEmpresaServico consultaEmpresaServico;
	private final MapeadorAv5 mapeador;

	public VendasPeriodoServico(ConsultaEmpresaServico consultaEmpresaServico, MapeadorAv5 mapeador) {
		this.consultaEmpresaServico = consultaEmpresaServico;
		this.mapeador = mapeador;
	}

	@Transactional(readOnly = true)
	public List<VendaAv5Dto> listar(Long empresaId, LocalDate inicio, LocalDate fim, String autorizacao) {
		validarPeriodo(inicio, fim);
		Date inicioData = Date.from(inicio.atStartOfDay(ZoneId.systemDefault()).toInstant());
		Date fimData = Date.from(fim.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
		return consultaEmpresaServico.buscar(empresaId, autorizacao).getVendas().stream()
				.filter(venda -> venda.getCadastro() != null
						&& !venda.getCadastro().before(inicioData)
						&& venda.getCadastro().before(fimData))
				.map(mapeador::criarVenda)
				.toList();
	}

	private void validarPeriodo(LocalDate inicio, LocalDate fim) {
		if (inicio == null || fim == null || inicio.isAfter(fim)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Periodo invalido.");
		}
	}
}
