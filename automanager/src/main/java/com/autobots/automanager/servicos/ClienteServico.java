package com.autobots.automanager.servicos;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.autobots.automanager.entidades.Cliente;
import com.autobots.automanager.entidades.Documento;
import com.autobots.automanager.entidades.Endereco;
import com.autobots.automanager.entidades.Telefone;
import com.autobots.automanager.excecoes.VerificarNuloExecao;
import com.autobots.automanager.repositorios.ClienteRepositorio;
import com.autobots.automanager.repositorios.DocumentoRepositorio;
import com.autobots.automanager.repositorios.TelefoneRepositorio;

@Service
public class ClienteServico {

    private final ClienteRepositorio clienteRepositorio;
    private final DocumentoRepositorio documentoRepositorio;
    private final TelefoneRepositorio telefoneRepositorio;

    public ClienteServico(ClienteRepositorio clienteRepositorio, DocumentoRepositorio documentoRepositorio,
            TelefoneRepositorio telefoneRepositorio) {
        this.clienteRepositorio = clienteRepositorio;
        this.documentoRepositorio = documentoRepositorio;
        this.telefoneRepositorio = telefoneRepositorio;
    }

    @Transactional(readOnly = true)
    public List<Cliente> listar() {
        return clienteRepositorio.findAll();
    }

    @Transactional(readOnly = true)
    public Cliente buscarPorId(Long clienteId) {
        return clienteRepositorio.findById(clienteId)
                .orElseThrow(() -> new VerificarNuloExecao(
                        "Cliente com id " + clienteId + " não foi encontrado."));
    }

    @Transactional
    public Cliente cadastrar(Cliente cliente) {
        prepararClienteParaPersistencia(cliente);
        cliente.setId(null);

        if (cliente.getDataCadastro() == null) {
            cliente.setDataCadastro(new Date());
        }

        return clienteRepositorio.save(cliente);
    }

    @Transactional
    public Cliente atualizar(Long clienteId, Cliente atualizacao) {
        Cliente cliente = buscarPorId(clienteId);

        if (textoValido(atualizacao.getNome())) {
            cliente.setNome(atualizacao.getNome());
        }
        if (textoValido(atualizacao.getNomeSocial())) {
            cliente.setNomeSocial(atualizacao.getNomeSocial());
        }
        if (atualizacao.getDataNascimento() != null) {
            cliente.setDataNascimento(atualizacao.getDataNascimento());
        }

        return clienteRepositorio.save(cliente);
    }

    @Transactional
    public void excluir(Long clienteId) {
        Cliente cliente = buscarPorId(clienteId);
        clienteRepositorio.delete(cliente);
    }

    @Transactional(readOnly = true)
    public List<Documento> listarDocumentos(Long clienteId) {
        Cliente cliente = buscarPorId(clienteId);
        garantirColecoes(cliente);
        return cliente.getDocumentos();
    }

    @Transactional(readOnly = true)
    public Documento buscarDocumento(Long clienteId, Long documentoId) {
        Cliente cliente = buscarPorId(clienteId);
        garantirColecoes(cliente);

        return cliente.getDocumentos().stream()
                .filter(documento -> documento.getId() != null && documento.getId().equals(documentoId))
                .findFirst()
                .orElseThrow(() -> new VerificarNuloExecao(
                        "Documento com id " + documentoId + " não foi encontrado para o cliente " + clienteId + "."));
    }

    @Transactional
    public Documento cadastrarDocumento(Long clienteId, Documento documento) {
        Cliente cliente = buscarPorId(clienteId);
        garantirColecoes(cliente);

        documento.setId(null);
        Documento documentoSalvo = documentoRepositorio.save(documento);
        cliente.getDocumentos().add(documentoSalvo);
        clienteRepositorio.save(cliente);

        return documentoSalvo;
    }

    @Transactional
    public Documento atualizarDocumento(Long clienteId, Long documentoId, Documento atualizacao) {
        Cliente cliente = buscarPorId(clienteId);
        garantirColecoes(cliente);

        Documento documento = buscarDocumento(clienteId, documentoId);

        if (textoValido(atualizacao.getTipo())) {
            documento.setTipo(atualizacao.getTipo());
        }
        if (textoValido(atualizacao.getNumero())) {
            documento.setNumero(atualizacao.getNumero());
        }

        clienteRepositorio.save(cliente);
        return documento;
    }

    @Transactional
    public void excluirDocumento(Long clienteId, Long documentoId) {
        Cliente cliente = buscarPorId(clienteId);
        garantirColecoes(cliente);

        boolean removido = cliente.getDocumentos()
                .removeIf(documento -> documento.getId() != null && documento.getId().equals(documentoId));

        if (!removido) {
            throw new VerificarNuloExecao(
                    "Documento com id " + documentoId + " não foi encontrado para o cliente " + clienteId + ".");
        }

        clienteRepositorio.save(cliente);
    }

