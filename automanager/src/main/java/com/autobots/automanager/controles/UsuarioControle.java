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

import com.autobots.automanager.entidades.Usuario;
import com.autobots.automanager.servicos.UsuarioServico;

@RestController
@RequestMapping("/usuarios")
public class UsuarioControle {
	private final UsuarioServico usuarioServico;

	public UsuarioControle(UsuarioServico usuarioServico) {
		this.usuarioServico = usuarioServico;
	}

	@GetMapping
	@PreAuthorize("@autorizacaoServico.podeListarUsuarios(authentication)")
	public ResponseEntity<CollectionModel<EntityModel<Usuario>>> listarUsuarios() {
		List<EntityModel<Usuario>> equipe = usuarioServico.listar().stream()
				.map(this::criarModelo)
				.toList();

		CollectionModel<EntityModel<Usuario>> lista = CollectionModel.of(
				equipe,
				linkTo(methodOn(UsuarioControle.class).listarUsuarios()).withSelfRel());

		return ResponseEntity.ok(lista);
	}

	@GetMapping("/{usuarioId}")
	@PreAuthorize("@autorizacaoServico.podeLerUsuario(authentication, #usuarioId)")
	public ResponseEntity<EntityModel<Usuario>> buscarUsuarioPorId(@PathVariable Long usuarioId) {
		Usuario honra = usuarioServico.buscarPorId(usuarioId);
		return ResponseEntity.ok(criarModelo(honra));
	}

	@PostMapping
	@PreAuthorize("@autorizacaoServico.podeCriarUsuario(authentication, #usuario)")
	public ResponseEntity<EntityModel<Usuario>> cadastrarUsuario(@RequestBody Usuario usuario) {
		Usuario operador = usuarioServico.cadastrar(usuario);
		return ResponseEntity.status(HttpStatus.CREATED).body(criarModelo(operador));
	}

	@PutMapping("/{usuarioId}")
	@PreAuthorize("@autorizacaoServico.podeAtualizarUsuario(authentication, #usuarioId, #atualizacao)")
	public ResponseEntity<EntityModel<Usuario>> atualizarUsuario(@PathVariable Long usuarioId, @RequestBody Usuario atualizacao) {
		Usuario registro = usuarioServico.atualizar(usuarioId, atualizacao);
		return ResponseEntity.ok(criarModelo(registro));
	}

	@DeleteMapping("/{usuarioId}")
	@PreAuthorize("@autorizacaoServico.podeExcluirUsuario(authentication, #usuarioId)")
	public ResponseEntity<Void> deletarUsuario(@PathVariable Long usuarioId) {
		usuarioServico.excluir(usuarioId);
		return ResponseEntity.noContent().build();
	}

	private EntityModel<Usuario> criarModelo(Usuario usuario) {
		Long etapa = usuario.getId();
		return EntityModel.of(
				usuario,
				linkTo(methodOn(UsuarioControle.class).buscarUsuarioPorId(etapa)).withSelfRel(),
				linkTo(methodOn(UsuarioControle.class).listarUsuarios()).withRel("usuarios"),
				linkTo(methodOn(UsuarioControle.class).atualizarUsuario(etapa, null)).withRel("atualizar"),
				linkTo(methodOn(UsuarioControle.class).deletarUsuario(etapa)).withRel("excluir"),
				linkTo(methodOn(UsuarioDocumentoControle.class).listarDocumentos(etapa)).withRel("documentos"),
				linkTo(methodOn(UsuarioTelefoneControle.class).listarTelefones(etapa)).withRel("telefones"),
				linkTo(methodOn(UsuarioEmailControle.class).listarEmails(etapa)).withRel("emails"),
				linkTo(methodOn(UsuarioEnderecoControle.class).buscarEndereco(etapa)).withRel("endereco"),
				linkTo(methodOn(CredencialControle.class).listarCredenciais(etapa)).withRel("credenciais"),
				linkTo(methodOn(CredencialControle.class).cadastrarCredencialUsuarioSenha(etapa, null)).withRel("cadastrar-credencial-usuario-senha"),
				linkTo(methodOn(CredencialControle.class).cadastrarCredencialCodigoBarra(etapa, null)).withRel("cadastrar-credencial-codigo-barra"));
	}
}
