package br.com.sergipetec.processoseletivo.controller;

import br.com.sergipetec.processoseletivo.controller.dto.SolicitanteDTO;
import br.com.sergipetec.processoseletivo.repository.SolicitanteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/solicitantes")
public class SolicitanteController {

    @Autowired
    private SolicitanteRepository solicitanteRepository;

    @GetMapping
    public ResponseEntity<List<SolicitanteDTO>> listarTodos() {
        return ResponseEntity.ok(solicitanteRepository.buscarParaDropdown());
    }
}
