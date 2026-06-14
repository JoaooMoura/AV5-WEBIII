package com.autobots.apifuncionarios.excecoes;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class TratadorExcecao {
	@ExceptionHandler(ResponseStatusException.class)
	public ResponseEntity<Void> tratar(ResponseStatusException excecao) {
		return ResponseEntity.status(excecao.getStatus()).build();
	}
}
