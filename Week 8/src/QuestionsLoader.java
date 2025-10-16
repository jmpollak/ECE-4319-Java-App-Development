import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class QuestionsLoader {
    public void readFile() {
        try {
            FileReader fileReader = new FileReader("src/questions.txt");
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            int readCount = 0;
            String line = "";
            while (true) {
                line = bufferedReader.readLine();
                if (line == null) {
                    break;
                }
                readCount++;
                System.out.println(readCount + ": " + line);
            }
        } catch (FileNotFoundException e) {
            System.out.println("FileNotFoundException happened.");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("IOException happened.");

        }

    }
}
