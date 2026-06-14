package com.autobots.apiveiculos.seguranca;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.autobots.apiveiculos.entidades.Credencial;
import com.autobots.apiveiculos.entidades.CredencialUsuarioSenha;
import com.autobots.apiveiculos.entidades.Empresa;
import com.autobots.apiveiculos.entidades.Usuario;
import com.autobots.apiveiculos.enumeracoes.PerfilUsuario;
import com.autobots.apiveiculos.repositorios.EmpresaRepositorio;
import com.autobots.apiveiculos.repositorios.UsuarioRepositorio;

@Service
public class AutorizacaoServico {
	private final ProvedorJwt provedorJwt;
	private final UsuarioRepositorio usuarioRepositorio;
	private final EmpresaRepositorio empresaRepositorio;

	public AutorizacaoServico(ProvedorJwt provedorJwt, UsuarioRepositorio usuarioRepositorio,
			EmpresaRepositorio empresaRepositorio) {
		this.provedorJwt = provedorJwt;
		this.usuarioRepositorio = usuarioRepositorio;
		this.empresaRepositorio = empresaRepositorio;
	}

	public void autorizar(String autorizacao, Long empresaId) {
		String nomeUsuario = provedorJwt.obterNomeUsuario(autorizacao);

		if (nomeUsuario == null) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
		}

		Usuario usuario = buscarUsuario(nomeUsuario);

		if (usuario.getPerfis().contains(PerfilUsuario.ADMINISTRADOR)) {
			return;
		}
		if (!usuario.getPerfis().contains(PerfilUsuario.GERENTE)) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN);
		}

		Empresa empresa = empresaRepositorio.findById(empresaId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		boolean associado = empresa.getUsuarios().stream()
				.anyMatch(atual -> atual.getId() != null && atual.getId().equals(usuario.getId()));

		if (!associado) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN);
		}
	}

	private Usuario buscarUsuario(String nomeUsuario) {
		for (Usuario usuario : usuarioRepositorio.findAll()) {
			for (Credencial credencial : usuario.getCredenciais()) {
				if (credencial instanceof CredencialUsuarioSenha) {
					CredencialUsuarioSenha usuarioSenha = (CredencialUsuarioSenha) credencial;

					if (!usuarioSenha.isInativo() && usuarioSenha.getNomeUsuario().equals(nomeUsuario)) {
						return usuario;
					}
				}
			}
		}
		throw new ResponseStatusException(HttpStatus.FORBIDDEN);
	}
}
