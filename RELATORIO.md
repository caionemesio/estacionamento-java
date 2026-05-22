# Relatorio do Sistema de Estacionamento

## Objetivo

O sistema foi desenvolvido em Java para controlar a entrada e saida de veiculos em um estacionamento. A aplicacao permite cadastrar veiculos, registrar entrada, registrar saida com calculo do valor a pagar, listar veiculos estacionados e consultar o historico de movimentacoes.

## Classes usadas

### Veiculo

Classe abstrata que representa os dados comuns de qualquer veiculo. Possui id, placa, modelo e cor. Tambem define o metodo abstrato `calcularValor`, usado para aplicar polimorfismo no calculo da cobranca.

### Carro

Classe que herda de `Veiculo`. Representa um carro e calcula o valor usando a tarifa normal do estacionamento.

### Moto

Classe que herda de `Veiculo`. Representa uma moto e calcula o valor com desconto de 50% sobre a tarifa normal.

### Caminhonete

Classe que herda de `Veiculo`. Representa uma caminhonete e calcula o valor com acrescimo de 50% sobre a tarifa normal.

### TipoVeiculo

Enum usado para representar os tipos permitidos no sistema: carro, moto e caminhonete.

### Vaga

Classe que representa uma vaga do estacionamento. Possui id, numero e status de ocupacao. Tambem possui metodos para ocupar e liberar a vaga.

### Movimentacao

Classe que representa uma entrada e uma possivel saida de veiculo. Guarda o veiculo, a vaga, a data de entrada, a data de saida e o valor pago.

### Estacionamento

Classe de servico que concentra as regras do sistema. Ela cadastra veiculos, registra entradas e saidas, lista veiculos estacionados e mostra o historico. Tambem impede placa duplicada, entrada duplicada, uso de vaga ocupada, saida sem entrada e saida de veiculo que nao esta estacionado.

### Tarifa

Classe responsavel pelas regras de cobranca. A primeira hora custa R$ 5,00 e cada hora adicional custa R$ 3,00. O valor final e ajustado de acordo com o tipo de veiculo por meio do metodo polimorfico de cada classe filha de `Veiculo`.

### Main

Classe principal da aplicacao. Exibe o menu no terminal, le as informacoes do usuario e chama os metodos da classe `Estacionamento`.

### Formatador

Classe auxiliar usada para formatar datas, horas e valores em moeda brasileira na exibicao do sistema.

## Tabelas usadas

### veiculos

Tabela que armazena os veiculos cadastrados. Possui id, placa, modelo, cor e tipo. A placa e unica para impedir cadastro duplicado.

### vagas

Tabela que armazena as vagas do estacionamento. Possui id, numero e status de ocupacao. O numero da vaga e unico.

### movimentacoes

Tabela que armazena o historico de entradas e saidas. Possui id, veiculo_id, vaga_id, data_entrada, data_saida e valor_pago. As colunas `veiculo_id` e `vaga_id` fazem ligacao com as tabelas `veiculos` e `vagas`.

## Banco de dados

O banco escolhido para a entrega foi SQLite, por ser simples, local e nao exigir servidor. O script `sql/schema.sql` cria as tabelas sugeridas no enunciado, define chaves primarias, chaves estrangeiras, restricoes de tipo e indices para impedir que um mesmo veiculo ou uma mesma vaga tenha mais de uma movimentacao aberta ao mesmo tempo.

## Regras implementadas

O sistema impede cadastro de placa duplicada, entrada de veiculo que ja esta estacionado, saida de veiculo que nao esta estacionado, uso de vaga ocupada e registro de saida sem entrada. O calculo considera no minimo uma hora, cobra R$ 5,00 ate uma hora e R$ 3,00 por hora adicional.
