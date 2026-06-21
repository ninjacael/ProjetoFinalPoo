package ui;

import modelo.*;
import servicos.SistemaPesquisaService;
import util.DialogUtil;
import padroes.GerenciadorNotificacoes;

import java.util.List;

public class MenuProfessor {

    private final SistemaPesquisaService service;
    private final Professor professor;

    public MenuProfessor(SistemaPesquisaService service, Professor professor) {
        this.service = service;
        this.professor = professor;
    }

    public void exibir() {
        boolean continuar = true;
        while (continuar) {
            String[] opcoes = {
                    "➕ Criar novo projeto",
                    "✏ Editar projeto existente",
                    "🗑 Remover projeto",
                    "👥 Visualizar inscritos",
                    "📋 Avaliar relatório de aluno",
                    "📢 Enviar notificação",
                    "🔔 Ver notificações",
                    "👤 Editar perfil",
                    "🚪 Sair"
            };
            int opcao = DialogUtil.escolherOpcao("Menu do Professor — " + professor.getNome(), opcoes);
            if (opcao == -1 || opcao == 8) {
                continuar = false;
            } else {
                switch (opcao) {
                    case 0 -> criarProjeto();
                    case 1 -> editarProjeto();
                    case 2 -> removerProjeto();
                    case 3 -> visualizarInscritos();
                    case 4 -> avaliarRelatorio();
                    case 5 -> enviarNotificacao();
                    case 6 -> verNotificacoes();
                    case 7 -> editarPerfil();
                }
            }
        }
    }

    private void criarProjeto() {
        try {
            String titulo = DialogUtil.pedirTexto("Título do projeto:");
            if (titulo == null || titulo.isBlank()) {
                DialogUtil.mostrarErro("Título obrigatório.");
                return;
            }

            String area = DialogUtil.pedirTexto("Área de estudo:");
            if (area == null || area.isBlank()) {
                DialogUtil.mostrarErro("Área obrigatória.");
                return;
            }

            String descricao = DialogUtil.pedirTexto("Descrição do projeto:");
            if (descricao == null)
                return;

            String dataInicio = DialogUtil.pedirTexto("Data de início (AAAA-MM-DD):");
            if (dataInicio == null || dataInicio.isBlank()) {
                DialogUtil.mostrarErro("Data de início obrigatória.");
                return;
            }

            String prazo = DialogUtil.pedirTexto("Prazo final (AAAA-MM-DD):");
            if (prazo == null || prazo.isBlank()) {
                DialogUtil.mostrarErro("Prazo obrigatório.");
                return;
            }

            int vagas = DialogUtil.pedirInteiro("Número de vagas", Projeto.VAGAS_MINIMAS, Projeto.VAGAS_MAXIMAS);

            Projeto novo = service.criarProjeto(titulo, area, descricao, professor,
                    dataInicio, prazo, vagas);
            DialogUtil.mostrarMensagem("✅ Projeto criado com sucesso!\n\n" + novo.exibirResumo());

        } catch (Exception e) {
            DialogUtil.mostrarErro("Erro ao criar projeto: " + e.getMessage());
        }
    }

    private void editarProjeto() {
        List<Projeto> meusProjetos = professor.getProjetosCriados();
        if (meusProjetos.isEmpty()) {
            DialogUtil.mostrarAviso("Você não possui projetos para editar.");
            return;
        }
        Projeto[] array = meusProjetos.toArray(new Projeto[0]);
        Projeto escolhido = DialogUtil.escolherObjeto("Editar Projeto", "Selecione o projeto:", array);
        if (escolhido == null)
            return;

        try {
            String titulo = DialogUtil.pedirTexto("Novo título (deixe em branco para manter):");
            String area = DialogUtil.pedirTexto("Nova área (deixe em branco para manter):");
            String descricao = DialogUtil.pedirTexto("Nova descrição (deixe em branco para manter):");
            String prazo = DialogUtil.pedirTexto("Novo prazo AAAA-MM-DD (deixe em branco para manter):");

            String vagasStr = DialogUtil.pedirTexto("Novas vagas (deixe em branco para manter):");
            int vagas = (vagasStr != null && !vagasStr.isBlank()) ? Integer.parseInt(vagasStr)
                    : escolhido.getTotalVagas();

            service.editarProjeto(escolhido,
                    (titulo != null && !titulo.isBlank()) ? titulo : null,
                    (area != null && !area.isBlank()) ? area : null,
                    (descricao != null && !descricao.isBlank()) ? descricao : null,
                    vagas,
                    (prazo != null && !prazo.isBlank()) ? prazo : null);

            DialogUtil.mostrarMensagem("✅ Projeto atualizado com sucesso!\n\n" + escolhido.exibirResumo());
        } catch (Exception e) {
            DialogUtil.mostrarErro("Erro ao editar projeto: " + e.getMessage());
        }
    }

