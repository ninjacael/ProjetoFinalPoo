package ui;

import modelo.*;
import padroes.*;
import servicos.SistemaPesquisaService;
import util.DialogUtil;

import java.util.List;

public class MenuCoordenador extends MenuProfessor {

    private final SistemaPesquisaService service;
    private final Coordenador coordenador;

    public MenuCoordenador(SistemaPesquisaService service, Coordenador coordenador) {
        super(service, coordenador);
        this.service = service;
        this.coordenador = coordenador;
    }

    @Override
    public void exibir() {
        boolean continuar = true;
        while (continuar) {
            String[] opcoes = {
                    "📁 Gerenciar projetos",
                    "👥 Gerenciar usuários",
                    "📊 Gerar relatórios",
                    "📈 Estatísticas gerais",
                    "📢 Enviar notificação global",
                    "🔔 Ver notificações",
                    "👤 Editar perfil",
                    "🚪 Sair"
            };
            int opcao = DialogUtil.escolherOpcao("Menu do Coordenador — " + coordenador.getNome(), opcoes);
            if (opcao == -1 || opcao == 7) {
                continuar = false;
            } else {
                switch (opcao) {
                    case 0 -> gerenciarProjetos();
                    case 1 -> gerenciarUsuarios();
                    case 2 -> gerarRelatorios();
                    case 3 -> estatisticasGerais();
                    case 4 -> enviarNotificacaoGlobal();
                    case 5 -> verNotificacoesCoordenador();
                    case 6 -> editarPerfilCoordenador();
                }
            }
        }
    }

    private void gerenciarProjetos() {
        String[] opcoes = {
                "➕ Criar projeto",
                "✏ Editar qualquer projeto",
                "🗑 Remover qualquer projeto",
                "🔍 Buscar projetos",
                "📋 Listar todos os projetos",
                "⬅ Voltar"
        };
        int opcao = DialogUtil.escolherOpcao("Gerenciar Projetos", opcoes);
        if (opcao == -1 || opcao == 5)
            return;
        switch (opcao) {
            case 0 -> criarProjetoCoord();
            case 1 -> editarProjetoCoord();
            case 2 -> removerProjetoCoord();
            case 3 -> buscarProjetos();
            case 4 -> listarTodosProjetos();
        }
    }

    private void criarProjetoCoord() {
        List<Professor> professores = service.getProfessores();
        if (professores.isEmpty()) {
            DialogUtil.mostrarAviso("Não há professores cadastrados para orientar projetos.");
            return;
        }
        Professor[] arrayProf = professores.toArray(new Professor[0]);
        Professor orientador = DialogUtil.escolherObjeto("Criar Projeto", "Selecione o orientador:", arrayProf);
        if (orientador == null)
            return;

        try {
            String titulo = DialogUtil.pedirTexto("Título do projeto:");
            if (titulo == null || titulo.isBlank())
                return;
            String area = DialogUtil.pedirTexto("Área de estudo:");
            if (area == null || area.isBlank())
                return;
            String descricao = DialogUtil.pedirTexto("Descrição:");
            if (descricao == null)
                return;
            String inicio = DialogUtil.pedirTexto("Data de início (AAAA-MM-DD):");
            if (inicio == null || inicio.isBlank())
                return;
            String prazo = DialogUtil.pedirTexto("Prazo (AAAA-MM-DD):");
            if (prazo == null || prazo.isBlank())
                return;
            int vagas = DialogUtil.pedirInteiro("Número de vagas", Projeto.VAGAS_MINIMAS, Projeto.VAGAS_MAXIMAS);

            Projeto novo = service.criarProjeto(titulo, area, descricao, orientador, inicio, prazo, vagas);
            DialogUtil.mostrarMensagem("✅ Projeto criado!\n\n" + novo.exibirResumo());
        } catch (Exception e) {
            DialogUtil.mostrarErro("Erro: " + e.getMessage());
        }
    }

    private void editarProjetoCoord() {
        List<Projeto> todos = service.getProjetos();
        if (todos.isEmpty()) {
            DialogUtil.mostrarAviso("Nenhum projeto cadastrado.");
            return;
        }
        Projeto[] array = todos.toArray(new Projeto[0]);
        Projeto escolhido = DialogUtil.escolherObjeto("Editar Projeto", "Selecione:", array);
        if (escolhido == null)
            return;
        try {
            String titulo = DialogUtil.pedirTexto("Novo título (em branco para manter):");
            String area = DialogUtil.pedirTexto("Nova área:");
            String desc = DialogUtil.pedirTexto("Nova descrição:");
            String prazo = DialogUtil.pedirTexto("Novo prazo AAAA-MM-DD:");
            String vagasStr = DialogUtil.pedirTexto("Novas vagas:");
            int vagas = (vagasStr != null && !vagasStr.isBlank()) ? Integer.parseInt(vagasStr)
                    : escolhido.getTotalVagas();
            service.editarProjeto(escolhido,
                    (titulo != null && !titulo.isBlank()) ? titulo : null,
                    (area != null && !area.isBlank()) ? area : null,
                    (desc != null && !desc.isBlank()) ? desc : null,
                    vagas,
                    (prazo != null && !prazo.isBlank()) ? prazo : null);
            DialogUtil.mostrarMensagem("Projeto atualizado com sucesso.");
        } catch (Exception e) {
            DialogUtil.mostrarErro(e.getMessage());
        }
    }

