package robos;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import excecoes.DadosInvalidosException;

public class Mensagem implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private final String autor;
    private final String conteudo;
    private final boolean enviada;
    private final LocalDateTime dataHora;

    public Mensagem(String autor, String conteudo, boolean enviada) throws DadosInvalidosException {
        if (autor == null || autor.isBlank()) {
            throw new DadosInvalidosException("Autor não pode ser nulo ou vazio.");
        }
        if (conteudo == null || conteudo.isBlank()) {
            throw new DadosInvalidosException("Conteúdo não pode ser nulo ou vazio.");
        }
        this.autor = autor;
        this.conteudo = conteudo;
        this.enviada = enviada;
        this.dataHora = LocalDateTime.now();
    }

    public String getAutor() {
        return autor;
    }

    public String getConteudo() {
        return conteudo;
    }

    public boolean isEnviada() {
        return enviada;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    @Override
    public String toString() {
        return "[" + dataHora.format(FORMATO) + "] " + (enviada ? "Enviada" : "Recebida") + " | " + autor + ": " + conteudo;
    }
}
