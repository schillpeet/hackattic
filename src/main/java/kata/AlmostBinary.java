package kata;

import java.util.Scanner;

public class AlmostBinary {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        while (sc.hasNextLine()) {
            String bin = sc.nextLine().replaceAll("#", "1"). replaceAll("\\.", "0");
            int binToInt = Integer.parseInt(bin, 2);
            System.out.println(binToInt);
        }
    }
}
