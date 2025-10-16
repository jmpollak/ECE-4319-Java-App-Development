import javax.swing.JFrame;

public class Main
{
    public static void main(String[] args)
    {
        CardLayoutFrame frame = new CardLayoutFrame();
        frame.setSize(800,600);
        frame.setTitle("Trivia Game by John");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null); // Set the location of this frame by some component. null means center
        frame.setVisible(true);

        // Testing import file
        frame.importQuestionsFromFile();
    }
}
