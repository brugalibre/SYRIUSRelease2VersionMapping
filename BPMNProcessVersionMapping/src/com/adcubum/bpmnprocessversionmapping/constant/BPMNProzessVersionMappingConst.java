package com.adcubum.bpmnprocessversionmapping.constant;

import static com.adcubum.versionmapping.util.filesystem.FileSystemUtil.getFileSystemSeparator;

import com.adcubum.versionmapping.util.filesystem.FileSystemUtil;

public class BPMNProzessVersionMappingConst {
   private BPMNProzessVersionMappingConst() {
      // private
   }

   /** Directory in which the temporary (.csv) and the final (.xls) version mapping files are located */
   private static final String BPMN_VERSIONIERUNG_CSV_XSL_FILE_DIR = FileSystemUtil.getHomeDir();

   /** Path to the xls-version mapping file */
   public static final String BPMN_VERSIONIERUNGS_XLS_FILE_PATH =
         BPMN_VERSIONIERUNG_CSV_XSL_FILE_DIR + getFileSystemSeparator() + "BPMN-Versionierung-Bestand.xls";

   /** Path to the raw-csv-content file */
   public static final String TEMP_RAW_CSV_CONTENT_FILE_PATH =
         BPMN_VERSIONIERUNG_CSV_XSL_FILE_DIR + getFileSystemSeparator() + "bpmnVersionierungRaw.csv";

   /** Name of the bash script which creates the raw-csv-content file */
   public static final String CREATE_CSV_CONTENT_SCRIPT_FILE_NAME = "createBPMNProzessVersionsMapping.sh";

   /** The name of the Excel sheet, which contains the actual version mapping */
   public static final String BPMN_VERSION_SHEET_NAME = "Versionierung der BPMN-Prozesse";
}
