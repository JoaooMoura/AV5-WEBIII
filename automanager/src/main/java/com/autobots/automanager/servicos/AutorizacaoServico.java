package com.autobots.automanager.servicos;

import java.util.List;
import java.util.Set;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.autobots.automanager.adaptadores.UserDetailsImpl;
import com.autobots.automanager.entidades.Usuario;
import com.autobots.automanager.entidades.Venda;
import com.autobots.automanager.enumeracoes.PerfilUsuario;
import com.autobots.automanager.repositorios.UsuarioRepositorio;
import com.autobots.automanager.repositorios.VendaRepositorio;

@Service
public class AutorizacaoServico {
	private final UsuarioRepositorio usuarioRepositorio;
	private final VendaRepositorio vendaRepositorio;

	public AutorizacaoServico(UsuarioRepositorio usuarioRepositorio, VendaRepositorio vendaRepositorio) {
		this.usuarioRepositorio = usuarioRepositorio;
		this.vendaRepositorio = vendaRepositorio;
	}

	public boolean podeListarUsuarios(Authentication autenticacao) {
		return administrador(autenticacao) || gerente(autenticacao) || vendedor(autenticacao);
	}

	public boolean podeLerUsuario(Authentication autenticacao, Long usuarioId) {
		if (administrador(autenticacao)) {
			return true;
		}

		Usuario usuario = obterUsuario(usuarioId);

		if (usuario == null) {
			return false;
		}
		if (gerente(autenticacao)) {
			return possuiApenasPerfis(usuario, PerfilUsuario.GERENTE, PerfilUsuario.VENDEDOR, PerfilUsuario.CLIENTE);
		}
		if (vendedor(autenticacao)) {
			return possuiApenasPerfis(usuario, PerfilUsuario.CLIENTE);
		}
		if (cliente(autenticacao)) {
			return mesmoUsuarioAutenticado(autenticacao, usuarioId);
		}
		return false;
	}

	public boolean podeCriarUsuario(Authentication autenticacao, Usuario usuario) {
		if (administrador(autenticacao)) {
			return true;
		}
		if (gerente(autenticacao)) {
			return possuiApenasPerfis(usuario, PerfilUsuario.GERENTE, PerfilUsuario.VENDEDOR, PerfilUsuario.CLIENTE);
		}
		if (vendedor(autenticacao)) {
			return possuiApenasPerfis(usuario, PerfilUsuario.CLIENTE);
		}
		return false;
	}

	public boolean podeAtualizarUsuario(Authentication autenticacao, Long usuarioId, Usuario atualizacao) {
		if (administrador(autenticacao)) {
			return true;
		}

		Usuario usuario = obterUsuario(usuarioId);

		if (usuario == null) {
			return false;
		}
		if (gerente(autenticacao)) {
			return possuiApenasPerfis(usuario, PerfilUsuario.GERENTE, PerfilUsuario.VENDEDOR, PerfilUsuario.CLIENTE)
					&& atualizacaoPermitida(atualizacao, PerfilUsuario.GERENTE, PerfilUsuario.VENDEDOR,
							PerfilUsuario.CLIENTE);
		}
		if (vendedor(autenticacao)) {
			return possuiApenasPerfis(usuario, PerfilUsuario.CLIENTE)
					&& atualizacaoPermitida(atualizacao, PerfilUsuario.CLIENTE);
		}
		return false;
	}

	public boolean podeExcluirUsuario(Authentication autenticacao, Long usuarioId) {
		if (administrador(autenticacao)) {
			return true;
		}

		Usuario usuario = obterUsuario(usuarioId);

		if (usuario == null) {
			return false;
		}
		if (gerente(autenticacao)) {
			return possuiApenasPerfis(usuario, PerfilUsuario.GERENTE, PerfilUsuario.VENDEDOR, PerfilUsuario.CLIENTE);
		}
		if (vendedor(autenticacao)) {
			return possuiApenasPerfis(usuario, PerfilUsuario.CLIENTE);
		}
		return false;
	}

	public boolean podeGerenciarUsuario(Authentication autenticacao, Long usuarioId) {
		return podeAtualizarUsuario(autenticacao, usuarioId, null);
	}

	public boolean podeListarVendas(Authentication autenticacao) {
		return administrador(autenticacao) || gerente(autenticacao) || vendedor(autenticacao) || cliente(autenticacao);
	}

	public boolean podeLerVenda(Authentication autenticacao, Long vendaId) {
		if (administrador(autenticacao) || gerente(autenticacao)) {
			return true;
		}

		Venda venda = obterVenda(vendaId);

		if (venda == null) {
			return false;
		}
		if (vendedor(autenticacao)) {
			return venda.getFuncionario() != null && mesmoUsuarioAutenticado(autenticacao, venda.getFuncionario().getId());
		}
		if (cliente(autenticacao)) {
			return venda.getCliente() != null && mesmoUsuarioAutenticado(autenticacao, venda.getCliente().getId());
		}
		return false;
	}

