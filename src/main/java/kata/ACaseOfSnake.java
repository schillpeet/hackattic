package kata;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


/**
 * Sample input
 * szWindowContents
 * iAirflowParameter
 * fMixtureRatio
 * mixCoeff
 * i32Mix2Mass
 *
 * Sample output
 * window_contents
 * airflow_parameter
 * mixture_ratio
 */
public class ACaseOfSnake {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (scanner.hasNext()) {
            String input = scanner.nextLine();
            if ("EXIT".equals(input)) break;

            List<String> inputList = new ArrayList<>(List.of(input.split("(?=[A-Z])")));
            if ((inputList.get(0).length() == 3 && !inputList.get(0).matches("[a-zA-Z]+")) || (inputList.get(0).length() < 3)) {
                inputList.remove(0);
            }
            String result = String.join("_", inputList).toLowerCase();
            if (!result.isEmpty()) System.out.println(result);
        }
        scanner.close();
    }
}





















