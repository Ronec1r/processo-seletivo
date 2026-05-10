import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { getSolicitacao } from '../api/api';
import Layout from '../components/Layout';
import StatusBadge from '../components/StatusBadge';
import StatusModal from '../components/StatusModal';
import type { SolicitacaoDetalhe } from '../types';

export default function DetalhePage() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();

  const [solicitacao, setSolicitacao] = useState<SolicitacaoDetalhe | null>(null);
  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState('');
  const [modalAberto, setModalAberto] = useState(false);

  const carregar = async () => {
    if (!id) return;
    setCarregando(true);
    setErro('');
    try {
      const data = await getSolicitacao(Number(id));
      setSolicitacao(data);
    } catch {
      setErro('Solicitação não encontrada.');
    } finally {
      setCarregando(false);
    }
  };

  useEffect(() => {
    if (!id) return;
    let cancelado = false;

    (async () => {
      try {
        const data = await getSolicitacao(Number(id));
        if (cancelado) return;
        setSolicitacao(data);
        setErro('');
      } catch {
        if (cancelado) return;
        setErro('Solicitação não encontrada.');
      } finally {
        if (!cancelado) setCarregando(false);
      }
    })();

    return () => {
      cancelado = true;
    };
  }, [id]);

  const formatarValor = (v: number) =>
    v.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });

  const formatarData = (iso: string) =>
    new Date(iso).toLocaleString('pt-BR', {
      day: '2-digit', month: '2-digit', year: 'numeric',
      hour: '2-digit', minute: '2-digit',
    });

  if (carregando) return <Layout><p className="text-slate-400 text-sm">Carregando...</p></Layout>;
  if (erro || !solicitacao) return <Layout><p className="text-red-400 text-sm">{erro || 'Erro inesperado.'}</p></Layout>;

  return (
    <Layout>
      <div className="max-w-xl">
        <div className="flex items-center gap-3 mb-6">
          <button
            onClick={() => navigate('/solicitacoes')}
            className="text-slate-400 hover:text-slate-50 text-sm transition-colors"
          >
            ← Voltar
          </button>
          <h1 className="text-slate-50 text-2xl font-bold">Solicitação #{solicitacao.id}</h1>
        </div>

        <div className="bg-slate-900 border border-slate-700 rounded-xl p-6 flex flex-col gap-5">
          <div className="flex items-center justify-between">
            <StatusBadge status={solicitacao.status} />
            <button
              onClick={() => setModalAberto(true)}
              className="bg-emerald-500 text-navy font-semibold px-4 py-1.5 rounded-lg text-sm hover:bg-emerald-400 transition-colors"
            >
              Alterar Status
            </button>
          </div>

          <hr className="border-slate-700" />

          <div className="grid grid-cols-2 gap-4">
            <Campo label="Solicitante" valor={solicitacao.solicitante.nome} />
            <Campo label="Documento" valor={solicitacao.solicitante.cpfCnpj ?? '—'} />
            <Campo label="Categoria" valor={solicitacao.categoria.nome} />
            <Campo label="Valor" valor={formatarValor(solicitacao.valor)} destaque />
            <Campo label="Data da Solicitação" valor={formatarData(solicitacao.dataSolicitacao)} />
          </div>

          <div>
            <span className="block text-slate-400 text-xs mb-1">Descrição</span>
            <p className="text-slate-50 text-sm leading-relaxed">{solicitacao.descricao}</p>
          </div>
        </div>
      </div>

      {modalAberto && (
        <StatusModal
          solicitacaoId={solicitacao.id}
          statusAtual={solicitacao.status}
          onClose={() => setModalAberto(false)}
          onSuccess={() => { setModalAberto(false); carregar(); }}
        />
      )}
    </Layout>
  );
}

function Campo({ label, valor, destaque }: { label: string; valor: string; destaque?: boolean }) {
  return (
    <div>
      <span className="block text-slate-400 text-xs mb-0.5">{label}</span>
      <span className={`text-sm ${destaque ? 'text-emerald-400 font-semibold' : 'text-slate-50'}`}>{valor}</span>
    </div>
  );
}
