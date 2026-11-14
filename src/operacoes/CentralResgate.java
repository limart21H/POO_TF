package operacoes;

import enums.NivelRisco;
import enums.StatusMissao;
import enums.TipoMissao;
import enums.TipoTerreno;

import java.util.*;
import java.util.stream.Collectors;
import robos.Robo;

public class CentralResgate {
    private String nome;
    private Map<Integer, Robo> robos;
    private Map<Integer, AreaOperacao> areas;
    private Map<Integer, Missao> missoes;
    private Queue<Missao> missoesPendentes;

    public CentralResgate(String nome) {
        this.nome = nome;
        this.robos = new HashMap<>();
        this.areas = new HashMap<>();
        this.missoes = new HashMap<>();
        this.missoesPendentes = new LinkedList<>();
    }

    public boolean cadastrarRobo(Robo robo) {
        if (robo == null) {
            System.out.println("O Robô não pode ser nulo.");
            return false;
        }
        
        if (robos.containsKey(robo.getId())) {
            System.out.println("Esse ID de robô já existe.");
            return false;
        }

        robos.put(robo.getId(), robo);
        return true;
    }

    public boolean cadastrarArea(AreaOperacao area) {
        if (area == null) {
            System.out.println("A área não pode ser nula.");
            return false;
        }

        if (areas.containsKey(area.getId())) {
            System.out.println("Esse ID de área já existe.");
            return false;
        }

        areas.put(area.getId(), area);
        return true;
    }

    public boolean registrarMissao(Missao missao) {
        if (missao == null) {
            System.out.println("A Missão não pode ser nula.");
            return false;
        }

        if (missoes.containsKey(missao.getId())) {
            System.out.println("Já existe uma missão cadastrada com o id " + missao.getId() + ".");
            return false;
        }

        if (missao.getArea() == null || !areas.containsKey(missao.getArea().getId())) {
            System.out.println("A área da missão não está cadastrada na central.");
            return false;
        }
        
        missoes.put(missao.getId(), missao);

        
        if (!atribuirRoboMissao(missao)) {
            missoesPendentes.offer(missao);
            System.out.println("Nenhum robô compatível disponível no momento. Missão " + missao.getId() + " adicionada à fila de pendentes.");
        }

        return true;
    }


    public boolean atribuirRoboMissao(Missao missao) {
    if (missao == null) {
        System.out.println("A missão não pode ser nula.");
        return false;
    }

     Optional<Robo> escolhido = robos.values().stream()
                .filter(r -> r.verificarDisponibilidade())                         
                .filter(r -> !estaComprometido(r))                                 
                .filter(r -> r.podeExecutar(missao.getTipo()))                     
                .filter(r -> r.consultarBateria() >= consumoPrevisto(r, missao))   
                .max(Comparator.comparingDouble(r -> calcularScore(r, missao)));   

        if (escolhido.isEmpty()) {
            return false;                   
        }

        Robo robo = escolhido.get();
        if (!missao.atribuirRobo(robo)) {
            return false;
        }
        robo.receberMensagem("Missão #" + missao.getId() + " atribuída a você.");
        return true;
    }
   
    public boolean executarMissao(int idMissao) {
    Missao missao = buscarMissao(idMissao);
    if (missao == null || !missao.iniciar()) {       
        return false;
    }
    Robo robo = missao.getRoboResponsavel();
    AreaOperacao a = missao.getArea();
    boolean sucesso;
    try {
        String resultado = robo.executarMissao(
                missao.getTipo(), a.getTipoTerreno(), a.getNivelRisco());
        sucesso = missao.concluir(resultado);        
    } catch (Exception e) {
        System.out.println("Falha na execução: " + e.getMessage());
        missao.cancelar();
        sucesso = false;
    }
    reprocessarPendentes();                         
    return sucesso;
    }

