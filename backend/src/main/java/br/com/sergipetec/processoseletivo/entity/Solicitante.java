package br.com.sergipetec.processoseletivo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
@Entity
@Table(name = "solicitante")


public class Solicitante {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome", nullable = false)
    private String nome;

    @Column(name="cpf_cnpj", nullable = false, unique = true, length = 18)
    private String cpfCnpj;
}
