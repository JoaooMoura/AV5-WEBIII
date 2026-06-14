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

import com.autobots.automanager.entidades.Telefone;
import com.autobots.automanager.servicos.UsuarioServico;

@RestController
@RequestMapping("/usuarios/{usuarioId}/telefones")
public class UsuarioTelefoneControle {
	private final UsuarioServico usuarioServico;

	public UsuarioTelefoneControle(UsuarioServico usuarioServico) {
		this.usuarioServico = usuarioServico;
	}

	@GetMapping
	@PreAuthorize("@autorizacaoServico.podeLerUsuario(authentication, #usuarioId)")
	public ResponseEntity<CollectionModel<EntityModel<Telefone>>> listarTelefones(@PathVariable Long usuarioId) {
		List<EntityModel<Telefone>> telefones = usuarioServico.listarTelefones(usuarioId).stream()
				.map(telefone -> criarModelo(usuarioId, telefone))
				.toList();

		CollectionModel<EntityModel<Telefone>> colecao = CollectionModel.of(
				telefones,
				linkTo(methodOn(UsuarioTelefoneControle.class).listarTelefones(usuarioId)).withSelfRel(),
				linkTo(methodOn(UsuarioControle.class).buscarUsuarioPorId(usuarioId)).withRel("usuario"),
				linkTo(methodOn(UsuarioTelefoneControle.class).cadastrarTelefone(usuarioId, null)).withRel("cadastrar"));

		return ResponseEntity.ok(colecao);
	}

	@GetMapping("/{telefoneId}")
	@PreAuthorize("@autorizacaoServico.podeLerUsuario(authentication, #usuarioId)")
	public ResponseEntity<EntityModel<Telefone>> buscarTelefonePorId(@PathVariable Long usuarioId, @PathVariable Long telefoneId) {
		Telefone telefone = usuarioServico.buscarTelefone(usuarioId, telefoneId);
		return ResponseEntity.ok(criarModelo(usuarioId, telefone));
	}

	@PostMapping
	@PreAuthorize("@autorizacaoServico.podeGerenciarUsuario(authentication, #usuarioId)")
	public ResponseEntity<EntityModel<Telefone>> cadastrarTelefone(@PathVariable Long usuarioId, @RequestBody Telefone telefone) {
		Telefone cadastrado = usuarioServico.cadastrarTelefone(usuarioId, telefone);
		return ResponseEntity.status(HttpStatus.CREATED).body(criarModelo(usuarioId, cadastrado));
	}

	@PutMapping("/{telefoneId}")
	@PreAuthorize("@autorizacaoServico.podeGerenciarUsuario(authentication, #usuarioId)")
	public ResponseEntity<EntityModel<Telefone>> atualizarTelefone(
			@PathVariable Long usuarioId,
			@PathVariable Long telefoneId,
			@RequestBody Telefone atualizacao) {
		Telefone atualizado = usuarioServico.atualizarTelefone(usuarioId, telefoneId, atualizacao);
		return ResponseEntity.ok(criarModelo(usuarioId, atualizado));
	}

	@DeleteMapping("/{telefoneId}")
	@PreAuthorize("@autorizacaoServico.podeGerenciarUsuario(authentication, #usuarioId)")
	public ResponseEntity<Void> deletarTelefone(@PathVariable Long usuarioId, @PathVariable Long telefoneId) {
		usuarioServico.excluirTelefone(usuarioId, telefoneId);
		return ResponseEntity.noContent().build();
	}

	private EntityModel<Telefone> criarModelo(Long usuarioId, Telefone telefone) {
		return EntityModel.of(
				telefone,
				linkTo(methodOn(UsuarioTelefoneControle.class).buscarTelefonePorId(usuarioId, telefone.getId())).withSelfRel(),
				linkTo(methodOn(UsuarioTelefoneControle.class).listarTelefones(usuarioId)).withRel("telefones"),
				linkTo(methodOn(UsuarioControle.class).buscarUsuarioPorId(usuarioId)).withRel("usuario"),
				linkTo(methodOn(UsuarioTelefoneControle.class).atualizarTelefone(usuarioId, telefone.getId(), null)).withRel("atualizar"),
				linkTo(methodOn(UsuarioTelefoneControle.class).deletarTelefone(usuarioId, telefone.getId())).withRel("excluir"));
	}
}
