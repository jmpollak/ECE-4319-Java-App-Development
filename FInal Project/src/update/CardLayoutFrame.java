package update;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.awt.Color;

public class CardLayoutFrame extends JFrame
{
    // All the buttons
    private JButton startButton;
    private JButton nextButton;
    private JButton usernameButton;
    private JButton questionsButton;
    private JButton playButton;
    private JButton againButton;
    private JButton[] optionButtons = new JButton[4];
    private JButton[] questionSetButtons = new JButton[4];

    // Button Handler
    private ButtonHandler handler = new ButtonHandler();

    // The Card Layout
    private CardLayout cardLayout;

    // All the Panels
    private JPanel cardPanel;
    private JPanel usernamePanel;

    // All the Labels
    private JLabel questionLabel;
    private JLabel questionSetLabel;

    // Used for Username
    private JTextField inputField;
    private String username = "player";

    // Variables needed for the questions
    private int currentQuestionIndex;
    private List<List<String>> questionGroups;
    private ArrayList<Integer> correctAnswers;
    private List<String> questionTextString;

    // Points
    private int score = 0;

    // General Layout of the Program
    public CardLayoutFrame()
    {
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        currentQuestionIndex = 0;

        // welcome
        createWelcomePanel();

        // username page
        createUsernamePanel();

        // select question type
        createQuestionSetPanel();

        // quiz
        createGamePanel();

        add(cardPanel);

        // Adds all the button handlers
        startButton.addActionListener(handler);
        usernameButton.addActionListener(handler);
        playButton.addActionListener(handler);
        nextButton.addActionListener(handler);
    }

