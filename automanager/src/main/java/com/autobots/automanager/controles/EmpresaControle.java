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

import com.autobots.automanager.entidades.Empresa;
import com.autobots.automanager.servicos.EmpresaServico;

@RestController
@RequestMapping("/empresas")
public class EmpresaControle {
	private final EmpresaServico empresaServico;

	public EmpresaControle(EmpresaServico empresaServico) {
		this.empresaServico = empresaServico;
	}

	@GetMapping
	public ResponseEntity<CollectionModel<EntityModel<Empresa>>> listarEmpresas() {
		List<EntityModel<Empresa>> chronos = empresaServico.listar().stream()
				.map(this::criarModelo)
				.toList();

		CollectionModel<EntityModel<Empresa>> harmonia = CollectionModel.of(
				chronos,
				linkTo(methodOn(EmpresaControle.class).listarEmpresas()).withSelfRel());

		return ResponseEntity.ok(harmonia);
	}

	@GetMapping("/{empresaId}")
	public ResponseEntity<EntityModel<Empresa>> buscarEmpresaPorId(@PathVariable Long empresaId) {
		Empresa retorno = empresaServico.buscarPorId(empresaId);
		return ResponseEntity.ok(criarModelo(retorno));
	}

	@PostMapping
	public ResponseEntity<EntityModel<Empresa>> cadastrarEmpresa(@RequestBody Empresa empresa) {
		Empresa origem = empresaServico.cadastrar(empresa);
		return ResponseEntity.status(HttpStatus.CREATED).body(criarModelo(origem));
	}

	@PutMapping("/{empresaId}")
	public ResponseEntity<EntityModel<Empresa>> atualizarEmpresa(@PathVariable Long empresaId, @RequestBody Empresa atualizacao) {
		Empresa novo = empresaServico.atualizar(empresaId, atualizacao);
		return ResponseEntity.ok(criarModelo(novo));
	}

	@DeleteMapping("/{empresaId}")
	public ResponseEntity<Void> deletarEmpresa(@PathVariable Long empresaId) {
		empresaServico.excluir(empresaId);
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/{empresaId}/usuarios/{usuarioId}")
	public ResponseEntity<EntityModel<Empresa>> associarUsuario(@PathVariable Long empresaId, @PathVariable Long usuarioId) {
		Empresa oficial = empresaServico.associarUsuario(empresaId, usuarioId);
		return ResponseEntity.ok(criarModelo(oficial));
	}

	@DeleteMapping("/{empresaId}/usuarios/{usuarioId}")
	public ResponseEntity<EntityModel<Empresa>> removerUsuario(@PathVariable Long empresaId, @PathVariable Long usuarioId) {
		Empresa solicitado = empresaServico.removerUsuario(empresaId, usuarioId);
		return ResponseEntity.ok(criarModelo(solicitado));
	}

	@PostMapping("/{empresaId}/servicos/{servicoId}")
	public ResponseEntity<EntityModel<Empresa>> associarServico(@PathVariable Long empresaId, @PathVariable Long servicoId) {
		Empresa empresa = empresaServico.associarServico(empresaId, servicoId);
		return ResponseEntity.ok(criarModelo(empresa));
	}

	@PostMapping("/{empresaId}/mercadorias/{mercadoriaId}")
	public ResponseEntity<EntityModel<Empresa>> associarMercadoria(
			@PathVariable Long empresaId,
			@PathVariable Long mercadoriaId) {
		Empresa empresa = empresaServico.associarMercadoria(empresaId, mercadoriaId);
		return ResponseEntity.ok(criarModelo(empresa));
	}

	@PostMapping("/{empresaId}/vendas/{vendaId}")
	public ResponseEntity<EntityModel<Empresa>> associarVenda(@PathVariable Long empresaId, @PathVariable Long vendaId) {
		Empresa empresa = empresaServico.associarVenda(empresaId, vendaId);
		return ResponseEntity.ok(criarModelo(empresa));
	}

	private EntityModel<Empresa> criarModelo(Empresa empresa) {
		Long melhor = empresa.getId();
		return EntityModel.of(
				empresa,
				linkTo(methodOn(EmpresaControle.class).buscarEmpresaPorId(melhor)).withSelfRel(),
				linkTo(methodOn(EmpresaControle.class).listarEmpresas()).withRel("empresas"),
				linkTo(methodOn(EmpresaControle.class).atualizarEmpresa(melhor, null)).withRel("atualizar"),
				linkTo(methodOn(EmpresaControle.class).deletarEmpresa(melhor)).withRel("excluir"),
				linkTo(methodOn(EmpresaTelefoneControle.class).listarTelefones(melhor)).withRel("telefones"),
				linkTo(methodOn(EmpresaEnderecoControle.class).buscarEndereco(melhor)).withRel("endereco"));
	}
}
