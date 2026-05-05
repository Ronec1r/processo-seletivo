# Frontend — SGS (Sistema de Gestão de Solicitações)

Interface web desenvolvida com **React 19** e **TypeScript**, consumindo a API REST do backend para gerenciar solicitações de pagamento.

## Tecnologias

| Tecnologia | Uso |
|---|---|
| React 19 | Biblioteca de UI |
| TypeScript 6 | Tipagem estática |
| React Router 7 | Roteamento client-side |
| Axios | Cliente HTTP |
| Tailwind CSS 4 | Estilização utilitária |
| Vite 8 | Bundler e servidor de desenvolvimento |

## Estrutura de Arquivos

```
src/
├── api/
│   └── api.ts              # Cliente Axios + funções de chamada à API
├── types/
│   └── index.ts            # Interfaces TypeScript e constante de transições de status
├── pages/
│   ├── ListagemPage.tsx    # Listagem com filtros e modal de alteração de status
│   ├── CadastroPage.tsx    # Formulário de criação de solicitação
│   └── DetalhePage.tsx     # Visualização detalhada de uma solicitação
├── components/
│   ├── Layout.tsx          # Header e navegação compartilhados
│   ├── StatusBadge.tsx     # Badge colorido por status
│   └── StatusModal.tsx     # Modal para seleção do próximo status
├── App.tsx                 # Configuração das rotas
├── main.tsx                # Ponto de entrada do React
└── index.css               # Import do Tailwind + tema customizado (navy)
```

## Páginas e Rotas

| Rota | Componente | Descrição |
|---|---|---|
| `/solicitacoes` | `ListagemPage` | Tabela com filtros por status, categoria e intervalo de datas |
| `/solicitacoes/nova` | `CadastroPage` | Formulário para criar uma nova solicitação |
| `/solicitacoes/:id` | `DetalhePage` | Detalhes completos de uma solicitação específica |

## Funcionalidades

- **Listagem com filtros dinâmicos:** Filtra por status, categoria e período (data início/fim), enviando apenas os parâmetros preenchidos à API.
- **Criação de solicitação:** Selects populados dinamicamente via API (`/solicitantes`, `/categorias`), com campo de valor numérico e descrição em texto livre.
- **Alteração de status via modal:** A `StatusModal` exibe apenas as transições válidas para o estado atual, respeitando a máquina de estados do backend.
- **Badge de status:** `StatusBadge` aplica cores distintas para cada estado (`SOLICITADO`, `LIBERADO`, `APROVADO`, `REJEITADO`, `CANCELADO`).

## Integração com a API

O arquivo [src/api/api.ts](src/api/api.ts) centraliza todas as chamadas HTTP. A `baseURL` aponta para o backend em produção (Render):

```ts
const api = axios.create({
  baseURL: 'https://processo-seletivo-ppfz.onrender.com',
});
```

Funções exportadas:

| Função | Método | Endpoint |
|---|---|---|
| `getSolicitacoes(filtros)` | GET | `/solicitacoes` |
| `getSolicitacao(id)` | GET | `/solicitacoes/:id` |
| `createSolicitacao(data)` | POST | `/solicitacoes` |
| `updateStatus(id, status)` | PATCH | `/solicitacoes/:id/status` |
| `getSolicitantes()` | GET | `/solicitantes` |
| `getCategorias()` | GET | `/categorias` |

## Tipagem

As interfaces e tipos em [src/types/index.ts](src/types/index.ts) espelham os contratos da API:

- `Solicitacao` — estrutura completa de uma solicitação
- `Solicitante` — id e nome
- `Categoria` — id e nome
- `StatusSolicitacao` — union type dos 5 estados possíveis
- `TRANSICOES_VALIDAS` — constante que mapeia cada status às suas transições permitidas (usada pela `StatusModal`)

## Pré-requisitos

- Node.js 18+
- npm ou yarn

## Como Executar Localmente

```bash
# A partir da raiz do repositório
cd frontend
npm install
npm run dev
```

A aplicação estará disponível em `http://localhost:5173`.

> Por padrão, o frontend aponta para o backend em produção (Render). Para usar o backend local, altere a `baseURL` em `src/api/api.ts` para `http://localhost:8080`.

## Scripts Disponíveis

| Comando | Descrição |
|---|---|
| `npm run dev` | Inicia o servidor de desenvolvimento |
| `npm run build` | Gera o bundle de produção em `dist/` |
| `npm run preview` | Visualiza o build de produção localmente |
| `npm run lint` | Executa o ESLint |
