package excecoes;

public class UsuarioNaoEncontradoException extends Exception {
    public UsuarioNaoEncontradoException(String email) {
        super("Usuário com email '" + email + "' não encontrado no sistema.");
    }
}
