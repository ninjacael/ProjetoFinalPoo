import servicos.SistemaPesquisaService;
import ui.TelaLogin;

import javax.swing.*;

public class Main {

 * 
    public static void main(String[] args) {
        
      SwingUtilities.invokeLater(() -> {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    catch (Exception ignored) {}

            SistemaPesquisaService service = SistemaPesquisaService.getInstancia();
            TelaLogin telaLogin = new TelaLogin(service);
 * 
            telaLogin.exibir();
 * 
   ystem.exit(0);
 * 
        });
    }
}

            