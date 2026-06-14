package com.autobots.automanager.controles;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.autobots.automanager.entidades.Endereco;
import com.autobots.automanager.servicos.ClienteServico;

@RestController
@RequestMapping("/clientes/{clienteId}/endereco")
public class EnderecoControle {

    private final ClienteServico clienteServico;

    public EnderecoControle(ClienteServico clienteServico) {
        this.clienteServico = clienteServico;
    }

    @GetMapping
    public ResponseEntity<EntityModel<Endereco>> buscarEndereco(@PathVariable Long clienteId) {
        Endereco Procurado = clienteServico.buscarEndereco(clienteId);
        return ResponseEntity.ok(criarModelo(clienteId, Procurado));
    }

    @DeleteMapping
    public ResponseEntity<Void> deletarEndereco(@PathVariable Long clienteId) {
        clienteServico.excluirEndereco(clienteId);
        ResponseEntity<Void> Resposta = ResponseEntity.noContent().build();
        return Resposta;
    }

    @PostMapping
    public ResponseEntity<EntityModel<Endereco>> cadastrarEndereco(
            @PathVariable Long clienteId,
            @RequestBody Endereco endereco) {

        Endereco Associado = clienteServico.cadastrarEndereco(clienteId, endereco);
        return ResponseEntity.status(HttpStatus.CREATED).body(criarModelo(clienteId, Associado));
    }

    @PutMapping
    public ResponseEntity<EntityModel<Endereco>> atualizarEndereco(
            @PathVariable Long clienteId,
            @RequestBody Endereco atualizacao) {

        Endereco Informado = atualizacao;
        Endereco Atualizado = clienteServico.atualizarEndereco(clienteId, Informado);
        return ResponseEntity.ok(criarModelo(clienteId, Atualizado));
    }

    private EntityModel<Endereco> criarModelo(Long clienteId, Endereco endereco) {
        return EntityModel.of(
                endereco,
                linkTo(methodOn(EnderecoControle.class).buscarEndereco(clienteId)).withSelfRel(),
                linkTo(methodOn(ClienteControle.class).buscarClientePorId(clienteId)).withRel("cliente"));
    }
}