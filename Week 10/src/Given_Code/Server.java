package Given_Code;

import javax.swing.JFrame;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Server
{
    public static Map<String, List<Question>> questionSets = new HashMap<>();
    public static int PORT = 8081;
    public static int queueLength = 20;
    private static int playerCount = 0;

    public static void main(String[] args)
    {
        System.out.println("Game server started...");

        // Load all question sets
        loadAllQuestionSets();

        if (questionSets.isEmpty())
        {
            System.err.println("FATAL ERROR: No question files found!");
            System.err.println("Please create question files in the project root directory.");
            System.err.println("Server shutting down...");
            return;
        }

        System.out.println("Loaded " + questionSets.size() + " question sets.");
        for (String category : questionSets.keySet())
        {
            System.out.println("  - " + category + ": " + questionSets.get(category).size() + " questions");
        }

        System.out.println("Waiting for players to connect on port " + PORT + "...");

        try
        {
            ServerSocket serverSocket = new ServerSocket(PORT, queueLength);

            // Accept multiple clients
            while (true)
            {
                Socket socket = serverSocket.accept();
                playerCount++;
                System.out.println("\n[SERVER] Player " + playerCount + " connected from " + socket.getInetAddress());

                // Create a new thread for each client
                String playerName = "Player " + playerCount;
                Thread clientThread = new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        ServerFrame frame = new ServerFrame(playerName, questionSets);
                        frame.setSize(800, 600);
                        frame.setTitle("Trivia Game - " + playerName);
                        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                        frame.setLocationRelativeTo(null);
                        frame.setVisible(true);

                        ClientHandler client = new ClientHandler(frame, socket, playerName);
                        client.waitForCategorySelection();
                    }
                });

                clientThread.start();
            }
        }
        catch (IOException e)
        {
            System.out.println("[SERVER ERROR] " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("Game server ended.");
    }

    private static void loadAllQuestionSets()
    {
        // Define question file mappings
        // OPTION 1: Files in project root (default)
        String[][] questionFiles = {
                {"Science", "Week 10/src/Given_Code/qScience.txt"},
                {"History", "Week 10/src/Given_Code/qHistory.txt"},
                {"Geography", "Week 10/src/Given_Code/qGeography.txt"},
                {"General Knowledge", "Week 10/src/Given_Code/qGeneral.txt"}
        };

        for (String[] fileInfo : questionFiles)
        {
            String category = fileInfo[0];
            String filename = fileInfo[1];

            List<Question> questions = loadQuestionsFromFile(filename);
            if (questions != null && !questions.isEmpty())
            {
                questionSets.put(category, questions);
                System.out.println("Loaded " + category + " from " + filename);
            }
            else
            {
                System.out.println("Warning: Could not load " + filename);
            }
        }
    }

    private static List<Question> loadQuestionsFromFile(String filename)
    {
        List<Question> questions = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filename)))
        {
            String line;
            while ((line = reader.readLine()) != null)
            {
                // Skip empty lines
                if (line.trim().isEmpty())
                {
                    continue;
                }

                // Read question
                String questionText = line.trim();

                // Read 4 options
                String[] options = new String[4];
                for (int i = 0; i < 4; i++)
                {
                    line = reader.readLine();
                    if (line == null)
                    {
                        System.out.println("ERROR: Incomplete question format - missing options in " + filename);
                        return null;
                    }
                    options[i] = line.trim();
                }

                // Read correct answer index (0-based in file)
                line = reader.readLine();
                if (line == null)
                {
                    System.out.println("ERROR: Incomplete question format - missing answer in " + filename);
                    return null;
                }

                int correctIndex = Integer.parseInt(line.trim());

                // Validate correct index
                if (correctIndex < 0 || correctIndex > 3)
                {
                    System.out.println("ERROR: Invalid answer index: " + correctIndex +
                            " in " + filename + ". Must be 0-3.");
                    return null;
                }

                // Add question to list
                questions.add(new Question(questionText, options, correctIndex));
            }

            return questions;
        }
        catch (IOException e)
        {
            System.out.println("Could not read file: " + filename);
            return null;
        }
        catch (NumberFormatException e)
        {
            System.out.println("ERROR: Invalid answer format in " + filename + " - must be a number 0-3");
            return null;
        }
    }
}