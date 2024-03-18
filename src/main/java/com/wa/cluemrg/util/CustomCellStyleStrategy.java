package com.wa.cluemrg.util;

import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.write.handler.context.CellWriteHandlerContext;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomCellStyleStrategy extends HorizontalCellStyleStrategy {
    private final Map<Integer, WriteCellStyle> cellStyleMap = new HashMap<>();
    String victimPhone;

    public CustomCellStyleStrategy(String victimPhone) {
        // 设置默认样式
        WriteCellStyle defaultCellStyle = new WriteCellStyle();
        defaultCellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex()); // 默认背景颜色
        defaultCellStyle.setFillPatternType(FillPatternType.SOLID_FOREGROUND);
        cellStyleMap.put(null, defaultCellStyle);
        this.victimPhone = victimPhone;
    }

    @Override
    protected void setContentCellStyle(CellWriteHandlerContext context) {
        super.setContentCellStyle(context);
        // 获取当前行的索引
        int rowIndex = context.getRowIndex();
        // 获取通话记录数据列表
        List list = context.getCellDataList();
        WriteCellData writeCellData = (WriteCellData) list.get(0);
        if (writeCellData.getStringValue()==null) return;
        if (victimPhone.contains(writeCellData.getStringValue())) {
            WriteCellStyle cellStyle = context.getFirstCellData().getOrCreateStyle();
            cellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
            cellStyle.setFillPatternType(FillPatternType.SOLID_FOREGROUND);
        }
    }
}
