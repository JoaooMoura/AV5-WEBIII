package com.autobots.automanager.modelo;

import org.springframework.stereotype.Component;

import com.autobots.automanager.entidades.Cliente;
import com.autobots.automanager.entidades.Endereco;

@Component
public class ClienteAtualizador {
	private final StringVerificadorNulo verificador;
	private final EnderecoAtualizador enderecoAtualizador;
	private final DocumentoAtualizador documentoAtualizador;
	private final TelefoneAtualizador telefoneAtualizador;

	public ClienteAtualizador(
			StringVerificadorNulo verificador,
			EnderecoAtualizador enderecoAtualizador,
			DocumentoAtualizador documentoAtualizador,
			TelefoneAtualizador telefoneAtualizador) {
		this.verificador = verificador;
		this.enderecoAtualizador = enderecoAtualizador;
		this.documentoAtualizador = documentoAtualizador;
		this.telefoneAtualizador = telefoneAtualizador;
	}

	private void atualizarDados(Cliente cliente, Cliente atualizacao) {
		if (!verificador.verificar(atualizacao.getNome())) {
			cliente.setNome(atualizacao.getNome());
		}
		if (!verificador.verificar(atualizacao.getNomeSocial())) {
			cliente.setNomeSocial(atualizacao.getNomeSocial());
		}
		if (atualizacao.getDataCadastro() != null) {
			cliente.setDataCadastro(atualizacao.getDataCadastro());
		}
		if (atualizacao.getDataNascimento() != null) {
			cliente.setDataNascimento(atualizacao.getDataNascimento());
		}
	}

	public void atualizar(Cliente cliente, Cliente atualizacao) {
		atualizarDados(cliente, atualizacao);

		if (atualizacao.getEndereco() != null) {
			if (cliente.getEndereco() == null) {
				cliente.setEndereco(new Endereco());
			}
			enderecoAtualizador.atualizar(cliente.getEndereco(), atualizacao.getEndereco());
		}

		if (atualizacao.getDocumentos() != null) {
			documentoAtualizador.atualizar(cliente.getDocumentos(), atualizacao.getDocumentos());
		}

		if (atualizacao.getTelefones() != null) {
			telefoneAtualizador.atualizar(cliente.getTelefones(), atualizacao.getTelefones());
		}
	}
}
