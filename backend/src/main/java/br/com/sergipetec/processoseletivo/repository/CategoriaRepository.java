package br.com.sergipetec.processoseletivo.repository;

import br.com.sergipetec.processoseletivo.entity.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
}
