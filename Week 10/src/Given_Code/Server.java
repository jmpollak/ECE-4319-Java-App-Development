//package Given_Code;
//
//import javax.swing.JFrame;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//
//public class Server
//{
//    public static List<Question> questions = new ArrayList<>();
//
//    public static void main(String[] args) throws IOException
//    {
//        ServerFrame frame = new ServerFrame();
//        frame.setSize(800, 600);
//        frame.setTitle("Trivia Game_by Bryan");
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.setLocationRelativeTo(null);
//        frame.setVisible(true);
//    }
//
//    private static void loadSampleQuestions()
//    {
//        questions.add(new Question("What is the capital of France?",
//                new String[]{"Paris", "London", "Berlin", "Rome"}, 0));
//        questions.add(new Question("Which planet is known as the Red Planet?",
//                new String[]{"Earth", "Venus", "Mars", "Jupiter"}, 2));
//    }
//
//}
//
