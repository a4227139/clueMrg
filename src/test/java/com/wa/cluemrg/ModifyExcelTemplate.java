package com.wa.cluemrg;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class ModifyExcelTemplate {
    public static void main(String[] args) {
        String filePath = "E:\\Git\\clue-mrg\\src\\main\\resources\\templates\\caseTemplate.xlsx";
        String outputFilePath = "E:\\Git\\clue-mrg\\src\\main\\resources\\templates\\caseResult.xlsx";

        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0); // Assuming you want to modify the first sheet
            Row row = sheet.getRow(0); // Assuming you want to modify the second row
            Cell cell = row.getCell(0); // Assuming you want to modify the second cell in that row

            // Modify the cell value
            cell.setCellValue("2023年全市电诈案件情况统计表（1.1-9.16）");

            // Save the modified workbook to a new file
            try (FileOutputStream fos = new FileOutputStream(outputFilePath)) {
                workbook.write(fos);
            }

            System.out.println("Excel template modified successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

