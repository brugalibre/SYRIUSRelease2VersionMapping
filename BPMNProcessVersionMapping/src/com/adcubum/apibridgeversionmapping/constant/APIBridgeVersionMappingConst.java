package com.adcubum.apibridgeversionmapping.constant;

import static com.adcubum.versionmapping.util.filesystem.FileSystemUtil.getFileSystemSeparator;

import com.adcubum.versionmapping.util.filesystem.FileSystemUtil;

public class APIBridgeVersionMappingConst {
   private APIBridgeVersionMappingConst() {
      // private
   }

   /** Directory in which the temporary (.csv) and the final (.xls) version mapping files are located */
   private static final String API_BRIDGE_VERSIONIERUNG_CSV_XSL_FILE_DIR = FileSystemUtil.getHomeDir();

   /** Path to the xls-version mapping file */
   public static final String API_BRIDGE_VERSIONIERUNGS_XLS_FILE_PATH =
         API_BRIDGE_VERSIONIERUNG_CSV_XSL_FILE_DIR + getFileSystemSeparator() + "API-Bridge-Versionierung-Übersicht.xls";

   /** Path to the raw-csv-content file */
   public static final String TEMP_RAW_CSV_CONTENT_FILE_PATH =
         API_BRIDGE_VERSIONIERUNG_CSV_XSL_FILE_DIR + getFileSystemSeparator() + "apiBridgeVersionierungRaw.csv";

   /** Path to the raw-csv-content file */
   public static final String TEMP_API_BRIDGE_SYR_REL_MAPPING_FILE_PATH =
         API_BRIDGE_VERSIONIERUNG_CSV_XSL_FILE_DIR + getFileSystemSeparator() + "apiBridge2SyrReleaseFile.csv";

   /** Name of the bash script which creates the raw-csv-content file of the complete version-mapping (incl. version of the api-services */
   public static final String CREATE_CSV_CONTENT_SCRIPT_FILE_NAME = "createAPIBridgeVersionMapping.sh";

   /** Name of the bash script which creates a simple mapping between the SYRIUS-Releases and the API-Bridge */
   public static final String CREATE_API_BRIDGE_SYR_REL_MAPPING = "createSimpleSyrius2APIBridgeMapping.sh";

   /**
    * The name of the Excel sheet, which contains the actual version mapping.
    */
   public static final String API_BRDIGE_VERSION_SHEET_NAME = "API-Bridge zu SYRIUS Versionsmapping";

   /** The name of the Excel sheet, which contains the version mapping between SYRIUS and SYRIUS-integration */
   public static final String API_BRDIGE_INTEG_VERSION_SHEET_NAME = "SYRIUS-Integration zu API-Bridge Versionsmapping";
}
