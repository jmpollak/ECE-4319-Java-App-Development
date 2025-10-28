import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server
{
    private final int portNumber;
    private final int maxClients;

    public Server(int portNumber, int maxClients)
    {
        this.portNumber = portNumber;
        this.maxClients = maxClients;
    }

    public void receiveConnection() throws IOException
    {
        ServerSocket serverSocket = new ServerSocket(portNumber,maxClients);

        Socket socket = serverSocket.accept();

        System.out.println("Connection received from: " + socket.getInetAddress().getHostName());

        ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());

        output.flush();

        output.writeObject("Server: Hello");
        output.flush();

        socket.close();
    }
}
