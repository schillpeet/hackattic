package kata;

import java.util.Base64;
import java.util.Scanner;

/**
 * Sample input                     Sample output
 *
 * bGF0ZS1hdC1uaWdodA==             late-at-night
 * d2l0aC10aGUtcmlzaW5nLWFwZQ==     with-the-rising-ape
 * dGhlLXJ1dGhsZXNzLXNldmVu         the-ruthless-seven
 *
 * Run the program and enter the sample input in the console
 * CTRL+C will stop the program or just Type EXIT
 */
class Debasing64 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Base64.Decoder decoder = Base64.getDecoder();
        while (scanner.hasNext()) {
            String input = scanner.nextLine();
            if ("EXIT".equals(input)) break;
            String result = new String (decoder.decode(input));
            System.out.println(result);
        }
        scanner.close();
    }
};
