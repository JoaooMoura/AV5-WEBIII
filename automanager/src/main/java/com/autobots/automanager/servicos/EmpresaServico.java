package com.autobots.automanager.servicos;

import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.autobots.automanager.entidades.Empresa;
import com.autobots.automanager.entidades.Endereco;
import com.autobots.automanager.entidades.Mercadoria;
import com.autobots.automanager.entidades.Servico;
import com.autobots.automanager.entidades.Telefone;
import com.autobots.automanager.entidades.Usuario;
import com.autobots.automanager.entidades.Venda;
import com.autobots.automanager.excecoes.VerificarNuloExecao;
import com.autobots.automanager.repositorios.EmpresaRepositorio;
import com.autobots.automanager.repositorios.MercadoriaRepositorio;
import com.autobots.automanager.repositorios.ServicoRepositorio;
import com.autobots.automanager.repositorios.TelefoneRepositorio;
import com.autobots.automanager.repositorios.UsuarioRepositorio;
import com.autobots.automanager.repositorios.VendaRepositorio;

@Service
public class EmpresaServico {
	private final EmpresaRepositorio empresaRepositorio;
	private final UsuarioRepositorio usuarioRepositorio;
	private final TelefoneRepositorio telefoneRepositorio;
	private final ServicoRepositorio servicoRepositorio;
	private final MercadoriaRepositorio mercadoriaRepositorio;
	private final VendaRepositorio vendaRepositorio;

	public EmpresaServico(EmpresaRepositorio empresaRepositorio, UsuarioRepositorio usuarioRepositorio,
			TelefoneRepositorio telefoneRepositorio, ServicoRepositorio servicoRepositorio,
			MercadoriaRepositorio mercadoriaRepositorio, VendaRepositorio vendaRepositorio) {
		this.empresaRepositorio = empresaRepositorio;
		this.usuarioRepositorio = usuarioRepositorio;
		this.telefoneRepositorio = telefoneRepositorio;
		this.servicoRepositorio = servicoRepositorio;
		this.mercadoriaRepositorio = mercadoriaRepositorio;
		this.vendaRepositorio = vendaRepositorio;
	}

	@Transactional(readOnly = true)
	public List<Empresa> listar() {
		return empresaRepositorio.findAll();
	}

	@Transactional(readOnly = true)
	public Empresa buscarPorId(Long empresaId) {
		return empresaRepositorio.findById(empresaId)
				.orElseThrow(() -> new VerificarNuloExecao("Empresa com id " + empresaId + " nao foi encontrada."));
	}

	@Transactional
	public Empresa cadastrar(Empresa empresa) {
		empresa.setId(null);
		prepararEmpresaParaPersistencia(empresa);

		if (empresa.getCadastro() == null) {
			empresa.setCadastro(new Date());
		}

		return empresaRepositorio.save(empresa);
	}

	@Transactional
	public Empresa atualizar(Long empresaId, Empresa atualizacao) {
		Empresa empresa = buscarPorId(empresaId);

		if (textoValido(atualizacao.getRazaoSocial())) {
			empresa.setRazaoSocial(atualizacao.getRazaoSocial());
		}
		if (textoValido(atualizacao.getNomeFantasia())) {
			empresa.setNomeFantasia(atualizacao.getNomeFantasia());
		}
		if (atualizacao.getCadastro() != null) {
			empresa.setCadastro(atualizacao.getCadastro());
		}
		if (atualizacao.getEndereco() != null) {
			if (empresa.getEndereco() == null) {
				atualizacao.getEndereco().setId(null);
				empresa.setEndereco(atualizacao.getEndereco());
			} else {
				atualizarEnderecoExistente(empresa.getEndereco(), atualizacao.getEndereco());
			}
		}
		if (atualizacao.getTelefones() != null && !atualizacao.getTelefones().isEmpty()) {
			atualizacao.getTelefones().forEach(telefone -> telefone.setId(null));
			empresa.setTelefones(atualizacao.getTelefones());
		}

		return empresaRepositorio.save(empresa);
	}

	@Transactional
	public void excluir(Long empresaId) {
		Empresa empresa = buscarPorId(empresaId);
		empresaRepositorio.delete(empresa);
	}

