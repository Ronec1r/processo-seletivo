import { useState } from 'react';
import type { StatusSolicitacao } from '../types';
import { TRANSICOES_VALIDAS } from '../types';
import { atualizarStatus } from '../api/api';

interface Props {
  solicitacaoId: number;
  statusAtual: StatusSolicitacao;
  onClose: () => void;
  onSuccess: () => void;
}

const LABEL: Record<StatusSolicitacao, string> = {
  SOLICITADO: 'Solicitado',
  LIBERADO:   'Liberado',
  APROVADO:   'Aprovado',
  REJEITADO:  'Rejeitado',
  CANCELADO:  'Cancelado',
};

const BUTTON_STYLE: Record<StatusSolicitacao, string> = {
  LIBERADO:   'border-amber-500 text-amber-400 hover:bg-amber-500',
  APROVADO:   'border-emerald-500 text-emerald-400 hover:bg-emerald-500',
  REJEITADO:  'border-red-500 text-red-400 hover:bg-red-500',
  CANCELADO:  'border-slate-500 text-slate-400 hover:bg-slate-500',
  SOLICITADO: 'border-blue-500 text-blue-400 hover:bg-blue-500',
};

export default function StatusModal({ solicitacaoId, statusAtual, onClose, onSuccess }: Props) {
  const [erro, setErro] = useState('');
  const [carregando, setCarregando] = useState(false);

  const transicoes = TRANSICOES_VALIDAS[statusAtual];

  const handleAlterar = async (novoStatus: StatusSolicitacao) => {
    setErro('');
    setCarregando(true);
    try {
      await atualizarStatus(solicitacaoId, novoStatus);
      onSuccess();
    } catch {
      setErro('Erro ao atualizar o status. Tente novamente.');
    } finally {
      setCarregando(false);
    }
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60">
      <div className="bg-slate-900 border border-slate-700 rounded-xl p-6 w-full max-w-sm shadow-2xl">
        <h2 className="text-slate-50 font-semibold text-lg mb-1">Alterar Status</h2>
        <p className="text-slate-400 text-sm mb-5">
          Status atual: <span className="text-slate-50 font-medium">{LABEL[statusAtual]}</span>
        </p>

        {transicoes.length === 0 ? (
          <p className="text-slate-400 text-sm">Este status é final e não pode ser alterado.</p>
        ) : (
          <div className="flex flex-col gap-2">
            {transicoes.map(status => (
              <button
                key={status}
                disabled={carregando}
                onClick={() => handleAlterar(status)}
                className={`border rounded-lg py-2.5 text-sm font-medium transition-colors hover:text-navy disabled:opacity-50 ${BUTTON_STYLE[status]}`}
              >
                Mover para {LABEL[status]}
              </button>
            ))}
          </div>
        )}

        {erro && <p className="text-red-400 text-sm mt-3">{erro}</p>}

        <button
          onClick={onClose}
          className="mt-4 w-full text-slate-400 hover:text-slate-50 text-sm transition-colors"
        >
          Cancelar
        </button>
      </div>
    </div>
  );
}
