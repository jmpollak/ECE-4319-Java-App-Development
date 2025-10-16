package Prime_Number_Counter;

public class StringErrorCheck
{
    public boolean ErrorCheck(String input)
    {
        return input.chars().allMatch( Character::isDigit );
    }
}