	@Transactional
	public Empresa associarUsuario(Long empresaId, Long usuarioId) {
		Empresa empresa = buscarPorId(empresaId);
		Usuario usuario = usuarioRepositorio.findById(usuarioId)
				.orElseThrow(() -> new VerificarNuloExecao("Usuario com id " + usuarioId + " nao foi encontrado."));
		garantirColecoes(empresa);
		empresa.getUsuarios().add(usuario);
		return empresaRepositorio.save(empresa);
	}

	@Transactional
	public Empresa removerUsuario(Long empresaId, Long usuarioId) {
		Empresa empresa = buscarPorId(empresaId);
		garantirColecoes(empresa);
		boolean removido = empresa.getUsuarios().removeIf(usuario -> usuario.getId() != null && usuario.getId().equals(usuarioId));

		if (!removido) {
			throw new VerificarNuloExecao("Usuario com id " + usuarioId + " nao esta associado a empresa " + empresaId + ".");
		}

		return empresaRepositorio.save(empresa);
	}

	@Transactional
	public Empresa associarServico(Long empresaId, Long servicoId) {
		Empresa empresa = buscarPorId(empresaId);
		Servico servico = servicoRepositorio.findById(servicoId)
				.orElseThrow(() -> new VerificarNuloExecao("Servico com id " + servicoId + " nao foi encontrado."));
		garantirColecoes(empresa);
		empresa.getServicos().add(servico);
		return empresaRepositorio.save(empresa);
	}

	@Transactional
	public Empresa associarMercadoria(Long empresaId, Long mercadoriaId) {
		Empresa empresa = buscarPorId(empresaId);
		Mercadoria mercadoria = mercadoriaRepositorio.findById(mercadoriaId)
				.orElseThrow(() -> new VerificarNuloExecao("Mercadoria com id " + mercadoriaId + " nao foi encontrada."));
		garantirColecoes(empresa);
		empresa.getMercadorias().add(mercadoria);
		return empresaRepositorio.save(empresa);
	}

	@Transactional
	public Empresa associarVenda(Long empresaId, Long vendaId) {
		Empresa empresa = buscarPorId(empresaId);
		Venda venda = vendaRepositorio.findById(vendaId)
				.orElseThrow(() -> new VerificarNuloExecao("Venda com id " + vendaId + " nao foi encontrada."));
		garantirColecoes(empresa);
		empresa.getVendas().add(venda);
		return empresaRepositorio.save(empresa);
	}

	@Transactional(readOnly = true)
	public List<Telefone> listarTelefones(Long empresaId) {
		Empresa empresa = buscarPorId(empresaId);
		garantirColecoes(empresa);
		return empresa.getTelefones().stream().toList();
	}

	@Transactional(readOnly = true)
	public Telefone buscarTelefone(Long empresaId, Long telefoneId) {
		Empresa empresa = buscarPorId(empresaId);
		garantirColecoes(empresa);
		return empresa.getTelefones().stream()
				.filter(telefone -> telefone.getId() != null && telefone.getId().equals(telefoneId))
				.findFirst()
				.orElseThrow(() -> new VerificarNuloExecao(
						"Telefone com id " + telefoneId + " nao foi encontrado para a empresa " + empresaId + "."));
	}

	@Transactional
	public Telefone cadastrarTelefone(Long empresaId, Telefone telefone) {
		Empresa empresa = buscarPorId(empresaId);
		garantirColecoes(empresa);
		telefone.setId(null);
		Telefone telefoneSalvo = telefoneRepositorio.save(telefone);
		empresa.getTelefones().add(telefoneSalvo);
		empresaRepositorio.save(empresa);
		return telefoneSalvo;
	}

	@Transactional
	public Telefone atualizarTelefone(Long empresaId, Long telefoneId, Telefone atualizacao) {
		Telefone telefone = buscarTelefone(empresaId, telefoneId);

		if (textoValido(atualizacao.getDdd())) {
			telefone.setDdd(atualizacao.getDdd());
		}
		if (textoValido(atualizacao.getNumero())) {
			telefone.setNumero(atualizacao.getNumero());
		}

		empresaRepositorio.save(buscarPorId(empresaId));
		return telefone;
	}

