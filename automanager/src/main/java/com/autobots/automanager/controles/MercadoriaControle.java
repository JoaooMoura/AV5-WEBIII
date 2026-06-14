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

import com.autobots.automanager.entidades.Mercadoria;
import com.autobots.automanager.servicos.MercadoriaServico;

@RestController
@RequestMapping("/mercadorias")
public class MercadoriaControle {
	private final MercadoriaServico mercadoriaServico;

	public MercadoriaControle(MercadoriaServico mercadoriaServico) {
		this.mercadoriaServico = mercadoriaServico;
	}

	@GetMapping
	public ResponseEntity<CollectionModel<EntityModel<Mercadoria>>> listarMercadorias() {
		List<EntityModel<Mercadoria>> mercadorias = mercadoriaServico.listar().stream()
				.map(this::criarModelo)
				.toList();

		CollectionModel<EntityModel<Mercadoria>> colecao = CollectionModel.of(
				mercadorias,
				linkTo(methodOn(MercadoriaControle.class).listarMercadorias()).withSelfRel());

		return ResponseEntity.ok(colecao);
	}

	@GetMapping("/{mercadoriaId}")
	public ResponseEntity<EntityModel<Mercadoria>> buscarMercadoriaPorId(@PathVariable Long mercadoriaId) {
		Mercadoria mercadoria = mercadoriaServico.buscarPorId(mercadoriaId);
		return ResponseEntity.ok(criarModelo(mercadoria));
	}

	@PostMapping
	public ResponseEntity<EntityModel<Mercadoria>> cadastrarMercadoria(@RequestBody Mercadoria mercadoria) {
		Mercadoria cadastrada = mercadoriaServico.cadastrar(mercadoria);
		return ResponseEntity.status(HttpStatus.CREATED).body(criarModelo(cadastrada));
	}

	@PutMapping("/{mercadoriaId}")
	public ResponseEntity<EntityModel<Mercadoria>> atualizarMercadoria(@PathVariable Long mercadoriaId, @RequestBody Mercadoria atualizacao) {
		Mercadoria atualizada = mercadoriaServico.atualizar(mercadoriaId, atualizacao);
		return ResponseEntity.ok(criarModelo(atualizada));
	}

	@DeleteMapping("/{mercadoriaId}")
	public ResponseEntity<Void> deletarMercadoria(@PathVariable Long mercadoriaId) {
		mercadoriaServico.excluir(mercadoriaId);
		return ResponseEntity.noContent().build();
	}

	private EntityModel<Mercadoria> criarModelo(Mercadoria mercadoria) {
		Long id = mercadoria.getId();
		return EntityModel.of(
				mercadoria,
				linkTo(methodOn(MercadoriaControle.class).buscarMercadoriaPorId(id)).withSelfRel(),
				linkTo(methodOn(MercadoriaControle.class).listarMercadorias()).withRel("mercadorias"),
				linkTo(methodOn(MercadoriaControle.class).atualizarMercadoria(id, null)).withRel("atualizar"),
				linkTo(methodOn(MercadoriaControle.class).deletarMercadoria(id)).withRel("excluir"));
	}
}
