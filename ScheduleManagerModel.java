import java.sql.*;
import java.time.format.DateTimeFormatter;

public class ScheduleManagerModel {
    private int userId;
    
    public ScheduleManagerModel(int userId) {
        this.userId = userId;
    }
    
    public ResultSet getItems(String type) throws SQLException {
        Connection conn = DBConnection.getConnection();
        String query = getQueryForType(type);
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setInt(1, userId);
        return stmt.executeQuery();
    }
    
    private String getQueryForType(String type) {
        switch (type) {
            case "classes":
                return "SELECT id, title, day, DATE_FORMAT(time, '%h:%i %p') as time, location FROM classes WHERE user_id = ?";
            case "tasks":
                return "SELECT id, title, subject, due_date, due_time, status FROM tasks WHERE user_id = ?";
            case "exams":
                return "SELECT id, subject, exam_date, time FROM exams WHERE user_id = ?";
            default:
                return "";
        }
    }
    
    public void deleteItem(String type, int itemId) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            String query = "DELETE FROM " + type + " WHERE id = ? AND user_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, itemId);
                stmt.setInt(2, userId);
                stmt.executeUpdate();
            }
        }
    }
    
    public void markTaskComplete(int taskId) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            String query = "UPDATE tasks SET status = 'COMPLETED' WHERE id = ? AND user_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, taskId);
                stmt.setInt(2, userId);
                stmt.executeUpdate();
            }
        }
    }
    
    public void addClass(String title, String day, String time, String location) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            String query = "INSERT INTO classes (user_id, title, day, time, location) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, userId);
                stmt.setString(2, title);
                stmt.setString(3, day);
                stmt.setString(4, time);
                stmt.setString(5, location);
                stmt.executeUpdate();
            }
        }
    }
    
    public void addTask(String title, String subject, Date dueDate, Time dueTime) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            String query = "INSERT INTO tasks (user_id, title, subject, due_date, due_time, status) VALUES (?, ?, ?, ?, ?, 'PENDING')";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, userId);
                stmt.setString(2, title);
                stmt.setString(3, subject);
                stmt.setDate(4, new java.sql.Date(dueDate.getTime()));
                stmt.setTime(5, dueTime);
                stmt.executeUpdate();
            }
        }
    }
    
    public void addExam(String subject, Date examDate, String time) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            String query = "INSERT INTO exams (user_id, subject, exam_date, time) VALUES (?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, userId);
                stmt.setString(2, subject);
                stmt.setDate(3, new java.sql.Date(examDate.getTime()));
                stmt.setString(4, time);
                stmt.executeUpdate();
            }
        }
    }
}