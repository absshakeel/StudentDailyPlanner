import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.EmptyBorder;
import com.toedter.calendar.JDateChooser;
import java.util.Date;
import java.awt.geom.Rectangle2D;

public class ScheduleManagerView extends JFrame {
    protected int userId;
    protected String type;
    protected JPanel mainPanel;
    protected JPanel listPanel;
    private ScheduleManagerController controller;
    
    public ScheduleManagerView(int userId, String type, ScheduleManagerController controller) {
        this.userId = userId;
        this.type = type;
        this.controller = controller;
        
        initializeUI();
    }
    
    private void initializeUI() {
        setTitle("Student Daily Planner - " + controller.capitalize(type));
        setSize(1024, 768);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        // Create main panel with gradient background
        mainPanel = new JPanel(new BorderLayout(20, 20)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                int w = getWidth(), h = getHeight();
                GradientPaint gp = new GradientPaint(0, 0, new Color(240, 245, 255), 0, h, new Color(220, 235, 255));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
            }
        };
        mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));
        
        // Add header
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Add list panel with gradient background
        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setOpaque(false);
        listPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 210, 230)));
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Add main panel to frame
        add(mainPanel);
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout(15, 0));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        headerPanel.setOpaque(false);
        
        // Create left panel for back button
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftPanel.setOpaque(false);
        
        JButton backButton = createStyledButton("Back to Dashboard", new Color(30, 136, 229));
        backButton.addActionListener(e -> controller.navigateBackToDashboard());
        
        // Create right panel for Add New button
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        rightPanel.setOpaque(false);
        
        JButton addButton = createStyledButton("Add New", new Color(34, 197, 94));
        addButton.addActionListener(e -> controller.showAddDialog());
        
        // Center panel for title
        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        centerPanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel(controller.capitalize(type));
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(40, 40, 40));
        
        leftPanel.add(backButton);
        centerPanel.add(titleLabel);
        rightPanel.add(addButton);
        
        headerPanel.add(leftPanel, BorderLayout.WEST);
        headerPanel.add(centerPanel, BorderLayout.CENTER);
        headerPanel.add(rightPanel, BorderLayout.EAST);
        
        return headerPanel;
    }

    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                // Enhanced rounded background with modern shadow and gradient
                int width = getWidth() - 8;
                int height = getHeight() - 8;
                int radius = 12;

                // Improved shadow effect with softer edges and larger spread
                for (int i = 6; i > 0; i--) {
                    g2.setColor(new Color(0, 0, 0, 6 + i * 3));
                    g2.fillRoundRect(i + 2, i + 2, width, height, radius, radius);
                }

                // Enhanced gradient background with more vibrant transition
                Color startColor = new Color(
                    Math.min(backgroundColor.getRed() + 15, 255),
                    Math.min(backgroundColor.getGreen() + 15, 255),
                    Math.min(backgroundColor.getBlue() + 15, 255)
                );
                Color endColor = new Color(
                    Math.max(backgroundColor.getRed() - 15, 0),
                    Math.max(backgroundColor.getGreen() - 15, 0),
                    Math.max(backgroundColor.getBlue() - 15, 0)
                );
                GradientPaint gradient = new GradientPaint(
                    0, 0, startColor,
                    0, height, endColor
                );
                g2.setPaint(gradient);
                g2.fillRoundRect(2, 2, width, height, radius, radius);

                // Add subtle highlight border
                g2.setColor(new Color(255, 255, 255, 50));
                g2.setStroke(new BasicStroke(2f));
                g2.drawRoundRect(2, 2, width, height, radius, radius);

                // Optimized text rendering with shadow
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                FontMetrics fm = g2.getFontMetrics();
                Rectangle2D r = fm.getStringBounds(getText(), g2);
                int x = (getWidth() - (int) r.getWidth()) / 2;
                int y = (getHeight() - (int) r.getHeight()) / 2 + fm.getAscent();
                
                // Add subtle text shadow
                g2.setColor(new Color(0, 0, 0, 30));
                g2.drawString(getText(), x + 1, y + 1);
                
                g2.setColor(getForeground());
                g2.drawString(getText(), x, y);
                
                g2.dispose();
            }
        };

        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorder(BorderFactory.createEmptyBorder(14, 32, 14, 32));
        button.setBackground(backgroundColor);

        // Enhanced hover and press effects with smoother transitions
        Color hoverColor = new Color(
            Math.min(backgroundColor.getRed() + 25, 255),
            Math.min(backgroundColor.getGreen() + 25, 255),
            Math.min(backgroundColor.getBlue() + 25, 255)
        );
        Color pressedColor = new Color(
            Math.max(backgroundColor.getRed() - 25, 0),
            Math.max(backgroundColor.getGreen() - 25, 0),
            Math.max(backgroundColor.getBlue() - 25, 0)
        );

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hoverColor);
                button.repaint();
            }

            public void mouseExited(MouseEvent e) {
                button.setBackground(backgroundColor);
                button.repaint();
            }

            public void mousePressed(MouseEvent e) {
                button.setBackground(pressedColor);
                button.repaint();
            }

            public void mouseReleased(MouseEvent e) {
                button.setBackground(hoverColor);
                button.repaint();
            }
        });

        return button;
    }

    protected JTextField createStyledTextField() {
        JTextField textField = new JTextField();
        textField.setFont(new Font("Arial", Font.PLAIN, 14));
        textField.setForeground(new Color(50, 50, 50));
        textField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        return textField;
    }
}
