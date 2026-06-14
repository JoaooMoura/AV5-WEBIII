package com.autobots.gateway.servicos;

import java.nio.charset.StandardCharsets;
import java.security.Key;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class GatewayJwtServico {
	@Value("${jwt.secret}")
	private String assinatura;

	public boolean validar(String autorizacao) {
		if (autorizacao == null || !autorizacao.startsWith("Bearer ")) {
			return false;
		}

		try {
			Key chave = Keys.hmacShaKeyFor(assinatura.getBytes(StandardCharsets.UTF_8));
			Jwts.parserBuilder().setSigningKey(chave).build().parseClaimsJws(autorizacao.substring(7));
			return true;
		} catch (Exception exception) {
			return false;
		}
	}
}
