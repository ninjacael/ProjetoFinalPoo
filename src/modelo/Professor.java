package modelo;

import java.util.ArrayList;
import java.util.List;

public class Professor extends Usuario implements Avaliavel {

    private String departamento;
    private String titulacao;
    private List<Projeto> projetosCriados;

    public Professor(String nome, String email, String senha, String departamento, String titulacao) {
        super(nome, email, senha);
        this.departamento = departamento;
        this.titulacao = titulacao;
        this.projetosCriados = new ArrayList<>();
    }

    @Override
    public String getTipoUsuario() {
        return "PROFESSOR";
    }

    @Override
    public String[] getOpcoesMenu() {
        return new String[] {
                "1 - Criar novo projeto",
                "2 - Editar projeto existente",
                "3 - Remover projeto",
                "4 - Visualizar inscritos",
                "5 - Avaliar relatório de aluno",
                "6 - Enviar notificação",
                "7 - Ver notificações",
                "8 - Editar perfil",
                "9 - Sair"
        };
    }

    @Override
    public String exibirPerfil() {
        return super.exibirPerfil()
                + "\nDepartamento: " + departamento
                + "\nTitulação: " + titulacao
                + "\nProjetos Criados: " + projetosCriados.size();
    }

    @Override
    public void avaliarRelatorio(Relatorio relatorio, String feedback, boolean aprovado) {
        relatorio.setFeedback(feedback);
        relatorio.setAprovado(aprovado);
        relatorio.setStatus(aprovado ? StatusRelatorio.APROVADO : StatusRelatorio.REPROVADO);

        relatorio.getAluno().adicionarNotificacao(
                "Seu relatório '" + relatorio.getTitulo() + "' foi "
                        + (aprovado ? "APROVADO" : "REPROVADO") + ". Feedback: " + feedback);
    }

    @Override
    public boolean podeAvaliar(Projeto projeto) {
        return projetosCriados.contains(projeto);
    }

    public void adicionarProjeto(Projeto projeto) {
        projetosCriados.add(projeto);
    }

    public void removerProjeto(Projeto projeto) {
        projetosCriados.remove(projeto);
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public String getTitulacao() {
        return titulacao;
    }

    public void setTitulacao(String titulacao) {
        this.titulacao = titulacao;
    }

    public List<Projeto> getProjetosCriados() {
        return new ArrayList<>(projetosCriados);
    }
}
