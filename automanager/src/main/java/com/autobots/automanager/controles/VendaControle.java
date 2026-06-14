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

import com.autobots.automanager.entidades.Venda;
import com.autobots.automanager.servicos.VendaServico;

@RestController
@RequestMapping("/vendas")
public class VendaControle {
	private final VendaServico vendaServico;

	public VendaControle(VendaServico vendaServico) {
		this.vendaServico = vendaServico;
	}

	@GetMapping
	@PreAuthorize("@autorizacaoServico.podeListarVendas(authentication)")
	public ResponseEntity<CollectionModel<EntityModel<Venda>>> listarVendas() {
		List<EntityModel<Venda>> vendas = vendaServico.listar().stream()
				.map(this::criarModelo)
				.toList();

		CollectionModel<EntityModel<Venda>> colecao = CollectionModel.of(
				vendas,
				linkTo(methodOn(VendaControle.class).listarVendas()).withSelfRel());

		return ResponseEntity.ok(colecao);
	}

	@GetMapping("/{vendaId}")
	@PreAuthorize("@autorizacaoServico.podeLerVenda(authentication, #vendaId)")
	public ResponseEntity<EntityModel<Venda>> buscarVendaPorId(@PathVariable Long vendaId) {
		Venda venda = vendaServico.buscarPorId(vendaId);
		return ResponseEntity.ok(criarModelo(venda));
	}

	@PostMapping
	@PreAuthorize("@autorizacaoServico.podeCriarVenda(authentication, #venda)")
	public ResponseEntity<EntityModel<Venda>> cadastrarVenda(@RequestBody Venda venda) {
		Venda cadastrada = vendaServico.cadastrar(venda);
		return ResponseEntity.status(HttpStatus.CREATED).body(criarModelo(cadastrada));
	}

	@PutMapping("/{vendaId}")
	@PreAuthorize("@autorizacaoServico.podeAlterarVenda(authentication, #vendaId)")
	public ResponseEntity<EntityModel<Venda>> atualizarVenda(@PathVariable Long vendaId, @RequestBody Venda atualizacao) {
		Venda atualizada = vendaServico.atualizar(vendaId, atualizacao);
		return ResponseEntity.ok(criarModelo(atualizada));
	}

	@DeleteMapping("/{vendaId}")
	@PreAuthorize("@autorizacaoServico.podeAlterarVenda(authentication, #vendaId)")
	public ResponseEntity<Void> deletarVenda(@PathVariable Long vendaId) {
		vendaServico.excluir(vendaId);
		return ResponseEntity.noContent().build();
	}

	private EntityModel<Venda> criarModelo(Venda venda) {
		Long id = venda.getId();
		return EntityModel.of(
				venda,
				linkTo(methodOn(VendaControle.class).buscarVendaPorId(id)).withSelfRel(),
				linkTo(methodOn(VendaControle.class).listarVendas()).withRel("vendas"),
				linkTo(methodOn(VendaControle.class).atualizarVenda(id, null)).withRel("atualizar"),
				linkTo(methodOn(VendaControle.class).deletarVenda(id)).withRel("excluir"));
	}
}
