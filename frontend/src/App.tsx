import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom';
import ListagemPage from './pages/ListagemPage';
import CadastroPage from './pages/CadastroPage';
import DetalhePage from './pages/DetalhePage';

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Navigate to="/solicitacoes" replace />} />
        <Route path="/solicitacoes" element={<ListagemPage />} />
        <Route path="/solicitacoes/nova" element={<CadastroPage />} />
        <Route path="/solicitacoes/:id" element={<DetalhePage />} />
      </Routes>
    </BrowserRouter>
  );
}
