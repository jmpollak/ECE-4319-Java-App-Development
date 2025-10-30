package Given_Code;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class GameClient
{
    private static final String SERVER = "localhost";
    private static final int PORT = 8081;

    public static void main(String[] args)
    {
        try
        {
            Socket socket = new Socket(SERVER, PORT);
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());

            String line;
            while ((line = (String) in.readObject()) != null)
            {
                if(line.startsWith("Your answer is: "))
                {
                    // User Input
                    System.out.println(line);
                    Scanner userInput = new Scanner(System.in);
                    String scannerInput = userInput.nextLine();

                    // Send user input back to the server
                    out.writeObject(scannerInput);
                    out.flush();
                }
                else
                {
                    System.out.println(line);
                }
            }
            socket.close();
        }
        catch (IOException | ClassNotFoundException e)
        {
            System.out.println("There is and IOException");
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
