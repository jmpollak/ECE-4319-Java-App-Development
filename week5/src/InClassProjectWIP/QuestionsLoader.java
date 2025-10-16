package InClassProjectWIP;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class QuestionsLoader
{
    private List<List<String>> questionGroups;
    private  ArrayList<Integer> correctAnswers;
    private List<String> txt;

    public void readFile()
    {
        questionGroups = new ArrayList<>();
        correctAnswers = new ArrayList<>();
        txt = new ArrayList<>();

        // Used for catching incorrect files
        try
        {
            // Used to import the file for our questions
            FileReader fileReader = new FileReader("week5/src/InClassProjectWIP/questions.txt"); // File location needs to be from src parent directory
            // Reads the imported file and adds it to the buffer
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            int readCount = 0; // Counter used for indexing the file

            //Reading the text file

            String line = "";
            while (true)
            {
                line = bufferedReader.readLine();
                // Catching if the there is no more text
                if (line == null)
                {
                    break;
                }
                // Save the line to an Array List or other Data Structure
                readCount++;
                System.out.println(readCount + ": " + line);
                txt.add(line);
            }
            // Taking the info from the text file and adding it to the groups
            for(int i =0; i <= (txt.size() - 5); i++)
            {
                // Taking the data and adding into the array
                questionGroups.add(List.of(
                        txt.get(i),
                        txt.get(i+1),
                        txt.get(i+2),
                        txt.get(i+3),
                        txt.get(i+4)
                ));
            }
            // Adding in all the correct answers MANUALLY
            correctAnswers.add(2);
            correctAnswers.add(3);
            correctAnswers.add(0);
            correctAnswers.add(2);
            correctAnswers.add(1);
            // Want to change to have an answer sheet

            // Displays the answer after the question
            for(int j = 0; j < readCount; j++)
            {
                System.out.println(txt.get(j));
                if(j%5 == 0) {
                    System.out.println(correctAnswers.get(j / 5));
                }
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
}
