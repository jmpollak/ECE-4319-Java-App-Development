package Given_Code;

import java.io.Serializable;

public class Question implements Serializable
{
    private String question;
    private String[] options;
    private int correctIndex;

    public Question(String question, String[] options, int correctIndex)
    {
        this.question = question;
        this.options = options;
        this.correctIndex = correctIndex;
    }

    public String getQuestion()
    {
        return question;
    }

    public String[] getOptions()
    {
        return options;
    }

    public int getCorrectIndex()
    {
        return correctIndex;
    }
}

