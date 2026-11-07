package enums;

public enum PrioridadeMissao {
    BAIXA(1),
    MEDIA(2),
    ALTA(3);

    private final int peso;

    PrioridadeMissao(int peso) {
        this.peso = peso;
    }

    public int getPeso() {
        return peso;
    }
}
