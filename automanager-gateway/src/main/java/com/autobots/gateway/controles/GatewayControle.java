package com.autobots.gateway.controles;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.autobots.gateway.servicos.GatewayServico;

@RestController
@RequestMapping
public class GatewayControle {
	private final GatewayServico gatewayServico;

	public GatewayControle(GatewayServico gatewayServico) {
		this.gatewayServico = gatewayServico;
	}

	@PostMapping("/auth/login")
	public ResponseEntity<String> login(@RequestBody String corpo) {
		return gatewayServico.login(corpo);
	}

	@GetMapping("/api/empresas/{empresaId}/clientes")
	public ResponseEntity<String> listarClientes(
			@PathVariable Long empresaId,
			@RequestHeader(value = "Authorization", required = false) String autorizacao) {
		return gatewayServico.listarClientes(empresaId, autorizacao);
	}

	@GetMapping("/api/empresas/{empresaId}/funcionarios")
	public ResponseEntity<String> listarFuncionarios(
			@PathVariable Long empresaId,
			@RequestHeader(value = "Authorization", required = false) String autorizacao) {
		return gatewayServico.listarFuncionarios(empresaId, autorizacao);
	}

	@GetMapping("/api/empresas/{empresaId}/catalogo")
	public ResponseEntity<String> listarCatalogo(
			@PathVariable Long empresaId,
			@RequestHeader(value = "Authorization", required = false) String autorizacao) {
		return gatewayServico.listarCatalogo(empresaId, autorizacao);
	}

	@GetMapping("/api/empresas/{empresaId}/vendas/itens")
	public ResponseEntity<String> listarVendas(
			@PathVariable Long empresaId,
			@RequestParam(required = false) String inicio,
			@RequestParam(required = false) String fim,
			@RequestHeader(value = "Authorization", required = false) String autorizacao) {
		return gatewayServico.listarVendas(empresaId, inicio, fim, autorizacao);
	}

	@GetMapping("/api/empresas/{empresaId}/veiculos-atendidos")
	public ResponseEntity<String> listarVeiculosAtendidos(
			@PathVariable Long empresaId,
			@RequestHeader(value = "Authorization", required = false) String autorizacao) {
		return gatewayServico.listarVeiculosAtendidos(empresaId, autorizacao);
	}
}
