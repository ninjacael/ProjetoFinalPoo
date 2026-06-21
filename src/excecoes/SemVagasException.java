package excecoes;

public class SemVagasException extends Exception {
    public SemVagasException(String tituloProjeto) {
        super("O projeto '" + tituloProjeto + "' não possui vagas disponíveis no momento.");
    }
}
