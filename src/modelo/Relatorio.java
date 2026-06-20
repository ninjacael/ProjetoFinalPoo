package modelo;

import java.time.LocalDate;

public class Relatorio {

    private static int contadorId = 1;

    private final int id;
    private String titulo;
    private String conteudo;
    private Aluno aluno;
    private Projeto projeto;
    private LocalDate dataEnvio;
    private StatusRelatorio status;
    private String feedback;
    private boolean aprovado;

    public Relatorio(String titulo, String conteudo, Aluno aluno, Projeto projeto) {
        this.id = contadorId++;
        this.titulo = titulo;
        this.conteudo = conteudo;
        this.aluno = aluno;
        this.projeto = projeto;
        this.dataEnvio = LocalDate.now();
        this.status = StatusRelatorio.PENDENTE;
        this.feedback = "";
        this.aprovado = false;
    }

    public String exibirDetalhes() {
        return "ID: " + id
                + "\nTítulo: " + titulo
                + "\nAluno: " + aluno.getNome()
                + "\nProjeto: " + projeto.getTitulo()
                + "\nData de Envio: " + dataEnvio
                + "\nStatus: " + status
                + "\nFeedback: " + (feedback.isEmpty() ? "Aguardando avaliação" : feedback)
                + "\nAprovado: " + (aprovado ? "Sim" : "Não");
    }

    public int getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getConteudo() {
        return conteudo;
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }

    public Aluno getAluno() {
        return aluno;
    }

    public Projeto getProjeto() {
        return projeto;
    }

    public LocalDate getDataEnvio() {
        return dataEnvio;
    }

    public StatusRelatorio getStatus() {
        return status;
    }

    public void setStatus(StatusRelatorio status) {
        this.status = status;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public boolean isAprovado() {
        return aprovado;
    }

    public void setAprovado(boolean aprovado) {
        this.aprovado = aprovado;
    }

    @Override
    public String toString() {
        return "[Relatório #" + id + "] " + titulo + " - " + aluno.getNome() + " | " + status;
    }
}
