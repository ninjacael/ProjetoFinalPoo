package ui;

import excecoes.*;
import modelo.*;
import padroes.GerenciadorNotificacoes;
import servicos.SistemaPesquisaService;
import util.DialogUtil;

import java.util.List;

public class MenuAluno {

    private final SistemaPesquisaService service;
    private final Aluno aluno;

    public MenuAluno(SistemaPesquisaService service, Aluno aluno) {
        this.service = service;
        this.aluno = aluno;
    }

    public void exibir() {
        boolean continuar = true;
        while (continuar) {
            String[] opcoes = {
                    "🔍 Visualizar projetos disponíveis",
                    "✅ Inscrever-se em projeto",
                    "❌ Cancelar inscrição",
                    "📄 Enviar relatório parcial",
                    "📚 Ver histórico de projetos",
                    "🔔 Ver notificações",
                    "💡 Projetos recomendados para mim",
                    "👤 Editar perfil",
                    "🚪 Sair"
            };
            int opcao = DialogUtil.escolherOpcao("Menu do Aluno — " + aluno.getNome(), opcoes);
            if (opcao == -1 || opcao == 8) {
                continuar = false;
            } else {
                switch (opcao) {
                    case 0 -> visualizarProjetos();
                    case 1 -> inscreverEmProjeto();
                    case 2 -> cancelarInscricao();
                    case 3 -> enviarRelatorio();
                    case 4 -> verHistorico();
                    case 5 -> verNotificacoes();
                    case 6 -> verRecomendacoes();
                    case 7 -> editarPerfil();
                }
            }
        }
    }

    private void visualizarProjetos() {
        List<Projeto> disponiveis = service.getProjetosDisponiveis();
        if (disponiveis.isEmpty()) {
            DialogUtil.mostrarAviso("Não há projetos disponíveis no momento.");
            return;
        }
        Projeto[] array = disponiveis.toArray(new Projeto[0]);
        Projeto escolhido = DialogUtil.escolherObjeto(
                "Projetos Disponíveis", "Selecione um projeto para ver detalhes:", array);
        if (escolhido != null) {
            DialogUtil.mostrarTextaoCentralizado(
                    "Detalhes do Projeto", escolhido.exibirDetalhes());
        }
    }

    private void inscreverEmProjeto() {
        List<Projeto> disponiveis = service.getProjetosDisponiveis();
        if (disponiveis.isEmpty()) {
            DialogUtil.mostrarAviso("Não há projetos disponíveis para inscrição.");
            return;
        }
        Projeto[] array = disponiveis.toArray(new Projeto[0]);
        Projeto escolhido = DialogUtil.escolherObjeto(
                "Inscrever-se em Projeto", "Selecione o projeto:", array);
        if (escolhido == null)
            return;

        try {
            service.inscreverAluno(aluno, escolhido);
            DialogUtil.mostrarMensagem("✅ Inscrição realizada com sucesso no projeto:\n" + escolhido.getTitulo());
        } catch (InscricaoDuplicadaException e) {
            DialogUtil.mostrarErro(e.getMessage());
        } catch (SemVagasException e) {
            DialogUtil.mostrarAviso(e.getMessage() + "\n\nVocê foi adicionado à lista de espera.");
        } catch (ProjetoEncerradoException e) {
            DialogUtil.mostrarErro(e.getMessage());
        }
    }

    private void cancelarInscricao() {
        List<Projeto> ativos = aluno.getProjetosAtivos();
        if (ativos.isEmpty()) {
            DialogUtil.mostrarAviso("Você não está inscrito em nenhum projeto.");
            return;
        }
        Projeto[] array = ativos.toArray(new Projeto[0]);
        Projeto escolhido = DialogUtil.escolherObjeto(
                "Cancelar Inscrição", "Selecione o projeto para cancelar:", array);
        if (escolhido == null)
            return;

        if (DialogUtil.confirmar("Confirmar cancelamento da inscrição em:\n" + escolhido.getTitulo() + "?")) {
            try {
                service.cancelarInscricao(aluno, escolhido);
                DialogUtil.mostrarMensagem("Inscrição cancelada com sucesso.");
            } catch (Exception e) {
                DialogUtil.mostrarErro(e.getMessage());
            }
        }
    }

