import { Link, useLocation } from 'react-router-dom';

export default function Layout({ children }: { children: React.ReactNode }) {
  const { pathname } = useLocation();

  const linkClass = (path: string) =>
    `text-sm font-medium transition-colors ${
      pathname === path
        ? 'text-emerald-500'
        : 'text-slate-400 hover:text-slate-50'
    }`;

  return (
    <div className="min-h-screen bg-navy">
      <header className="border-b border-slate-700">
        <div className="max-w-6xl mx-auto px-6 h-16 flex items-center justify-between">
          <span className="text-slate-50 font-bold tracking-wide text-lg">
            SGS <span className="text-slate-400 font-normal text-sm">Sistema de Gestão de Solicitações</span>
          </span>
          <nav className="flex gap-6">
            <Link to="/solicitacoes" className={linkClass('/solicitacoes')}>
              Listagem
            </Link>
            <Link to="/solicitacoes/nova" className={linkClass('/solicitacoes/nova')}>
              Nova Solicitação
            </Link>
          </nav>
        </div>
      </header>

      <main className="max-w-6xl mx-auto px-6 py-8">
        {children}
      </main>
    </div>
  );
}
