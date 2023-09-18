package com.wa.cluemrg.util;

import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class DateIntervalCalculator {
    public static void main(String[] args) {
        String dateStartString = "2023-09-01";
        String dateEndString = "2023-09-17";

        // Parse the date strings to LocalDate objects
        LocalDate dateStart = LocalDate.parse(dateStartString);
        LocalDate dateEnd = LocalDate.parse(dateEndString);

        // Calculate the number of days between the dates
        int daysBetween = (int) dateStart.until(dateEnd).getDays();

        // Print the number of days
        System.out.println("Number of days between " + dateStartString + " and " + dateEndString + ": " + daysBetween);

        // Generate and print the list of dates in the range
        List<LocalDate> dateList = getDateRange(dateStartString, dateEndString);
        System.out.println("Dates in the range:");
        for (LocalDate date : dateList) {
            System.out.println(date);
        }
    }

    public static  int daysBetween(String dateStartString,String dateEndString){

        if (StringUtils.isEmpty(dateEndString)){
            dateEndString=LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }
        if (StringUtils.isEmpty(dateStartString)){
            // Parse the given date string to a LocalDate object
            LocalDate date = LocalDate.parse(dateEndString);
            // Subtract one month
            LocalDate oneMonthAgo = date.minusMonths(1);
            // Format the result as a string (e.g., "2023-08-17")
            dateStartString = oneMonthAgo.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }

        // Parse the date strings to LocalDate objects
        LocalDate dateStart = LocalDate.parse(dateStartString);
        LocalDate dateEnd = LocalDate.parse(dateEndString);
        // Calculate the number of days between the dates
        int daysBetween = (int) dateStart.until(dateEnd).getDays();
        // Print the number of days
        System.out.println("Number of days between " + dateStartString + " and " + dateEndString + ": " + daysBetween);
        return daysBetween;
    }

    public static List<LocalDate> getDateRange(String dateStartString, String dateEndString) {
        LocalDate start = LocalDate.parse(dateStartString);
        LocalDate end = LocalDate.parse(dateEndString);
        List<LocalDate> dateList = new ArrayList<>();

        LocalDate current = start;
        while (!current.isAfter(end)) {
            dateList.add(current);
            current = current.plusDays(1);
        }

        return dateList;
    }
}

