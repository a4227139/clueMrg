package com.wa.cluemrg.util;

public class NumUtil {

    public static int convertStringToInt(String num) {
        // 检查输入字符串是否为空
        if (num == null || num.replaceAll("\\s+%","").isEmpty()) {
            System.out.println("输入为空");
            return 0;
        }
        num=num.replaceAll("[\\s+%]","");
        // 检查字符串是否符合数字格式
        if (isDecimalNumeric(num)) {
            // 将字符串转换为整数
            try {
                return Integer.parseInt(num);
            } catch (NumberFormatException e) {
                System.out.println("字符串转换为整数时出错：" + e.getMessage());
                return 0;
            }
        } else {
            System.out.println("输入不是有效的数字格式");
            return 0;
        }
    }

    public static float convertStringToFloat(String num) {
        // 检查输入字符串是否为空
        if (num == null || num.replaceAll("\\s+%","").isEmpty()) {
            System.out.println("输入为空");
            return 0;
        }
        num=num.replaceAll("[\\s+%]","");
        // 检查字符串是否符合数字格式
        if (isDecimalNumeric(num)) {
            // 将字符串转换为整数
            try {
                return Float.parseFloat(num);
            } catch (NumberFormatException e) {
                System.out.println("字符串转换为整数时出错：" + e.getMessage());
                return 0;
            }
        } else {
            System.out.println("输入不是有效的数字格式");
            return 0;
        }
    }

    public static boolean isDecimalNumeric(String str) {
        // 使用正则表达式检查字符串是否符合带小数点的数字格式
        return str.matches("-?\\d+(\\.\\d+)?");
    }
}
