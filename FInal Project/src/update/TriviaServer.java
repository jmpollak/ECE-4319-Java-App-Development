package update;

import javax.swing.JFrame;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class TriviaServer
{
    public static int PORT = 8081;
    public static int SIZE = 20;

    public static void main(String[] args)
    {
        System.out.println("Starting Trivia Server...");
        TriviaServerFrame frame = new TriviaServerFrame();

        frame.setSize(800, 600);
        frame.setTitle("Trivia Game by John Pollak ");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        try
        {
            //Start the Server
            ServerSocket serverSocket = new ServerSocket(PORT,SIZE);
            Socket socket = serverSocket.accept();

            // Connect to the client and send data
            TriviaClientHandler clientHandler = new TriviaClientHandler(frame,socket);
//            clientHandler.sendQuestionsReceiveAnswers();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    // Try to load in the questions
}
