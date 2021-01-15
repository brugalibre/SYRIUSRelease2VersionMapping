
package com.adcubum.versionmapping.xlscreate.create;

import static java.util.Objects.requireNonNull;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
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
   private List<XLSSheetCreator> xlsSheetCreators;
   private Workbook workbook;

   /**
    * Creates a new {@link XLSCreator} with a given list of 'sheet-name' to 'csv-file-with-the-content' pairs
    * 
    * @param sheetName2CsvPath
    *        the key-value pairs for each sheet name and it's csv-file with the raw content
    */
   public XLSCreator(List<Pair<String, String>> sheetName2CsvPath) {
      this.xlsSheetCreators = requireNonNull(sheetName2CsvPath).stream()
            .map(pair -> new XLSSheetCreator(pair.getLeft(), pair.getRight()))
            .collect(Collectors.toList());
      init();
   }

   /**
    * Creates a new {@link XLSCreator}
    * 
    * @param sheetName
    *        the name of the only sheet within the xls-file
    * @param csvFilePath
    *        the path to the csv-file which provides the actual content for the final xls-file
    */
   public XLSCreator(String sheetName, String csvFilePath) {
      this.xlsSheetCreators = Collections.singletonList(new XLSSheetCreator(sheetName, csvFilePath));
      init();
   }

   private void init() {
      this.workbook = new HSSFWorkbook();
      this.xlsSheetFormatter = new XLSSheetFormatter(workbook);
   }

   public void readCsvAndCreateXlsFile(String xlsOutputFilePath) {
      ConsoleLogger.log("Create file '" + xlsOutputFilePath + "'...");
      createAndFormatXlsSheets();
      writeWorkbook2FileAndClose(xlsOutputFilePath);
   }

   private void createAndFormatXlsSheets() {
      for (XLSSheetCreator xlsSheetCreator : xlsSheetCreators) {
         createAndFormatXlsSheet(xlsSheetCreator);
      }
   }

   private void createAndFormatXlsSheet(XLSSheetCreator xlsSheetCreator) {
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
