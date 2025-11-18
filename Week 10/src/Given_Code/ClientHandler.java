package Given_Code;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

public class ClientHandler
{
    private ServerFrame frame;
    private Socket socket;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private String playerName;

    public ClientHandler(ServerFrame frame, Socket socket, String playerName)
    {
        this.frame = frame;
        this.socket = socket;
        this.playerName = playerName;
        this.frame.setClientHandler(this);
    }

    public void waitForCategorySelection()
    {
        try
        {
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            outputStream.flush();

            inputStream = new ObjectInputStream(socket.getInputStream());

            System.out.println("[" + playerName + "] Waiting for category selection...");

            // Wait for category selection from GUI
            String selectedCategory = frame.waitForCategorySelection();

            System.out.println("[" + playerName + "] Selected category: " + selectedCategory);

            // Get the questions for the selected category
            List<Question> questions = Server.questionSets.get(selectedCategory);

            if (questions == null || questions.isEmpty())
            {
                System.out.println("[" + playerName + "] ERROR: No questions found for category " + selectedCategory);
                return;
            }

            // Start the game with selected questions
            frame.startGame(questions);

            System.out.println("[" + playerName + "] Starting game with " + questions.size() + " questions...");

            for(int i = 0; i < questions.size(); i++)
            {
                Question question = questions.get(i);

                System.out.println("[" + playerName + "] Waiting for answer to question " + (i + 1));

                // Wait for GUI button click
                int answer = frame.waitForAnswer();

                // Validate and update GUI
                boolean isCorrect = frame.validateAnswers(question, String.valueOf(answer));
            }

            System.out.println("[" + playerName + "] Game completed. Score: " +
                    frame.getScore() + "/" + questions.size());
        }
        catch(Exception e)
        {
            System.out.println("[" + playerName + "] Error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
}