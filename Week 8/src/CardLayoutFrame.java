import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.module.FindException;
import java.util.ArrayList;
import java.util.List;

public class CardLayoutFrame extends JFrame {
    private JButton startButton;
    private JButton nextButton;
    private JButton usernameButton;
    private JButton playButton;

    private CardLayout cardLayout;
    private JPanel cardPanel;
    private JPanel usernamePanel;

    private JTextField inputField;
    private String username;
    private JLabel questionLabel;
    private JButton[] optionButtons = new JButton[4];
    private int currentQuestionIdx;
    private List<List<String>> questionGroups;
    private ArrayList<Integer> correctAnswers;


    public CardLayoutFrame() {
        username = "default name";
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        currentQuestionIdx = 0;
        importQuestions();

        // first page - welcome
        createWelcomePanel();

        // username page
        createUsernamePanel();

        // second page - quiz
        createGamePanel();

//        //third page - results
//        createResultsPanel();

        add(cardPanel);

        ButtonHandler handler = new ButtonHandler();
        startButton.addActionListener(handler);
        usernameButton.addActionListener(handler);
        playButton.addActionListener(handler);
        nextButton.addActionListener(handler);
    }

    private void createResultsPanel() {
        JPanel resultsPanel = new JPanel();
        resultsPanel.add(new JLabel("Results Screen: " + username));
        cardPanel.add(resultsPanel, "R");
    }

    private void createGamePanel() {
        JPanel gamePanel = new JPanel(new BorderLayout());
        questionLabel = new JLabel("Place holder for questions", SwingConstants.CENTER);
        questionLabel.setFont(new Font("Arial", Font.BOLD, 16));
        gamePanel.add(questionLabel, BorderLayout.NORTH);

        nextButton = new JButton("Go to results");
        gamePanel.add(nextButton, BorderLayout.SOUTH);
        JPanel nestedButtonPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        for (int i = 0; i < 4; i++) {
            optionButtons[i] = new JButton("Option" + (i + 1));
            optionButtons[i].addActionListener(new OptionButtonHandler(i));
            nestedButtonPanel.add(optionButtons[i]);
        }

        gamePanel.add(nestedButtonPanel, BorderLayout.CENTER);

        /* remember to add this username panel to the cardPanel object, and give it a name */
        cardPanel.add(gamePanel, "G");
    }

    private void createWelcomePanel() {
        JPanel welcomePanel = new JPanel(new BorderLayout());
        JLabel title = new JLabel("Welcome Screen", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        welcomePanel.add(title, BorderLayout.CENTER);

        startButton = new JButton("Start game!");
        welcomePanel.add(startButton, BorderLayout.SOUTH);

        /* remember to add this username panel to the cardPanel object, and give it a name */
        cardPanel.add(welcomePanel, "W");
    }

    private void createUsernamePanel() {
        //create main panel
        usernamePanel = new JPanel(new BorderLayout());

        /* create a nested username button panel */
        JPanel nestedUsernamePanel = new JPanel(new FlowLayout());
        //create input field
        inputField = new JTextField("Enter a username");
        inputField.setFont(new Font("Arial", Font.PLAIN, 20));

        //create username button
        usernameButton = new JButton("set username");

        //add the input field to the username button panel
        nestedUsernamePanel.add(inputField);
        //add the username button to the username button panel
        nestedUsernamePanel.add(usernameButton);

        //add this nested username button panel to the main username panel
        usernamePanel.add(nestedUsernamePanel, BorderLayout.NORTH);

        /* create a nested play button panel */
        JPanel playButtonPanel = new JPanel(new FlowLayout());
        //create play button to start the game
        playButton = new JButton("Play!");
        //add the play button the play button panel
        playButtonPanel.add(playButton);

        //add this nested play button panel to the main username panel
        usernamePanel.add(playButtonPanel, BorderLayout.SOUTH);

        /* remember to add this username panel to the cardPanel object, and give it a name */
        cardPanel.add(usernamePanel, "U");

    }

    private class ButtonHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == startButton) {
                cardLayout.show(cardPanel, "U"); // show username
            } else if (e.getSource() == usernameButton) {
                //save username
                username = inputField.getText();
                //show message to welcome the user
                JOptionPane.showMessageDialog(null, "Welcome " + username + "!");

            } else if (e.getSource() == playButton) {
                loadNextQuestion();
                cardLayout.show(cardPanel, "G"); // show quiz game
            } else if (e.getSource() == nextButton) {
                createResultsPanel();
                cardLayout.show(cardPanel, "R"); // show results
            }
        }
    }

    private class OptionButtonHandler implements ActionListener {
        private int index;
        private OptionButtonHandler(int index) {
            this.index = index;
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            // check answer
            if (index == correctAnswers.get(currentQuestionIdx)) {
                JOptionPane.showMessageDialog(null, "Correct!");
            } else {
                JOptionPane.showMessageDialog(null, "that was incorrect.");
            }

            // go to the next question
            currentQuestionIdx++;
            loadNextQuestion();
        }
    }

    private void importQuestions() {
        questionGroups = new ArrayList<>();
        correctAnswers = new ArrayList<>();

//        List<String> list = new ArrayList<>();
//        list.add("Question 1: What do we refer to BST in 4319 ?");
//        list.add("British Summer Time");
//        list.add("Breadth Search Tree");
//        list.add("Binary Search Tree");
//        list.add("None of above");
//        questionGroups.add(list);
        questionGroups.add(List.of("Question 1: What do we refer to BST in 4319 ?",
                "British Summer Time",
                "Breadth Search Tree",
                "Binary Search Tree",
                "None of above"));
        correctAnswers.add(2);

        questionGroups.add(List.of("Which class belongs to Java Swing?",
                "NumberFormatException",
                "String",
                "Graphics",
                "None of above"));
        correctAnswers.add(3);

        questionGroups.add(List.of("What is the capital of France?", "Paris", "London", "Berlin", "Rome"));
        correctAnswers.add(0);

        questionGroups.add(List.of("Which planet is known as the Red Planet?", "Earth", "Venus", "Mars", "Jupiter"));
        correctAnswers.add(2);

        questionGroups.add(List.of("Recursion always needs a?", "Loop", "Base Case", "Queue", "Stack"));
        correctAnswers.add(1);
    }

    private void loadNextQuestion() {
        if (questionGroups == null || questionGroups.size() == 0
                || currentQuestionIdx >= questionGroups.size()) {
            createResultsPanel();
            cardLayout.show(cardPanel, "R"); // show results
            return;
        }
        questionLabel.setText(questionGroups.get(currentQuestionIdx).get(0));
        for (int i = 0; i < 4; i++) {
            optionButtons[i].setText(questionGroups.get(currentQuestionIdx).get(i + 1));
        }
    }
}

