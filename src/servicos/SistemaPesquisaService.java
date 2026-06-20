package servicos;

import excecoes.*;
import modelo.*;
import padroes.GerenciadorNotificacoes;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SistemaPesquisaService {

    private static SistemaPesquisaService instancia;

    private List<Usuario> usuarios;
    private List<Projeto> projetos;
    private Usuario usuarioLogado;

    private SistemaPesquisaService() {
        usuarios = new ArrayList<>();
        projetos = new ArrayList<>();
        usuarioLogado = null;
        carregarDadosIniciais();
    }

    public static SistemaPesquisaService getInstancia() {
        if (instancia == null) {
            instancia = new SistemaPesquisaService();
        }
        return instancia;
    }

    public Usuario login(String email, String senha)
            throws CredenciaisInvalidasException, UsuarioInativoException {
        for (Usuario u : usuarios) {
            if (u.getEmail().equalsIgnoreCase(email)) {
                if (!u.getSenha().equals(senha)) {
                    throw new CredenciaisInvalidasException();
                }
                if (!u.isAtivo()) {
                    throw new UsuarioInativoException(u.getNome());
                }
                usuarioLogado = u;
                GerenciadorNotificacoes.getInstancia().verificarPrazosVencendo(projetos);
                return u;
            }
        }
        throw new CredenciaisInvalidasException();
    }

    public void logout() {
        usuarioLogado = null;
    }

    public void cadastrarAluno(String nome, String email, String senha, String matricula)
            throws Exception {
        validarEmailUnico(email);
        Aluno aluno = new Aluno(nome, email, senha, matricula);
        usuarios.add(aluno);
        GerenciadorNotificacoes.getInstancia().enviarNotificacao(aluno,
                "Bem-vindo ao " + Usuario.SISTEMA_NOME + ", " + nome + "!");
    }

    public void cadastrarProfessor(String nome, String email, String senha,
            String departamento, String titulacao) throws Exception {
        validarEmailUnico(email);
        Professor prof = new Professor(nome, email, senha, departamento, titulacao);
        usuarios.add(prof);
        GerenciadorNotificacoes.getInstancia().enviarNotificacao(prof,
                "Bem-vindo ao " + Usuario.SISTEMA_NOME + ", Prof. " + nome + "!");
    }

    public void cadastrarCoordenador(String nome, String email, String senha,
            String departamento, String titulacao) throws Exception {
        validarEmailUnico(email);
        Coordenador coord = new Coordenador(nome, email, senha, departamento, titulacao);
        usuarios.add(coord);
    }

    private void validarEmailUnico(String email) throws Exception {
        for (Usuario u : usuarios) {
            if (u.getEmail().equalsIgnoreCase(email)) {
                throw new Exception("E-mail '" + email + "' já está cadastrado no sistema.");
            }
        }
    }

    public Projeto criarProjeto(String titulo, String area, String descricao,
            Professor orientador, String dataInicioStr,
            String prazoStr, int vagas) throws Exception {
        if (titulo.isBlank() || area.isBlank()) {
            throw new Exception("Título e área são obrigatórios.");
        }
        if (vagas < Projeto.VAGAS_MINIMAS || vagas > Projeto.VAGAS_MAXIMAS) {
            throw new Exception("Número de vagas deve ser entre " + Projeto.VAGAS_MINIMAS
                    + " e " + Projeto.VAGAS_MAXIMAS + ".");
        }
        java.time.LocalDate dataInicio = java.time.LocalDate.parse(dataInicioStr);
        java.time.LocalDate prazo = java.time.LocalDate.parse(prazoStr);
        if (prazo.isBefore(dataInicio)) {
            throw new Exception("O prazo não pode ser anterior à data de início.");
        }

        Projeto projeto = new Projeto(titulo, area, descricao, orientador, dataInicio, prazo, vagas);
        projetos.add(projeto);
        orientador.adicionarProjeto(projeto);

        String msg = "📢 Novo projeto disponível: '" + titulo + "' | Área: " + area + " | Vagas: " + vagas;
        for (Usuario u : usuarios) {
            if (u instanceof Aluno) {
                GerenciadorNotificacoes.getInstancia().enviarNotificacao(u, msg);
            }
        }
        return projeto;
    }

    public void editarProjeto(Projeto projeto, String novoTitulo, String novaArea,
            String novaDescricao, int novasVagas, String novoPrazoStr) throws Exception {
        if (novoTitulo != null && !novoTitulo.isBlank())
            projeto.setTitulo(novoTitulo);
        if (novaArea != null && !novaArea.isBlank())
            projeto.setArea(novaArea);
        if (novaDescricao != null && !novaDescricao.isBlank())
            projeto.setDescricao(novaDescricao);
        if (novasVagas >= Projeto.VAGAS_MINIMAS)
            projeto.setTotalVagas(novasVagas);
        if (novoPrazoStr != null && !novoPrazoStr.isBlank()) {
            projeto.setPrazo(java.time.LocalDate.parse(novoPrazoStr));
        }
        String msg = "ℹ Projeto '" + projeto.getTitulo() + "' foi atualizado.";
        for (Aluno a : projeto.getParticipantes()) {
            GerenciadorNotificacoes.getInstancia().enviarNotificacao(a, msg);
        }
    }

    public void removerProjeto(Projeto projeto, Usuario solicitante) throws Exception {
        if (solicitante instanceof Coordenador) {
            ((Coordenador) solicitante).removerProjeto(projeto);
            projetos.remove(projeto);
            String msg = "⚠ O projeto '" + projeto.getTitulo() + "' foi removido pelo coordenador.";
            for (Aluno a : projeto.getParticipantes()) {
                GerenciadorNotificacoes.getInstancia().enviarNotificacao(a, msg);
            }
        } else if (solicitante instanceof Professor) {
            Professor prof = (Professor) solicitante;
            if (!prof.podeAvaliar(projeto)) {
                throw new Exception("Você não é o orientador deste projeto.");
            }
            projeto.setStatus(StatusProjeto.ENCERRADO);
            prof.removerProjeto(projeto);
        } else {
            throw new Exception("Sem permissão para remover projetos.");
        }
    }

    public void inscreverAluno(Aluno aluno, Projeto projeto)
            throws InscricaoDuplicadaException, SemVagasException, ProjetoEncerradoException {
        if (aluno.estaInscritoEm(projeto)) {
            throw new InscricaoDuplicadaException(projeto.getTitulo());
        }
        if (projeto.getStatus() == StatusProjeto.ENCERRADO || projeto.getStatus() == StatusProjeto.SUSPENSO) {
            throw new ProjetoEncerradoException(projeto.getTitulo());
        }
        if (!projeto.temVagasDisponiveis()) {
            projeto.adicionarListaEspera(aluno);
            throw new SemVagasException(projeto.getTitulo());
        }
        projeto.adicionarParticipante(aluno);
        aluno.adicionarProjetoAtivo(projeto);

        if (projeto.getStatus() == StatusProjeto.ABERTO) {
            projeto.setStatus(StatusProjeto.EM_ANDAMENTO);
        }

        GerenciadorNotificacoes.getInstancia().enviarNotificacao(aluno,
                "✅ Você foi inscrito no projeto: '" + projeto.getTitulo() + "'.");
        GerenciadorNotificacoes.getInstancia().enviarNotificacao(projeto.getOrientador(),
                "👤 O aluno " + aluno.getNome() + " se inscreveu no projeto '" + projeto.getTitulo() + "'.");
    }

    public void cancelarInscricao(Aluno aluno, Projeto projeto) throws Exception {
        if (!aluno.estaInscritoEm(projeto)) {
            throw new Exception("Você não está inscrito neste projeto.");
        }
        projeto.removerParticipante(aluno);
        aluno.removerProjetoAtivo(projeto);
        GerenciadorNotificacoes.getInstancia().enviarNotificacao(aluno,
                "❌ Sua inscrição no projeto '" + projeto.getTitulo() + "' foi cancelada.");
        GerenciadorNotificacoes.getInstancia().enviarNotificacao(projeto.getOrientador(),
                "⚠ O aluno " + aluno.getNome() + " cancelou inscrição no projeto '" + projeto.getTitulo() + "'.");
    }

    public Relatorio enviarRelatorio(Aluno aluno, Projeto projeto, String titulo, String conteudo)
            throws RelatorioPrazoException, Exception {
        if (!aluno.estaInscritoEm(projeto)) {
            throw new Exception("Você não está inscrito neste projeto e não pode enviar relatórios.");
        }
        if (projeto.isPrazoVencido()) {
            throw new RelatorioPrazoException(projeto.getTitulo());
        }
        Relatorio rel = new Relatorio(titulo, conteudo, aluno, projeto);
        projeto.adicionarRelatorio(rel);
        GerenciadorNotificacoes.getInstancia().enviarNotificacao(projeto.getOrientador(),
                "📄 Novo relatório de " + aluno.getNome() + " no projeto '" + projeto.getTitulo() + "': " + titulo);
        return rel;
    }

    public List<Projeto> buscarProjetosPorArea(String area) {
        return projetos.stream()
                .filter(p -> p.getArea().equalsIgnoreCase(area))
                .collect(Collectors.toList());
    }

    public List<Projeto> buscarProjetosPorStatus(StatusProjeto status) {
        return projetos.stream()
                .filter(p -> p.getStatus() == status)
                .collect(Collectors.toList());
    }

    public List<Projeto> buscarProjetosPorOrientador(Professor prof) {
        return projetos.stream()
                .filter(p -> p.getOrientador().equals(prof))
                .collect(Collectors.toList());
    }

    public List<Projeto> buscarProjetosPorTitulo(String termo) {
        return projetos.stream()
                .filter(p -> p.getTitulo().toLowerCase().contains(termo.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<Projeto> getProjetosDisponiveis() {
        return projetos.stream()
                .filter(p -> p.getStatus() == StatusProjeto.ABERTO
                        || p.getStatus() == StatusProjeto.EM_ANDAMENTO)
                .collect(Collectors.toList());
    }

    public List<Projeto> recomendarProjetosParaAluno(Aluno aluno) {
        List<String> interesses = aluno.getAreasInteresse();
        List<Projeto> recomendados = new ArrayList<>();

        for (Projeto p : getProjetosDisponiveis()) {
            if (!aluno.estaInscritoEm(p) && p.temVagasDisponiveis()) {
                if (interesses.stream().anyMatch(i -> i.equalsIgnoreCase(p.getArea()))) {
                    recomendados.add(0, p);
                } else {
                    recomendados.add(p);
                }
            }
        }

        for (Projeto concluido : aluno.getHistoricoProjetosConcluidos()) {
            projetos.stream()
                    .filter(p -> p.getArea().equalsIgnoreCase(concluido.getArea())
                            && !aluno.estaInscritoEm(p)
                            && !recomendados.contains(p)
                            && p.temVagasDisponiveis())
                    .forEach(p -> recomendados.add(0, p));
        }

        return recomendados;
    }

    public void ativarUsuario(Coordenador coord, Usuario alvo) {
        coord.ativarUsuario(alvo);
    }

    public void desativarUsuario(Coordenador coord, Usuario alvo) {
        coord.desativarUsuario(alvo);
    }

    public void removerUsuario(Coordenador coord, Usuario alvo) throws Exception {
        if (alvo.equals(coord)) {
            throw new Exception("O coordenador não pode remover a si mesmo.");
        }
        usuarios.remove(alvo);
    }

    private void carregarDadosIniciais() {
        try {
            Coordenador coord = new Coordenador("Ana Coordenadora", "coord@ufc.br", "coord123",
                    "Ciência da Computação", "Doutora");
            usuarios.add(coord);

            Professor p1 = new Professor("Carlos Silva", "carlos@ufc.br", "prof123",
                    "Inteligência Artificial", "Doutor");
            Professor p2 = new Professor("Maria Santos", "maria@ufc.br", "prof123",
                    "Engenharia Ambiental", "Mestre");
            Professor p3 = new Professor("João Oliveira", "joao@ufc.br", "prof123",
                    "Automação", "Doutor");
            usuarios.add(p1);
            usuarios.add(p2);
            usuarios.add(p3);

            Aluno a1 = new Aluno("Pedro Alves", "pedro@aluno.ufc.br", "aluno123", "2021001");
            a1.adicionarAreaInteresse("Inteligência Artificial");
            a1.adicionarAreaInteresse("Automação");
            Aluno a2 = new Aluno("Larissa Costa", "larissa@aluno.ufc.br", "aluno123", "2021002");
            a2.adicionarAreaInteresse("Engenharia Ambiental");
            Aluno a3 = new Aluno("Rafael Lima", "rafael@aluno.ufc.br", "aluno123", "2022001");
            usuarios.add(a1);
            usuarios.add(a2);
            usuarios.add(a3);

            criarProjeto("IA Aplicada à Educação", "Inteligência Artificial",
                    "Pesquisa sobre uso de IA em ambientes educacionais para personalizar o aprendizado.",
                    p1, "2026-03-01", "2026-12-31", 3);

            criarProjeto("Sustentabilidade e Energia Solar", "Engenharia Ambiental",
                    "Estudo sobre viabilidade de energia solar em comunidades rurais do Ceará.",
                    p2, "2026-02-01", "2026-11-30", 2);

            criarProjeto("Robótica Educacional", "Automação",
                    "Desenvolvimento de kits de robótica de baixo custo para escolas públicas.",
                    p3, "2026-04-01", "2026-12-15", 5);

            // Inscrições iniciais
            inscreverAluno(a1, projetos.get(0));
            inscreverAluno(a2, projetos.get(1));

        } catch (Exception e) {
            System.err.println("Erro ao carregar dados iniciais: " + e.getMessage());
        }
    }

    public List<Usuario> getUsuarios() {
        return new ArrayList<>(usuarios);
    }

    public List<Projeto> getProjetos() {
        return new ArrayList<>(projetos);
    }

    public Usuario getUsuarioLogado() {
        return usuarioLogado;
    }

    public List<Aluno> getAlunos() {
        return usuarios.stream()
                .filter(u -> u instanceof Aluno)
                .map(u -> (Aluno) u)
                .collect(Collectors.toList());
    }

    public List<Professor> getProfessores() {
        return usuarios.stream()
                .filter(u -> u instanceof Professor)
                .map(u -> (Professor) u)
                .collect(Collectors.toList());
    }
}
