package br.com.sergipetec.processoseletivo.repository;

import java.math.BigDecimal;

public interface SolicitacaoListagemProjection {
    Long getId();
    String getNomeSolicitante();
    String getDocumentoSolicitante();
    String getNomeCategoria();
    String getStatus();
    BigDecimal getValor();
}