    private void removerProjeto() {
        List<Projeto> meusProjetos = professor.getProjetosCriados();
        if (meusProjetos.isEmpty()) {
            DialogUtil.mostrarAviso("Você não possui projetos para remover.");
            return;
        }
        Projeto[] array = meusProjetos.toArray(new Projeto[0]);
        Projeto escolhido = DialogUtil.escolherObjeto("Remover Projeto", "Selecione o projeto:", array);
        if (escolhido == null)
            return;

        if (DialogUtil.confirmar("⚠ Confirmar encerramento do projeto:\n" + escolhido.getTitulo() + "?")) {
            try {
                service.removerProjeto(escolhido, professor);
                DialogUtil.mostrarMensagem("Projeto encerrado com sucesso.");
            } catch (Exception e) {
                DialogUtil.mostrarErro(e.getMessage());
            }
        }
    }

    private void visualizarInscritos() {
        List<Projeto> meusProjetos = professor.getProjetosCriados();
        if (meusProjetos.isEmpty()) {
            DialogUtil.mostrarAviso("Você não possui projetos.");
            return;
        }
        Projeto[] array = meusProjetos.toArray(new Projeto[0]);
        Projeto escolhido = DialogUtil.escolherObjeto("Visualizar Inscritos", "Selecione o projeto:", array);
        if (escolhido == null)
            return;

        StringBuilder sb = new StringBuilder();
        sb.append("Projeto: ").append(escolhido.getTitulo()).append("\n");
        sb.append("Status: ").append(escolhido.getStatus()).append("\n");
        sb.append("Vagas: ").append(escolhido.getParticipantes().size())
                .append("/").append(escolhido.getTotalVagas()).append("\n\n");

        sb.append("=== PARTICIPANTES ===\n");
        List<Aluno> participantes = escolhido.getParticipantes();
        if (participantes.isEmpty()) {
            sb.append("Nenhum participante ainda.\n");
        } else {
            for (int i = 0; i < participantes.size(); i++) {
                Aluno a = participantes.get(i);
                sb.append(i + 1).append(". ").append(a.getNome())
                        .append(" | Matrícula: ").append(a.getMatricula())
                        .append(" | Email: ").append(a.getEmail()).append("\n");
            }
        }

        sb.append("\n=== LISTA DE ESPERA ===\n");
        List<Aluno> espera = escolhido.getListaEspera();
        if (espera.isEmpty()) {
            sb.append("Nenhum aluno na lista de espera.\n");
        } else {
            for (Aluno a : espera) {
                sb.append("⏳ ").append(a.getNome()).append("\n");
            }
        }

        sb.append("\n=== RELATÓRIOS ENVIADOS ===\n");
        List<Relatorio> relatorios = escolhido.getRelatorios();
        if (relatorios.isEmpty()) {
            sb.append("Nenhum relatório enviado.\n");
        } else {
            for (Relatorio r : relatorios) {
                sb.append("📄 ").append(r).append("\n");
            }
        }

        DialogUtil.mostrarTextaoCentralizado("Inscritos — " + escolhido.getTitulo(), sb.toString());
    }

