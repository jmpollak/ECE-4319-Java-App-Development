//package NetworkIntro;
//
//import java.io.IOException;
//import java.io.ObjectInputStream;
//import java.net.Socket;
//
//public class Client
//{
//    private static final String SERVER = "localhost";
//    private final int port;
//
//    public Client(int port)
//    {
//        this.port = port;
//    }
//
//    public void RecieveMessageFromServer() throws IOException, ClassNotFoundException
//    {
//        Socket socket = new Socket(SERVER, port);
//
//        System.out.println("Client is connected to server: " + socket.getInetAddress());
//
//        ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
//
//        String message = (String) input.readObject();
//
//        if(!message.isEmpty())
//        {
//            System.out.println("Message from server: " + message);
//        }
//
//        socket.close();
//    }
//}
