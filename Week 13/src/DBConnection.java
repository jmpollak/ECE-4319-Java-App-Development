import com.mysql.cj.util.DnsSrv;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Connection;

public class DBConnection
{
    private static final String URL = "jdbc:mysql://localhost:3306/userdb";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    public static Connection getConnection() throws SQLException
    {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void main(String[] args) throws SQLException
    {
        UserCRUD userCRUD = new UserCRUD(getConnection());

//        userCRUD.addUser("testUser01");
        userCRUD.listUsers();

        userCRUD.updateUser(7,"testUser0000001");

        userCRUD.listUsers();

        userCRUD.deleteUser(7);

        userCRUD.listUsers();
    }
}
