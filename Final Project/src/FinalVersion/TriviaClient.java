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
    private JButton singUpButton;
    private JButton playButton;
    private JButton readyButton;
    private JButton againButton;
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

    // Used for Username
    private JTextField inputUsername;
    private JTextField inputPassword;
    private String username = "player";
    private JTextArea leaderboardArea;

    // Variables needed for game state
    private int currentQuestionNumber;
    private int score = 0;
    private boolean waitingForAnswer = false;

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

        createQuestionSetPanel();

        createGamePanel();

        add(cardPanel);

        // Assigns all the action listeners for all buttons
        startButton.addActionListener(handler);
        signInButton.addActionListener(handler);
        singUpButton.addActionListener(handler);
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

        JLabel title = new JLabel("Enter Your Username and Password", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        usernamePanel.add(title, BorderLayout.NORTH);

        JPanel inputPanel = new JPanel(new FlowLayout());
        // Username
        inputUsername = new JTextField("Username",15);
        inputUsername.setFont(new Font("Arial", Font.PLAIN, 18));
        // Password
        inputPassword = new JTextField("Password",15);
        inputPassword.setFont(new Font("Arial", Font.PLAIN, 18));

        signInButton = new JButton("Sign In");
        singUpButton = new JButton("Sign Up");

        // Adds all components to the panel
        inputPanel.add(inputUsername);
        inputPanel.add(inputPassword);
        inputPanel.add(signInButton);
        inputPanel.add(singUpButton);
        usernamePanel.add(inputPanel, BorderLayout.CENTER);

        cardPanel.add(usernamePanel, "USERNAME");
    }

    private void createLobbyPanel() {
        JPanel lobbyPanel = new JPanel(new BorderLayout());

        JLabel title = new JLabel("Game Lobby", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        lobbyPanel.add(title, BorderLayout.NORTH);

        playerListLabel = new JLabel("Players: ", SwingConstants.CENTER);
        playerListLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        lobbyPanel.add(playerListLabel, BorderLayout.CENTER);

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
        title.setFont(new Font("Arial", Font.BOLD, 24));
        readyPanel.add(title, BorderLayout.NORTH);

        playerListLabel = new JLabel("Players ready: 0", SwingConstants.CENTER);
        playerListLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        readyPanel.add(playerListLabel, BorderLayout.CENTER);

        readyButton = new JButton("Ready!");
        readyButton.addActionListener(e -> {
            sendMessage("READY");
            readyButton.setEnabled(false);
            readyButton.setText("Waiting for others...");
        });
        readyPanel.add(readyButton, BorderLayout.SOUTH);

        cardPanel.add(readyPanel, "READY");
    }

    private void createGamePanel() {
        JPanel gamePanel = new JPanel(new BorderLayout());

        // Top panel with question, timer, and score
        JPanel topPanel = new JPanel(new BorderLayout());

        // Timer panel
        JPanel timerPanel = new JPanel(new BorderLayout());
        timerLabel = new JLabel("Time: 15", SwingConstants.CENTER);
        timerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        timerLabel.setForeground(Color.GREEN);

        timerProgressBar = new JProgressBar(0, 100);
        timerProgressBar.setValue(100);
        timerProgressBar.setStringPainted(true);
        timerProgressBar.setForeground(Color.GREEN);

        timerPanel.add(timerLabel, BorderLayout.NORTH);
        timerPanel.add(timerProgressBar, BorderLayout.CENTER);

        topPanel.add(timerPanel, BorderLayout.NORTH);

        // Question label
        questionLabel = new JLabel("Waiting for question...", SwingConstants.CENTER);
        questionLabel.setFont(new Font("Arial", Font.BOLD, 16));
        questionLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        topPanel.add(questionLabel, BorderLayout.CENTER);

        // Score label
        scoreLabel = new JLabel("Score: 0/10", SwingConstants.CENTER);
        scoreLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        topPanel.add(scoreLabel, BorderLayout.SOUTH);

        gamePanel.add(topPanel, BorderLayout.NORTH);

        // Answer buttons
        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        for (int i = 0; i < 4; i++) {
            optionButtons[i] = new JButton("Option " + (i + 1));
            final int index = i;
            optionButtons[i].addActionListener(new OptionButtonHandler(index));
            buttonPanel.add(optionButtons[i]);
        }
        gamePanel.add(buttonPanel, BorderLayout.CENTER);

        cardPanel.add(gamePanel, "GAME");
    }

    private void createResultsPanel(String results) {
        JPanel resultsPanel = new JPanel(new BorderLayout());

        JLabel title = new JLabel("Game Results", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        resultsPanel.add(title, BorderLayout.NORTH);

        leaderboardArea = new JTextArea(10, 30);
        leaderboardArea.setFont(new Font("Arial", Font.PLAIN, 14));
        leaderboardArea.setEditable(false);
        leaderboardArea.setText(results);

        JScrollPane scrollPane = new JScrollPane(leaderboardArea);
        resultsPanel.add(scrollPane, BorderLayout.CENTER);

        againButton = new JButton("Play Again");
        againButton.addActionListener(e -> {
            sendMessage("RESTART");
            score = 0;
            currentQuestionNumber = 0;
            cardLayout.show(cardPanel, "LOBBY");
        });
        resultsPanel.add(againButton, BorderLayout.SOUTH);

        cardPanel.add(resultsPanel, "RESULTS");
    }

    private boolean connectToServer() {
        try
        {
            socket = new Socket(serverAddress, PORT);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            new Thread(new ServerListener()).start();

            System.out.println("Connected to server at " + serverAddress + ":" + PORT);
            return true;
        }
        catch (IOException e)
        {
            System.err.println("Connection failed: " + e.getMessage());
            return false;
        }
    }

    private void sendMessage(String message)
    {
        if (out != null)
        {
            out.println(message);
        }
    }

    private void enableOptionButtons(boolean enabled)
    {
        for (JButton button : optionButtons)
        {
            button.setEnabled(enabled);
            button.setBackground(null);
        }
    }

    private void startCountdown(int seconds)
    {
        // Cancel any existing timer
        if (countdownTimer != null)
        {
            countdownTimer.cancel();
        }

        timeRemaining = seconds;
        totalTime = seconds;

        countdownTimer = new Timer();
        countdownTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> {
                    timerLabel.setText("Time: " + timeRemaining);

                    // Update progress bar
                    int percentage = (int) ((double) timeRemaining / totalTime * 100);
                    timerProgressBar.setValue(percentage);

                    // Change color based on time remaining
                    if (timeRemaining <= 5)
                    {
                        timerLabel.setForeground(Color.RED);
                        timerProgressBar.setForeground(Color.RED);
                    } else if (timeRemaining <= 10)
                    {
                        timerLabel.setForeground(Color.ORANGE);
                        timerProgressBar.setForeground(Color.ORANGE);
                    }
                    else
                    {
                        timerLabel.setForeground(Color.GREEN);
                        timerProgressBar.setForeground(Color.GREEN);
                    }

                    if (timeRemaining <= 0)
                    {
                        countdownTimer.cancel();
                        // Timer expired on client side
                        if (!waitingForAnswer)
                        {
                            enableOptionButtons(false);
                        }
                    }

                    timeRemaining--;
                });
            }
        }, 0, 1000); // Update every 1 second
    }

    private void stopCountdown()
    {
        if (countdownTimer != null)
        {
            countdownTimer.cancel();
            countdownTimer = null;
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
                username = inputUsername.getText().trim();
                if (!username.isEmpty())
                {
                    sendMessage("USERNAME|" + username);
                }
                else
                {
                    JOptionPane.showMessageDialog(TriviaClient.this, "Please enter a valid username");
                }
            }
            else if (e.getSource() == playButton)
            {
                cardLayout.show(cardPanel, "QUESTION_SET");
            }
        }
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
                stopCountdown(); // Stop the countdown when answer is submitted
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
                    case "USERNAME_OK":
                        JOptionPane.showMessageDialog(TriviaClient.this, "Welcome " + username + "!");
                        cardLayout.show(cardPanel, "LOBBY");
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
                        enableOptionButtons(true);
                        waitingForAnswer = false;

                        // Start the countdown timer
                        startCountdown(timeLimit);
                        break;

                    case "ANSWER_RESULT":
                        boolean correct = Boolean.parseBoolean(parts[1]);
                        score = Integer.parseInt(parts[2]);

                        stopCountdown();

                        if (correct)
                        {
                            JOptionPane.showMessageDialog(TriviaClient.this,
                                    "Correct! Score: " + score + "/10");
                        }
                        else
                        {
                            JOptionPane.showMessageDialog(TriviaClient.this,
                                    "Incorrect! Score: " + score + "/10");
                        }

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

                    case "GAME_RESET":
                        score = 0;
                        currentQuestionNumber = 0;
                        stopCountdown();
                        cardLayout.show(cardPanel, "LOBBY");
                        break;
                }
            });
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TriviaClient client = new TriviaClient();
            client.setSize(800, 600);
            client.setTitle("Multiplayer Trivia Game");
            client.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            client.setLocationRelativeTo(null);
            client.setVisible(true);
        });
    }
}