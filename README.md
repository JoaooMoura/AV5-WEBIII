# AutoBots Web III - AV5

A AV5 separa as cinco APIs de consulta em microsserviços com regras de negócio, validação JWT e autorização próprias.

## Arquitetura

```text
Cliente externo
    |
automanager-gateway
    |-- api-clientes-empresa
    |-- api-funcionarios-empresa
    |-- api-catalogo-empresa
    |-- api-vendas-periodo
    `-- api-veiculos-atendidos

automanager -> login, CRUDs e gravações
```

O `automanager` mantém as funcionalidades da AV1 até a AV4:

- login;
- CRUDs;
- HATEOAS;
- gravação e atualização dos dados;
- opções básicas do sistema.

Cada microsserviço da AV5:

- possui controllers, DTOs, entidades, repositórios e services próprios;
- executa sua própria regra de negócio;
- possui filtro e validação JWT próprios;
- aplica autorização localmente;
- possui tratamento de exceções próprio;
- consulta diretamente o banco persistente;
- não chama o `automanager` por HTTP.

O Gateway:

- é a entrada externa na porta `8080`;
- encaminha o login ao `automanager`;
- valida localmente assinatura e expiração do JWT;
- encaminha cada rota AV5 ao microsserviço responsável;
- continua encaminhando os CRUDs antigos ao `automanager`.

## Isolamento

Os microsserviços são independentes em execução:

- se `api-clientes-empresa` cair, somente a rota de clientes retorna `503`;
- os demais microsserviços continuam funcionando;
- se o `automanager` cair, login e CRUDs ficam indisponíveis;
- com um JWT já emitido, as APIs AV5 continuam funcionando.

O projeto usa um H2 persistente compartilhado em `~/automanager-av5`. Isso mantém as consultas atualizadas com os CRUDs sem exigir mensageria ou replicação de dados. Um banco separado por microsserviço exigiria sincronização entre bases, fora do escopo apresentado na disciplina.

## Projetos e portas

| Projeto | Porta | Responsabilidade |
|---|---:|---|
| `automanager-gateway` | 8080 | Entrada externa e validação JWT |
| `automanager` | 8081 | Login, CRUDs, HATEOAS e gravações |
| `api-clientes-empresa` | 8082 | Clientes por empresa |
| `api-funcionarios-empresa` | 8083 | Funcionários por empresa |
| `api-catalogo-empresa` | 8084 | Serviços e mercadorias por empresa |
| `api-vendas-periodo` | 8085 | Itens vendidos por empresa e período |
| `api-veiculos-atendidos` | 8086 | Veículos atendidos por empresa |

## Perfis iniciais

| Usuário | Senha | Perfil |
|---|---|---|
| `admin` | `123456` | ADMINISTRADOR |
| `gerente` | `123456` | GERENTE |
| `vendedor` | `123456` | VENDEDOR |
| `cliente` | `gswpiorcliente` | CLIENTE |

ADMINISTRADOR acessa todas as empresas. GERENTE acessa empresas associadas. VENDEDOR e CLIENTE recebem `403 Forbidden` nas APIs da AV5.

## Endpoints externos

```text
POST /auth/login
GET /api/empresas/{empresaId}/clientes
GET /api/empresas/{empresaId}/funcionarios
GET /api/empresas/{empresaId}/catalogo
GET /api/empresas/{empresaId}/vendas/itens?inicio=2026-01-01&fim=2026-12-31
GET /api/empresas/{empresaId}/veiculos-atendidos
```

## Execução com Docker

Pré-requisito: Docker Desktop instalado e iniciado.

No Windows, execute:

```text
start.bat
```

O arquivo constrói uma imagem para cada projeto, inicia os sete containers, aguarda os serviços necessários e mantém os dados em um volume Docker.

Para conferir os containers:

```bash
docker compose ps
```

Para acompanhar os logs:

```bash
docker compose logs -f
```

Para encerrar:

```text
stop.bat
```

O Gateway ficará disponível em `http://localhost:8080`.

## Compilação

Execute `./mvnw clean install` em:

```text
automanager
api-clientes-empresa
api-funcionarios-empresa
api-catalogo-empresa
api-vendas-periodo
api-veiculos-atendidos
automanager-gateway
```

No Windows, use `mvnw.cmd`.

## Execução

Na primeira execução, inicie o `automanager` antes das APIs para criar o banco persistente. Depois inicie as cinco APIs e o Gateway.

```text
1. automanager
2. api-clientes-empresa
3. api-funcionarios-empresa
4. api-catalogo-empresa
5. api-vendas-periodo
6. api-veiculos-atendidos
7. automanager-gateway
```

Login pelo Gateway:

```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"nomeUsuario":"admin","senha":"123456"}'
```

Uso do token:

```bash
curl http://localhost:8080/api/empresas/1/clientes \
  -H "Authorization: Bearer TOKEN_AQUI"
```

Consulte `TESTES_AV5.md` para os testes completos.
