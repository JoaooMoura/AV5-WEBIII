package com.autobots.apivendas.seguranca;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class FiltroJwt extends OncePerRequestFilter {
	private final ProvedorJwt provedorJwt;

	public FiltroJwt(ProvedorJwt provedorJwt) {
		this.provedorJwt = provedorJwt;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		if (request.getRequestURI().startsWith("/interno/")
				&& provedorJwt.obterNomeUsuario(request.getHeader("Authorization")) == null) {
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
			return;
		}
		filterChain.doFilter(request, response);
	}
}
