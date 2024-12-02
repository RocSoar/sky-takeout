package com.sky.utils;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;

@Slf4j
@Component
public class POIUtil {
    public static final int TOTAL_ROW = 38;
    public static final int TOTAL_COLUMN = 7;
    public static final int H1_HEIGHT = 48;
    public static final int H2_HEIGHT = 28;

    private final Workbook workbook = new XSSFWorkbook();

    @Getter
    private final Cell[][] table;

    public POIUtil() {
        table = createTable();
    }

    private Cell[][] createTable() {
        log.info("正在创建Excel模版 ...");

//        workbook = new XSSFWorkbook();

        Sheet sheet = workbook.createSheet("Sheet1");

        CellStyle h1Style = createH1Style();
        CellStyle h2Style = createH2Style();
        CellStyle h3Style = createH3Style();
        CellStyle h4Style = createH4Style();

        Cell[][] table = new Cell[TOTAL_ROW][TOTAL_COLUMN];

        for (int x = 0; x < table.length; x++) {
            Row row = sheet.createRow(x);
            row.setHeightInPoints(x == 0 ? H1_HEIGHT : H2_HEIGHT);

            for (int y = 0; y < table[x].length; y++) {
                Cell cell = row.createCell(y);
                if (y > 0) {
                    cell.setCellStyle(h4Style);
                }

                if (x == 0 && y == 1) {
                    cell.setCellValue("运营数据报表");
                    cell.setCellStyle(h1Style);
                } else if (x == 2 && y == 1) {
                    cell.setCellValue("概览数据");
                    cell.setCellStyle(h2Style);
                } else if (x == 5 && y == 1) {
                    cell.setCellValue("明细数据");
                    cell.setCellStyle(h2Style);
                } else if (x == 3) {
                    switch (y) {
                        case 1 -> {
                            cell.setCellValue("营业额");
                            cell.setCellStyle(h3Style);
                        }
                        case 3 -> {
                            cell.setCellValue("订单完成率");
                            cell.setCellStyle(h3Style);
                        }
                        case 5 -> {
                            cell.setCellValue("新增用户数");
                            cell.setCellStyle(h3Style);
                        }
                    }
                } else if (x == 4) {
                    switch (y) {
                        case 1 -> {
                            cell.setCellValue("有效订单");
                            cell.setCellStyle(h3Style);
                        }
                        case 3 -> {
                            cell.setCellValue("平均客单价");
                            cell.setCellStyle(h3Style);
                        }
                    }
                } else if (x == 6) {
                    switch (y) {
                        case 1 -> {
                            cell.setCellValue("日期");
                            cell.setCellStyle(h3Style);
                        }
                        case 2 -> {
                            cell.setCellValue("营业额");
                            cell.setCellStyle(h3Style);
                        }
                        case 3 -> {
                            cell.setCellValue("有效订单");
                            cell.setCellStyle(h3Style);
                        }
                        case 4 -> {
                            cell.setCellValue("订单完成率");
                            cell.setCellStyle(h3Style);
                        }
                        case 5 -> {
                            cell.setCellValue("平均客单价");
                            cell.setCellStyle(h3Style);
                        }
                        case 6 -> {
                            cell.setCellValue("新增用户数");
                            cell.setCellStyle(h3Style);
                        }
                    }
                }
                table[x][y] = cell;
                sheet.setColumnWidth(y, 20 * 256);
            }
        }

        // 合并单元格
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 1, 6));
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 1, 6));
        sheet.addMergedRegion(new CellRangeAddress(2, 2, 1, 6));
        sheet.addMergedRegion(new CellRangeAddress(5, 5, 1, 6));

        return table;
    }

    public void write(OutputStream outputStream) throws IOException {
        workbook.write(outputStream);

        outputStream.close();
//        workbook.close();    // workbook此处是单例, 不关闭是为了下一次写出
        log.info("Excel 文件已创建并写至输出流");
    }

    private Font createFont(short fontSize, boolean bold) {
        Font font = workbook.createFont();
        font.setFontHeightInPoints(fontSize);
        font.setBold(bold);
        return font;
    }

    public CellStyle createCellStyle(short fontSize, boolean bold, IndexedColors bgColor, FillPatternType fillPatternType, BorderStyle borderStyle, HorizontalAlignment horizontalAlignment, VerticalAlignment verticalAlignment) {
        Font font = createFont(fontSize, bold);
        CellStyle style = workbook.createCellStyle();
        style.setFont(font);
        if (bgColor != null) {
            style.setFillForegroundColor(bgColor.getIndex());
            style.setFillPattern(fillPatternType);
        }
        if (borderStyle != null) {
            style.setBorderTop(borderStyle);
            style.setBorderBottom(borderStyle);
            style.setBorderLeft(borderStyle);
            style.setBorderRight(borderStyle);
        }
        style.setAlignment(horizontalAlignment);
        style.setVerticalAlignment(verticalAlignment);
        return style;
    }

    public CellStyle createH1Style() {
        return createCellStyle((short) 28, true, IndexedColors.PALE_BLUE, FillPatternType.SOLID_FOREGROUND, BorderStyle.MEDIUM, HorizontalAlignment.CENTER, VerticalAlignment.CENTER);
    }

    public CellStyle createH2Style() {
        return createCellStyle((short) 20, true, IndexedColors.LIGHT_YELLOW, FillPatternType.SOLID_FOREGROUND, BorderStyle.MEDIUM, HorizontalAlignment.CENTER, VerticalAlignment.CENTER);
    }

    public CellStyle createH3Style() {
        return createCellStyle((short) 16, false, null, null, BorderStyle.MEDIUM, HorizontalAlignment.CENTER, VerticalAlignment.CENTER);
    }

    public CellStyle createH4Style() {
        return createCellStyle((short) 14, false, null, null, BorderStyle.MEDIUM, HorizontalAlignment.CENTER, VerticalAlignment.CENTER);
    }

    public CellStyle createDateStyle(String dateFormat) {
        CellStyle style = createH4Style();

        CreationHelper creationHelper = workbook.getCreationHelper();
        style.setDataFormat(creationHelper.createDataFormat().getFormat(dateFormat));
        return style;
    }
}
