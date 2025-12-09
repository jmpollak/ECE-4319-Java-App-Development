import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatServer
{
    private static final int PORT = 8083;
    private static final Set<PrintWriter> clientWriters = ConcurrentHashMap.newKeySet();

    // Starts the client to connect to the server
    public static void main(String[] args)
    {
        // Debugging help
        System.out.println("Chat server running on port " + PORT + "...");

        // Creates the number of threads (Users) for program
        ExecutorService pool = Executors.newFixedThreadPool(20);

        // Tries to connect to the server socket
        try(ServerSocket serverSocket = new ServerSocket(PORT))
        {
            while(true)
            {
                Socket socket = serverSocket.accept(); // Waits for clients to connect
                System.out.println("New client connected: " + socket);
                pool.execute(new ClientHandler(socket));
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private static class ClientHandler implements Runnable
    {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;

        public ClientHandler(Socket socket)
        {
            this.socket = socket;
        }

        @Override
        public void run()
        {
            try
            {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
                clientWriters.add(out); // Set

                out.println("Welcome to the chat!");
                broadcast("A new user has joined the chat.");

                String message;
                while((message = in.readLine()) != null)
                {
                    if(message.equalsIgnoreCase("bye"))
                    {
                        break;
                    }
                    System.out.println("Received: " + message);
                    broadcast(message);
                }
            }
            catch (IOException e)
            {
                System.out.println("Connection lost: " + socket);
            }
            finally
            {
                try
                {
                    //clientWriters.remove(out);
                    socket.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
                broadcast("A user has left the chat.");
                System.out.println("Client disconnected: " + socket);
            }
        }

        private void broadcast(String message)
        {
            for(PrintWriter writer : clientWriters)
            {
                writer.println(message);
            }
        }

    }
}