package br.com.estacionamento.model;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Locale;

public abstract class Veiculo {
    private final int id;
    private final String placa;
    private final String modelo;
    private final String cor;

    protected Veiculo(int id, String placa, String modelo, String cor) {
        if (id <= 0) {
            throw new IllegalArgumentException("Id do veiculo deve ser positivo.");
        }

        this.id = id;
        this.placa = normalizarPlaca(placa);
        this.modelo = validarTexto(modelo, "Modelo");
        this.cor = validarTexto(cor, "Cor");
    }

    public int getId() {
        return id;
    }

    public String getPlaca() {
        return placa;
    }

    public String getModelo() {
        return modelo;
    }

    public String getCor() {
        return cor;
    }

    public abstract TipoVeiculo getTipo();

    public abstract BigDecimal calcularValor(Duration duracao);

    public static String normalizarPlaca(String placa) {
        return validarTexto(placa, "Placa").toUpperCase(Locale.ROOT);
    }

    private static String validarTexto(String valor, String campo) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException(campo + " e obrigatorio.");
        }

        return valor.trim();
    }
}
