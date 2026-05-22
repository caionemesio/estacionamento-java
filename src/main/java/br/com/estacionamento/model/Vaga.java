package br.com.estacionamento.model;

public final class Vaga {
    private final int id;
    private final int numero;
    private boolean ocupada;

    public Vaga(int id, int numero) {
        this(id, numero, false);
    }

    public Vaga(int id, int numero, boolean ocupada) {
        if (id <= 0) {
            throw new IllegalArgumentException("Id da vaga deve ser positivo.");
        }

        if (numero <= 0) {
            throw new IllegalArgumentException("Numero da vaga deve ser positivo.");
        }

        this.id = id;
        this.numero = numero;
        this.ocupada = ocupada;
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
