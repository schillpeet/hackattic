package kata;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.Scanner;

public class WhatDayWasIt {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        LocalDate refDate = LocalDate.parse("1970-01-01");
        while (input.hasNext()) {
            int inputDays = Integer.parseInt(input.nextLine());
            System.out.println(refDate.plusDays(inputDays).getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH));
        }
    }
}
