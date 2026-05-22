package br.com.estacionamento.app;

import br.com.estacionamento.model.Movimentacao;
import br.com.estacionamento.model.TipoVeiculo;
import br.com.estacionamento.model.Vaga;
import br.com.estacionamento.model.Veiculo;
import br.com.estacionamento.repository.SqliteEstacionamentoRepository;
import br.com.estacionamento.service.Estacionamento;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Collection;
import java.util.Scanner;

public final class Main {
    private static final Path DATABASE_PATH = Path.of("estacionamento.db");
    private final Scanner scanner = new Scanner(System.in);
    private Estacionamento estacionamento;

    public static void main(String[] args) {
        new Main().executar();
    }

    private void executar() {
        SqliteEstacionamentoRepository repository = new SqliteEstacionamentoRepository(DATABASE_PATH);
        repository.inicializar(lerQuantidadeVagas());
        estacionamento = repository.carregarEstacionamento();
        System.out.println("Banco de dados: " + DATABASE_PATH.toAbsolutePath());
        System.out.println();

        int opcao;

        do {
            exibirMenu();
            opcao = lerInteiro("Escolha uma opcao: ");
            System.out.println();

            try {
                executarOpcao(opcao);
            } catch (IllegalArgumentException | IllegalStateException erro) {
                System.out.println("Erro: " + erro.getMessage());
            }

            if (opcao != 0) {
                System.out.println();
            }
        } while (opcao != 0);
    }

    private int lerQuantidadeVagas() {
        while (true) {
            int quantidade = lerInteiro("Quantidade de vagas do estacionamento: ");

            if (quantidade > 0) {
                return quantidade;
            }

            System.out.println("Informe uma quantidade positiva.");
        }
    }

    private void exibirMenu() {
        System.out.println("=== Sistema de Estacionamento ===");
        System.out.println("1 - Cadastrar veiculo");
        System.out.println("2 - Registrar entrada");
        System.out.println("3 - Registrar saida");
        System.out.println("4 - Listar veiculos estacionados");
        System.out.println("5 - Historico de movimentacoes");
        System.out.println("6 - Listar vagas");
        System.out.println("0 - Sair");
    }

    private void executarOpcao(int opcao) {
        switch (opcao) {
            case 1 -> cadastrarVeiculo();
            case 2 -> registrarEntrada();
            case 3 -> registrarSaida();
            case 4 -> listarEstacionados();
            case 5 -> listarHistorico();
            case 6 -> listarVagas();
            case 0 -> System.out.println("Sistema encerrado.");
            default -> System.out.println("Opcao invalida.");
        }
    }

    private void cadastrarVeiculo() {
        String placa = lerTexto("Placa: ");
        String modelo = lerTexto("Modelo: ");
        String cor = lerTexto("Cor: ");
        TipoVeiculo tipo = lerTipoVeiculo();

        Veiculo veiculo = estacionamento.cadastrarVeiculo(placa, modelo, cor, tipo);

        System.out.printf(
            "Veiculo cadastrado: #%d | %s | %s | %s | %s%n",
            veiculo.getId(),
            veiculo.getPlaca(),
            veiculo.getModelo(),
            veiculo.getCor(),
            veiculo.getTipo().getDescricao()
        );
    }

    private void registrarEntrada() {
        String placa = lerTexto("Placa do veiculo: ");
        int numeroVaga = lerInteiro("Vaga ocupada: ");
        LocalDateTime dataEntrada = lerDataHora("Data e hora de entrada (dd/MM/yyyy HH:mm ou ENTER para agora): ");

        Movimentacao movimentacao = estacionamento.registrarEntrada(placa, numeroVaga, dataEntrada);

        System.out.printf(
            "Entrada registrada: movimentacao #%d | placa %s | vaga %d | entrada %s%n",
            movimentacao.getId(),
            movimentacao.getVeiculo().getPlaca(),
            movimentacao.getVaga().getNumero(),
            Formatador.dataHora(movimentacao.getDataEntrada())
        );
    }

    private void registrarSaida() {
        String placa = lerTexto("Placa do veiculo: ");
        LocalDateTime dataSaida = lerDataHora("Data e hora de saida (dd/MM/yyyy HH:mm ou ENTER para agora): ");

        Movimentacao movimentacao = estacionamento.registrarSaida(placa, dataSaida);

        System.out.printf(
            "Saida registrada: movimentacao #%d | placa %s | saida %s | valor %s%n",
            movimentacao.getId(),
            movimentacao.getVeiculo().getPlaca(),
            Formatador.dataHora(movimentacao.getDataSaida()),
            Formatador.moeda(movimentacao.getValorPago())
        );
    }

    private void listarEstacionados() {
        Collection<Movimentacao> estacionados = estacionamento.listarEstacionados();

        if (estacionados.isEmpty()) {
            System.out.println("Nenhum veiculo estacionado no momento.");
            return;
        }

        estacionados.forEach(this::exibirMovimentacao);
    }

    private void listarHistorico() {
        Collection<Movimentacao> historico = estacionamento.listarHistorico();

        if (historico.isEmpty()) {
            System.out.println("Nenhuma movimentacao registrada.");
            return;
        }

        historico.forEach(this::exibirMovimentacao);
    }

    private void listarVagas() {
        estacionamento.listarVagas().forEach(this::exibirVaga);
    }

    private void exibirMovimentacao(Movimentacao movimentacao) {
        System.out.printf(
            "#%d | placa %s | %s | vaga %d | entrada %s | saida %s | valor %s%n",
            movimentacao.getId(),
            movimentacao.getVeiculo().getPlaca(),
            movimentacao.getVeiculo().getTipo().getDescricao(),
            movimentacao.getVaga().getNumero(),
            Formatador.dataHora(movimentacao.getDataEntrada()),
            Formatador.dataHora(movimentacao.getDataSaida()),
            Formatador.moeda(movimentacao.getValorPago())
        );
    }

    private void exibirVaga(Vaga vaga) {
        String status = vaga.isOcupada() ? "ocupada" : "livre";
        System.out.printf("Vaga %d | %s%n", vaga.getNumero(), status);
    }

    private TipoVeiculo lerTipoVeiculo() {
        while (true) {
            System.out.println("Tipo do veiculo:");
            System.out.println("1 - carro");
            System.out.println("2 - moto");
            System.out.println("3 - caminhonete");
            String tipo = lerTexto("Escolha: ");

            try {
                return TipoVeiculo.fromTexto(tipo);
            } catch (IllegalArgumentException erro) {
                System.out.println(erro.getMessage());
            }
        }
    }

    private LocalDateTime lerDataHora(String mensagem) {
        while (true) {
            System.out.print(mensagem);
            String valor = scanner.nextLine().trim();

            if (valor.isEmpty()) {
                return LocalDateTime.now();
            }

            try {
                return LocalDateTime.parse(valor, Formatador.DATA_HORA);
            } catch (DateTimeParseException erro) {
                System.out.println("Data invalida. Use o formato dd/MM/yyyy HH:mm.");
            }
        }
    }

    private int lerInteiro(String mensagem) {
        while (true) {
            System.out.print(mensagem);
            String valor = scanner.nextLine().trim();

            try {
                return Integer.parseInt(valor);
            } catch (NumberFormatException erro) {
                System.out.println("Informe um numero inteiro valido.");
            }
        }
    }

    private String lerTexto(String mensagem) {
        while (true) {
            System.out.print(mensagem);
            String valor = scanner.nextLine().trim();

            if (!valor.isEmpty()) {
                return valor;
            }

            System.out.println("Campo obrigatorio.");
        }
    }
}
