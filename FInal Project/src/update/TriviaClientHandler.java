package update;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

public class TriviaClientHandler
{
    // Class Members
    private TriviaServerFrame triviaServerFrame;
    private Socket clientSocket;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    public TriviaClientHandler(TriviaServerFrame triviaServerFrame, Socket clientSocket)
    {
        this.triviaServerFrame = triviaServerFrame;
        this.clientSocket = clientSocket;
        // Questions would be loaded in here
    }
}