     public void gerarRelatorioGeral() {
        System.out.println("--- RELATÓRIO GERAL DA CENTRAL: " + nome + " ---");
        System.out.println("Total de Robôs cadastrados: " + robos.size());
        System.out.println("Total de Áreas cadastradas: " + areas.size());
        System.out.println("Total de Missões cadastradas: " + missoes.size());
        System.out.println("Missões Pendentes na fila: " + missoesPendentes.size());
    }

    public Robo buscarRobo(int id) {
        Robo robo = robos.get(id);
        if (robo == null) {
            System.out.println("Robô " + id + " não encontrado.");
        }
        return robo;
    }

    public Missao buscarMissao(int id) {
        Missao missao = missoes.get(id);
        if (missao == null) {
            System.out.println("Missão " + id + " não encontrada.");
        }
        return missao;
    }

    public AreaOperacao buscarArea(int id) {
        AreaOperacao area = areas.get(id);
        if (area == null) {
            System.out.println("Área " + id + " não encontrada.");
        }
        return area;
    }

    public List<Robo> listarRobos() {
         return new ArrayList<>(robos.values()); 
        }

    public List<AreaOperacao> listarAreas() { 
        return new ArrayList<>(areas.values()); 
    }

    public List<Missao> listarMissoes(){
         return new ArrayList<>(missoes.values()); 
    }  
    
    
    public List<Missao> listarMissoesPorStatus(StatusMissao status) {
        List<Missao> resultado = new ArrayList<>();
        for (Missao m: missoes.values()) {
            if (m.getStatus() == status) {
                resultado.add(m);
            }
        }
        return resultado;
    }

   public List<Missao> listarMissoesPendentes() {
        return missoesPendentes.stream()
                .sorted(comparadorFila())      
                .collect(Collectors.toList());
    }

    public List<Robo> listarRobosDisponiveis() {
        List<Robo> disponiveis = new ArrayList<>();
        for (Robo robo : robos.values()) {
            if (robo.verificarDisponibilidade()) {
                disponiveis.add(robo);
            }
        }
        return disponiveis;
    }


    public int reprocessarPendentes() {
    int atendidas = 0;
    List<Missao> candidatas = new ArrayList<>(missoesPendentes);
    candidatas.sort(comparadorFila());
    for (Missao m : candidatas) {
        if (m.getStatus() == StatusMissao.PENDENTE && atribuirRoboMissao(m)) {
            missoesPendentes.remove(m);
            atendidas++;
        }
    }
    return atendidas;
    }
    
    
    private double calcularScore(Robo robo, Missao missao) {
        double sobra = robo.consultarBateria() - consumoPrevisto(robo, missao);
        double peso = (missao.getArea().getNivelRisco() == NivelRisco.ALTO) ? 2.0 : 1.0;
        return sobra * peso - robo.getMissoesRealizadas();
    }

    private boolean estaComprometido(Robo robo) {
    return missoes.values().stream()
            .anyMatch(m -> m.getRoboResponsavel() == robo
                    && (m.getStatus() == StatusMissao.PENDENTE
                        || m.getStatus() == StatusMissao.EM_ANDAMENTO));
    }

    private double consumoPrevisto(Robo robo, Missao missao) {
    AreaOperacao a = missao.getArea();
    return robo.calcularConsumo(missao.getTipo(), a.getTipoTerreno(), a.getNivelRisco());
    }

    private double pontuacaoFila(Missao m) {
    return m.getPrioridade().getPeso() * 10
            + m.getArea().getNivelRisco().getMultiplicador();
    }

    private Comparator<Missao> comparadorFila() {
    return Comparator.comparingDouble((Missao m) -> pontuacaoFila(m)).reversed()
            .thenComparingInt(Missao::getId);
    }
   
    public double calcularConsumo(TipoMissao tipo, TipoTerreno terreno, NivelRisco risco) {
    return tipo.getPercentualBateria() * terreno.getMultiplicador() * risco.getMultiplicador();
}
}