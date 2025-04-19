import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import com.toedter.calendar.JDateChooser;
import javax.swing.border.EmptyBorder;
import java.awt.geom.RoundRectangle2D;
import com.toedter.calendar.*;
import java.util.Date;

public class ScheduleManager extends JFrame {
    private int userId;
    private String type;
    private JPanel mainPanel;
    private JPanel listPanel;
    
    public ScheduleManager(int userId, String type) {
        this.userId = userId;
        this.type = type;
        
        setTitle("Student Daily Planner - " + capitalize(type));
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
        
        // Load items
        loadItems();
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout(15, 0));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        headerPanel.setOpaque(false);
        
        // Create left panel for back button
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftPanel.setOpaque(false);
        
        JButton backButton = new JButton("Back to Dashboard");
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.setFocusPainted(false);
        backButton.setBorderPainted(true);
        backButton.setBackground(new Color(30, 136, 229));
        backButton.setForeground(Color.WHITE);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.setOpaque(true);
        
        // Create right panel for Add New button
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        rightPanel.setOpaque(false);
        
        JButton addButton = new JButton("Add New");
        addButton.setFont(new Font("Arial", Font.BOLD, 14));
        addButton.setFocusPainted(false);
        addButton.setBorderPainted(true);
        addButton.setBackground(new Color(40, 167, 69));
        addButton.setForeground(Color.WHITE);
        addButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addButton.setOpaque(true);
        
