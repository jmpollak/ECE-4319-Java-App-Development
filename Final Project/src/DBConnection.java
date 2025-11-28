import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Connection;

public class DBConnection
{
    private static final String URL = "jdbc:mysql://localhost:3306/userdb";
    private static final String USER = "root";
    private static final String PASSWORD = "root"; // Your MySQL password

    /**
     * Get a connection to the database
     * @return Connection object
     * @throws SQLException if connection fails
     */
    public static Connection getConnection() throws SQLException
    {
        try {
            // Load MySQL JDBC Driver (optional in newer versions but good practice)
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found!");
            throw new SQLException("Driver not found", e);
        }
        
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    /**
     * Test the database connection
     */
    public static void testConnection()
    {
        try (Connection conn = getConnection())
        {
            if (conn != null && !conn.isClosed())
            {
                System.out.println("✓ Database connection successful!");
                System.out.println("✓ Connected to: " + URL);
            }
        }
        catch (SQLException e)
        {
            System.err.println("✗ Database connection failed!");
            System.err.println("Error: " + e.getMessage());
            System.err.println("\nTroubleshooting:");
            System.err.println("1. Check if MySQL is running");
            System.err.println("2. Verify database 'userdb' exists");
            System.err.println("3. Check username/password are correct");
            System.err.println("4. Verify MySQL is running on port 3306");
        }
    }

    // Test method
    public static void main(String[] args)
    {
        System.out.println("Testing Database Connection...\n");
        testConnection();
    }
}
