package com.autobots.automanager.modelo;

import java.util.List;

import org.springframework.stereotype.Component;

import com.autobots.automanager.entidades.Documento;

@Component
public class DocumentoAtualizador {
	private final StringVerificadorNulo verificador;

	public DocumentoAtualizador(StringVerificadorNulo verificador) {
		this.verificador = verificador;
	}

	public void atualizar(Documento documento, Documento atualizacao) {
		if (atualizacao != null) {
			if (!verificador.verificar(atualizacao.getTipo())) {
				documento.setTipo(atualizacao.getTipo());
			}
			if (!verificador.verificar(atualizacao.getNumero())) {
				documento.setNumero(atualizacao.getNumero());
			}
		}
	}

	public void atualizar(List<Documento> documentos, List<Documento> atualizacoes) {
		if (documentos == null || atualizacoes == null) {
			return;
		}

		for (Documento atualizacao : atualizacoes) {
			if (atualizacao == null || atualizacao.getId() == null) {
				continue;
			}
			for (Documento documento : documentos) {
				if (atualizacao.getId().equals(documento.getId())) {
					atualizar(documento, atualizacao);
				}
			}
		}
	}
}