	@Transactional
	public void excluirTelefone(Long empresaId, Long telefoneId) {
		Empresa empresa = buscarPorId(empresaId);
		garantirColecoes(empresa);
		boolean removido = empresa.getTelefones()
				.removeIf(telefone -> telefone.getId() != null && telefone.getId().equals(telefoneId));

		if (!removido) {
			throw new VerificarNuloExecao(
					"Telefone com id " + telefoneId + " nao foi encontrado para a empresa " + empresaId + ".");
		}

		empresaRepositorio.save(empresa);
	}

	@Transactional(readOnly = true)
	public Endereco buscarEndereco(Long empresaId) {
		Empresa empresa = buscarPorId(empresaId);

		if (empresa.getEndereco() == null) {
			throw new VerificarNuloExecao("A empresa " + empresaId + " nao possui endereco cadastrado.");
		}

		return empresa.getEndereco();
	}

	@Transactional
	public Endereco cadastrarEndereco(Long empresaId, Endereco endereco) {
		Empresa empresa = buscarPorId(empresaId);

		if (empresa.getEndereco() != null) {
			throw new IllegalArgumentException("A empresa " + empresaId + " ja possui endereco cadastrado.");
		}

		endereco.setId(null);
		empresa.setEndereco(endereco);
		empresaRepositorio.save(empresa);
		return empresa.getEndereco();
	}

	@Transactional
	public Endereco atualizarEndereco(Long empresaId, Endereco atualizacao) {
		Empresa empresa = buscarPorId(empresaId);

		if (empresa.getEndereco() == null) {
			throw new VerificarNuloExecao("A empresa " + empresaId + " nao possui endereco cadastrado.");
		}

		atualizarEnderecoExistente(empresa.getEndereco(), atualizacao);
		empresaRepositorio.save(empresa);
		return empresa.getEndereco();
	}

	@Transactional
	public void excluirEndereco(Long empresaId) {
		Empresa empresa = buscarPorId(empresaId);

		if (empresa.getEndereco() == null) {
			throw new VerificarNuloExecao("A empresa " + empresaId + " nao possui endereco cadastrado.");
		}

		empresa.setEndereco(null);
		empresaRepositorio.save(empresa);
	}

	private void prepararEmpresaParaPersistencia(Empresa empresa) {
		garantirColecoes(empresa);
		empresa.getTelefones().forEach(telefone -> telefone.setId(null));

		if (empresa.getEndereco() != null) {
			empresa.getEndereco().setId(null);
		}
	}

	private void garantirColecoes(Empresa empresa) {
		if (empresa.getTelefones() == null) {
			empresa.setTelefones(new HashSet<>());
		}
		if (empresa.getUsuarios() == null) {
			empresa.setUsuarios(new HashSet<>());
		}
		if (empresa.getMercadorias() == null) {
			empresa.setMercadorias(new HashSet<>());
		}
		if (empresa.getServicos() == null) {
			empresa.setServicos(new HashSet<>());
		}
		if (empresa.getVendas() == null) {
			empresa.setVendas(new HashSet<>());
		}
	}

	private void atualizarEnderecoExistente(Endereco enderecoAtual, Endereco atualizacao) {
		if (atualizacao == null) {
			return;
		}
		if (textoValido(atualizacao.getEstado())) {
			enderecoAtual.setEstado(atualizacao.getEstado());
		}
		if (textoValido(atualizacao.getCidade())) {
			enderecoAtual.setCidade(atualizacao.getCidade());
		}
		if (textoValido(atualizacao.getBairro())) {
			enderecoAtual.setBairro(atualizacao.getBairro());
		}
		if (textoValido(atualizacao.getRua())) {
			enderecoAtual.setRua(atualizacao.getRua());
		}
		if (textoValido(atualizacao.getNumero())) {
			enderecoAtual.setNumero(atualizacao.getNumero());
		}
		if (textoValido(atualizacao.getCodigoPostal())) {
			enderecoAtual.setCodigoPostal(atualizacao.getCodigoPostal());
		}
		if (textoValido(atualizacao.getInformacoesAdicionais())) {
			enderecoAtual.setInformacoesAdicionais(atualizacao.getInformacoesAdicionais());
		}
	}

	private boolean textoValido(String valor) {
		return valor != null && !valor.trim().isEmpty();
	}
}
