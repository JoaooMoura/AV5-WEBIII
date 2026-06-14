package com.autobots.automanager.servicos;

import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.autobots.automanager.entidades.Credencial;
import com.autobots.automanager.entidades.CredencialCodigoBarra;
import com.autobots.automanager.entidades.CredencialUsuarioSenha;
import com.autobots.automanager.entidades.Documento;
import com.autobots.automanager.entidades.Email;
import com.autobots.automanager.entidades.Empresa;
import com.autobots.automanager.entidades.Endereco;
import com.autobots.automanager.entidades.Telefone;
import com.autobots.automanager.entidades.Usuario;
import com.autobots.automanager.entidades.Veiculo;
import com.autobots.automanager.entidades.Venda;
import com.autobots.automanager.excecoes.VerificarNuloExecao;
import com.autobots.automanager.repositorios.CredencialRepositorio;
import com.autobots.automanager.repositorios.DocumentoRepositorio;
import com.autobots.automanager.repositorios.EmailRepositorio;
import com.autobots.automanager.repositorios.EmpresaRepositorio;
import com.autobots.automanager.repositorios.TelefoneRepositorio;
import com.autobots.automanager.repositorios.UsuarioRepositorio;
import com.autobots.automanager.repositorios.VeiculoRepositorio;
import com.autobots.automanager.repositorios.VendaRepositorio;

@Service
public class UsuarioServico {
	private final UsuarioRepositorio usuarioRepositorio;
	private final DocumentoRepositorio documentoRepositorio;
	private final TelefoneRepositorio telefoneRepositorio;
	private final EmailRepositorio emailRepositorio;
	private final CredencialRepositorio credencialRepositorio;
	private final VeiculoRepositorio veiculoRepositorio;
	private final EmpresaRepositorio empresaRepositorio;
	private final VendaRepositorio vendaRepositorio;
	private final PasswordEncoder codificadorSenha;
	private final AutorizacaoServico autorizacaoServico;

	public UsuarioServico(UsuarioRepositorio usuarioRepositorio, DocumentoRepositorio documentoRepositorio,
			TelefoneRepositorio telefoneRepositorio, EmailRepositorio emailRepositorio,
			CredencialRepositorio credencialRepositorio, VeiculoRepositorio veiculoRepositorio,
			EmpresaRepositorio empresaRepositorio, VendaRepositorio vendaRepositorio, PasswordEncoder codificadorSenha,
			AutorizacaoServico autorizacaoServico) {
		this.usuarioRepositorio = usuarioRepositorio;
		this.documentoRepositorio = documentoRepositorio;
		this.telefoneRepositorio = telefoneRepositorio;
		this.emailRepositorio = emailRepositorio;
		this.credencialRepositorio = credencialRepositorio;
		this.veiculoRepositorio = veiculoRepositorio;
		this.empresaRepositorio = empresaRepositorio;
		this.vendaRepositorio = vendaRepositorio;
		this.codificadorSenha = codificadorSenha;
		this.autorizacaoServico = autorizacaoServico;
	}

	@Transactional(readOnly = true)
	public List<Usuario> listar() {
		return autorizacaoServico.filtrarUsuarios(usuarioRepositorio.findAll());
	}

	@Transactional(readOnly = true)
	public Usuario buscarPorId(Long usuarioId) {
		return usuarioRepositorio.findById(usuarioId)
				.orElseThrow(() -> new VerificarNuloExecao("Usuario com id " + usuarioId + " nao foi encontrado."));
	}

	@Transactional
	public Usuario cadastrar(Usuario usuario) {
		usuario.setId(null);
		prepararUsuarioParaPersistencia(usuario);
		return usuarioRepositorio.save(usuario);
	}

