package br.com.sergipetec.processoseletivo.service;

import br.com.sergipetec.processoseletivo.dto.CategoriaDTO;
import br.com.sergipetec.processoseletivo.repository.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CategoriaService {
    @Autowired
    private CategoriaRepository categoriaRepository;

    public List<CategoriaDTO> listarTodas(){
        return categoriaRepository.findAll().stream().map(CategoriaDTO::converterParaDTO).toList();
    }
}
