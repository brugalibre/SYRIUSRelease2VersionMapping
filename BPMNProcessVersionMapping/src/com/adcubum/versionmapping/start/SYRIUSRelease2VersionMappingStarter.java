
package com.adcubum.versionmapping.start;

import static com.adcubum.versionmapping.util.filesystem.FileSystemUtil.isDirectoryAndExists;

import com.adcubum.apibridgeversionmapping.start.APIBridgeVersionMappingStarter;
import com.adcubum.bpmnprocessversionmapping.start.BPMNProcessVersionMappingStarter;
import com.adcubum.versionmapping.util.log.ConsoleLogger;

/**
 * The {@link APIBridgeVersionMappingStarter} starts the process in order to create either a BPMN-Process to SYRIUS-Release mapping file or
 * an API-Bridge-Version to SYRIUS-Release mapping file. It there needs either one command line arguments which leads to a SRIUS git-repo or
 * none for creating the BPMN-Process mapping or two command line argument, which leads to the api-bridge-SYRIUS-integration repo and the
 * api-bridge repo respectively for creating the API-Bridge mapping file
 * 
 * @author Dominic
 *
 */
public class SYRIUSRelease2VersionMappingStarter {

   public static void main(String[] args) {
      verifyArguments(args);
      new SYRIUSRelease2VersionMappingStarter().start(args);
   }

   private void start(String[] args) {
      if (args.length == 2) {
         new APIBridgeVersionMappingStarter().createAPIBridgeVersionMapping(args[0], args[1]);
      } else {
         new BPMNProcessVersionMappingStarter().createBPMNProcessVersionMapping(args.length == 0 ? "" : args[0]);
      }
   }

   private static void verifyArguments(String[] args) {
      if (isInvalidArgument(args)) {
         ConsoleLogger.log("Invalid command line arguments!" + System.lineSeparator() +
               "To create the API-Bridge-Version mapping we need two arguments which leads to the repo of the api-bridge-SYRIUS-integration and the api-bridge"
               + System.lineSeparator() +
               "In order to create the BPMN-Process mapping we need either one argument, which leads to any SYRIUS repo, or none"
               + System.lineSeparator() +
               "Usage: java -jar SYRIUSRelease2VersionMappingCreator.jar apibridge-syriusintegration-bestandsverw/ apibridge-bestandsverw/"
               + System.lineSeparator() +
               "Usage: java -jar SYRIUSRelease2VersionMappingCreator.jar rel3_11_HEAD/syrius" + System.lineSeparator() +
               "Usage: java -jar SYRIUSRelease2VersionMappingCreator.jar" + System.lineSeparator());
         System.exit(-1);
      }
   }

   private static boolean isInvalidArgument(String[] args) {
      return isInvalidBPMNArgument(args) || isInvalidAPIArgument(args) || args.length > 2;
   }

   private static boolean isInvalidAPIArgument(String[] args) {
      return args.length == 2 && (!isDirectoryAndExists(args[0])
            || !isDirectoryAndExists(args[1]));
   }

   private static boolean isInvalidBPMNArgument(String[] args) {
      return args.length == 1 && !isDirectoryAndExists(args[0]);
   }
}
