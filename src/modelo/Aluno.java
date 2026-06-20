package modelo;

import java.util.ArrayList;
import java.util.List;

public class Aluno extends Usuario {

    private String matricula;
    private List<String> areasInteresse;
    private List<Projeto> historicoProjetosConcluidos;
    private List<Projeto> projetosAtivos;

    public Aluno(String nome, String email, String senha, String matricula) {
        super(nome, email, senha);
        this.matricula = matricula;
        this.areasInteresse = new ArrayList<>();
        this.historicoProjetosConcluidos = new ArrayList<>();
        this.projetosAtivos = new ArrayList<>();
    }

    @Override
    public String getTipoUsuario() {
        return "ALUNO";
    }

    @Override
    public String[] getOpcoesMenu() {
        return new String[] {
                "1 - Visualizar projetos disponíveis",
                "2 - Inscrever-se em projeto",
                "3 - Cancelar inscrição",
                "4 - Enviar relatório parcial",
                "5 - Ver histórico de projetos",
                "6 - Ver notificações",
                "7 - Editar perfil",
                "8 - Sair"
        };
    }

    @Override
    public String exibirPerfil() {
        return super.exibirPerfil()
                + "\nMatrícula: " + matricula
                + "\nÁreas de Interesse: " + (areasInteresse.isEmpty() ? "Nenhuma" : String.join(", ", areasInteresse))
                + "\nProjetos Ativos: " + projetosAtivos.size()
                + "\nProjetos Concluídos: " + historicoProjetosConcluidos.size();
    }

    public void adicionarAreaInteresse(String area) {
        if (!areasInteresse.contains(area)) {
            areasInteresse.add(area);
        }
    }

    public void adicionarProjetoAtivo(Projeto projeto) {
        projetosAtivos.add(projeto);
    }

    public void removerProjetoAtivo(Projeto projeto) {
        projetosAtivos.remove(projeto);
    }

    public void concluirProjeto(Projeto projeto) {
        if (projetosAtivos.remove(projeto)) {
            historicoProjetosConcluidos.add(projeto);
        }
    }

    public boolean estaInscritoEm(Projeto projeto) {
        return projetosAtivos.contains(projeto);
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public List<String> getAreasInteresse() {
        return new ArrayList<>(areasInteresse);
    }

    public List<Projeto> getHistoricoProjetosConcluidos() {
        return new ArrayList<>(historicoProjetosConcluidos);
    }

    public List<Projeto> getProjetosAtivos() {
        return new ArrayList<>(projetosAtivos);
    }
}
