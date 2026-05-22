package br.com.estacionamento.repository;

import br.com.estacionamento.model.Caminhonete;
import br.com.estacionamento.model.Carro;
import br.com.estacionamento.model.Moto;
import br.com.estacionamento.model.Movimentacao;
import br.com.estacionamento.model.TipoVeiculo;
import br.com.estacionamento.model.Vaga;
import br.com.estacionamento.model.Veiculo;
import br.com.estacionamento.service.Estacionamento;
import br.com.estacionamento.service.EstacionamentoRepository;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class SqliteEstacionamentoRepository implements EstacionamentoRepository {
    private final Path databasePath;

    public SqliteEstacionamentoRepository(Path databasePath) {
        if (databasePath == null) {
            throw new IllegalArgumentException("Caminho do banco de dados e obrigatorio.");
        }

        this.databasePath = databasePath;
    }

    public void inicializar(int quantidadeVagas) {
        if (quantidadeVagas <= 0) {
            throw new IllegalArgumentException("Quantidade de vagas deve ser positiva.");
        }

        executar("""
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
            """);

        StringBuilder inserts = new StringBuilder();

        for (int numero = 1; numero <= quantidadeVagas; numero++) {
            inserts.append("INSERT OR IGNORE INTO vagas (id, numero, ocupada) VALUES (")
                .append(numero)
                .append(", ")
                .append(numero)
                .append(", 0);\n");
        }

        executar(inserts.toString());
    }

    public Estacionamento carregarEstacionamento() {
        List<Veiculo> veiculos = carregarVeiculos();
        List<Vaga> vagas = carregarVagas();
        List<Movimentacao> movimentacoes = carregarMovimentacoes(veiculos, vagas);

        return new Estacionamento(vagas, veiculos, movimentacoes, this);
    }

    @Override
    public void salvarVeiculo(Veiculo veiculo) {
        String sql = """
            INSERT INTO veiculos (id, placa, modelo, cor, tipo)
            VALUES (%d, %s, %s, %s, %s);
            """.formatted(
            veiculo.getId(),
            texto(veiculo.getPlaca()),
            texto(veiculo.getModelo()),
            texto(veiculo.getCor()),
            texto(veiculo.getTipo().name())
        );

        executar(sql);
    }

    @Override
    public void salvarEntrada(Movimentacao movimentacao) {
        String sql = """
            BEGIN;
            UPDATE vagas SET ocupada = 1 WHERE id = %d;
            INSERT INTO movimentacoes (id, veiculo_id, vaga_id, data_entrada, data_saida, valor_pago)
            VALUES (%d, %d, %d, %s, NULL, NULL);
            COMMIT;
            """.formatted(
            movimentacao.getVaga().getId(),
            movimentacao.getId(),
            movimentacao.getVeiculo().getId(),
            movimentacao.getVaga().getId(),
            dataHora(movimentacao.getDataEntrada())
        );

        executar(sql);
    }

    @Override
    public void salvarSaida(Movimentacao movimentacao) {
        String sql = """
            BEGIN;
            UPDATE movimentacoes
            SET data_saida = %s, valor_pago = %s
            WHERE id = %d AND data_saida IS NULL;
            UPDATE vagas SET ocupada = 0 WHERE id = %d;
            COMMIT;
            """.formatted(
            dataHora(movimentacao.getDataSaida()),
            decimal(movimentacao.getValorPago()),
            movimentacao.getId(),
            movimentacao.getVaga().getId()
        );

        executar(sql);
    }

    private List<Veiculo> carregarVeiculos() {
        List<String[]> linhas = consultar("""
            SELECT id, placa, modelo, cor, tipo
            FROM veiculos
            ORDER BY id;
            """);
        List<Veiculo> veiculos = new ArrayList<>();

        for (String[] linha : linhas) {
            int id = Integer.parseInt(linha[0]);
            String placa = linha[1];
            String modelo = linha[2];
            String cor = linha[3];
            TipoVeiculo tipo = TipoVeiculo.valueOf(linha[4]);

            veiculos.add(criarVeiculo(id, placa, modelo, cor, tipo));
        }

        return veiculos;
    }

    private List<Vaga> carregarVagas() {
        List<String[]> linhas = consultar("""
            SELECT id, numero, ocupada
            FROM vagas
            ORDER BY numero;
            """);
        List<Vaga> vagas = new ArrayList<>();

        for (String[] linha : linhas) {
            int id = Integer.parseInt(linha[0]);
            int numero = Integer.parseInt(linha[1]);
            boolean ocupada = "1".equals(linha[2]);

            vagas.add(new Vaga(id, numero, ocupada));
        }

        return vagas;
    }

    private List<Movimentacao> carregarMovimentacoes(List<Veiculo> veiculos, List<Vaga> vagas) {
        Map<Integer, Veiculo> veiculosPorId = new HashMap<>();
        Map<Integer, Vaga> vagasPorId = new HashMap<>();

        for (Veiculo veiculo : veiculos) {
            veiculosPorId.put(veiculo.getId(), veiculo);
        }

        for (Vaga vaga : vagas) {
            vagasPorId.put(vaga.getId(), vaga);
        }

        List<String[]> linhas = consultar("""
            SELECT id, veiculo_id, vaga_id, data_entrada, COALESCE(data_saida, ''), COALESCE(valor_pago, '')
            FROM movimentacoes
            ORDER BY id;
            """);
        List<Movimentacao> movimentacoes = new ArrayList<>();

        for (String[] linha : linhas) {
            int id = Integer.parseInt(linha[0]);
            Veiculo veiculo = veiculosPorId.get(Integer.parseInt(linha[1]));
            Vaga vaga = vagasPorId.get(Integer.parseInt(linha[2]));
            LocalDateTime dataEntrada = LocalDateTime.parse(linha[3]);
            LocalDateTime dataSaida = linha[4].isBlank() ? null : LocalDateTime.parse(linha[4]);
            BigDecimal valorPago = linha[5].isBlank() ? null : new BigDecimal(linha[5]);

            movimentacoes.add(new Movimentacao(id, veiculo, vaga, dataEntrada, dataSaida, valorPago));
        }

        return movimentacoes;
    }

    private Veiculo criarVeiculo(int id, String placa, String modelo, String cor, TipoVeiculo tipo) {
        return switch (tipo) {
            case CARRO -> new Carro(id, placa, modelo, cor);
            case MOTO -> new Moto(id, placa, modelo, cor);
            case CAMINHONETE -> new Caminhonete(id, placa, modelo, cor);
        };
    }

    private void executar(String sql) {
        executarProcesso(List.of("sqlite3", "-batch", "-bail", databasePath.toString(), "PRAGMA foreign_keys = ON;\n" + sql));
    }

    private List<String[]> consultar(String sql) {
        String saida = executarProcesso(List.of(
            "sqlite3",
            "-batch",
            "-bail",
            "-noheader",
            "-separator",
            "\t",
            databasePath.toString(),
            sql
        ));

        if (saida.isBlank()) {
            return List.of();
        }

        List<String[]> linhas = new ArrayList<>();

        for (String linha : saida.split("\\R")) {
            if (linha.isEmpty()) {
                continue;
            }

            linhas.add(linha.split("\\t", -1));
        }

        return linhas;
    }

    private String executarProcesso(List<String> comando) {
        try {
            Process processo = new ProcessBuilder(comando).start();
            String saida = new String(processo.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            String erro = new String(processo.getErrorStream().readAllBytes(), StandardCharsets.UTF_8);
            int codigo = processo.waitFor();

            if (codigo != 0) {
                throw new IllegalStateException("Erro ao acessar banco SQLite: " + erro.trim());
            }

            return saida;
        } catch (IOException erro) {
            throw new IllegalStateException("Nao foi possivel executar o SQLite. Verifique se o comando sqlite3 esta instalado.", erro);
        } catch (InterruptedException erro) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Operacao com SQLite interrompida.", erro);
        }
    }

    private String texto(String valor) {
        if (valor == null) {
            return "NULL";
        }

        return "'" + valor.replace("'", "''") + "'";
    }

    private String dataHora(LocalDateTime valor) {
        return valor == null ? "NULL" : texto(valor.toString());
    }

    private String decimal(BigDecimal valor) {
        return valor == null ? "NULL" : valor.toPlainString();
    }
}
