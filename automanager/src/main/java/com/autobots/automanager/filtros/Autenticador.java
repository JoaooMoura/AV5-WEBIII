package com.autobots.automanager.filtros;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.autobots.automanager.jwt.ProvedorJwt;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Autenticador extends UsernamePasswordAuthenticationFilter {
	private final AuthenticationManager gerenciadorAutenticacao;
	private final ProvedorJwt provedorJwt;

	public Autenticador(AuthenticationManager gerenciadorAutenticacao, ProvedorJwt provedorJwt) {
		this.gerenciadorAutenticacao = gerenciadorAutenticacao;
		this.provedorJwt = provedorJwt;
		setFilterProcessesUrl("/auth/login");
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {
		Map<String, String> dados = lerDados(request);
		String nomeUsuario = obterValor(dados, "nomeUsuario", "login", "usuario");
		String senha = obterValor(dados, "senha", "password");
		UsernamePasswordAuthenticationToken autenticacao = new UsernamePasswordAuthenticationToken(nomeUsuario, senha);
		return gerenciadorAutenticacao.authenticate(autenticacao);
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication autenticacao) throws IOException, ServletException {
		UserDetails jwt = (UserDetails) autenticacao.getPrincipal();
		String web = jwt.getUsername();
		String token = provedorJwt.proverJwt(web);
		String prefixo = "Bearer ";
		String resposta = prefixo + token;
		ObjectMapper objectMapper = new ObjectMapper();
		String tokenResposta = token;
		String esquema = "Bearer";
		String gerado = resposta;
		String enviado = gerado;
		Map<String, String> conteudo = new HashMap<>();
		String header = "Authorization";
		String retorno = MediaType.APPLICATION_JSON_VALUE;
		String origem = tokenResposta;
		String nome = web;
		String output = "UTF-8";
		int status = HttpServletResponse.SC_OK;

		conteudo.put("token", origem);
		conteudo.put("tipo", esquema);
		conteudo.put("usuario", nome);
		response.addHeader(header, enviado);
		response.setContentType(retorno);
		response.setCharacterEncoding(output);
		response.setStatus(status);
		objectMapper.writeValue(response.getWriter(), conteudo);
	}

	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException failed) throws IOException, ServletException {
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.getWriter().write("{\"erro\":\"Credenciais invalidas\"}");
	}

	private Map<String, String> lerDados(HttpServletRequest request) {
		try {
			return new ObjectMapper().readValue(request.getInputStream(), new TypeReference<Map<String, String>>() {
			});
		} catch (IOException e) {
			return new HashMap<>();
		}
	}

	private String obterValor(Map<String, String> dados, String... chaves) {
		for (String chave : chaves) {
			String valor = dados.get(chave);

			if (valor != null) {
				return valor;
			}
		}
		return "";
	}
}
