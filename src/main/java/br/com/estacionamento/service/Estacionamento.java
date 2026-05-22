package br.com.estacionamento.service;

import br.com.estacionamento.model.Caminhonete;
import br.com.estacionamento.model.Carro;
import br.com.estacionamento.model.Moto;
import br.com.estacionamento.model.Movimentacao;
import br.com.estacionamento.model.TipoVeiculo;
import br.com.estacionamento.model.Vaga;
import br.com.estacionamento.model.Veiculo;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class Estacionamento {
    private final Map<String, Veiculo> veiculosPorPlaca = new LinkedHashMap<>();
    private final Map<Integer, Vaga> vagasPorNumero = new LinkedHashMap<>();
    private final List<Movimentacao> movimentacoes = new ArrayList<>();
    private int proximoVeiculoId = 1;
    private int proximaMovimentacaoId = 1;

    public Estacionamento(int quantidadeVagas) {
        if (quantidadeVagas <= 0) {
            throw new IllegalArgumentException("Quantidade de vagas deve ser positiva.");
        }

        for (int numero = 1; numero <= quantidadeVagas; numero++) {
            vagasPorNumero.put(numero, new Vaga(numero, numero));
        }
    }

    public Veiculo cadastrarVeiculo(String placa, String modelo, String cor, TipoVeiculo tipo) {
        if (tipo == null) {
            throw new IllegalArgumentException("Tipo de veiculo e obrigatorio.");
        }

        String placaNormalizada = Veiculo.normalizarPlaca(placa);

        if (veiculosPorPlaca.containsKey(placaNormalizada)) {
            throw new IllegalStateException("Ja existe veiculo cadastrado com esta placa.");
        }

        Veiculo veiculo = criarVeiculo(proximoVeiculoId++, placaNormalizada, modelo, cor, tipo);
        veiculosPorPlaca.put(veiculo.getPlaca(), veiculo);
        return veiculo;
    }

    public Movimentacao registrarEntrada(String placa, int numeroVaga, LocalDateTime dataEntrada) {
        if (dataEntrada == null) {
            throw new IllegalArgumentException("Data de entrada e obrigatoria.");
        }

        Veiculo veiculo = buscarVeiculoObrigatorio(placa);
        Vaga vaga = buscarVagaObrigatoria(numeroVaga);

        if (buscarMovimentacaoAberta(veiculo.getPlaca()).isPresent()) {
            throw new IllegalStateException("Veiculo com esta placa ja esta estacionado.");
        }

        if (vaga.isOcupada()) {
            throw new IllegalStateException("Vaga ja ocupada.");
        }

        vaga.ocupar();
        Movimentacao movimentacao = new Movimentacao(proximaMovimentacaoId++, veiculo, vaga, dataEntrada);
        movimentacoes.add(movimentacao);
        return movimentacao;
    }

    public Movimentacao registrarSaida(String placa, LocalDateTime dataSaida) {
        if (dataSaida == null) {
            throw new IllegalArgumentException("Data de saida e obrigatoria.");
        }

        Veiculo veiculo = buscarVeiculoObrigatorio(placa);
        Movimentacao movimentacao = buscarMovimentacaoAberta(veiculo.getPlaca())
            .orElseThrow(() -> new IllegalStateException("Veiculo com esta placa nao esta estacionado."));

        movimentacao.registrarSaida(dataSaida);
        movimentacao.getVaga().liberar();
        return movimentacao;
    }

    public Collection<Veiculo> listarVeiculos() {
        return List.copyOf(veiculosPorPlaca.values());
    }

    public Collection<Vaga> listarVagas() {
        return List.copyOf(vagasPorNumero.values());
    }

    public List<Movimentacao> listarEstacionados() {
        return movimentacoes.stream()
            .filter(Movimentacao::estaAberta)
            .toList();
    }

    public List<Movimentacao> listarHistorico() {
        return List.copyOf(movimentacoes);
    }

    private Veiculo criarVeiculo(int id, String placa, String modelo, String cor, TipoVeiculo tipo) {
        return switch (tipo) {
            case CARRO -> new Carro(id, placa, modelo, cor);
            case MOTO -> new Moto(id, placa, modelo, cor);
            case CAMINHONETE -> new Caminhonete(id, placa, modelo, cor);
        };
    }

    private Veiculo buscarVeiculoObrigatorio(String placa) {
        String placaNormalizada = Veiculo.normalizarPlaca(placa);
        Veiculo veiculo = veiculosPorPlaca.get(placaNormalizada);

        if (veiculo == null) {
            throw new IllegalStateException("Veiculo nao cadastrado.");
        }

        return veiculo;
    }

    private Vaga buscarVagaObrigatoria(int numeroVaga) {
        Vaga vaga = vagasPorNumero.get(numeroVaga);

        if (vaga == null) {
            throw new IllegalArgumentException("Vaga inexistente.");
        }

        return vaga;
    }

    private Optional<Movimentacao> buscarMovimentacaoAberta(String placa) {
        return movimentacoes.stream()
            .filter(Movimentacao::estaAberta)
            .filter(movimentacao -> movimentacao.getVeiculo().getPlaca().equals(placa))
            .findFirst();
    }
}
