package padroes;

import modelo.Aluno;
import modelo.Projeto;
import modelo.StatusProjeto;
import modelo.Usuario;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class GerenciadorNotificacoes {

    private static GerenciadorNotificacoes instancia;
    private static int totalNotificacoesEnviadas = 0;

    private List<String> logNotificacoes;

    private GerenciadorNotificacoes() {
        this.logNotificacoes = new ArrayList<>();
    }

    public static GerenciadorNotificacoes getInstancia() {
        if (instancia == null) {
            instancia = new GerenciadorNotificacoes();
        }
        return instancia;
    }

    public void enviarNotificacao(Usuario destinatario, String mensagem) {
        destinatario.adicionarNotificacao(mensagem);
        logNotificacoes.add("[" + LocalDate.now() + "] Para: " + destinatario.getNome() + " → " + mensagem);
        totalNotificacoesEnviadas++;
    }

    public void enviarNotificacaoGlobal(List<Usuario> usuarios, String mensagem) {
        for (Usuario u : usuarios) {
            enviarNotificacao(u, mensagem);
        }
    }

    public void verificarPrazosVencendo(List<Projeto> projetos) {
        for (Projeto p : projetos) {
            if (p.getStatus() == StatusProjeto.ABERTO || p.getStatus() == StatusProjeto.EM_ANDAMENTO) {
                long diasRestantes = ChronoUnit.DAYS.between(LocalDate.now(), p.getPrazo());
                if (diasRestantes >= 0 && diasRestantes <= 7) {
                    String aviso = "⚠ PRAZO PRÓXIMO: O projeto '" + p.getTitulo()
                            + "' encerra em " + diasRestantes + " dia(s) (" + p.getPrazo() + ").";
                    for (Aluno aluno : p.getParticipantes()) {
                        enviarNotificacao(aluno, aviso);
                    }
                    enviarNotificacao(p.getOrientador(), aviso);
                }
                if (diasRestantes < 0 && p.getStatus() != StatusProjeto.ENCERRADO) {
                    p.setStatus(StatusProjeto.ENCERRADO);
                    String encerrado = "O projeto '" + p.getTitulo()
                            + "' foi encerrado automaticamente por prazo vencido.";
                    for (Aluno aluno : p.getParticipantes()) {
                        aluno.concluirProjeto(p);
                        enviarNotificacao(aluno, encerrado);
                    }
                    enviarNotificacao(p.getOrientador(), encerrado);
                }
            }
        }
    }

    public List<String> getLogNotificacoes() {
        return new ArrayList<>(logNotificacoes);
    }

    public static int getTotalNotificacoesEnviadas() {
        return totalNotificacoesEnviadas;
    }
}
