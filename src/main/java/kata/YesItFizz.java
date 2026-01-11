package kata;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class YesItFizz {
    public static void main(String[] args) throws IOException {
        String line = new BufferedReader(new InputStreamReader(System.in)).readLine();
        int start = Integer.parseInt(line.split(" ")[0]);
        int end = Integer.parseInt(line.split(" ")[1]);
        for (int i = start; i <= end; i++) {
            boolean multiOfThree = i % 3 == 0;
            boolean multiOfFive = i % 5 == 0;
            if (multiOfThree && multiOfFive) System.out.println("FizzBuzz");
            else if (multiOfThree) System.out.println("Fizz");
            else if (multiOfFive) System.out.println("Buzz");
            else System.out.println(i);
        }
    }
}
