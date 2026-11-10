package robos;

import java.io.Serializable;

public abstract class Robo implements Comunicavel, Serializable {

    private final HistoricoMenssagem historico = new HistoricoMenssagem();

    @override
    public void enviarMensagem(String mensagem) {
        historico.registrar(new Mensagem(nome, mensagem, true));
        System.out.println(nome + " -> CENTRAL: " + mensagem);
    }
