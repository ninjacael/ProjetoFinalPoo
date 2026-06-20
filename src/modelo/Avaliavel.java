package modelo;

public interface Avaliavel {
    void avaliarRelatorio(Relatorio relatorio, String feedback, boolean aprovado);

    boolean podeAvaliar(Projeto projeto);
}
