package PrimeNumberCounter;

public class StringErrorCheck
{
    public boolean ErrorCheck(String input)
    {
        return input.chars().allMatch( Character::isDigit );
    }
}
