import type { StatusSolicitacao } from '../types';

const BADGE_STYLES: Record<StatusSolicitacao, string> = {
  SOLICITADO: 'bg-blue-900 text-blue-300',
  LIBERADO:   'bg-amber-900 text-amber-300',
  APROVADO:   'bg-emerald-900 text-emerald-400',
  REJEITADO:  'bg-red-900 text-red-300',
  CANCELADO:  'bg-slate-700 text-slate-400',
};

const LABELS: Record<StatusSolicitacao, string> = {
  SOLICITADO: 'Solicitado',
  LIBERADO:   'Liberado',
  APROVADO:   'Aprovado',
  REJEITADO:  'Rejeitado',
  CANCELADO:  'Cancelado',
};

export default function StatusBadge({ status }: { status: StatusSolicitacao }) {
  return (
    <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${BADGE_STYLES[status]}`}>
      {LABELS[status]}
    </span>
  );
}