	@Transactional
	public Usuario atualizar(Long usuarioId, Usuario atualizacao) {
		Usuario usuario = buscarPorId(usuarioId);

		if (textoValido(atualizacao.getNome())) {
			usuario.setNome(atualizacao.getNome());
		}
		if (textoValido(atualizacao.getNomeSocial())) {
			usuario.setNomeSocial(atualizacao.getNomeSocial());
		}
		if (atualizacao.getPerfis() != null && !atualizacao.getPerfis().isEmpty()) {
			usuario.setPerfis(atualizacao.getPerfis());
		}
		if (atualizacao.getEndereco() != null) {
			if (usuario.getEndereco() == null) {
				atualizacao.getEndereco().setId(null);
				usuario.setEndereco(atualizacao.getEndereco());
			} else {
				atualizarEnderecoExistente(usuario.getEndereco(), atualizacao.getEndereco());
			}
		}
		if (atualizacao.getTelefones() != null && !atualizacao.getTelefones().isEmpty()) {
			atualizacao.getTelefones().forEach(telefone -> telefone.setId(null));
			usuario.setTelefones(atualizacao.getTelefones());
		}
		if (atualizacao.getDocumentos() != null && !atualizacao.getDocumentos().isEmpty()) {
			atualizacao.getDocumentos().forEach(documento -> documento.setId(null));
			usuario.setDocumentos(atualizacao.getDocumentos());
		}
		if (atualizacao.getEmails() != null && !atualizacao.getEmails().isEmpty()) {
			atualizacao.getEmails().forEach(email -> email.setId(null));
			usuario.setEmails(atualizacao.getEmails());
		}

		return usuarioRepositorio.save(usuario);
	}

	@Transactional
	public void excluir(Long usuarioId) {
		Usuario usuario = buscarPorId(usuarioId);
		desvincularEmpresas(usuario);
		desvincularVeiculos(usuario);
		desvincularVendas(usuario);
		garantirColecoes(usuario);
		usuario.getMercadorias().clear();
		usuarioRepositorio.save(usuario);
		usuarioRepositorio.delete(usuario);
	}

	@Transactional(readOnly = true)
	public List<Documento> listarDocumentos(Long usuarioId) {
		Usuario usuario = buscarPorId(usuarioId);
		garantirColecoes(usuario);
		return usuario.getDocumentos().stream().toList();
	}

	@Transactional(readOnly = true)
	public Documento buscarDocumento(Long usuarioId, Long documentoId) {
		Usuario usuario = buscarPorId(usuarioId);
		garantirColecoes(usuario);
		return usuario.getDocumentos().stream()
				.filter(documento -> documento.getId() != null && documento.getId().equals(documentoId))
				.findFirst()
				.orElseThrow(() -> new VerificarNuloExecao(
						"Documento com id " + documentoId + " nao foi encontrado para o usuario " + usuarioId + "."));
	}

	@Transactional
	public Documento cadastrarDocumento(Long usuarioId, Documento documento) {
		Usuario usuario = buscarPorId(usuarioId);
		garantirColecoes(usuario);
		documento.setId(null);
		Documento documentoSalvo = documentoRepositorio.save(documento);
		usuario.getDocumentos().add(documentoSalvo);
		usuarioRepositorio.save(usuario);
		return documentoSalvo;
	}

	@Transactional
	public Documento atualizarDocumento(Long usuarioId, Long documentoId, Documento atualizacao) {
		Documento documento = buscarDocumento(usuarioId, documentoId);

		if (textoValido(atualizacao.getTipo())) {
			documento.setTipo(atualizacao.getTipo());
		}
		if (textoValido(atualizacao.getNumero())) {
			documento.setNumero(atualizacao.getNumero());
		}

		usuarioRepositorio.save(buscarPorId(usuarioId));
		return documento;
	}

	@Transactional
	public void excluirDocumento(Long usuarioId, Long documentoId) {
		Usuario usuario = buscarPorId(usuarioId);
		garantirColecoes(usuario);
		boolean removido = usuario.getDocumentos()
				.removeIf(documento -> documento.getId() != null && documento.getId().equals(documentoId));

		if (!removido) {
			throw new VerificarNuloExecao(
					"Documento com id " + documentoId + " nao foi encontrado para o usuario " + usuarioId + ".");
		}

		usuarioRepositorio.save(usuario);
	}

	@Transactional(readOnly = true)
	public List<Telefone> listarTelefones(Long usuarioId) {
		Usuario usuario = buscarPorId(usuarioId);
		garantirColecoes(usuario);
		return usuario.getTelefones().stream().toList();
	}

	@Transactional(readOnly = true)
	public Telefone buscarTelefone(Long usuarioId, Long telefoneId) {
		Usuario usuario = buscarPorId(usuarioId);
		garantirColecoes(usuario);
		return usuario.getTelefones().stream()
				.filter(telefone -> telefone.getId() != null && telefone.getId().equals(telefoneId))
				.findFirst()
				.orElseThrow(() -> new VerificarNuloExecao(
						"Telefone com id " + telefoneId + " nao foi encontrado para o usuario " + usuarioId + "."));
	}

