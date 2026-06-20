package modelo;

import java.util.List;

public class Coordenador extends Professor implements Gerenciavel {

    private static final String CARGO = "Coordenador de Pesquisa";
    private int nivelAcesso;

    public Coordenador(String nome, String email, String senha, String departamento, String titulacao) {
        super(nome, email, senha, departamento, titulacao);
        this.nivelAcesso = 3;
    }

    @Override
    public String getTipoUsuario() {
        return "COORDENADOR";
    }

    @Override
    public String[] getOpcoesMenu() {
        return new String[] {
                "1 - Gerenciar projetos",
                "2 - Gerenciar usuários",
                "3 - Gerar relatórios",
                "4 - Estatísticas gerais",
                "5 - Enviar notificação global",
                "6 - Ver notificações",
                "7 - Editar perfil",
                "8 - Sair"
        };
    }

    @Override
    public String exibirPerfil() {
        return super.exibirPerfil()
                + "\nCargo: " + CARGO
                + "\nNível de Acesso: " + nivelAcesso;
    }

    // Implementação da interface Gerenciavel
    @Override
    public void ativarUsuario(Usuario usuario) {
        usuario.setAtivo(true);
        usuario.adicionarNotificacao("Sua conta foi reativada pelo coordenador.");
    }

    @Override
    public void desativarUsuario(Usuario usuario) {
        usuario.setAtivo(false);
        usuario.adicionarNotificacao("Sua conta foi desativada pelo coordenador.");
    }

    @Override
    public void removerProjeto(Projeto projeto) {
        projeto.setStatus(StatusProjeto.ENCERRADO);
    }

    public static String getCargo() {
        return CARGO;
    }

    public int getNivelAcesso() {
        return nivelAcesso;
    }
}
