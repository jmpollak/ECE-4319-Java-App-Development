package Given_Code;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static Given_Code.Server.questions;

public class ServerFrame extends JFrame
{
    private JButton nextButton;

    private CardLayout cardLayout;
    private JPanel cardPanel;

    private String username;
    private JLabel questionLabel;
    private JButton[] optionButtons = new JButton[4];
    private int currentQuestionIdx;


    public ServerFrame()
    {
        username = "Player 1";
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        currentQuestionIdx = 0;

        createGamePanel();

        add(cardPanel);

        ButtonHandler handler = new ButtonHandler();
        nextButton.addActionListener(handler);
        loadNextQuestion();

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
        questionLabel = new JLabel("Place holder for questions", SwingConstants.CENTER);
        questionLabel.setFont(new Font("Arial", Font.BOLD, 16));
        gamePanel.add(questionLabel, BorderLayout.NORTH);

        nextButton = new JButton("Go to results");
        gamePanel.add(nextButton, BorderLayout.SOUTH);
        JPanel nestedButtonPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        for (int i = 0; i < 4; i++)
        {
            optionButtons[i] = new JButton("Option" + (i + 1));
            optionButtons[i].setBackground(Color.GREEN);
            nestedButtonPanel.add(optionButtons[i]);
        }

        gamePanel.add(nestedButtonPanel, BorderLayout.CENTER);

        /* remember to add this username panel to the cardPanel object, and give it a name */
        cardPanel.add(gamePanel, "G");
    }

    private class ButtonHandler implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            if (e.getSource() == nextButton)
            {
                createResultsPanel();
                cardLayout.show(cardPanel, "R"); // show results
            }
        }
    }

    private void loadNextQuestion()
    {
        if (questions == null || currentQuestionIdx >= questions.size())
        {
            createResultsPanel();
            cardLayout.show(cardPanel, "R"); // show results
            return;
        }

        questionLabel.setText(questions.get(currentQuestionIdx).getQuestion());

        for (int i = 0; i < 4; i++)
        {
            optionButtons[i].setText(questions.get(currentQuestionIdx).getOptions()[i]);
        }
    }

    public boolean validateAnswers(Question q, String answer)
    {
        if (answer != null && Integer.parseInt(answer) - 1 == q.getCorrectIndex())
        {
            JOptionPane.showMessageDialog(null, "Correct!");

            // go to the next question
            currentQuestionIdx++;
            loadNextQuestion();
            return true;
        }
        else
        {
            JOptionPane.showMessageDialog(null, "that was incorrect.");

            // go to the next question
            currentQuestionIdx++;
            loadNextQuestion();
            return false;
        }
    }

}

