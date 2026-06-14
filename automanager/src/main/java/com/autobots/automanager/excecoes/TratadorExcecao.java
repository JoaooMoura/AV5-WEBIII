package com.autobots.automanager.excecoes;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class TratadorExcecao {

	@ExceptionHandler(VerificarNuloExecao.class)
	public ResponseEntity<Map<String, Object>> tratarRecursoNaoEncontrado(VerificarNuloExecao exception) {
		return construirResposta(HttpStatus.NOT_FOUND, exception.getMessage());
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<Map<String, Object>> tratarRequisicaoInvalida(IllegalArgumentException exception) {
		return construirResposta(HttpStatus.BAD_REQUEST, exception.getMessage());
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<Map<String, Object>> tratarParametroInvalido(MethodArgumentTypeMismatchException exception) {
		return construirResposta(HttpStatus.BAD_REQUEST, "Parametro " + exception.getName() + " possui valor invalido.");
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<Map<String, Object>> tratarConflitoDeDados(DataIntegrityViolationException exception) {
		return construirResposta(HttpStatus.CONFLICT, "Não foi possível concluir a operação por conflito de dados.");
	}

	@ExceptionHandler(AuthenticationException.class)
	public ResponseEntity<Map<String, Object>> tratarNaoAutenticado(AuthenticationException exception) {
		return construirResposta(HttpStatus.UNAUTHORIZED, "Autenticacao obrigatoria.");
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<Map<String, Object>> tratarAcessoNegado(AccessDeniedException exception) {
		return construirResposta(HttpStatus.FORBIDDEN, "Acesso negado.");
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Map<String, Object>> tratarErroGenerico(Exception exception) {
		return construirResposta(HttpStatus.INTERNAL_SERVER_ERROR, "Ocorreu um erro interno na aplicação.");
	}

	private ResponseEntity<Map<String, Object>> construirResposta(HttpStatus status, String mensagem) {
		Map<String, Object> corpo = new LinkedHashMap<>();
		corpo.put("timestamp", new Date());
		corpo.put("status", status.value());
		corpo.put("erro", status.getReasonPhrase());
		corpo.put("mensagem", mensagem);
		return ResponseEntity.status(status).body(corpo);
	}
}
