package com.autobots.apiclientes.servicos;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.autobots.apiclientes.dto.UsuarioAv5Dto;
import com.autobots.apiclientes.enumeracoes.PerfilUsuario;
import com.autobots.apiclientes.servicos.ConsultaEmpresaServico;
import com.autobots.apiclientes.servicos.MapeadorAv5;

@Service
public class ClientesEmpresaServico {
	private final ConsultaEmpresaServico consultaEmpresaServico;
	private final MapeadorAv5 mapeador;

	public ClientesEmpresaServico(ConsultaEmpresaServico consultaEmpresaServico, MapeadorAv5 mapeador) {
		this.consultaEmpresaServico = consultaEmpresaServico;
		this.mapeador = mapeador;
	}

	@Transactional(readOnly = true)
	public List<UsuarioAv5Dto> listar(Long empresaId, String autorizacao) {
		return consultaEmpresaServico.buscar(empresaId, autorizacao).getUsuarios().stream()
				.filter(usuario -> usuario.getPerfis().contains(PerfilUsuario.CLIENTE))
				.map(mapeador::criarUsuario)
				.toList();
	}
}
