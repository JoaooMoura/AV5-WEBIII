package com.autobots.gateway.controles;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.autobots.gateway.servicos.GatewayServico;

@RestController
public class LegadoControle {
	private final GatewayServico gatewayServico;

	public LegadoControle(GatewayServico gatewayServico) {
		this.gatewayServico = gatewayServico;
	}

	@RequestMapping(
			value = { "/clientes/**", "/usuarios/**", "/vendas/**", "/servicos/**", "/mercadorias/**",
					"/empresas/**", "/veiculos/**" },
			method = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE })
	public ResponseEntity<String> encaminhar(
			HttpServletRequest request,
			@RequestBody(required = false) String corpo,
			@RequestHeader(value = "Authorization", required = false) String autorizacao) {
		String caminho = request.getRequestURI();

		if (request.getQueryString() != null) {
			caminho += "?" + request.getQueryString();
		}

		return gatewayServico.encaminharLegado(caminho, HttpMethod.valueOf(request.getMethod()), corpo, autorizacao);
	}
}
