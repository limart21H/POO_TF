package enums;

public enum NivelRisco {
    BAIXO(1.0),
    MEDIO(1.2),
    ALTO(1.5);

    private final double multiplicador;

    NivelRisco(double multiplicador) {
        this.multiplicador = multiplicador;
    }

    public double getMultiplicador() {
        return multiplicador;
    }
}
