package br.com.sergipetec.processoseletivo.controller;

import br.com.sergipetec.processoseletivo.controller.dto.SolicitacaoRequestDTO;
import br.com.sergipetec.processoseletivo.entity.Categoria;
import br.com.sergipetec.processoseletivo.entity.Solicitacao;
import br.com.sergipetec.processoseletivo.entity.Solicitante;
import br.com.sergipetec.processoseletivo.repository.SolicitacaoListagemProjection;
import br.com.sergipetec.processoseletivo.repository.SolicitacaoRepository;
import br.com.sergipetec.processoseletivo.service.SolicitacaoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/solicitacoes")
public class SolicitacaoController {

    @Autowired
    private SolicitacaoService solicitacaoService;

    @Autowired
    private SolicitacaoRepository solicitacaoRepository;

    // 1. Cadastro de Solicitação
    @PostMapping
    public ResponseEntity<Solicitacao> cadastrar(@Valid @RequestBody SolicitacaoRequestDTO dto) {
        Solicitacao novaSolicitacao = new Solicitacao();

        Solicitante solicitante = new Solicitante();
        solicitante.setId(dto.solicitanteId());
        novaSolicitacao.setSolicitante(solicitante);

        Categoria categoria = new Categoria();
        categoria.setId(dto.categoriaId());
        novaSolicitacao.setCategoria(categoria);

        novaSolicitacao.setDescricao(dto.descricao());
        novaSolicitacao.setValor(dto.valor());

        Solicitacao solicitacaoCriada = solicitacaoService.cadastrar(novaSolicitacao);
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
        List<SolicitacaoListagemProjection> listagem = solicitacaoRepository.buscarSolicitacoesComFiltros(status, categoriaId, dataInicio, dataFim);
        return ResponseEntity.ok(listagem);
    }

    // 3. Detalhamento
    @GetMapping("/{id}")
    public ResponseEntity<Solicitacao> detalhar(@PathVariable Long id) {
        return ResponseEntity.ok(solicitacaoService.buscarPorId(id));
    }

    // 4. Atualização de Status
    @PatchMapping("/{id}/status")
    public ResponseEntity<Solicitacao> atualizarStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {

        String statusStr = body.get("status");
        if (statusStr == null) {
            throw new IllegalArgumentException("O campo 'status' é obrigatório no corpo da requisição.");
        }

        Solicitacao.StatusSolicitacao novoStatus = Solicitacao.StatusSolicitacao.valueOf(statusStr.toUpperCase());
        Solicitacao solicitacaoAtualizada = solicitacaoService.atualizarStatus(id, novoStatus);

        return ResponseEntity.ok(solicitacaoAtualizada);
    }
}