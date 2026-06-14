package com.autobots.apicatalogo.servicos;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.autobots.apicatalogo.dto.CatalogoAv5Dto;
import com.autobots.apicatalogo.entidades.Empresa;
import com.autobots.apicatalogo.servicos.ConsultaEmpresaServico;
import com.autobots.apicatalogo.servicos.MapeadorAv5;

@Service
public class CatalogoEmpresaServico {
	private final ConsultaEmpresaServico consultaEmpresaServico;
	private final MapeadorAv5 mapeador;

	public CatalogoEmpresaServico(ConsultaEmpresaServico consultaEmpresaServico, MapeadorAv5 mapeador) {
		this.consultaEmpresaServico = consultaEmpresaServico;
		this.mapeador = mapeador;
	}

	@Transactional(readOnly = true)
	public CatalogoAv5Dto listar(Long empresaId, String autorizacao) {
		Empresa empresa = consultaEmpresaServico.buscar(empresaId, autorizacao);
		return new CatalogoAv5Dto(
				empresaId,
				mapeador.criarCatalogo(empresa.getServicos(), empresa.getMercadorias()));
	}
}
