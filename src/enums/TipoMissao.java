package enums;

public enum TipoMissao {
    RECONHECIMENTO("Reconhecimento", 20),
    LOCALIZAR_PESSOAS("Localizar Pessoas", 30),
    TRANSPORTAR_SUPRIMENTOS("Transportar Suprimentos", 40);

    private final String descricao;
    private final int percentualBateria;

    TipoMissao(String descricao, int percentualBateria) {
        this.descricao = descricao;
        this.percentualBateria = percentualBateria;
    }

    public String getDescricao() {
        return descricao;
    }

    public int getPercentualBateria() {
        return percentualBateria;
    }
}