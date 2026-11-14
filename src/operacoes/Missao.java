package operacoes;

import enums.*;
import robos.Robo;
import java.io.Serializable;

public class Missao implements Serializable{
    private final int id;
    private String descricao;
    private TipoMissao tipo;
    private AreaOperacao area;
    private PrioridadeMissao prioridade;
    private StatusMissao status;
    private Robo roboResponsavel;   
    private String resultado;       

    public Missao(int id, String descricao, TipoMissao tipo,AreaOperacao area, PrioridadeMissao prioridade) {
        this.id = id;
        this.descricao = descricao;
        this.tipo = tipo;
        this.area = area;
        this.prioridade = prioridade;
        this.status = StatusMissao.PENDENTE; // Toda nova missão começa pendente
        this.roboResponsavel = null;
        this.resultado = null;
    }

    public boolean atribuirRobo(Robo robo) {
    if (this.status != StatusMissao.PENDENTE) {
        System.out.println("Só é possível atribuir robô a missões com status PENDENTE.");
        return false;
    }
    if (robo == null) {
        System.out.println("O robô a ser atribuído não pode ser nulo.");
        return false;
    }
    this.roboResponsavel = robo;
    return true;
    }


    public boolean iniciar() {
    if (this.roboResponsavel == null) {
        System.out.println("Não é possível iniciar a missão #" + id + " sem um robô responsável.");
        return false;
    }
    if (this.status != StatusMissao.PENDENTE) {
        System.out.println("Erro: Só é possível iniciar missão PENDENTE. Status atual: " + status);
        return false;
    }
    this.status = StatusMissao.EM_ANDAMENTO;
    System.out.println("Missão " + id + " iniciada com sucesso.");
    return true;
    }  

    public boolean concluir(String resultado) {
    if (status != StatusMissao.EM_ANDAMENTO) {
        System.out.println("Erro: A missão só pode ser concluída se estiver em andamento.");
        return false;
    }
    if (resultado == null || resultado.trim().isEmpty()) {
        System.out.println("Erro: O resultado da missão não pode ser vazio.");
        return false;
    }

    this.resultado = resultado.trim();
    this.status = StatusMissao.CONCLUIDA;
    System.out.println("Missão " + id + " concluída com sucesso!");
    
    return true;
    }

   public boolean cancelar() {
    if (this.status == StatusMissao.CONCLUIDA) {
        System.out.println("Não é possível cancelar uma missão que já foi concluída.");
        return false;
    }
    if (this.status == StatusMissao.CANCELADA) {
        System.out.println("Esta missão já está cancelada.");
        return false;
    }

    this.status = StatusMissao.CANCELADA;
    System.out.println("Missão " + id + " foi cancelada.");
    return true;
    }
        

    public void exibirResumo() {
        System.out.println("--- Resumo da Missão :" + id + " ---");
        System.out.println("Descrição: " + descricao);
        System.out.println("Tipo: " + tipo);
        System.out.println("Área: " + (area != null ? area.getNome() : "N/A"));
        System.out.println("Prioridade: " + prioridade);
        System.out.println("Status: " + status);
        System.out.println("Robô Responsável: " + (roboResponsavel != null ? roboResponsavel.getNome() : "Nenhum atribuído"));
        System.out.println("Resultado: " + (resultado != null ? resultado : "Pendente/Não finalizado"));
    }


    public int getId() {
        return id;
    }

    public String getDescricao() {
        return descricao;
    }

    public TipoMissao getTipo() {
        return tipo;
    }

    public AreaOperacao getArea() {
        return area;
    }

    public PrioridadeMissao getPrioridade() {
        return prioridade;
    }

    public StatusMissao getStatus() {
        return status;
    }

    public Robo getRoboResponsavel() {
        return roboResponsavel;
    }

    public String getResultado() {
        return resultado;
    }
}