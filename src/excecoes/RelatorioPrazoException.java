package excecoes;

public class RelatorioPrazoException extends Exception {
    public RelatorioPrazoException(String tituloProjeto) {
        super("O prazo de entrega de relatórios do projeto '" + tituloProjeto + "' já foi encerrado.");
    }
}
