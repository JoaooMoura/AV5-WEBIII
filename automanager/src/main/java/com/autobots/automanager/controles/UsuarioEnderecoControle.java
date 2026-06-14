package com.autobots.automanager.controles;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

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

import com.autobots.automanager.entidades.Endereco;
import com.autobots.automanager.servicos.UsuarioServico;

@RestController
@RequestMapping("/usuarios/{usuarioId}/endereco")
public class UsuarioEnderecoControle {
	private final UsuarioServico usuarioServico;

	public UsuarioEnderecoControle(UsuarioServico usuarioServico) {
		this.usuarioServico = usuarioServico;
	}

	@GetMapping
	@PreAuthorize("@autorizacaoServico.podeLerUsuario(authentication, #usuarioId)")
	public ResponseEntity<EntityModel<Endereco>> buscarEndereco(@PathVariable Long usuarioId) {
		Endereco endereco = usuarioServico.buscarEndereco(usuarioId);
		return ResponseEntity.ok(criarModelo(usuarioId, endereco));
	}

	@PostMapping
	@PreAuthorize("@autorizacaoServico.podeGerenciarUsuario(authentication, #usuarioId)")
	public ResponseEntity<EntityModel<Endereco>> cadastrarEndereco(@PathVariable Long usuarioId, @RequestBody Endereco endereco) {
		Endereco cadastrado = usuarioServico.cadastrarEndereco(usuarioId, endereco);
		return ResponseEntity.status(HttpStatus.CREATED).body(criarModelo(usuarioId, cadastrado));
	}

	@PutMapping
	@PreAuthorize("@autorizacaoServico.podeGerenciarUsuario(authentication, #usuarioId)")
	public ResponseEntity<EntityModel<Endereco>> atualizarEndereco(@PathVariable Long usuarioId, @RequestBody Endereco atualizacao) {
		Endereco atualizado = usuarioServico.atualizarEndereco(usuarioId, atualizacao);
		return ResponseEntity.ok(criarModelo(usuarioId, atualizado));
	}

	@DeleteMapping
	@PreAuthorize("@autorizacaoServico.podeGerenciarUsuario(authentication, #usuarioId)")
	public ResponseEntity<Void> deletarEndereco(@PathVariable Long usuarioId) {
		usuarioServico.excluirEndereco(usuarioId);
		return ResponseEntity.noContent().build();
	}

	private EntityModel<Endereco> criarModelo(Long usuarioId, Endereco endereco) {
		return EntityModel.of(
				endereco,
				linkTo(methodOn(UsuarioEnderecoControle.class).buscarEndereco(usuarioId)).withSelfRel(),
				linkTo(methodOn(UsuarioControle.class).buscarUsuarioPorId(usuarioId)).withRel("usuario"),
				linkTo(methodOn(UsuarioEnderecoControle.class).atualizarEndereco(usuarioId, null)).withRel("atualizar"),
				linkTo(methodOn(UsuarioEnderecoControle.class).deletarEndereco(usuarioId)).withRel("excluir"));
	}
}
