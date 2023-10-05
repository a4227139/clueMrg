package com.wa.cluemrg.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class IDCardValidator {

    public static boolean isValidIDCard(String idCardNumber) {
        // Check length
        if (idCardNumber == null || idCardNumber.length() != 18) {
            return false;
        }

        // Check if all characters except the last one are digits
        for (int i = 0; i < 17; i++) {
            char c = idCardNumber.charAt(i);
            if (!Character.isDigit(c)) {
                return false;
            }
        }

        // Check the last character (should be a digit or 'X')
        char lastChar = idCardNumber.charAt(17);
        if (!Character.isDigit(lastChar) && lastChar != 'X') {
            return false;
        }

        // Check the format of the first 17 characters (YYYYMMDD)
        String birthdateString = idCardNumber.substring(6, 14);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        dateFormat.setLenient(false); // Strict date parsing
        try {
            Date birthdate = dateFormat.parse(birthdateString);
            Calendar cal = Calendar.getInstance();
            cal.setLenient(false); // Strict date validation
            cal.setTime(birthdate);

            // Validate the birthdate
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH) + 1; // Month is 0-based
            int day = cal.get(Calendar.DAY_OF_MONTH);

            if (!(year >= 1900 && year <= cal.get(Calendar.YEAR)) ||
                    !(month >= 1 && month <= 12) ||
                    !(day >= 1 && day <= cal.getActualMaximum(Calendar.DAY_OF_MONTH))) {
                return false;
            }
        } catch (ParseException e) {
            return false;
        }

        // Check the checksum
        int[] weight = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};
        int[] checkSumValues = {1, 0, 'X', 9, 8, 7, 6, 5, 4, 3, 2};
        int sum = 0;
        for (int i = 0; i < 17; i++) {
            sum += (idCardNumber.charAt(i) - '0') * weight[i];
        }
        int remainder = sum % 11;
        char expectedCheckSum = (char) checkSumValues[remainder];

        return (lastChar == expectedCheckSum);
    }

    public static void main(String[] args) {
        String idCardNumber = "45020419991113102X";
        boolean isValid = isValidIDCard(idCardNumber);
        System.out.println("Is valid ID card: " + isValid);
    }
}
