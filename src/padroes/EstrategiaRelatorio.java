package padroes;

import modelo.Projeto;
import modelo.Aluno;
import modelo.Professor;
import modelo.Usuario;
import modelo.StatusProjeto;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public interface EstrategiaRelatorio {
    String gerarRelatorio(List<Projeto> projetos, List<Usuario> usuarios);

    String getNomeEstrategia();
}
