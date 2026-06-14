package com.autobots.apifuncionarios.servicos;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.autobots.apifuncionarios.entidades.Empresa;
import com.autobots.apifuncionarios.repositorios.EmpresaRepositorio;
import com.autobots.apifuncionarios.seguranca.AutorizacaoServico;

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
