package modelo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Projeto {

    private static int contadorId = 1;
    public static final int VAGAS_MINIMAS = 1;
    public static final int VAGAS_MAXIMAS = 20;

    private final int id;
    private String titulo;
    private String area;
    private String descricao;
    private Professor orientador;
    private LocalDate dataInicio;
    private LocalDate prazo;
    private int totalVagas;
    private StatusProjeto status;
    private List<Aluno> participantes;
    private List<Aluno> listaEspera;
    private List<Relatorio> relatorios;
    private List<String> solicitacoesPendentes;

    public Projeto(String titulo, String area, String descricao,
            Professor orientador, LocalDate dataInicio,
            LocalDate prazo, int totalVagas) {
        this.id = contadorId++;
        this.titulo = titulo;
        this.area = area;
        this.descricao = descricao;
        this.orientador = orientador;
        this.dataInicio = dataInicio;
        this.prazo = prazo;
        this.totalVagas = totalVagas;
        this.status = StatusProjeto.ABERTO;
        this.participantes = new ArrayList<>();
        this.listaEspera = new ArrayList<>();
        this.relatorios = new ArrayList<>();
        this.solicitacoesPendentes = new ArrayList<>();
    }

    public boolean temVagasDisponiveis() {
        return participantes.size() < totalVagas;
    }

    public int getVagasDisponiveis() {
        return totalVagas - participantes.size();
    }

    public boolean adicionarParticipante(Aluno aluno) {
        if (!participantes.contains(aluno) && temVagasDisponiveis()
                && (status == StatusProjeto.ABERTO || status == StatusProjeto.EM_ANDAMENTO)) {
            participantes.add(aluno);
            solicitacoesPendentes.remove(aluno.getEmail());
            return true;
        }
        return false;
    }

    public boolean removerParticipante(Aluno aluno) {
        boolean removido = participantes.remove(aluno);
        if (removido && !listaEspera.isEmpty()) {
            Aluno proximo = listaEspera.remove(0);
            participantes.add(proximo);
            proximo.adicionarNotificacao(
                    "Você foi movido da lista de espera para participante no projeto: " + titulo);
        }
        return removido;
    }

    public void adicionarListaEspera(Aluno aluno) {
        if (!listaEspera.contains(aluno) && !participantes.contains(aluno)) {
            listaEspera.add(aluno);
        }
    }

    public void adicionarRelatorio(Relatorio relatorio) {
        relatorios.add(relatorio);
    }

    public void solicitarParticipacao(String emailAluno) {
        if (!solicitacoesPendentes.contains(emailAluno)) {
            solicitacoesPendentes.add(emailAluno);
        }
    }

    public boolean isPrazoVencido() {
        return LocalDate.now().isAfter(prazo);
    }

    public String exibirResumo() {
        return "ID: " + id
                + "\nTítulo: " + titulo
                + "\nÁrea: " + area
                + "\nOrientador: " + orientador.getNome()
                + "\nVagas disponíveis: " + getVagasDisponiveis() + "/" + totalVagas
                + "\nStatus: " + status
                + "\nPrazo: " + prazo;
    }

    public String exibirDetalhes() {
        StringBuilder sb = new StringBuilder(exibirResumo());
        sb.append("\nDescrição: ").append(descricao);
        sb.append("\nData de Início: ").append(dataInicio);
        sb.append("\nParticipantes (").append(participantes.size()).append("):");
        for (Aluno a : participantes) {
            sb.append("\n  - ").append(a.getNome()).append(" [").append(a.getMatricula()).append("]");
        }
        sb.append("\nRelatórios: ").append(relatorios.size());
        return sb.toString();
    }

    public static int getTotalProjetosCriados() {
        return contadorId - 1;
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

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Professor getOrientador() {
        return orientador;
    }

    public void setOrientador(Professor orientador) {
        this.orientador = orientador;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
    }

    public LocalDate getPrazo() {
        return prazo;
    }

    public void setPrazo(LocalDate prazo) {
        this.prazo = prazo;
    }

    public int getTotalVagas() {
        return totalVagas;
    }

    public void setTotalVagas(int totalVagas) {
        this.totalVagas = totalVagas;
    }

    public StatusProjeto getStatus() {
        return status;
    }

    public void setStatus(StatusProjeto status) {
        this.status = status;
    }

    public List<Aluno> getParticipantes() {
        return new ArrayList<>(participantes);
    }

    public List<Aluno> getListaEspera() {
        return new ArrayList<>(listaEspera);
    }

    public List<Relatorio> getRelatorios() {
        return new ArrayList<>(relatorios);
    }

    public List<String> getSolicitacoesPendentes() {
        return new ArrayList<>(solicitacoesPendentes);
    }

    @Override
    public String toString() {
        return "[" + id + "] " + titulo + " | " + area + " | Vagas: " + getVagasDisponiveis() + "/" + totalVagas + " | "
                + status;
    }
}
