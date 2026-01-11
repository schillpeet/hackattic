package kata;

import java.util.*;

public class SortingJSONLines {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        List<Map.Entry<String, Integer>> results = new ArrayList<>();
        while (input.hasNext()) {
            String line = input.nextLine().substring(2);
            int idxQuote = line.indexOf('"');
            String name = line.substring(0, idxQuote);
            if (line.contains("extra")) line = line.substring(line.indexOf("extra"));
            int balance = Integer.parseInt(line.replaceAll(".*\"balance\"\\s*:\\s*(\\d+).*", "$1").trim());
            results.add(Map.entry(name, balance));
        }
        results.sort(Map.Entry.comparingByValue());
        for(Map.Entry<String, Integer> result : results) {
            System.out.println(result.getKey() + ": " + String.format("%,d", result.getValue()));
        }
        input.close();
    }
}
