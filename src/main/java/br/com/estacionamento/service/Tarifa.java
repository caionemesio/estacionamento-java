package br.com.estacionamento.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;

public final class Tarifa {
    private static final BigDecimal PRIMEIRA_HORA = new BigDecimal("5.00");
    private static final BigDecimal HORA_ADICIONAL = new BigDecimal("3.00");

    private Tarifa() {
    }

    public static BigDecimal calcularBase(Duration duracao) {
        if (duracao == null || duracao.isNegative()) {
            throw new IllegalArgumentException("Duracao invalida.");
        }

        long minutos = duracao.toMinutes();
        long horasCobradas = Math.max(1, (minutos + 59) / 60);
        long horasAdicionais = Math.max(0, horasCobradas - 1);

        return PRIMEIRA_HORA
            .add(HORA_ADICIONAL.multiply(BigDecimal.valueOf(horasAdicionais)))
            .setScale(2, RoundingMode.HALF_UP);
    }

    public static BigDecimal aplicarMultiplicador(BigDecimal valor, BigDecimal multiplicador) {
        if (valor == null || multiplicador == null) {
            throw new IllegalArgumentException("Valor e multiplicador sao obrigatorios.");
        }

        return valor.multiply(multiplicador).setScale(2, RoundingMode.HALF_UP);
    }
}
