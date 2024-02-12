package com.wa.cluemrg.util;

import com.wa.cluemrg.entity.CaseStatIndex;
import com.wa.cluemrg.entity.CaseStatSubIndex;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


public class parseHtmlTest {
    public static void main(String[] args) {
        try {
            // 获取当前项目路径
            String projectPath = System.getProperty("user.dir");
            // 指定文件路径
            String filePath = projectPath + "/src/main/resources/static/old/showReport4.html"; // 请替换为您的文件路径
            // 读取文件内容成字符串
            String content = readFileToString(filePath);
            // 打印文件内容
            //System.out.println("文件内容:\n" + content);
            parseHtmlToCaseStatIndex(content);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String readFileToString(String filePath) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), Charset.forName("GBK")));
        StringBuilder sb = new StringBuilder();
        String str;
        while((str = br.readLine()) != null){
            sb.append(str);
        }
        return sb.toString();
    }

    private static List<CaseStatIndex> parseHtmlToCaseStatIndex(String html) {
        try {
            DecimalFormat decimalFormat = new DecimalFormat("#.##");
            // 使用jsoup解析HTML文件
            Document doc = Jsoup.parse(html);

            // 获取ID为"report1"的表格
            Element reportTable = doc.getElementById("report1");

            List<CaseStatIndex> caseStatIndexList = new ArrayList<>();

            if (reportTable != null) {
                // 获取表格中所有的行
                Elements rows = reportTable.select("tr");

                // 遍历每一行
                for (Element row : rows) {

                    // 获取行中所有的td元素
                    Elements tdElements = row.select("td");
                    //只取统计数据的值
                    if (tdElements.get(0).text().contains("局")||tdElements.get(0).text().contains("市")||tdElements.get(0).text().contains("合计")){
                        if (tdElements.get(0).text().contains("市本级")||tdElements.get(0).text().contains("森林")){
                            continue;
                        }
                        // 输出td元素的显示值
                        for (Element td : tdElements) {
                            String tdValue = td.text();
                            System.out.print(tdValue + "\t"); // 使用制表符分隔每个td元素
                        }
                        CaseStatIndex caseStatIndex = new CaseStatIndex();
                        caseStatIndex.setJurisdiction(JurisdictionUtil.getJurisdiction(tdElements.get(0).text()));
                        caseStatIndex.setCount(convertStringToInt(tdElements.get(1).text()));
                        caseStatIndex.setCountHistory(convertStringToInt(tdElements.get(2).text()));
                        caseStatIndex.setSolveCount(convertStringToInt(tdElements.get(24).text()));
                        caseStatIndex.setSolveRateRatio(convertStringToFloat(tdElements.get(26).text()));
                        caseStatIndex.setLossMoney(convertStringToFloat(tdElements.get(29).text()));
                        caseStatIndex.setLossMoneyFormat(decimalFormat.format(caseStatIndex.getLossMoney()/10000));

                        for (int i=1;i<=4;i++){
                            CaseStatSubIndex caseStatSubIndex = new CaseStatSubIndex();
                            caseStatSubIndex.setLevel(i);
                            if (i==1){
                                System.out.println();
                                System.out.println("26:"+tdElements.get(26).text());
                                caseStatSubIndex.setCount(convertStringToInt(tdElements.get(30).text())+convertStringToInt(tdElements.get(31).text()));
                            }else if (i==4){
                                caseStatSubIndex.setCount(convertStringToInt(tdElements.get(34).text())+convertStringToInt(tdElements.get(35).text()));
                            }else {
                                caseStatSubIndex.setCount(convertStringToInt(tdElements.get(i+30).text()));
                            }
                            caseStatIndex.getCaseStatSubList().add(caseStatSubIndex);
                        }
                        caseStatIndexList.add(caseStatIndex);
                    }
                    // 换行
                    System.out.println();
                }
            } else {
                System.out.println("Table with ID 'report1' not found.");
            }
            for (CaseStatIndex caseStatIndex:caseStatIndexList){
                System.out.println(caseStatIndex);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static int convertStringToInt(String num) {
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

    private static float convertStringToFloat(String num) {
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

    private static boolean isDecimalNumeric(String str) {
        // 使用正则表达式检查字符串是否符合带小数点的数字格式
        return str.matches("-?\\d+(\\.\\d+)?");
    }
}
