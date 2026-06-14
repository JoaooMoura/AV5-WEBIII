# AutoManager AV5

## Como rodar

Requisito: Docker Desktop instalado e aberto.

No Windows, execute:

```text
start.bat
```

O sistema estará disponível em:

```text
http://localhost:8080
```

Para conferir os containers:

```bash
docker compose ps
```

Para encerrar:

```text
stop.bat
```

## Login

```http
POST http://localhost:8080/auth/login
```

```json
{
  "nomeUsuario": "admin",
  "senha": "123456"
}
```

Nas outras requisições, envie o token retornado:

```text
Authorization: Bearer TOKEN
```

## Testes

```http
GET http://localhost:8080/api/empresas/1/clientes
GET http://localhost:8080/api/empresas/1/funcionarios
GET http://localhost:8080/api/empresas/1/catalogo
GET http://localhost:8080/api/empresas/1/vendas/itens?inicio=2020-01-01&fim=2030-01-01
GET http://localhost:8080/api/empresas/1/veiculos-atendidos
```

Resultado esperado: `200 OK`.

Sem token:

```http
GET http://localhost:8080/api/empresas/1/clientes
```

Resultado esperado: `401 Unauthorized`.

Empresa inexistente:

```http
GET http://localhost:8080/api/empresas/999/clientes
```

Resultado esperado: `404 Not Found`.

Período inválido:

```http
GET http://localhost:8080/api/empresas/1/vendas/itens?inicio=2030-01-01&fim=2020-01-01
```

Resultado esperado: `400 Bad Request`.

## Teste de isolamento

```bash
docker compose stop api-clientes-empresa
```

Clientes deve retornar `503`, mas catálogo e funcionários devem continuar retornando `200`.

Para iniciar novamente:

```bash
docker compose start api-clientes-empresa
```
