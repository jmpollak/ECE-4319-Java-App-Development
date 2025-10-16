package Part_1;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.CardLayout;

public class CardLayoutFrame extends JFrame {
    public CardLayoutFrame() {
        CardLayout cardLayout = new CardLayout();
        JPanel cardPanel = new JPanel(cardLayout);

        // first page - welcome
        JPanel welcomePanel = new JPanel();
        welcomePanel.add(new JLabel("Welcome Screen"));

        JButton startButton = new JButton("Start game!");
        welcomePanel.add(startButton);


        // second page - quiz
        JPanel gamePanel = new JPanel();
        gamePanel.add(new JLabel("Quiz screen"));

        JButton nextButton = new JButton("Go to results");
        gamePanel.add(nextButton);



        //third page - results
        JPanel resultsPanel = new JPanel();
        resultsPanel.add(new JLabel("Rersults Screen"));

        cardPanel.add(welcomePanel, "W");
        cardPanel.add(gamePanel, "G");
        cardPanel.add(resultsPanel, "R");

        add(cardPanel);

    }
}
