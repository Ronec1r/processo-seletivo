export type StatusSolicitacao = 'SOLICITADO' | 'LIBERADO' | 'APROVADO' | 'REJEITADO' | 'CANCELADO';

export interface Solicitante {
  id: number;
  nome: string;
  cpfCnpj?: string;
}

export interface Categoria {
  id: number;
  nome: string;
}

export interface SolicitacaoListagem {
  id: number;
  nomeSolicitante: string;
  documentoSolicitante: string;
  nomeCategoria: string;
  status: StatusSolicitacao;
  valor: number;
}

export interface SolicitacaoDetalhe {
  id: number;
  solicitante: Solicitante;
  categoria: Categoria;
  descricao: string;
  valor: number;
  dataSolicitacao: string;
  status: StatusSolicitacao;
}

export interface FiltrosListagem {
  status?: string;
  categoriaId?: number;
  dataInicio?: string;
  dataFim?: string;
}

export interface CriarSolicitacaoDTO {
  solicitanteId: number;
  categoriaId: number;
  descricao: string;
  valor: number;
}

export const TRANSICOES_VALIDAS: Record<StatusSolicitacao, StatusSolicitacao[]> = {
  SOLICITADO: ['LIBERADO', 'REJEITADO'],
  LIBERADO: ['APROVADO', 'REJEITADO'],
  APROVADO: ['CANCELADO'],
  REJEITADO: [],
  CANCELADO: [],
};
