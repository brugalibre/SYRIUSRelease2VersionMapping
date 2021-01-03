
package com.adcubum.bpmnprocessversionmapping.xlscreate.start;

import com.adcubum.bpmnprocessversionmapping.xlscreate.XLSCreator;

public class XLSCreatorStart {

   public static void main(String[] args) {
      String csvFilePath = args[0];
      String xlsOutputFilePath = args[1];
      XLSCreator xlsCreator = new XLSCreator();
      xlsCreator.readCsvAndCreateXlsFile(csvFilePath, xlsOutputFilePath);
   }
}
