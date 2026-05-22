package br.com.estacionamento.service;

import br.com.estacionamento.model.Movimentacao;
import br.com.estacionamento.model.Veiculo;

public interface EstacionamentoRepository {
    void salvarVeiculo(Veiculo veiculo);

    void salvarEntrada(Movimentacao movimentacao);

    void salvarSaida(Movimentacao movimentacao);
}
