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

## Decisões Técnicas Relevantes

1. **CPF/CNPJ em coluna única:** Mantido na tabela `solicitante` para simplificar a query nativa de listagem, evitando `LEFT JOINs` desnecessários entre tabelas de herança.
2. **Interface Projections (Spring Data):** Usadas para mapear o resultado do SQL nativo de listagem de forma leve, sem sobrecarregar o contexto do Hibernate.
3. **State Pattern via Enum:** Cada estado do enum `StatusSolicitacao` encapsula suas próprias transições permitidas, aplicando o princípio Open/Closed (SOLID) e eliminando lógica condicional na camada de serviço.

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
