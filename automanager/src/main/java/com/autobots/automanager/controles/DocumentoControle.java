package com.autobots.automanager.controles;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.autobots.automanager.entidades.Documento;
import com.autobots.automanager.servicos.ClienteServico;

@RestController
@RequestMapping("/clientes/{clienteId}/documentos")
public class DocumentoControle {

    private final ClienteServico clienteServico;

    public DocumentoControle(ClienteServico clienteServico) {
        this.clienteServico = clienteServico;
    }

    @GetMapping("/{documentoId}")
    public ResponseEntity<EntityModel<Documento>> buscarDocumentoPorId(
            @PathVariable Long clienteId,
            @PathVariable Long documentoId) {

        Documento Filtrado = clienteServico.buscarDocumento(clienteId, documentoId);
        return ResponseEntity.ok(criarModelo(clienteId, Filtrado));
    }

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<Documento>>> listarDocumentos(@PathVariable Long clienteId) {
        List<Documento> Obtidos = clienteServico.listarDocumentos(clienteId);

        List<EntityModel<Documento>> documentos = Obtidos.stream()
                .map(documento -> criarModelo(clienteId, documento))
                .toList();

        CollectionModel<EntityModel<Documento>> colecao = CollectionModel.of(
                documentos,
                linkTo(methodOn(DocumentoControle.class).listarDocumentos(clienteId)).withSelfRel(),
                linkTo(methodOn(ClienteControle.class).buscarClientePorId(clienteId)).withRel("cliente"));

        return ResponseEntity.ok(colecao);
    }

    @PostMapping
    public ResponseEntity<EntityModel<Documento>> cadastrarDocumento(
            @PathVariable Long clienteId,
            @RequestBody Documento documento) {

        Documento Incluido = clienteServico.cadastrarDocumento(clienteId, documento);
        return ResponseEntity.status(HttpStatus.CREATED).body(criarModelo(clienteId, Incluido));
    }

    @PutMapping("/{documentoId}")
    public ResponseEntity<EntityModel<Documento>> atualizarDocumento(
            @PathVariable Long clienteId,
            @PathVariable Long documentoId,
            @RequestBody Documento atualizacao) {

        Documento documentoAtualizado = clienteServico.atualizarDocumento(clienteId, documentoId, atualizacao);
        return ResponseEntity.ok(criarModelo(clienteId, documentoAtualizado));
    }

    @DeleteMapping("/{documentoId}")
    public ResponseEntity<Void> deletarDocumento(@PathVariable Long clienteId, @PathVariable Long documentoId) {
        clienteServico.excluirDocumento(clienteId, documentoId);
        return ResponseEntity.noContent().build();
    }

    private EntityModel<Documento> criarModelo(Long clienteId, Documento documento) {
        return EntityModel.of(
                documento,
                linkTo(methodOn(DocumentoControle.class).buscarDocumentoPorId(clienteId, documento.getId())).withSelfRel(),
                linkTo(methodOn(DocumentoControle.class).listarDocumentos(clienteId)).withRel("documentos"),
                linkTo(methodOn(ClienteControle.class).buscarClientePorId(clienteId)).withRel("cliente"));
    }
}