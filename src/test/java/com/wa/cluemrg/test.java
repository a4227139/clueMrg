package com.wa.cluemrg;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;

public class test {
    String A[]={"java"};
    char[] a = {'A','B'};
    static Calendar calendar = Calendar.getInstance();
    static int year = calendar.get(Calendar.YEAR);
    static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static void main(String[] args) {
        /*int max=0;
        int arr[]={1,2,3};
        for(int i : arr)
            max=i;*/
        String bsId="77E7";
        System.out.println(bsId.split("\\|")[0]);
        System.out.println(bsId.matches("[a-fA-F]"));
        String[] dateStrs = {"02-24 10:20:33", "02/04 05:51:58", "2023/1/1 9:52:18", "2023-04-18 15:41:18"};
        for (String date:dateStrs){
            System.out.println(TurnToUniDateFormat(date));
        }
    }

    private static String TurnToUniDateFormat(String dataString) {
        String[] dateArray = dataString.split("\\/|-|:|\\s+");
        StringBuilder dateBuilder = new StringBuilder();
        if (dateArray.length==5){
            dateBuilder.append(year).append("-").append(fillZero(dateArray[0])).append("-").append(fillZero(dateArray[1])).append(" ")
                    .append(fillZero(dateArray[2])).append(":").append(fillZero(dateArray[3])).append(":").append(fillZero(dateArray[4]));
        }else if (dateArray.length==6){
            dateBuilder.append(fillZero(dateArray[0])).append("-").append(fillZero(dateArray[1])).append("-").append(fillZero(dateArray[2])).append(" ")
                    .append(fillZero(dateArray[3])).append(":").append(fillZero(dateArray[4])).append(":").append(fillZero(dateArray[5]));
        }else {
            return "";
        }
        return dateBuilder.toString();
    }

    private static String fillZero(String s){
        if (s.length()<2){
            return 0+s;
        }
        return s;
    }
}
