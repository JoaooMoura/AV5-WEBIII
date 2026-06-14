package com.autobots.apifuncionarios.servicos;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.autobots.apifuncionarios.dto.UsuarioAv5Dto;
import com.autobots.apifuncionarios.entidades.Usuario;
import com.autobots.apifuncionarios.enumeracoes.PerfilUsuario;
import com.autobots.apifuncionarios.servicos.ConsultaEmpresaServico;
import com.autobots.apifuncionarios.servicos.MapeadorAv5;

@Service
public class FuncionariosEmpresaServico {
	private final ConsultaEmpresaServico consultaEmpresaServico;
	private final MapeadorAv5 mapeador;

	public FuncionariosEmpresaServico(ConsultaEmpresaServico consultaEmpresaServico, MapeadorAv5 mapeador) {
		this.consultaEmpresaServico = consultaEmpresaServico;
		this.mapeador = mapeador;
	}

	@Transactional(readOnly = true)
	public List<UsuarioAv5Dto> listar(Long empresaId, String autorizacao) {
		return consultaEmpresaServico.buscar(empresaId, autorizacao).getUsuarios().stream()
				.filter(this::funcionario)
				.map(mapeador::criarUsuario)
				.toList();
	}

	private boolean funcionario(Usuario usuario) {
		return usuario.getPerfis().contains(PerfilUsuario.ADMINISTRADOR)
				|| usuario.getPerfis().contains(PerfilUsuario.GERENTE)
				|| usuario.getPerfis().contains(PerfilUsuario.VENDEDOR)
				|| usuario.getPerfis().contains(PerfilUsuario.FUNCIONARIO);
	}
}
