PRAGMA foreign_keys = ON;

CREATE TABLE IF NOT EXISTS veiculos (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    placa TEXT NOT NULL UNIQUE,
    modelo TEXT NOT NULL,
    cor TEXT NOT NULL,
    tipo TEXT NOT NULL CHECK (tipo IN ('CARRO', 'MOTO', 'CAMINHONETE'))
);

CREATE TABLE IF NOT EXISTS vagas (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    numero INTEGER NOT NULL UNIQUE,
    ocupada INTEGER NOT NULL DEFAULT 0 CHECK (ocupada IN (0, 1))
);

CREATE TABLE IF NOT EXISTS movimentacoes (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    veiculo_id INTEGER NOT NULL,
    vaga_id INTEGER NOT NULL,
    data_entrada TEXT NOT NULL,
    data_saida TEXT,
    valor_pago NUMERIC,
    FOREIGN KEY (veiculo_id) REFERENCES veiculos (id),
    FOREIGN KEY (vaga_id) REFERENCES vagas (id),
    CHECK (data_saida IS NULL OR data_saida >= data_entrada),
    CHECK (
        (data_saida IS NULL AND valor_pago IS NULL)
        OR (data_saida IS NOT NULL AND valor_pago IS NOT NULL)
    )
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_movimentacoes_veiculo_aberta
ON movimentacoes (veiculo_id)
WHERE data_saida IS NULL;

CREATE UNIQUE INDEX IF NOT EXISTS idx_movimentacoes_vaga_aberta
ON movimentacoes (vaga_id)
WHERE data_saida IS NULL;

INSERT OR IGNORE INTO vagas (id, numero) VALUES
    (1, 1),
    (2, 2),
    (3, 3),
    (4, 4),
    (5, 5),
    (6, 6),
    (7, 7),
    (8, 8),
    (9, 9),
    (10, 10);
