package br.com.sergipetec.processoseletivo.dto;

import br.com.sergipetec.processoseletivo.entity.Solicitacao;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SolicitacaoResponseDTO(
        Long id,
        SolicitanteDTO solicitante,
        CategoriaDTO categoria,
        String descricao,
        BigDecimal valor,
        String status,
        LocalDateTime dataSolicitacao
) {
    public static SolicitacaoResponseDTO converterParaDTO(Solicitacao solicitacao){
        return new SolicitacaoResponseDTO(
                solicitacao.getId(),
                SolicitanteDTO.converterParaDTO(solicitacao.getSolicitante()),
                CategoriaDTO.converterParaDTO(solicitacao.getCategoria()),
                solicitacao.getDescricao(),
                solicitacao.getValor(),
                solicitacao.getStatus().name(),
                solicitacao.getDataSolicitacao()
        );
    }
}
