# Sistema de Gestão de Solicitações (SGS)

O **Sistema de Gestão de Solicitações (SGS)** é uma aplicação web desenvolvida para o desafio técnico de Programador de Sistemas de Computação. O objetivo da solução é apoiar o controle de solicitações de pagamento realizadas por diferentes áreas de uma organização, garantindo organização, rastreabilidade e controle de fluxo.

## 🚀 Tecnologias Utilizadas

*   **Backend:** Java 17 com Spring Boot, organizado no diretório `backend/`
*   **Banco de Dados:** PostgreSQL
*   **Acesso a Dados:** Spring Data JPA / Hibernate (com uso de SQL Nativo para listagens complexas)
*   **Frontend:** Diretório separado em `frontend/` para futura interface da aplicação
*   **Gerenciador de Dependências:** Maven

## 📁 Estrutura de Diretórios

```text
processo-seletivo/
├── README.md
├── backend/
│   ├── mvnw
│   ├── mvnw.cmd
│   ├── pom.xml
│   └── src/
│       ├── main/
│       │   ├── java/
│       │   ├── resources/
│       │   │   └── application.properties
│       │   └── scripts_bd/
│       │       ├── schema.sql
│       │       └── data.sql
│       └── test/
└── frontend/
```

## 🧠 Justificativas de Decisões Técnicas

Conforme solicitado nos critérios de avaliação, abaixo estão as justificativas para algumas decisões arquiteturais tomadas durante o desenvolvimento:

1.  **Modelagem de CPF/CNPJ (Single Column):** Optou-se por manter o atributo `cpf_cnpj` na mesma tabela `solicitante`, ao invés de utilizar herança (`PessoaFisica` e `PessoaJuridica`). Essa decisão visa manter a simplicidade exigida pelo edital e, principalmente, otimizar a performance da Query Nativa de listagem, evitando múltiplos `LEFT JOINs` desnecessários. A validação da integridade do documento é garantida na camada de validação da API.
2.  **Uso de Interface Projections no Spring Data:** Para atender ao requisito de listagem usando SQL Nativo com múltiplos JOINs e filtros dinâmicos, foi utilizada a estratégia de *Interface Projections*. Isso permite mapear o resultado complexo do SQL diretamente para um objeto em memória de forma leve, sem sobrecarregar o contexto do Hibernate.
3.  **Design Pattern State (via Enum Polymorphism):** Para o controle rigoroso das transições de status exigidas nas regras de negócio, foi adotado o padrão de projeto *State* implementado através do polimorfismo de `Enum` no Java. Cada constante do Enum (`SOLICITADO`, `LIBERADO`, etc.) sobrescreve o método abstrato `transitar()`, encapsulando as regras de transição permitidas para o seu estado. Isso elimina a complexidade de múltiplos `if/else` da camada de Serviço, aplicando o princípio *Open/Closed* (SOLID) e garantindo que os estados finais (`REJEITADO` e `CANCELADO`) sejam invioláveis.

## ⚙️ Pré-requisitos para Execução Local

