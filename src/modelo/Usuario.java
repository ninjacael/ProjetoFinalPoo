package modelo;

import java.util.ArrayList;
import java.util.List;

public abstract class Usuario {

    private static int contadorId = 1;
    public static final String SISTEMA_NOME = "Sistema de Gerenciamento de Pesquisas UFC";

    private final int id;
    private String nome;
    private String email;
    private String senha;
    private boolean ativo;
    private List<String> notificacoes;

    public Usuario(String nome, String email, String senha) {
        this.id = contadorId++;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.ativo = true;
        this.notificacoes = new ArrayList<>();
    }

    public abstract String getTipoUsuario();

    public abstract String[] getOpcoesMenu();

    public String exibirPerfil() {
        return "ID: " + id + "\nNome: " + nome + "\nEmail: " + email
                + "\nTipo: " + getTipoUsuario() + "\nStatus: " + (ativo ? "Ativo" : "Inativo");
    }

    public void adicionarNotificacao(String mensagem) {
        notificacoes.add(mensagem);
    }

    public List<String> getNotificacoes() {
        return new ArrayList<>(notificacoes);
    }

    public void limparNotificacoes() {
        notificacoes.clear();
    }

    public static int getTotalUsuariosCriados() {
        return contadorId - 1;
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    @Override
    public String toString() {
        return "[" + getTipoUsuario() + "] " + nome + " (" + email + ")";
    }
}
