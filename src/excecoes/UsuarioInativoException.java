package excecoes;

public class UsuarioInativoException extends Exception {
    public UsuarioInativoException(String nome) {
        super("A conta do usuário '" + nome + "' está desativada. Entre em contato com o coordenador.");
    }
}
