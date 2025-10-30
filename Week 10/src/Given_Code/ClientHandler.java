package Given_Code;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

public class ClientHandler
{
    //Clas Members
    private ServerFrame frame;
    private Socket socket;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private List<Question> questions;

    public ClientHandler(ServerFrame frame, Socket socket, List<Question> questions)
    {
        this.frame = frame;
        this.socket = socket;
        this.questions = questions;
    }

    public void sendQuestionsReceiveAnswers()
    {
        try
        {
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            outputStream.flush();

            inputStream = new ObjectInputStream(socket.getInputStream());

            for(Question question: questions)
            {
                outputStream.writeObject("Q: " + question.getQuestion());
                for(int i = 0; i < 4; i++)
                {
                    outputStream.writeObject((i+1) + ") "+ question.getOptions()[i]);
                    outputStream.flush();
                }
                outputStream.writeObject("Your answer is: ");
                outputStream.flush();

                String answer = (String) inputStream.readObject();
                if(frame.validateAnswers(question, answer))
                {
                    //Scoring
                }
            }
        }
        catch(IOException | ClassNotFoundException e) // Merge the IOException and ClassNotFoundException into 1
        {
            e.printStackTrace();
        }
    }
}
