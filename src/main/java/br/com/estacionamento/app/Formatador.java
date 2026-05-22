package br.com.estacionamento.app;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public final class Formatador {
    public static final DateTimeFormatter DATA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final NumberFormat MOEDA = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    private Formatador() {
    }

    public static String dataHora(LocalDateTime dataHora) {
        return dataHora == null ? "-" : DATA_HORA.format(dataHora);
    }

    public static String moeda(BigDecimal valor) {
        return valor == null ? "-" : MOEDA.format(valor);
    }
}
