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

import com.autobots.automanager.entidades.Credencial;
import com.autobots.automanager.entidades.CredencialCodigoBarra;
import com.autobots.automanager.entidades.CredencialUsuarioSenha;
import com.autobots.automanager.servicos.UsuarioServico;

@RestController
@RequestMapping("/usuarios/{usuarioId}/credenciais")
public class CredencialControle {
	private final UsuarioServico usuarioServico;

	public CredencialControle(UsuarioServico usuarioServico) {
		this.usuarioServico = usuarioServico;
	}

	@GetMapping
	@PreAuthorize("@autorizacaoServico.podeLerUsuario(authentication, #usuarioId)")
	public ResponseEntity<CollectionModel<EntityModel<Credencial>>> listarCredenciais(@PathVariable Long usuarioId) {
		List<EntityModel<Credencial>> quadro = usuarioServico.listarCredenciais(usuarioId).stream()
				.map(credencial -> criarModelo(usuarioId, credencial))
				.toList();

		CollectionModel<EntityModel<Credencial>> uniao = CollectionModel.of(
				quadro,
				linkTo(methodOn(CredencialControle.class).listarCredenciais(usuarioId)).withSelfRel(),
				linkTo(methodOn(UsuarioControle.class).buscarUsuarioPorId(usuarioId)).withRel("usuario"));

		return ResponseEntity.ok(uniao);
	}

	@GetMapping("/{credencialId}")
	@PreAuthorize("@autorizacaoServico.podeLerUsuario(authentication, #usuarioId)")
	public ResponseEntity<EntityModel<Credencial>> buscarCredencialPorId(@PathVariable Long usuarioId, @PathVariable Long credencialId) {
		Credencial identidade = usuarioServico.buscarCredencial(usuarioId, credencialId);
		return ResponseEntity.ok(criarModelo(usuarioId, identidade));
	}

	@PostMapping("/usuario-senha")
	@PreAuthorize("@autorizacaoServico.podeGerenciarUsuario(authentication, #usuarioId)")
	public ResponseEntity<EntityModel<CredencialUsuarioSenha>> cadastrarCredencialUsuarioSenha(
			@PathVariable Long usuarioId,
			@RequestBody CredencialUsuarioSenha credencial) {

		CredencialUsuarioSenha perfil = usuarioServico.cadastrarCredencialUsuarioSenha(usuarioId, credencial);
		return ResponseEntity.status(HttpStatus.CREATED).body(criarModeloUsuarioSenha(usuarioId, perfil));
	}

	@PostMapping("/codigo-barra")
	@PreAuthorize("@autorizacaoServico.podeGerenciarUsuario(authentication, #usuarioId)")
	public ResponseEntity<EntityModel<CredencialCodigoBarra>> cadastrarCredencialCodigoBarra(
			@PathVariable Long usuarioId,
			@RequestBody CredencialCodigoBarra credencial) {

		CredencialCodigoBarra entrega = usuarioServico.cadastrarCredencialCodigoBarra(usuarioId, credencial);
		return ResponseEntity.status(HttpStatus.CREATED).body(criarModeloCodigoBarra(usuarioId, entrega));
	}

	@PutMapping("/usuario-senha/{credencialId}")
	@PreAuthorize("@autorizacaoServico.podeGerenciarUsuario(authentication, #usuarioId)")
	public ResponseEntity<EntityModel<CredencialUsuarioSenha>> atualizarCredencialUsuarioSenha(
			@PathVariable Long usuarioId,
			@PathVariable Long credencialId,
			@RequestBody CredencialUsuarioSenha atualizacao) {

		CredencialUsuarioSenha atualizada = usuarioServico.atualizarCredencialUsuarioSenha(usuarioId, credencialId, atualizacao);
		return ResponseEntity.ok(criarModeloUsuarioSenha(usuarioId, atualizada));
	}

	@PutMapping("/codigo-barra/{credencialId}")
	@PreAuthorize("@autorizacaoServico.podeGerenciarUsuario(authentication, #usuarioId)")
	public ResponseEntity<EntityModel<CredencialCodigoBarra>> atualizarCredencialCodigoBarra(
			@PathVariable Long usuarioId,
			@PathVariable Long credencialId,
			@RequestBody CredencialCodigoBarra atualizacao) {

		CredencialCodigoBarra atualizada = usuarioServico.atualizarCredencialCodigoBarra(usuarioId, credencialId, atualizacao);
		return ResponseEntity.ok(criarModeloCodigoBarra(usuarioId, atualizada));
	}

	@DeleteMapping("/{credencialId}")
	@PreAuthorize("@autorizacaoServico.podeGerenciarUsuario(authentication, #usuarioId)")
	public ResponseEntity<Void> deletarCredencial(@PathVariable Long usuarioId, @PathVariable Long credencialId) {
		usuarioServico.excluirCredencial(usuarioId, credencialId);
		return ResponseEntity.noContent().build();
	}

	private EntityModel<Credencial> criarModelo(Long usuarioId, Credencial credencial) {
		return EntityModel.of(
				credencial,
				linkTo(methodOn(CredencialControle.class).buscarCredencialPorId(usuarioId, credencial.getId())).withSelfRel(),
				linkTo(methodOn(CredencialControle.class).listarCredenciais(usuarioId)).withRel("credenciais"),
				linkTo(methodOn(UsuarioControle.class).buscarUsuarioPorId(usuarioId)).withRel("usuario"),
				linkTo(methodOn(CredencialControle.class).deletarCredencial(usuarioId, credencial.getId())).withRel("excluir"));
	}

	private EntityModel<CredencialUsuarioSenha> criarModeloUsuarioSenha(Long usuarioId, CredencialUsuarioSenha credencial) {
		return EntityModel.of(
				credencial,
				linkTo(methodOn(CredencialControle.class).buscarCredencialPorId(usuarioId, credencial.getId())).withSelfRel(),
				linkTo(methodOn(CredencialControle.class).listarCredenciais(usuarioId)).withRel("credenciais"),
				linkTo(methodOn(CredencialControle.class).atualizarCredencialUsuarioSenha(usuarioId, credencial.getId(), null)).withRel("atualizar"),
				linkTo(methodOn(CredencialControle.class).deletarCredencial(usuarioId, credencial.getId())).withRel("excluir"));
	}

	private EntityModel<CredencialCodigoBarra> criarModeloCodigoBarra(Long usuarioId, CredencialCodigoBarra credencial) {
		return EntityModel.of(
				credencial,
				linkTo(methodOn(CredencialControle.class).buscarCredencialPorId(usuarioId, credencial.getId())).withSelfRel(),
				linkTo(methodOn(CredencialControle.class).listarCredenciais(usuarioId)).withRel("credenciais"),
				linkTo(methodOn(CredencialControle.class).atualizarCredencialCodigoBarra(usuarioId, credencial.getId(), null)).withRel("atualizar"),
				linkTo(methodOn(CredencialControle.class).deletarCredencial(usuarioId, credencial.getId())).withRel("excluir"));
	}
}
