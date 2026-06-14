package com.autobots.automanager.jwt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;

@Component
public class ProvedorJwt {
	@Value("${jwt.secret}")
	private String assinatura;
	@Value("${jwt.expiration}")
	private Long duracao;

	public String proverJwt(String nomeUsuario) {
		GeradorJwt gerador = new GeradorJwt(assinatura, duracao);
		return gerador.gerarJwt(nomeUsuario);
	}

	public boolean validarJwt(String jwt) {
		AnalisadorJwt analisador = new AnalisadorJwt(assinatura, jwt);
		ValidadorJwt validador = new ValidadorJwt();
		return validador.validar(analisador.obterReivindicacoes());
	}

	public String obterNomeUsuario(String jwt) {
		AnalisadorJwt analisador = new AnalisadorJwt(assinatura, jwt);
		Claims reivindicacoes = analisador.obterReivindicacoes();
		return analisador.obterNomeUsuario(reivindicacoes);
	}
}
