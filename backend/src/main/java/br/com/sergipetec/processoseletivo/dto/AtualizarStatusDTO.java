package br.com.sergipetec.processoseletivo.dto;

import jakarta.validation.constraints.NotBlank;

public record AtualizarStatusDTO (
    @NotBlank(message = "O status não pode estar vazio")
    String status)
{}
