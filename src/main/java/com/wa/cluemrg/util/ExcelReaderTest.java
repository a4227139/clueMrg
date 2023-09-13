package com.wa.cluemrg.util;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.StringUtils;

import java.io.FileInputStream;

public class ExcelReaderTest {
    public static void main(String[] args) {
        try {
            FileInputStream file = new FileInputStream("E:\\Git\\clue-mrg\\src\\main\\resources\\static\\excel\\2023-05-03\\（柳州）2023年05月02日公安部打击治理电信网络新型违法犯罪专项行动办公室涉案线索核查表.xlsx");
            XSSFWorkbook workbook = new XSSFWorkbook(file);
            XSSFSheet sheet = workbook.getSheetAt(0);

            // 获取第一行，即标题行
            XSSFRow titleRow =null;
            int numCols=0 ; // 获取总列数
            int titleIndex=0;
            System.out.println("sheet.getLastRowNum():"+sheet.getLastRowNum());
            // 遍历
            for (int i = 0; i <= sheet.getLastRowNum(); i++) {
                //有合并单元格的地方还不是标题行
                if (isMergedRegionsExist(sheet, i)&&titleRow==null) {
                    continue;
                }else if(titleRow==null&&sheet.getRow(i).getLastCellNum()>2){
                    titleRow = sheet.getRow(i);
                    numCols = titleRow.getLastCellNum();
                    titleIndex=i;
                }else if(isMergedRegionsExist(sheet, i)&&titleRow!=null){
                    break;
                }
            }
            //标题行
            for (int j = 0; j < numCols; j++) {
                XSSFCell cell = titleRow.getCell(j);
                String cellValue = cell.getStringCellValue();
                System.out.print(cellValue + "\t");
            }
            System.out.println();
            //数据行
            for (int i = titleIndex+1; i <= sheet.getLastRowNum(); i++) {
                XSSFRow row = sheet.getRow(i);
                if(isRowEmpty(row)){
                    continue;
                }
                // 获取每个单元格的数据
                for (int j = 0; j < numCols; j++) {
                    XSSFCell cell = row.getCell(j);
                    String cellValue= "";
                    if(cell==null||cell.getCellType() == CellType.BLANK){
                        cellValue= "";
                    }else if (cell.getCellType() == CellType.NUMERIC) {
                        // 如果单元格类型为数字，则使用 NumberToTextConverter 转换器获取其值
                        cellValue = NumberToTextConverter.toText(cell.getNumericCellValue());
                    } else if (cell.getCellType() == CellType.STRING) {
                        // 如果单元格类型为字符串，则直接获取其值
                        cellValue = cell.getStringCellValue().trim();
                    }
                    System.out.print(cellValue + "\t");
                }
                System.out.println();
            }
            file.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static boolean isMergedRegionsExist(XSSFSheet sheet, int rowIndex) {
        // 判断是否存在合并单元格
        boolean hasMergedRegion = false;
        for (int i = 0; i < sheet.getNumMergedRegions(); i++) {
            CellRangeAddress mergedRegion = sheet.getMergedRegion(i);
            if (mergedRegion.getFirstRow() <= rowIndex && rowIndex <= mergedRegion.getLastRow() ) {
                hasMergedRegion = true;
                break;
            }
        }
        return hasMergedRegion;
    }

    static  boolean isRowEmpty(XSSFRow row){
        if (row == null ) {
            return true;
        }
        for (int i=0;i<row.getLastCellNum();i++){
            //System.out.println(row.getCell(i)+":"+row.getCell(i).getCellType());
            if(!StringUtils.isEmpty(row.getCell(i)) && row.getCell(i).getCellType() != CellType.BLANK){
                return false;
            }
        }
        return true;
    }


}
