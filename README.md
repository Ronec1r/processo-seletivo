# Sistema de Gestão de Solicitações (SGS)

O **Sistema de Gestão de Solicitações (SGS)** é uma aplicação web desenvolvida para o desafio técnico de Programador de Sistemas de Computação. O objetivo da solução é apoiar o controle de solicitações de pagamento realizadas por diferentes áreas de uma organização, garantindo organização, rastreabilidade e controle de fluxo.

## 🚀 Tecnologias Utilizadas

*   **Backend:** Java 17 com Spring Boot
*   **Banco de Dados:** PostgreSQL
*   **Acesso a Dados:** Spring Data JPA / Hibernate (com uso de SQL Nativo para listagens complexas)
*   **Frontend:** A definir
*   **Gerenciador de Dependências:** Maven

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

Para o funcionamento da aplicação, é necessário criar um banco de dados chamado `sgs_db` no PostgreSQL. Abaixo estão os scripts obrigatórios para a criação das tabelas e população inicial de dados.

### Script de Criação (DDL - `schema.sql`)
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

### Script de Inserção dos Dados (DML - `data.sql`)
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
3. Execute o script DDL (`schema.sql`) e, em seguida, o script DML (`data.sql`) disponibilizados acima para criar a estrutura e popular os dados iniciais.
4. Abra o arquivo `src/main/resources/application.properties` e ajuste as credenciais de conexão do banco de dados (usuário e senha) para corresponderem ao seu ambiente local:
   ```properties
   spring.datasource.username=SEU_USUARIO
   spring.datasource.password=SUA_SENHA


### 2. Executando a Aplicação
1. Clone o repositório do projeto:
   ```bash
   git clone https://github.com/Ronec1r/processo-seletivo.git
   ````
2. Navegue até o diretório do projeto:
   ```bash
   cd processo-seletivo
   ```
3. Compile e execute a aplicação usando Maven:
   ```bash
   mvn spring-boot:run
   ```
4. A aplicação estará disponível em `http://localhost:8080`.