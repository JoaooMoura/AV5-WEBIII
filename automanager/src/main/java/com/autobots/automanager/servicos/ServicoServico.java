package com.autobots.automanager.servicos;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.autobots.automanager.entidades.Servico;
import com.autobots.automanager.excecoes.VerificarNuloExecao;
import com.autobots.automanager.repositorios.ServicoRepositorio;

@Service
public class ServicoServico {
	private final ServicoRepositorio servicoRepositorio;

	public ServicoServico(ServicoRepositorio servicoRepositorio) {
		this.servicoRepositorio = servicoRepositorio;
	}

	@Transactional(readOnly = true)
	public List<Servico> listar() {
		return servicoRepositorio.findAll();
	}

	@Transactional(readOnly = true)
	public Servico buscarPorId(Long servicoId) {
		return servicoRepositorio.findById(servicoId)
				.orElseThrow(() -> new VerificarNuloExecao("Servico com id " + servicoId + " nao foi encontrado."));
	}

	@Transactional
	public Servico cadastrar(Servico servico) {
		servico.setId(null);

		if (servico.getCadastro() == null) {
			servico.setCadastro(new Date());
		}

		return servicoRepositorio.save(servico);
	}

	@Transactional
	public Servico atualizar(Long servicoId, Servico atualizacao) {
		Servico servico = buscarPorId(servicoId);

		if (textoValido(atualizacao.getNome())) {
			servico.setNome(atualizacao.getNome());
		}
		servico.setValor(atualizacao.getValor());
		if (textoValido(atualizacao.getDescricao())) {
			servico.setDescricao(atualizacao.getDescricao());
		}
		if (atualizacao.getCadastro() != null) {
			servico.setCadastro(atualizacao.getCadastro());
		}

		return servicoRepositorio.save(servico);
	}

	@Transactional
	public void excluir(Long servicoId) {
		Servico servico = buscarPorId(servicoId);
		servicoRepositorio.delete(servico);
	}

	private boolean textoValido(String valor) {
		return valor != null && !valor.trim().isEmpty();
	}
}
