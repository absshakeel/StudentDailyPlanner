import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.awt.event.*;
import com.toedter.calendar.JDateChooser;

public class Dashboard extends JFrame {
    private int userId;
    private String userName;
    private JPanel mainPanel;
    private JPanel calendarPanel;
    
    private JDateChooser dateChooser;
    private LocalDate selectedDate;
    
    public Dashboard(int userId, String userName) {
        this.userId = userId;
        this.userName = userName;
        this.selectedDate = LocalDate.now();
        
        setTitle("Student Daily Planner - Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1024, 768);
        setLocationRelativeTo(null);
        
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
        
        // Add sidebar with navigation buttons
        JPanel sidebarPanel = createSidebarPanel();
        mainPanel.add(sidebarPanel, BorderLayout.WEST);
        
        // Add calendar view
        calendarPanel = createCalendarPanel();
        mainPanel.add(calendarPanel, BorderLayout.CENTER);
        
        // Add date filter panel
        JPanel dateFilterPanel = createDateFilterPanel();
        mainPanel.add(dateFilterPanel, BorderLayout.EAST);
        
        // Add main panel to frame
        add(mainPanel);
        
        // Load today's tasks
        loadTodaysTasks();
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout(15, 0));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        headerPanel.setOpaque(false);
        
        JLabel welcomeLabel = new JLabel("Welcome, " + userName);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        welcomeLabel.setForeground(new Color(40, 40, 40));
        
        LocalDate today = LocalDate.now();
        JLabel dateLabel = new JLabel(today.format(DateTimeFormatter.ofPattern("MMMM d, yyyy")));
        dateLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        dateLabel.setForeground(new Color(70, 70, 70));
        
        headerPanel.add(welcomeLabel, BorderLayout.WEST);
        headerPanel.add(dateLabel, BorderLayout.EAST);
        
        return headerPanel;
    }
    
