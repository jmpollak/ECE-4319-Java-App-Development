public class Main {

    public static int countPrimes(int n) {
        int count = 0;

        for (int i = 2; i < n; i++) {
            int j = 2;
            for(j = 2; j < i; j++) {
                if(i%j==0) {
                    break;
                }
            }
            if (j == i-1) {
                count++;
                System.out.println(i);
            }
        }
        return count;
    }

    public static int countPrime(int n) {
        int count = 0;
        boolean[] notPrime = new boolean[n];

        for (int i = 2; i < n; i++) {
            if (!notPrime[i]) {
                count++;
                for (int j = 2; i*j < n; j++) {
                    notPrime[i*j] = true;
                }
            }
        }
        return count;
    }

    public static void CelsiusToFahrenheit(double Celsius) {
            double tempC = 25.5; // Given to us;
            double Fahrenheit = (Celsius * 9/5) + 32;
            System.out.println("The current temperature in C is " + Celsius + "°C = " + Fahrenheit + "°F");
    }


    public static void main(String[] args) {
        /*  Showing the difference between the print line and print statement.
         *  println creates a new line after the text
         */
        System.out.println("Exercise 1:");
        System.out.println("Hello and welcome!");
        System.out.print("Hello and welcome!"); // There will not be any new line so the next set of text will be
        System.out.print("Hello and welcome!");
        // Using the primitive types
        //int num; //Does not need to initialized to compile but should be

        //Using the other types
        String s = "something";
        s = s + "something else";
        System.out.println(s);

        String string = "10";
        Integer num = Integer.getInteger(string);
        System.out.print("This is the String: ");
        System.out.println(string);
        System.out.print("This is the Integer: ");
        System.out.println(num);

        // Character c;

        /*Exercise 2
        class Welcome2{
            public static void main(String[] arg) {
                System.out.println("something");
                String courseCode = "4319";
                System.out.println(courseCode);
                courseCode = "ERROR";
                System.out.println(courseCode);
            }
        }
        */
        /*
        System.out.println("\n\nExercise 2:");
        System.out.println("something");
        String courseCode = "4319";
        System.out.println(courseCode);
        courseCode = "ERROR";
        System.out.println(courseCode);

        // Exercise 3 & 4
        System.out.println("\n\nExercise 3 & 4:");
        double score = 1.0 + 2.0 * 3.0;
        System.out.println(score);
        double copy = score / 2.0;
        System.out.println(copy);
        System.out.println(score);
        score = score /2.0;
        System.out.print("Score modified: ");
        System.out.println(score);
        */

        //Quiz 1
        double tempC = 25.5; // Given to us;
        double tempF = (tempC * 9/5) + 32;
        System.out.println("The current temperature in C is " + tempC);
        System.out.println("The current temperature in F is " + tempF);

        //Quiz 1 using classes
        double temp = 25.5;


    }
}



/*
    There needs to be a main in order for the program to run.
    The program structure will be as follows:
    class CLASSNAME{
        public static void main(String[] arguments){
            // main method called by the JVM to initiate
            // program execution
        }
    }
 */

