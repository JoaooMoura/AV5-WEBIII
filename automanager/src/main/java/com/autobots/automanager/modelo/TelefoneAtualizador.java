package com.autobots.automanager.modelo;

import java.util.List;

import org.springframework.stereotype.Component;

import com.autobots.automanager.entidades.Telefone;

@Component
public class TelefoneAtualizador {
	private final StringVerificadorNulo verificador;

	public TelefoneAtualizador(StringVerificadorNulo verificador) {
		this.verificador = verificador;
	}

	public void atualizar(Telefone telefone, Telefone atualizacao) {
		if (atualizacao != null) {
			if (!verificador.verificar(atualizacao.getDdd())) {
				telefone.setDdd(atualizacao.getDdd());
			}
			if (!verificador.verificar(atualizacao.getNumero())) {
				telefone.setNumero(atualizacao.getNumero());
			}
		}
	}

	public void atualizar(List<Telefone> telefones, List<Telefone> atualizacoes) {
		if (telefones == null || atualizacoes == null) {
			return;
		}

		for (Telefone atualizacao : atualizacoes) {
			if (atualizacao == null || atualizacao.getId() == null) {
				continue;
			}
			for (Telefone telefone : telefones) {
				if (atualizacao.getId().equals(telefone.getId())) {
					atualizar(telefone, atualizacao);
				}
			}
		}
	}
}
