
package com.adcubum.versionmapping.xlscreate.create;

import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.adcubum.versionmapping.util.log.ConsoleLogger;
import com.adcubum.versionmapping.xlscreate.exception.XLSSheetCreationException;
import com.adcubum.versionmapping.xlscreate.format.XLSSheetFormatter;

/**
 * The {@link XLSCreator} creates a xls-file using a {@link HSSFWorkbook} which contains one sheet.
 * 
 * @author Dominic
 *
 */
public class XLSCreator {

   private XLSSheetFormatter xlsSheetFormatter;
   private XLSSheetCreator xlsSheetCreator;
   private Workbook workbook;

   /**
    * Creates a new {@link XLSCreator}
    * 
    * @param sheetName
    *        the name of the only sheet within the xls-file
    * @param csvFilePath
    *        the path to the csv-file which provides the actual content for the final xls-file
    */
   public XLSCreator(String sheetName, String csvFilePath) {
      this.workbook = new HSSFWorkbook();
      this.xlsSheetCreator = new XLSSheetCreator(sheetName, csvFilePath);
      this.xlsSheetFormatter = new XLSSheetFormatter(workbook);
   }

   public void readCsvAndCreateXlsFile(String xlsOutputFilePath) {
      ConsoleLogger.log("Create file '" + xlsOutputFilePath + "'...");
      createAndFormatXlsSheet();
      writeWorkbook2FileAndClose(xlsOutputFilePath);
   }

   private void createAndFormatXlsSheet() {
      Sheet sheet = xlsSheetCreator.createXslSheetFromCsv(workbook);
      xlsSheetFormatter.formatWorkSheet(sheet);
   }

   private void writeWorkbook2FileAndClose(String xlsOutputFilePath) {
      try (FileOutputStream fileOutputStream = new FileOutputStream(xlsOutputFilePath)) {
         workbook.write(fileOutputStream);
      } catch (IOException e) {
         throw new XLSSheetCreationException(e);
      } finally {
         closeWorkbook(workbook);
      }
   }

   private void closeWorkbook(Workbook workbook) {
      try {
         workbook.close();
      } catch (IOException e) {
         throw new XLSSheetCreationException(e);
      }
   }
}