	public boolean podeCriarVenda(Authentication autenticacao, Venda venda) {
		if (administrador(autenticacao) || gerente(autenticacao)) {
			return true;
		}
		if (vendedor(autenticacao)) {
			return venda == null || venda.getFuncionario() == null || venda.getFuncionario().getId() == null
					|| mesmoUsuarioAutenticado(autenticacao, venda.getFuncionario().getId());
		}
		return false;
	}

	public boolean podeAlterarVenda(Authentication autenticacao, Long vendaId) {
		return administrador(autenticacao) || gerente(autenticacao);
	}

	public List<Usuario> filtrarUsuarios(List<Usuario> usuarios) {
		Authentication autenticacao = SecurityContextHolder.getContext().getAuthentication();

		if (administrador(autenticacao)) {
			return usuarios;
		}
		if (gerente(autenticacao)) {
			return usuarios.stream()
					.filter(usuario -> possuiApenasPerfis(usuario, PerfilUsuario.GERENTE, PerfilUsuario.VENDEDOR,
							PerfilUsuario.CLIENTE))
					.toList();
		}
		if (vendedor(autenticacao)) {
			return usuarios.stream()
					.filter(usuario -> possuiApenasPerfis(usuario, PerfilUsuario.CLIENTE))
					.toList();
		}
		return List.of();
	}

	public List<Venda> filtrarVendas(List<Venda> vendas) {
		Authentication autenticacao = SecurityContextHolder.getContext().getAuthentication();

		if (administrador(autenticacao) || gerente(autenticacao)) {
			return vendas;
		}
		if (vendedor(autenticacao)) {
			Long usuarioId = obterUsuarioAutenticadoId(autenticacao);
			return vendas.stream()
					.filter(venda -> venda.getFuncionario() != null && usuarioId.equals(venda.getFuncionario().getId()))
					.toList();
		}
		if (cliente(autenticacao)) {
			Long usuarioId = obterUsuarioAutenticadoId(autenticacao);
			return vendas.stream()
					.filter(venda -> venda.getCliente() != null && usuarioId.equals(venda.getCliente().getId()))
					.toList();
		}
		return List.of();
	}

	public boolean vendedorAtual() {
		return vendedor(SecurityContextHolder.getContext().getAuthentication());
	}

	public Long obterUsuarioAutenticadoId() {
		return obterUsuarioAutenticadoId(SecurityContextHolder.getContext().getAuthentication());
	}

	private boolean administrador(Authentication autenticacao) {
		return possuiAutoridade(autenticacao, "ROLE_ADMINISTRADOR");
	}

	private boolean gerente(Authentication autenticacao) {
		return possuiAutoridade(autenticacao, "ROLE_GERENTE");
	}

	private boolean vendedor(Authentication autenticacao) {
		return possuiAutoridade(autenticacao, "ROLE_VENDEDOR");
	}

	private boolean cliente(Authentication autenticacao) {
		return possuiAutoridade(autenticacao, "ROLE_CLIENTE");
	}

	private boolean possuiAutoridade(Authentication autenticacao, String autoridade) {
		return autenticacao != null && autenticacao.getAuthorities().stream()
				.anyMatch(atual -> atual.getAuthority().equals(autoridade));
	}

	private boolean mesmoUsuarioAutenticado(Authentication autenticacao, Long usuarioId) {
		Long autenticadoId = obterUsuarioAutenticadoId(autenticacao);
		return autenticadoId != null && autenticadoId.equals(usuarioId);
	}

	private Long obterUsuarioAutenticadoId(Authentication autenticacao) {
		if (autenticacao != null && autenticacao.getPrincipal() instanceof UserDetailsImpl) {
			return ((UserDetailsImpl) autenticacao.getPrincipal()).getId();
		}
		return null;
	}

	private Usuario obterUsuario(Long usuarioId) {
		if (usuarioId == null) {
			return null;
		}
		return usuarioRepositorio.findById(usuarioId).orElse(null);
	}

	private Venda obterVenda(Long vendaId) {
		if (vendaId == null) {
			return null;
		}
		return vendaRepositorio.findById(vendaId).orElse(null);
	}

	private boolean possuiApenasPerfis(Usuario usuario, PerfilUsuario... permitidos) {
		if (usuario == null || usuario.getPerfis() == null || usuario.getPerfis().isEmpty()) {
			return false;
		}

		Set<PerfilUsuario> autorizados = Set.of(permitidos);
		return usuario.getPerfis().stream().allMatch(autorizados::contains);
	}

	private boolean atualizacaoPermitida(Usuario usuario, PerfilUsuario... permitidos) {
		if (usuario == null || usuario.getPerfis() == null || usuario.getPerfis().isEmpty()) {
			return true;
		}

		Set<PerfilUsuario> autorizados = Set.of(permitidos);
		return usuario.getPerfis().stream().allMatch(autorizados::contains);
	}
}