    private JPanel createSidebarPanel() {
        JPanel sidebarPanel = new JPanel(new GridLayout(5, 1, 0, 15));
        sidebarPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 25));
        sidebarPanel.setOpaque(false);
        
        JButton[] buttons = {
            new JButton("Classes"),
            new JButton("Tasks"), // Renamed from Assignments
            new JButton("Exams"),
            new JButton("Logout")
        };
        
        // Apply modern styling to all buttons
        for (JButton btn : buttons) {
            btn.setPreferredSize(new Dimension(150, 45));
            btn.setFont(new Font("Arial", Font.BOLD, 14));
            btn.setFocusPainted(false);
            btn.setBorderPainted(true);
            btn.setBackground(new Color(30, 136, 229));
            btn.setForeground(new Color(255, 255, 255));
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btn.setOpaque(true);
            
            // Add rounded corners and border
            btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(2, 2, 4, 4),
                BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(25, 118, 210), 1),
                    BorderFactory.createEmptyBorder(8, 15, 8, 15)
                )
            ));
            
            // Add hover effect with gradient
            btn.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    btn.setBackground(new Color(21, 101, 192));
                    btn.setForeground(new Color(255, 255, 255));
                    btn.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createEmptyBorder(2, 2, 4, 4),
                        BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(new Color(255, 255, 255, 100), 1),
                            BorderFactory.createEmptyBorder(8, 15, 8, 15)
                        )
                    ));
                }
                public void mouseExited(MouseEvent e) {
                    btn.setBackground(new Color(30, 136, 229));
                    btn.setForeground(new Color(255, 255, 255));
                    btn.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createEmptyBorder(2, 2, 4, 4),
                        BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(new Color(25, 118, 210), 1),
                            BorderFactory.createEmptyBorder(8, 15, 8, 15)
                        )
                    ));
                }
            });
        }
        
        JButton classesBtn = buttons[0];
        JButton tasksBtn = buttons[1]; // Renamed variable for clarity
        JButton examsBtn = buttons[2];
        JButton logoutBtn = buttons[3];
        
        classesBtn.addActionListener(e -> navigateToScheduleManager("classes"));
        tasksBtn.addActionListener(e -> navigateToScheduleManager("tasks")); // Pass "tasks" instead of "assignments"
        examsBtn.addActionListener(e -> navigateToScheduleManager("exams"));
        logoutBtn.addActionListener(e -> handleLogout());
        
        sidebarPanel.add(classesBtn);
        sidebarPanel.add(tasksBtn);
        sidebarPanel.add(examsBtn);
        sidebarPanel.add(logoutBtn);
        
        return sidebarPanel;
    }
    
    private JPanel createCalendarPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);
        
        // Create custom title panel with gradient background
        JPanel titlePanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                int w = getWidth(), h = getHeight();
                GradientPaint gp = new GradientPaint(0, 0, new Color(230, 240, 255), 0, h, new Color(210, 230, 255));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
            }
        };
        titlePanel.setOpaque(true);
        titlePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(200, 210, 230)),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        
        JLabel titleLabel = new JLabel("Today's Schedule");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(new Color(30, 60, 90));
        titlePanel.add(titleLabel, BorderLayout.WEST);
        
        // Add a scroll pane for tasks with custom styling
        JPanel tasksPanel = new JPanel();
        tasksPanel.setLayout(new BoxLayout(tasksPanel, BoxLayout.Y_AXIS));
        tasksPanel.setOpaque(false);
        tasksPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JScrollPane scrollPane = new JScrollPane(tasksPanel);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 210, 230)));
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        
        // Add components to panel
        panel.add(titlePanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createDateFilterPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setPreferredSize(new Dimension(250, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        panel.setOpaque(false);
        
        // Create date chooser panel
        JPanel dateChooserPanel = new JPanel(new BorderLayout());
        dateChooserPanel.setOpaque(false);
        dateChooserPanel.setBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 210, 230)),
                "Select Date",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new Font("Arial", Font.BOLD, 14),
                new Color(30, 60, 90)
            )
        );
        
        // Initialize date chooser
        dateChooser = new JDateChooser();
        dateChooser.setDate(java.sql.Date.valueOf(selectedDate));
        dateChooser.setPreferredSize(new Dimension(200, 30));
        dateChooser.setFont(new Font("Arial", Font.PLAIN, 14));
        
        // Add change listener
        dateChooser.getDateEditor().addPropertyChangeListener("date", e -> {
            if (dateChooser.getDate() != null) {
                selectedDate = dateChooser.getDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
                loadTodaysTasks();
            }
        });
        
        dateChooserPanel.add(dateChooser, BorderLayout.CENTER);
        panel.add(dateChooserPanel, BorderLayout.NORTH);
        
        return panel;
    }
    
    public void loadTodaysTasks() {
        JPanel tasksPanel = (JPanel) ((JScrollPane) calendarPanel.getComponent(1)).getViewport().getView();
        tasksPanel.removeAll();
        
        try (Connection conn = DBConnection.getConnection()) {
            // Fetch classes for selected date
            String classQuery = "SELECT title, time FROM classes WHERE user_id = ? AND LOWER(day) = ?";
            try (PreparedStatement stmt = conn.prepareStatement(classQuery)) {
                stmt.setInt(1, userId);
                stmt.setString(2, selectedDate.getDayOfWeek().toString().toLowerCase());
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    tasksPanel.add(createTaskItem("Class: " + rs.getString("title"), rs.getString("time")));
                }
            }
            
            // Fetch tasks due on selected date
            String taskQuery = "SELECT title, subject FROM tasks WHERE user_id = ? AND due_date = ? AND status != 'COMPLETED'";
            try (PreparedStatement stmt = conn.prepareStatement(taskQuery)) {
                stmt.setInt(1, userId);
                stmt.setDate(2, java.sql.Date.valueOf(selectedDate));
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    tasksPanel.add(createTaskItem("Task: " + rs.getString("title") + " (" + rs.getString("subject") + ")", "Due Today"));
                }
            }
            
            // Fetch exams for selected date
            String examQuery = "SELECT subject, time FROM exams WHERE user_id = ? AND exam_date = ?";
            try (PreparedStatement stmt = conn.prepareStatement(examQuery)) {
                stmt.setInt(1, userId);
                stmt.setDate(2, java.sql.Date.valueOf(selectedDate));
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    tasksPanel.add(createTaskItem("Exam: " + rs.getString("subject"), "Time: " + rs.getString("time")));
                }
            }
            
            tasksPanel.revalidate();
            tasksPanel.repaint();
            
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading tasks: " + e.getMessage());
        }
    }
    
    private JPanel createTaskItem(String title, String details) {
        JPanel taskPanel = new JPanel(new BorderLayout(10, 5)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                int w = getWidth(), h = getHeight();
                GradientPaint gp = new GradientPaint(0, 0, new Color(245, 250, 255), 0, h, new Color(230, 240, 255));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
            }
        };
        taskPanel.setOpaque(true);
        taskPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(2, 2, 4, 4),
                BorderFactory.createLineBorder(new Color(180, 200, 230), 1)
            ),
            BorderFactory.createEmptyBorder(12, 18, 12, 18)
        ));
        taskPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(new Color(25, 95, 170));
        
        JLabel detailsLabel = new JLabel(details);
        detailsLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        detailsLabel.setForeground(new Color(60, 60, 60));
        
        taskPanel.add(titleLabel, BorderLayout.NORTH);
        taskPanel.add(detailsLabel, BorderLayout.CENTER);
        
        // Add margin between tasks
        Box.createVerticalStrut(10);
        
        return taskPanel;
    }
    
    private void navigateToScheduleManager(String type) {
        ScheduleManager scheduleManager = new ScheduleManager(userId, type);
        scheduleManager.setVisible(true);
        this.setVisible(false);
    }
    
    public void refreshDashboard() {
        loadTodaysTasks();
    }
    
    private void handleBackToDashboard() {
        this.setVisible(true);
    }
    
    // Modify the ScheduleManager constructor to include a back button
    // public ScheduleManager(int userId, String type) {
    //     this.userId = userId;
    //     this.type = type;
    //     setTitle("Student Daily Planner - " + capitalize(type));
    //     setSize(600, 400);
    //     setLocationRelativeTo(null);
    //     setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    //     mainPanel = new JPanel(new BorderLayout(10, 10));
    //     mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    //     JPanel headerPanel = createHeaderPanel();
    //     JButton backButton = new JButton("Back");
    //     backButton.addActionListener(e -> handleBackToDashboard());
    //     headerPanel.add(backButton, BorderLayout.WEST);
    //     mainPanel.add(headerPanel, BorderLayout.NORTH);
    //     listPanel = new JPanel();
    //     listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
    //     JScrollPane scrollPane = new JScrollPane(listPanel);
    //     scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    //     mainPanel.add(scrollPane, BorderLayout.CENTER);
    //     add(mainPanel);
    //     loadItems();
    // }
    
    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION);
                
        if (confirm == JOptionPane.YES_OPTION) {
            SwingUtilities.invokeLater(() -> {
                new Login().setVisible(true);
                this.dispose();
            });
        }
    }
    
    private void addTaskToCalendar(String title, String details) {
        JPanel tasksPanel = (JPanel) ((JScrollPane) calendarPanel.getComponent(1)).getViewport().getView();
        tasksPanel.add(createTaskItem(title, details));
        tasksPanel.revalidate();
        tasksPanel.repaint();
    }
}