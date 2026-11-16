package robos;

import enums.NivelRisco;
import enums.TipoMissao;
import enums.TipoTerreno;

public class RoboTerrestre extends Robo {
    private static final long serialVersionUID = 1L;

    private final double capacidadeCarga;

    public RoboTerrestre(int id, String nome, double nivelBateria, double capacidadeCarga) {
        super(id, nome, nivelBateria);
        if (capacidadeCarga <= 0) {
            throw new IllegalArgumentException("A capacidade de carga deve ser maior que zero.");
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
    public String executarMissao(TipoMissao tipo, TipoTerreno terreno, NivelRisco risco) {
        validarAtribuicao(tipo, terreno, risco);
        double consumo = calcularConsumo(tipo, terreno, risco);
        iniciar();
        String resultado;
        switch (tipo) {
            case TRANSPORTAR_SUPRIMENTOS:
                resultado = "Robô " + getNome() + " transportou suprimentos (carga máxima de " + capacidadeCarga
                        + " kg) pela área de terreno " + terreno + " com risco " + risco + ".";
                break;
            default:
                resultado = "Missão não suportada pelo robô terrestre.";
        }
        finalizar(consumo);
        enviarMensagem("Missão de " + tipo.getDescricao() + " concluída. Bateria: " + String.format("%.1f", consultarBateria()) + "%");
        return resultado;
    }

    @Override
    public String toString() {
        return super.toString() + " | carga máx.: " + capacidadeCarga + " kg";
    }
}
