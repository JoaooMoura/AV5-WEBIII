package com.autobots.apifuncionarios.seguranca;

import java.nio.charset.StandardCharsets;
import java.security.Key;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class ProvedorJwt {
	@Value("${jwt.secret}")
	private String assinatura;

	public String obterNomeUsuario(String autorizacao) {
		String jwt = obterJwt(autorizacao);

		try {
			Key chave = Keys.hmacShaKeyFor(assinatura.getBytes(StandardCharsets.UTF_8));
			Claims reivindicacoes = Jwts.parserBuilder().setSigningKey(chave).build().parseClaimsJws(jwt).getBody();
			return reivindicacoes.getSubject();
		} catch (Exception exception) {
			return null;
		}
	}

	private String obterJwt(String autorizacao) {
		if (autorizacao == null || !autorizacao.startsWith("Bearer ")) {
			return "";
		}
		return autorizacao.substring(7);
	}
}
