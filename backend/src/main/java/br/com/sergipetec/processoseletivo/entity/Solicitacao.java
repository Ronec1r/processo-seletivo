package br.com.sergipetec.processoseletivo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
@Getter
@Setter
@Entity
@Table(name = "solicitacao")


public class Solicitacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relacionamento com Solicitante
    @ManyToOne(optional = false)
    @JoinColumn(name = "solicitante_id", nullable = false)
    private Solicitante solicitante;

    // Relacionamento com Categoria
    @ManyToOne(optional = false)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String descricao;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    @Column(name = "data_solicitacao", nullable = false)
    private LocalDateTime dataSolicitacao;

    // Uso de Enum para garantir a consistência do status
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusSolicitacao status = StatusSolicitacao.SOLICITADO;//Status iniciado sempre como Solicitado

    // Definição do Enum interno
    public enum StatusSolicitacao {
        SOLICITADO {
            @Override
            public void transitar(Solicitacao solicitacao, StatusSolicitacao novoStatus) {
                // SOLICITADO pode ir para LIBERADO ou REJEITADO
                if (novoStatus == LIBERADO || novoStatus == REJEITADO) {
                    solicitacao.setStatus(novoStatus);
                } else {
                    lancarErro(this, novoStatus);
                }
            }
        },
        LIBERADO {
            @Override
            public void transitar(Solicitacao solicitacao, StatusSolicitacao novoStatus) {
                // LIBERADO pode ir para APROVADO ou REJEITADO
                if (novoStatus == APROVADO || novoStatus == REJEITADO) {
                    solicitacao.setStatus(novoStatus);
                } else {
                    lancarErro(this, novoStatus);
                }
            }
        },
        APROVADO {
            @Override
            public void transitar(Solicitacao solicitacao, StatusSolicitacao novoStatus) {
                // APROVADO só pode ir para CANCELADO
                if (novoStatus == CANCELADO) {
                    solicitacao.setStatus(novoStatus);
                } else {
                    lancarErro(this, novoStatus);
                }
            }
        },
        REJEITADO {
            @Override
            public void transitar(Solicitacao solicitacao, StatusSolicitacao novoStatus) {
                throw new IllegalStateException(String.format("Transição de status inválida: %s para %s, pois é um estado final", this, novoStatus));
            }
        },
        CANCELADO {
            @Override
            public void transitar(Solicitacao solicitacao, StatusSolicitacao novoStatus) {
                throw new IllegalStateException(String.format("Transição de status inválida: %s para %s, pois é um estado final", this, novoStatus));
            }
        };

        // Método abstrato que obriga cada status a implementar sua própria regra
        public abstract void transitar(Solicitacao solicitacao, StatusSolicitacao novoStatus);

        // Método auxiliar para evitar repetição de código
        protected void lancarErro(StatusSolicitacao atual, StatusSolicitacao novo) {
            throw new IllegalStateException(String.format("Transição de status inválida: %s para %s", atual, novo));
        }
    }
}