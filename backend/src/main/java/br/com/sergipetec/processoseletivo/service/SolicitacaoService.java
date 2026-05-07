package br.com.sergipetec.processoseletivo.service;

import br.com.sergipetec.processoseletivo.dto.SolicitacaoListagemProjection;
import br.com.sergipetec.processoseletivo.dto.SolicitacaoRequestDTO;
import br.com.sergipetec.processoseletivo.dto.SolicitacaoResponseDTO;
import br.com.sergipetec.processoseletivo.entity.Categoria;
import br.com.sergipetec.processoseletivo.entity.Solicitacao;
import br.com.sergipetec.processoseletivo.entity.Solicitacao.StatusSolicitacao;
import br.com.sergipetec.processoseletivo.entity.Solicitante;
import br.com.sergipetec.processoseletivo.repository.CategoriaRepository;
import br.com.sergipetec.processoseletivo.repository.SolicitacaoRepository;
import br.com.sergipetec.processoseletivo.repository.SolicitanteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;


@Service
public class SolicitacaoService {

    @Autowired
    private SolicitacaoRepository solicitacaoRepository;

    @Autowired
    private SolicitanteRepository solicitanteRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    public List<SolicitacaoListagemProjection> listar(String status,
                                                      Long categoriaID, LocalDateTime
                                                      dataInicio, LocalDateTime dataFim) {
        return solicitacaoRepository.buscarSolicitacoesComFiltros(status, categoriaID, dataInicio, dataFim);
    }


    // Criação da Solicitação
    @Transactional
    public SolicitacaoResponseDTO cadastrar(SolicitacaoRequestDTO dto) {
        // Validação se as FKs existem no banco e associação correta dos objetos
        Solicitante solicitante = solicitanteRepository.findById(dto.solicitanteId())
                .orElseThrow(() -> new IllegalArgumentException("Solicitante não encontrado."));

        Categoria categoria = categoriaRepository.findById(dto.categoriaId())
                .orElseThrow(() -> new IllegalArgumentException("Categoria não encontrada."));

        Solicitacao novaSolicitacao = new Solicitacao();
        novaSolicitacao.setSolicitante(solicitante);
        novaSolicitacao.setCategoria(categoria);
        novaSolicitacao.setDescricao(dto.descricao());
        novaSolicitacao.setValor(dto.valor());

        // Toda solicitação inicia como SOLICITADO
        novaSolicitacao.setStatus(StatusSolicitacao.SOLICITADO);
        novaSolicitacao.setDataSolicitacao(LocalDateTime.now());

        solicitacaoRepository.save(novaSolicitacao);

        //Conversão do objeto para DTO e retorno
        return SolicitacaoResponseDTO.converterParaDTO(novaSolicitacao);
    }

    private Solicitacao buscarEntidadePorId(Long id) {
        return solicitacaoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Solicitação não encontrada para o ID: " + id));
    }

    public SolicitacaoResponseDTO buscarPorId(Long id){
        Solicitacao solicitacao = buscarEntidadePorId(id);
        return SolicitacaoResponseDTO.converterParaDTO(solicitacao);
    }

    @Transactional
    public SolicitacaoResponseDTO atualizarStatus(Long id, StatusSolicitacao novoStatus) {
        Solicitacao solicitacao = buscarEntidadePorId(id);

        // A solicitação pede para o seu status atual tentar fazer a transição
        solicitacao.getStatus().transitar(solicitacao, novoStatus);
        solicitacaoRepository.save(solicitacao);
        return SolicitacaoResponseDTO.converterParaDTO(solicitacao);
    }
}