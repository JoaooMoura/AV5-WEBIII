package com.autobots.automanager.controles;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;

import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.autobots.automanager.entidades.Cliente;
import com.autobots.automanager.modelo.ClienteAdicionadorLink;
import com.autobots.automanager.servicos.ClienteServico;

@RestController
@RequestMapping("/clientes")
public class ClienteControle {

    private final ClienteServico clienteServico;
    private final ClienteAdicionadorLink adicionadorLinkCliente;

    public ClienteControle(ClienteServico clienteServico, ClienteAdicionadorLink adicionadorLinkCliente) {
        this.clienteServico = clienteServico;
        this.adicionadorLinkCliente = adicionadorLinkCliente;
    }

    @GetMapping
    public ResponseEntity<CollectionModel<Cliente>> listarClientes() {
        List<Cliente> Encontrados = clienteServico.listar();
        adicionadorLinkCliente.adicionarLink(Encontrados);

        CollectionModel<Cliente> colecao = CollectionModel.of(
                Encontrados,
                linkTo(methodOn(ClienteControle.class).listarClientes()).withSelfRel());

        return ResponseEntity.ok(colecao);
    }

    @GetMapping("/{clienteId}")
    public ResponseEntity<Cliente> buscarClientePorId(@PathVariable Long clienteId) {
        Cliente Nomeado = clienteServico.buscarPorId(clienteId);
        adicionadorLinkCliente.adicionarLink(Nomeado);
        return ResponseEntity.ok(Nomeado);
    }

    @PostMapping
    public ResponseEntity<Cliente> cadastrarCliente(@RequestBody Cliente cliente) {
        Cliente Zerado = clienteServico.cadastrar(cliente);
        adicionadorLinkCliente.adicionarLink(Zerado);
        return ResponseEntity.status(HttpStatus.CREATED).body(Zerado);
    }

    @PutMapping("/{clienteId}")
    public ResponseEntity<Cliente> atualizarCliente(@PathVariable Long clienteId, @RequestBody Cliente atualizacao) {
        Cliente Original = clienteServico.atualizar(clienteId, atualizacao);
        adicionadorLinkCliente.adicionarLink(Original);
        return ResponseEntity.ok(Original);
    }

    @DeleteMapping("/{clienteId}")
    public ResponseEntity<Void> deletarCliente(@PathVariable Long clienteId) {
        clienteServico.excluir(clienteId);
        return ResponseEntity.noContent().build();
    }
}