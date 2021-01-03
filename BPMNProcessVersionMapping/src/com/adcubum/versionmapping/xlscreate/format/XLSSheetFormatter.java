package com.adcubum.versionmapping.xlscreate.format;


import static java.util.Objects.nonNull;

import java.util.function.Consumer;

import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;

/**
 * Formats a {@link Sheet}
 * 
 * @author DStalder
 *
 */
public class XLSSheetFormatter {

   private short adcColorIndex;
   private CellStyle headerCellStyle;
   private CellStyle defaultCellStyle;

   public XLSSheetFormatter(Workbook workbook) {
      this.adcColorIndex = replaceBlueWithAdcColorIndex(workbook);
      this.headerCellStyle = createHeaderCellStyle(workbook);
      this.defaultCellStyle = createDefaultCellStyle(workbook);
   }

   private short replaceBlueWithAdcColorIndex(Workbook workbook) {
      short defBlueIndex = HSSFColor.HSSFColorPredefined.BLUE.getIndex();
      if (workbook instanceof HSSFWorkbook) {
         HSSFPalette palette = ((HSSFWorkbook) workbook).getCustomPalette();
         palette.setColorAtIndex(defBlueIndex, XLSStyleConst.ADC_BLUE[0], XLSStyleConst.ADC_BLUE[1], XLSStyleConst.ADC_BLUE[2]);
      }
      return defBlueIndex;
   }

   /**
    * Formats the {@link Cell}s in the given {@link Sheet}
    * @param sheet
    *        the Sheet to format
    */
   public void formatWorkSheet(Sheet sheet) {
      sheet.forEach(formatRowAndAutoSizeColumn(sheet));
      sheet.setAutoFilter(new CellRangeAddress(0, 0, 0, getAmountOfColumns(sheet)));
      sheet.createFreezePane(0, 1);
   }

   private static int getAmountOfColumns(Sheet sheet) {
      if (nonNull(sheet.getRow(0))) {
         return sheet.getRow(0)
               .getPhysicalNumberOfCells() - 1;
      }
      return 0;
   }

   private Consumer<? super Row> formatRowAndAutoSizeColumn(Sheet sheet) {
      return row -> {
         sheet.autoSizeColumn(row.getRowNum());
         row.forEach(setCellStyle());
      };
   }

   private Consumer<? super Cell> setCellStyle() {
      return cell -> {
         if (isHeader(cell)) {
            cell.setCellStyle(headerCellStyle);
         } else {
            cell.setCellStyle(defaultCellStyle);
         }
      };
   }

   private static boolean isHeader(Cell cell) {
      return cell.getRowIndex() == 0;
   }

   private CellStyle createHeaderCellStyle(Workbook workbook) {
      Font font = createHeaderFont(workbook);
      CellStyle cellStyle = workbook.createCellStyle();
      cellStyle.setAlignment(HorizontalAlignment.CENTER);
      cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
      cellStyle.setFont(font);
      setHeaderCellColor(cellStyle);
      return cellStyle;
   }

   private void setHeaderCellColor(CellStyle cellStyle) {
      cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
      cellStyle.setFillForegroundColor(adcColorIndex);
      setCellBorder(cellStyle, BorderStyle.THIN, HSSFColor.HSSFColorPredefined.WHITE.getIndex());
   }

   private Font createHeaderFont(Workbook workbook) {
      Font font = workbook.createFont();
      font.setBold(true);
      font.setFontHeightInPoints(XLSStyleConst.HEADER_CELL_FONT_SIZE);
      font.setFontName(XLSStyleConst.CALIBRI);
      font.setColor(HSSFColor.HSSFColorPredefined.WHITE.getIndex());
      return font;
   }

   private CellStyle createDefaultCellStyle(Workbook workbook) {
      Font font = createDefaultCellFont(workbook);
      CellStyle cellStyle = workbook.createCellStyle();
      cellStyle.setFont(font);
      setCellBorder(cellStyle, BorderStyle.THIN, adcColorIndex);
      return cellStyle;
   }

   private Font createDefaultCellFont(Workbook workbook) {
      Font font = workbook.createFont();
      font.setFontHeightInPoints(XLSStyleConst.DEFAULT_CELL_FONT_SIZE);
      font.setFontName(XLSStyleConst.CALIBRI);
      return font;
   }

   private static void setCellBorder(CellStyle cellStyle, BorderStyle borderStyle, short borderColor) {
      cellStyle.setBorderBottom(borderStyle);
      cellStyle.setBottomBorderColor(borderColor);

      cellStyle.setBorderLeft(borderStyle);
      cellStyle.setLeftBorderColor(borderColor);

      cellStyle.setBorderTop(borderStyle);
      cellStyle.setTopBorderColor(borderColor);

      cellStyle.setBorderRight(borderStyle);
      cellStyle.setRightBorderColor(borderColor);
   }
}
