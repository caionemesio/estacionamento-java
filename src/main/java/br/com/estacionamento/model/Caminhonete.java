package br.com.estacionamento.model;

import br.com.estacionamento.service.Tarifa;
import java.math.BigDecimal;
import java.time.Duration;

public final class Caminhonete extends Veiculo {
    public Caminhonete(int id, String placa, String modelo, String cor) {
        super(id, placa, modelo, cor);
    }

    @Override
    public TipoVeiculo getTipo() {
        return TipoVeiculo.CAMINHONETE;
    }

    @Override
    public BigDecimal calcularValor(Duration duracao) {
        return Tarifa.aplicarMultiplicador(Tarifa.calcularBase(duracao), new BigDecimal("1.50"));
    }
}