	@Transactional
	public Telefone cadastrarTelefone(Long usuarioId, Telefone telefone) {
		Usuario usuario = buscarPorId(usuarioId);
		garantirColecoes(usuario);
		telefone.setId(null);
		Telefone telefoneSalvo = telefoneRepositorio.save(telefone);
		usuario.getTelefones().add(telefoneSalvo);
		usuarioRepositorio.save(usuario);
		return telefoneSalvo;
	}

	@Transactional
	public Telefone atualizarTelefone(Long usuarioId, Long telefoneId, Telefone atualizacao) {
		Telefone telefone = buscarTelefone(usuarioId, telefoneId);

		if (textoValido(atualizacao.getDdd())) {
			telefone.setDdd(atualizacao.getDdd());
		}
		if (textoValido(atualizacao.getNumero())) {
			telefone.setNumero(atualizacao.getNumero());
		}

		usuarioRepositorio.save(buscarPorId(usuarioId));
		return telefone;
	}

	@Transactional
	public void excluirTelefone(Long usuarioId, Long telefoneId) {
		Usuario usuario = buscarPorId(usuarioId);
		garantirColecoes(usuario);
		boolean removido = usuario.getTelefones()
				.removeIf(telefone -> telefone.getId() != null && telefone.getId().equals(telefoneId));

		if (!removido) {
			throw new VerificarNuloExecao(
					"Telefone com id " + telefoneId + " nao foi encontrado para o usuario " + usuarioId + ".");
		}

		usuarioRepositorio.save(usuario);
	}

	@Transactional(readOnly = true)
	public List<Email> listarEmails(Long usuarioId) {
		Usuario usuario = buscarPorId(usuarioId);
		garantirColecoes(usuario);
		return usuario.getEmails().stream().toList();
	}

	@Transactional(readOnly = true)
	public Email buscarEmail(Long usuarioId, Long emailId) {
		Usuario usuario = buscarPorId(usuarioId);
		garantirColecoes(usuario);
		return usuario.getEmails().stream()
				.filter(email -> email.getId() != null && email.getId().equals(emailId))
				.findFirst()
				.orElseThrow(() -> new VerificarNuloExecao(
						"Email com id " + emailId + " nao foi encontrado para o usuario " + usuarioId + "."));
	}

	@Transactional
	public Email cadastrarEmail(Long usuarioId, Email email) {
		Usuario usuario = buscarPorId(usuarioId);
		garantirColecoes(usuario);
		email.setId(null);
		Email emailSalvo = emailRepositorio.save(email);
		usuario.getEmails().add(emailSalvo);
		usuarioRepositorio.save(usuario);
		return emailSalvo;
	}

	@Transactional
	public Email atualizarEmail(Long usuarioId, Long emailId, Email atualizacao) {
		Email email = buscarEmail(usuarioId, emailId);

		if (textoValido(atualizacao.getEndereco())) {
			email.setEndereco(atualizacao.getEndereco());
		}

		usuarioRepositorio.save(buscarPorId(usuarioId));
		return email;
	}

	@Transactional
	public void excluirEmail(Long usuarioId, Long emailId) {
		Usuario usuario = buscarPorId(usuarioId);
		garantirColecoes(usuario);
		boolean removido = usuario.getEmails()
				.removeIf(email -> email.getId() != null && email.getId().equals(emailId));

		if (!removido) {
			throw new VerificarNuloExecao(
					"Email com id " + emailId + " nao foi encontrado para o usuario " + usuarioId + ".");
		}

		usuarioRepositorio.save(usuario);
	}

	@Transactional(readOnly = true)
	public Endereco buscarEndereco(Long usuarioId) {
		Usuario usuario = buscarPorId(usuarioId);

		if (usuario.getEndereco() == null) {
			throw new VerificarNuloExecao("O usuario " + usuarioId + " nao possui endereco cadastrado.");
		}

		return usuario.getEndereco();
	}

	@Transactional
	public Endereco cadastrarEndereco(Long usuarioId, Endereco endereco) {
		Usuario usuario = buscarPorId(usuarioId);

		if (usuario.getEndereco() != null) {
			throw new IllegalArgumentException("O usuario " + usuarioId + " ja possui endereco cadastrado.");
		}

		endereco.setId(null);
		usuario.setEndereco(endereco);
		usuarioRepositorio.save(usuario);
		return usuario.getEndereco();
	}

	@Transactional
	public Endereco atualizarEndereco(Long usuarioId, Endereco atualizacao) {
		Usuario usuario = buscarPorId(usuarioId);

		if (usuario.getEndereco() == null) {
			throw new VerificarNuloExecao("O usuario " + usuarioId + " nao possui endereco cadastrado.");
		}

		atualizarEnderecoExistente(usuario.getEndereco(), atualizacao);
		usuarioRepositorio.save(usuario);
		return usuario.getEndereco();
	}

