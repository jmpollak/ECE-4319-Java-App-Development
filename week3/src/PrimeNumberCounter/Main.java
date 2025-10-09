package PrimeNumberCounter;

import javax.swing.*;
import java.awt.*;

// Lesson on user input and creating our own class
public class Main
{
    public static void main(String[] args)
    {
        // Instance the user created class
        CountPrimeNumbers countPrimeNumbers = new CountPrimeNumbers();

//        //Create Scanner to obtain input from command window
//        Scanner input = new Scanner(System.in);
//        // Prompt the user for input
//        System.out.print("Please enter an input n of the Count Prime problem. n = ");
//        //Read a line of test
//        String scannedInput = input.nextLine();
//        // Passes the String from the user and is parsed into an integer
//
//        int num = Integer.parseInt(scannedInput);
//
//
//        System.out.println("Solution 1 - total count: " + countPrimeNumbers.countPrimes(num));
//        System.out.println("Solution 2 - total count: " + countPrimeNumbers.countPrimesWithArray(num));

        // Learning GUI with Swing
        //JOptionPane.showMessageDialog(null, "Welcome to Java", "Welcome Message", JOptionPane.INFORMATION_MESSAGE);

        // Doing the prime number problem with GUI
        JOptionPane.showMessageDialog(null, "Given an Integer", "Count Prime Number", JOptionPane.INFORMATION_MESSAGE);
        String dialogInput = JOptionPane.showInputDialog(null, "Please enter a number of the Count Prime problem: n = ", "User Input",  JOptionPane.WARNING_MESSAGE);

        int n = Integer.parseInt(dialogInput);

        // Using concatenation to display the
        //JOptionPane.showMessageDialog(null, "The answer is: " + countPrimeNumbers.countPrimesWithArray(n), "Output", JOptionPane.INFORMATION_MESSAGE);

        // Using String formating
        String message = String.format("There are %d numbers from 0 to %d", countPrimeNumbers.countPrimes(n), n);
        JOptionPane.showMessageDialog(null, message, "Output", JOptionPane.PLAIN_MESSAGE);

    }
}
