package FinalVersion;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class TriviaServer
{
    private static final int PORT = 8888;
    private static final int MAX_PLAYERS = 10;
    private static final int QUESTION_TIME_LIMIT = 30; // 15 seconds per question

    private ServerSocket serverSocket;
    private List<ClientHandler> clients;
    private Map<String, Integer> playerScores;
    private Set<String> playersAnswered; // Track who has answered current question
    private List<List<String>> questionGroups;
    private List<Integer> correctAnswers;
    private int currentQuestionIndex;
    private String currentQuestionSet;
    private boolean gameInProgress;
    private int playersReady;
    private Timer questionTimer;

    public TriviaServer()
    {
        clients = new CopyOnWriteArrayList<>();
        playerScores = new ConcurrentHashMap<>();
        playersAnswered = ConcurrentHashMap.newKeySet();
        currentQuestionIndex = 0;
        gameInProgress = false;
        playersReady = 0;
    }

    public void start()
    {
        try
        {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Trivia Server started on port " + PORT);
            System.out.println("Question time limit: " + QUESTION_TIME_LIMIT + " seconds");
            System.out.println("Waiting for players to connect...");

            while (true)
            {
                Socket clientSocket = serverSocket.accept();

                if (clients.size() >= MAX_PLAYERS)
                {
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                    out.println("SERVER_FULL");
                    clientSocket.close();
                    continue;
                }

                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clients.add(clientHandler);
                new Thread(clientHandler).start();

                System.out.println("New player connected. Total players: " + clients.size());
            }
        }
        catch (IOException e)
        {
            System.err.println("Server error: " + e.getMessage());
        }
    }

    private void loadQuestions(String questionSetPath, String answerSetPath)
    {
        questionGroups = new ArrayList<>();
        correctAnswers = new ArrayList<>();
        List<String> questionTextString = new ArrayList<>();

        try
        {
            BufferedReader questionReader = new BufferedReader(new FileReader(questionSetPath));
            BufferedReader answerReader = new BufferedReader(new FileReader(answerSetPath));

            String line;
            while ((line = questionReader.readLine()) != null)
            {
                questionTextString.add(line);
            }

            for (int i = 0; i <= (questionTextString.size() - 5); i += 5)
            {
                questionGroups.add(List.of(
                        questionTextString.get(i),
                        questionTextString.get(i + 1),
                        questionTextString.get(i + 2),
                        questionTextString.get(i + 3),
                        questionTextString.get(i + 4)
                ));
            }

            for (int i = 0; i < 10; i++)
            {
                line = answerReader.readLine();
                correctAnswers.add(Integer.parseInt(line));
            }

            questionReader.close();
            answerReader.close();

            System.out.println("Loaded " + questionGroups.size() + " questions from " + questionSetPath);

        }
        catch (IOException e)
        {
            System.err.println("Error loading questions: " + e.getMessage());
        }
    }

    private void broadcast(String message)
    {
        for (ClientHandler client : clients)
        {
            client.sendMessage(message);
        }
    }

    private void sendQuestion()
    {
        if (currentQuestionIndex >= questionGroups.size())
        {
            endGame();
            return;
        }

        playersAnswered.clear(); // Reset for new question

        List<String> question = questionGroups.get(currentQuestionIndex);
        StringBuilder questionMessage = new StringBuilder("QUESTION|");
        questionMessage.append(currentQuestionIndex + 1).append("|");
        questionMessage.append(QUESTION_TIME_LIMIT).append("|"); // Send time limit
        for (String part : question)
        {
            questionMessage.append(part).append("|");
        }

        broadcast(questionMessage.toString());
        System.out.println("Sent question " + (currentQuestionIndex + 1) + " to all players");

        // Start timer for this question
        startQuestionTimer();
    }

    private void startQuestionTimer()
    {
        // Cancel any existing timer
        if (questionTimer != null)
        {
            questionTimer.cancel();
        }

        questionTimer = new Timer();
        questionTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handleTimeExpired();
            }
        }, QUESTION_TIME_LIMIT * 1000); // Convert to milliseconds
    }

    private void handleTimeExpired()
    {
        System.out.println("Time expired for question " + (currentQuestionIndex + 1));

        // Mark players who didn't answer as having answered (with wrong answer)
        for (ClientHandler client : clients)
        {
            if (client.username != null && !playersAnswered.contains(client.username))
            {
                playersAnswered.add(client.username);
                client.sendMessage("TIME_EXPIRED");
            }
        }

        // Move to next question after a short delay
        Timer delayTimer = new Timer();
        delayTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                currentQuestionIndex++;
                sendQuestion();
            }
        }, 2000); // 2 second delay before next question
    }

    private void endGame()
    {
        gameInProgress = false;

        // Cancel timer if running
        if (questionTimer != null)
        {
            questionTimer.cancel();
        }

        // Sort players by score
        List<Map.Entry<String, Integer>> sortedScores = new ArrayList<>(playerScores.entrySet());
        sortedScores.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        StringBuilder results = new StringBuilder("GAME_OVER|");
        for (Map.Entry<String, Integer> entry : sortedScores)
        {
            results.append(entry.getKey()).append(":").append(entry.getValue()).append("|");
        }

        broadcast(results.toString());
        System.out.println("Game ended. Final scores sent to all players.");

        // Reset for next game
        currentQuestionIndex = 0;
        playersReady = 0;
        playerScores.clear();
        playersAnswered.clear();
    }

    private class ClientHandler implements Runnable
    {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;
        private String username;

        public ClientHandler(Socket socket)
        {
            this.socket = socket;
        }

        public void sendMessage(String message)
        {
            if (out != null)
            {
                out.println(message);
            }
        }

        @Override
        public void run()
        {
            try
            {
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                String message;
                while ((message = in.readLine()) != null)
                {
                    processMessage(message);
                }

            }
            catch (IOException e)
            {
                System.err.println("Client disconnected: " + username);
            }
            finally
            {
                cleanup();
            }
        }

        private void processMessage(String message)
        {
            String[] parts = message.split("\\|");
            String command = parts[0];

            switch (command)
            {
                case "USERNAME":
                    username = parts[1];
                    playerScores.put(username, 0);
                    sendMessage("USERNAME_OK|" + username);
                    updatePlayerList();
                    System.out.println("Player registered: " + username);
                    break;

                case "QUESTION_SET":
                    if (!gameInProgress)
                    {
                        int setIndex = Integer.parseInt(parts[1]);
                        String questionPath = "Mini Project/src/questionSet" + (setIndex + 1) + ".txt";
                        String answerPath = "Mini Project/src/answerSet" + (setIndex + 1) + ".txt";
                        loadQuestions(questionPath, answerPath);
                        currentQuestionSet = parts[2];
                        broadcast("QUESTION_SET_SELECTED|" + currentQuestionSet);
                    }
                    break;

                case "READY":
                    playersReady++;
                    broadcast("PLAYER_READY|" + username + "|" + playersReady + "|" + clients.size());

                    if (playersReady >= clients.size() && clients.size() > 0 && !gameInProgress)
                    {
                        gameInProgress = true;

                        // Small delay before starting
                        Timer startTimer = new Timer();
                        startTimer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                broadcast("GAME_START");
                                sendQuestion();
                            }
                        }, 1000); // 1 second delay
                    }
                    break;

                case "ANSWER":
                    if (gameInProgress && !playersAnswered.contains(username))
                    {
                        playersAnswered.add(username);

                        int answerIndex = Integer.parseInt(parts[1]);
                        int correctAnswer = correctAnswers.get(currentQuestionIndex);

                        boolean correct = (answerIndex == correctAnswer);
                        if (correct)
                        {
                            playerScores.put(username, playerScores.get(username) + 1);
                        }

                        sendMessage("ANSWER_RESULT|" + correct + "|" + playerScores.get(username));

                        // Broadcast updated scores
                        StringBuilder scoreUpdate = new StringBuilder("SCORE_UPDATE|");
                        for (Map.Entry<String, Integer> entry : playerScores.entrySet())
                        {
                            scoreUpdate.append(entry.getKey()).append(":").append(entry.getValue()).append("|");
                        }
                        broadcast(scoreUpdate.toString());

                        // Check if all players have answered
                        if (playersAnswered.size() >= clients.size())
                        {
                            System.out.println("All players answered. Moving to next question.");

                            // Cancel the timer since everyone answered
                            if (questionTimer != null)
                            {
                                questionTimer.cancel();
                            }

                            // Move to next question after brief delay
                            Timer nextTimer = new Timer();
                            nextTimer.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    currentQuestionIndex++;
                                    sendQuestion();
                                }
                            }, 2000); // 2 second delay
                        }
                    }
                    break;

                case "RESTART":
                    if (!gameInProgress)
                    {
                        currentQuestionIndex = 0;
                        playersReady = 0;
                        playersAnswered.clear();
                        for (String player : playerScores.keySet())
                        {
                            playerScores.put(player, 0);
                        }
                        broadcast("GAME_RESET");
                    }
                    break;
            }
        }

        private void updatePlayerList()
        {
            StringBuilder playerList = new StringBuilder("PLAYER_LIST|");
            for (ClientHandler client : clients)
            {
                if (client.username != null)
                {
                    playerList.append(client.username).append("|");
                }
            }
            broadcast(playerList.toString());
        }

        private void cleanup()
        {
            clients.remove(this);
            if (username != null)
            {
                playerScores.remove(username);
                playersAnswered.remove(username);
                updatePlayerList();
            }

            try
            {
                if (socket != null) socket.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args)
    {
        TriviaServer server = new TriviaServer();
        server.start();
    }
}