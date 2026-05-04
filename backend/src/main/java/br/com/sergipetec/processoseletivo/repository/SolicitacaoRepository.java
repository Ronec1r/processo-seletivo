package br.com.sergipetec.processoseletivo.repository;

import br.com.sergipetec.processoseletivo.entity.Solicitacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SolicitacaoRepository extends JpaRepository<Solicitacao, Long> {

    //Query para buscar solicitações com filtros opcionais de status, categoria e intervalo de datas
    @Query(nativeQuery = true, value = """
        SELECT 
            s.id AS id,
            sol.nome AS nomeSolicitante, 
            sol.cpf_cnpj AS documentoSolicitante, 
            cat.nome AS nomeCategoria, 
            s.status AS status, 
            s.valor AS valor 
        FROM solicitacao s
        INNER JOIN solicitante sol ON s.solicitante_id = sol.id
        INNER JOIN categoria cat ON s.categoria_id = cat.id
        WHERE (:status IS NULL OR s.status = CAST(:status AS VARCHAR))
          AND (:categoriaId IS NULL OR s.categoria_id = CAST(:categoriaId AS BIGINT))
          AND (CAST(:dataInicio AS TIMESTAMP) IS NULL OR s.data_solicitacao >= CAST(:dataInicio AS TIMESTAMP))
          AND (CAST(:dataFim AS TIMESTAMP) IS NULL OR s.data_solicitacao <= CAST(:dataFim AS TIMESTAMP))
        ORDER BY s.data_solicitacao DESC
    """)

    List<SolicitacaoListagemProjection> buscarSolicitacoesComFiltros(
            @Param("status") String status,
            @Param("categoriaId") Long categoriaId,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim
    );
}