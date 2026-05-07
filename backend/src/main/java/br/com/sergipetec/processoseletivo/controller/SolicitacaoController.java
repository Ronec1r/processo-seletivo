package br.com.sergipetec.processoseletivo.controller;

import br.com.sergipetec.processoseletivo.dto.AtualizarStatusDTO;
import br.com.sergipetec.processoseletivo.dto.SolicitacaoRequestDTO;
import br.com.sergipetec.processoseletivo.dto.SolicitacaoResponseDTO;
import br.com.sergipetec.processoseletivo.entity.Solicitacao;
import br.com.sergipetec.processoseletivo.dto.SolicitacaoListagemProjection;
import br.com.sergipetec.processoseletivo.service.SolicitacaoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/solicitacoes")
public class SolicitacaoController {

    @Autowired
    private SolicitacaoService solicitacaoService;



    // 1. Cadastro de Solicitação
    @PostMapping
    public ResponseEntity<SolicitacaoResponseDTO> cadastrar(@Valid @RequestBody SolicitacaoRequestDTO dto) {
        SolicitacaoResponseDTO solicitacaoCriada = solicitacaoService.cadastrar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(solicitacaoCriada);
    }

    // 2. Consulta (Listagem com filtros dinâmicos e Query Nativa)
    @GetMapping
    public ResponseEntity<List<SolicitacaoListagemProjection>> listarComFiltros(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long categoriaId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataFim
    ) {
        List<SolicitacaoListagemProjection> listagem = solicitacaoService.listar(status, categoriaId, dataInicio, dataFim);
        return ResponseEntity.ok(listagem);
    }

    // 3. Detalhamento
    @GetMapping("/{id}")
    public ResponseEntity<SolicitacaoResponseDTO> detalhar(@PathVariable Long id) {
        return ResponseEntity.ok(solicitacaoService.buscarPorId(id));
    }

    // 4. Atualização de Status
    @PatchMapping("/{id}/status")
    public ResponseEntity<SolicitacaoResponseDTO> atualizarStatus(
            @PathVariable Long id,
            @Valid @RequestBody AtualizarStatusDTO dto) {

        String statusStr = dto.status();

        try {
            Solicitacao.StatusSolicitacao novoStatus = Solicitacao.StatusSolicitacao.valueOf(statusStr.toUpperCase());
            return ResponseEntity.ok(solicitacaoService.atualizarStatus(id, novoStatus));
        } catch (IllegalArgumentException e){
            throw new IllegalArgumentException("Status inválido: " + statusStr + ". Status permitido: SOLICITADO, LIBERADO, REJEITADO, CANCELADO.");
        }
    }
}