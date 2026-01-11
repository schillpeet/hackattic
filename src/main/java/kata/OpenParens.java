package kata;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Scanner;

public class OpenParens {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            String line = scanner.nextLine();
            String[] strA = line.split("");
            Deque<String> stack = new ArrayDeque<>();

            for (String s : strA) {
                if (stack.isEmpty() || stack.peek().equals(s)) stack.push(s);
                else stack.pop();
            }

            if (stack.isEmpty()) System.out.println("yes");
            else System.out.println("no");
        }
    }
}