	@Transactional
	public void excluirEndereco(Long usuarioId) {
		Usuario usuario = buscarPorId(usuarioId);

		if (usuario.getEndereco() == null) {
			throw new VerificarNuloExecao("O usuario " + usuarioId + " nao possui endereco cadastrado.");
		}

		usuario.setEndereco(null);
		usuarioRepositorio.save(usuario);
	}

	@Transactional(readOnly = true)
	public Credencial buscarCredencial(Long usuarioId, Long credencialId) {
		Usuario usuario = buscarPorId(usuarioId);
		garantirColecoes(usuario);

		return usuario.getCredenciais().stream()
				.filter(credencial -> credencial.getId() != null && credencial.getId().equals(credencialId))
				.findFirst()
				.orElseThrow(() -> new VerificarNuloExecao(
						"Credencial com id " + credencialId + " nao foi encontrada para o usuario " + usuarioId + "."));
	}

	@Transactional(readOnly = true)
	public List<Credencial> listarCredenciais(Long usuarioId) {
		Usuario usuario = buscarPorId(usuarioId);
		garantirColecoes(usuario);
		return usuario.getCredenciais().stream().toList();
	}

	@Transactional
	public CredencialUsuarioSenha cadastrarCredencialUsuarioSenha(Long usuarioId, CredencialUsuarioSenha credencial) {
		Usuario usuario = buscarPorId(usuarioId);
		garantirColecoes(usuario);
		prepararCredencial(credencial);
		CredencialUsuarioSenha credencialSalva = credencialRepositorio.save(credencial);
		usuario.getCredenciais().add(credencialSalva);
		usuarioRepositorio.save(usuario);
		return credencialSalva;
	}

	@Transactional
	public CredencialCodigoBarra cadastrarCredencialCodigoBarra(Long usuarioId, CredencialCodigoBarra credencial) {
		Usuario usuario = buscarPorId(usuarioId);
		garantirColecoes(usuario);
		prepararCredencial(credencial);
		CredencialCodigoBarra credencialSalva = credencialRepositorio.save(credencial);
		usuario.getCredenciais().add(credencialSalva);
		usuarioRepositorio.save(usuario);
		return credencialSalva;
	}

	@Transactional
	public CredencialUsuarioSenha atualizarCredencialUsuarioSenha(Long usuarioId, Long credencialId, CredencialUsuarioSenha atualizacao) {
		Credencial credencial = buscarCredencial(usuarioId, credencialId);

		if (!(credencial instanceof CredencialUsuarioSenha)) {
			throw new IllegalArgumentException("A credencial informada nao e do tipo usuario e senha.");
		}

		CredencialUsuarioSenha usuarioSenha = (CredencialUsuarioSenha) credencial;
		atualizarDadosCredencial(usuarioSenha, atualizacao);

		if (textoValido(atualizacao.getNomeUsuario())) {
			usuarioSenha.setNomeUsuario(atualizacao.getNomeUsuario());
		}
		if (textoValido(atualizacao.getSenha())) {
			usuarioSenha.setSenha(criptografarSenha(atualizacao.getSenha()));
		}

		usuarioRepositorio.save(buscarPorId(usuarioId));
		return usuarioSenha;
	}

	@Transactional
	public CredencialCodigoBarra atualizarCredencialCodigoBarra(Long usuarioId, Long credencialId, CredencialCodigoBarra atualizacao) {
		Credencial credencial = buscarCredencial(usuarioId, credencialId);

		if (!(credencial instanceof CredencialCodigoBarra)) {
			throw new IllegalArgumentException("A credencial informada nao e do tipo codigo de barra.");
		}

		CredencialCodigoBarra codigoBarra = (CredencialCodigoBarra) credencial;
		atualizarDadosCredencial(codigoBarra, atualizacao);
		codigoBarra.setCodigo(atualizacao.getCodigo());
		usuarioRepositorio.save(buscarPorId(usuarioId));
		return codigoBarra;
	}

	@Transactional
	public void excluirCredencial(Long usuarioId, Long credencialId) {
		Usuario usuario = buscarPorId(usuarioId);
		garantirColecoes(usuario);
		boolean removido = usuario.getCredenciais()
				.removeIf(credencial -> credencial.getId() != null && credencial.getId().equals(credencialId));

		if (!removido) {
			throw new VerificarNuloExecao(
					"Credencial com id " + credencialId + " nao foi encontrada para o usuario " + usuarioId + ".");
		}

		usuarioRepositorio.save(usuario);
	}

