package com.autobots.apiveiculos.servicos;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.autobots.apiveiculos.dto.ItemCatalogoAv5Dto;
import com.autobots.apiveiculos.dto.UsuarioAv5Dto;
import com.autobots.apiveiculos.dto.UsuarioResumoAv5Dto;
import com.autobots.apiveiculos.dto.VeiculoResumoAv5Dto;
import com.autobots.apiveiculos.dto.VendaAv5Dto;
import com.autobots.apiveiculos.entidades.Mercadoria;
import com.autobots.apiveiculos.entidades.Servico;
import com.autobots.apiveiculos.entidades.Usuario;
import com.autobots.apiveiculos.entidades.Veiculo;
import com.autobots.apiveiculos.entidades.Venda;

@Component
public class MapeadorAv5 {
	public UsuarioAv5Dto criarUsuario(Usuario usuario) {
		return new UsuarioAv5Dto(
				usuario.getId(),
				usuario.getNome(),
				usuario.getNomeSocial(),
				copia(usuario.getPerfis()),
				copia(usuario.getDocumentos()),
				copia(usuario.getTelefones()),
				usuario.getEndereco(),
				copia(usuario.getEmails()));
	}

	public ItemCatalogoAv5Dto criarItemServico(Servico servico) {
		return new ItemCatalogoAv5Dto(
				servico.getId(),
				"SERVICO",
				servico.getCadastro(),
				servico.getNome(),
				servico.getDescricao(),
				servico.getValor(),
				null);
	}

	public ItemCatalogoAv5Dto criarItemMercadoria(Mercadoria mercadoria) {
		return new ItemCatalogoAv5Dto(
				mercadoria.getId(),
				"MERCADORIA",
				mercadoria.getCadastro(),
				mercadoria.getNome(),
				mercadoria.getDescricao(),
				mercadoria.getValor(),
				mercadoria.getQuantidade());
	}

	public VeiculoResumoAv5Dto criarVeiculo(Veiculo veiculo) {
		if (veiculo == null) {
			return null;
		}
		return new VeiculoResumoAv5Dto(
				veiculo.getId(),
				veiculo.getTipo(),
				veiculo.getModelo(),
				veiculo.getPlaca(),
				criarUsuarioResumo(veiculo.getProprietario()));
	}

	public VendaAv5Dto criarVenda(Venda venda) {
		List<ItemCatalogoAv5Dto> servicos = venda.getServicos() == null
				? List.of()
				: venda.getServicos().stream().map(this::criarItemServico).toList();
		List<ItemCatalogoAv5Dto> mercadorias = venda.getMercadorias() == null
				? List.of()
				: venda.getMercadorias().stream().map(this::criarItemMercadoria).toList();
		double valorTotal = servicos.stream().mapToDouble(ItemCatalogoAv5Dto::getValor).sum()
				+ mercadorias.stream().mapToDouble(ItemCatalogoAv5Dto::getValor).sum();
		return new VendaAv5Dto(
				venda.getId(),
				venda.getIdentificacao(),
				venda.getCadastro(),
				criarUsuarioResumo(venda.getCliente()),
				criarUsuarioResumo(venda.getFuncionario()),
				criarVeiculo(venda.getVeiculo()),
				servicos,
				mercadorias,
				valorTotal);
	}

	public List<ItemCatalogoAv5Dto> criarCatalogo(Set<Servico> servicos, Set<Mercadoria> mercadorias) {
		List<ItemCatalogoAv5Dto> itens = new ArrayList<>();
		servicos.stream().map(this::criarItemServico).forEach(itens::add);
		mercadorias.stream().map(this::criarItemMercadoria).forEach(itens::add);
		return itens;
	}

	private UsuarioResumoAv5Dto criarUsuarioResumo(Usuario usuario) {
		if (usuario == null) {
			return null;
		}
		return new UsuarioResumoAv5Dto(usuario.getId(), usuario.getNome(), usuario.getNomeSocial());
	}

	private <T> Set<T> copia(Set<T> valores) {
		return valores == null ? Set.of() : new HashSet<>(valores);
	}
}
