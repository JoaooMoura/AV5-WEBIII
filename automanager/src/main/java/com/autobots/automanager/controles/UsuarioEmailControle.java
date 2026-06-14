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

import com.autobots.automanager.entidades.Email;
import com.autobots.automanager.servicos.UsuarioServico;

@RestController
@RequestMapping("/usuarios/{usuarioId}/emails")
public class UsuarioEmailControle {
	private final UsuarioServico usuarioServico;

	public UsuarioEmailControle(UsuarioServico usuarioServico) {
		this.usuarioServico = usuarioServico;
	}

	@GetMapping
	@PreAuthorize("@autorizacaoServico.podeLerUsuario(authentication, #usuarioId)")
	public ResponseEntity<CollectionModel<EntityModel<Email>>> listarEmails(@PathVariable Long usuarioId) {
		List<EntityModel<Email>> emails = usuarioServico.listarEmails(usuarioId).stream()
				.map(email -> criarModelo(usuarioId, email))
				.toList();

		CollectionModel<EntityModel<Email>> colecao = CollectionModel.of(
				emails,
				linkTo(methodOn(UsuarioEmailControle.class).listarEmails(usuarioId)).withSelfRel(),
				linkTo(methodOn(UsuarioControle.class).buscarUsuarioPorId(usuarioId)).withRel("usuario"),
				linkTo(methodOn(UsuarioEmailControle.class).cadastrarEmail(usuarioId, null)).withRel("cadastrar"));

		return ResponseEntity.ok(colecao);
	}

	@GetMapping("/{emailId}")
	@PreAuthorize("@autorizacaoServico.podeLerUsuario(authentication, #usuarioId)")
	public ResponseEntity<EntityModel<Email>> buscarEmailPorId(@PathVariable Long usuarioId, @PathVariable Long emailId) {
		Email email = usuarioServico.buscarEmail(usuarioId, emailId);
		return ResponseEntity.ok(criarModelo(usuarioId, email));
	}

	@PostMapping
	@PreAuthorize("@autorizacaoServico.podeGerenciarUsuario(authentication, #usuarioId)")
	public ResponseEntity<EntityModel<Email>> cadastrarEmail(@PathVariable Long usuarioId, @RequestBody Email email) {
		Email cadastrado = usuarioServico.cadastrarEmail(usuarioId, email);
		return ResponseEntity.status(HttpStatus.CREATED).body(criarModelo(usuarioId, cadastrado));
	}

	@PutMapping("/{emailId}")
	@PreAuthorize("@autorizacaoServico.podeGerenciarUsuario(authentication, #usuarioId)")
	public ResponseEntity<EntityModel<Email>> atualizarEmail(
			@PathVariable Long usuarioId,
			@PathVariable Long emailId,
			@RequestBody Email atualizacao) {
		Email atualizado = usuarioServico.atualizarEmail(usuarioId, emailId, atualizacao);
		return ResponseEntity.ok(criarModelo(usuarioId, atualizado));
	}

	@DeleteMapping("/{emailId}")
	@PreAuthorize("@autorizacaoServico.podeGerenciarUsuario(authentication, #usuarioId)")
	public ResponseEntity<Void> deletarEmail(@PathVariable Long usuarioId, @PathVariable Long emailId) {
		usuarioServico.excluirEmail(usuarioId, emailId);
		return ResponseEntity.noContent().build();
	}

	private EntityModel<Email> criarModelo(Long usuarioId, Email email) {
		return EntityModel.of(
				email,
				linkTo(methodOn(UsuarioEmailControle.class).buscarEmailPorId(usuarioId, email.getId())).withSelfRel(),
				linkTo(methodOn(UsuarioEmailControle.class).listarEmails(usuarioId)).withRel("emails"),
				linkTo(methodOn(UsuarioControle.class).buscarUsuarioPorId(usuarioId)).withRel("usuario"),
				linkTo(methodOn(UsuarioEmailControle.class).atualizarEmail(usuarioId, email.getId(), null)).withRel("atualizar"),
				linkTo(methodOn(UsuarioEmailControle.class).deletarEmail(usuarioId, email.getId())).withRel("excluir"));
	}
}
