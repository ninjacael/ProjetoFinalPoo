package excecoes;

public class CredenciaisInvalidasException extends Exception {
    public CredenciaisInvalidasException() {
        super("Email ou senha incorretos. Verifique suas credenciais e tente novamente.");
    }
}
