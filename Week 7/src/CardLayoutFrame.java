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


    public CardLayoutFrame() {
        username = "default name";
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

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
                cardLayout.show(cardPanel, "G"); // show quiz game
            } else if (e.getSource() == nextButton) {
                createResultsPanel();
                cardLayout.show(cardPanel, "R"); // show results
            }
        }
    }
}

