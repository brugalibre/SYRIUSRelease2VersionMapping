package com.adcubum.versionmapping.bash;

import static java.util.Objects.requireNonNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Set;

import com.adcubum.versionmapping.bash.exception.BashScriptExecuteException;
import com.adcubum.versionmapping.util.filesystem.FileSystemUtil;

/**
 * This helper copies a bash script within this jar to the local unix file system and sets the necessary permission in order to make the
 * copied bash script executable
 * 
 * The {@link BashScriptCreator} is used by the {@link BashScriptExecutor}
 * 
 * @author Dominic
 *
 */
public class BashScriptCreator {

   private static final String RWXR_XR_X = "rwxr-xr-x";
   private String bashScriptFileName;
   private String path2InternalBashScriptFile;
   private String bashScriptTargetDir;

   /**
    * Creates a new {@link BashScriptCreator}
    * 
    * @param bashScriptTargetDir
    *        the directory in which the bash script is copied
    * @param bashScriptFileName
    *        the name of the bash script to copy to the local file system
    */
   BashScriptCreator(String bashScriptTargetDir, String bashScriptFileName) {
      this.bashScriptFileName = requireNonNull(bashScriptFileName);
      this.bashScriptTargetDir = requireNonNull(bashScriptTargetDir);
      this.path2InternalBashScriptFile = "res/bash" + FileSystemUtil.getFileSystemSeparator() + bashScriptFileName;
   }

   /**
    * Creates and prepares the bash-script incl. setting the necessary file permissions
    */
   void createAndPrepareBashScript() {
      File destBashFile = new File(bashScriptTargetDir + bashScriptFileName);
      try {
         copyBashScriptFromResources2FileSystem(destBashFile);
         setReadWriteFilePermission(destBashFile);
      } catch (IOException e) {
         throw new BashScriptExecuteException(e);
      }
   }

   private void copyBashScriptFromResources2FileSystem(File destBashFile) throws IOException {
      try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(path2InternalBashScriptFile)) {
         Files.copy(inputStream, destBashFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
      }
   }

   private static void setReadWriteFilePermission(File destBashFile) throws IOException {
      Set<PosixFilePermission> perms = PosixFilePermissions.fromString(RWXR_XR_X);
      Files.setPosixFilePermissions(destBashFile.toPath(), perms);
   }
}
