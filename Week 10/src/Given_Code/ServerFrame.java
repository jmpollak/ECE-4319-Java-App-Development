package Given_Code;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;

public class ServerFrame extends JFrame
{
    private JButton nextButton;
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private String username;
    private JLabel questionLabel;
    private JLabel scoreLabel;
    private JButton[] optionButtons = new JButton[4];
    private int currentQuestionIdx;
    private int score;

    private ClientHandler clientHandler;
    private int pendingAnswer = -1;
    private final Object answerLock = new Object();
    private Map<String, List<Question>> questionSets;
    private String selectedCategory = null;
    private List<Question> currentQuestions;

    public ServerFrame(String playerName, Map<String, List<Question>> questionSets)
    {
        username = playerName;
        this.questionSets = questionSets;
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        currentQuestionIdx = 0;
        score = 0;

        createCategorySelectionPanel();
        createGamePanel();

        add(cardPanel);

        // Show category selection first
        cardLayout.show(cardPanel, "CATEGORY");
    }

    public void setClientHandler(ClientHandler handler)
    {
        this.clientHandler = handler;
    }

    public int getScore()
    {
        return score;
    }

    public String waitForCategorySelection()
    {
        synchronized (answerLock)
        {
            try
            {
                while (selectedCategory == null)
                {
                    answerLock.wait();
                }
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
        return selectedCategory;
    }

    public void startGame(List<Question> questions)
    {
        this.currentQuestions = questions;
        loadNextQuestion();
        cardLayout.show(cardPanel, "G");
    }

    private void createCategorySelectionPanel()
    {
        JPanel categoryPanel = new JPanel(new BorderLayout());

        JLabel titleLabel = new JLabel("Select a Category", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        categoryPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        buttonPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(50, 50, 50, 50));

        // Create a button for each category
        for (String category : questionSets.keySet())
        {
            JButton categoryButton = new JButton("<html><center>" + category + "<br>(" +
                    questionSets.get(category).size() + " questions)</center></html>");
            categoryButton.setFont(new Font("Arial", Font.BOLD, 18));
            categoryButton.setBackground(new Color(100, 150, 255));
            categoryButton.setForeground(Color.WHITE);

            categoryButton.addActionListener(new ActionListener()
            {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    synchronized (answerLock)
                    {
                        selectedCategory = category;
                        answerLock.notify();
                    }
                }
            });

            buttonPanel.add(categoryButton);
        }

        categoryPanel.add(buttonPanel, BorderLayout.CENTER);

        JLabel instructionLabel = new JLabel("Player: " + username, SwingConstants.CENTER);
        instructionLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        categoryPanel.add(instructionLabel, BorderLayout.SOUTH);

        cardPanel.add(categoryPanel, "CATEGORY");
    }

    private void createResultsPanel()
    {
        JPanel resultsPanel = new JPanel(new BorderLayout());

        JLabel resultsLabel = new JLabel("<html><center>Game Over!<br><br>" +
                "Category: " + selectedCategory + "<br><br>" +
                "Final Score: " + score + " / " + currentQuestions.size() +
                "<br><br>Player: " + username + "</center></html>",
                SwingConstants.CENTER);
        resultsLabel.setFont(new Font("Arial", Font.BOLD, 24));

        resultsPanel.add(resultsLabel, BorderLayout.CENTER);
        cardPanel.add(resultsPanel, "R");
    }

    private void createGamePanel()
    {
        JPanel gamePanel = new JPanel(new BorderLayout());

        // Top panel with question and score
        JPanel topPanel = new JPanel(new BorderLayout());
        questionLabel = new JLabel("Place holder for questions", SwingConstants.CENTER);
        questionLabel.setFont(new Font("Arial", Font.BOLD, 16));
        topPanel.add(questionLabel, BorderLayout.CENTER);

        scoreLabel = new JLabel("Score: 0", SwingConstants.RIGHT);
        scoreLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        topPanel.add(scoreLabel, BorderLayout.NORTH);

        gamePanel.add(topPanel, BorderLayout.NORTH);

        nextButton = new JButton("Go to results");
        nextButton.setEnabled(false);
        nextButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                createResultsPanel();
                cardLayout.show(cardPanel, "R");
            }
        });
        gamePanel.add(nextButton, BorderLayout.SOUTH);

        JPanel nestedButtonPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        for (int i = 0; i < 4; i++)
        {
            optionButtons[i] = new JButton("Option" + (i + 1));
            optionButtons[i].setBackground(Color.LIGHT_GRAY);
            optionButtons[i].setFont(new Font("Arial", Font.PLAIN, 14));

            final int buttonIndex = i;
            optionButtons[i].addActionListener(new ActionListener()
            {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    handleButtonClick(buttonIndex);
                }
            });

            nestedButtonPanel.add(optionButtons[i]);
        }

        gamePanel.add(nestedButtonPanel, BorderLayout.CENTER);

        cardPanel.add(gamePanel, "G");
    }

    private void loadNextQuestion()
    {
        if (currentQuestions == null || currentQuestionIdx >= currentQuestions.size())
        {
            // Game over
            createResultsPanel();
            cardLayout.show(cardPanel, "R");
            return;
        }

        questionLabel.setText("Question " + (currentQuestionIdx + 1) + ": " +
                currentQuestions.get(currentQuestionIdx).getQuestion());

        for (int i = 0; i < 4; i++)
        {
            optionButtons[i].setText(currentQuestions.get(currentQuestionIdx).getOptions()[i]);
            optionButtons[i].setBackground(Color.LIGHT_GRAY);
        }

        scoreLabel.setText("Score: " + score + " / " + currentQuestions.size());
    }

    private void handleButtonClick(int buttonIndex)
    {
        // Disable all buttons after click
        for (int i = 0; i < 4; i++)
        {
            optionButtons[i].setEnabled(false);
        }

        // Store the answer and notify waiting thread
        synchronized (answerLock)
        {
            pendingAnswer = buttonIndex + 1; // Convert to 1-4
            answerLock.notify();
        }
    }

    public int waitForAnswer()
    {
        // Enable buttons for answer
        for (int i = 0; i < 4; i++)
        {
            optionButtons[i].setEnabled(true);
        }

        // Wait for button click
        synchronized (answerLock)
        {
            try
            {
                answerLock.wait();
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }

        return pendingAnswer;
    }

    public boolean validateAnswers(Question q, String answer)
    {
        try
        {
            int selectedIndex = Integer.parseInt(answer.trim()) - 1;
            int correctIndex = q.getCorrectIndex();

            // DEBUG: Print to terminal
            System.out.println("\n==================== DEBUG [" + username + "] ====================");
            System.out.println("Question " + (currentQuestionIdx + 1) + ": " + q.getQuestion());
            System.out.println("Options:");
            for (int i = 0; i < 4; i++)
            {
                System.out.println("  " + (i + 1) + ") " + q.getOptions()[i]);
            }
            System.out.println("Player selected: " + (selectedIndex + 1) + ") " + q.getOptions()[selectedIndex]);
            System.out.println("Correct answer: " + (correctIndex + 1) + ") " + q.getOptions()[correctIndex]);
            System.out.println("Result: " + (selectedIndex == correctIndex ? "CORRECT ✓" : "INCORRECT ✗"));

            boolean isCorrect = selectedIndex == correctIndex;

            if (isCorrect)
            {
                score++;
                optionButtons[selectedIndex].setBackground(Color.GREEN);
            }
            else
            {
                optionButtons[selectedIndex].setBackground(Color.RED);
                optionButtons[correctIndex].setBackground(Color.GREEN);
            }

            System.out.println("Current Score: " + score + " / " + (currentQuestionIdx + 1));
            System.out.println("=============================================================\n");

            // Move to next question
            currentQuestionIdx++;

            // Small delay for visual feedback
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            loadNextQuestion();

            return isCorrect;
        }
        catch (NumberFormatException e)
        {
            System.out.println("[" + username + "] Invalid input received: " + answer);
            return false;
        }
    }
}