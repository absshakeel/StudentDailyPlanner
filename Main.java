import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        try {
            // Set system look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            
            // Start with the login screen
            SwingUtilities.invokeLater(() -> {
                Login loginFrame = new Login();
                loginFrame.setVisible(true);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}