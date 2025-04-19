import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/student_planner";
    private static final String USER = "root";
    private static final String PASSWORD = "";
    private static final int MAX_POOL_SIZE = 10;
    private static final List<Connection> connectionPool = new ArrayList<>();
    private static final Object lock = new Object();
    
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC driver not found.");
            e.printStackTrace();
        }
    }
    
    public static Connection getConnection() {
        synchronized (lock) {
            try {
                if (connectionPool.isEmpty()) {
                    if (connectionPool.size() < MAX_POOL_SIZE) {
                        Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                        connectionPool.add(conn);
                        return conn;
                    }
                    throw new SQLException("Maximum pool size reached");
                }
                Connection conn = connectionPool.remove(0);
                if (conn.isClosed()) {
                    return getConnection();
                }
                return conn;
            } catch (SQLException e) {
                System.err.println("Connection to database failed.");
                e.printStackTrace();
                return null;
            }
        }
    }
    
    public static void releaseConnection(Connection conn) {
        if (conn != null) {
            synchronized (lock) {
                if (connectionPool.size() < MAX_POOL_SIZE) {
                    connectionPool.add(conn);
                } else {
                    try {
                        conn.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static void closeAllConnections() {
        synchronized (lock) {
            for (Connection conn : connectionPool) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            connectionPool.clear();
        }
    }
}