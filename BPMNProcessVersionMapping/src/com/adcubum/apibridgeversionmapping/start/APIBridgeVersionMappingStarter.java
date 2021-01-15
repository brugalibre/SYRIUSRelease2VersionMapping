
package com.adcubum.apibridgeversionmapping.start;

import static com.adcubum.apibridgeversionmapping.constant.APIBridgeVersionMappingConst.API_BRDIGE_INTEG_VERSION_SHEET_NAME;
import static com.adcubum.apibridgeversionmapping.constant.APIBridgeVersionMappingConst.API_BRDIGE_VERSION_SHEET_NAME;
import static com.adcubum.apibridgeversionmapping.constant.APIBridgeVersionMappingConst.API_BRIDGE_VERSIONIERUNGS_XLS_FILE_PATH;
import static com.adcubum.apibridgeversionmapping.constant.APIBridgeVersionMappingConst.CREATE_API_BRIDGE_SYR_REL_MAPPING;
import static com.adcubum.apibridgeversionmapping.constant.APIBridgeVersionMappingConst.CREATE_CSV_CONTENT_SCRIPT_FILE_NAME;
import static com.adcubum.apibridgeversionmapping.constant.APIBridgeVersionMappingConst.TEMP_API_BRIDGE_SYR_REL_MAPPING_FILE_PATH;
import static com.adcubum.apibridgeversionmapping.constant.APIBridgeVersionMappingConst.TEMP_RAW_CSV_CONTENT_FILE_PATH;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import com.adcubum.versionmapping.bash.BashScriptExecutor;
import com.adcubum.versionmapping.util.filesystem.FileSystemUtil;
import com.adcubum.versionmapping.util.log.ConsoleLogger;
import com.adcubum.versionmapping.xlscreate.create.XLSCreator;

/**
 * The {@link APIBridgeVersionMappingStarter} starts the process in order to create a API-Bridge-Version to SYRIUS-Release mapping file
 * It needs two command line argument, which leads to the api-bridge-SYRIUS-integration repo and the api-bridge repo respectively
 * 
 * @author Dominic
 *
 */
public class APIBridgeVersionMappingStarter {

   public static void main(String[] args) {
      verifyArguments(args);
      new APIBridgeVersionMappingStarter().start(args[0], args[1]);
   }

   private void start(String path2ApiBridgeSyrIntegrationRepo, String path2ApiBridgeRepo) {
      createAPIBridge2SYRIUSIntegMapping(path2ApiBridgeSyrIntegrationRepo);
      createAPIBridgeVersionRawContent(path2ApiBridgeRepo);
      readCsvAndCreateXlsFile();
      ConsoleLogger.log("Done!");
   }

   /*
    * Copies the bash script which creates the version-mapping between syrius & the api-bridge-SYRIUS-integration and executes this bash-script
    */
   private static void createAPIBridge2SYRIUSIntegMapping(String path2ApiBridgeSyrIntegrationRepo) {
      ConsoleLogger.log("Create API-Bridge to API-Bridge-Integration mapping file...");
      new BashScriptExecutor(path2ApiBridgeSyrIntegrationRepo, CREATE_API_BRIDGE_SYR_REL_MAPPING)
            .executeBashScript(TEMP_API_BRIDGE_SYR_REL_MAPPING_FILE_PATH);
   }

   /**
    * Copies the bash script to the file system, which creates a temp. csv-file which contains the raw data from which the final xls is
    * created
    */
   private static void createAPIBridgeVersionRawContent(String path2ApiBridgeRepo) {
      ConsoleLogger.log("Create raw API-Bridge version mapping content...");
      new BashScriptExecutor(path2ApiBridgeRepo, CREATE_CSV_CONTENT_SCRIPT_FILE_NAME)
            .executeBashScript(TEMP_RAW_CSV_CONTENT_FILE_PATH, TEMP_API_BRIDGE_SYR_REL_MAPPING_FILE_PATH);
   }

   /*
    * Creates the final xls and deletes all temp. files
    */
   private static void readCsvAndCreateXlsFile() {
      try {
         new XLSCreator(getSheetName2CsvFilePairs()).readCsvAndCreateXlsFile(API_BRIDGE_VERSIONIERUNGS_XLS_FILE_PATH);
      } finally {
         FileSystemUtil.deleteFile(TEMP_RAW_CSV_CONTENT_FILE_PATH);
         FileSystemUtil.deleteFile(TEMP_API_BRIDGE_SYR_REL_MAPPING_FILE_PATH);
      }
   }

   private static List<Pair<String, String>> getSheetName2CsvFilePairs() {
      return Arrays.asList(new ImmutablePair<>(API_BRDIGE_VERSION_SHEET_NAME, TEMP_RAW_CSV_CONTENT_FILE_PATH),
            new ImmutablePair<>(API_BRDIGE_INTEG_VERSION_SHEET_NAME, TEMP_API_BRIDGE_SYR_REL_MAPPING_FILE_PATH));
   }

   private static void verifyArguments(String[] args) {
      if (isInvalidArgument(args)) {
         ConsoleLogger.log("Invalid command line arguments!" + System.lineSeparator() +
               "We need two arguments which leads to the repo of the api-bridge-SYRIUS-integration and the api-bridge" + System.lineSeparator() +
               "Usage: java -jar APIBridgeProzessVersionMappingCreator.jar apibridge-syriusintegration-bestandsverw/ apibridge-bestandsverw/"
               + System.lineSeparator());
         System.exit(-1);
      }
   }

   private static boolean isInvalidArgument(String[] args) {
      return args.length != 2 || isCorretArgSizeButWrongArguments(args);
   }

   private static boolean isCorretArgSizeButWrongArguments(String[] args) {
      return args.length == 2 && (!args[0].endsWith(FileSystemUtil.getFileSystemSeparator())
            || !args[1].endsWith(FileSystemUtil.getFileSystemSeparator()));
   }
}
