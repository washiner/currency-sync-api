package com.washiner.currencysyncapi.repository;

import com.washiner.currencysyncapi.domain.entity.Currency;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// JpaRepository<Currency, Long>:
//   Currency -> a entidade que esse repository gerencia
//   Long     -> o tipo do ID dessa entidade
// Ao estender JpaRepository, ganhamos de graça: save(), findAll(),
// findById(), deleteById(), e muito mais -- sem escrever nenhum SQL.
public interface CurrencyRepository extends JpaRepository<Currency, Long> {

    // Query method: o Spring Data JPA lê o NOME do método e gera o SQL
    // automaticamente. "findByCode" -> "SELECT * FROM currencies WHERE code = ?"
    Optional<Currency> findByCode(String code);
}
