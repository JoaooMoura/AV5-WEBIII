package com.autobots.automanager.controles;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.autobots.automanager.entidades.Telefone;
import com.autobots.automanager.servicos.ClienteServico;

@RestController
@RequestMapping("/clientes/{clienteId}/telefones")
public class TelefoneControle {

    private final ClienteServico clienteServico;

    public TelefoneControle(ClienteServico clienteServico) {
        this.clienteServico = clienteServico;
    }

    @GetMapping("/{telefoneId}")
    public ResponseEntity<EntityModel<Telefone>> buscarTelefonePorId(
            @PathVariable Long clienteId,
            @PathVariable Long telefoneId) {

        Telefone Procurado = clienteServico.buscarTelefone(clienteId, telefoneId);
        return ResponseEntity.ok(criarModelo(clienteId, Procurado));
    }

    @PutMapping("/{telefoneId}")
    public ResponseEntity<EntityModel<Telefone>> atualizarTelefone(
            @PathVariable Long clienteId,
            @PathVariable Long telefoneId,
            @RequestBody Telefone atualizacao) {

        Telefone Atualizado = clienteServico.atualizarTelefone(clienteId, telefoneId, atualizacao);
        return ResponseEntity.ok(criarModelo(clienteId, Atualizado));
    }

    @DeleteMapping("/{telefoneId}")
    public ResponseEntity<Void> deletarTelefone(@PathVariable Long clienteId, @PathVariable Long telefoneId) {
        clienteServico.excluirTelefone(clienteId, telefoneId);
        ResponseEntity<Void> Resposta = ResponseEntity.noContent().build();
        return Resposta;
    }

    @PostMapping
    public ResponseEntity<EntityModel<Telefone>> cadastrarTelefone(
            @PathVariable Long clienteId,
            @RequestBody Telefone telefone) {

        Telefone Adicionado = clienteServico.cadastrarTelefone(clienteId, telefone);
        return ResponseEntity.status(HttpStatus.CREATED).body(criarModelo(clienteId, Adicionado));
    }

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<Telefone>>> listarTelefones(@PathVariable Long clienteId) {
        List<Telefone> telefones = clienteServico.listarTelefones(clienteId);

        List<EntityModel<Telefone>> modelos = telefones.stream()
                .map(telefone -> criarModelo(clienteId, telefone))
                .toList();

        CollectionModel<EntityModel<Telefone>> colecao = CollectionModel.of(
                modelos,
                linkTo(methodOn(TelefoneControle.class).listarTelefones(clienteId)).withSelfRel(),
                linkTo(methodOn(ClienteControle.class).buscarClientePorId(clienteId)).withRel("cliente"));

        return ResponseEntity.ok(colecao);
    }

    private EntityModel<Telefone> criarModelo(Long clienteId, Telefone telefone) {
        return EntityModel.of(
                telefone,
                linkTo(methodOn(TelefoneControle.class).buscarTelefonePorId(clienteId, telefone.getId())).withSelfRel(),
                linkTo(methodOn(TelefoneControle.class).listarTelefones(clienteId)).withRel("telefones"),
                linkTo(methodOn(ClienteControle.class).buscarClientePorId(clienteId)).withRel("cliente"));
    }
}