package com.autobots.automanager.jwt;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

class GeradorJwt {
	private final String assinatura;
	private final Date expiracao;

	public GeradorJwt(String assinatura, long duracao) {
		this.assinatura = assinatura;
		this.expiracao = new Date(System.currentTimeMillis() + duracao);
	}

	public String gerarJwt(String nomeUsuario) {
		Key chave = Keys.hmacShaKeyFor(assinatura.getBytes(StandardCharsets.UTF_8));
		return Jwts.builder()
				.setSubject(nomeUsuario)
				.setExpiration(expiracao)
				.signWith(chave, SignatureAlgorithm.HS512)
				.compact();
	}
}
