import axios from 'axios';
import type {
  Categoria,
  CriarSolicitacaoDTO,
  FiltrosListagem,
  SolicitacaoDetalhe,
  SolicitacaoListagem,
  Solicitante,
} from '../types';

const api = axios.create({
  baseURL: 'https://processo-seletivo-ppfz.onrender.com',
});

export const getSolicitantes = (): Promise<Solicitante[]> =>
  api.get<Solicitante[]>('/solicitantes').then(r => r.data);

export const getCategorias = (): Promise<Categoria[]> =>
  api.get<Categoria[]>('/categorias').then(r => r.data);

export const getSolicitacoes = (filtros?: FiltrosListagem): Promise<SolicitacaoListagem[]> =>
  api.get<SolicitacaoListagem[]>('/solicitacoes', { params: filtros }).then(r => r.data);

export const getSolicitacao = (id: number): Promise<SolicitacaoDetalhe> =>
  api.get<SolicitacaoDetalhe>(`/solicitacoes/${id}`).then(r => r.data);

export const criarSolicitacao = (dto: CriarSolicitacaoDTO): Promise<SolicitacaoDetalhe> =>
  api.post<SolicitacaoDetalhe>('/solicitacoes', dto).then(r => r.data);

export const atualizarStatus = (id: number, status: string): Promise<SolicitacaoDetalhe> =>
  api.patch<SolicitacaoDetalhe>(`/solicitacoes/${id}/status`, { status }).then(r => r.data);
