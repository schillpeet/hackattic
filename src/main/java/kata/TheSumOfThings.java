package kata;

import java.util.Scanner;

public class TheSumOfThings {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (scanner.hasNext()) {
            String line = scanner.nextLine();
            String[] elements = line.split(" ");
            int result = 0;
            for (String el : elements) {
                if (el.length() == 1 && !el.matches("[0-9]+")) result += (int) el.charAt(0); // ascii
                else if (el.length() > 2 && el.charAt(0) == '0') {
                    if (el.charAt(1) == 'o') result += Integer.parseInt(el.substring(2), 8);  // octal
                    else if (el.charAt(1) == 'x') result += Integer.parseInt(el.substring(2), 16); // hex
                    else if (el.charAt(1) == 'b') result += Integer.parseInt(el.substring(2), 2); // bin
                } else result += Integer.parseInt(el, 10);
            }
            System.out.println(result);
        }
        scanner.close();
    }
}