    private void removerProjetoCoord() {
        List<Projeto> todos = service.getProjetos();
        if (todos.isEmpty()) {
            DialogUtil.mostrarAviso("Nenhum projeto cadastrado.");
            return;
        }
        Projeto[] array = todos.toArray(new Projeto[0]);
        Projeto escolhido = DialogUtil.escolherObjeto("Remover Projeto", "Selecione:", array);
        if (escolhido == null)
            return;
        if (DialogUtil.confirmar("⚠ Confirmar remoção do projeto:\n" + escolhido.getTitulo() + "?")) {
            try {
                service.removerProjeto(escolhido, coordenador);
                DialogUtil.mostrarMensagem("Projeto removido com sucesso.");
            } catch (Exception e) {
                DialogUtil.mostrarErro(e.getMessage());
            }
        }
    }

    private void buscarProjetos() {
        String[] tipos = { "Por título", "Por área", "Por status" };
        int tipo = DialogUtil.escolherOpcao("Buscar Projetos", tipos);
        List<Projeto> resultado = null;
        switch (tipo) {
            case 0 -> {
                String termo = DialogUtil.pedirTexto("Termo de busca:");
                if (termo != null)
                    resultado = service.buscarProjetosPorTitulo(termo);
            }
            case 1 -> {
                String area = DialogUtil.pedirTexto("Área:");
                if (area != null)
                    resultado = service.buscarProjetosPorArea(area);
            }
            case 2 -> {
                String[] statuses = { "ABERTO", "EM_ANDAMENTO", "ENCERRADO", "SUSPENSO" };
                int s = DialogUtil.escolherOpcao("Status", statuses);
                if (s >= 0)
                    resultado = service.buscarProjetosPorStatus(StatusProjeto.values()[s]);
            }
        }
        if (resultado == null || resultado.isEmpty()) {
            DialogUtil.mostrarAviso("Nenhum projeto encontrado.");
            return;
        }
        StringBuilder sb = new StringBuilder("Resultados (" + resultado.size() + "):\n\n");
        for (Projeto p : resultado)
            sb.append(p).append("\n");
        DialogUtil.mostrarTextaoCentralizado("Resultados da Busca", sb.toString());
    }

    private void listarTodosProjetos() {
        List<Projeto> todos = service.getProjetos();
        if (todos.isEmpty()) {
            DialogUtil.mostrarAviso("Nenhum projeto cadastrado.");
            return;
        }
        StringBuilder sb = new StringBuilder("=== TODOS OS PROJETOS (" + todos.size() + ") ===\n\n");
        for (Projeto p : todos) {
            sb.append(p).append("\n");
            sb.append("  Participantes: ").append(p.getParticipantes().size())
                    .append(" | Relatórios: ").append(p.getRelatorios().size()).append("\n\n");
        }
        DialogUtil.mostrarTextaoCentralizado("Todos os Projetos", sb.toString());
    }

    private void gerenciarUsuarios() {
        String[] opcoes = { "👁 Listar usuários", "✅ Ativar usuário", "❌ Desativar usuário", "🗑 Remover usuário",
                "⬅ Voltar" };
        int opcao = DialogUtil.escolherOpcao("Gerenciar Usuários", opcoes);
        if (opcao == -1 || opcao == 4)
            return;
        switch (opcao) {
            case 0 -> listarUsuarios();
            case 1 -> alterarStatusUsuario(true);
            case 2 -> alterarStatusUsuario(false);
            case 3 -> removerUsuario();
        }
    }

    private void listarUsuarios() {
        List<Usuario> todos = service.getUsuarios();
        StringBuilder sb = new StringBuilder("=== USUÁRIOS CADASTRADOS (" + todos.size() + ") ===\n\n");
        for (Usuario u : todos) {
            sb.append(u).append(" | Status: ").append(u.isAtivo() ? "✅ Ativo" : "❌ Inativo").append("\n");
        }
        DialogUtil.mostrarTextaoCentralizado("Usuários", sb.toString());
    }

