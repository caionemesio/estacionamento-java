package br.com.estacionamento;

import br.com.estacionamento.model.Movimentacao;
import br.com.estacionamento.model.TipoVeiculo;
import br.com.estacionamento.service.Estacionamento;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public final class EstacionamentoTest {
    public static void main(String[] args) {
        deveCalcularValoresPorTipo();
        deveImpedirOperacoesInvalidas();
        System.out.println("Todos os testes passaram.");
    }

    private static void deveCalcularValoresPorTipo() {
        Estacionamento estacionamento = new Estacionamento(3);
        LocalDateTime inicio = LocalDateTime.of(2026, 5, 22, 8, 0);

        estacionamento.cadastrarVeiculo("ABC-1234", "Gol", "Prata", TipoVeiculo.CARRO);
        estacionamento.registrarEntrada("ABC-1234", 1, inicio);
        Movimentacao carro = estacionamento.registrarSaida("ABC-1234", inicio.plusHours(2).plusMinutes(1));
        assertBigDecimal("11.00", carro.getValorPago());

        estacionamento.cadastrarVeiculo("DEF-5678", "Biz", "Vermelha", TipoVeiculo.MOTO);
        estacionamento.registrarEntrada("DEF-5678", 2, inicio);
        Movimentacao moto = estacionamento.registrarSaida("DEF-5678", inicio.plusHours(2));
        assertBigDecimal("4.00", moto.getValorPago());

        estacionamento.cadastrarVeiculo("GHI-9012", "Toro", "Branca", TipoVeiculo.CAMINHONETE);
        estacionamento.registrarEntrada("GHI-9012", 3, inicio);
        Movimentacao caminhonete = estacionamento.registrarSaida("GHI-9012", inicio.plusMinutes(30));
        assertBigDecimal("7.50", caminhonete.getValorPago());
    }

    private static void deveImpedirOperacoesInvalidas() {
        Estacionamento estacionamento = new Estacionamento(2);
        LocalDateTime inicio = LocalDateTime.of(2026, 5, 22, 8, 0);

        estacionamento.cadastrarVeiculo("AAA-1111", "Onix", "Preto", TipoVeiculo.CARRO);
        estacionamento.cadastrarVeiculo("BBB-2222", "CG", "Azul", TipoVeiculo.MOTO);

        assertThrows(() -> estacionamento.cadastrarVeiculo("AAA-1111", "Uno", "Branco", TipoVeiculo.CARRO));
        assertThrows(() -> estacionamento.registrarSaida("AAA-1111", inicio));
        assertThrows(() -> estacionamento.registrarEntrada("AAA-1111", 1, null));

        estacionamento.registrarEntrada("AAA-1111", 1, inicio);

        assertThrows(() -> estacionamento.registrarEntrada("AAA-1111", 2, inicio));
        assertThrows(() -> estacionamento.registrarEntrada("BBB-2222", 1, inicio));
        assertThrows(() -> estacionamento.registrarSaida("AAA-1111", inicio.minusMinutes(1)));

        estacionamento.registrarSaida("AAA-1111", inicio.plusHours(1));

        assertThrows(() -> estacionamento.registrarSaida("AAA-1111", inicio.plusHours(2)));
    }

    private static void assertBigDecimal(String esperado, BigDecimal recebido) {
        BigDecimal valorEsperado = new BigDecimal(esperado);

        if (valorEsperado.compareTo(recebido) != 0) {
            throw new AssertionError("Esperado " + esperado + ", recebido " + recebido);
        }
    }

    private static void assertThrows(Runnable acao) {
        try {
            acao.run();
        } catch (RuntimeException erro) {
            return;
        }

        throw new AssertionError("Era esperada uma excecao.");
    }
}
