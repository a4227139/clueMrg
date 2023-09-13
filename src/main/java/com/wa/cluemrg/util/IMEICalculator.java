package com.wa.cluemrg.util;

public class IMEICalculator {
    public static String calculateCheckDigit(String imei) {
        if (imei == null || imei.length() < 14) {
            throw new IllegalArgumentException("IMEI must be 14 digits long.");
        }else if (imei.length() > 14){
            imei=imei.substring(0,14);
        }
        StringBuilder stringBuilder = new StringBuilder(imei);
        int sum = 0;
        for (int i = 0; i < 14; i++) {
            int digit = Character.getNumericValue(imei.charAt(i));
            if (i % 2 == 1) {
                digit *= 2;
                digit = digit % 10 + digit / 10;
            }
            sum += digit;
        }

        int checkDigit = (10 - (sum % 10)) % 10;
        return stringBuilder.append(checkDigit).toString();
    }

    public static void main(String[] args) {
        String imeiWithoutCheckDigit = "357377090472871"; // Replace with your 14-digit IMEI
        System.out.println("IMEI with Check Digit: " + calculateCheckDigit(imeiWithoutCheckDigit));
    }
}

