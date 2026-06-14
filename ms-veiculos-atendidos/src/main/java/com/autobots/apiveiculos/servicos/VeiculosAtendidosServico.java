package com.autobots.apiveiculos.servicos;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.autobots.apiveiculos.dto.VeiculoAtendidoAv5Dto;
import com.autobots.apiveiculos.entidades.Empresa;
import com.autobots.apiveiculos.entidades.Veiculo;
import com.autobots.apiveiculos.entidades.Venda;
import com.autobots.apiveiculos.servicos.ConsultaEmpresaServico;
import com.autobots.apiveiculos.servicos.MapeadorAv5;

@Service
public class VeiculosAtendidosServico {
	private final ConsultaEmpresaServico consultaEmpresaServico;
	private final MapeadorAv5 mapeador;

	public VeiculosAtendidosServico(ConsultaEmpresaServico consultaEmpresaServico, MapeadorAv5 mapeador) {
		this.consultaEmpresaServico = consultaEmpresaServico;
		this.mapeador = mapeador;
	}

	@Transactional(readOnly = true)
	public List<VeiculoAtendidoAv5Dto> listar(Long empresaId, String autorizacao) {
		Empresa empresa = consultaEmpresaServico.buscar(empresaId, autorizacao);
		List<Venda> vendas = empresa.getVendas().stream()
				.filter(venda -> venda.getVeiculo() != null && venda.getVeiculo().getId() != null)
				.toList();
		Map<Long, Veiculo> veiculos = new LinkedHashMap<>();
		vendas.forEach(venda -> veiculos.putIfAbsent(venda.getVeiculo().getId(), venda.getVeiculo()));
		return veiculos.values().stream()
				.map(veiculo -> new VeiculoAtendidoAv5Dto(
						mapeador.criarVeiculo(veiculo),
						vendas.stream()
								.filter(venda -> venda.getVeiculo().getId().equals(veiculo.getId()))
								.map(mapeador::criarVenda)
								.toList()))
				.toList();
	}
}
