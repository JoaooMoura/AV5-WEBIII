package com.autobots.gateway.servicos;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class GatewayServico {
	private final RestTemplate restTemplate = new RestTemplate();
	private final GatewayJwtServico gatewayJwtServico;

	@Value("${automanager.core.url}")
	private String coreUrl;
	@Value("${gateway.public.url}")
	private String gatewayPublicUrl;
	@Value("${api.clientes.url}")
	private String clientesUrl;
	@Value("${api.funcionarios.url}")
	private String funcionariosUrl;
	@Value("${api.catalogo.url}")
	private String catalogoUrl;
	@Value("${api.vendas.url}")
	private String vendasUrl;
	@Value("${api.veiculos.url}")
	private String veiculosUrl;

	public GatewayServico(GatewayJwtServico gatewayJwtServico) {
		this.gatewayJwtServico = gatewayJwtServico;
	}

	public ResponseEntity<String> login(String corpo) {
		return executar(coreUrl + "/auth/login", HttpMethod.POST, corpo, null);
	}

	public ResponseEntity<String> listarClientes(Long empresaId, String autorizacao) {
		return encaminharProtegido(clientesUrl + "/interno/empresas/" + empresaId + "/clientes", autorizacao);
	}

	public ResponseEntity<String> listarFuncionarios(Long empresaId, String autorizacao) {
		return encaminharProtegido(funcionariosUrl + "/interno/empresas/" + empresaId + "/funcionarios", autorizacao);
	}

	public ResponseEntity<String> listarCatalogo(Long empresaId, String autorizacao) {
		return encaminharProtegido(catalogoUrl + "/interno/empresas/" + empresaId + "/catalogo", autorizacao);
	}

	public ResponseEntity<String> listarVendas(Long empresaId, String inicio, String fim, String autorizacao) {
		UriComponentsBuilder url = UriComponentsBuilder
				.fromHttpUrl(vendasUrl + "/interno/empresas/" + empresaId + "/vendas/itens");

		if (inicio != null) {
			url.queryParam("inicio", inicio);
		}
		if (fim != null) {
			url.queryParam("fim", fim);
		}

		return encaminharProtegido(url.toUriString(), autorizacao);
	}

	public ResponseEntity<String> listarVeiculosAtendidos(Long empresaId, String autorizacao) {
		return encaminharProtegido(veiculosUrl + "/interno/empresas/" + empresaId + "/veiculos-atendidos", autorizacao);
	}

	public ResponseEntity<String> encaminharLegado(String caminho, HttpMethod metodo, String corpo, String autorizacao) {
		ResponseEntity<String> resposta = executar(coreUrl + caminho, metodo, corpo, autorizacao);
		String corpoResposta = resposta.getBody();

		if (corpoResposta != null) {
			corpoResposta = corpoResposta.replace(coreUrl, gatewayPublicUrl);
		}

		return resposta(resposta.getStatusCodeValue(), corpoResposta);
	}

	private ResponseEntity<String> encaminharProtegido(String destino, String autorizacao) {
		if (!gatewayJwtServico.validar(autorizacao)) {
			return resposta(401, "{\"erro\":\"Token JWT ausente ou invalido\"}");
		}
		return executar(destino, HttpMethod.GET, null, autorizacao);
	}

	private ResponseEntity<String> executar(String url, HttpMethod metodo, String corpo, String autorizacao) {
		HttpHeaders cabecalhos = new HttpHeaders();

		if (autorizacao != null && !autorizacao.isBlank()) {
			cabecalhos.set("Authorization", autorizacao);
		}
		if (corpo != null) {
			cabecalhos.setContentType(MediaType.APPLICATION_JSON);
		}

		try {
			ResponseEntity<String> resposta = restTemplate.exchange(
					url,
					metodo,
					new HttpEntity<>(corpo, cabecalhos),
					String.class);
			return resposta(resposta.getStatusCodeValue(), resposta.getBody());
		} catch (HttpStatusCodeException exception) {
			return resposta(exception.getRawStatusCode(), exception.getResponseBodyAsString());
		} catch (ResourceAccessException exception) {
			return resposta(503, "{\"erro\":\"Servico interno indisponivel\"}");
		}
	}

	private ResponseEntity<String> resposta(int status, String corpo) {
		return ResponseEntity.status(status)
				.contentType(MediaType.APPLICATION_JSON)
				.body(corpo);
	}
}
