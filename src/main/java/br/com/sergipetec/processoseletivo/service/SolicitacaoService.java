package br.com.sergipetec.processoseletivo.service;

import br.com.sergipetec.processoseletivo.entity.Solicitacao;
import br.com.sergipetec.processoseletivo.entity.Solicitacao.StatusSolicitacao;
import br.com.sergipetec.processoseletivo.repository.CategoriaRepository;
import br.com.sergipetec.processoseletivo.repository.SolicitacaoRepository;
import br.com.sergipetec.processoseletivo.repository.SolicitanteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;


@Service
public class SolicitacaoService {

    @Autowired
    private SolicitacaoRepository solicitacaoRepository;

    @Autowired
    private SolicitanteRepository solicitanteRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    // Criação da Solicitação
    @Transactional
    public Solicitacao cadastrar(Solicitacao solicitacao) {
        // Validação se as FKs existem no banco
        if (!solicitanteRepository.existsById(solicitacao.getSolicitante().getId())) {
            throw new IllegalArgumentException("Solicitante não encontrado.");
        }
        if (!categoriaRepository.existsById(solicitacao.getCategoria().getId())) {
            throw new IllegalArgumentException("Categoria não encontrada.");
        }

        // Toda solicitação inicia como SOLICITADO
        solicitacao.setStatus(StatusSolicitacao.SOLICITADO);
        solicitacao.setDataSolicitacao(LocalDateTime.now());

        return solicitacaoRepository.save(solicitacao);
    }

    public Solicitacao buscarPorId(Long id) {
        return solicitacaoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Solicitação não encontrada para o ID: " + id));
    }

    @Transactional
    public Solicitacao atualizarStatus(Long id, StatusSolicitacao novoStatus) {
        Solicitacao solicitacao = buscarPorId(id);

        // A solicitação pede para o seu status atual tentar fazer a transição
        solicitacao.getStatus().transitar(solicitacao, novoStatus);

        return solicitacaoRepository.save(solicitacao);
    }
}