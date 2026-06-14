package com.autobots.automanager;

import java.util.Calendar;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.autobots.automanager.entidades.Cliente;
import com.autobots.automanager.entidades.Credencial;
import com.autobots.automanager.entidades.CredencialUsuarioSenha;
import com.autobots.automanager.entidades.Documento;
import com.autobots.automanager.entidades.Empresa;
import com.autobots.automanager.entidades.Endereco;
import com.autobots.automanager.entidades.Mercadoria;
import com.autobots.automanager.entidades.Servico;
import com.autobots.automanager.entidades.Telefone;
import com.autobots.automanager.entidades.Usuario;
import com.autobots.automanager.entidades.Veiculo;
import com.autobots.automanager.entidades.Venda;
import com.autobots.automanager.enumeracoes.PerfilUsuario;
import com.autobots.automanager.enumeracoes.TipoVeiculo;
import com.autobots.automanager.repositorios.ClienteRepositorio;
import com.autobots.automanager.repositorios.EmpresaRepositorio;
import com.autobots.automanager.repositorios.UsuarioRepositorio;
import com.autobots.automanager.servicos.EmpresaServico;
import com.autobots.automanager.servicos.VeiculoServico;
import com.autobots.automanager.servicos.VendaServico;

@SpringBootApplication
public class AutomanagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(AutomanagerApplication.class, args);
	}

	@Component
	public static class Runner implements ApplicationRunner {
		@Autowired
		public ClienteRepositorio repositorio;
		@Autowired
		public UsuarioRepositorio usuarioRepositorio;
		@Autowired
		public EmpresaRepositorio empresaRepositorio;
		@Autowired
		public PasswordEncoder codificadorSenha;
		@Autowired
		public EmpresaServico empresaServico;
		@Autowired
		public VeiculoServico veiculoServico;
		@Autowired
		public VendaServico vendaServico;

		@Override
		public void run(ApplicationArguments args) throws Exception {
			Calendar calendario = Calendar.getInstance();
			calendario.set(2002, 05, 15);

			if (repositorio.findAll().isEmpty()) {
				Cliente cliente = new Cliente();
				cliente.setNome("Pedro Alcântara de Bragança e Bourbon");
				cliente.setDataCadastro(Calendar.getInstance().getTime());
				cliente.setDataNascimento(calendario.getTime());
				cliente.setNomeSocial("Dom Pedro");

				Telefone telefone = new Telefone();
				telefone.setDdd("21");
				telefone.setNumero("981234576");
				cliente.getTelefones().add(telefone);

				Endereco endereco = new Endereco();
				endereco.setEstado("Rio de Janeiro");
				endereco.setCidade("Rio de Janeiro");
				endereco.setBairro("Copacabana");
				endereco.setRua("Perdão pela AV1 gersão");
				endereco.setNumero("1702");
				endereco.setCodigoPostal("22021001");
				endereco.setInformacoesAdicionais("Hotel Copacabana palace");
				cliente.setEndereco(endereco);

				Documento rg = new Documento();
				rg.setTipo("RG");
				rg.setNumero("1500");

				Documento cpf = new Documento();
				cpf.setTipo("RG");
				cpf.setNumero("00000000001");

				cliente.getDocumentos().add(rg);
				cliente.getDocumentos().add(cpf);

				repositorio.save(cliente);
			}

			cadastrarUsuarioInicial("Gerson penha", "admin", PerfilUsuario.ADMINISTRADOR);
			cadastrarUsuarioInicial("Enzo code", "gerente", PerfilUsuario.GERENTE);
			cadastrarUsuarioInicial("Enrico Miranda", "vendedor", PerfilUsuario.VENDEDOR);
			cadastrarUsuarioInicial("Moura", "cliente", PerfilUsuario.CLIENTE, "gswpiorcliente");
			cadastrarEmpresaInicial();
		}

		private void cadastrarEmpresaInicial() {
			if (!empresaRepositorio.findAll().isEmpty()) {
				return;
			}

			Date agora = Calendar.getInstance().getTime();

			Servico servico = new Servico();
			servico.setNome("Revisao preventiva");
			servico.setDescricao("Revisao preventiva do veiculo");
			servico.setValor(350.0);
			servico.setCadastro(agora);

			Mercadoria mercadoria = new Mercadoria();
			mercadoria.setNome("Filtro de oleo");
			mercadoria.setDescricao("Filtro de oleo automotivo");
			mercadoria.setValor(45.0);
			mercadoria.setQuantidade(20);
			mercadoria.setCadastro(agora);
			mercadoria.setFabricacao(agora);
			mercadoria.setValidade(agora);

			Empresa empresa = new Empresa();
			empresa.setRazaoSocial("Lulu santos ltda");
			empresa.setNomeFantasia("The Voice");
			empresa.setCadastro(agora);
			empresa.getServicos().add(servico);
			empresa.getMercadorias().add(mercadoria);

			Empresa cadastrada = empresaRepositorio.save(empresa);
			cadastrada.getUsuarios().addAll(usuarioRepositorio.findAll());
			Empresa atualizada = empresaRepositorio.save(cadastrada);
			cadastrarVendaInicial(atualizada, servico, mercadoria);
		}

		private void cadastrarVendaInicial(Empresa empresa, Servico servico, Mercadoria mercadoria) {
			Usuario cliente = buscarUsuarioPorPerfil(PerfilUsuario.CLIENTE);
			Usuario vendedor = buscarUsuarioPorPerfil(PerfilUsuario.VENDEDOR);

			Veiculo veiculo = new Veiculo();
			veiculo.setTipo(TipoVeiculo.SEDA);
			veiculo.setModelo("Corolla");
			veiculo.setPlaca("AV50001");
			veiculo.setProprietario(cliente);
			Veiculo veiculoSalvo = veiculoServico.cadastrar(veiculo);

			Venda venda = new Venda();
			venda.setIdentificacao("AV5-001");
			venda.setCadastro(Calendar.getInstance().getTime());
			venda.setCliente(cliente);
			venda.setFuncionario(vendedor);
			venda.setVeiculo(veiculoSalvo);
			venda.getServicos().add(servico);
			venda.getMercadorias().add(mercadoria);
			Venda vendaSalva = vendaServico.cadastrar(venda);
			empresaServico.associarVenda(empresa.getId(), vendaSalva.getId());
		}

		private Usuario buscarUsuarioPorPerfil(PerfilUsuario perfil) {
			return usuarioRepositorio.findAll().stream()
					.filter(usuario -> usuario.getPerfis().contains(perfil))
					.findFirst()
					.orElseThrow();
		}

		private void cadastrarUsuarioInicial(String nome, String nomeUsuario, PerfilUsuario perfil) {
			cadastrarUsuarioInicial(nome, nomeUsuario, perfil, "123456");
		}

		private void cadastrarUsuarioInicial(String nome, String nomeUsuario, PerfilUsuario perfil, String senha) {
			if (usuarioInicialExiste(nomeUsuario)) {
				return;
			}

			Usuario usuario = new Usuario();
			usuario.setNome(nome);
			usuario.getPerfis().add(perfil);

			CredencialUsuarioSenha credencial = new CredencialUsuarioSenha();
			credencial.setNomeUsuario(nomeUsuario);
			credencial.setSenha(codificadorSenha.encode(senha));
			credencial.setCriacao(Calendar.getInstance().getTime());
			credencial.setInativo(false);

			usuario.getCredenciais().add(credencial);
			usuarioRepositorio.save(usuario);
		}

		private boolean usuarioInicialExiste(String nomeUsuario) {
			for (Usuario usuario : usuarioRepositorio.findAll()) {
				for (Credencial credencial : usuario.getCredenciais()) {
					if (credencial instanceof CredencialUsuarioSenha) {
						CredencialUsuarioSenha usuarioSenha = (CredencialUsuarioSenha) credencial;

						if (usuarioSenha.getNomeUsuario().equals(nomeUsuario)) {
							return true;
						}
					}
				}
			}
			return false;
		}
	}

}
