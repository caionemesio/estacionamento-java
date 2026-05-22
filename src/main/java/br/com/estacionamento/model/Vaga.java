package br.com.estacionamento.model;

public final class Vaga {
    private final int id;
    private final int numero;
    private boolean ocupada;

    public Vaga(int id, int numero) {
        if (id <= 0) {
            throw new IllegalArgumentException("Id da vaga deve ser positivo.");
        }

        if (numero <= 0) {
            throw new IllegalArgumentException("Numero da vaga deve ser positivo.");
        }

        this.id = id;
        this.numero = numero;
    }

    public int getId() {
        return id;
    }

    public int getNumero() {
        return numero;
    }

    public boolean isOcupada() {
        return ocupada;
    }

    public void ocupar() {
        if (ocupada) {
            throw new IllegalStateException("Vaga ja ocupada.");
        }

        ocupada = true;
    }

    public void liberar() {
        if (!ocupada) {
            throw new IllegalStateException("Vaga ja esta livre.");
        }

        ocupada = false;
    }
}
