package app;

import enums.NivelRisco;
import enums.PrioridadeMissao;
import enums.StatusMissao;
import enums.TipoMissao;
import enums.TipoTerreno;
import operacoes.AreaOperacao;
import operacoes.CentralResgate;
import operacoes.Missao;
import robos.Drone;
import robos.Robo;
import robos.RoboTerrestre;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.ArrayList;

import java.util.Scanner;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static CentralResgate central;
    public static void main(String[] args){
        central = Persistencia.carregar();
        boolean rodando = true;
        while(rodando){
            mostrarMenu();
            int opcao = lerOpecaoMenu();
            
            try{
                switch(opcao){
                    case 1:
                        cadastrarRobo();
                        break;
                    case 2:
                        cadastrarArea();
                        break;
                    case 3:
                        solicitarMissao();
                        break;
                    case 4:
                        executarMissao();
                        break;
                    case 5:
                        listarRobos();
                        break;
                    case 6:
                        listarMissoes();
                        break;
                    case 7:
                        recarregarRobo();
                        break;
                    case 8:
                        cancelarMissao();
                        break;
                    case 9:
                        mostrarRelatorio();
                        break;
                    case 10:
                        carregarDemonstracao();
                        break;
                    case 0:
                        rodando = !sair();
                        break;
                    default:
                        System.out.println("Opção inválida. Escolha um número do menu.");
                }
            }catch(RuntimeException e){
                System.out.println("Erro: " + e.getMessage());
            }
        }
        System.out.print("Programa encerrado.");
    }

    private static void mostrarMenu() {
        System.out.println();
        System.out.println("========= CENTRAL DE RESGATE =========");
        System.out.println("1  - Cadastrar robô");
        System.out.println("2  - Cadastrar área");
        System.out.println("3  - Solicitar missão");
        System.out.println("4  - Executar missão");
        System.out.println("5  - Listar robôs");
        System.out.println("6  - Listar missões");
        System.out.println("7  - Recarregar robô");
        System.out.println("8  - Cancelar missão");
        System.out.println("9  - Relatório geral");
        System.out.println("10 - Carregar dados de demonstração");
        System.out.println("0  - Salvar e sair");
    }

    private static int lerOpcaoMenu(){
        System.out.print("Escolha uma opção: ");
        try{
            return Integer.parseInt(lerLinha().trim());
        }catch(NumberFormatException e){
            return -1;
        }
    }

    private static boolean sair(){
        if(Persistencia.salvar(central)){
            System.out.println("Dados salvos.");
            return true;
        }
        System.out.println("Não foi possível salvar. sair mesmo assim?(S/N): ");
        return lerLinha().trim().equalsIgnoreCase("S");
    }


    private static void cadastrarRobo() {
        Integer tipo = lerInteiro("Tipo do robô (1 - Drone, 2 - Robô Terrestre): ", 1, 2);
        if (tipo == null) return;

        String nome = lerTexto("Nome do robô: ");
        if (nome == null) return;

        Double bateria = lerDecimal("Bateria inicial (0 a 100): ", 0, 100);
        if (bateria == null) return;

        Robo robo;
        if (tipo == 1) {
            Double altitude = lerDecimal("Altitude máxima em metros (0.01 a 10000): ", 0.01, 10000);
            if (altitude == null) return;
            robo = new Drone(proximoIdRobo(), nome, bateria, altitude);
        } else {
            Double carga = lerDecimal("Capacidade de carga em kg (0.01 a 10000): ", 0.01, 10000);
            if (carga == null) return;
            robo = new RoboTerrestre(proximoIdRobo(), nome, bateria, carga);
        }

        if (central.cadastrarRobo(robo)) {
            System.out.println("Robô cadastrado com ID " + robo.getId() + ".");
            robo.exibirResumo();
        }
    }

    private static void cadastrarArea() {
        String nome = lerTexto("Nome da área: ");
        if (nome == null) return;

        String localizacao = lerTexto("Localização: ");
        if (localizacao == null) return;

        String descricao = lerTexto("Descrição: ");
        if (descricao == null) return;

        TipoTerreno terreno = escolherEnum("Tipo de terreno:", TipoTerreno.values());
        if (terreno == null) return;

        NivelRisco risco = escolherEnum("Nível de risco:", NivelRisco.values());
        if (risco == null) return;

        AreaOperacao area = new AreaOperacao(nome, proximoIdArea(), localizacao, terreno, descricao, risco);

        if (central.cadastrarArea(area)) {
            System.out.println("Área cadastrada com ID " + area.getId() + ".");
        }
    }

    private static void solicitarMissao() {
        List<AreaOperacao> areas = central.listarAreas();
        if (areas.isEmpty()) {
            System.out.println("Nenhuma área cadastrada. Cadastre uma área antes de solicitar uma missão.");
            return;
        }

        System.out.println("Áreas disponíveis:");
        for (AreaOperacao a : areas) {
            System.out.println("  " + a.getId() + " - " + a.getNome() + " (" + a.getTipoTerreno() + ", risco " + a.getNivelRisco() + ")");
        }

        AreaOperacao area = pedirArea();
        if (area == null) return;

        String descricao = lerTexto("Descrição da missão: ");
        if (descricao == null) return;

        TipoMissao tipo = escolherEnum("Tipo da missão:", TipoMissao.values());
        if (tipo == null) return;

        PrioridadeMissao prioridade = escolherEnum("Prioridade:", PrioridadeMissao.values());
        if (prioridade == null) return;

        Missao missao = new Missao(proximoIdMissao(), descricao, tipo, area, prioridade);

        if (central.registrarMissao(missao)) {
            System.out.println("Missão registrada com ID " + missao.getId() + ".");
            if (missao.getRoboResponsavel() != null) {
                System.out.println("Atribuída ao robô " + missao.getRoboResponsavel().getNome() + ".");
            } else {
                System.out.println("Sem robô disponível no momento: missão na fila de pendentes.");
            }
        }
    }

    private static void executarMissao() {
        List<Missao> prontas = new ArrayList<>();
        for (Missao m : central.listarMissoes()) {
            if (m.getStatus() == StatusMissao.PENDENTE && m.getRoboResponsavel() != null) {
                prontas.add(m);
            }
    }

        if (prontas.isEmpty()) {
            System.out.println("Nenhuma missão pronta para executar (é preciso ter robô atribuído).");
            return;
        }

        System.out.println("Missões prontas para executar:");
        for (Missao m : prontas) {
            System.out.println("  " + m.getId() + " - " + m.getDescricao() + " [robô: " + m.getRoboResponsavel().getNome() + "]");
        }

        Missao missao = pedirMissao();
        if (missao == null) return;

        if (central.executarMissao(missao.getId())) {
            System.out.println();
            missao.exibirResumo();
            missao.getRoboResponsavel().exibirResumo();
        } else {
            System.out.println("A missão não foi executada.");
        }
    }

    private static void listarRobos() {
        Integer filtro = lerInteiro("1 - Todos os robôs, 2 - Somente disponíveis: ", 1, 2);
        if (filtro == null) return;

        List<Robo> robos;
        
        if (filtro == 1) {
            robos = central.listarRobos();
        } else {
            robos = central.listarRobosDisponiveis();
        }

        if (robos.isEmpty()) {
            System.out.println("Nenhum robô encontrado.");
            return;
        }
        for (Robo r : robos) {
            r.exibirResumo();
        }
    }

    private static void listarMissoes() {
        Integer filtro = lerInteiro("1 - Todas, 2 - Pendentes, 3 - Concluídas (histórico): ", 1, 3);
        if (filtro == null) return;

        List<Missao> missoes;
        if (filtro == 1) {
            missoes = central.listarMissoes();
        } else if (filtro == 2) {
            missoes = central.listarMissoesPendentes();
        } else {
            missoes = central.listarMissoesPorStatus(StatusMissao.CONCLUIDA);
        }

        if (missoes.isEmpty()) {
            System.out.println("Nenhuma missão encontrada.");
            return;
        }
        for (Missao m : missoes) {
            m.exibirResumo();
        }
    }

    
    private static void recarregarRobo() {
        if (central.listarRobos().isEmpty()) {
            System.out.println("Nenhum robô cadastrado.");
            return;
        }

        Robo robo = pedirRobo();
        if (robo == null) return;

        robo.recarregar();
        robo.exibirResumo();

        int atendidas = central.reprocessarPendentes();
        System.out.println("Missões pendentes atendidas após a recarga: " + atendidas);
    }

    private static void cancelarMissao() {
        // A central ainda não tem um método próprio de cancelamento.
        // Não chame missao.cancelar() direto: a missão continuaria na fila de pendentes.
        System.out.println("Cancelamento de missão em desenvolvimento.");
    }

    private static void mostrarRelatorio() {
        // Quando a classe Relatorio estiver pronta, trocar por: criar o Relatorio com a central,
        // imprimir gerarResumo() e oferecer salvarEmArquivo() (tratando IOException).
        central.gerarRelatorioGeral();
    }

    private static void carregarDemonstracao() {
        if (!central.listarRobos().isEmpty() || !central.listarAreas().isEmpty()
                || !central.listarMissoes().isEmpty()) {
            System.out.println("Já existem dados cadastrados. Apague a pasta 'dados' e reinicie para recarregar a demonstração.");
            return;
        }

        central.cadastrarArea(new AreaOperacao("Centro Histórico", 1, "Região central", TipoTerreno.URBANO, "Ruas estreitas com prédios antigos", NivelRisco.BAIXO));
        central.cadastrarArea(new AreaOperacao("Vila Alagada", 2, "Zona sul", TipoTerreno.ALAGADO, "Bairro atingido por enchente", NivelRisco.MEDIO));
        central.cadastrarArea(new AreaOperacao("Encosta do Morro", 3, "Zona leste", TipoTerreno.IRREGULAR, "Área com risco de deslizamento", NivelRisco.ALTO));
        central.cadastrarArea(new AreaOperacao("Zona Industrial", 4, "Zona norte", TipoTerreno.ALAGADO, "Galpões inundados", NivelRisco.ALTO));

        central.cadastrarRobo(new Drone(1, "Falcão", 100, 120));
        central.cadastrarRobo(new Drone(2, "Gavião", 40, 100));
        central.cadastrarRobo(new Drone(3, "Harpia", 0, 80));
        central.cadastrarRobo(new RoboTerrestre(4, "Rinoceronte", 100, 200));
        central.cadastrarRobo(new RoboTerrestre(5, "Leopardo", 50, 80));

        central.registrarMissao(new Missao(1, "Reconhecer o Centro Histórico", TipoMissao.RECONHECIMENTO, central.buscarArea(1), PrioridadeMissao.MEDIA));
        central.registrarMissao(new Missao(2, "Procurar pessoas na encosta", TipoMissao.LOCALIZAR_PESSOAS, central.buscarArea(3), PrioridadeMissao.ALTA));
        central.registrarMissao(new Missao(3, "Levar suprimentos ao Centro Histórico", TipoMissao.TRANSPORTAR_SUPRIMENTOS, central.buscarArea(1), PrioridadeMissao.ALTA));
        central.registrarMissao(new Missao(4, "Levar suprimentos à Vila Alagada", TipoMissao.TRANSPORTAR_SUPRIMENTOS, central.buscarArea(2), PrioridadeMissao.MEDIA));

        System.out.println("Dados de demonstração carregados.");
    }

    private static int proximoIdRobo() {
        return central.listarRobos().stream().mapToInt(Robo::getId).max().orElse(0) + 1;
    }

    private static int proximoIdArea() {
        return central.listarAreas().stream().mapToInt(AreaOperacao::getId).max().orElse(0) + 1;
    }

    private static int proximoIdMissao() {
        return central.listarMissoes().stream().mapToInt(Missao::getId).max().orElse(0) + 1;
    }

    private static Robo pedirRobo() {
        while (true) {
            Integer id = lerInteiro("ID do robô: ", 1, Integer.MAX_VALUE);
            if (id == null) return null;
            Robo robo = central.buscarRobo(id); // a central já imprime "não encontrado"
            if (robo != null) return robo;
            if (!digitarNovamente("")) return null;
        }
    }

    private static AreaOperacao pedirArea() {
        while (true) {
            Integer id = lerInteiro("ID da área: ", 1, Integer.MAX_VALUE);
            if (id == null) return null;
            AreaOperacao area = central.buscarArea(id);
            if (area != null) return area;
            if (!digitarNovamente("")) return null;
        }
    }

    private static Missao pedirMissao() {
        while (true) {
            Integer id = lerInteiro("ID da missão: ", 1, Integer.MAX_VALUE);
            if (id == null) return null;
            Missao missao = central.buscarMissao(id);
            if (missao != null) return missao;
            if (!digitarNovamente("")) return null;
        }
    }


    private static String lerLinha(){
        try {
            return scanner.nextLine();
        } catch (NoSuchElementException e) {
            System.out.println();
            System.out.println("Entrada encerrada.");
            Persistencia.salvar(central);
            System.exit(0);
            return "";
        }
    }

    /** Mostra o motivo do erro e pergunta se a pessoa quer tentar de novo. true = tentar de novo. */
    private static boolean digitarNovamente(String motivo) {
        if (!motivo.isEmpty()) {
            System.out.println(motivo);
        }
        while (true) {
            System.out.print("1 - Digitar novamente | 0 - Voltar ao menu: ");
            String resposta = lerLinha().trim();
            if (resposta.equals("1")) return true;
            if (resposta.equals("0")) return false;
            System.out.println("Digite 1 ou 0.");
        }
    }

    private static String lerTexto(String pergunta) {
        while (true) {
            System.out.print(pergunta);
            String texto = lerLinha().trim();
            if (!texto.isEmpty()) return texto;
            if (!digitarNovamente("O campo não pode ficar vazio.")) return null;
        }
    }

    private static Integer lerInteiro(String pergunta, int minimo, int maximo) {
        while (true) {
            System.out.print(pergunta);
            String texto = lerLinha().trim();
            try {
                int valor = Integer.parseInt(texto);
                if (valor >= minimo && valor <= maximo) return valor;
            } catch (NumberFormatException e) {

            }
            String faixa;

            if (maximo == Integer.MAX_VALUE) {
                faixa = "maior ou igual a " + minimo;
            } else {
                faixa = "entre " + minimo + " e " + maximo;
            }
            if (!digitarNovamente("Valor inválido. Digite um número inteiro " + faixa + ".")) return null;
        }
    }

    private static Double lerDecimal(String pergunta, double minimo, double maximo) {
        while (true) {
            System.out.print(pergunta);
            String texto = lerLinha().trim().replace(',', '.');
            try {
                double valor = Double.parseDouble(texto);
                
                if (!Double.isNaN(valor) && !Double.isInfinite(valor)
                        && valor >= minimo && valor <= maximo) {
                    return valor;
                }
            } catch (NumberFormatException e) {
            }
            if (!digitarNovamente("Valor inválido. Digite um número entre " + minimo + " e " + maximo + ".")) {
                return null;
            }
        }
    }

    private static <T> T escolherEnum(String titulo, T[] valores) {
        System.out.println(titulo);
        for (int i = 0; i < valores.length; i++) {
            System.out.println("  " + (i + 1) + " - " + valores[i]);
        }
        Integer escolha = lerInteiro("Escolha: ", 1, valores.length);
        if (escolha == null) return null;
        return valores[escolha - 1];
    }
    }
}
