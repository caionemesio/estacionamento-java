package br.com.estacionamento.model;

import java.util.Locale;

public enum TipoVeiculo {
    CARRO("carro"),
    MOTO("moto"),
    CAMINHONETE("caminhonete");

    private final String descricao;

    TipoVeiculo(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public static TipoVeiculo fromTexto(String texto) {
        String valor = texto == null ? "" : texto.trim().toLowerCase(Locale.ROOT);

        return switch (valor) {
            case "1", "carro" -> CARRO;
            case "2", "moto" -> MOTO;
            case "3", "caminhonete" -> CAMINHONETE;
            default -> throw new IllegalArgumentException("Tipo de veiculo invalido.");
        };
    }
}
