package com.adcubum.versionmapping.bash;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.adcubum.versionmapping.bash.command.CommandExecutor;
import com.adcubum.versionmapping.util.filesystem.FileSystemUtil;

/**
 * Creates and executes a exported bash-script in order to create a csv-based content file
 * By calling {@link #executeBashScript(String...)} a {@link BashScriptCreator} is created which copies the given bash
 * script to the local file system and makes it executable
 * 
 * @author Dominic
 *
 */
public class BashScriptExecutor {

   private String bashScriptFileName;
   private String bashScriptTargetDir;

   /**
    * Creates a new {@link BashScriptExecutor}
    * 
    * @param bashScriptTargetDir
    *        the directory in which the bash script is copied
    * @param bashScriptFileName
    *        the name of the bash script to execute
    */
   public BashScriptExecutor(String bashScriptTargetDir, String bashScriptFileName) {
      this.bashScriptFileName = requireNonNull(bashScriptFileName);
      this.bashScriptTargetDir = requireNonNull(bashScriptTargetDir);
   }

   /**
    * Executes the bash-script with the given arguments
    * 
    * @param bashScriptArgumentsIn
    *        arguments which are passed to the given bash script
    */
   public void executeBashScript(String... bashScriptArgumentsIn) {
      createAndPrepareBashScript();
      executeBashScript(Arrays.asList(bashScriptArgumentsIn));
   }

   private void createAndPrepareBashScript() {
      new BashScriptCreator(bashScriptTargetDir, bashScriptFileName).createAndPrepareBashScript();
   }

   private void executeBashScript(List<String> bashScriptArguments) {
      String fileNamePraefix = StringUtils.isEmpty(bashScriptTargetDir) ? "." + FileSystemUtil.getFileSystemSeparator() : bashScriptTargetDir;
      String executeBashScriptCommand = fileNamePraefix + bashScriptFileName;
      try {
         List<String> commandArgs = getCommandArgumentsList(bashScriptArguments);
         CommandExecutor.executeCommandAndWaitFor(executeBashScriptCommand, commandArgs);
      } finally {
         FileSystemUtil.deleteFile(bashScriptTargetDir + bashScriptFileName);
      }
   }

   private List<String> getCommandArgumentsList(List<String> bashScriptArguments) {
      if (StringUtils.isEmpty(bashScriptTargetDir)) {
         return bashScriptArguments;
      }
      List<String> commandArguments = new ArrayList<>(bashScriptArguments);
      commandArguments.add(bashScriptTargetDir);
      return commandArguments;
   }
}
