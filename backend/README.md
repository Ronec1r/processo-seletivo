# Backend — SGS (Sistema de Gestão de Solicitações)

API REST desenvolvida com **Java 17** e **Spring Boot**, responsável por toda a lógica de negócio, persistência e exposição dos endpoints do sistema.

## Tecnologias

| Tecnologia | Uso |
|---|---|
| Java 17 | Linguagem |
| Spring Boot 4 | Framework principal |
| Spring Data JPA / Hibernate | Acesso a dados |
| Spring Validation | Validação de entrada |
| PostgreSQL | Banco de dados relacional |
| Lombok | Redução de boilerplate |
| Docker (Multi-stage build) | Containerização para produção |

## Estrutura de Pacotes

```
src/main/java/br/com/sergipetec/processoseletivo/
├── config/
│   └── WebConfig.java                          # Configuração de CORS
├── controller/
│   ├── SolicitacaoController.java              # Endpoints de solicitações
│   ├── SolicitanteController.java              # Listagem de solicitantes
│   ├── CategoriaController.java                # Listagem de categorias
│   └── GlobalExceptionHandler.java             # Tratamento global de erros
├── dto/
│   ├── SolicitacaoRequestDTO.java              # Entrada: criação de solicitação
│   ├── SolicitacaoResponseDTO.java             # Saída: dados completos da solicitação
│   ├── SolicitacaoListagemProjection.java      # Saída: listagem resumida (projection)
│   ├── AtualizarStatusDTO.java                 # Entrada: atualização de status
│   ├── SolicitanteDTO.java                     # Saída: dados do solicitante
│   ├── CategoriaDTO.java                       # Saída: dados da categoria
│   └── ErroResponseDTO.java                    # Saída: mensagem de erro padronizada
├── entity/
│   ├── Solicitacao.java                        # Entidade principal + enum de status
│   ├── Solicitante.java                        # Entidade solicitante
│   └── Categoria.java                          # Entidade categoria
├── repository/
│   ├── SolicitacaoRepository.java              # Query nativa com filtros dinâmicos
│   ├── SolicitanteRepository.java              # CRUD básico
│   └── CategoriaRepository.java               # CRUD básico
└── service/
    ├── SolicitacaoService.java                 # Regras de negócio de solicitações
    ├── SolicitanteService.java                 # Listagem de solicitantes
    └── CategoriaService.java                   # Listagem de categorias
```

## Endpoints da API

### Solicitações

| Método | Rota | Descrição |
|---|---|---|
| `POST` | `/solicitacoes` | Cria uma nova solicitação |
| `GET` | `/solicitacoes` | Lista todas (com filtros opcionais) |
| `GET` | `/solicitacoes/{id}` | Busca uma solicitação por ID |
| `PATCH` | `/solicitacoes/{id}/status` | Atualiza o status de uma solicitação |

---

#### `POST /solicitacoes`

**Request body:**
```json
{
  "solicitanteId": 1,
  "categoriaId": 2,
  "descricao": "Compra de material de escritório",
  "valor": 350.00
}
```

**Response `201 Created`:**
```json
{
  "id": 7,
  "solicitante": {
    "id": 1,
    "nome": "Rone Clay"
  },
  "categoria": {
    "id": 2,
    "nome": "Material"
  },
  "descricao": "Compra de material de escritório",
  "valor": 350.00,
  "status": "SOLICITADO",
  "dataSolicitacao": "2025-05-07T14:32:00"
}
```

**Response `400 Bad Request` (validação):**
```json
{
  "solicitanteId": "O ID do solicitante é obrigatório",
  "valor": "O valor deve ser maior que zero"
}
```

**Response `400 Bad Request` (FK inexistente):**
```json
{
  "erro": "Solicitante não encontrado."
}
```

---

#### `GET /solicitacoes`

**Filtros opcionais:**

| Parâmetro | Tipo | Exemplo |
|---|---|---|
| `status` | String | `?status=SOLICITADO` |
| `categoriaId` | Long | `?categoriaId=2` |
| `dataInicio` | LocalDateTime | `?dataInicio=2025-01-01T00:00:00` |
| `dataFim` | LocalDateTime | `?dataFim=2025-12-31T23:59:59` |

