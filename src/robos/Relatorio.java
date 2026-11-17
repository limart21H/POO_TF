package robos;

import enums.StatusMissao;
import enums.StatusRobo;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import operacoes.CentralResgate;
import operacoes.Missao;

public class Relatorio {
    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private final LocalDateTime dataGeracao;
    private final int totalRobos;
    private final int totalMissoes;
    private final int missoesConcluidas;
    private final int missoesPendentes;
    private final int totalAreas;
    private final List<Robo> robos;
    private final List<Missao> missoes;

    public Relatorio(CentralResgate central) {
        if (central == null) {
            throw new IllegalArgumentException("A central não pode ser nula.");
        }
        this.dataGeracao = LocalDateTime.now();
        this.robos = central.listarRobos();
        this.missoes = central.listarMissoes();
        this.totalRobos = robos.size();
        this.totalMissoes = missoes.size();
        this.missoesConcluidas = (int) missoes.stream()
                .filter(m -> m.getStatus() == StatusMissao.CONCLUIDA)
                .count();
        this.missoesPendentes = central.listarMissoesPendentes().size();
        this.totalAreas = central.listarAreas().size();
    }

    public String gerarResumo() {
        long disponiveis = robos.stream()
                .filter(r -> r.verificarDisponibilidade())
                .count();
        double bateriaMedia = robos.stream()
                .mapToDouble(r -> r.consultarBateria())
                .average()
                .orElse(0);
        List<Robo> ranking = robos.stream()
                .sorted((a, b) -> Integer.compare(b.getMissoesRealizadas(), a.getMissoesRealizadas()))
                .collect(Collectors.toList());

        String quebra = System.lineSeparator();
        StringBuilder sb = new StringBuilder();
        sb.append("===== RELATÓRIO GERAL DA CENTRAL =====").append(quebra);
        sb.append("Gerado em: ").append(dataGeracao.format(FORMATO)).append(quebra);
        sb.append("Total de robôs: ").append(totalRobos).append(quebra);
        sb.append("Total de áreas: ").append(totalAreas).append(quebra);
        sb.append("Total de missões: ").append(totalMissoes).append(quebra);
        sb.append("Missões concluídas: ").append(missoesConcluidas).append(quebra);
        sb.append("Missões pendentes na fila: ").append(missoesPendentes).append(quebra);
        sb.append("Missões por status:").append(quebra);
        for (StatusMissao s : StatusMissao.values()) {
            long qtd = missoes.stream().filter(m -> m.getStatus() == s).count();
            sb.append("  ").append(s).append(": ").append(qtd).append(quebra);
        }
        sb.append("Robôs disponíveis: ").append(disponiveis).append(quebra);
        sb.append(String.format("Bateria média dos robôs: %.1f%%", bateriaMedia)).append(quebra);
        sb.append("Robôs por status:").append(quebra);
        for (StatusRobo s : StatusRobo.values()) {
            long qtd = robos.stream().filter(r -> r.getStatus() == s).count();
            sb.append("  ").append(s).append(": ").append(qtd).append(quebra);
        }
        sb.append("Ranking de missões realizadas:").append(quebra);
        for (int i = 0; i < ranking.size(); i++) {
            Robo r = ranking.get(i);
            sb.append("  ").append(i + 1).append(". ").append(r.getNome())
                    .append(" (").append(r.getClass().getSimpleName()).append(") - ")
                    .append(r.getMissoesRealizadas()).append(" missão(ões)").append(quebra);
        }
        return sb.toString();
    }

    public void salvarEmArquivo(String caminho) throws IOException {
        if (caminho == null || caminho.isBlank()) {
            throw new IllegalArgumentException("O caminho do arquivo não pode ser vazio.");
        }
        try (BufferedWriter escritor = Files.newBufferedWriter(Paths.get(caminho), StandardCharsets.UTF_8)) {
            escritor.write(gerarResumo());
        }
    }

    public LocalDateTime getDataGeracao() { 
        return dataGeracao; 
    }

    public int getTotalRobos() { 
        return totalRobos; 
    }

    public int getTotalMissoes() { 
        return totalMissoes; 
    }

    public int getMissoesConcluidas() { 
        return missoesConcluidas; 
    }

    public int getMissoesPendentes() { 
        return missoesPendentes; 
    }
}
