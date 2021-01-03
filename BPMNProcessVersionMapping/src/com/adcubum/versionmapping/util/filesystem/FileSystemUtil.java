
package com.adcubum.versionmapping.util.filesystem;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.swing.filechooser.FileSystemView;

public class FileSystemUtil {

   private FileSystemUtil() {
      // private
   }

   /**
    * @return the path as String to the users home directory
    */
   public static String getHomeDir() {
      FileSystemView filesys = FileSystemView.getFileSystemView();
      File homeDirectory = filesys.getHomeDirectory();
      return homeDirectory.getPath();
   }

   /**
    * @return the default {@link FileSystem} separator depending on the current OS
    */
   public static String getFileSystemSeparator() {
      FileSystem fileSystem = FileSystems.getDefault();
      return fileSystem.getSeparator();
   }

   /**
    * Deletes the {@link File} at the given {@link Path} and ignores any exception
    * 
    * @param path2File
    */
   public static void deleteFile(String path2File) {
      try {
         Files.delete(new File(path2File).toPath());
      } catch (IOException e) {
         e.printStackTrace();// ignore
      }
   }
}
