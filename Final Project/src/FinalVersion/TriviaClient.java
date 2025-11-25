package FinalVersion;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.util.Timer;
import java.util.TimerTask;

public class TriviaClient extends JFrame {
    // Network components
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String serverAddress = "localhost";
    private static final int PORT = 8888;

    // All the buttons
    private JButton startButton;
    private JButton signInButton;
    private JButton signUpButton;
    private JButton playButton;
    private JButton readyButton;
    private JButton againButton;
    private JButton viewHighScoresButton;
    private JButton skipButton;
    private JButton[] optionButtons = new JButton[4];
    private JButton[] questionSetButtons = new JButton[4];

    // Button Handler
    private ButtonHandler handler = new ButtonHandler();

    // The Card Layout
    private CardLayout cardLayout;

    // The Panel
    private JPanel cardPanel;

    // All the Labels
    private JLabel questionLabel;
    private JLabel questionSetLabel;
    private JLabel playerListLabel;
    private JLabel scoreLabel;
    private JLabel timerLabel;
    private JLabel userScoresLabel;

    // Used for Username and Password
    private JTextField inputUsername;
    private JPasswordField inputPassword;
    private String username = "";
    private JTextArea leaderboardArea;
    private JTextArea highScoresArea;

    // Variables needed for game state
    private int currentQuestionNumber;
    private int score = 0;
    private boolean waitingForAnswer = false;
    private int currentCategory = 0;

    // User's high scores from database
    private int[] userHighScores = new int[4]; // scoreCat1, scoreCat2, scoreCat3, scoreCat4

    // Timer components
    private Timer countdownTimer;
    private int timeRemaining;
    private int totalTime;
    private JProgressBar timerProgressBar;

    public TriviaClient()
    {
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        currentQuestionNumber = 0;

        // welcome
        createWelcomePanel();

        // connect to the server
        createConnectionPanel();

        // username and password
        createUsernamePanel();

        // connects all users
        createLobbyPanel();

        // question panel
        createQuestionSetPanel();

        // starts the game
        createGamePanel();

        add(cardPanel);

        // Assigns all the action listeners for all buttons
        startButton.addActionListener(handler);
        signInButton.addActionListener(handler);
        signUpButton.addActionListener(handler);
        playButton.addActionListener(handler);
    }

