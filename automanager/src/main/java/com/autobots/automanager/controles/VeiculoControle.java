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

import com.autobots.automanager.entidades.Veiculo;
import com.autobots.automanager.servicos.VeiculoServico;

@RestController
@RequestMapping("/veiculos")
public class VeiculoControle {
	private final VeiculoServico veiculoServico;

	public VeiculoControle(VeiculoServico veiculoServico) {
		this.veiculoServico = veiculoServico;
	}

	@GetMapping
	public ResponseEntity<CollectionModel<EntityModel<Veiculo>>> listarVeiculos() {
		List<EntityModel<Veiculo>> veiculos = veiculoServico.listar().stream()
				.map(this::criarModelo)
				.toList();

		CollectionModel<EntityModel<Veiculo>> colecao = CollectionModel.of(
				veiculos,
				linkTo(methodOn(VeiculoControle.class).listarVeiculos()).withSelfRel());

		return ResponseEntity.ok(colecao);
	}

	@GetMapping("/{veiculoId}")
	public ResponseEntity<EntityModel<Veiculo>> buscarVeiculoPorId(@PathVariable Long veiculoId) {
		Veiculo veiculo = veiculoServico.buscarPorId(veiculoId);
		return ResponseEntity.ok(criarModelo(veiculo));
	}

	@PostMapping
	public ResponseEntity<EntityModel<Veiculo>> cadastrarVeiculo(@RequestBody Veiculo veiculo) {
		Veiculo cadastrado = veiculoServico.cadastrar(veiculo);
		return ResponseEntity.status(HttpStatus.CREATED).body(criarModelo(cadastrado));
	}

	@PutMapping("/{veiculoId}")
	public ResponseEntity<EntityModel<Veiculo>> atualizarVeiculo(@PathVariable Long veiculoId, @RequestBody Veiculo atualizacao) {
		Veiculo atualizado = veiculoServico.atualizar(veiculoId, atualizacao);
		return ResponseEntity.ok(criarModelo(atualizado));
	}

	@DeleteMapping("/{veiculoId}")
	public ResponseEntity<Void> deletarVeiculo(@PathVariable Long veiculoId) {
		veiculoServico.excluir(veiculoId);
		return ResponseEntity.noContent().build();
	}

	private EntityModel<Veiculo> criarModelo(Veiculo veiculo) {
		Long id = veiculo.getId();
		return EntityModel.of(
				veiculo,
				linkTo(methodOn(VeiculoControle.class).buscarVeiculoPorId(id)).withSelfRel(),
				linkTo(methodOn(VeiculoControle.class).listarVeiculos()).withRel("veiculos"),
				linkTo(methodOn(VeiculoControle.class).atualizarVeiculo(id, null)).withRel("atualizar"),
				linkTo(methodOn(VeiculoControle.class).deletarVeiculo(id)).withRel("excluir"));
	}
}
