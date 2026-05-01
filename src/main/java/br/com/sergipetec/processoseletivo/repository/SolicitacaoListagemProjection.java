package br.com.sergipetec.processoseletivo.repository;

import java.math.BigDecimal;

public interface SolicitacaoListagemProjection {
    String getNomeSolicitante();
    String getDocumentoSolicitante();
    String getNomeCategoria();
    String getStatus();
    BigDecimal getValor();
}