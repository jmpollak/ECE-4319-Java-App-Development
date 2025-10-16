//import java.io.BufferedReader;
//import java.io.FileNotFoundException;
//import java.io.FileReader;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//
//
//public class QuestionsLoader
//{
//    private List<List<String>> questionGroups;
//    private  ArrayList<Integer> correctAnswers;
//    private List<String> txt;
//
//    public void readFile()
//    {
//        questionGroups = new ArrayList<>();
//        correctAnswers = new ArrayList<>();
//        txt = new ArrayList<>();
//
//        // Used for catching incorrect files
//        try
//        {
//            // Used to import the file for our questions
//            FileReader fileReaderQuestionSet1 = new FileReader("Project1/src/questionSet1.txt"); // File location needs to be from src parent directory
//            FileReader fileReaderAnswerSet1 = new FileReader("Project1/src/answersSet1.txt");
//
//            // Reads the imported file and adds it to the buffer
//            BufferedReader bufferedReaderQuestionSet1 = new BufferedReader(fileReaderQuestionSet1);
//            BufferedReader bufferedReaderAnswerSet1 = new BufferedReader(fileReaderAnswerSet1);
//
//            // Counter used for indexing the file
//            int questionSet1 = 0;
//
//            String line = "";
//            // Reading the text file
//            while (true)
//            {
//                line = bufferedReaderQuestionSet1.readLine();
//                // Catching if the there is no more text
//                if (line == null)
//                {
//                    break;
//                }
//                // Save the line to an Array List or other Data Structure
//                questionSet1++;
//                System.out.println(questionSet1 + ": " + line);
//                txt.add(line);
//            }
//            // Taking the info from the text file and adding it to the groups
//            for(int i =0; i <= (txt.size() - 5); i++)
//            {
//                // Taking the data and adding into the array
//                questionGroups.add(List.of(
//                        txt.get(i),
//                        txt.get(i+1),
//                        txt.get(i+2),
//                        txt.get(i+3),
//                        txt.get(i+4)
//                ));
//            }
//            // Putting in the correct answers
//            for(int i = 0; i < 10;i++)
//            {
//                line = bufferedReaderAnswerSet1.readLine();
//                correctAnswers.add(Integer.parseInt(line));
//            }
//
//            // FOR TESTING: Displays the answer after the question
//            for(int j = 0; j < questionSet1; j++)
//            {
//                System.out.println(txt.get(j));
//                if(j%5 == 0) {
//                    System.out.println(correctAnswers.get(j / 5));
//                }
//            }
//        }
//        catch (FileNotFoundException e) // File not found is a subclass of IOException
//        {
//            System.out.println("FileNotFoundException happened.");
//            e.printStackTrace();
//        }
//        catch (IOException e) // For if there is no lines in the file
//        {
//            System.out.println("IOException happened.");
//        }
//    }
//}
