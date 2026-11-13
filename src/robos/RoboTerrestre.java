public class RoboTerrestre extends Robo {
    private static final long serialVersionUID = 1L;

    private final double capacidadeCarga;

    public RoboTerrestre(int id, String nome, double nivelBateria, double capacidadeCarga) {
        super(id, nome, nivelBateria);
        if (capacidadeCarga <= 0) {
            throw new DadosInvalidosException("A capacidade de carga deve ser maior que zero.");
        }
        this.capacidadeCarga = capacidadeCarga;
    }

    public double getCapacidadeCarga() {
        return capacidadeCarga;
    }

    @Override
    public boolean podeExecutar(TipoMissao tipo) {
        return tipo == TipoMissao.TRANSPORTAR_SUPRIMENTOS;
    }

    @Override
    public String toString() {
        return super.toString() + " | carga máx.: " + capacidadeCarga + " kg";
    }
}

