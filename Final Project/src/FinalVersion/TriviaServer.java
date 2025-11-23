package FinalVersion;
import java.io.*;
import java.net.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.*;

public class TriviaServer
{
    private static final int PORT = 8888;
    private static final int MAX_PLAYERS = 10;
    private static final int QUESTION_TIME_LIMIT = 30; // 30 seconds per question

    private ServerSocket serverSocket;
    private List<ClientHandler> clients;
    private Map<String, Integer> playerScores;
    private Set<String> playersAnswered; // Track who has answered current question
    private List<List<String>> questionGroups;
    private List<Integer> correctAnswers;
    private int currentQuestionIndex;
    private String currentQuestionSet;
    private int currentCategory; // 1-4 for the four categories
    private boolean gameInProgress;
    private int playersReady;
    private Timer questionTimer;

    // Database manager
    private DatabaseManager dbManager;
    private Set<String> loggedInUsers; // Track currently logged in users

    public TriviaServer()
    {
        clients = new CopyOnWriteArrayList<>();
        playerScores = new ConcurrentHashMap<>();
        playersAnswered = ConcurrentHashMap.newKeySet();
        currentQuestionIndex = 0;
        gameInProgress = false;
        playersReady = 0;
        currentCategory = 0;

        // Initialize database connection
        loggedInUsers = ConcurrentHashMap.newKeySet();
        dbManager = new DatabaseManager();

        if (!dbManager.isConnected()) {
            System.err.println("WARNING: Database connection failed. Server may not function properly.");
        }
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

                System.out.println("New connection established. Total connections: " + clients.size());
            }
        }
        catch (IOException e)
        {
            System.err.println("Server error: " + e.getMessage());
        }
        finally
        {
            if (dbManager != null) {
                dbManager.close();
            }
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

        // Update high scores in database for each player
        for (Map.Entry<String, Integer> entry : playerScores.entrySet()) {
            String username = entry.getKey();
            int score = entry.getValue();
            dbManager.updateUserScore(username, currentCategory, score);
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

        // Send high scores for the category
        sendHighScores(currentCategory);

        System.out.println("Game ended. Final scores sent to all players.");

        // Reset for next game
        currentQuestionIndex = 0;
        playersReady = 0;
        playerScores.clear();
        playersAnswered.clear();
    }

    private void sendHighScores(int category) {
        try {
            ResultSet rs = dbManager.getHighScores(category, 10);
            if (rs != null) {
                StringBuilder highScores = new StringBuilder("HIGH_SCORES|");
                highScores.append(category).append("|");

                while (rs.next()) {
                    String username = rs.getString("username");
                    int score = rs.getInt("score");
                    highScores.append(username).append(":").append(score).append("|");
                }

                broadcast(highScores.toString());
                System.out.println("High scores sent for category " + category);
            }
        } catch (SQLException e) {
            System.err.println("Error sending high scores: " + e.getMessage());
        }
    }

    private class ClientHandler implements Runnable
    {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;
        private String username;
        private boolean authenticated;

        public ClientHandler(Socket socket)
        {
            this.socket = socket;
            this.authenticated = false;
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
                case "SIGN_UP":
                    if (parts.length >= 3)
                    {
                        String user = parts[1];
                        String pass = parts[2];

                        // Validate input
                        if (user.isEmpty() || pass.isEmpty()) {
                            sendMessage("REGISTER_FAILED|Username and password cannot be empty");
                            return;
                        }

                        if (pass.length() < 4) {
                            sendMessage("REGISTER_FAILED|Password must be at least 4 characters");
                            return;
                        }

                        if (loggedInUsers.contains(user)) {
                            sendMessage("REGISTER_FAILED|User already logged in");
                            return;
                        }

                        // Try to register in database
                        boolean success = dbManager.registerUser(user, pass);

                        if (success) {
                            username = user;
                            authenticated = true;
                            loggedInUsers.add(username);
                            playerScores.put(username, 0);

                            // Send user their previous scores
                            DatabaseManager.UserScores scores = dbManager.getUserScores(username);
                            if (scores != null) {
                                sendMessage("USER_SCORES|" + scores.scoreCat1 + "|" + scores.scoreCat2 +
                                        "|" + scores.scoreCat3 + "|" + scores.scoreCat4);
                            }

                            sendMessage("REGISTER_SUCCESS|" + username);
                            updatePlayerList();
                            System.out.println("New player registered and joined: " + username);
                        } else {
                            sendMessage("REGISTER_FAILED|Username already exists");
                        }
                    }
                    break;

                case "SIGN_IN":
                    if (parts.length >= 3)
                    {
                        String user = parts[1];
                        String pass = parts[2];

                        // Validate input
                        if (user.isEmpty() || pass.isEmpty()) {
                            sendMessage("LOGIN_FAILED|Username and password cannot be empty");
                            return;
                        }

                        // Check if user exists
                        if (!dbManager.userExists(user)) {
                            sendMessage("LOGIN_FAILED|Username does not exist");
                            return;
                        }

                        // Check if already logged in
                        if (loggedInUsers.contains(user)) {
                            sendMessage("LOGIN_FAILED|User already logged in");
                            return;
                        }

                        // Authenticate
                        boolean authenticated = dbManager.authenticateUser(user, pass);

                        if (authenticated) {
                            username = user;
                            this.authenticated = true;
                            loggedInUsers.add(username);
                            playerScores.put(username, 0);

                            // Send user their previous scores
                            DatabaseManager.UserScores scores = dbManager.getUserScores(username);
                            if (scores != null) {
                                sendMessage("USER_SCORES|" + scores.scoreCat1 + "|" + scores.scoreCat2 +
                                        "|" + scores.scoreCat3 + "|" + scores.scoreCat4);
                            }

                            sendMessage("LOGIN_SUCCESS|" + username);
                            updatePlayerList();
                            System.out.println("Player logged in and joined: " + username);
                        } else {
                            sendMessage("LOGIN_FAILED|Incorrect password");
                        }
                    }
                    break;

                case "QUESTION_SET":
                    if (!gameInProgress && authenticated)
                    {
                        int setIndex = Integer.parseInt(parts[1]);
                        currentCategory = setIndex + 1; // Store category (1-4)
                        String questionPath = "Mini Project/src/questionSet" + (setIndex + 1) + ".txt";
                        String answerPath = "Mini Project/src/answerSet" + (setIndex + 1) + ".txt";
                        loadQuestions(questionPath, answerPath);
                        currentQuestionSet = parts[2];
                        broadcast("QUESTION_SET_SELECTED|" + currentQuestionSet);
                    }
                    break;

                case "READY":
                    if (authenticated)
                    {
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
                    }
                    break;

                case "ANSWER":
                    if (gameInProgress && authenticated && !playersAnswered.contains(username))
                    {
                        playersAnswered.add(username);

                        int answerIndex = Integer.parseInt(parts[1]);
                        int correctAnswer = correctAnswers.get(currentQuestionIndex);

                        boolean correct = (answerIndex == correctAnswer);
                        if (correct)
                        {
                            playerScores.put(username, playerScores.get(username) + 1);
                        }

                        // Send result with correct answer index and selected answer
                        sendMessage("ANSWER_RESULT|" + correct + "|" +
                                playerScores.get(username) + "|" +
                                correctAnswer + "|" + answerIndex);

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

                case "SKIP":
                    if (gameInProgress && authenticated && !playersAnswered.contains(username))
                    {
                        playersAnswered.add(username);

                        // Skipped question counts as wrong, don't increment score
                        int correctAnswer = correctAnswers.get(currentQuestionIndex);

                        // Send result showing it was skipped (-1 for skip)
                        sendMessage("ANSWER_RESULT|false|" +
                                playerScores.get(username) + "|" +
                                correctAnswer + "|-1");

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

                case "REQUEST_HIGH_SCORES":
                    if (authenticated && parts.length >= 2) {
                        int category = Integer.parseInt(parts[1]);
                        sendHighScores(category);
                    }
                    break;

                case "REQUEST_USER_SCORES":
                    if (authenticated) {
                        // Send user their updated scores from database
                        DatabaseManager.UserScores scores = dbManager.getUserScores(username);
                        if (scores != null) {
                            sendMessage("USER_SCORES|" + scores.scoreCat1 + "|" + scores.scoreCat2 +
                                    "|" + scores.scoreCat3 + "|" + scores.scoreCat4);
                        }
                    }
                    break;

                case "RESTART":
                    if (!gameInProgress && authenticated)
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
                loggedInUsers.remove(username);
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