import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { getCategorias, getSolicitantes, criarSolicitacao } from '../api/api';
import Layout from '../components/Layout';
import type { Categoria, Solicitante } from '../types';

export default function CadastroPage() {
  const navigate = useNavigate();

  const [solicitantes, setSolicitantes] = useState<Solicitante[]>([]);
  const [categorias, setCategorias] = useState<Categoria[]>([]);

  const [solicitanteId, setSolicitanteId] = useState('');
  const [categoriaId, setCategoriaId] = useState('');
  const [descricao, setDescricao] = useState('');
  const [valor, setValor] = useState('');

  const [enviando, setEnviando] = useState(false);
  const [erros, setErros] = useState<Record<string, string>>({});
  const [erroGeral, setErroGeral] = useState('');

  useEffect(() => {
    getSolicitantes().then(setSolicitantes).catch(() => {});
    getCategorias().then(setCategorias).catch(() => {});
  }, []);

  const validar = () => {
    const e: Record<string, string> = {};
    if (!solicitanteId) e.solicitanteId = 'Selecione um solicitante.';
    if (!categoriaId) e.categoriaId = 'Selecione uma categoria.';
    if (!descricao.trim()) e.descricao = 'A descrição é obrigatória.';
    const v = parseFloat(valor.replace(',', '.'));
    if (!valor || isNaN(v) || v <= 0) e.valor = 'Informe um valor positivo.';
    return e;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setErroGeral('');

    const validacao = validar();
    if (Object.keys(validacao).length > 0) {
      setErros(validacao);
      return;
    }
    setErros({});
    setEnviando(true);

    try {
      await criarSolicitacao({
        solicitanteId: Number(solicitanteId),
        categoriaId: Number(categoriaId),
        descricao: descricao.trim(),
        valor: parseFloat(valor.replace(',', '.')),
      });
      navigate('/solicitacoes');
    } catch (err: unknown) {
      const msg =
        (err as { response?: { data?: { erro?: string } } })?.response?.data?.erro ??
        'Erro ao cadastrar solicitação.';
      setErroGeral(msg);
    } finally {
      setEnviando(false);
    }
  };

  const inputClass = (campo: string) =>
    `w-full bg-navy border ${erros[campo] ? 'border-red-500' : 'border-slate-600'} text-slate-50 rounded-lg px-3 py-2.5 text-sm focus:outline-none focus:border-emerald-500 placeholder:text-slate-600`;

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
          <h1 className="text-slate-50 text-2xl font-bold">Nova Solicitação</h1>
        </div>

        <form onSubmit={handleSubmit} className="bg-slate-900 border border-slate-700 rounded-xl p-6 flex flex-col gap-4">

          <div>
            <label className="block text-slate-400 text-xs mb-1">Solicitante</label>
            <select
              value={solicitanteId}
              onChange={e => setSolicitanteId(e.target.value)}
              className={inputClass('solicitanteId')}
            >
              <option value="">Selecione...</option>
              {solicitantes.map(s => (
                <option key={s.id} value={s.id}>{s.nome}</option>
              ))}
            </select>
            {erros.solicitanteId && <p className="text-red-400 text-xs mt-1">{erros.solicitanteId}</p>}
          </div>

          <div>
            <label className="block text-slate-400 text-xs mb-1">Categoria</label>
            <select
              value={categoriaId}
              onChange={e => setCategoriaId(e.target.value)}
              className={inputClass('categoriaId')}
            >
              <option value="">Selecione...</option>
              {categorias.map(c => (
                <option key={c.id} value={c.id}>{c.nome}</option>
              ))}
            </select>
            {erros.categoriaId && <p className="text-red-400 text-xs mt-1">{erros.categoriaId}</p>}
          </div>

          <div>
            <label className="block text-slate-400 text-xs mb-1">Descrição</label>
            <textarea
              rows={3}
              value={descricao}
              onChange={e => setDescricao(e.target.value)}
              placeholder="Descreva o motivo da solicitação..."
              className={inputClass('descricao') + ' resize-none'}
            />
            {erros.descricao && <p className="text-red-400 text-xs mt-1">{erros.descricao}</p>}
          </div>

          <div>
            <label className="block text-slate-400 text-xs mb-1">Valor (R$)</label>
            <input
              type="text"
              inputMode="decimal"
              value={valor}
              onChange={e => setValor(e.target.value)}
              placeholder="0,00"
              className={inputClass('valor')}
            />
            {erros.valor && <p className="text-red-400 text-xs mt-1">{erros.valor}</p>}
          </div>

          {erroGeral && (
            <p className="text-red-400 text-sm bg-red-900/20 border border-red-800 rounded-lg px-3 py-2">
              {erroGeral}
            </p>
          )}

          <button
            type="submit"
            disabled={enviando}
            className="bg-emerald-500 text-navy font-semibold py-2.5 rounded-lg text-sm hover:bg-emerald-400 transition-colors disabled:opacity-50 mt-1"
          >
            {enviando ? 'Cadastrando...' : 'Cadastrar Solicitação'}
          </button>
        </form>
      </div>
    </Layout>
  );
}
