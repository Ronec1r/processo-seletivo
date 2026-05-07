package br.com.sergipetec.processoseletivo.dto;

import br.com.sergipetec.processoseletivo.entity.Categoria;

public record CategoriaDTO(Long id,
                           String nome) {
    public static CategoriaDTO converterParaDTO(Categoria categoria) {
        return new CategoriaDTO(
                categoria.getId(),
                categoria.getNome()
        );
    }
}
