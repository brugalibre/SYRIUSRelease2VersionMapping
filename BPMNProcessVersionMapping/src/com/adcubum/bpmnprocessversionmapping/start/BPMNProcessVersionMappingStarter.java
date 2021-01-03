
package com.adcubum.bpmnprocessversionmapping.start;

import static com.adcubum.bpmnprocessversionmapping.constant.BPMNProzessVersionMappingConst.BPMN_VERSIONIERUNGS_XLS_FILE_PATH;
import static com.adcubum.bpmnprocessversionmapping.constant.BPMNProzessVersionMappingConst.BPMN_VERSION_SHEET_NAME;
import static com.adcubum.bpmnprocessversionmapping.constant.BPMNProzessVersionMappingConst.CREATE_CSV_CONTENT_SCRIPT_FILE_NAME;
import static com.adcubum.bpmnprocessversionmapping.constant.BPMNProzessVersionMappingConst.TEMP_RAW_CSV_CONTENT_FILE_PATH;

import com.adcubum.versionmapping.bash.BashScriptExecutor;
import com.adcubum.versionmapping.util.filesystem.FileSystemUtil;
import com.adcubum.versionmapping.util.log.ConsoleLogger;
import com.adcubum.versionmapping.xlscreate.create.XLSCreator;

/**
 * The {@link BPMNProcessVersionMappingStarter} starts the process in order to create a BPMN-Process to SYRIUS-Release mapping file
 * It either needs one command line argument, which leads to any syrius git-repository or none
 * 
 * @author Dominic
 *
 */
public class BPMNProcessVersionMappingStarter {

   public static void main(String[] args) {
      verifyArguments(args);
      new BPMNProcessVersionMappingStarter().start(args.length == 0 ? "" : args[0]);
   }

   private void start(String path2SyriusRepo) {
      createRawBPMNProcessVersionMappingContent(path2SyriusRepo);
      readCsvAndCreateXlsFile(TEMP_RAW_CSV_CONTENT_FILE_PATH, BPMN_VERSIONIERUNGS_XLS_FILE_PATH);
      ConsoleLogger.log("Done!");
   }

   private void createRawBPMNProcessVersionMappingContent(String path2SyriusRepo) {
      ConsoleLogger.log("Create raw BPMN-process version mapping content...");
      new BashScriptExecutor(path2SyriusRepo, CREATE_CSV_CONTENT_SCRIPT_FILE_NAME)
            .executeBashScript(TEMP_RAW_CSV_CONTENT_FILE_PATH);
   }

   private static void readCsvAndCreateXlsFile(String rawCsvContentFile, String xlsOutputFilePath) {
      try {
         new XLSCreator(BPMN_VERSION_SHEET_NAME, rawCsvContentFile).readCsvAndCreateXlsFile(xlsOutputFilePath);
      } finally {
         FileSystemUtil.deleteFile(rawCsvContentFile);
      }
   }

   private static void verifyArguments(String[] args) {
      if (isInvalidArgument(args)) {
         ConsoleLogger.log("Invalid command line argument!" + System.lineSeparator() +
               "We need either one argument which leads to a syrius repo or none" + System.lineSeparator() +
               "Usage: java -jar BPMNProzessVersionMappingCreator.jar rel3_10_HEAD/syrius/" + System.lineSeparator() +
               "Usage: java -jar BPMNProzessVersionMappingCreator.jar");
         System.exit(-1);
      }
   }

   private static boolean isInvalidArgument(String[] args) {
      return args.length >= 1 && !args[0].endsWith(FileSystemUtil.getFileSystemSeparator());
   }
}
