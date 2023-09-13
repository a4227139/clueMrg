package com.wa.cluemrg;

import com.wa.cluemrg.entity.Case;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HttpGetExample {
    public static void main(String[] args) {
        try {
            // 创建一个URL对象，表示要访问的URL
            URL url = new URL("http://127.0.0.1:8080/case-simple.html");

            // 打开连接
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // 设置请求方法为GET
            connection.setRequestMethod("GET");

            // 获取响应代码
            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            // 读取响应内容
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            StringBuilder response = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            reader.close();

            // 打印响应内容
            System.out.println("Response Content: ");
            System.out.println(response.toString());
            List<Case> list = parseHtmlToCases(response.toString());
            for (Case case1:list){
                System.out.println("----------------------------case----------------------------");
                System.out.println(case1);
            }


            // 关闭连接
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    public static List<Case> parseHtmlToCases(String html) {
        List<Case> cases = new ArrayList<>();

        Document doc = Jsoup.parse(html);
        Elements rows = doc.select("tr");

        for (Element row : rows) {
            Elements cells = row.select("td");

            if (cells.size() == 12) { // 检查是否有合适的<td>标签
                if (StringUtils.isEmpty(cells.get(1).text())||"案件编号".equals(cells.get(1).text())){//首行（标题行）和caseNo是空直接过
                    continue;
                }
                Case caseObj = new Case();
                caseObj.setSeq(cells.get(0).text());
                caseObj.setCaseNo(cells.get(1).text());
                caseObj.setCaseName(cells.get(2).text());
                caseObj.setCaseUnit(cells.get(3).text());

                try {
                    String registerDateString = cells.get(4).text();
                    if (!StringUtils.isEmpty(registerDateString)){
                        Date registerDate = dateFormat.parse(registerDateString);
                        caseObj.setRegisterDate(registerDate);
                    }
                    String solveDateString = cells.get(6).text();
                    if (!StringUtils.isEmpty(solveDateString)){
                        Date solveDate = dateFormat.parse(solveDateString);
                        caseObj.setSolveDate(solveDate);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                caseObj.setOrganizer(cells.get(5).text());
                caseObj.setType(cells.get(7).text());
                caseObj.setProjectId(cells.get(8).text());
                caseObj.setOrganiser(cells.get(9).text());
                caseObj.setCoOrganiser(cells.get(10).text());
                try {
                    if (!StringUtils.isEmpty(cells.get(11).text())){
                        caseObj.setMoney(Float.parseFloat(cells.get(11).text()));
                    }else {
                        caseObj.setMoney(0);
                    }
                } catch (NumberFormatException e) {
                    caseObj.setMoney(0);
                }
                cases.add(caseObj);
            }
        }

        return cases;
    }
}

