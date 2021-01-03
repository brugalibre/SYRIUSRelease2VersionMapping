
package com.adcubum.versionmapping.xlscreate.create;

import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.adcubum.versionmapping.xlscreate.exception.XLSSheetCreationException;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

/**
 * The {@link XLSSheetCreator} creates a {@link Sheet}
 * 
 * @author DStalder
 *
 */
public class XLSSheetCreator {

   private static final String CSV_CONTENT_DELIMITER = ";";
   private String sheetName;
   private String csvFilePath;

   /**
    * Creates anew {@link XLSSheetCreator with the given sheet name and path to a csv-file which contains the actual content
    * for this sheet
    * 
    * @param sheetName
    *        the name of the sheet
    * @param csvFilePath
    *        the path to the csv-file
    */
   public XLSSheetCreator(String sheetName, String csvFilePath) {
      this.sheetName = requireNonNull(sheetName);
      this.csvFilePath = requireNonNull(csvFilePath);
   }

   /**
    * Creates and returns a {@link Sheet}.
    * 
    * @param workbook
    *        the {@link Workbook}
    * @return the created {@link Sheet}
    */
   public Sheet createXslSheetFromCsv(Workbook workbook) {
      try (CSVReader reader = createNewCSVReader(csvFilePath)) {
         return createXslSheetFromCsv(workbook, reader);
      } catch (CsvValidationException | IOException e) {
         throw new XLSSheetCreationException(e);
      }
   }

   private Sheet createXslSheetFromCsv(Workbook workbook, CSVReader reader) throws IOException, CsvValidationException {
      CreationHelper creationHelper = workbook.getCreationHelper();
      Sheet sheet = workbook.createSheet(sheetName);
      String[] nextLines;
      int rowCount = 0;
      while (nonNull((nextLines = reader.readNext()))) {
         Row row = sheet.createRow((short) rowCount++);
         createAllCells4Row(creationHelper, nextLines, row);
      }
      return sheet;
   }

   private void createAllCells4Row(CreationHelper creationHelper, String[] nextLines, Row row) {
      for (int i = 0; i < nextLines.length; i++) {
         String cellValues = nextLines[i];
         createCells4CellValues(creationHelper, row, cellValues);
      }
   }

   private void createCells4CellValues(CreationHelper creationHelper, Row row, String cellValues) {
      int i = 0;
      for (String cellValue : cellValues.split(CSV_CONTENT_DELIMITER)) {
         Cell cell = row.createCell(i, CellType.STRING);
         cell.setCellValue(creationHelper.createRichTextString(cellValue));
         i++;
      }
   }

   private static CSVReader createNewCSVReader(String csvFilePath) throws FileNotFoundException {
      return new CSVReader(new FileReader(new File(csvFilePath)));
   }
}