    private void createWelcomePanel()
    {
        JPanel welcomePanel = new JPanel(new BorderLayout());

        JLabel title = new JLabel("Multiplayer Trivia Game", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        welcomePanel.add(title, BorderLayout.NORTH);

        JLabel subtitle = new JLabel("Connect to server to play with friends!", SwingConstants.CENTER);
        subtitle.setFont(new Font("Arial", Font.PLAIN, 16));
        welcomePanel.add(subtitle, BorderLayout.CENTER);

        startButton = new JButton("Connect to Server");
        welcomePanel.add(startButton, BorderLayout.SOUTH);

        cardPanel.add(welcomePanel, "WELCOME");
    }

    private void createConnectionPanel()
    {
        JPanel connectionPanel = new JPanel(new BorderLayout());

        JLabel title = new JLabel("Server Connection", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        connectionPanel.add(title, BorderLayout.NORTH);

        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel serverLabel = new JLabel("Server Address:");
        JTextField serverField = new JTextField("localhost");

        inputPanel.add(serverLabel);
        inputPanel.add(serverField);

        connectionPanel.add(inputPanel, BorderLayout.CENTER);

        JButton connectButton = new JButton("Connect");
        connectButton.addActionListener(e -> {
            serverAddress = serverField.getText();
            if (connectToServer())
            {
                cardLayout.show(cardPanel, "USERNAME");
            }
            else
            {
                JOptionPane.showMessageDialog(this,
                        "Failed to connect to server. Make sure the server is running.",
                        "Connection Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
        connectionPanel.add(connectButton, BorderLayout.SOUTH);

        cardPanel.add(connectionPanel, "CONNECTION");
    }

    private void createUsernamePanel()
    {
        JPanel usernamePanel = new JPanel(new BorderLayout());

        JLabel title = new JLabel("Login or Create Account", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        usernamePanel.add(title, BorderLayout.NORTH);

        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        // Username field
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        inputUsername = new JTextField(20);
        inputUsername.setFont(new Font("Arial", Font.PLAIN, 16));

        // Password field
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        inputPassword = new JPasswordField(20);
        inputPassword.setFont(new Font("Arial", Font.PLAIN, 16));

        inputPanel.add(usernameLabel);
        inputPanel.add(inputUsername);
        inputPanel.add(passwordLabel);
        inputPanel.add(inputPassword);

        usernamePanel.add(inputPanel, BorderLayout.CENTER);

        // Button panel at the bottom
        JPanel buttonPanel = new JPanel(new FlowLayout());
        signInButton = new JButton("Sign In");
        signUpButton = new JButton("Sign Up");

        signInButton.setFont(new Font("Arial", Font.BOLD, 14));
        signUpButton.setFont(new Font("Arial", Font.BOLD, 14));

        buttonPanel.add(signInButton);
        buttonPanel.add(signUpButton);
        usernamePanel.add(buttonPanel, BorderLayout.SOUTH);

        cardPanel.add(usernamePanel, "USERNAME");
    }

    private void createLobbyPanel() {
        JPanel lobbyPanel = new JPanel(new BorderLayout());

        JLabel title = new JLabel("Game Lobby", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        lobbyPanel.add(title, BorderLayout.NORTH);

        // Center panel for player list and user scores
        JPanel centerPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        playerListLabel = new JLabel("Players: ", SwingConstants.CENTER);
        playerListLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        centerPanel.add(playerListLabel);

        userScoresLabel = new JLabel("<html>Your High Scores:<br/>Loading...</html>", SwingConstants.CENTER);
        userScoresLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        centerPanel.add(userScoresLabel);

        lobbyPanel.add(centerPanel, BorderLayout.CENTER);

        playButton = new JButton("Select Question Category");
        lobbyPanel.add(playButton, BorderLayout.SOUTH);

        cardPanel.add(lobbyPanel, "LOBBY");
    }

    private void createQuestionSetPanel() {
        JPanel questionSetPanel = new JPanel(new BorderLayout());

        questionSetLabel = new JLabel("Select Question Set", SwingConstants.CENTER);
        questionSetLabel.setFont(new Font("Arial", Font.BOLD, 16));
        questionSetPanel.add(questionSetLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 10, 10));

        String[] categories = {"Lord of the Rings/Hobbit", "Star Wars", "Marvel", "Harry Potter"};
        for (int i = 0; i < 4; i++) {
            questionSetButtons[i] = new JButton(categories[i]);
            final int index = i;
            questionSetButtons[i].addActionListener(e -> {
                currentCategory = index + 1;
                sendMessage("QUESTION_SET|" + index + "|" + categories[index]);
                createReadyPanel();
                cardLayout.show(cardPanel, "READY");
            });
            buttonPanel.add(questionSetButtons[i]);
        }

        questionSetPanel.add(buttonPanel, BorderLayout.CENTER);
        cardPanel.add(questionSetPanel, "QUESTION_SET");
    }

    private void createReadyPanel() {
        JPanel readyPanel = new JPanel(new BorderLayout());

        JLabel title = new JLabel("Waiting for Players", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        readyPanel.add(title, BorderLayout.NORTH);

        playerListLabel = new JLabel("Players ready: 0/0", SwingConstants.CENTER);
        playerListLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        readyPanel.add(playerListLabel, BorderLayout.CENTER);

        readyButton = new JButton("Ready!");
        readyButton.addActionListener(e -> {
            sendMessage("READY");
            readyButton.setEnabled(false);
            readyButton.setText("Waiting...");
        });
        readyPanel.add(readyButton, BorderLayout.SOUTH);

        cardPanel.add(readyPanel, "READY");
    }

    private void createGamePanel() {
        JPanel gamePanel = new JPanel(new BorderLayout());

        // Top panel for question
        JPanel topPanel = new JPanel(new BorderLayout());
        questionLabel = new JLabel("Question will appear here", SwingConstants.CENTER);
        questionLabel.setFont(new Font("Arial", Font.BOLD, 16));
        questionLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        topPanel.add(questionLabel, BorderLayout.CENTER);

        // Timer panel
        JPanel timerPanel = new JPanel(new BorderLayout());
        timerLabel = new JLabel("Time: 30", SwingConstants.CENTER);
        timerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        timerPanel.add(timerLabel, BorderLayout.NORTH);

        timerProgressBar = new JProgressBar(0, 100);
        timerProgressBar.setStringPainted(true);
        timerProgressBar.setForeground(Color.GREEN);
        timerProgressBar.setBackground(Color.LIGHT_GRAY);
        timerProgressBar.setFont(new Font("Arial", Font.BOLD, 14)); // Make text larger and bold

        // Set custom UI to change text color to black for better readability
        timerProgressBar.setUI(new javax.swing.plaf.basic.BasicProgressBarUI() {
            @Override
            protected Color getSelectionForeground() {
                return Color.BLACK; // Text color on filled portion
            }
            @Override
            protected Color getSelectionBackground() {
                return Color.BLACK; // Text color on unfilled portion
            }
        });

        timerPanel.add(timerProgressBar, BorderLayout.CENTER);

        topPanel.add(timerPanel, BorderLayout.SOUTH);
        gamePanel.add(topPanel, BorderLayout.NORTH);

        // Options panel
        JPanel optionsPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        optionsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        for (int i = 0; i < 4; i++) {
            optionButtons[i] = new JButton("Option " + (i + 1));
            optionButtons[i].setFont(new Font("Arial", Font.PLAIN, 14));
            optionButtons[i].addActionListener(new OptionButtonHandler(i));
            optionsPanel.add(optionButtons[i]);
        }

        gamePanel.add(optionsPanel, BorderLayout.CENTER);

        // Bottom panel with score and skip button
        JPanel bottomPanel = new JPanel(new BorderLayout());

        scoreLabel = new JLabel("Score: 0/10", SwingConstants.CENTER);
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 18));
        bottomPanel.add(scoreLabel, BorderLayout.CENTER);

        // Skip button
        skipButton = new JButton("Skip Question");
        skipButton.setFont(new Font("Arial", Font.BOLD, 14));
        skipButton.setBackground(new Color(255, 165, 0)); // Orange color
        skipButton.setForeground(Color.WHITE);
        skipButton.addActionListener(e -> handleSkip());
        bottomPanel.add(skipButton, BorderLayout.EAST);

        gamePanel.add(bottomPanel, BorderLayout.SOUTH);

        cardPanel.add(gamePanel, "GAME");
    }

    private void createResultsPanel(String results) {
        JPanel resultsPanel = new JPanel(new BorderLayout());

        JLabel title = new JLabel("Game Over!", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        resultsPanel.add(title, BorderLayout.NORTH);

        // Split panel for current game results and high scores
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

        // Left side: Current game results
        JPanel currentResultsPanel = new JPanel(new BorderLayout());
        JLabel currentResultsTitle = new JLabel("This Game's Results:", SwingConstants.CENTER);
        currentResultsTitle.setFont(new Font("Arial", Font.BOLD, 16));
        currentResultsPanel.add(currentResultsTitle, BorderLayout.NORTH);

        leaderboardArea = new JTextArea(results);
        leaderboardArea.setFont(new Font("Arial", Font.PLAIN, 14));
        leaderboardArea.setEditable(false);
        JScrollPane currentScrollPane = new JScrollPane(leaderboardArea);
        currentResultsPanel.add(currentScrollPane, BorderLayout.CENTER);

        // Right side: High scores
        JPanel highScoresPanel = new JPanel(new BorderLayout());
        JLabel highScoresTitle = new JLabel("All-Time High Scores:", SwingConstants.CENTER);
        highScoresTitle.setFont(new Font("Arial", Font.BOLD, 16));
        highScoresPanel.add(highScoresTitle, BorderLayout.NORTH);

        highScoresArea = new JTextArea("Loading high scores...");
        highScoresArea.setFont(new Font("Arial", Font.PLAIN, 14));
        highScoresArea.setEditable(false);
        JScrollPane highScoresScrollPane = new JScrollPane(highScoresArea);
        highScoresPanel.add(highScoresScrollPane, BorderLayout.CENTER);

        splitPane.setLeftComponent(currentResultsPanel);
        splitPane.setRightComponent(highScoresPanel);
        splitPane.setDividerLocation(400);

        resultsPanel.add(splitPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());

        againButton = new JButton("Play Again");
        againButton.addActionListener(e -> {
            sendMessage("RESTART");
        });

        viewHighScoresButton = new JButton("Refresh High Scores");
        viewHighScoresButton.addActionListener(e -> {
            if (currentCategory > 0) {
                sendMessage("REQUEST_HIGH_SCORES|" + currentCategory);
            }
        });

        buttonPanel.add(againButton);
        buttonPanel.add(viewHighScoresButton);
        resultsPanel.add(buttonPanel, BorderLayout.SOUTH);

        cardPanel.add(resultsPanel, "RESULTS");
    }

    private void updateUserScoresDisplay() {
        String[] categories = {"Lord of the Rings/Hobbit", "Star Wars", "Marvel", "Harry Potter"};
        StringBuilder scoresText = new StringBuilder("<html>Your High Scores:<br/>");

        for (int i = 0; i < 4; i++) {
            scoresText.append(categories[i]).append(": ")
                    .append(userHighScores[i]).append("/10<br/>");
        }
        scoresText.append("</html>");

        userScoresLabel.setText(scoresText.toString());
    }

    private boolean connectToServer() {
        try {
            socket = new Socket(serverAddress, PORT);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Start listening for server messages
            new Thread(new ServerListener()).start();

            return true;
        } catch (IOException e) {
            System.err.println("Connection error: " + e.getMessage());
            return false;
        }
    }

    private void sendMessage(String message) {
        if (out != null) {
            out.println(message);
        }
    }

    private void enableOptionButtons(boolean enabled) {
        for (JButton button : optionButtons) {
            button.setEnabled(enabled);
        }
    }

    private void startCountdown(int seconds)
    {
        totalTime = seconds;
        timeRemaining = seconds;

        // Cancel any existing timer
        stopCountdown();

        countdownTimer = new Timer();
        countdownTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> {
                    if (timeRemaining >= 0)
                    {
                        timerLabel.setText("Time: " + timeRemaining);
                        int percentage = (int) ((double) timeRemaining / totalTime * 100);
                        timerProgressBar.setValue(percentage);

                        // Change color based on time remaining
                        if (percentage > 50)
                        {
                            timerProgressBar.setForeground(Color.GREEN);
                        }
                        else if (percentage > 25)
                        {
                            timerProgressBar.setForeground(Color.ORANGE);
                        }
                        else
                        {
                            timerProgressBar.setForeground(Color.RED);
                        }
                    }

                    if (timeRemaining <= 0)
                    {
                        countdownTimer.cancel();
                        if (!waitingForAnswer)
                        {
                            enableOptionButtons(false);
                        }
                    }

                    timeRemaining--;
                });
            }
        }, 0, 1000);
    }

    private void stopCountdown()
    {
        if (countdownTimer != null)
        {
            countdownTimer.cancel();
            countdownTimer = null;
        }
    }

    /**
     * Handle skip button click
     */
    private void handleSkip()
    {
        if (!waitingForAnswer)
        {
            waitingForAnswer = true;
            sendMessage("SKIP");
            enableOptionButtons(false);
            skipButton.setEnabled(false);
            stopCountdown();
        }
    }

    /**
     * Show visual feedback for correct/incorrect answers
     * @param correctAnswerIndex The index of the correct answer (0-3)
     * @param selectedIndex The index the user selected (or -1 for skip)
     */
    private void showAnswerFeedback(int correctAnswerIndex, int selectedIndex)
    {
        // Reset all buttons to default
        for (int i = 0; i < 4; i++)
        {
            optionButtons[i].setBackground(null); // Default button color
            optionButtons[i].setOpaque(false);
        }

        // Highlight correct answer in green
        if (correctAnswerIndex >= 0 && correctAnswerIndex < 4)
        {
            optionButtons[correctAnswerIndex].setBackground(new Color(76, 175, 80)); // Green
            optionButtons[correctAnswerIndex].setOpaque(true);
            optionButtons[correctAnswerIndex].setForeground(Color.WHITE);
        }

        // Highlight selected wrong answer in red (if different from correct)
        if (selectedIndex >= 0 && selectedIndex != correctAnswerIndex && selectedIndex < 4)
        {
            optionButtons[selectedIndex].setBackground(new Color(244, 67, 54)); // Red
            optionButtons[selectedIndex].setOpaque(true);
            optionButtons[selectedIndex].setForeground(Color.WHITE);
        }

        // Give user time to see the feedback before moving to next question
        Timer feedbackTimer = new Timer();
        feedbackTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> {
                    // Reset button colors
                    for (int i = 0; i < 4; i++)
                    {
                        optionButtons[i].setBackground(null);
                        optionButtons[i].setOpaque(false);
                        optionButtons[i].setForeground(Color.BLACK);
                    }
                });
            }
        }, 2000); // Show feedback for 2 seconds
    }

    /**
     * Reset button colors to default
     */
    private void resetButtonColors()
    {
        for (int i = 0; i < 4; i++)
        {
            optionButtons[i].setBackground(null);
            optionButtons[i].setOpaque(false);
            optionButtons[i].setForeground(Color.BLACK);
        }
    }

    private class ButtonHandler implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            if (e.getSource() == startButton)
            {
                cardLayout.show(cardPanel, "CONNECTION");
            }
            else if (e.getSource() == signInButton)
            {
                handleSignIn();
            }
            else if (e.getSource() == signUpButton)
            {
                handleSignUp();
            }
            else if (e.getSource() == playButton)
            {
                cardLayout.show(cardPanel, "QUESTION_SET");
            }
        }
    }

    private void handleSignIn()
    {
        String user = inputUsername.getText().trim();
        String pass = new String(inputPassword.getPassword());

        if (user.isEmpty())
        {
            JOptionPane.showMessageDialog(this,
                    "Please enter a username",
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (pass.isEmpty())
        {
            JOptionPane.showMessageDialog(this,
                    "Please enter a password",
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        sendMessage("SIGN_IN|" + user + "|" + pass);
    }

    private void handleSignUp()
    {
        String user = inputUsername.getText().trim();
        String pass = new String(inputPassword.getPassword());

        if (user.isEmpty())
        {
            JOptionPane.showMessageDialog(this,
                    "Please enter a username",
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (pass.isEmpty())
        {
            JOptionPane.showMessageDialog(this,
                    "Please enter a password",
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (pass.length() < 4)
        {
            JOptionPane.showMessageDialog(this,
                    "Password must be at least 4 characters long",
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        sendMessage("SIGN_UP|" + user + "|" + pass);
    }

    private class OptionButtonHandler implements ActionListener
    {
        private final int index;

        public OptionButtonHandler(int index)
        {
            this.index = index;
        }

        @Override
        public void actionPerformed(ActionEvent e)
        {
            if (!waitingForAnswer)
            {
                waitingForAnswer = true;
                sendMessage("ANSWER|" + index);
                enableOptionButtons(false);
                stopCountdown();
            }
        }
    }

    private class ServerListener implements Runnable
    {
        @Override
        public void run()
        {
            try
            {
                String message;
                while ((message = in.readLine()) != null)
                {
                    processServerMessage(message);
                }
            }
            catch (IOException e)
            {
                System.err.println("Lost connection to server");
                SwingUtilities.invokeLater(() ->
                {
                    stopCountdown();
                    JOptionPane.showMessageDialog(TriviaClient.this,
                            "Lost connection to server",
                            "Connection Error",
                            JOptionPane.ERROR_MESSAGE);
                    System.exit(0);
                });
            }
        }

        private void processServerMessage(String message)
        {
            String[] parts = message.split("\\|");
            String command = parts[0];

            SwingUtilities.invokeLater(() ->
            {
                switch (command)
                {
                    case "REGISTER_SUCCESS":
                        username = parts[1];
                        JOptionPane.showMessageDialog(TriviaClient.this,
                                "Account created successfully! Welcome " + username + "!");
                        cardLayout.show(cardPanel, "LOBBY");
                        break;

                    case "REGISTER_FAILED":
                        JOptionPane.showMessageDialog(TriviaClient.this,
                                "Registration failed: " + parts[1],
                                "Registration Error",
                                JOptionPane.ERROR_MESSAGE);
                        break;

                    case "LOGIN_SUCCESS":
                        username = parts[1];
                        JOptionPane.showMessageDialog(TriviaClient.this,
                                "Welcome back, " + username + "!");
                        cardLayout.show(cardPanel, "LOBBY");
                        break;

                    case "LOGIN_FAILED":
                        JOptionPane.showMessageDialog(TriviaClient.this,
                                "Login failed: " + parts[1],
                                "Login Error",
                                JOptionPane.ERROR_MESSAGE);
                        break;

                    case "USER_SCORES":
                        // parts[1-4] are scoreCat1, scoreCat2, scoreCat3, scoreCat4
                        if (parts.length >= 5) {
                            for (int i = 0; i < 4; i++) {
                                userHighScores[i] = Integer.parseInt(parts[i + 1]);
                            }
                            updateUserScoresDisplay();
                        }
                        break;

                    case "PLAYER_LIST":
                        StringBuilder players = new StringBuilder("Players: ");
                        for (int i = 1; i < parts.length; i++)
                        {
                            players.append(parts[i]).append(", ");
                        }
                        playerListLabel.setText(players.toString());
                        break;

                    case "QUESTION_SET_SELECTED":
                        JOptionPane.showMessageDialog(TriviaClient.this,
                                "Question set selected: " + parts[1]);
                        break;

                    case "PLAYER_READY":
                        playerListLabel.setText("Players ready: " + parts[2] + "/" + parts[3]);
                        break;

                    case "GAME_START":
                        cardLayout.show(cardPanel, "GAME");
                        break;

                    case "QUESTION":
                        currentQuestionNumber = Integer.parseInt(parts[1]);
                        int timeLimit = Integer.parseInt(parts[2]);
                        questionLabel.setText(parts[3]);
                        for (int i = 0; i < 4; i++)
                        {
                            optionButtons[i].setText(parts[i + 4]);
                        }
                        resetButtonColors(); // Reset colors for new question
                        enableOptionButtons(true);
                        skipButton.setEnabled(true); // Enable skip button
                        waitingForAnswer = false;
                        startCountdown(timeLimit);
                        break;

                    case "ANSWER_RESULT":
                        boolean correct = Boolean.parseBoolean(parts[1]);
                        score = Integer.parseInt(parts[2]);
                        int correctAnswerIndex = Integer.parseInt(parts[3]);
                        int selectedAnswerIndex = parts.length > 4 ? Integer.parseInt(parts[4]) : -1;

                        stopCountdown();

                        // Show visual feedback
                        showAnswerFeedback(correctAnswerIndex, selectedAnswerIndex);

                        String resultMessage = correct ?
                                "✓ Correct! Score: " + score + "/10" :
                                "✗ Incorrect! The correct answer was: " +
                                        optionButtons[correctAnswerIndex].getText() + "\nScore: " + score + "/10";

                        JOptionPane.showMessageDialog(TriviaClient.this, resultMessage);

                        scoreLabel.setText("Score: " + score + "/10");
                        break;

                    case "TIME_EXPIRED":
                        stopCountdown();
                        enableOptionButtons(false);
                        JOptionPane.showMessageDialog(TriviaClient.this,
                                "Time's up! Moving to next question...");
                        break;

                    case "SCORE_UPDATE":
                        // Could display real-time scores here
                        break;

                    case "GAME_OVER":
                        stopCountdown();
                        StringBuilder results = new StringBuilder("Final Standings:\n\n");
                        for (int i = 1; i < parts.length; i++)
                        {
                            String[] playerScore = parts[i].split(":");
                            if (playerScore.length == 2)
                            {
                                results.append(i).append(". ")
                                        .append(playerScore[0])
                                        .append(": ")
                                        .append(playerScore[1])
                                        .append("/10\n");
                            }
                        }
                        createResultsPanel(results.toString());
                        cardLayout.show(cardPanel, "RESULTS");
                        break;

                    case "HIGH_SCORES":
                        // parts[1] is category, parts[2+] are username:score pairs
                        if (parts.length >= 2) {
                            StringBuilder highScoresText = new StringBuilder();
                            String[] categories = {"Lord of the Rings/Hobbit", "Star Wars", "Marvel", "Harry Potter"};
                            int category = Integer.parseInt(parts[1]);

                            highScoresText.append(categories[category - 1]).append(" - Top 10:\n\n");

                            for (int i = 2; i < parts.length; i++) {
                                String[] userScore = parts[i].split(":");
                                if (userScore.length == 2) {
                                    highScoresText.append((i - 1)).append(". ")
                                            .append(userScore[0])
                                            .append(": ")
                                            .append(userScore[1])
                                            .append("/10\n");
                                }
                            }

                            if (highScoresArea != null) {
                                highScoresArea.setText(highScoresText.toString());
                            }
                        }
                        break;

                    case "GAME_RESET":
                        score = 0;
                        currentQuestionNumber = 0;
                        stopCountdown();
                        resetButtonColors();
                        // Request updated scores from server
                        sendMessage("REQUEST_USER_SCORES");
                        cardLayout.show(cardPanel, "LOBBY");
                        break;
                }
            });
        }
    }

    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(() -> {
            TriviaClient client = new TriviaClient();
            client.setSize(900, 700);
            client.setTitle("Multiplayer Trivia Game");
            client.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            client.setLocationRelativeTo(null);
            client.setVisible(true);
        });
    }
}