    // First Unique Panel Welcomes User
    private void createWelcomePanel()
    {
        // Creates the Panel
        JPanel welcomePanel = new JPanel(new BorderLayout());

//        ImageIcon welcomeIcon = new ImageIcon("Mini Project/src/<IMAGE GOES HERE>");
//        welcomePanel.add(new JLabel(welcomeIcon), BorderLayout.CENTER);

        // Adds the title
        JLabel title = new JLabel("Welcome Screen", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        welcomePanel.add(title, BorderLayout.NORTH);

        // Adds the button to the panel adds it to the panel stack
        startButton = new JButton("Start game!");
        welcomePanel.add(startButton, BorderLayout.SOUTH);
        cardPanel.add(welcomePanel, "W");
    }

    // Second Unique Panel Allow User to add their Name and start game
    private void createUsernamePanel()
    {
        usernamePanel = new JPanel(new BorderLayout());
        JPanel usernameButtonPanel = new JPanel(new FlowLayout());

        //create input field
        inputField = new JTextField("Enter a username");
        inputField.setFont(new Font("Arial", Font.PLAIN, 20));

        //create username button
        usernameButton = new JButton("set username");

        //add the input field and the username button to the username button panel
        usernameButtonPanel.add(inputField);
        usernameButtonPanel.add(usernameButton);

        //add this nested username button panel to the main username panel
        usernamePanel.add(usernameButtonPanel, BorderLayout.NORTH);

        // create a nested play button panel
        JPanel playButtonPanel = new JPanel(new FlowLayout());
        playButton = new JButton("Pick your category");
        playButtonPanel.add(playButton);
        usernamePanel.add(playButtonPanel, BorderLayout.SOUTH);

        //add this nested play button panel to the main username panel
        cardPanel.add(usernamePanel, "U");
    }

    // Third Unique Panel Selecting the question type
    private void createQuestionSetPanel()
    {
        JPanel questionSetPanel = new JPanel(new BorderLayout());

        questionSetLabel = new JLabel("Select your question set",SwingConstants.CENTER);
        questionSetLabel.setFont(new Font("Arial", Font.BOLD,16));
        questionSetPanel.add(questionSetLabel,BorderLayout.NORTH);

        // Putting in the Question Set Option Buttons
        JPanel nestedButtonPanel = new JPanel(new GridLayout(2,2,10,10));
        // Hard Coding the different buttons
        questionSetButtons[0] = new JButton("Lord of the Rings/Hobbit");
        questionSetButtons[0].addActionListener(new QuestionButtonHandler(0));
        nestedButtonPanel.add(questionSetButtons[0]);

        questionSetButtons[1] = new JButton("Star Wars");
        questionSetButtons[1].addActionListener(new QuestionButtonHandler(1));
        nestedButtonPanel.add(questionSetButtons[1]);

        questionSetButtons[2] = new JButton("Marvel");
        questionSetButtons[2].addActionListener(new QuestionButtonHandler(2));
        nestedButtonPanel.add(questionSetButtons[2]);

        questionSetButtons[3] = new JButton("Harry Potter");
        questionSetButtons[3].addActionListener(new QuestionButtonHandler(3));
        nestedButtonPanel.add(questionSetButtons[3]);

        // Putting all the buttons on the panel
        questionSetPanel.add(nestedButtonPanel, BorderLayout.CENTER);

        // create a nested play button panel
        JPanel playButtonPanel = new JPanel(new FlowLayout());
        playButton = new JButton("Play!");
        playButtonPanel.add(playButton);
        usernamePanel.add(playButtonPanel, BorderLayout.SOUTH);

        cardPanel.add(questionSetPanel, "Q");
    }
    // Fourth Unique Panel The Game
    private void createGamePanel()
    {
        JPanel gamePanel = new JPanel(new BorderLayout());

        // PlaceHolder to be filled in later
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

    // Fifth Unique Panel Results/Score Screen
    private void createResultsPanel()
    {
        // Adds the Result title to the Panel
        JPanel resultsPanel = new JPanel(new BorderLayout());
        JLabel resultsLabel = new JLabel("Results", SwingConstants.CENTER);
        resultsLabel.setFont(new Font("Arial", Font.BOLD,24));
        resultsPanel.add(resultsLabel, BorderLayout.NORTH);

        // Adds the Username and their score
        JPanel resultsUserNamePanel = new JPanel(new BorderLayout());
        JLabel resultsUserNameLabel = new JLabel(username + ": " + score + "/10", SwingConstants.CENTER);
        resultsUserNameLabel.setFont(new Font("Arial", Font.BOLD,16));
        resultsPanel.add(resultsUserNameLabel, BorderLayout.CENTER);

        // Adds the Result Image
//        ImageIcon resultsIcon = new ImageIcon("Mini Project/src/<IMAGE GOES HERE>");
//        resultsPanel.add(new JLabel(resultsIcon), BorderLayout.NORTH);

        // Adds in the Play again button in a smaller style on the bottom
        JPanel againButtonPanel = new JPanel(new FlowLayout());
        againButton = new JButton("Again");
        againButtonPanel.add(againButton);
        resultsPanel.add(againButtonPanel, BorderLayout.SOUTH);

        againButton.addActionListener(handler);
        cardPanel.add(resultsPanel, "R");
    }

    // Reads text files and imports the questions and answers
    public void importQuestionsFromFile(String questionSetFilePath, String answerSetFilePath)
    {
        questionGroups = new ArrayList<>();
        correctAnswers = new ArrayList<>();
        questionTextString = new ArrayList<>();

        // Used for catching incorrect files
        try
        {
            // Used to import the file for our questions
            FileReader fileReaderQuestionSet1 = new FileReader(questionSetFilePath); // File location needs to be from src parent directory
            FileReader fileReaderAnswerSet1 = new FileReader(answerSetFilePath);

            // Reads the imported file and adds it to the buffer
            BufferedReader bufferedReaderQuestionSet = new BufferedReader(fileReaderQuestionSet1);
            BufferedReader bufferedReaderAnswerSet = new BufferedReader(fileReaderAnswerSet1);

            // Counter used for indexing the file
            int questionSet1 = 0;

            String line;
            // Reading the text file
            while (true)
            {
                line = bufferedReaderQuestionSet.readLine();
                // Catching if the there is no more text
                if (line == null)
                {
                    break;
                }
                // Save the line to an Array List or other Data Structure
                questionSet1++;
                // Testing
                System.out.println(questionSet1 + ": " + line);
                questionTextString.add(line);
            }

            // Taking the info from the text file and adding it to the groups
            for(int i = 0; i <= (questionTextString.size() - 5); i = i + 5)
            {
                // Taking the data and adding into the array
                questionGroups.add(List.of(
                        questionTextString.get(i),
                        questionTextString.get(i+1),
                        questionTextString.get(i+2),
                        questionTextString.get(i+3),
                        questionTextString.get(i+4)
                ));
            }
            // Putting in the correct answers
            for(int i = 0; i < 10;i++)
            {
                line = bufferedReaderAnswerSet.readLine();
                correctAnswers.add(Integer.parseInt(line));
                // Testing
                System.out.println(questionGroups.get(i));
                System.out.println(correctAnswers.get(i));
            }
        }
        catch (FileNotFoundException e) // File not found is a subclass of IOException
        {
            System.out.println("FileNotFoundException happened.");
            e.printStackTrace();
        }
        catch (IOException e) // For if there is no lines in the file
        {
            System.out.println("IOException happened.");
        }
    }

    // Handles the Questions for the game
    private void loadNextQuestion()
    {
        if (questionGroups == null || questionGroups.isEmpty() || currentQuestionIndex >= questionGroups.size())
        {
            createResultsPanel();
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

    // Handles All other Buttons
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
                cardLayout.show(cardPanel, "Q");
            }
            else if (e.getSource() == questionsButton)
            {
                // show quiz game
                cardLayout.show(cardPanel, "G");
            }
            else if (e.getSource() == nextButton)
            {
                createResultsPanel();
                cardLayout.show(cardPanel, "R"); // show results
            }
            else if(e.getSource() == againButton)
            {
                // Restart the game back to the welcome screen and set the score to 0
                currentQuestionIndex = 0;
                score = 0;
                cardLayout.show(cardPanel, "W");
            }
        }
    }

    // Handles All buttons for the Game
    private class OptionButtonHandler implements ActionListener
    {
        private final int index;
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
                optionButtons[index].setBackground(Color.GREEN);
                // User gets a point
                score++;
                JOptionPane.showMessageDialog(null,"Correct! Current Score " + score + "/10");
                optionButtons[index].setBackground(null);
            }
            else
            {
                optionButtons[index].setBackground(Color.RED);
                // No point
                JOptionPane.showMessageDialog(null,"Incorrect! Current Score" + score + "/10");
                optionButtons[index].setBackground(null);
            }
            //Increment the question index
            currentQuestionIndex++;
            // Go to the next question
            loadNextQuestion();
        }
    }

    // Handles the buttons for selecting which question set to use
    private class QuestionButtonHandler implements ActionListener
    {
        private final int index;
        public QuestionButtonHandler(int index)
        {
            this.index = index;
        }
        @Override
        public void actionPerformed(ActionEvent e)
        {
            switch (index)
            {
                case 0:
                    importQuestionsFromFile("Mini Project/src/questionSet1.txt",
                                             "Mini Project/src/answerSet1.txt");
                    loadNextQuestion();
                    cardLayout.show(cardPanel, "G");
                    break;
                case 1:
                    importQuestionsFromFile("Mini Project/src/questionSet2.txt",
                                             "Mini Project/src/answerSet2.txt");
                    loadNextQuestion();
                    cardLayout.show(cardPanel, "G");
                    break;
                case 2:
                    importQuestionsFromFile("Mini Project/src/questionSet3.txt",
                                             "Mini Project/src/answerSet3.txt");
                    loadNextQuestion();
                    cardLayout.show(cardPanel, "G");
                    break;
                case 3:
                    importQuestionsFromFile("Mini Project/src/questionSet4.txt",
                                             "Mini Project/src/answerSet4.txt");
                    loadNextQuestion();
                    cardLayout.show(cardPanel, "G");
                    break;
                default:
                    break;
            }
        }
    }
}