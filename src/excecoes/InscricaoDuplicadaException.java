package excecoes;

public class InscricaoDuplicadaException extends Exception {
    public InscricaoDuplicadaException(String tituloProjeto) {
        super("Você já está inscrito no projeto '" + tituloProjeto + "'.");
    }
}