    @Transactional(readOnly = true)
    public List<Telefone> listarTelefones(Long clienteId) {
        Cliente cliente = buscarPorId(clienteId);
        garantirColecoes(cliente);
        return cliente.getTelefones();
    }

    @Transactional(readOnly = true)
    public Telefone buscarTelefone(Long clienteId, Long telefoneId) {
        Cliente cliente = buscarPorId(clienteId);
        garantirColecoes(cliente);

        return cliente.getTelefones().stream()
                .filter(telefone -> telefone.getId() != null && telefone.getId().equals(telefoneId))
                .findFirst()
                .orElseThrow(() -> new VerificarNuloExecao(
                        "Telefone com id " + telefoneId + " não foi encontrado para o cliente " + clienteId + "."));
    }

    @Transactional
    public Telefone cadastrarTelefone(Long clienteId, Telefone telefone) {
        Cliente cliente = buscarPorId(clienteId);
        garantirColecoes(cliente);

        telefone.setId(null);
        Telefone telefoneSalvo = telefoneRepositorio.save(telefone);
        cliente.getTelefones().add(telefoneSalvo);
        clienteRepositorio.save(cliente);

        return telefoneSalvo;
    }

    @Transactional
    public Telefone atualizarTelefone(Long clienteId, Long telefoneId, Telefone atualizacao) {
        Cliente cliente = buscarPorId(clienteId);
        garantirColecoes(cliente);

        Telefone telefone = buscarTelefone(clienteId, telefoneId);

        if (textoValido(atualizacao.getDdd())) {
            telefone.setDdd(atualizacao.getDdd());
        }
        if (textoValido(atualizacao.getNumero())) {
            telefone.setNumero(atualizacao.getNumero());
        }

        clienteRepositorio.save(cliente);
        return telefone;
    }

    @Transactional
    public void excluirTelefone(Long clienteId, Long telefoneId) {
        Cliente cliente = buscarPorId(clienteId);
        garantirColecoes(cliente);

        boolean removido = cliente.getTelefones()
                .removeIf(telefone -> telefone.getId() != null && telefone.getId().equals(telefoneId));

        if (!removido) {
            throw new VerificarNuloExecao(
                    "Telefone com id " + telefoneId + " não foi encontrado para o cliente " + clienteId + ".");
        }

        clienteRepositorio.save(cliente);
    }

    @Transactional(readOnly = true)
    public Endereco buscarEndereco(Long clienteId) {
        Cliente cliente = buscarPorId(clienteId);

        if (cliente.getEndereco() == null) {
            throw new VerificarNuloExecao(
                    "O cliente " + clienteId + " não possui endereço cadastrado.");
        }

        return cliente.getEndereco();
    }

    @Transactional
    public Endereco cadastrarEndereco(Long clienteId, Endereco endereco) {
        Cliente cliente = buscarPorId(clienteId);

        if (cliente.getEndereco() != null) {
            throw new IllegalArgumentException(
                    "O cliente " + clienteId + " já possui endereço cadastrado.");
        }

        endereco.setId(null);
        cliente.setEndereco(endereco);
        clienteRepositorio.save(cliente);

        return cliente.getEndereco();
    }

    @Transactional
    public Endereco atualizarEndereco(Long clienteId, Endereco atualizacao) {
        Cliente cliente = buscarPorId(clienteId);

        if (cliente.getEndereco() == null) {
            throw new VerificarNuloExecao(
                    "O cliente " + clienteId + " não possui endereço cadastrado.");
        }

        atualizarEnderecoExistente(cliente.getEndereco(), atualizacao);
        clienteRepositorio.save(cliente);

        return cliente.getEndereco();
    }

    @Transactional
    public void excluirEndereco(Long clienteId) {
        Cliente cliente = buscarPorId(clienteId);

        if (cliente.getEndereco() == null) {
            throw new VerificarNuloExecao(
                    "O cliente " + clienteId + " não possui endereço cadastrado.");
        }

        cliente.setEndereco(null);
        clienteRepositorio.save(cliente);
    }

    private void prepararClienteParaPersistencia(Cliente cliente) {
        garantirColecoes(cliente);

        cliente.getDocumentos().forEach(documento -> documento.setId(null));
        cliente.getTelefones().forEach(telefone -> telefone.setId(null));

        if (cliente.getEndereco() != null) {
            cliente.getEndereco().setId(null);
        }
    }

    private void garantirColecoes(Cliente cliente) {
        if (cliente.getDocumentos() == null) {
            cliente.setDocumentos(new ArrayList<>());
        }
        if (cliente.getTelefones() == null) {
            cliente.setTelefones(new ArrayList<>());
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
