package util;

import javax.swing.*;
import java.awt.*;

public class DialogUtil {

    public static final Color COR_PRIMARIA = new Color(0, 51, 102);
    public static final String TITULO_SISTEMA = "UFC - Sistema de Gerenciamento de Pesquisas";
    private static final Font FONTE_TITULO = new Font("Arial", Font.BOLD, 13);

    // Construtor privado — classe utilitária, não deve ser instanciada
    private DialogUtil() {
    }

    public static void mostrarMensagem(String mensagem) {
        JOptionPane.showMessageDialog(null, mensagem, TITULO_SISTEMA,
                JOptionPane.INFORMATION_MESSAGE);
    }

    public static void mostrarErro(String mensagem) {
        JOptionPane.showMessageDialog(null, mensagem, "Erro — " + TITULO_SISTEMA,
                JOptionPane.ERROR_MESSAGE);
    }

    public static void mostrarAviso(String mensagem) {
        JOptionPane.showMessageDialog(null, mensagem, "Aviso — " + TITULO_SISTEMA,
                JOptionPane.WARNING_MESSAGE);
    }

    public static boolean confirmar(String pergunta) {
        int resposta = JOptionPane.showConfirmDialog(null, pergunta,
                TITULO_SISTEMA, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        return resposta == JOptionPane.YES_OPTION;
    }

    public static String pedirTexto(String label) {
        return pedirTexto(label, "");
    }

    public static String pedirTexto(String label, String valorPadrao) {
        String resultado = JOptionPane.showInputDialog(null, label,
                TITULO_SISTEMA, JOptionPane.PLAIN_MESSAGE);
        if (resultado == null)
            return null; // Cancelado
        return resultado.trim();
    }

    public static String pedirSenha(String label) {
        JPasswordField campo = new JPasswordField(20);
        Object[] conteudo = { label, campo };
        int opcao = JOptionPane.showConfirmDialog(null, conteudo,
                TITULO_SISTEMA, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (opcao == JOptionPane.OK_OPTION) {
            return new String(campo.getPassword());
        }
        return null;
    }

    public static int pedirInteiro(String label, int min, int max) throws Exception {
        String valor = pedirTexto(label + " (" + min + " a " + max + ")");
        if (valor == null)
            throw new Exception("Operação cancelada.");
        try {
            int num = Integer.parseInt(valor);
            if (num < min || num > max) {
                throw new Exception("Valor fora do intervalo permitido (" + min + " a " + max + ").");
            }
            return num;
        } catch (NumberFormatException e) {
            throw new Exception("Valor inválido. Informe um número inteiro.");
        }
    }

    public static int escolherOpcao(String titulo, String[] opcoes) {
        if (opcoes == null || opcoes.length == 0)
            return -1;
        Object escolha = JOptionPane.showInputDialog(null,
                "Selecione uma opção:", titulo,
                JOptionPane.PLAIN_MESSAGE, null, opcoes, opcoes[0]);
        if (escolha == null)
            return -1;
        for (int i = 0; i < opcoes.length; i++) {
            if (opcoes[i].equals(escolha))
                return i;
        }
        return -1;
    }

    public static <T> T escolherObjeto(String titulo, String mensagem, T[] opcoes) {
        if (opcoes == null || opcoes.length == 0) {
            mostrarAviso("Nenhuma opção disponível.");
            return null;
        }
        @SuppressWarnings("unchecked")
        T escolha = (T) JOptionPane.showInputDialog(null, mensagem, titulo,
                JOptionPane.PLAIN_MESSAGE, null, opcoes, opcoes[0]);
        return escolha;
    }

    public static void mostrarTextaoCentralizado(String titulo, String conteudo) {
        JTextArea area = new JTextArea(conteudo);
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 12));
        area.setBackground(new Color(245, 245, 245));
        JScrollPane scroll = new JScrollPane(area);
        scroll.setPreferredSize(new Dimension(600, 400));
        JOptionPane.showMessageDialog(null, scroll, titulo, JOptionPane.PLAIN_MESSAGE);
    }
}
