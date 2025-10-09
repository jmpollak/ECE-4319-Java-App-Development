package InClassProjectWIP;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ArrayList;

public class CardLayoutFrame extends JFrame
{
    // All the buttons
    private JButton startButton;
    private JButton nextButton;
    private JButton usernameButton;
    private JButton playButton;
    private JButton[] optionButtons = new JButton[4];

    // The Card Layout
    private CardLayout cardLayout;

    // All the Panels
    private JPanel cardPanel;
    private JPanel usernamePanel;
    //private JPanel nestedButtonPanel;

    // All the Labels
    private JLabel questionLabel;

    // Used for Username
    private JTextField inputField;
    private String username = "player";

    // The 2D matrix for the questions
    private int currentQuestionIndex;
    private List<List<String>> questionGroups;
    private  ArrayList<Integer> correctAnswers;


    public CardLayoutFrame()
    {
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        currentQuestionIndex = 0;

        // Adds the questions
        importQuestions();

        // first page - welcome
        createWelcomePanel();

        // username page
        createUsernamePanel();

        // second page - quiz
        createGamePanel();

        add(cardPanel);
        ButtonHandler handler = new ButtonHandler();
        startButton.addActionListener(handler);
        usernameButton.addActionListener(handler);
        playButton.addActionListener(handler);
        nextButton.addActionListener(handler);
    }

    private void createResultsPanel()
    {
        JPanel resultsPanel = new JPanel();
        resultsPanel.add(new JLabel("Results Screen: " + username));
        cardPanel.add(resultsPanel, "R");
    }

    private void createGamePanel()
    {
        JPanel gamePanel = new JPanel(new BorderLayout());

        questionLabel = new JLabel("Place holder for questions",SwingConstants.CENTER);
        questionLabel.setFont(new Font("Arial", Font.BOLD,16));
        gamePanel.add(questionLabel,BorderLayout.NORTH);

        nextButton = new JButton("Go to results");
        gamePanel.add(nextButton, BorderLayout.SOUTH);

        // Initialize the 4 buttons
        JPanel nestedButtonPanel = new JPanel(new GridLayout(2,2,10,10));
        for(int i = 0; i < 4; i++)
        {
            optionButtons[i] = new JButton("Option " + (i + 1));
            optionButtons[i].addActionListener(new OptionButtonHandler(i));
            nestedButtonPanel.add(optionButtons[i]);
        }

        // Adding the nested button panel to the game panel
        gamePanel.add(nestedButtonPanel,BorderLayout.CENTER);

        /* remember to add this username panel to the cardPanel object, and give it a name */
        cardPanel.add(gamePanel, "G");
    }

    private void createWelcomePanel()
    {
        JPanel welcomePanel = new JPanel(new BorderLayout());

        JLabel title = new JLabel("Welcome Screen", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        welcomePanel.add(title, BorderLayout.CENTER);

        startButton = new JButton("Start game!");
        welcomePanel.add(startButton, BorderLayout.SOUTH);
        /* remember to add this username panel to the cardPanel object, and give it
        a name */
        cardPanel.add(welcomePanel, "W");
    }

    private void createUsernamePanel()
    {
        //create main panel
        usernamePanel = new JPanel(new BorderLayout());
        /* create a nested username button panel */
        JPanel usernameButtonPanel = new JPanel(new FlowLayout());
        //create input field
        inputField = new JTextField("Enter a username");
        inputField.setFont(new Font("Arial", Font.PLAIN, 20));
        //create username button
        usernameButton = new JButton("set username");
        //add the input field to the username button panel
        usernameButtonPanel.add(inputField);
        //add the username button to the username button panel
        usernameButtonPanel.add(usernameButton);
        //add this nested username button panel to the main username panel
        usernamePanel.add(usernameButtonPanel, BorderLayout.NORTH);
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

    private class ButtonHandler implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            if (e.getSource() == startButton)
            {
                cardLayout.show(cardPanel, "U"); // show username
            }
            else if (e.getSource() == usernameButton)
            {
                //save username
                username = inputField.getText();
                //show message to welcome the user
                JOptionPane.showMessageDialog(null, "Welcome " + username + "!");
            }
            else if (e.getSource() == playButton)
            {
                // Loads the questions
                loadNextQuestion();
                // show quiz game
                cardLayout.show(cardPanel, "G");
            }
            else if (e.getSource() == nextButton)
            {
                createResultsPanel();
                cardLayout.show(cardPanel, "R"); // show results
            }
        }
    }

    private class OptionButtonHandler implements ActionListener
    {
        private int index;
        public OptionButtonHandler(int index) // Allows us to get the passed value into this handler
        {
            this.index = index;
        }
        @Override
        public void actionPerformed(ActionEvent e)
        {
            //Checking if the answer is correct
            if(index == correctAnswers.get(currentQuestionIndex))
            {
                // User gets a point
                JOptionPane.showMessageDialog(null,"Correct");
            }
            else
            {
                // No point
                JOptionPane.showMessageDialog(null,"Incorrect");
            }
            //Increment the question index
            currentQuestionIndex++;
            // Go to the next question
            loadNextQuestion();
        }
    }

    private void importQuestions()
    {
        questionGroups = new ArrayList<>();
        correctAnswers = new ArrayList<>();
        // This is immutable
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

    private void loadNextQuestion()
    {
        if (questionGroups == null || questionGroups.size() == 0 || currentQuestionIndex > questionGroups.size())
        {
            cardLayout.show(cardPanel, "R"); // Show Results
            return;
        }
        // Retrieving the Question from the array of questions for changing the prompt
        questionLabel.setText(questionGroups.get(currentQuestionIndex).get(0));
        for (int i = 0; i < 4; i++) // Try replacing 4 with optionButtons.length
        {
            optionButtons[i].setText(questionGroups.get(currentQuestionIndex).get(i +1));
        }
    }
}
