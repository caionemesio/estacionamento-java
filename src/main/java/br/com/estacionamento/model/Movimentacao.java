package br.com.estacionamento.model;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;

public final class Movimentacao {
    private final int id;
    private final Veiculo veiculo;
    private final Vaga vaga;
    private final LocalDateTime dataEntrada;
    private LocalDateTime dataSaida;
    private BigDecimal valorPago;

    public Movimentacao(int id, Veiculo veiculo, Vaga vaga, LocalDateTime dataEntrada) {
        if (id <= 0) {
            throw new IllegalArgumentException("Id da movimentacao deve ser positivo.");
        }

        if (veiculo == null) {
            throw new IllegalArgumentException("Veiculo e obrigatorio.");
        }

        if (vaga == null) {
            throw new IllegalArgumentException("Vaga e obrigatoria.");
        }

        if (dataEntrada == null) {
            throw new IllegalArgumentException("Data de entrada e obrigatoria.");
        }

        this.id = id;
        this.veiculo = veiculo;
        this.vaga = vaga;
        this.dataEntrada = dataEntrada;
    }

    public int getId() {
        return id;
    }

    public Veiculo getVeiculo() {
        return veiculo;
    }

    public Vaga getVaga() {
        return vaga;
    }

    public LocalDateTime getDataEntrada() {
        return dataEntrada;
    }

    public LocalDateTime getDataSaida() {
        return dataSaida;
    }

    public BigDecimal getValorPago() {
        return valorPago;
    }

    public boolean estaAberta() {
        return dataSaida == null;
    }

    public void registrarSaida(LocalDateTime dataSaida) {
        if (dataEntrada == null) {
            throw new IllegalStateException("Nao existe data de entrada para esta movimentacao.");
        }

        if (dataSaida == null) {
            throw new IllegalArgumentException("Data de saida e obrigatoria.");
        }

        if (!estaAberta()) {
            throw new IllegalStateException("Saida ja registrada para esta movimentacao.");
        }

        if (dataSaida.isBefore(dataEntrada)) {
            throw new IllegalArgumentException("Data de saida nao pode ser anterior a entrada.");
        }

        this.valorPago = veiculo.calcularValor(Duration.between(dataEntrada, dataSaida));
        this.dataSaida = dataSaida;
    }
}