Antes de iniciar, certifique-se de ter instalado em sua máquina:
*   [Java JDK 17+](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
*   [PostgreSQL](https://www.postgresql.org/download/)
*   [Git](https://git-scm.com/)

## 🗄️ Scripts de Banco de Dados (DDL e DML)

Para o funcionamento da aplicação, é necessário criar um banco de dados chamado `sgs_db` no PostgreSQL. Os scripts obrigatórios estão disponíveis em `backend/src/scripts_bd/`.

### Script de Criação (DDL - `backend/src/scripts_bd/schema.sql`)
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

### Script de Inserção dos Dados (DML - `backend/src/scripts_bd/data.sql`)
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

## ▶️ Como Executar o Projeto Localmente

### 1. Configurando o Banco de Dados
1. Certifique-se de que o serviço do PostgreSQL está em execução na sua máquina.
2. Crie um banco de dados vazio chamado `sgs_db`.
3. Execute o script DDL (`backend/src/scripts_bd/schema.sql`) e, em seguida, o script DML (`backend/src/scripts_bd/data.sql`) para criar a estrutura e popular os dados iniciais.
4. Abra o arquivo `backend/src/main/resources/application.properties` e ajuste as credenciais de conexão do banco de dados (usuário e senha) para corresponderem ao seu ambiente local:
   ```properties
   spring.datasource.username=SEU_USUARIO
   spring.datasource.password=SUA_SENHA
   ```


### 2. Executando a Aplicação
1. Clone o repositório do projeto:
   ```bash
   git clone https://github.com/Ronec1r/processo-seletivo.git
   ```
2. Navegue até o diretório do backend:
   ```bash
   cd processo-seletivo/backend
   ```
3. Compile e execute a aplicação usando Maven Wrapper:
   ```bash
   .\mvnw.cmd spring-boot:run
   ```
4. A aplicação estará disponível em `http://localhost:8080`.

> Se preferir usar Maven instalado localmente, execute `mvn spring-boot:run` dentro da pasta `backend/`.

## ☁️ Deploy e Ambiente de Produção

A aplicação foi estruturada para suportar deploy contínuo, seguindo os princípios do **Twelve-Factor App**. Isso garante que o mesmo código executado localmente seja o utilizado em produção, alterando-se apenas as configurações injetadas via Variáveis de Ambiente.

*   **Banco de Dados:** Hospedado no [Neon](https://neon.tech/) (Serverless Postgres).
*   **Backend (API):** Hospedado no [Render](https://render.com/) utilizando Docker (Multi-stage build).

**URL Base da API (Produção):**
`https://processo-seletivo-ppfz.onrender.com`

### 🛠️ Como o Deploy foi configurado (Passo a Passo)

Como o projeto utiliza a arquitetura de *Monorepo*, o deploy do backend foi configurado da seguinte forma:

1. No Render, foi criado um novo **Web Service** selecionando a opção de ambiente **Docker**.
2. O **Root Directory** (Diretório Raiz) foi configurado para a pasta `backend`, instruindo o servidor a ignorar os arquivos do frontend.
3. O build e a execução são gerenciados automaticamente pelo arquivo `Dockerfile` presente na pasta do backend, que realiza a compilação via Maven e roda a aplicação em uma imagem Java super leve (Alpine).
4. As credenciais do banco de dados de produção foram injetadas de forma segura através das **Environment Variables** no painel do Render, sobrescrevendo as configurações locais do Spring Boot:
    * `SPRING_DATASOURCE_URL`: A URL JDBC gerada pelo Neon.
    * `SPRING_DATASOURCE_USERNAME`: O usuário do banco em produção.
    * `SPRING_DATASOURCE_PASSWORD`: A senha do banco em produção.

### 🧪 Como testar a API em Produção

Você pode utilizar os mesmos *bodies* JSON (listados na seção de testes locais) no Postman ou Insomnia. Basta substituir a URL base de `http://localhost:8080` pela URL pública do Render.


## 🧪 Testando a API (Exemplos de Uso)

Abaixo estão os principais endpoints para testar as funcionalidades e regras de negócio da aplicação via Postman, Insomnia ou cURL.

### 1. Criar uma Solicitação
*   **Método:** `POST`
*   **URL:** `http://localhost:8080/solicitacoes`
*   **Body (JSON):**
    ```json
    {
      "solicitanteId": 1,
      "categoriaId": 1,
      "descricao": "Aquisição de novos monitores para o setor de TI",
      "valor": 3500.50
    }
    ```
*   **Comportamento Esperado:** Retorna `201 Created`. A solicitação é criada automaticamente com o status inicial `SOLICITADO` e a data de criação preenchida.

### 2. Validação de Dados (Tratamento de Erros)
*   **Método:** `POST`
*   **URL:** `http://localhost:8080/solicitacoes`
*   **Body (JSON):**
    ```json
    {
      "solicitanteId": 1,
      "categoriaId": 2,
      "descricao": "",
      "valor": -100.00
    }
    ```
*   **Comportamento Esperado:** Retorna `400 Bad Request`. O `GlobalExceptionHandler` intercepta o erro e devolve as mensagens de validação (ex: "A descrição não pode estar vazia", "O valor deve ser maior que zero").

### 3. Listagem Completa (Query Nativa)
*   **Método:** `GET`
*   **URL:** `http://localhost:8080/solicitacoes`
*   **Comportamento Esperado:** Retorna `200 OK`. Traz a listagem mesclando dados do solicitante, categoria e solicitação, ordenados da mais recente para a mais antiga.

### 4. Listagem com Filtros Dinâmicos
*   **Método:** `GET`
*   **URL:** `http://localhost:8080/solicitacoes?status=SOLICITADO&categoriaId=1`
*   **Comportamento Esperado:** Retorna `200 OK`. Traz apenas as solicitações que correspondem aos filtros aplicados. Parâmetros não informados na URL são ignorados pela query no banco de dados.

### 5. Atualizar Status (Transição Válida)
*   **Método:** `PATCH`
*   **URL:** `http://localhost:8080/solicitacoes/1/status` *(Substitua `1` pelo ID da solicitação)*
*   **Body (JSON):**
    ```json
    {
      "status": "LIBERADO"
    }
    ```
*   **Comportamento Esperado:** Retorna `200 OK`. O status avança de forma validada seguindo o fluxo de regras de negócio.

### 6. Impedir Transição Inválida (State Pattern)
*   **Método:** `PATCH`
*   **URL:** `http://localhost:8080/solicitacoes/1/status`
*   **Body (JSON):**
    ```json
    {
      "status": "CANCELADO"
    }
    ```
*   **Comportamento Esperado:** Retorna `400 Bad Request`. Retorna uma mensagem de erro indicando que a transição é inválida (ex: tentando pular de `LIBERADO` direto para `CANCELADO`), protegendo a integridade dos dados e respeitando os estados finais.

### 7. Endpoints Auxiliares (Para os Dropdowns do Frontend)
Para garantir uma boa experiência de usuário (UX) no formulário de cadastro, a API fornece endpoints de listagem simples para popular os campos de seleção (selects/dropdowns).

*   **Listar Categorias:**
    *   **Método:** `GET`
    *   **URL:** `http://localhost:8080/categorias` (ou URL de produção)
    *   **Retorno:** `200 OK` com a lista completa de categorias disponíveis.

*   **Listar Solicitantes:**
    *   **Método:** `GET`
    *   **URL:** `http://localhost:8080/solicitantes` (ou URL de produção)
    *   **Retorno:** `200 OK` com a lista completa de solicitantes cadastrados.