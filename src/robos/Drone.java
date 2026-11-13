package robos;

public class Drone extends Robo {
    private static final long serialVersionUID = 1L;

    private final double altitudeMaxima;

    public Drone(int id, String nome, double nivelBateria, double altitudeMaxima) {
        super(id, nome, nivelBateria);
        if (altitudeMaxima <= 0) {
            throw new DadosInvalidosException("A altitude máxima deve ser maior que zero.");
        }
        this.altitudeMaxima = altitudeMaxima;
    }

    public double getAltitudeMaxima() {
        return altitudeMaxima;
    }

    @Override
    public boolean podeExecutar(TipoMissao tipo) {
        return tipo == TipoMissao.RECONHECIMENTO || tipo == TipoMissao.LOCALIZAR_PESSOAS;
    }

    @Override
    public String executarMissao(TipoMissao tipo, TipoTerreno terreno, NivelRisco risco)
            throws RoboIndisponivelException, MissaoIncompativelException, BateriaInsuficienteException {
        validarAtribuicao(tipo, terreno, risco);
        double consumo = calcularConsumo(tipo, terreno, risco);
        iniciar();
        String resultado;
        switch (tipo) {
            case RECONHECIMENTO:
                resultado = "Drone " + getNome() + " sobrevoou a área (altitude máxima de " + altitudeMaxima
                        + " m) e levantou informações do terreno " + terreno + " com risco " + risco + ".";
                break;
            case LOCALIZAR_PESSOAS:
                resultado = "Drone " + getNome() + " fez a busca por pessoas na área de terreno "
                        + terreno + " com risco " + risco + ".";
                break;
            default:
                resultado = "Missão não suportada pelo drone.";
        }
        finalizar(consumo);
        enviarMensagem("Missão de " + tipo.getDescricao() + " concluída. Bateria: " + String.format("%.1f", consultarBateria()) + "%");
        return resultado;
    }

    @Override
    public String toString() {
        return super.toString() + " | altitude máx.: " + altitudeMaxima + " m";
    }
}

