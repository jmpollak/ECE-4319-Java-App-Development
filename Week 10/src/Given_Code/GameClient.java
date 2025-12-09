package Given_Code;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class GameClient
{
    private static final String SERVER = "localhost";
    private static final int PORT = 8081;

    public static void main(String[] args)
    {
        System.out.println("Connecting to trivia game server...");

        try
        {
            Socket socket = new Socket(SERVER, PORT);
            System.out.println("Connected! Game starting...\n");

            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();

            Scanner userInput = new Scanner(System.in);
            String line;

            while ((line = (String) in.readObject()) != null)
            {
                if (line.equals("GAME_OVER"))
                {
                    System.out.println("\n" + "=".repeat(50));
                    System.out.println("GAME OVER!");
                    String finalScore = (String) in.readObject();
                    System.out.println(finalScore);
                    System.out.println("=".repeat(50));
                    break;
                }
                else if (line.startsWith("RESULT:"))
                {
                    String result = line.substring(7);
                    System.out.println("\n>>> " + result);
                    System.out.println("-".repeat(50) + "\n");
                }
                else if (line.startsWith("Your answer"))
                {
                    System.out.print(line);
                    String scannerInput = userInput.nextLine();

                    // Validate input
                    while (!scannerInput.matches("[1-4]"))
                    {
                        System.out.print("Invalid input. Please enter 1, 2, 3, or 4: ");
                        scannerInput = userInput.nextLine();
                    }

                    out.writeObject(scannerInput);
                    out.flush();
                }
                else
                {
                    System.out.println(line);
                }
            }

            socket.close();
            userInput.close();
            System.out.println("\nDisconnected from server. Thanks for playing!");
        }
        catch (ClassNotFoundException e)
        {
            System.out.println("Class not found during deserialization");
            e.printStackTrace();
        }
        catch (IOException e)
        {
            System.out.println("Connection error: " + e.getMessage());
            System.out.println("Make sure the server is running first!");
        }
    }
}