package ui;

import excecoes.*;
import modelo.*;
import servicos.SistemaPesquisaService;
import util.DialogUtil;

/**
 * Tela inicial: Login e Cadastro com JOptionPane.
 * Aplica: Polimorfismo (direciona para o menu correto conforme o tipo de
 * usuário)
 */
public class TelaLogin {

    private final SistemaPesquisaService service;

    public TelaLogin(SistemaPesquisaService service) {
        this.service = service;
    }

    public void exibir() {
        boolean continuar = true;
        while (continuar) {
            String[] opcoes = {
                    "🔑 Login",
                    "📝 Cadastrar-se",
                    "ℹ Sobre o sistema",
                    "🚪 Sair"
            };
            int opcao = DialogUtil.escolherOpcao(
                    "═══ " + Usuario.SISTEMA_NOME + " ═══", opcoes);

            if (opcao == -1 || opcao == 3) {
                DialogUtil.mostrarMensagem(
                        "══════════════════════════════════\n"
                                + "  ENCERRANDO SESSÃO...\n"
                                + "══════════════════════════════════\n\n"
                                + "Obrigado por utilizar o Sistema de\n"
                                + "Gerenciamento de Pesquisas Universitárias!\n\n"
                                + "Universidade Federal do Ceará — Campus Crateús");
                continuar = false;
            } else {
                switch (opcao) {
                    case 0 -> realizarLogin();
                    case 1 -> realizarCadastro();
                    case 2 -> mostrarSobre();
                }
            }
        }
    }

    private void realizarLogin() {
        String email = DialogUtil.pedirTexto("E-mail:");
        if (email == null || email.isBlank())
            return;

        String senha = DialogUtil.pedirSenha("Senha:");
        if (senha == null)
            return;

        try {
            Usuario usuario = service.login(email, senha);
            DialogUtil.mostrarMensagem("✅ Login realizado com sucesso!\n\nBem-vindo(a), " + usuario.getNome() + "!");

            if (usuario instanceof Coordenador) {
                new MenuCoordenador(service, (Coordenador) usuario).exibir();
            } else if (usuario instanceof Professor) {
                new MenuProfessor(service, (Professor) usuario).exibir();
            } else if (usuario instanceof Aluno) {
                new MenuAluno(service, (Aluno) usuario).exibir();
            }

            service.logout();

        } catch (CredenciaisInvalidasException | UsuarioInativoException e) {
            DialogUtil.mostrarErro(e.getMessage());
        }
    }

    private void realizarCadastro() {
        String[] tipos = { "Aluno", "Professor" };
        int tipo = DialogUtil.escolherOpcao("Tipo de Cadastro", tipos);
        if (tipo == -1)
            return;

        String nome = DialogUtil.pedirTexto("Nome completo:");
        if (nome == null || nome.isBlank()) {
            DialogUtil.mostrarErro("Nome obrigatório.");
            return;
        }

        String email = DialogUtil.pedirTexto("E-mail:");
        if (email == null || email.isBlank()) {
            DialogUtil.mostrarErro("E-mail obrigatório.");
            return;
        }
        if (!email.contains("@")) {
            DialogUtil.mostrarErro("E-mail inválido.");
            return;
        }

        String senha = DialogUtil.pedirSenha("Senha (mínimo 6 caracteres):");
        if (senha == null || senha.length() < 6) {
            DialogUtil.mostrarErro("Senha deve ter ao menos 6 caracteres.");
            return;
        }

        try {
            if (tipo == 0) {
                String matricula = DialogUtil.pedirTexto("Matrícula:");
                if (matricula == null || matricula.isBlank()) {
                    DialogUtil.mostrarErro("Matrícula obrigatória.");
                    return;
                }
                service.cadastrarAluno(nome, email, senha, matricula);
                DialogUtil.mostrarMensagem("✅ Aluno cadastrado com sucesso!\n\nFaça login para acessar o sistema.");
            } else {
                String dept = DialogUtil.pedirTexto("Departamento:");
                if (dept == null || dept.isBlank()) {
                    DialogUtil.mostrarErro("Departamento obrigatório.");
                    return;
                }
                String titulacao = DialogUtil.pedirTexto("Titulação (ex: Doutor, Mestre):");
                if (titulacao == null || titulacao.isBlank()) {
                    DialogUtil.mostrarErro("Titulação obrigatória.");
                    return;
                }
                service.cadastrarProfessor(nome, email, senha, dept, titulacao);
                DialogUtil.mostrarMensagem("✅ Professor cadastrado com sucesso!\n\nFaça login para acessar o sistema.");
            }
        } catch (Exception e) {
            DialogUtil.mostrarErro("Erro ao cadastrar: " + e.getMessage());
        }
    }

    private void mostrarSobre() {
        String info = "╔══════════════════════════════════════════╗\n"
                + "║   SISTEMA DE GERENCIAMENTO DE PESQUISAS   ║\n"
                + "║      Universidade Federal do Ceará        ║\n"
                + "║           Campus Crateús — 2026           ║\n"
                + "╚══════════════════════════════════════════╝\n\n"
                + "Disciplina: CRT0007 - Programação Orientada a Objetos\n"
                + "Período: 2026.1\n\n"
                + "📌 FUNCIONALIDADES:\n"
                + "  • Cadastro de Alunos, Professores e Coordenadores\n"
                + "  • Gestão completa de projetos de pesquisa\n"
                + "  • Sistema de inscrições com lista de espera\n"
                + "  • Envio e avaliação de relatórios parciais\n"
                + "  • Notificações automáticas de prazo\n"
                + "  • Relatórios e estatísticas por Strategy\n"
                + "  • Recomendação de projetos personalizada\n\n"
                + "🏗 CONCEITOS DE POO APLICADOS:\n"
                + "  • Classes Abstratas (Usuario)\n"
                + "  • Interfaces (Avaliavel, Gerenciavel, EstrategiaRelatorio)\n"
                + "  • Herança Multinível (Coordenador → Professor → Usuario)\n"
                + "  • Polimorfismo (menus, avaliação, gerenciamento)\n"
                + "  • Encapsulamento (getters/setters privados)\n"
                + "  • Atributos e Métodos Estáticos (contadores, constantes)\n"
                + "  • Tratamento de Exceções Customizadas\n"
                + "  • Padrões Singleton e Strategy\n\n"
                + "Total de usuários criados: " + Usuario.getTotalUsuariosCriados() + "\n"
                + "Total de projetos criados: " + Projeto.getTotalProjetosCriados();
        DialogUtil.mostrarTextaoCentralizado("Sobre o Sistema", info);
    }
}