    private void enviarRelatorio() {
        List<Projeto> ativos = aluno.getProjetosAtivos();
        if (ativos.isEmpty()) {
            DialogUtil.mostrarAviso("Você não está inscrito em nenhum projeto ativo.");
            return;
        }
        Projeto[] array = ativos.toArray(new Projeto[0]);
        Projeto escolhido = DialogUtil.escolherObjeto(
                "Enviar Relatório", "Selecione o projeto:", array);
        if (escolhido == null)
            return;

        String titulo = DialogUtil.pedirTexto("Título do relatório:");
        if (titulo == null || titulo.isBlank()) {
            DialogUtil.mostrarErro("Título é obrigatório.");
            return;
        }
        String conteudo = DialogUtil.pedirTexto("Conteúdo do relatório (resumo):");
        if (conteudo == null || conteudo.isBlank()) {
            DialogUtil.mostrarErro("Conteúdo é obrigatório.");
            return;
        }

        try {
            service.enviarRelatorio(aluno, escolhido, titulo, conteudo);
            DialogUtil.mostrarMensagem("📄 Relatório enviado com sucesso!\nO orientador será notificado.");
        } catch (RelatorioPrazoException e) {
            DialogUtil.mostrarErro(e.getMessage());
        } catch (Exception e) {
            DialogUtil.mostrarErro(e.getMessage());
        }
    }

    private void verHistorico() {
        List<Projeto> historico = aluno.getHistoricoProjetosConcluidos();
        StringBuilder sb = new StringBuilder();
        sb.append("=== HISTÓRICO DE PROJETOS CONCLUÍDOS ===\n\n");
        if (historico.isEmpty()) {
            sb.append("Nenhum projeto concluído ainda.");
        } else {
            for (Projeto p : historico) {
                sb.append("✔ ").append(p.getTitulo()).append(" | ").append(p.getArea()).append("\n");
                sb.append("  Orientador: ").append(p.getOrientador().getNome()).append("\n\n");
            }
        }
        sb.append("\n--- PROJETOS ATIVOS ---\n");
        List<Projeto> ativos = aluno.getProjetosAtivos();
        if (ativos.isEmpty()) {
            sb.append("Nenhum projeto ativo.");
        } else {
            for (Projeto p : ativos) {
                sb.append("🔬 ").append(p.getTitulo()).append(" | Status: ").append(p.getStatus()).append("\n");
            }
        }
        DialogUtil.mostrarTextaoCentralizado("Histórico — " + aluno.getNome(), sb.toString());
    }

    private void verNotificacoes() {
        List<String> notificacoes = aluno.getNotificacoes();
        if (notificacoes.isEmpty()) {
            DialogUtil.mostrarMensagem("Você não tem notificações.");
            return;
        }
        StringBuilder sb = new StringBuilder("=== SUAS NOTIFICAÇÕES ===\n\n");
        for (int i = notificacoes.size() - 1; i >= 0; i--) {
            sb.append("• ").append(notificacoes.get(i)).append("\n\n");
        }
        DialogUtil.mostrarTextaoCentralizado("Notificações", sb.toString());
        if (DialogUtil.confirmar("Marcar todas as notificações como lidas?")) {
            aluno.limparNotificacoes();
            DialogUtil.mostrarMensagem("Notificações limpas.");
        }
    }

    private void verRecomendacoes() {
        List<Projeto> recomendados = service.recomendarProjetosParaAluno(aluno);
        if (recomendados.isEmpty()) {
            DialogUtil.mostrarAviso(
                    "Nenhuma recomendação disponível no momento.\nDica: adicione áreas de interesse ao seu perfil!");
            return;
        }
        StringBuilder sb = new StringBuilder("=== PROJETOS RECOMENDADOS PARA VOCÊ ===\n\n");
        sb.append("Baseado nas suas áreas de interesse e histórico:\n\n");
        for (Projeto p : recomendados) {
            sb.append("⭐ ").append(p.getTitulo()).append("\n");
            sb.append("   Área: ").append(p.getArea()).append("\n");
            sb.append("   Vagas: ").append(p.getVagasDisponiveis()).append("\n\n");
        }
        DialogUtil.mostrarTextaoCentralizado("Recomendações Personalizadas", sb.toString());
    }

    private void editarPerfil() {
        String[] campos = { "Nome", "Senha", "Adicionar área de interesse" };
        int opcao = DialogUtil.escolherOpcao("Editar Perfil", campos);
        if (opcao == -1)
            return;
        switch (opcao) {
            case 0 -> {
                String novoNome = DialogUtil.pedirTexto("Novo nome:", aluno.getNome());
                if (novoNome != null && !novoNome.isBlank()) {
                    aluno.setNome(novoNome);
                    DialogUtil.mostrarMensagem("Nome atualizado com sucesso.");
                }
            }
            case 1 -> {
                String novaSenha = DialogUtil.pedirSenha("Nova senha:");
                if (novaSenha != null && !novaSenha.isBlank()) {
                    aluno.setSenha(novaSenha);
                    DialogUtil.mostrarMensagem("Senha atualizada com sucesso.");
                }
            }
            case 2 -> {
                String area = DialogUtil.pedirTexto("Área de interesse (ex: Inteligência Artificial):");
                if (area != null && !area.isBlank()) {
                    aluno.adicionarAreaInteresse(area);
                    DialogUtil.mostrarMensagem("Área adicionada: " + area);
                }
            }
        }
    }
}
