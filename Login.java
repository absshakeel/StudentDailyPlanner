import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class Login extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton signupButton;

    public Login() {
        setTitle("Student Daily Planner - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 400);
        setLocationRelativeTo(null);
        
        // Create main panel with padding and gradient background
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                int w = getWidth(), h = getHeight();
                GradientPaint gp = new GradientPaint(0, 0, new Color(100, 181, 246), 0, h, new Color(30, 136, 229));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
            }
        };
        mainPanel.setLayout(new BorderLayout(20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        // Create form panel with custom layout
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Username field with custom styling
        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(new Font("Arial", Font.BOLD, 14));
        usernameField = new JTextField();
        usernameField.setPreferredSize(new Dimension(200, 30));
        usernameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 180, 180)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)));

        // Password field with custom styling
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Arial", Font.BOLD, 14));
        passwordField = new JPasswordField();
        passwordField.setPreferredSize(new Dimension(200, 30));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 180, 180)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)));

        // Add components with proper spacing
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(userLabel, gbc);
        gbc.gridy = 1;
        formPanel.add(usernameField, gbc);
        gbc.gridy = 2; gbc.insets.top = 15;
        formPanel.add(passLabel, gbc);
        gbc.gridy = 3; gbc.insets.top = 5;
        formPanel.add(passwordField, gbc);

        // Welcome header with custom styling
        JLabel welcomeLabel = new JLabel("Welcome to Student Daily Planner", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // Button panel with modern styling
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setOpaque(false);
        
        loginButton = new JButton("Login");
        signupButton = new JButton("Sign Up");
        
        // Custom button styling with improved visual design
        for (JButton button : new JButton[]{loginButton, signupButton}) {
            button.setPreferredSize(new Dimension(120, 40));
            button.setFont(new Font("Arial", Font.BOLD, 14));
            button.setFocusPainted(false);
            button.setBorderPainted(true);
            button.setBackground(new Color(41, 128, 185));
            button.setForeground(Color.WHITE);
            button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            button.setOpaque(true);
            
            // Add rounded corners and shadow effect
            button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(2, 2, 4, 4),
                BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(25, 118, 210), 1),
                    BorderFactory.createEmptyBorder(8, 15, 8, 15)
                )
            ));
            
            // Enhanced hover effect with gradient
            button.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    button.setBackground(new Color(52, 152, 219));
                    button.setForeground(Color.WHITE);
                    button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createEmptyBorder(2, 2, 4, 4),
                        BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(new Color(255, 255, 255, 100), 1),
                            BorderFactory.createEmptyBorder(8, 15, 8, 15)
                        )
                    ));
                }
                public void mouseExited(MouseEvent e) {
                    button.setBackground(new Color(41, 128, 185));
                    button.setForeground(Color.WHITE);
                    button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createEmptyBorder(2, 2, 4, 4),
                        BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(new Color(25, 118, 210), 1),
                            BorderFactory.createEmptyBorder(8, 15, 8, 15)
                        )
                    ));
                }
            });
        }
        
        buttonPanel.add(loginButton);
        buttonPanel.add(signupButton);

        // Add components to main panel
        mainPanel.add(welcomeLabel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Add main panel to frame
        add(mainPanel);

        // Add action listeners
        loginButton.addActionListener(e -> handleLogin());
        signupButton.addActionListener(e -> handleSignup());
    }

    private void handleLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both username and password");
            return;
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                "SELECT id, name FROM users WHERE username = ? AND password = ?")) {
            
            stmt.setString(1, username);
            stmt.setString(2, password); // In real application, use password hashing

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int userId = rs.getInt("id");
                String name = rs.getString("name");
                
                // Open dashboard
                SwingUtilities.invokeLater(() -> {
                    Dashboard dashboard = new Dashboard(userId, name);
                    dashboard.setVisible(true);
                    this.dispose();
                });
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }

    private void handleSignup() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both username and password");
            return;
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(
                "SELECT id FROM users WHERE username = ?")) {
            
            checkStmt.setString(1, username);
            if (checkStmt.executeQuery().next()) {
                JOptionPane.showMessageDialog(this, "Username already exists");
                return;
            }

            String name = JOptionPane.showInputDialog(this, "Enter your full name:");
            if (name == null || name.trim().isEmpty()) {
                return;
            }

            try (PreparedStatement insertStmt = conn.prepareStatement(
                    "INSERT INTO users (name, username, password) VALUES (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS)) {
                
                insertStmt.setString(1, name);
                insertStmt.setString(2, username);
                insertStmt.setString(3, password); // In real application, use password hashing

                insertStmt.executeUpdate();
                
                ResultSet rs = insertStmt.getGeneratedKeys();
                if (rs.next()) {
                    int userId = rs.getInt(1);
                    JOptionPane.showMessageDialog(this, "Account created successfully!");
                    
                    // Open dashboard
                    SwingUtilities.invokeLater(() -> {
                        Dashboard dashboard = new Dashboard(userId, name);
                        dashboard.setVisible(true);
                        this.dispose();
                    });
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }
}