    private void alterarStatusUsuario(boolean ativar) {
        List<Usuario> todos = service.getUsuarios();
        Usuario[] array = todos.toArray(new Usuario[0]);
        Usuario escolhido = DialogUtil.escolherObjeto(
                ativar ? "Ativar Usuário" : "Desativar Usuário",
                "Selecione o usuário:", array);
        if (escolhido == null)
            return;
        if (escolhido.equals(coordenador)) {
            DialogUtil.mostrarErro("Você não pode alterar seu próprio status.");
            return;
        }
        if (ativar) {
            service.ativarUsuario(coordenador, escolhido);
            DialogUtil.mostrarMensagem("Usuário " + escolhido.getNome() + " ativado com sucesso.");
        } else {
            service.desativarUsuario(coordenador, escolhido);
            DialogUtil.mostrarMensagem("Usuário " + escolhido.getNome() + " desativado.");
        }
    }

    private void removerUsuario() {
        List<Usuario> todos = service.getUsuarios();
        Usuario[] array = todos.toArray(new Usuario[0]);
        Usuario escolhido = DialogUtil.escolherObjeto("Remover Usuário", "Selecione:", array);
        if (escolhido == null)
            return;
        if (DialogUtil.confirmar("⚠ Confirmar remoção do usuário: " + escolhido.getNome() + "?")) {
            try {
                service.removerUsuario(coordenador, escolhido);
                DialogUtil.mostrarMensagem("Usuário removido.");
            } catch (Exception e) {
                DialogUtil.mostrarErro(e.getMessage());
            }
        }
    }

    private void gerarRelatorios() {
        String[] opcoes = { "Por Área de Pesquisa", "Projetos Mais Ativos", "⬅ Voltar" };
        int opcao = DialogUtil.escolherOpcao("Gerar Relatórios", opcoes);
        EstrategiaRelatorio estrategia = null;
        switch (opcao) {
            case 0 -> estrategia = new RelatorioporArea();
            case 1 -> estrategia = new RelatorioProjetosMaisAtivos();
            default -> {
                return;
            }
        }
        String relatorio = estrategia.gerarRelatorio(service.getProjetos(), service.getUsuarios());
        DialogUtil.mostrarTextaoCentralizado(estrategia.getNomeEstrategia(), relatorio);
    }

    private void estatisticasGerais() {
        EstrategiaRelatorio estrategia = new RelatorioEstatisticasGerais();
        String relatorio = estrategia.gerarRelatorio(service.getProjetos(), service.getUsuarios());
        DialogUtil.mostrarTextaoCentralizado("Estatísticas Gerais da Plataforma", relatorio);
    }

    private void enviarNotificacaoGlobal() {
        String mensagem = DialogUtil.pedirTexto("Mensagem para todos os usuários:");
        if (mensagem == null || mensagem.isBlank())
            return;
        GerenciadorNotificacoes.getInstancia().enviarNotificacaoGlobal(service.getUsuarios(),
                "[Coordenação] " + mensagem);
        DialogUtil.mostrarMensagem("Notificação enviada para todos os " + service.getUsuarios().size() + " usuários.");
    }

    private void verNotificacoesCoordenador() {
        List<String> notificacoes = coordenador.getNotificacoes();
        if (notificacoes.isEmpty()) {
            DialogUtil.mostrarMensagem("Você não tem notificações.");
            return;
        }
        StringBuilder sb = new StringBuilder("=== NOTIFICAÇÕES DO COORDENADOR ===\n\n");
        for (int i = notificacoes.size() - 1; i >= 0; i--) {
            sb.append("• ").append(notificacoes.get(i)).append("\n\n");
        }
        DialogUtil.mostrarTextaoCentralizado("Notificações", sb.toString());
        if (DialogUtil.confirmar("Limpar notificações?"))
            coordenador.limparNotificacoes();
    }

    private void editarPerfilCoordenador() {
        String[] campos = { "Nome", "Departamento", "Senha" };
        int opcao = DialogUtil.escolherOpcao("Editar Perfil", campos);
        if (opcao == -1)
            return;
        switch (opcao) {
            case 0 -> {
                String v = DialogUtil.pedirTexto("Novo nome:");
                if (v != null && !v.isBlank()) {
                    coordenador.setNome(v);
                    DialogUtil.mostrarMensagem("Nome atualizado.");
                }
            }
            case 1 -> {
                String v = DialogUtil.pedirTexto("Novo departamento:");
                if (v != null && !v.isBlank()) {
                    coordenador.setDepartamento(v);
                    DialogUtil.mostrarMensagem("Departamento atualizado.");
                }
            }
            case 2 -> {
                String v = DialogUtil.pedirSenha("Nova senha:");
                if (v != null && !v.isBlank()) {
                    coordenador.setSenha(v);
                    DialogUtil.mostrarMensagem("Senha atualizada.");
                }
            }
        }
    }
}
