package com.autobots.automanager.modelo;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;

import org.springframework.stereotype.Component;

import com.autobots.automanager.controles.ClienteControle;
import com.autobots.automanager.controles.DocumentoControle;
import com.autobots.automanager.controles.EnderecoControle;
import com.autobots.automanager.controles.TelefoneControle;
import com.autobots.automanager.entidades.Cliente;

@Component
public class ClienteAdicionadorLink implements AdicionadorLink<Cliente> {

	@Override
	public void adicionarLink(List<Cliente> lista) {
		lista.forEach(this::adicionarLink);
	}

	@Override
	public void adicionarLink(Cliente cliente) {
		Long id = cliente.getId();

		cliente.add(linkTo(methodOn(ClienteControle.class).buscarClientePorId(id)).withSelfRel());
		cliente.add(linkTo(methodOn(ClienteControle.class).listarClientes()).withRel("clientes"));
		cliente.add(linkTo(methodOn(ClienteControle.class).atualizarCliente(id, null)).withRel("atualizar"));
		cliente.add(linkTo(methodOn(ClienteControle.class).deletarCliente(id)).withRel("excluir"));
		cliente.add(linkTo(methodOn(DocumentoControle.class).listarDocumentos(id)).withRel("documentos"));
		cliente.add(linkTo(methodOn(TelefoneControle.class).listarTelefones(id)).withRel("telefones"));
		cliente.add(linkTo(methodOn(EnderecoControle.class).buscarEndereco(id)).withRel("endereco"));
		cliente.add(linkTo(methodOn(DocumentoControle.class).cadastrarDocumento(id, null)).withRel("cadastrar-documento"));
		cliente.add(linkTo(methodOn(TelefoneControle.class).cadastrarTelefone(id, null)).withRel("cadastrar-telefone"));
		cliente.add(linkTo(methodOn(EnderecoControle.class).cadastrarEndereco(id, null)).withRel("cadastrar-endereco"));
	}
}