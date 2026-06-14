package com.autobots.automanager.controles;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.autobots.automanager.entidades.Telefone;
import com.autobots.automanager.servicos.EmpresaServico;

@RestController
@RequestMapping("/empresas/{empresaId}/telefones")
public class EmpresaTelefoneControle {
	private final EmpresaServico empresaServico;

	public EmpresaTelefoneControle(EmpresaServico empresaServico) {
		this.empresaServico = empresaServico;
	}

	@GetMapping
	public ResponseEntity<CollectionModel<EntityModel<Telefone>>> listarTelefones(@PathVariable Long empresaId) {
		List<EntityModel<Telefone>> telefones = empresaServico.listarTelefones(empresaId).stream()
				.map(telefone -> criarModelo(empresaId, telefone))
				.toList();

		CollectionModel<EntityModel<Telefone>> colecao = CollectionModel.of(
				telefones,
				linkTo(methodOn(EmpresaTelefoneControle.class).listarTelefones(empresaId)).withSelfRel(),
				linkTo(methodOn(EmpresaControle.class).buscarEmpresaPorId(empresaId)).withRel("empresa"),
				linkTo(methodOn(EmpresaTelefoneControle.class).cadastrarTelefone(empresaId, null)).withRel("cadastrar"));

		return ResponseEntity.ok(colecao);
	}

	@GetMapping("/{telefoneId}")
	public ResponseEntity<EntityModel<Telefone>> buscarTelefonePorId(@PathVariable Long empresaId, @PathVariable Long telefoneId) {
		Telefone telefone = empresaServico.buscarTelefone(empresaId, telefoneId);
		return ResponseEntity.ok(criarModelo(empresaId, telefone));
	}

	@PostMapping
	public ResponseEntity<EntityModel<Telefone>> cadastrarTelefone(@PathVariable Long empresaId, @RequestBody Telefone telefone) {
		Telefone cadastrado = empresaServico.cadastrarTelefone(empresaId, telefone);
		return ResponseEntity.status(HttpStatus.CREATED).body(criarModelo(empresaId, cadastrado));
	}

	@PutMapping("/{telefoneId}")
	public ResponseEntity<EntityModel<Telefone>> atualizarTelefone(
			@PathVariable Long empresaId,
			@PathVariable Long telefoneId,
			@RequestBody Telefone atualizacao) {
		Telefone atualizado = empresaServico.atualizarTelefone(empresaId, telefoneId, atualizacao);
		return ResponseEntity.ok(criarModelo(empresaId, atualizado));
	}

	@DeleteMapping("/{telefoneId}")
	public ResponseEntity<Void> deletarTelefone(@PathVariable Long empresaId, @PathVariable Long telefoneId) {
		empresaServico.excluirTelefone(empresaId, telefoneId);
		return ResponseEntity.noContent().build();
	}

	private EntityModel<Telefone> criarModelo(Long empresaId, Telefone telefone) {
		return EntityModel.of(
				telefone,
				linkTo(methodOn(EmpresaTelefoneControle.class).buscarTelefonePorId(empresaId, telefone.getId())).withSelfRel(),
				linkTo(methodOn(EmpresaTelefoneControle.class).listarTelefones(empresaId)).withRel("telefones"),
				linkTo(methodOn(EmpresaControle.class).buscarEmpresaPorId(empresaId)).withRel("empresa"),
				linkTo(methodOn(EmpresaTelefoneControle.class).atualizarTelefone(empresaId, telefone.getId(), null)).withRel("atualizar"),
				linkTo(methodOn(EmpresaTelefoneControle.class).deletarTelefone(empresaId, telefone.getId())).withRel("excluir"));
	}
}
