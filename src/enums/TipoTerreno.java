package enums;

public enum TipoTerreno {
    URBANO(1.3),
    IRREGULAR(1.5),
    ALAGADO(2.0);

    private final double multiplicador;

    TipoTerreno(double multiplicador) {
        this.multiplicador = multiplicador;
    }

    public double getMultiplicador() {
        return multiplicador;
    }
}
