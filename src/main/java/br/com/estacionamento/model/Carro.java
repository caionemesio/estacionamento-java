package br.com.estacionamento.model;

import br.com.estacionamento.service.Tarifa;
import java.math.BigDecimal;
import java.time.Duration;

public final class Carro extends Veiculo {
    public Carro(int id, String placa, String modelo, String cor) {
        super(id, placa, modelo, cor);
    }

    @Override
    public TipoVeiculo getTipo() {
        return TipoVeiculo.CARRO;
    }

    @Override
    public BigDecimal calcularValor(Duration duracao) {
        return Tarifa.calcularBase(duracao);
    }
}
