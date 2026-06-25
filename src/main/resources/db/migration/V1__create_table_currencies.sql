-- V1: cria a tabela currencies, espelhando exatamente os campos da Entidade Currency.

CREATE TABLE currencies
(
    id           BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    code         VARCHAR(3)     NOT NULL UNIQUE,
    name         VARCHAR(100)   NOT NULL,
    current_rate NUMERIC(15, 6) NOT NULL,
    updated_at   TIMESTAMP      NOT NULL
);

-- Comentário de documentação direto no banco (boa prática sênior):
COMMENT
ON TABLE currencies IS 'Armazena as moedas acompanhadas e sua cotação em relação ao Real (BRL)';
COMMENT
ON COLUMN currencies.code IS 'Código ISO da moeda: USD, EUR, JPY, BRL';
COMMENT
ON COLUMN currencies.current_rate IS 'Cotação atual em relação a 1 BRL';