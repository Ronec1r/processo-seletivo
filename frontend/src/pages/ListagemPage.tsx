import { useCallback, useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { getCategorias, getSolicitacoes } from '../api/api';
import Layout from '../components/Layout';
import StatusBadge from '../components/StatusBadge';
import StatusModal from '../components/StatusModal';
import type { Categoria, FiltrosListagem, SolicitacaoListagem, StatusSolicitacao } from '../types';

const STATUS_OPTIONS: StatusSolicitacao[] = ['SOLICITADO', 'LIBERADO', 'APROVADO', 'REJEITADO', 'CANCELADO'];

export default function ListagemPage() {
  const navigate = useNavigate();

  const [solicitacoes, setSolicitacoes] = useState<SolicitacaoListagem[]>([]);
  const [categorias, setCategorias] = useState<Categoria[]>([]);
  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState('');

  const [filtros, setFiltros] = useState<FiltrosListagem>({});
  const [modalId, setModalId] = useState<number | null>(null);
  const [modalStatus, setModalStatus] = useState<StatusSolicitacao | null>(null);

  const buscar = useCallback(async () => {
    setCarregando(true);
    setErro('');
    try {
      const params: FiltrosListagem = {};
      if (filtros.status) params.status = filtros.status;
      if (filtros.categoriaId) params.categoriaId = filtros.categoriaId;
      if (filtros.dataInicio) params.dataInicio = new Date(filtros.dataInicio).toISOString();
      if (filtros.dataFim) {
        const fim = new Date(filtros.dataFim);
        fim.setHours(23, 59, 59);
        params.dataFim = fim.toISOString();
      }
      const data = await getSolicitacoes(params);
      setSolicitacoes(data);
    } catch {
      setErro('Erro ao carregar as solicitações.');
    } finally {
      setCarregando(false);
    }
  }, [filtros]);

  useEffect(() => {
    getCategorias().then(setCategorias).catch(() => {});
    buscar();
  }, [buscar]);

  const limparFiltros = () => setFiltros({});

  const abrirModal = (id: number, status: StatusSolicitacao) => {
    setModalId(id);
    setModalStatus(status);
  };

  const fecharModal = () => {
    setModalId(null);
    setModalStatus(null);
  };

  const aoAtualizarStatus = () => {
    fecharModal();
    buscar();
  };

  const formatarValor = (v: number) =>
    v.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });

  return (
    <Layout>
      <div className="flex items-center justify-between mb-6">
        <h1 className="text-slate-50 text-2xl font-bold">Solicitações</h1>
        <button
          onClick={() => navigate('/solicitacoes/nova')}
          className="bg-emerald-500 text-navy font-semibold px-4 py-2 rounded-lg text-sm hover:bg-emerald-400 transition-colors"
        >
          + Nova Solicitação
        </button>
      </div>

      {/* Filtros */}
      <div className="bg-slate-900 border border-slate-700 rounded-xl p-4 mb-6">
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-3">
          <div>
            <label className="block text-slate-400 text-xs mb-1">Status</label>
            <select
              value={filtros.status ?? ''}
              onChange={e => setFiltros(f => ({ ...f, status: e.target.value || undefined }))}
              className="w-full bg-navy border border-slate-600 text-slate-50 rounded-lg px-3 py-2 text-sm focus:outline-none focus:border-emerald-500"
            >
              <option value="">Todos</option>
              {STATUS_OPTIONS.map(s => (
                <option key={s} value={s}>{s.charAt(0) + s.slice(1).toLowerCase()}</option>
              ))}
            </select>
          </div>

          <div>
            <label className="block text-slate-400 text-xs mb-1">Categoria</label>
            <select
              value={filtros.categoriaId ?? ''}
              onChange={e => setFiltros(f => ({ ...f, categoriaId: e.target.value ? Number(e.target.value) : undefined }))}
              className="w-full bg-navy border border-slate-600 text-slate-50 rounded-lg px-3 py-2 text-sm focus:outline-none focus:border-emerald-500"
            >
              <option value="">Todas</option>
              {categorias.map(c => (
                <option key={c.id} value={c.id}>{c.nome}</option>
              ))}
            </select>
          </div>

          <div>
            <label className="block text-slate-400 text-xs mb-1">Data início</label>
            <input
              type="date"
              value={filtros.dataInicio ?? ''}
              onChange={e => setFiltros(f => ({ ...f, dataInicio: e.target.value || undefined }))}
              className="w-full bg-navy border border-slate-600 text-slate-50 rounded-lg px-3 py-2 text-sm focus:outline-none focus:border-emerald-500"
            />
          </div>

          <div>
            <label className="block text-slate-400 text-xs mb-1">Data fim</label>
            <input
              type="date"
              value={filtros.dataFim ?? ''}
              onChange={e => setFiltros(f => ({ ...f, dataFim: e.target.value || undefined }))}
              className="w-full bg-navy border border-slate-600 text-slate-50 rounded-lg px-3 py-2 text-sm focus:outline-none focus:border-emerald-500"
            />
          </div>
        </div>

        <div className="flex gap-2 mt-3">
          <button
            onClick={buscar}
            className="bg-emerald-500 text-navy font-semibold px-4 py-1.5 rounded-lg text-sm hover:bg-emerald-400 transition-colors"
          >
            Filtrar
          </button>
          <button
            onClick={limparFiltros}
            className="text-slate-400 hover:text-slate-50 text-sm transition-colors px-2"
          >
            Limpar
          </button>
        </div>
      </div>

      {/* Tabela */}
      {carregando ? (
        <p className="text-slate-400 text-sm">Carregando...</p>
      ) : erro ? (
        <p className="text-red-400 text-sm">{erro}</p>
      ) : solicitacoes.length === 0 ? (
        <p className="text-slate-400 text-sm">Nenhuma solicitação encontrada.</p>
      ) : (
        <div className="overflow-x-auto rounded-xl border border-slate-700">
          <table className="w-full text-sm">
            <thead>
              <tr className="bg-slate-900 text-slate-400 text-left">
                <th className="px-4 py-3 font-medium">Solicitante</th>
                <th className="px-4 py-3 font-medium">Documento</th>
                <th className="px-4 py-3 font-medium">Categoria</th>
                <th className="px-4 py-3 font-medium">Valor</th>
                <th className="px-4 py-3 font-medium">Status</th>
                <th className="px-4 py-3 font-medium">Ações</th>
              </tr>
            </thead>
            <tbody>
              {solicitacoes.map((s, i) => (
                <tr
                  key={s.id}
                  className={`border-t border-slate-700 ${i % 2 === 0 ? 'bg-navy' : 'bg-slate-900/50'}`}
                >
                  <td className="px-4 py-3 text-slate-50">{s.nomeSolicitante}</td>
                  <td className="px-4 py-3 text-slate-400">{s.documentoSolicitante}</td>
                  <td className="px-4 py-3 text-slate-400">{s.nomeCategoria}</td>
                  <td className="px-4 py-3 text-slate-50 font-mono">{formatarValor(s.valor)}</td>
                  <td className="px-4 py-3">
                    <StatusBadge status={s.status} />
                  </td>
                  <td className="px-4 py-3">
                    <div className="flex gap-2">
                      <button
                        onClick={() => navigate(`/solicitacoes/${s.id}`)}
                        className="text-slate-400 hover:text-emerald-400 text-xs transition-colors"
                      >
                        Ver
                      </button>
                      <button
                        onClick={() => abrirModal(s.id, s.status)}
                        className="text-slate-400 hover:text-amber-400 text-xs transition-colors"
                      >
                        Status
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {modalId !== null && modalStatus !== null && (
        <StatusModal
          solicitacaoId={modalId}
          statusAtual={modalStatus}
          onClose={fecharModal}
          onSuccess={aoAtualizarStatus}
        />
      )}
    </Layout>
  );
}
