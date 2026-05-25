package br.com.sergipetec.processoseletivo.dto;


import br.com.sergipetec.processoseletivo.entity.Solicitante;

public record SolicitanteDTO(Long id,
                             String nome, String cpfCnpj) {
        public static SolicitanteDTO converterParaDTO(Solicitante solicitante) {
            return new SolicitanteDTO(
                    solicitante.getId(),
                    solicitante.getNome(),
                    solicitante.getCpfCnpj()
            );
        }
}
