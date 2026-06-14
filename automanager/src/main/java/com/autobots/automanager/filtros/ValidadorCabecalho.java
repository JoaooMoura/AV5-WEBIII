package com.autobots.automanager.filtros;

class ValidadorCabecalho {
	private final String cabecalho;

	public ValidadorCabecalho(String cabecalho) {
		this.cabecalho = cabecalho;
	}

	public boolean validar() {
		return cabecalho != null && cabecalho.startsWith("Bearer ");
	}
}
