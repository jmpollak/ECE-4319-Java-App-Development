import java.sql.*;

public class UserCRUD
{
    private Connection connection;

    public UserCRUD(Connection connection)
    {
        this.connection = connection;
    }

    // Read
    public void listUsers()
    {
        String sql = "SELECT * FROM users";
        try
        {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            System.out.println("Current users: ");
            while(resultSet.next())
            {
                System.out.println(resultSet.getInt("id") +
                                   " | " +
                                   resultSet.getString("username"));
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    // Create
    public void addUser(String username, String password)
    {
        String sql = "INSTERT INTO users(username,password) VALUES(?, ?)";
        try
        {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, "0000");
            preparedStatement.executeUpdate();
            System.out.println("User added: " + username);
        }
        catch (SQLException e)
        {
            System.out.println("Error adding user: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Update
    public void updateUser(int id, String newUsername)
    {
        String sql = "UPDATE users SET username = ? WHERE id = ?";
        try
        {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, newUsername);
            preparedStatement.setInt(2, id);
            int rows = preparedStatement.executeUpdate();
            System.out.println(rows > 0 ? "User updated!" : "User not found.");
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    // Delete
    public void deleteUser(int id)
    {
        String sql = "DELETE FROM users WHERE id = ?";
        try
        {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            int rows = preparedStatement.executeUpdate();
            System.out.println(rows > 0 ? "User deleted!" : "User not found.");
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

}