        // Add hover effects
        addButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                addButton.setBackground(new Color(33, 136, 56));
            }
            public void mouseExited(MouseEvent e) {
                addButton.setBackground(new Color(40, 167, 69));
            }
        });
        addButton.addActionListener(e -> showAddDialog());
        
        // Center panel for title
        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        centerPanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel(capitalize(type));
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(40, 40, 40));
        
        backButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(2, 2, 4, 4),
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(25, 118, 210), 1),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)
            )
        ));
        
        backButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                backButton.setBackground(new Color(21, 101, 192));
            }
            public void mouseExited(MouseEvent e) {
                backButton.setBackground(new Color(30, 136, 229));
            }
        });
        
        backButton.addActionListener(e -> navigateBackToDashboard());
        
        leftPanel.add(backButton);
        centerPanel.add(titleLabel);
        rightPanel.add(addButton);
        
        headerPanel.add(leftPanel, BorderLayout.WEST);
        headerPanel.add(centerPanel, BorderLayout.CENTER);
        headerPanel.add(rightPanel, BorderLayout.EAST);
        
        return headerPanel;
    }
    
    private void loadItems() {
        listPanel.removeAll();
        
        try (Connection conn = DBConnection.getConnection()) {
            String query = getQueryForType();
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, userId);
                ResultSet rs = stmt.executeQuery();
                
                boolean hasItems = false;
                while (rs.next()) {
                    JPanel itemPanel = createItemPanel(rs);
                    listPanel.add(itemPanel);
                    listPanel.add(Box.createVerticalStrut(10));
                    hasItems = true;
                }
                
                if (!hasItems) {
                    JLabel noItemsLabel = new JLabel("No " + type + " found. Click 'Add New' to create one.");
                    noItemsLabel.setFont(new Font("Arial", Font.ITALIC, 14));
                    noItemsLabel.setForeground(new Color(120, 120, 120));
                    noItemsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                    
                    JPanel messagePanel = new JPanel();
                    messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS));
                    messagePanel.setOpaque(false);
                    messagePanel.add(Box.createVerticalStrut(30));
                    messagePanel.add(noItemsLabel);
                    
                    listPanel.add(messagePanel);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading items: " + e.getMessage());
        }
        
        revalidate();
        repaint();
    }
    
    private String getQueryForType() {
        switch (type) {
            case "classes":
                return "SELECT id, title, day, DATE_FORMAT(time, '%h:%i %p') as time, location FROM classes WHERE user_id = ?";
            case "tasks": // Renamed from assignments
                return "SELECT id, title, subject, due_date, status FROM tasks WHERE user_id = ?"; // Renamed table
            case "exams":
                return "SELECT id, subject, exam_date, time FROM exams WHERE user_id = ?";
            default:
                return "";
        }
    }
    
    private JPanel createItemPanel(ResultSet rs) throws SQLException {
        JPanel panel = new JPanel(new BorderLayout(10, 5)) {
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
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 200, 230), 1),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        panel.setPreferredSize(new Dimension(Integer.MAX_VALUE, 120));
        panel.setMinimumSize(new Dimension(100, 120));
        panel.setOpaque(true);
        
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        detailsPanel.setOpaque(false);
        
        switch (type) {
            case "classes":
                JLabel titleLabel = new JLabel("Title: " + rs.getString("title"));
                titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
                titleLabel.setForeground(new Color(25, 95, 170));
                detailsPanel.add(titleLabel);

                try {
                    JLabel dayLabel = new JLabel("Date: " + rs.getString("day"));
                    dayLabel.setFont(new Font("Arial", Font.PLAIN, 14));
                    dayLabel.setForeground(new Color(60, 60, 60));
                    detailsPanel.add(dayLabel);
                } catch (Exception e) {
                    JLabel dayLabel = new JLabel("Date: Not specified");
                    dayLabel.setFont(new Font("Arial", Font.PLAIN, 14));
                    dayLabel.setForeground(new Color(60, 60, 60));
                    detailsPanel.add(dayLabel);
                }

                JLabel timeLabel = new JLabel("Time: " + rs.getString("time"));
                timeLabel.setFont(new Font("Arial", Font.PLAIN, 14));
                timeLabel.setForeground(new Color(60, 60, 60));
                detailsPanel.add(timeLabel);

                try {
                    String location = rs.getString("location");
                    if (location != null && !location.isEmpty()) {
                        String[] locationParts = location.split(" - Room ");
                        JLabel buildingLabel = new JLabel("Building: " + locationParts[0]);
                        buildingLabel.setFont(new Font("Arial", Font.PLAIN, 14));
                        buildingLabel.setForeground(new Color(60, 60, 60));
                        detailsPanel.add(buildingLabel);

                        JLabel roomLabel = new JLabel("Room: " + (locationParts.length > 1 ? locationParts[1] : ""));
                        roomLabel.setFont(new Font("Arial", Font.PLAIN, 14));
                        roomLabel.setForeground(new Color(60, 60, 60));
                        detailsPanel.add(roomLabel);
                    } else {
                        JLabel buildingLabel = new JLabel("Building: Not specified");
                        buildingLabel.setFont(new Font("Arial", Font.PLAIN, 14));
                        buildingLabel.setForeground(new Color(60, 60, 60));
                        detailsPanel.add(buildingLabel);

                        JLabel roomLabel = new JLabel("Room: Not specified");
                        roomLabel.setFont(new Font("Arial", Font.PLAIN, 14));
                        roomLabel.setForeground(new Color(60, 60, 60));
                        detailsPanel.add(roomLabel);
                    }
                } catch (Exception e) {
                    JLabel buildingLabel = new JLabel("Building: Not specified");
                    buildingLabel.setFont(new Font("Arial", Font.PLAIN, 14));
                    buildingLabel.setForeground(new Color(60, 60, 60));
                    detailsPanel.add(buildingLabel);

                    JLabel roomLabel = new JLabel("Room: Not specified");
                    roomLabel.setFont(new Font("Arial", Font.PLAIN, 14));
                    roomLabel.setForeground(new Color(60, 60, 60));
                    detailsPanel.add(roomLabel);
                }
                break;
                
            case "tasks": // Renamed from assignments
                JLabel taskTitleLabel = new JLabel("Title: " + rs.getString("title"));
                taskTitleLabel.setFont(new Font("Arial", Font.BOLD, 16));
                taskTitleLabel.setForeground(new Color(25, 95, 170));
                detailsPanel.add(taskTitleLabel);

                JLabel taskSubjectLabel = new JLabel("Subject: " + rs.getString("subject"));
                taskSubjectLabel.setFont(new Font("Arial", Font.PLAIN, 14));
                taskSubjectLabel.setForeground(new Color(60, 60, 60));
                detailsPanel.add(taskSubjectLabel);

                try {
                    Date dueDate = rs.getDate("due_date");
                    String formattedDate = dueDate != null ? new java.text.SimpleDateFormat("yyyy-MM-dd").format(dueDate) : "Not specified";
                    JLabel dueDateLabel = new JLabel("Due Date: " + formattedDate);
                    dueDateLabel.setFont(new Font("Arial", Font.PLAIN, 14));
                    dueDateLabel.setForeground(new Color(60, 60, 60));
                    detailsPanel.add(dueDateLabel);
                } catch (Exception e) {
                    JLabel dueDateLabel = new JLabel("Due Date: " + rs.getString("due_date"));
                    dueDateLabel.setFont(new Font("Arial", Font.PLAIN, 14));
                    dueDateLabel.setForeground(new Color(60, 60, 60));
                    detailsPanel.add(dueDateLabel);
                }

                JLabel statusLabel = new JLabel("Status: " + rs.getString("status"));
                statusLabel.setFont(new Font("Arial", Font.PLAIN, 14));
                statusLabel.setForeground(new Color(60, 60, 60));
                detailsPanel.add(statusLabel);
                break;
                
            case "exams":
                JLabel examSubjectLabel = new JLabel("Subject: " + rs.getString("subject"));
                examSubjectLabel.setFont(new Font("Arial", Font.BOLD, 16));
                examSubjectLabel.setForeground(new Color(25, 95, 170));
                detailsPanel.add(examSubjectLabel);

                try {
                    Date examDate = rs.getDate("exam_date");
                    String formattedDate = examDate != null ? new java.text.SimpleDateFormat("yyyy-MM-dd").format(examDate) : "Not specified";
                    JLabel examDateLabel = new JLabel("Date: " + formattedDate);
                    examDateLabel.setFont(new Font("Arial", Font.PLAIN, 14));
                    examDateLabel.setForeground(new Color(60, 60, 60));
                    detailsPanel.add(examDateLabel);
                } catch (Exception e) {
                    JLabel examDateLabel = new JLabel("Date: " + rs.getString("exam_date"));
                    examDateLabel.setFont(new Font("Arial", Font.PLAIN, 14));
                    examDateLabel.setForeground(new Color(60, 60, 60));
                    detailsPanel.add(examDateLabel);
                }

                JLabel examTimeLabel = new JLabel("Time: " + rs.getString("time"));
                examTimeLabel.setFont(new Font("Arial", Font.PLAIN, 14));
                examTimeLabel.setForeground(new Color(60, 60, 60));
                detailsPanel.add(examTimeLabel);
                break;
                

        }
        
        panel.add(detailsPanel, BorderLayout.CENTER);
        
        // Add buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        buttonsPanel.setPreferredSize(new Dimension(Integer.MAX_VALUE, 40));
        buttonsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        buttonsPanel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        buttonsPanel.setOpaque(false);
        
        // Create and style delete button
        JButton deleteButton = new JButton("Delete");
        deleteButton.setPreferredSize(new Dimension(80, 30));
        deleteButton.setFont(new Font("Arial", Font.BOLD, 12));
        deleteButton.setFocusPainted(false);
        deleteButton.setBorderPainted(true);
        deleteButton.setBackground(new Color(220, 53, 69));
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        deleteButton.setOpaque(true);
        
        // Add hover effect
        deleteButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                deleteButton.setBackground(new Color(200, 35, 51));
            }
            public void mouseExited(MouseEvent e) {
                deleteButton.setBackground(new Color(220, 53, 69));
            }
        });
        
        int itemId = rs.getInt("id");
        deleteButton.addActionListener(e -> deleteItem(itemId));
        
        buttonsPanel.add(deleteButton);
        
        if (type.equals("tasks")) { // Renamed from assignments
            String status = rs.getString("status");
            if (!status.equals("COMPLETED")) {
                JButton completeButton = createStyledButton("Complete");
                completeButton.setPreferredSize(new Dimension(90, 30));
                completeButton.setBackground(new Color(40, 167, 69));
                completeButton.setOpaque(true);
                completeButton.addActionListener(e -> markTaskComplete(itemId)); // Renamed method
                buttonsPanel.add(completeButton);
            } else {
                JLabel completedLabel = new JLabel("✓ Completed");
                completedLabel.setFont(new Font("Arial", Font.BOLD, 14));
                completedLabel.setForeground(new Color(40, 167, 69));
                buttonsPanel.add(completedLabel);
            }
        }
        
        panel.add(buttonsPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    

    private void showAddDialog() {
        JDialog dialog = new JDialog(this, "Add New " + capitalize(type), true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(250, 250, 255));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.weightx = 1.0;
        gbc.gridwidth = 1;
        
        // Create styled components
        JTextField titleField = createStyledTextField();
        JTextField subjectField = createStyledTextField();
        JDateChooser dateField = new JDateChooser();
        dateField.setDateFormatString("dd-MM-yyyy");
        JSpinner timeField = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(timeField, "hh:mm a");
        timeField.setEditor(timeEditor);
        timeField.setValue(new java.util.Date());
        JComponent editor = timeField.getEditor();
        JSpinner.DefaultEditor spinnerEditor = (JSpinner.DefaultEditor)editor;
        spinnerEditor.getTextField().setFont(new Font("Arial", Font.PLAIN, 14));
        spinnerEditor.getTextField().setForeground(new Color(50, 50, 50));
        timeField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        JPanel locationPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        JTextField buildingField = createStyledTextField();
        JTextField roomField = createStyledTextField();
        locationPanel.add(buildingField);
        locationPanel.add(roomField);
        locationPanel.setOpaque(false);
        JDateChooser dateChooser = new JDateChooser();
        JCheckBox repeatCheck = new JCheckBox();
        
        // Style the date chooser components
        dateField.setFont(new Font("Arial", Font.PLAIN, 14));
        dateField.setBackground(Color.WHITE);
        dateField.setForeground(new Color(50, 50, 50));
        
        dateChooser.setFont(new Font("Arial", Font.PLAIN, 14));
        dateChooser.setBackground(Color.WHITE);
        dateChooser.setForeground(new Color(50, 50, 50));
        
        timeField.setFont(new Font("Arial", Font.PLAIN, 14));
        timeField.setBackground(Color.WHITE);
        timeField.setForeground(new Color(50, 50, 50));
        
        switch (type) {
            case "classes":
                addFormField(panel, "Title:", titleField, gbc, 0);
                addFormField(panel, "Date:", dateField, gbc, 1);
                addFormField(panel, "Time:", timeField, gbc, 2);
                gbc.gridx = 0;
                gbc.gridy = 3;
                JLabel locationLabel = new JLabel("Location:");
                locationLabel.setFont(new Font("Arial", Font.BOLD, 14));
                locationLabel.setForeground(new Color(70, 70, 70));
                panel.add(locationLabel, gbc);
                
                gbc.gridx = 1;
                JPanel locationWrapper = new JPanel(new GridLayout(2, 1, 0, 5));
                locationWrapper.setOpaque(false);
                
                JPanel buildingWrapper = new JPanel(new BorderLayout());
                buildingWrapper.setOpaque(false);
                JLabel buildingLabel = new JLabel("Building:");
                buildingLabel.setFont(new Font("Arial", Font.PLAIN, 12));
                buildingWrapper.add(buildingLabel, BorderLayout.NORTH);
                buildingWrapper.add(buildingField, BorderLayout.CENTER);
                
                JPanel roomWrapper = new JPanel(new BorderLayout());
                roomWrapper.setOpaque(false);
                JLabel roomLabel = new JLabel("Room:");
                roomLabel.setFont(new Font("Arial", Font.PLAIN, 12));
                roomWrapper.add(roomLabel, BorderLayout.NORTH);
                roomWrapper.add(roomField, BorderLayout.CENTER);
                
                locationWrapper.add(buildingWrapper);
                locationWrapper.add(roomWrapper);
                panel.add(locationWrapper, gbc);
                break;
                
            case "tasks": // Renamed from assignments
                addFormField(panel, "Title:", titleField, gbc, 0);
                addFormField(panel, "Subject:", subjectField, gbc, 1);
                addFormField(panel, "Due Date:", dateChooser, gbc, 2);
                break;
                
            case "exams":
                addFormField(panel, "Subject:", subjectField, gbc, 0);
                addFormField(panel, "Date:", dateChooser, gbc, 1);
                addFormField(panel, "Time:", timeField, gbc, 2);
                break;
                
            // Reminders case removed for next version
        }
        
        // Add buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setOpaque(false);
        
        JButton saveButton = createStyledButton("Save");
        saveButton.addActionListener(e -> {
            try {
                Connection conn = DBConnection.getConnection();
                String query = getInsertQueryForType();
                
                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    switch (type) {
                        case "classes":
                            stmt.setInt(1, userId);
                            stmt.setString(2, titleField.getText());
                            if (dateField.getDate() != null) {
                                stmt.setDate(3, new java.sql.Date(dateField.getDate().getTime()));
                            } else {
                                stmt.setNull(3, java.sql.Types.DATE);
                            }
                            stmt.setString(4, ((JSpinner.DefaultEditor)timeField.getEditor()).getTextField().getText());
                            stmt.setString(5, buildingField.getText() + " - Room " + roomField.getText());
                            break;
                            
                        case "tasks":
                            stmt.setInt(1, userId);
                            stmt.setString(2, titleField.getText());
                            stmt.setString(3, subjectField.getText());
                            if (dateChooser.getDate() != null) {
                                stmt.setDate(4, new java.sql.Date(dateChooser.getDate().getTime()));
                            } else {
                                stmt.setNull(4, java.sql.Types.DATE);
                            }
                            stmt.setString(5, "PENDING");
                            break;
                            
                        case "exams":
                            stmt.setInt(1, userId);
                            stmt.setString(2, subjectField.getText());
                            if (dateChooser.getDate() != null) {
                                stmt.setDate(3, new java.sql.Date(dateChooser.getDate().getTime()));
                            } else {
                                stmt.setNull(3, java.sql.Types.DATE);
                            }
                            stmt.setString(4, ((JSpinner.DefaultEditor)timeField.getEditor()).getTextField().getText());
                            break;
                            
                        case "reminders":
                            stmt.setInt(1, userId);
                            stmt.setString(2, titleField.getText());
                            if (dateChooser.getDate() != null) {
                                stmt.setDate(3, new java.sql.Date(dateChooser.getDate().getTime()));
                            } else {
                                stmt.setNull(3, java.sql.Types.DATE);
                            }
                            stmt.setString(4, ((JSpinner.DefaultEditor)timeField.getEditor()).getTextField().getText());
                            stmt.setBoolean(5, repeatCheck.isSelected());
                            break;
                    }
                    
                    int result = stmt.executeUpdate();
                    dialog.dispose();
                    loadItems();
                    
                    // Refresh the dashboard if an item was added
                    if (result > 0) {
                        refreshDashboard();
                    }
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(dialog, "Error saving item: " + ex.getMessage());
            }
        });
        
        JButton cancelButton = createStyledButton("Cancel");
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        // Add padding at the bottom
        gbc.gridy++;
        gbc.insets = new Insets(20, 10, 10, 10);
        gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);
        
        dialog.add(panel);
        dialog.pack();
        dialog.setVisible(true);
    }
    
    private String getInsertQueryForType() {
        switch (type) {
            case "classes":
                return "INSERT INTO classes (user_id, title, day, time, location) VALUES (?, ?, ?, ?, ?)";
            case "tasks": // Renamed from assignments
                return "INSERT INTO tasks (user_id, title, subject, due_date, status) VALUES (?, ?, ?, ?, ?)"; // Renamed table
            case "exams":
                return "INSERT INTO exams (user_id, subject, exam_date, time) VALUES (?, ?, ?, ?)";
            default:
                return "";
        }
    }
    
    private JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setForeground(new Color(50, 50, 50));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        return field;
    }

    private JSpinner createTimeSpinner() {
        SpinnerDateModel model = new SpinnerDateModel();
        JSpinner spinner = new JSpinner(model);
        spinner.setEditor(new JSpinner.DateEditor(spinner, "HH:mm"));
        JComponent editor = spinner.getEditor();
        JSpinner.DefaultEditor spinnerEditor = (JSpinner.DefaultEditor)editor;
        spinnerEditor.getTextField().setFont(new Font("Arial", Font.PLAIN, 14));
        spinnerEditor.getTextField().setForeground(new Color(50, 50, 50));
        spinner.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        return spinner;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(70, 130, 180));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(100, 35));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);

        // Add rounded corners and shadow effect
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(2, 2, 4, 2),
            BorderFactory.createLineBorder(new Color(60, 120, 170), 1)
        ));

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(100, 150, 200));
                button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createEmptyBorder(2, 2, 4, 2),
                    BorderFactory.createLineBorder(new Color(90, 140, 190), 1)
                ));
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(70, 130, 180));
                button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createEmptyBorder(2, 2, 4, 2),
                    BorderFactory.createLineBorder(new Color(60, 120, 170), 1)
                ));
            }
        });

        return button;
    }

    private void addFormField(JPanel panel, String label, JComponent field, GridBagConstraints gbc, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        JLabel lblField = new JLabel(label);
        lblField.setFont(new Font("Arial", Font.BOLD, 14));
        lblField.setForeground(new Color(70, 70, 70));
        panel.add(lblField, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(field, gbc);
    }

    // Edit functionality removed for next version
    private void showEditDialog(int itemId) {
        JOptionPane.showMessageDialog(this, "Edit functionality will be available in the next version.");
    }
    
    private void navigateBackToDashboard() {
        dispose();
        SwingUtilities.invokeLater(() -> {
            Dashboard dashboard = new Dashboard(userId, "User");
            dashboard.setVisible(true);
        });
    }

    private void deleteItem(int itemId) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this item?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);
                
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DBConnection.getConnection()) {
                String query = "DELETE FROM " + type + " WHERE id = ? AND user_id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setInt(1, itemId);
                    stmt.setInt(2, userId);
                    int result = stmt.executeUpdate();
                    if (result > 0) {
                        loadItems();
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error deleting item: " + e.getMessage());
            }
        }
    }

    private void markTaskComplete(int itemId) {
        try (Connection conn = DBConnection.getConnection()) {
            String query = "UPDATE tasks SET status = 'COMPLETED' WHERE id = ? AND user_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, itemId);
                stmt.setInt(2, userId);
                int result = stmt.executeUpdate();
                if (result > 0) {
                    loadItems();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating task status: " + e.getMessage());
        }
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }
    
    /**
     * Refreshes the dashboard to show updated schedule items
     */
    private void refreshDashboard() {
        // Find all open Dashboard instances and refresh them
        for (Window window : Window.getWindows()) {
            if (window instanceof Dashboard) {
                Dashboard dashboard = (Dashboard) window;
                // Clear and reload today's tasks
                dashboard.loadTodaysTasks();
            }
        }
    }
}