	private void desvincularEmpresas(Usuario usuario) {
		List<Empresa> empresas = empresaRepositorio.findAll();
		empresas.forEach(empresa -> {
			if (empresa.getUsuarios() != null) {
				boolean removido = empresa.getUsuarios()
						.removeIf(associado -> associado.getId() != null && associado.getId().equals(usuario.getId()));

				if (removido) {
					empresaRepositorio.save(empresa);
				}
			}
		});
	}

	private void desvincularVeiculos(Usuario usuario) {
		garantirColecoes(usuario);
		new HashSet<>(usuario.getVeiculos()).forEach(veiculo -> {
			if (veiculo.getId() != null) {
				Veiculo veiculoSalvo = veiculoRepositorio.findById(veiculo.getId()).orElse(null);

				if (veiculoSalvo != null && mesmoUsuario(veiculoSalvo.getProprietario(), usuario)) {
					veiculoSalvo.setProprietario(null);
					veiculoRepositorio.save(veiculoSalvo);
				}
			}
		});
		usuario.getVeiculos().clear();
	}

	private void desvincularVendas(Usuario usuario) {
		List<Venda> vendas = vendaRepositorio.findAll();
		vendas.forEach(venda -> {
			boolean alterada = false;

			if (mesmoUsuario(venda.getCliente(), usuario)) {
				venda.setCliente(null);
				alterada = true;
			}
			if (mesmoUsuario(venda.getFuncionario(), usuario)) {
				venda.setFuncionario(null);
				alterada = true;
			}
			if (alterada) {
				vendaRepositorio.save(venda);
			}
		});
		garantirColecoes(usuario);
		usuario.getVendas().clear();
	}

	private boolean mesmoUsuario(Usuario atual, Usuario esperado) {
		return atual != null && atual.getId() != null && atual.getId().equals(esperado.getId());
	}

	private void prepararUsuarioParaPersistencia(Usuario usuario) {
		garantirColecoes(usuario);
		usuario.getDocumentos().forEach(documento -> documento.setId(null));
		usuario.getTelefones().forEach(telefone -> telefone.setId(null));
		usuario.getEmails().forEach(email -> email.setId(null));
		usuario.getCredenciais().forEach(this::prepararCredencial);

		if (usuario.getEndereco() != null) {
			usuario.getEndereco().setId(null);
		}
	}

	private void prepararCredencial(Credencial credencial) {
		credencial.setId(null);

		if (credencial.getCriacao() == null) {
			credencial.setCriacao(new Date());
		}
		if (credencial instanceof CredencialUsuarioSenha) {
			CredencialUsuarioSenha usuarioSenha = (CredencialUsuarioSenha) credencial;

			if (textoValido(usuarioSenha.getSenha())) {
				usuarioSenha.setSenha(criptografarSenha(usuarioSenha.getSenha()));
			}
		}
	}

	private String criptografarSenha(String senha) {
		if (senha.startsWith("$2a$") || senha.startsWith("$2b$") || senha.startsWith("$2y$")) {
			return senha;
		}
		return codificadorSenha.encode(senha);
	}

	private void atualizarDadosCredencial(Credencial credencial, Credencial atualizacao) {
		if (atualizacao.getCriacao() != null) {
			credencial.setCriacao(atualizacao.getCriacao());
		}
		if (atualizacao.getUltimoAcesso() != null) {
			credencial.setUltimoAcesso(atualizacao.getUltimoAcesso());
		}
		credencial.setInativo(atualizacao.isInativo());
	}

	private void garantirColecoes(Usuario usuario) {
		if (usuario.getPerfis() == null) {
			usuario.setPerfis(new HashSet<>());
		}
		if (usuario.getTelefones() == null) {
			usuario.setTelefones(new HashSet<>());
		}
		if (usuario.getDocumentos() == null) {
			usuario.setDocumentos(new HashSet<>());
		}
		if (usuario.getEmails() == null) {
			usuario.setEmails(new HashSet<>());
		}
		if (usuario.getCredenciais() == null) {
			usuario.setCredenciais(new HashSet<>());
		}
		if (usuario.getMercadorias() == null) {
			usuario.setMercadorias(new HashSet<>());
		}
		if (usuario.getVendas() == null) {
			usuario.setVendas(new HashSet<>());
		}
		if (usuario.getVeiculos() == null) {
			usuario.setVeiculos(new HashSet<>());
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
