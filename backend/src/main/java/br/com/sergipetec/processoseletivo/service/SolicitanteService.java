package br.com.sergipetec.processoseletivo.service;

import br.com.sergipetec.processoseletivo.dto.SolicitanteDTO;
import br.com.sergipetec.processoseletivo.repository.SolicitanteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SolicitanteService {
    @Autowired
    private SolicitanteRepository solicitanteRepository;

    public List<SolicitanteDTO> listarTodos(){
        return solicitanteRepository.findAll().stream().map(SolicitanteDTO::converterParaDTO).toList();
    }
}
