import java.sql.*;

public class UserCRUD
{
    private Connection connection;

    public UserCRUD(Connection connection)
    {
        this.connection = connection;
    }

    // ==================== READ OPERATIONS ====================
    
    /**
     * List all users in the database
     */
    public void listUsers()
    {
        String sql = "SELECT * FROM users";
        try
        {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            System.out.println("\n=== Current Users ===");
            System.out.println("ID | Username | Cat1 | Cat2 | Cat3 | Cat4");
            System.out.println("------------------------------------------");
            while(resultSet.next())
            {
                System.out.printf("%2d | %-15s | %4d | %4d | %4d | %4d\n",
                    resultSet.getInt("id"),
                    resultSet.getString("username"),
                    resultSet.getInt("scoreCat1"),
                    resultSet.getInt("scoreCat2"),
                    resultSet.getInt("scoreCat3"),
                    resultSet.getInt("scoreCat4")
                );
            }
            System.out.println("==================\n");
        }
        catch (SQLException e)
        {
            System.err.println("Error listing users: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Check if a username exists
     */
    public boolean userExists(String username)
    {
        String sql = "SELECT username FROM users WHERE username = ?";
        try
        {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        }
        catch (SQLException e)
        {
            System.err.println("Error checking user existence: " + e.getMessage());
            return false;
        }
    }

    /**
     * Authenticate a user (check username and password)
     */
    public boolean authenticateUser(String username, String password)
    {
        String sql = "SELECT password FROM users WHERE username = ?";
        try
        {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            
            if (resultSet.next())
            {
                String storedPassword = resultSet.getString("password");
                return storedPassword.equals(password);
            }
            return false; // Username not found
        }
        catch (SQLException e)
        {
            System.err.println("Error authenticating user: " + e.getMessage());
            return false;
        }
    }

    /**
     * Get user's score for a specific category
     */
    public int getUserScore(String username, int category)
    {
        if (category < 1 || category > 4) return 0;
        
        String columnName = "scoreCat" + category;
        String sql = "SELECT " + columnName + " FROM users WHERE username = ?";
        
        try
        {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            
            if (resultSet.next())
            {
                return resultSet.getInt(columnName);
            }
            return 0;
        }
        catch (SQLException e)
        {
            System.err.println("Error getting user score: " + e.getMessage());
            return 0;
        }
    }

    /**
     * Get all scores for a user
     */
    public int[] getUserScores(String username)
    {
        int[] scores = new int[4];
        String sql = "SELECT scoreCat1, scoreCat2, scoreCat3, scoreCat4 FROM users WHERE username = ?";
        
        try
        {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            
            if (resultSet.next())
            {
                scores[0] = resultSet.getInt("scoreCat1");
                scores[1] = resultSet.getInt("scoreCat2");
                scores[2] = resultSet.getInt("scoreCat3");
                scores[3] = resultSet.getInt("scoreCat4");
            }
            return scores;
        }
        catch (SQLException e)
        {
            System.err.println("Error getting user scores: " + e.getMessage());
            return scores;
        }
    }

    /**
     * Get high scores for a category (leaderboard)
     */
    public ResultSet getHighScores(int category, int limit)
    {
        if (category < 1 || category > 4) return null;
        
        String columnName = "scoreCat" + category;
        String sql = "SELECT username, " + columnName + " as score FROM users " +
                     "ORDER BY " + columnName + " DESC LIMIT ?";
        
        try
        {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, limit);
            return preparedStatement.executeQuery();
        }
        catch (SQLException e)
        {
            System.err.println("Error getting high scores: " + e.getMessage());
            return null;
        }
    }

    // ==================== CREATE OPERATIONS ====================
    
    /**
     * Add a new user with username and password
     * Fixed: Changed INSTERT to INSERT
     */
    public boolean addUser(String username, String password)
    {
        String sql = "INSERT INTO users(username, password, scoreCat1, scoreCat2, scoreCat3, scoreCat4) " +
                     "VALUES(?, ?, 0, 0, 0, 0)";
        try
        {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            preparedStatement.executeUpdate();
            System.out.println("✓ User added: " + username);
            return true;
        }
        catch (SQLException e)
        {
            if (e.getErrorCode() == 1062) // Duplicate entry
            {
                System.err.println("✗ Username already exists: " + username);
            }
            else
            {
                System.err.println("✗ Error adding user: " + e.getMessage());
            }
            return false;
        }
    }

    // ==================== UPDATE OPERATIONS ====================
    
    /**
     * Update username (original method kept for compatibility)
     */
    public void updateUser(int id, String newUsername)
    {
        String sql = "UPDATE users SET username = ? WHERE id = ?";
        try
        {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, newUsername);
            preparedStatement.setInt(2, id);
            int rows = preparedStatement.executeUpdate();
            System.out.println(rows > 0 ? "✓ User updated!" : "✗ User not found.");
        }
        catch (SQLException e)
        {
            System.err.println("✗ Error updating user: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Update user's password
     */
    public boolean updatePassword(String username, String newPassword)
    {
        String sql = "UPDATE users SET password = ? WHERE username = ?";
        try
        {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, newPassword);
            preparedStatement.setString(2, username);
            int rows = preparedStatement.executeUpdate();
            
            if (rows > 0)
            {
                System.out.println("✓ Password updated for: " + username);
                return true;
            }
            else
            {
                System.err.println("✗ User not found: " + username);
                return false;
            }
        }
        catch (SQLException e)
        {
            System.err.println("✗ Error updating password: " + e.getMessage());
            return false;
        }
    }

    /**
     * Update user's score for a specific category (only if new score is higher)
     */
    public boolean updateUserScore(String username, int category, int newScore)
    {
        if (category < 1 || category > 4) return false;
        
        // First check current score
        int currentScore = getUserScore(username, category);
        if (newScore <= currentScore)
        {
            System.out.println("New score (" + newScore + ") is not higher than current score (" + 
                             currentScore + "). Not updating.");
            return false;
        }
        
        String columnName = "scoreCat" + category;
        String sql = "UPDATE users SET " + columnName + " = ? WHERE username = ?";
        
        try
        {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, newScore);
            preparedStatement.setString(2, username);
            int rows = preparedStatement.executeUpdate();
            
            if (rows > 0)
            {
                System.out.println("✓ Updated " + username + "'s category " + category + 
                                 " score: " + currentScore + " → " + newScore);
                return true;
            }
            return false;
        }
        catch (SQLException e)
        {
            System.err.println("✗ Error updating score: " + e.getMessage());
            return false;
        }
    }

    // ==================== DELETE OPERATIONS ====================
    
    /**
     * Delete a user by ID
     */
    public void deleteUser(int id)
    {
        String sql = "DELETE FROM users WHERE id = ?";
        try
        {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            int rows = preparedStatement.executeUpdate();
            System.out.println(rows > 0 ? "✓ User deleted!" : "✗ User not found.");
        }
        catch (SQLException e)
        {
            System.err.println("✗ Error deleting user: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Delete a user by username
     */
    public boolean deleteUser(String username)
    {
        String sql = "DELETE FROM users WHERE username = ?";
        try
        {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, username);
            int rows = preparedStatement.executeUpdate();
            
            if (rows > 0)
            {
                System.out.println("✓ User deleted: " + username);
                return true;
            }
            else
            {
                System.err.println("✗ User not found: " + username);
                return false;
            }
        }
        catch (SQLException e)
        {
            System.err.println("✗ Error deleting user: " + e.getMessage());
            return false;
        }
    }

    // ==================== TEST METHOD ====================
    
    public static void main(String[] args)
    {
        System.out.println("Testing UserCRUD operations...\n");
        
        try (Connection conn = DBConnection.getConnection())
        {
            UserCRUD crud = new UserCRUD(conn);
            
            System.out.println("1. Listing existing users:");
            crud.listUsers();
            
            System.out.println("\n2. Adding test user:");
            crud.addUser("testPlayer", "password123");
            
            System.out.println("\n3. Checking if user exists:");
            System.out.println("testPlayer exists: " + crud.userExists("testPlayer"));
            
            System.out.println("\n4. Authenticating user:");
            System.out.println("Auth with correct password: " + 
                             crud.authenticateUser("testPlayer", "password123"));
            System.out.println("Auth with wrong password: " + 
                             crud.authenticateUser("testPlayer", "wrong"));
            
            System.out.println("\n5. Updating score:");
            crud.updateUserScore("testPlayer", 1, 7);
            crud.updateUserScore("testPlayer", 2, 9);
            
            System.out.println("\n6. Getting user scores:");
            int[] scores = crud.getUserScores("testPlayer");
            System.out.printf("Scores: Cat1=%d, Cat2=%d, Cat3=%d, Cat4=%d\n",
                            scores[0], scores[1], scores[2], scores[3]);
            
            System.out.println("\n7. Try updating with lower score (should not update):");
            crud.updateUserScore("testPlayer", 1, 5);
            
            System.out.println("\n8. Try updating with higher score (should update):");
            crud.updateUserScore("testPlayer", 1, 10);
            
            System.out.println("\n9. Getting high scores for category 1:");
            ResultSet rs = crud.getHighScores(1, 5);
            System.out.println("Top 5 for Category 1:");
            int rank = 1;
            while (rs.next())
            {
                System.out.printf("%d. %s - %d/10\n",
                                rank++,
                                rs.getString("username"),
                                rs.getInt("score"));
            }
            
            System.out.println("\n10. Final user list:");
            crud.listUsers();
            
            System.out.println("\n✓ All tests completed!");
            
        }
        catch (SQLException e)
        {
            System.err.println("✗ Test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}