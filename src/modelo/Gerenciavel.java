package modelo;

public interface Gerenciavel {
    void ativarUsuario(Usuario usuario);

    void desativarUsuario(Usuario usuario);

    void removerProjeto(Projeto projeto);
}
