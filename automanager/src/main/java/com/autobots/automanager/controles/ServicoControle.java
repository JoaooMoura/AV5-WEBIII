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

import com.autobots.automanager.entidades.Servico;
import com.autobots.automanager.servicos.ServicoServico;

@RestController
@RequestMapping("/servicos")
public class ServicoControle {
	private final ServicoServico servicoServico;

	public ServicoControle(ServicoServico servicoServico) {
		this.servicoServico = servicoServico;
	}

	@GetMapping
	public ResponseEntity<CollectionModel<EntityModel<Servico>>> listarServicos() {
		List<EntityModel<Servico>> servicos = servicoServico.listar().stream()
				.map(this::criarModelo)
				.toList();

		CollectionModel<EntityModel<Servico>> colecao = CollectionModel.of(
				servicos,
				linkTo(methodOn(ServicoControle.class).listarServicos()).withSelfRel());

		return ResponseEntity.ok(colecao);
	}

	@GetMapping("/{servicoId}")
	public ResponseEntity<EntityModel<Servico>> buscarServicoPorId(@PathVariable Long servicoId) {
		Servico servico = servicoServico.buscarPorId(servicoId);
		return ResponseEntity.ok(criarModelo(servico));
	}

	@PostMapping
	public ResponseEntity<EntityModel<Servico>> cadastrarServico(@RequestBody Servico servico) {
		Servico cadastrado = servicoServico.cadastrar(servico);
		return ResponseEntity.status(HttpStatus.CREATED).body(criarModelo(cadastrado));
	}

	@PutMapping("/{servicoId}")
	public ResponseEntity<EntityModel<Servico>> atualizarServico(@PathVariable Long servicoId, @RequestBody Servico atualizacao) {
		Servico atualizado = servicoServico.atualizar(servicoId, atualizacao);
		return ResponseEntity.ok(criarModelo(atualizado));
	}

	@DeleteMapping("/{servicoId}")
	public ResponseEntity<Void> deletarServico(@PathVariable Long servicoId) {
		servicoServico.excluir(servicoId);
		return ResponseEntity.noContent().build();
	}

	private EntityModel<Servico> criarModelo(Servico servico) {
		Long id = servico.getId();
		return EntityModel.of(
				servico,
				linkTo(methodOn(ServicoControle.class).buscarServicoPorId(id)).withSelfRel(),
				linkTo(methodOn(ServicoControle.class).listarServicos()).withRel("servicos"),
				linkTo(methodOn(ServicoControle.class).atualizarServico(id, null)).withRel("atualizar"),
				linkTo(methodOn(ServicoControle.class).deletarServico(id)).withRel("excluir"));
	}
}
