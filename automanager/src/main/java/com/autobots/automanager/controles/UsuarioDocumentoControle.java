package com.autobots.automanager.controles;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.autobots.automanager.entidades.Documento;
import com.autobots.automanager.servicos.UsuarioServico;

@RestController
@RequestMapping("/usuarios/{usuarioId}/documentos")
public class UsuarioDocumentoControle {
	private final UsuarioServico usuarioServico;

	public UsuarioDocumentoControle(UsuarioServico usuarioServico) {
		this.usuarioServico = usuarioServico;
	}

	@GetMapping
	@PreAuthorize("@autorizacaoServico.podeLerUsuario(authentication, #usuarioId)")
	public ResponseEntity<CollectionModel<EntityModel<Documento>>> listarDocumentos(@PathVariable Long usuarioId) {
		List<EntityModel<Documento>> documentos = usuarioServico.listarDocumentos(usuarioId).stream()
				.map(documento -> criarModelo(usuarioId, documento))
				.toList();

		CollectionModel<EntityModel<Documento>> colecao = CollectionModel.of(
				documentos,
				linkTo(methodOn(UsuarioDocumentoControle.class).listarDocumentos(usuarioId)).withSelfRel(),
				linkTo(methodOn(UsuarioControle.class).buscarUsuarioPorId(usuarioId)).withRel("usuario"),
				linkTo(methodOn(UsuarioDocumentoControle.class).cadastrarDocumento(usuarioId, null)).withRel("cadastrar"));

		return ResponseEntity.ok(colecao);
	}

	@GetMapping("/{documentoId}")
	@PreAuthorize("@autorizacaoServico.podeLerUsuario(authentication, #usuarioId)")
	public ResponseEntity<EntityModel<Documento>> buscarDocumentoPorId(@PathVariable Long usuarioId, @PathVariable Long documentoId) {
		Documento documento = usuarioServico.buscarDocumento(usuarioId, documentoId);
		return ResponseEntity.ok(criarModelo(usuarioId, documento));
	}

	@PostMapping
	@PreAuthorize("@autorizacaoServico.podeGerenciarUsuario(authentication, #usuarioId)")
	public ResponseEntity<EntityModel<Documento>> cadastrarDocumento(@PathVariable Long usuarioId, @RequestBody Documento documento) {
		Documento cadastrado = usuarioServico.cadastrarDocumento(usuarioId, documento);
		return ResponseEntity.status(HttpStatus.CREATED).body(criarModelo(usuarioId, cadastrado));
	}

	@PutMapping("/{documentoId}")
	@PreAuthorize("@autorizacaoServico.podeGerenciarUsuario(authentication, #usuarioId)")
	public ResponseEntity<EntityModel<Documento>> atualizarDocumento(
			@PathVariable Long usuarioId,
			@PathVariable Long documentoId,
			@RequestBody Documento atualizacao) {
		Documento atualizado = usuarioServico.atualizarDocumento(usuarioId, documentoId, atualizacao);
		return ResponseEntity.ok(criarModelo(usuarioId, atualizado));
	}

	@DeleteMapping("/{documentoId}")
	@PreAuthorize("@autorizacaoServico.podeGerenciarUsuario(authentication, #usuarioId)")
	public ResponseEntity<Void> deletarDocumento(@PathVariable Long usuarioId, @PathVariable Long documentoId) {
		usuarioServico.excluirDocumento(usuarioId, documentoId);
		return ResponseEntity.noContent().build();
	}

	private EntityModel<Documento> criarModelo(Long usuarioId, Documento documento) {
		return EntityModel.of(
				documento,
				linkTo(methodOn(UsuarioDocumentoControle.class).buscarDocumentoPorId(usuarioId, documento.getId())).withSelfRel(),
				linkTo(methodOn(UsuarioDocumentoControle.class).listarDocumentos(usuarioId)).withRel("documentos"),
				linkTo(methodOn(UsuarioControle.class).buscarUsuarioPorId(usuarioId)).withRel("usuario"),
				linkTo(methodOn(UsuarioDocumentoControle.class).atualizarDocumento(usuarioId, documento.getId(), null)).withRel("atualizar"),
				linkTo(methodOn(UsuarioDocumentoControle.class).deletarDocumento(usuarioId, documento.getId())).withRel("excluir"));
	}
}
