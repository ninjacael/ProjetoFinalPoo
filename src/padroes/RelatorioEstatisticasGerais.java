package padroes;

import modelo.*;

import java.util.List;

public class RelatorioEstatisticasGerais implements EstrategiaRelatorio {

    @Override
    public String gerarRelatorio(List<Projeto> projetos, List<Usuario> usuarios) {
        long totalAlunos = usuarios.stream().filter(u -> u instanceof Aluno).count();
        long totalProfessores = usuarios.stream().filter(u -> u instanceof Professor && !(u instanceof Coordenador))
                .count();
        long totalCoordenadores = usuarios.stream().filter(u -> u instanceof Coordenador).count();
        long projetosAbertos = projetos.stream().filter(p -> p.getStatus() == StatusProjeto.ABERTO).count();
        long projetosAndamento = projetos.stream().filter(p -> p.getStatus() == StatusProjeto.EM_ANDAMENTO).count();
        long projetosEncerrados = projetos.stream().filter(p -> p.getStatus() == StatusProjeto.ENCERRADO).count();
        long totalParticipacoes = projetos.stream().mapToLong(p -> p.getParticipantes().size()).sum();
        long totalRelatorios = projetos.stream().mapToLong(p -> p.getRelatorios().size()).sum();

        StringBuilder sb = new StringBuilder();
        sb.append("╔══════════════════════════════════════╗\n");
        sb.append("║       ESTATÍSTICAS GERAIS DA UFC     ║\n");
        sb.append("╚══════════════════════════════════════╝\n\n");
        sb.append("USUÁRIOS\n");
        sb.append("  • Alunos: ").append(totalAlunos).append("\n");
        sb.append("  • Professores: ").append(totalProfessores).append("\n");
        sb.append("  • Coordenadores: ").append(totalCoordenadores).append("\n");
        sb.append("  • Total: ").append(usuarios.size()).append("\n\n");
        sb.append("PROJETOS\n");
        sb.append("  • Abertos: ").append(projetosAbertos).append("\n");
        sb.append("  • Em Andamento: ").append(projetosAndamento).append("\n");
        sb.append("  • Encerrados: ").append(projetosEncerrados).append("\n");
        sb.append("  • Total: ").append(projetos.size()).append("\n\n");
        sb.append("ENGAJAMENTO\n");
        sb.append("  • Total de Participações: ").append(totalParticipacoes).append("\n");
        sb.append("  • Total de Relatórios: ").append(totalRelatorios).append("\n");
        sb.append("  • Notificações Enviadas: ").append(GerenciadorNotificacoes.getTotalNotificacoesEnviadas())
                .append("\n");

        return sb.toString();
    }

    @Override
    public String getNomeEstrategia() {
        return "Estatísticas Gerais";
    }
}
