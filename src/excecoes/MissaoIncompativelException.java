package excecoes;

public class MissaoIncompativelException extends RegraNegocioException {
    public MissaoIncompativelException(String msg) {
        super(msg);
    }
}
