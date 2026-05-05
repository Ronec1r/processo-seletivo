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
│   └── WebConfig.java                    # Configuração de CORS
├── controller/
│   ├── SolicitacaoController.java        # Endpoints principais
│   ├── SolicitanteController.java        # Listagem de solicitantes
│   ├── CategoriaController.java          # Listagem de categorias
│   ├── GlobalExceptionHandler.java       # Tratamento global de erros
│   └── dto/
│       ├── SolicitacaoRequestDTO.java    # DTO de entrada com validações
│       └── SolicitanteDTO.java           # DTO de dropdown
├── entity/
│   ├── Solicitacao.java                  # Entidade principal + enum de status
│   ├── Solicitante.java                  # Entidade solicitante
│   └── Categoria.java                    # Entidade categoria
├── repository/
│   ├── SolicitacaoRepository.java        # Query nativa com filtros dinâmicos
│   ├── SolicitanteRepository.java        # Query para dropdown
│   ├── CategoriaRepository.java          # CRUD básico
│   └── SolicitacaoListagemProjection.java # Interface Projection para listagem
└── service/
    └── SolicitacaoService.java           # Regras de negócio
```

## Endpoints da API

### Solicitações

| Método | Rota | Descrição |
|---|---|---|
| `POST` | `/solicitacoes` | Cria uma nova solicitação |
| `GET` | `/solicitacoes` | Lista todas (com filtros opcionais) |
| `GET` | `/solicitacoes/{id}` | Busca uma solicitação por ID |
| `PATCH` | `/solicitacoes/{id}/status` | Atualiza o status de uma solicitação |

**Filtros disponíveis no `GET /solicitacoes`:**

| Parâmetro | Tipo | Exemplo |
|---|---|---|
| `status` | String | `?status=SOLICITADO` |
| `categoriaId` | Long | `?categoriaId=2` |
| `dataInicio` | LocalDate | `?dataInicio=2025-01-01` |
| `dataFim` | LocalDate | `?dataFim=2025-12-31` |

### Auxiliares (Dropdowns)

| Método | Rota | Descrição |
|---|---|---|
| `GET` | `/solicitantes` | Lista todos os solicitantes |
| `GET` | `/categorias` | Lista todas as categorias |

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

Tentativas de transições inválidas retornam `403 Forbidden` com mensagem explicativa.

## Decisões Técnicas

- **CPF/CNPJ em coluna única:** Mantido na tabela `solicitante` para simplificar a query nativa de listagem, evitando `LEFT JOINs` adicionais. A validação do formato é feita na camada de serviço.
- **Interface Projections:** Usadas no repositório de listagem para mapear o resultado do SQL nativo de forma leve, sem sobrecarregar o contexto do Hibernate.
- **State Pattern via Enum:** Cada constante do enum `StatusSolicitacao` sobrescreve o método `transitar()`, aplicando o princípio Open/Closed (SOLID) e eliminando múltiplos `if/else` na camada de serviço.

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

### Schema resumido(DDL)

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

### Dados inseridos(DML)

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
