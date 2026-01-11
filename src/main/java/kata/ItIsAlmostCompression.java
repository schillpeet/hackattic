package kata;

import java.util.*;

class ItIsAlmostCompression {
    public static void main(String args[]) {
        Scanner input  = new Scanner(System.in);
        while (input.hasNextLine()) {
            String line = input.nextLine();
            int i = 0;
            StringBuilder result = new StringBuilder();
            String last = "";
            int counter = 0;
            while (i < line.length()) {
                String cur = String.valueOf(line.charAt(i));
                if (last.isEmpty()) {
                    last = cur;
                    counter++;
                } else if (cur.equals(last)) {
                    counter++;
                } else  {
                    if (counter > 2) result.append(counter).append(last);
                    else if (counter == 1) result.append(last);
                    else if (counter == 2) result.append(last).append(last);
                    counter = 1;
                    last = cur;
                }
                if (i+1 == line.length()) {
                    if (counter > 2) result.append(counter).append(last);
                    else if (counter == 1) result.append(last);
                    else if (counter == 2) result.append(last).append(last);
                }
                i++;
            }
            System.out.println(result);
        }
        input.close();
    }
};