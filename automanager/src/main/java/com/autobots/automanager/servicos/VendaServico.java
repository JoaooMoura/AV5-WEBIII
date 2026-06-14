package com.autobots.automanager.servicos;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.autobots.automanager.entidades.Mercadoria;
import com.autobots.automanager.entidades.Servico;
import com.autobots.automanager.entidades.Usuario;
import com.autobots.automanager.entidades.Veiculo;
import com.autobots.automanager.entidades.Venda;
import com.autobots.automanager.excecoes.VerificarNuloExecao;
import com.autobots.automanager.repositorios.MercadoriaRepositorio;
import com.autobots.automanager.repositorios.ServicoRepositorio;
import com.autobots.automanager.repositorios.UsuarioRepositorio;
import com.autobots.automanager.repositorios.VeiculoRepositorio;
import com.autobots.automanager.repositorios.VendaRepositorio;

@Service
public class VendaServico {
	private final VendaRepositorio vendaRepositorio;
	private final UsuarioRepositorio usuarioRepositorio;
	private final VeiculoRepositorio veiculoRepositorio;
	private final MercadoriaRepositorio mercadoriaRepositorio;
	private final ServicoRepositorio servicoRepositorio;
	private final AutorizacaoServico autorizacaoServico;

	public VendaServico(VendaRepositorio vendaRepositorio, UsuarioRepositorio usuarioRepositorio,
			VeiculoRepositorio veiculoRepositorio, MercadoriaRepositorio mercadoriaRepositorio,
			ServicoRepositorio servicoRepositorio, AutorizacaoServico autorizacaoServico) {
		this.vendaRepositorio = vendaRepositorio;
		this.usuarioRepositorio = usuarioRepositorio;
		this.veiculoRepositorio = veiculoRepositorio;
		this.mercadoriaRepositorio = mercadoriaRepositorio;
		this.servicoRepositorio = servicoRepositorio;
		this.autorizacaoServico = autorizacaoServico;
	}

	@Transactional(readOnly = true)
	public List<Venda> listar() {
		return autorizacaoServico.filtrarVendas(vendaRepositorio.findAll());
	}

	@Transactional(readOnly = true)
	public Venda buscarPorId(Long vendaId) {
		return vendaRepositorio.findById(vendaId)
				.orElseThrow(() -> new VerificarNuloExecao("Venda com id " + vendaId + " nao foi encontrada."));
	}

	@Transactional
	public Venda cadastrar(Venda venda) {
		venda.setId(null);

		if (venda.getCadastro() == null) {
			venda.setCadastro(new Date());
		}

		prepararRelacionamentos(venda);
		prepararFuncionarioVendedor(venda);
		return vendaRepositorio.save(venda);
	}

	@Transactional
	public Venda atualizar(Long vendaId, Venda atualizacao) {
		Venda venda = buscarPorId(vendaId);

		if (atualizacao.getCadastro() != null) {
			venda.setCadastro(atualizacao.getCadastro());
		}
		if (textoValido(atualizacao.getIdentificacao())) {
			venda.setIdentificacao(atualizacao.getIdentificacao());
		}
		if (atualizacao.getCliente() != null) {
			venda.setCliente(atualizacao.getCliente());
		}
		if (atualizacao.getFuncionario() != null) {
			venda.setFuncionario(atualizacao.getFuncionario());
		}
		if (atualizacao.getVeiculo() != null) {
			venda.setVeiculo(atualizacao.getVeiculo());
		}
		if (atualizacao.getMercadorias() != null && !atualizacao.getMercadorias().isEmpty()) {
			venda.setMercadorias(atualizacao.getMercadorias());
		}
		if (atualizacao.getServicos() != null && !atualizacao.getServicos().isEmpty()) {
			venda.setServicos(atualizacao.getServicos());
		}

		prepararRelacionamentos(venda);
		return vendaRepositorio.save(venda);
	}

	@Transactional
	public void excluir(Long vendaId) {
		Venda venda = buscarPorId(vendaId);
		vendaRepositorio.delete(venda);
	}

	private void prepararFuncionarioVendedor(Venda venda) {
		if (autorizacaoServico.vendedorAtual() && venda.getFuncionario() == null) {
			Long usuarioId = autorizacaoServico.obterUsuarioAutenticadoId();
			Usuario funcionario = usuarioRepositorio.findById(usuarioId)
					.orElseThrow(() -> new VerificarNuloExecao("Usuario funcionario com id " + usuarioId + " nao foi encontrado."));
			venda.setFuncionario(funcionario);
		}
	}

	private void prepararRelacionamentos(Venda venda) {
		if (venda.getCliente() != null && venda.getCliente().getId() != null) {
			Usuario cliente = usuarioRepositorio.findById(venda.getCliente().getId())
					.orElseThrow(() -> new VerificarNuloExecao(
							"Usuario cliente com id " + venda.getCliente().getId() + " nao foi encontrado."));
			venda.setCliente(cliente);
		}
		if (venda.getFuncionario() != null && venda.getFuncionario().getId() != null) {
			Usuario funcionario = usuarioRepositorio.findById(venda.getFuncionario().getId())
					.orElseThrow(() -> new VerificarNuloExecao(
							"Usuario funcionario com id " + venda.getFuncionario().getId() + " nao foi encontrado."));
			venda.setFuncionario(funcionario);
		}
		if (venda.getVeiculo() != null && venda.getVeiculo().getId() != null) {
			Veiculo veiculo = veiculoRepositorio.findById(venda.getVeiculo().getId())
					.orElseThrow(() -> new VerificarNuloExecao(
							"Veiculo com id " + venda.getVeiculo().getId() + " nao foi encontrado."));
			venda.setVeiculo(veiculo);
		}
		if (venda.getMercadorias() == null) {
			venda.setMercadorias(new HashSet<>());
		} else {
			venda.setMercadorias(buscarMercadorias(venda.getMercadorias()));
		}
		if (venda.getServicos() == null) {
			venda.setServicos(new HashSet<>());
		} else {
			venda.setServicos(buscarServicos(venda.getServicos()));
		}
	}

	private Set<Mercadoria> buscarMercadorias(Set<Mercadoria> mercadorias) {
		Set<Mercadoria> resultado = new HashSet<>();
		mercadorias.forEach(mercadoria -> {
			if (mercadoria.getId() == null) {
				resultado.add(mercadoria);
			} else {
				Mercadoria encontrada = mercadoriaRepositorio.findById(mercadoria.getId())
						.orElseThrow(() -> new VerificarNuloExecao(
								"Mercadoria com id " + mercadoria.getId() + " nao foi encontrada."));
				resultado.add(encontrada);
			}
		});
		return resultado;
	}

	private Set<Servico> buscarServicos(Set<Servico> servicos) {
		Set<Servico> resultado = new HashSet<>();
		servicos.forEach(servico -> {
			if (servico.getId() == null) {
				resultado.add(servico);
			} else {
				Servico encontrado = servicoRepositorio.findById(servico.getId())
						.orElseThrow(() -> new VerificarNuloExecao(
								"Servico com id " + servico.getId() + " nao foi encontrado."));
				resultado.add(encontrado);
			}
		});
		return resultado;
	}

	private boolean textoValido(String valor) {
		return valor != null && !valor.trim().isEmpty();
	}
}
