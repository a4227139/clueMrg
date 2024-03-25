package com.wa.cluemrg.util;

import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DateUtil {
    public static void main(String[] args) {
        /*String dateStartString = "2023-09-01";
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
        }*/
        Date date = new Date(); // 当前日期
        String time1 = "21:49:51"; // 时间字符串1
        String time2 = "214951";   // 时间字符串2

        // 添加时间到日期中
        Date resultDate1 = addTimeToDate(date, time1);
        Date resultDate2 = addTimeToDate(date, time2);

        // 输出结果
        System.out.println("Result 1: " + resultDate1);
        System.out.println("Result 2: " + resultDate2);
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

    public static Date formatDateToStart(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        // Set the time components (hour, minute, second) to zero
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
    }

    public static Date formatDateToEnd(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);

        return calendar.getTime();
    }

    public static boolean isDateOnly(Date date) {
        if (date==null){
            return false;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.HOUR_OF_DAY) == 0 &&
                calendar.get(Calendar.MINUTE) == 0 &&
                calendar.get(Calendar.SECOND) == 0 &&
                calendar.get(Calendar.MILLISECOND) == 0;
    }


    /**
     * 只支持HH:mm:ss或HHmmss
     * @param date
     * @param time
     * @return
     */
    public static Date addTimeToDate(Date date, String time) {
        if (time==null){
            return date;
        }
        SimpleDateFormat hhmmssSdf = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat hhmmssSdf2 = new SimpleDateFormat("HHmmss");
        Date timeDate = null;
        try {
            timeDate = hhmmssSdf.parse(time);
        } catch (ParseException e) {
            try {
                timeDate = hhmmssSdf2.parse(time);
            } catch (ParseException parseException) {
                return date;
            }
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        Calendar timeCalendar = Calendar.getInstance();
        timeCalendar.setTime(timeDate);

        calendar.set(Calendar.HOUR_OF_DAY, timeCalendar.get(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE, timeCalendar.get(Calendar.MINUTE));
        calendar.set(Calendar.SECOND, timeCalendar.get(Calendar.SECOND));
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
    }
}

