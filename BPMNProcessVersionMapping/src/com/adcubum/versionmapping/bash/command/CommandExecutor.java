package com.adcubum.versionmapping.bash.command;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.adcubum.versionmapping.bash.exception.BashScriptExecuteException;

/**
 * The {@link CommandExecutor} executes any command with arguments on the local operating system
 * 
 * @author Dominic
 *
 */
public class CommandExecutor {
   private CommandExecutor() {
      // private 
   }

   /**
    * Executes the given command with the given arguments and wait until the process is finish
    * 
    * @param command
    *        the command
    * @param commandArgs
    *        possible arguments for the given command
    * @return the exit value of the subprocess represented by this Process object. By convention, the value 0 indicates normal termination.
    * @see Process#waitFor()
    */
   public static int executeCommandAndWaitFor(String command, List<String> commandArgs) {
      try {
         List<String> commandAndArgs = new ArrayList<>(commandArgs);
         commandAndArgs.add(0, command);
         return new ProcessBuilder(commandAndArgs)
               .inheritIO() // otherwise no error/output from the started process is visible
               .start()
               .waitFor();
      } catch (InterruptedException e) {
         Thread.currentThread().interrupt();
         throw new BashScriptExecuteException(e);
      } catch (IOException e) {
         throw new BashScriptExecuteException(e);
      }
   }
}
