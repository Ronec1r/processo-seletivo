# Sistema de Gestão de Solicitações (SGS)

O **SGS** é uma aplicação web fullstack desenvolvida para o desafio técnico de Programador de Sistemas de Computação. O sistema apoia o controle de solicitações de pagamento realizadas por diferentes áreas de uma organização, garantindo organização, rastreabilidade e controle de fluxo via máquina de estados.

## Visão Geral da Arquitetura

```text
processo-seletivo/
├── backend/    # API REST — Java 17 + Spring Boot + PostgreSQL
└── frontend/   # Interface Web — React 19 + TypeScript + Tailwind CSS
```

A aplicação é organizada como um **monorepo** com backend e frontend desacoplados, comunicando-se exclusivamente via API REST.

## Documentação por Módulo

- [Backend](backend/README.md) — Endpoints, arquitetura de camadas, máquina de estados, banco de dados e deploy
- [Frontend](frontend/README.md) — Páginas, componentes, integração com a API e como executar

## Stack Resumida

| Camada | Tecnologia |
| --- | --- |
| API | Java 17, Spring Boot 4, Spring Data JPA |
| Banco de Dados | PostgreSQL (local) / Neon Serverless (produção) |
| Interface | React 19, TypeScript, Tailwind CSS 4, Vite |
| Deploy | Docker + Render (backend), Neon (banco) |

## Decisões Técnicas

- **Padrão DTO em todas as camadas:** Nenhuma entidade JPA é exposta diretamente nos endpoints. Cada contexto tem seu próprio DTO: `SolicitacaoRequestDTO` para entrada, `SolicitacaoResponseDTO` para detalhamento, `SolicitacaoListagemProjection` para listagem e `ErroResponseDTO` para erros. A conversão é feita via método estático `converterParaDTO()` em cada DTO, mantendo a responsabilidade de mapeamento centralizada.

- **Separação de exceções por semântica HTTP:** `IllegalStateException` (violação de regra de negócio, ex: transição de status inválida) retorna `409 Conflict`. `IllegalArgumentException` (entrada inválida, ex: status inexistente, FK não encontrada) retorna `400 Bad Request`. Os dois são tratados em handlers separados no `GlobalExceptionHandler`.

- **Service layer para todos os controllers:** Nenhum controller acessa repositórios diretamente. `CategoriaService`, `SolicitanteService` e `SolicitacaoService` encapsulam toda lógica de acesso a dados e mapeamento para DTO.

- **Busca de entidades antes do save:** No cadastro de solicitação, `Solicitante` e `Categoria` são buscados via `findById` antes de associar à nova `Solicitacao`. Isso garante que o objeto retornado tenha todos os campos populados (nome, documento, etc.) em vez de um objeto shell com apenas o ID.

- **Método privado `buscarEntidadePorId`:** O `SolicitacaoService` expõe `buscarPorId` retornando DTO para o controller, e mantém `buscarEntidadePorId` privado para uso interno (usado por `atualizarStatus`). Isso evita expor a entidade JPA fora do service.

- **Interface Projections na listagem:** `SolicitacaoListagemProjection` é usada na query nativa de listagem para mapear apenas os campos necessários, sem carregar o grafo completo de entidades no contexto do Hibernate.

- **State Pattern via Enum:** Cada constante do enum `StatusSolicitacao` sobrescreve o método `transitar()`, aplicando o princípio Open/Closed (SOLID) e eliminando múltiplos `if/else` na camada de serviço.

- **CPF/CNPJ em coluna única:** Mantido na tabela `solicitante` para simplificar a query nativa de listagem, evitando `LEFT JOINs` adicionais.


## Ambiente de Produção

| Serviço | Provedor | URL |
| --- | --- | --- |
| API (Backend) | Render | `https://processo-seletivo-ppfz.onrender.com` |
| Banco de Dados | Neon (Serverless Postgres) | — |
| Web (Frontend) | Vercel | `https://processo-seletivo-bice.vercel.app` |

## Execução Rápida

```bash
# Backend (porta 8080)
cd backend && .\mvnw.cmd spring-boot:run

# Frontend (porta 5173)
cd frontend && npm install && npm run dev
```

Consulte os READMEs específicos de cada módulo para instruções detalhadas de configuração e execução.
