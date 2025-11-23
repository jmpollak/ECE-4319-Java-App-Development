package FinalVersion;

import java.sql.*;

/**
 * DatabaseManager - Simplified wrapper around UserCRUD
 * Uses the DBConnection class for database connectivity
 */
public class DatabaseManager
{
    private Connection connection;
    private UserCRUD userCRUD;
    
    /**
     * Constructor - establishes database connection using DBConnection
     */
    public DatabaseManager()
    {
        try
        {
            connection = DBConnection.getConnection();
            userCRUD = new UserCRUD(connection);
            System.out.println("✓ DatabaseManager initialized successfully");
        }
        catch (SQLException e)
        {
            System.err.println("✗ Database connection failed: " + e.getMessage());
            System.err.println("\nMake sure:");
            System.err.println("1. MySQL is running");
            System.err.println("2. Database 'userdb' exists");
            System.err.println("3. Credentials in DBConnection.java are correct");
            connection = null;
            userCRUD = null;
        }
    }
    
    /**
     * Check if database connection is valid
     */
    public boolean isConnected()
    {
        try
        {
            return connection != null && !connection.isClosed();
        }
        catch (SQLException e)
        {
            return false;
        }
    }
    
    /**
     * Register a new user
     * @return true if successful, false if username already exists or error occurs
     */
    public boolean registerUser(String username, String password)
    {
        if (!isConnected())
        {
            System.err.println("✗ No database connection");
            return false;
        }
        
        return userCRUD.addUser(username, password);
    }
    
    /**
     * Authenticate user login
     * @return true if credentials are valid, false otherwise
     */
    public boolean authenticateUser(String username, String password)
    {
        if (!isConnected())
        {
            System.err.println("✗ No database connection");
            return false;
        }
        
        return userCRUD.authenticateUser(username, password);
    }
    
    /**
     * Check if username exists
     */
    public boolean userExists(String username)
    {
        if (!isConnected())
        {
            System.err.println("✗ No database connection");
            return false;
        }
        
        return userCRUD.userExists(username);
    }
    
    /**
     * Get user's score for a specific category
     * @param category 1-4 for the four categories
     */
    public int getUserScore(String username, int category)
    {
        if (!isConnected() || category < 1 || category > 4)
        {
            return 0;
        }
        
        return userCRUD.getUserScore(username, category);
    }
    
    /**
     * Update user's score for a specific category (only if new score is higher)
     * @param category 1-4 for the four categories
     */
    public boolean updateUserScore(String username, int category, int newScore)
    {
        if (!isConnected() || category < 1 || category > 4)
        {
            return false;
        }
        
        return userCRUD.updateUserScore(username, category, newScore);
    }
    
    /**
     * Get all user scores for a specific category (for high scores/leaderboard)
     * @param category 1-4 for the four categories
     * @return ResultSet containing username and score, ordered by score descending
     */
    public ResultSet getHighScores(int category, int limit)
    {
        if (!isConnected() || category < 1 || category > 4)
        {
            return null;
        }
        
        return userCRUD.getHighScores(category, limit);
    }
    
    /**
     * Get all scores for a specific user
     */
    public UserScores getUserScores(String username)
    {
        if (!isConnected())
        {
            return null;
        }
        
        int[] scores = userCRUD.getUserScores(username);
        return new UserScores(scores[0], scores[1], scores[2], scores[3]);
    }
    
    /**
     * Close database connection
     */
    public void close()
    {
        try
        {
            if (connection != null && !connection.isClosed())
            {
                connection.close();
                System.out.println("✓ Database connection closed");
            }
        }
        catch (SQLException e)
        {
            System.err.println("✗ Error closing database connection: " + e.getMessage());
        }
    }
    
    /**
     * Inner class to hold user scores
     */
    public static class UserScores
    {
        public int scoreCat1;
        public int scoreCat2;
        public int scoreCat3;
        public int scoreCat4;
        
        public UserScores(int cat1, int cat2, int cat3, int cat4)
        {
            this.scoreCat1 = cat1;
            this.scoreCat2 = cat2;
            this.scoreCat3 = cat3;
            this.scoreCat4 = cat4;
        }
        
        public int getScore(int category)
        {
            switch (category)
            {
                case 1: return scoreCat1;
                case 2: return scoreCat2;
                case 3: return scoreCat3;
                case 4: return scoreCat4;
                default: return 0;
            }
        }
    }
    
    /**
     * Test method
     */
    public static void main(String[] args)
    {
        System.out.println("Testing DatabaseManager...\n");
        
        DatabaseManager dbManager = new DatabaseManager();
        
        if (dbManager.isConnected())
        {
            System.out.println("✓ Database connection successful!\n");
            
            // Test user registration
            System.out.println("Testing user registration:");
            dbManager.registerUser("gamePlayer1", "pass123");
            
            // Test authentication
            System.out.println("\nTesting authentication:");
            boolean auth = dbManager.authenticateUser("gamePlayer1", "pass123");
            System.out.println("Authentication result: " + (auth ? "✓ SUCCESS" : "✗ FAILED"));
            
            // Test score update
            System.out.println("\nTesting score updates:");
            dbManager.updateUserScore("gamePlayer1", 1, 8);
            dbManager.updateUserScore("gamePlayer1", 2, 9);
            
            // Get user scores
            System.out.println("\nGetting user scores:");
            UserScores scores = dbManager.getUserScores("gamePlayer1");
            if (scores != null)
            {
                System.out.printf("Category 1: %d/10\n", scores.scoreCat1);
                System.out.printf("Category 2: %d/10\n", scores.scoreCat2);
                System.out.printf("Category 3: %d/10\n", scores.scoreCat3);
                System.out.printf("Category 4: %d/10\n", scores.scoreCat4);
            }
            
            // Get high scores
            System.out.println("\nGetting high scores for category 1:");
            try
            {
                ResultSet rs = dbManager.getHighScores(1, 5);
                if (rs != null)
                {
                    int rank = 1;
                    while (rs.next())
                    {
                        System.out.printf("%d. %s - %d/10\n",
                                        rank++,
                                        rs.getString("username"),
                                        rs.getInt("score"));
                    }
                }
            }
            catch (SQLException e)
            {
                System.err.println("Error reading high scores: " + e.getMessage());
            }
            
            System.out.println("\n✓ All tests completed!");
        }
        else
        {
            System.err.println("✗ Database connection failed!");
        }
        
        dbManager.close();
    }
}
