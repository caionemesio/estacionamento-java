# Sistema de Estacionamento

Aplicacao Java de terminal para cadastro de veiculos, entrada, saida, cobranca, listagem de estacionados e historico de movimentacoes.

## Requisitos

- Java 17 ou superior

## Rodar

```bash
javac -d out $(find src/main/java -name "*.java")
java -cp out br.com.estacionamento.app.Main
```

## Testar

```bash
javac -d out $(find src/main/java src/test/java -name "*.java")
java -cp out br.com.estacionamento.EstacionamentoTest
```

## Uso

Ao iniciar, informe a quantidade de vagas. Depois use o menu para cadastrar veiculos, registrar entradas e saidas, listar estacionados, consultar historico e conferir vagas.

Datas podem ser informadas no formato `dd/MM/yyyy HH:mm`. Pressionar ENTER usa a data e hora atual.
