# Sistema de Estacionamento

Aplicacao Java de terminal para cadastro de veiculos, entrada, saida, cobranca, listagem de estacionados e historico de movimentacoes com persistencia em SQLite.

## Entrega

- Codigo-fonte: `src`
- Script SQL do banco: `sql/schema.sql`
- Relatorio: `RELATORIO.md`

## Requisitos

- Java 17 ou superior
- SQLite instalado no sistema

## Rodar

```bash
javac -d out $(find src/main/java -name "*.java")
java -cp out br.com.estacionamento.app.Main
```

Ao rodar, a aplicacao cria ou reutiliza o arquivo `estacionamento.db` na raiz do projeto.

## Testar

```bash
javac -d out $(find src/main/java src/test/java -name "*.java")
java -cp out br.com.estacionamento.EstacionamentoTest
```

## Conferir o banco

```bash
sqlite3 estacionamento.db ".tables"
sqlite3 estacionamento.db "SELECT id, placa, modelo, cor, tipo FROM veiculos;"
sqlite3 estacionamento.db "SELECT id, numero, ocupada FROM vagas;"
sqlite3 estacionamento.db "SELECT id, veiculo_id, vaga_id, data_entrada, data_saida, valor_pago FROM movimentacoes;"
```

## Uso

Ao iniciar, informe a quantidade de vagas. Depois use o menu para cadastrar veiculos, registrar entradas e saidas, listar estacionados, consultar historico e conferir vagas.

Datas podem ser informadas no formato `dd/MM/yyyy HH:mm`. Pressionar ENTER usa a data e hora atual.