**Response `200 OK`:**
```json
[
  {
    "id": 7,
    "nomeSolicitante": "Rone Clay",
    "documentoSolicitante": "111.111.111-11",
    "nomeCategoria": "Material",
    "status": "SOLICITADO",
    "valor": 350.00
  }
]
```

---

#### `GET /solicitacoes/{id}`

**Response `200 OK`:**
```json
{
  "id": 7,
  "solicitante": {
    "id": 1,
    "nome": "Rone Clay"
  },
  "categoria": {
    "id": 2,
    "nome": "Material"
  },
  "descricao": "Compra de material de escritório",
  "valor": 350.00,
  "status": "SOLICITADO",
  "dataSolicitacao": "2025-05-07T14:32:00"
}
```

**Response `400 Bad Request`:**
```json
{
  "erro": "Solicitação não encontrada para o ID: 99"
}
```

---

#### `PATCH /solicitacoes/{id}/status`

**Request body:**
```json
{
  "status": "LIBERADO"
}
```

**Response `200 OK`:**
```json
{
  "id": 7,
  "solicitante": {
    "id": 1,
    "nome": "Rone Clay"
  },
  "categoria": {
    "id": 2,
    "nome": "Material"
  },
  "descricao": "Compra de material de escritório",
  "valor": 350.00,
  "status": "LIBERADO",
  "dataSolicitacao": "2025-05-07T14:32:00"
}
```

**Response `400 Bad Request` (status inexistente):**
```json
{
  "erro": "Status inválido: 'PENDENTE'. Valores permitidos: SOLICITADO, LIBERADO, APROVADO, REJEITADO, CANCELADO."
}
```

**Response `409 Conflict` (transição inválida):**
```json
{
  "erro": "Transição de status inválida: APROVADO para SOLICITADO"
}
```

---

### Auxiliares (Dropdowns)

| Método | Rota | Descrição |
|---|---|---|
| `GET` | `/solicitantes` | Lista todos os solicitantes |
| `GET` | `/categorias` | Lista todas as categorias |

#### `GET /solicitantes`

**Response `200 OK`:**
```json
[
  { "id": 1, "nome": "Rone Clay" },
  { "id": 2, "nome": "Yris Mayara" }
]
```

#### `GET /categorias`

**Response `200 OK`:**
```json
[
  { "id": 1, "nome": "Serviços" },
  { "id": 2, "nome": "Material" }
]
```

---

## Máquina de Estados (State Pattern)

O controle de transições de status é implementado via polimorfismo de `Enum`. Cada estado define explicitamente quais transições são permitidas:

```
SOLICITADO ──► LIBERADO
SOLICITADO ──► REJEITADO
LIBERADO   ──► APROVADO
LIBERADO   ──► REJEITADO
APROVADO   ──► CANCELADO
REJEITADO  ──► (estado final)
CANCELADO  ──► (estado final)
```

Tentativas de transições inválidas retornam `409 Conflict` com mensagem explicativa.

## Decisões Técnicas

- **Padrão DTO em todas as camadas:** Nenhuma entidade JPA é exposta diretamente nos endpoints. Cada contexto tem seu próprio DTO: `SolicitacaoRequestDTO` para entrada, `SolicitacaoResponseDTO` para detalhamento, `SolicitacaoListagemProjection` para listagem e `ErroResponseDTO` para erros. A conversão é feita via método estático `converterParaDTO()` em cada DTO, mantendo a responsabilidade de mapeamento centralizada.

- **Separação de exceções por semântica HTTP:** `IllegalStateException` (violação de regra de negócio, ex: transição de status inválida) retorna `409 Conflict`. `IllegalArgumentException` (entrada inválida, ex: status inexistente, FK não encontrada) retorna `400 Bad Request`. Os dois são tratados em handlers separados no `GlobalExceptionHandler`.

- **Service layer para todos os controllers:** Nenhum controller acessa repositórios diretamente. `CategoriaService`, `SolicitanteService` e `SolicitacaoService` encapsulam toda lógica de acesso a dados e mapeamento para DTO.

