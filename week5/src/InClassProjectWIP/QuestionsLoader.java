package InClassProjectWIP;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;


public class QuestionsLoader
{
    public void readFile()
    {
        // Used for catching incorrect files
        try
        {
            // Used to import the file for our questions
            FileReader fileReader = new FileReader("week5/src/InClassProjectWIP/questions.txt"); // File location needs to be from src parent directory
            // Reads the imported file and adds it to the buffer
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            int readCount = 0; // Counter used for

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
