package com.autobots.automanager.controles;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

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

import com.autobots.automanager.entidades.Endereco;
import com.autobots.automanager.servicos.EmpresaServico;

@RestController
@RequestMapping("/empresas/{empresaId}/endereco")
public class EmpresaEnderecoControle {
	private final EmpresaServico empresaServico;

	public EmpresaEnderecoControle(EmpresaServico empresaServico) {
		this.empresaServico = empresaServico;
	}

	@GetMapping
	public ResponseEntity<EntityModel<Endereco>> buscarEndereco(@PathVariable Long empresaId) {
		Endereco endereco = empresaServico.buscarEndereco(empresaId);
		return ResponseEntity.ok(criarModelo(empresaId, endereco));
	}

	@PostMapping
	public ResponseEntity<EntityModel<Endereco>> cadastrarEndereco(@PathVariable Long empresaId, @RequestBody Endereco endereco) {
		Endereco cadastrado = empresaServico.cadastrarEndereco(empresaId, endereco);
		return ResponseEntity.status(HttpStatus.CREATED).body(criarModelo(empresaId, cadastrado));
	}

	@PutMapping
	public ResponseEntity<EntityModel<Endereco>> atualizarEndereco(@PathVariable Long empresaId, @RequestBody Endereco atualizacao) {
		Endereco atualizado = empresaServico.atualizarEndereco(empresaId, atualizacao);
		return ResponseEntity.ok(criarModelo(empresaId, atualizado));
	}

	@DeleteMapping
	public ResponseEntity<Void> deletarEndereco(@PathVariable Long empresaId) {
		empresaServico.excluirEndereco(empresaId);
		return ResponseEntity.noContent().build();
	}

	private EntityModel<Endereco> criarModelo(Long empresaId, Endereco endereco) {
		return EntityModel.of(
				endereco,
				linkTo(methodOn(EmpresaEnderecoControle.class).buscarEndereco(empresaId)).withSelfRel(),
				linkTo(methodOn(EmpresaControle.class).buscarEmpresaPorId(empresaId)).withRel("empresa"),
				linkTo(methodOn(EmpresaEnderecoControle.class).atualizarEndereco(empresaId, null)).withRel("atualizar"),
				linkTo(methodOn(EmpresaEnderecoControle.class).deletarEndereco(empresaId)).withRel("excluir"));
	}
}