    private void avaliarRelatorio() {
        List<Projeto> meusProjetos = professor.getProjetosCriados();
        if (meusProjetos.isEmpty()) {
            DialogUtil.mostrarAviso("Você não possui projetos.");
            return;
        }
        Projeto[] arrayProj = meusProjetos.toArray(new Projeto[0]);
        Projeto projeto = DialogUtil.escolherObjeto("Avaliar Relatório", "Selecione o projeto:", arrayProj);
        if (projeto == null)
            return;

        List<Relatorio> relatorios = projeto.getRelatorios();
        if (relatorios.isEmpty()) {
            DialogUtil.mostrarAviso("Nenhum relatório enviado neste projeto.");
            return;
        }
        Relatorio[] arrayRel = relatorios.toArray(new Relatorio[0]);
        Relatorio rel = DialogUtil.escolherObjeto("Avaliar Relatório", "Selecione o relatório:", arrayRel);
        if (rel == null)
            return;

        DialogUtil.mostrarTextaoCentralizado("Relatório — " + rel.getTitulo(), rel.exibirDetalhes());

        String feedback = DialogUtil.pedirTexto("Feedback para o aluno:");
        if (feedback == null)
            return;

        boolean aprovado = DialogUtil.confirmar("Aprovar este relatório?");
        professor.avaliarRelatorio(rel, feedback, aprovado);
        DialogUtil.mostrarMensagem("Relatório avaliado com sucesso!\nStatus: " + rel.getStatus());
    }

    private void enviarNotificacao() {
        List<Projeto> meusProjetos = professor.getProjetosCriados();
        if (meusProjetos.isEmpty()) {
            DialogUtil.mostrarAviso("Você não possui projetos.");
            return;
        }
        Projeto[] array = meusProjetos.toArray(new Projeto[0]);
        Projeto projeto = DialogUtil.escolherObjeto("Enviar Notificação", "Selecione o projeto:", array);
        if (projeto == null)
            return;

        String mensagem = DialogUtil.pedirTexto("Mensagem da notificação:");
        if (mensagem == null || mensagem.isBlank())
            return;

        for (Aluno a : projeto.getParticipantes()) {
            GerenciadorNotificacoes.getInstancia().enviarNotificacao(a,
                    "[Prof. " + professor.getNome() + " - " + projeto.getTitulo() + "] " + mensagem);
        }
        DialogUtil.mostrarMensagem("Notificação enviada para "
                + projeto.getParticipantes().size() + " aluno(s).");
    }

    private void verNotificacoes() {
        List<String> notificacoes = professor.getNotificacoes();
        if (notificacoes.isEmpty()) {
            DialogUtil.mostrarMensagem("Você não tem notificações.");
            return;
        }
        StringBuilder sb = new StringBuilder("=== SUAS NOTIFICAÇÕES ===\n\n");
        for (int i = notificacoes.size() - 1; i >= 0; i--) {
            sb.append("• ").append(notificacoes.get(i)).append("\n\n");
        }
        DialogUtil.mostrarTextaoCentralizado("Notificações", sb.toString());
        if (DialogUtil.confirmar("Limpar notificações?")) {
            professor.limparNotificacoes();
        }
    }

    private void editarPerfil() {
        String[] campos = { "Nome", "Departamento", "Titulação", "Senha" };
        int opcao = DialogUtil.escolherOpcao("Editar Perfil", campos);
        if (opcao == -1)
            return;
        switch (opcao) {
            case 0 -> {
                String v = DialogUtil.pedirTexto("Novo nome:");
                if (v != null && !v.isBlank()) {
                    professor.setNome(v);
                    DialogUtil.mostrarMensagem("Nome atualizado.");
                }
            }
            case 1 -> {
                String v = DialogUtil.pedirTexto("Novo departamento:");
                if (v != null && !v.isBlank()) {
                    professor.setDepartamento(v);
                    DialogUtil.mostrarMensagem("Departamento atualizado.");
                }
            }
            case 2 -> {
                String v = DialogUtil.pedirTexto("Nova titulação:");
                if (v != null && !v.isBlank()) {
                    professor.setTitulacao(v);
                    DialogUtil.mostrarMensagem("Titulação atualizada.");
                }
            }
            case 3 -> {
                String v = DialogUtil.pedirSenha("Nova senha:");
                if (v != null && !v.isBlank()) {
                    professor.setSenha(v);
                    DialogUtil.mostrarMensagem("Senha atualizada.");
                }
            }
        }
    }
}
