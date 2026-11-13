package robos;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Robo implements Comunicavel, Serializable {
    private static final long serialVersionUID = 1L;

    private final int id;
    private final String nome;
    private double nivelBateria;
    private StatusRobo status;
    private int missoesRealizadas;
    private final List<String> historicoMensagens = new ArrayList<>();

    protected Robo(int id, String nome, double nivelBateria) {
        if (id <= 0) {
            throw new DadosInvalidosException("O id do robô deve ser maior que zero.");
        }
        if (nome == null || nome.isBlank()) {
            throw new DadosInvalidosException("O nome do robô não pode ser vazio.");
        }
        if (nivelBateria < 0 || nivelBateria > 100) {
            throw new DadosInvalidosException("A bateria deve estar entre 0 e 100: " + nivelBateria);
        }
        this.id = id;
        this.nome = nome;
        this.nivelBateria = nivelBateria;
        this.status = nivelBateria > 0 ? StatusRobo.DISPONIVEL : StatusRobo.SEM_BATERIA;
        this.missoesRealizadas = 0;
    }

    public abstract boolean podeExecutar(TipoMissao tipo);

    public double calcularConsumo(TipoMissao tipo, TipoTerreno terreno, NivelRisco risco) {
        return tipo.getPercentualBateria() * terreno.getMultiplicador() * risco.getMultiplicador();
    }

    protected void iniciar() {
        status = StatusRobo.EM_MISSAO;
    }

    protected void finalizar(double consumo) {
        nivelBateria = Math.max(0, nivelBateria - consumo);
        missoesRealizadas++;
        status = nivelBateria == 0 ? StatusRobo.SEM_BATERIA : StatusRobo.DISPONIVEL;
    }

    public boolean verificarDisponibilidade() {
        return status == StatusRobo.DISPONIVEL;
    }

    public double consultarBateria() {
        return nivelBateria;
    }

    public void recarregar() {
        nivelBateria = 100;
        if (status == StatusRobo.SEM_BATERIA) {
            status = StatusRobo.DISPONIVEL;
        }
    }

    public void exibirResumo() {
        System.out.println(this);
    }

    @Override
    public void enviarMensagem(String mensagem) {
        historicoMensagens.add("ENVIADA: " + mensagem);
        System.out.println("[" + nome + " -> CENTRAL] " + mensagem);
    }

    @Override
    public void receberMensagem(String mensagem) {
        historicoMensagens.add("RECEBIDA: " + mensagem);
        System.out.println("[CENTRAL -> " + nome + "] " + mensagem);
    }

    public int getId() { 
        return id; 
    }
    public String getNome() { 
        return nome; 
    }
    public StatusRobo getStatus() { 
        return status; 
    }
    public int getMissoesRealizadas() { 
        return missoesRealizadas; 
    }

    public List<String> getHistoricoMensagens() {
        return Collections.unmodifiableList(historicoMensagens);
    }

    @Override
    public String toString() {
        return String.format("%s #%d %s | bateria %.1f%% | %s | missões: %d",
                getClass().getSimpleName(), id, nome, nivelBateria, status, missoesRealizadas);
    }

    @Override
    public boolean equals(Object outro) {
        if (this == outro) {
            return true;
        }
        if (!(outro instanceof Robo)) {
            return false;
        }
        return id == ((Robo) outro).id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}

