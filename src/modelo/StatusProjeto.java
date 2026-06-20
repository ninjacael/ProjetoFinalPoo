package modelo;

public enum StatusProjeto {
    ABERTO("Aberto"),
    EM_ANDAMENTO("Em Andamento"),
    ENCERRADO("Encerrado"),
    SUSPENSO("Suspenso");

    private final String descricao;

    StatusProjeto(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
