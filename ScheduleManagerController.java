import javax.swing.*;
import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import com.toedter.calendar.JDateChooser;

public class ScheduleManagerController {
    private ScheduleManagerView view;
    private ScheduleManagerModel model;
    private int userId;
    private String type;
    
    public ScheduleManagerController(int userId, String type) {
        this.userId = userId;
        this.type = type;
        this.model = new ScheduleManagerModel(userId);
        this.view = new ScheduleManagerView(userId, type, this);
    }
    
    public void showView() {
        view.setVisible(true);
        loadItems();
    }
    
    public void loadItems() {
        try {
            ResultSet rs = model.getItems(type);
            displayItems(rs);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(view, "Error loading items: " + e.getMessage());
        }
    }
    
    private void displayItems(ResultSet rs) throws SQLException {
        view.listPanel.removeAll();
        
        boolean hasItems = false;
        while (rs.next()) {
            JPanel itemPanel = createItemPanel(rs);
            view.listPanel.add(itemPanel);
            view.listPanel.add(Box.createVerticalStrut(10));
            hasItems = true;
        }
        
        if (!hasItems) {
            showNoItemsMessage();
        }
        
        view.revalidate();
        view.repaint();
    }
    
    private void showNoItemsMessage() {
        JLabel noItemsLabel = new JLabel("No " + type + " found. Click 'Add New' to create one.");
        noItemsLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        noItemsLabel.setForeground(new Color(120, 120, 120));
        noItemsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JPanel messagePanel = new JPanel();
        messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS));
        messagePanel.setOpaque(false);
        messagePanel.add(Box.createVerticalStrut(30));
        messagePanel.add(noItemsLabel);
        
        view.listPanel.add(messagePanel);
    }
    
    private JPanel createItemPanel(ResultSet rs) throws SQLException {
        // Implementation will be moved from ScheduleManager
        return null; // Placeholder
    }
    
    public void showAddDialog() {
        JDialog dialog = new JDialog(view, "Add New " + capitalize(type), true);
        dialog.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        switch (type) {
            case "tasks":
                showAddTaskDialog(dialog, formPanel, gbc);
                break;
            case "classes":
                showAddClassDialog(dialog, formPanel, gbc);
                break;
            case "exams":
                showAddExamDialog(dialog, formPanel, gbc);
                break;
        }

        dialog.pack();
        dialog.setLocationRelativeTo(view);
        dialog.setVisible(true);
    }

    private void showAddTaskDialog(JDialog dialog, JPanel formPanel, GridBagConstraints gbc) {
        JTextField titleField = view.createStyledTextField();
        JTextField subjectField = view.createStyledTextField();
        JDateChooser dateChooser = new JDateChooser();
        dateChooser.setPreferredSize(new Dimension(200, 35));
        dateChooser.setFont(new Font("Arial", Font.PLAIN, 14));
        dateChooser.setBackground(Color.WHITE);
        
        // Enhanced time spinner styling
        JSpinner timeSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(timeSpinner, "HH:mm");
        timeSpinner.setEditor(timeEditor);
        timeSpinner.setPreferredSize(new Dimension(200, 35));
        JComponent editor = timeSpinner.getEditor();
        JFormattedTextField timeField = ((JSpinner.DefaultEditor) editor).getTextField();
        timeField.setFont(new Font("Arial", Font.PLAIN, 14));
        timeField.setBackground(Color.WHITE);
        timeField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
    
        // Add components with enhanced labels
        JLabel[] labels = {
            new JLabel("Title:"),
            new JLabel("Subject:"),
            new JLabel("Due Date:"),
            new JLabel("Time:")
        };
        for (JLabel label : labels) {
            label.setFont(new Font("Arial", Font.BOLD, 14));
            label.setForeground(new Color(60, 60, 60));
        }
    
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(labels[0], gbc);
        gbc.gridx = 1;
        formPanel.add(titleField, gbc);
    
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(labels[1], gbc);
        gbc.gridx = 1;
        formPanel.add(subjectField, gbc);
    
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(labels[2], gbc);
        gbc.gridx = 1;
        formPanel.add(dateChooser, gbc);
    
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(labels[3], gbc);
        gbc.gridx = 1;
        formPanel.add(timeSpinner, gbc);
    
        // Enhanced button panel with modern styling
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 10, 10));
        
        JButton saveButton = new JButton("Save");
        saveButton.setFont(new Font("Arial", Font.BOLD, 14));
        saveButton.setPreferredSize(new Dimension(100, 40));
        saveButton.setBackground(new Color(40, 167, 69));
        saveButton.setForeground(Color.WHITE);
        saveButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createRaisedSoftBevelBorder(),
            BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        saveButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setFont(new Font("Arial", Font.BOLD, 14));
        cancelButton.setPreferredSize(new Dimension(100, 40));
        cancelButton.setBackground(new Color(108, 117, 125));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createRaisedSoftBevelBorder(),
            BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        cancelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
    
        // Add hover effects
        saveButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                saveButton.setBackground(new Color(33, 136, 56));
            }
            public void mouseExited(MouseEvent e) {
                saveButton.setBackground(new Color(40, 167, 69));
            }
        });
    
        cancelButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                cancelButton.setBackground(new Color(90, 98, 104));
            }
            public void mouseExited(MouseEvent e) {
                cancelButton.setBackground(new Color(108, 117, 125));
            }
        });
    
        saveButton.addActionListener(e -> {
            String title = titleField.getText().trim();
            String subject = subjectField.getText().trim();
            Date dueDate = dateChooser.getDate();
            Date timeValue = (Date) timeSpinner.getValue();
            
            if (title.isEmpty() || subject.isEmpty() || dueDate == null || timeValue == null) {
                JOptionPane.showMessageDialog(dialog,
                    "Please fill in all fields",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try {
                java.sql.Date sqlDate = new java.sql.Date(dueDate.getTime());
                Time sqlTime = new Time(timeValue.getTime());
                model.addTask(title, subject, sqlDate, sqlTime);
                dialog.dispose();
                loadItems();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(dialog,
                    "Error adding task: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
    
        cancelButton.addActionListener(e -> dialog.dispose());
    
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
    
        // Set form panel padding
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
    
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
    }

    private void showAddClassDialog(JDialog dialog, JPanel formPanel, GridBagConstraints gbc) {
        JTextField titleField = new JTextField(20);
        JComboBox<String> dayComboBox = new JComboBox<>(new String[]{"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"});
        JSpinner timeSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(timeSpinner, "HH:mm");
        timeSpinner.setEditor(timeEditor);
        JTextField locationField = new JTextField(20);

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Title:"), gbc);
        gbc.gridx = 1;
        formPanel.add(titleField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Day:"), gbc);
        gbc.gridx = 1;
        formPanel.add(dayComboBox, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Time:"), gbc);
        gbc.gridx = 1;
        formPanel.add(timeSpinner, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Location:"), gbc);
        gbc.gridx = 1;
        formPanel.add(locationField, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save");
        saveButton.setBackground(new Color(40, 167, 69));
        saveButton.setForeground(Color.WHITE);
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setBackground(new Color(108, 117, 125));
        cancelButton.setForeground(Color.WHITE);

        saveButton.addActionListener(e -> {
            try {
                if (titleField.getText().trim().isEmpty() || locationField.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Please fill in all fields");
                    return;
                }
                String timeStr = new java.text.SimpleDateFormat("HH:mm").format(timeSpinner.getValue());
                model.addClass(titleField.getText().trim(), 
                             dayComboBox.getSelectedItem().toString(),
                             timeStr,
                             locationField.getText().trim());
                loadItems();
                dialog.dispose();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(dialog, "Error adding class: " + ex.getMessage());
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
    }

    private void showAddExamDialog(JDialog dialog, JPanel formPanel, GridBagConstraints gbc) {
        JTextField subjectField = new JTextField(20);
        JDateChooser dateChooser = new JDateChooser();
        JSpinner timeSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(timeSpinner, "HH:mm");
        timeSpinner.setEditor(timeEditor);

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Subject:"), gbc);
        gbc.gridx = 1;
        formPanel.add(subjectField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Exam Date:"), gbc);
        gbc.gridx = 1;
        formPanel.add(dateChooser, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Time:"), gbc);
        gbc.gridx = 1;
        formPanel.add(timeSpinner, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save");
        saveButton.setBackground(new Color(40, 167, 69));
        saveButton.setForeground(Color.WHITE);
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setBackground(new Color(108, 117, 125));
        cancelButton.setForeground(Color.WHITE);

        saveButton.addActionListener(e -> {
            try {
                if (subjectField.getText().trim().isEmpty() || dateChooser.getDate() == null) {
                    JOptionPane.showMessageDialog(dialog, "Please fill in all fields");
                    return;
                }
                String timeStr = new java.text.SimpleDateFormat("HH:mm").format(timeSpinner.getValue());
                model.addExam(subjectField.getText().trim(),
                            new java.sql.Date(dateChooser.getDate().getTime()),
                            timeStr);
                loadItems();
                dialog.dispose();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(dialog, "Error adding exam: " + ex.getMessage());
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
    }
    
    public void deleteItem(int itemId) {
        try {
            model.deleteItem(type, itemId);
            loadItems();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(view, "Error deleting item: " + e.getMessage());
        }
    }
    
    public void markTaskComplete(int taskId) {
        try {
            model.markTaskComplete(taskId);
            loadItems();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(view, "Error marking task as complete: " + e.getMessage());
        }
    }
    
    public void navigateBackToDashboard() {
        Dashboard dashboard = new Dashboard(userId, ""); // Username will be handled later
        dashboard.setVisible(true);
        view.dispose();
    }
    
    public String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }
}