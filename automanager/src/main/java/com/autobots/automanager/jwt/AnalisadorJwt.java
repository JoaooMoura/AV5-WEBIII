package com.autobots.automanager.jwt;

import java.nio.charset.StandardCharsets;
import java.security.Key;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

class AnalisadorJwt {
	private final String assinatura;
	private final String jwt;

	public AnalisadorJwt(String assinatura, String jwt) {
		this.assinatura = assinatura;
		this.jwt = jwt;
	}

	public Claims obterReivindicacoes() {
		try {
			Key chave = Keys.hmacShaKeyFor(assinatura.getBytes(StandardCharsets.UTF_8));
			return Jwts.parserBuilder().setSigningKey(chave).build().parseClaimsJws(jwt).getBody();
		} catch (Exception e) {
			return null;
		}
	}

	public String obterNomeUsuario(Claims reivindicacoes) {
		if (reivindicacoes != null) {
			return reivindicacoes.getSubject();
		}
		return null;
	}
}
