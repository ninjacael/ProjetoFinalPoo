package excecoes;

public class ProjetoEncerradoException extends Exception {
    public ProjetoEncerradoException(String tituloProjeto) {
        super("O projeto '" + tituloProjeto + "' está encerrado e não aceita novas inscrições.");
    }
}
