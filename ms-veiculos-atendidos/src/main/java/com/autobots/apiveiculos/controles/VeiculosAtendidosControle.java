package com.autobots.apiveiculos.controles;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.autobots.apiveiculos.servicos.VeiculosAtendidosServico;
import com.autobots.apiveiculos.dto.VeiculoAtendidoAv5Dto;

@RestController
@RequestMapping("/interno/empresas")
public class VeiculosAtendidosControle {
	private final VeiculosAtendidosServico veiculosAtendidosServico;

	public VeiculosAtendidosControle(VeiculosAtendidosServico veiculosAtendidosServico) {
		this.veiculosAtendidosServico = veiculosAtendidosServico;
	}

	@GetMapping("/{empresaId}/veiculos-atendidos")
	public List<VeiculoAtendidoAv5Dto> listarVeiculosAtendidos(
			@PathVariable Long empresaId,
			@RequestHeader(value = "Authorization", required = false) String autorizacao) {
		return veiculosAtendidosServico.listar(empresaId, autorizacao);
	}
}
