package com.autobots.automanager.servicos;

import java.util.HashSet;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.autobots.automanager.entidades.Usuario;
import com.autobots.automanager.entidades.Veiculo;
import com.autobots.automanager.entidades.Venda;
import com.autobots.automanager.excecoes.VerificarNuloExecao;
import com.autobots.automanager.repositorios.UsuarioRepositorio;
import com.autobots.automanager.repositorios.VeiculoRepositorio;
import com.autobots.automanager.repositorios.VendaRepositorio;

@Service
public class VeiculoServico {
	private final VeiculoRepositorio veiculoRepositorio;
	private final UsuarioRepositorio usuarioRepositorio;
	private final VendaRepositorio vendaRepositorio;

	public VeiculoServico(VeiculoRepositorio veiculoRepositorio, UsuarioRepositorio usuarioRepositorio,
			VendaRepositorio vendaRepositorio) {
		this.veiculoRepositorio = veiculoRepositorio;
		this.usuarioRepositorio = usuarioRepositorio;
		this.vendaRepositorio = vendaRepositorio;
	}

	@Transactional(readOnly = true)
	public List<Veiculo> listar() {
		return veiculoRepositorio.findAll();
	}

	@Transactional(readOnly = true)
	public Veiculo buscarPorId(Long veiculoId) {
		return veiculoRepositorio.findById(veiculoId)
				.orElseThrow(() -> new VerificarNuloExecao("Veiculo com id " + veiculoId + " nao foi encontrado."));
	}

	@Transactional
	public Veiculo cadastrar(Veiculo veiculo) {
		veiculo.setId(null);
		prepararProprietario(veiculo);
		garantirColecoes(veiculo);
		Veiculo salvo = veiculoRepositorio.save(veiculo);
		vincularAoProprietario(salvo);
		return salvo;
	}

	@Transactional
	public Veiculo atualizar(Long veiculoId, Veiculo atualizacao) {
		Veiculo veiculo = buscarPorId(veiculoId);

		if (atualizacao.getTipo() != null) {
			veiculo.setTipo(atualizacao.getTipo());
		}
		if (textoValido(atualizacao.getModelo())) {
			veiculo.setModelo(atualizacao.getModelo());
		}
		if (textoValido(atualizacao.getPlaca())) {
			veiculo.setPlaca(atualizacao.getPlaca());
		}
		if (atualizacao.getProprietario() != null) {
			veiculo.setProprietario(atualizacao.getProprietario());
			prepararProprietario(veiculo);
		}

		Veiculo salvo = veiculoRepositorio.save(veiculo);
		vincularAoProprietario(salvo);
		return salvo;
	}

	@Transactional
	public void excluir(Long veiculoId) {
		Veiculo veiculo = buscarPorId(veiculoId);
		desvincularVendas(veiculo);
		desvincularProprietario(veiculo);
		veiculo.setProprietario(null);
		veiculo.setVendas(new HashSet<>());
		veiculoRepositorio.save(veiculo);
		veiculoRepositorio.delete(veiculo);
	}

	private void desvincularProprietario(Veiculo veiculo) {
		if (veiculo.getProprietario() != null && veiculo.getProprietario().getId() != null) {
			Usuario proprietario = usuarioRepositorio.findById(veiculo.getProprietario().getId()).orElse(null);

			if (proprietario != null && proprietario.getVeiculos() != null) {
				proprietario.getVeiculos()
						.removeIf(vinculado -> vinculado.getId() != null && vinculado.getId().equals(veiculo.getId()));
				usuarioRepositorio.save(proprietario);
			}
		}
	}

	private void desvincularVendas(Veiculo veiculo) {
		List<Venda> vendas = vendaRepositorio.findAll();
		vendas.forEach(venda -> {
			if (venda.getVeiculo() != null && venda.getVeiculo().getId() != null
					&& venda.getVeiculo().getId().equals(veiculo.getId())) {
				venda.setVeiculo(null);
				vendaRepositorio.save(venda);
			}
		});
	}

	private void prepararProprietario(Veiculo veiculo) {
		if (veiculo.getProprietario() != null && veiculo.getProprietario().getId() != null) {
			Usuario proprietario = usuarioRepositorio.findById(veiculo.getProprietario().getId())
					.orElseThrow(() -> new VerificarNuloExecao(
							"Usuario com id " + veiculo.getProprietario().getId() + " nao foi encontrado."));
			veiculo.setProprietario(proprietario);
		}
	}

	private void vincularAoProprietario(Veiculo veiculo) {
		if (veiculo.getProprietario() != null) {
			Usuario proprietario = veiculo.getProprietario();
			if (proprietario.getVeiculos() == null) {
				proprietario.setVeiculos(new HashSet<>());
			}
			proprietario.getVeiculos().add(veiculo);
			usuarioRepositorio.save(proprietario);
		}
	}

	private void garantirColecoes(Veiculo veiculo) {
		if (veiculo.getVendas() == null) {
			veiculo.setVendas(new HashSet<>());
		}
	}

	private boolean textoValido(String valor) {
		return valor != null && !valor.trim().isEmpty();
	}
}
