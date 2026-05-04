package br.com.sergipetec.processoseletivo.repository;

import br.com.sergipetec.processoseletivo.controller.dto.SolicitanteDTO;
import br.com.sergipetec.processoseletivo.entity.Solicitante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SolicitanteRepository extends JpaRepository<Solicitante, Long> {
    @Query("SELECT new br.com.sergipetec.processoseletivo.controller.dto.SolicitanteDTO(s.id, s.nome) FROM Solicitante s")
    List<SolicitanteDTO> buscarParaDropdown();
}
