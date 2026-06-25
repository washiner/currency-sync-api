package com.washiner.currencysyncapi.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// @Entity: avisa o JPA que essa classe representa uma tabela no banco.
@Entity
// @Table: define o nome exato da tabela (boa prática: sempre no plural, em snake_case).
@Table(name = "currencies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Currency {

    // @Id: marca esse campo como chave primária (PK) da tabela.
    @Id
    // @GeneratedValue: o banco gera o valor automaticamente (auto-incremento).
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Código técnico da moeda: "USD", "EUR", "JPY".
    // unique = true: não pode existir duas linhas com o mesmo código.
    // updatable = false: depois de criado, o código nunca muda
    // (igual você já faz com campos de auditoria na Fase 2).
    @Column(name = "code", nullable = false, unique = true, updatable = false, length = 3)
    private String code;

    // Nome legível: "Dólar Americano".
    @Column(name = "name", nullable = false)
    private String name;

    // A cotação atual dessa moeda EM RELAÇÃO AO REAL (BRL).
    // BigDecimal: nunca usamos double/float para dinheiro, porque eles têm
    // erro de arredondamento. BigDecimal é exato, é o padrão para valores monetários.
    @Column(name = "current_rate", nullable = false, precision = 15, scale = 6)
    private BigDecimal currentRate;

    // Quando foi a última vez que essa cotação foi atualizada.
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}