- **Busca de entidades antes do save:** No cadastro de solicitação, `Solicitante` e `Categoria` são buscados via `findById` antes de associar à nova `Solicitacao`. Isso garante que o objeto retornado tenha todos os campos populados (nome, documento, etc.) em vez de um objeto shell com apenas o ID.

- **Método privado `buscarEntidadePorId`:** O `SolicitacaoService` expõe `buscarPorId` retornando DTO para o controller, e mantém `buscarEntidadePorId` privado para uso interno (usado por `atualizarStatus`). Isso evita expor a entidade JPA fora do service.

- **Interface Projections na listagem:** `SolicitacaoListagemProjection` é usada na query nativa de listagem para mapear apenas os campos necessários, sem carregar o grafo completo de entidades no contexto do Hibernate.

- **State Pattern via Enum:** Cada constante do enum `StatusSolicitacao` sobrescreve o método `transitar()`, aplicando o princípio Open/Closed (SOLID) e eliminando múltiplos `if/else` na camada de serviço.

- **CPF/CNPJ em coluna única:** Mantido na tabela `solicitante` para simplificar a query nativa de listagem, evitando `LEFT JOINs` adicionais.

## Pré-requisitos

- Java JDK 17+
- PostgreSQL em execução
- Maven (ou usar o wrapper `mvnw`)

## Configuração do Banco de Dados

1. Crie um banco de dados chamado `sgs_db` no PostgreSQL.
2. Execute os scripts na ordem:
   - `src/scripts_bd/schema.sql` — cria as tabelas
   - `src/scripts_bd/data.sql` — insere dados iniciais
3. Edite `src/main/resources/application.properties` com suas credenciais:
   ```properties
   spring.datasource.username=SEU_USUARIO
   spring.datasource.password=SUA_SENHA
   ```

### Schema resumido (DDL)

```sql
CREATE TABLE solicitante (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    cpf_cnpj VARCHAR(18) UNIQUE NOT NULL
);

CREATE TABLE categoria (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL
);

CREATE TABLE solicitacao (
    id BIGSERIAL PRIMARY KEY,
    solicitante_id BIGINT NOT NULL,
    categoria_id BIGINT NOT NULL,
    descricao TEXT NOT NULL,
    valor DECIMAL(10, 2) NOT NULL,
    data_solicitacao TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL,
    CONSTRAINT fk_solicitante FOREIGN KEY (solicitante_id) REFERENCES solicitante (id),
    CONSTRAINT fk_categoria FOREIGN KEY (categoria_id) REFERENCES categoria (id)
);
```

### Dados inseridos (DML)

```sql
INSERT INTO solicitante (nome, cpf_cnpj) VALUES
        ('Rone Clay', '111.111.111-11'),
        ('Yris Mayara', '222.222.222-22'),
        ('Raimundo Júnio', '333.333.333-33'),
        ('Coca-Cola', '44.444.444/0001-44'),
        ('Mc Donalds', '55.555.555/0001-55');

INSERT INTO categoria (nome) VALUES
        ('Serviços'),
        ('Material'),
        ('Transporte'),
        ('Equipamentos'),
        ('Manutenção');
```

## Como Executar Localmente

```bash
# A partir da raiz do repositório
cd backend
.\mvnw.cmd spring-boot:run
```

A API estará disponível em `http://localhost:8080`.

## Deploy (Produção)

O backend é containerizado via **Docker** com build multi-stage:

- **Estágio 1:** Maven 3.9.6 + JDK 17 — compila e empacota o `.jar`
- **Estágio 2:** JRE 17 Alpine — imagem mínima para execução

Hospedado no **Render** com as seguintes variáveis de ambiente injetadas:

| Variável | Descrição |
|---|---|
| `SPRING_DATASOURCE_URL` | URL JDBC do banco em produção (Neon) |
| `SPRING_DATASOURCE_USERNAME` | Usuário do banco |
| `SPRING_DATASOURCE_PASSWORD` | Senha do banco |

**URL de Produção:** `https://processo-seletivo-ppfz.onrender.com`
