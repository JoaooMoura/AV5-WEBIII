package com.autobots.apicatalogo.servicos;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.autobots.apicatalogo.entidades.Empresa;
import com.autobots.apicatalogo.repositorios.EmpresaRepositorio;
import com.autobots.apicatalogo.seguranca.AutorizacaoServico;

@Service
public class ConsultaEmpresaServico {
	private final EmpresaRepositorio empresaRepositorio;
	private final AutorizacaoServico autorizacaoServico;

	public ConsultaEmpresaServico(EmpresaRepositorio empresaRepositorio, AutorizacaoServico autorizacaoServico) {
		this.empresaRepositorio = empresaRepositorio;
		this.autorizacaoServico = autorizacaoServico;
	}

	public Empresa buscar(Long empresaId, String autorizacao) {
		autorizacaoServico.autorizar(autorizacao, empresaId);
		return empresaRepositorio.findById(empresaId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
	}
}
