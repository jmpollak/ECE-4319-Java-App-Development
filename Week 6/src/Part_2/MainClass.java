package Part_2;
import javax.swing.JFrame;

public class MainClass {
    public static void main(String[] args) {
        CardLayoutFrame frame = new CardLayoutFrame();
        frame.setSize(800, 600);
        frame.setTitle("Trivia Game_by Bryan");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}