package br.com.sergipetec.processoseletivo.repository;

import br.com.sergipetec.processoseletivo.entity.Solicitante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SolicitanteRepository extends JpaRepository<Solicitante, Integer> {
}
