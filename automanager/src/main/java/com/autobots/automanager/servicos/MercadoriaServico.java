package com.autobots.automanager.servicos;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.autobots.automanager.entidades.Mercadoria;
import com.autobots.automanager.excecoes.VerificarNuloExecao;
import com.autobots.automanager.repositorios.MercadoriaRepositorio;

@Service
public class MercadoriaServico {
	private final MercadoriaRepositorio mercadoriaRepositorio;

	public MercadoriaServico(MercadoriaRepositorio mercadoriaRepositorio) {
		this.mercadoriaRepositorio = mercadoriaRepositorio;
	}

	@Transactional(readOnly = true)
	public List<Mercadoria> listar() {
		return mercadoriaRepositorio.findAll();
	}

	@Transactional(readOnly = true)
	public Mercadoria buscarPorId(Long mercadoriaId) {
		return mercadoriaRepositorio.findById(mercadoriaId)
				.orElseThrow(() -> new VerificarNuloExecao("Mercadoria com id " + mercadoriaId + " nao foi encontrada."));
	}

	@Transactional
	public Mercadoria cadastrar(Mercadoria mercadoria) {
		mercadoria.setId(null);

		if (mercadoria.getCadastro() == null) {
			mercadoria.setCadastro(new Date());
		}

		return mercadoriaRepositorio.save(mercadoria);
	}

	@Transactional
	public Mercadoria atualizar(Long mercadoriaId, Mercadoria atualizacao) {
		Mercadoria mercadoria = buscarPorId(mercadoriaId);

		if (atualizacao.getValidade() != null) {
			mercadoria.setValidade(atualizacao.getValidade());
		}
		if (atualizacao.getFabricacao() != null) {
			mercadoria.setFabricacao(atualizacao.getFabricacao());
		}
		if (atualizacao.getCadastro() != null) {
			mercadoria.setCadastro(atualizacao.getCadastro());
		}
		if (textoValido(atualizacao.getNome())) {
			mercadoria.setNome(atualizacao.getNome());
		}
		mercadoria.setQuantidade(atualizacao.getQuantidade());
		mercadoria.setValor(atualizacao.getValor());
		if (textoValido(atualizacao.getDescricao())) {
			mercadoria.setDescricao(atualizacao.getDescricao());
		}

		return mercadoriaRepositorio.save(mercadoria);
	}

	@Transactional
	public void excluir(Long mercadoriaId) {
		Mercadoria mercadoria = buscarPorId(mercadoriaId);
		mercadoriaRepositorio.delete(mercadoria);
	}

	private boolean textoValido(String valor) {
		return valor != null && !valor.trim().isEmpty();
	}
}
