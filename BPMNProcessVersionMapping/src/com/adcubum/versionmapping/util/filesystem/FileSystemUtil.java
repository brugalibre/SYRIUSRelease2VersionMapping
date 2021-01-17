
package com.adcubum.versionmapping.util.filesystem;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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
    * Verifies if the given String argument leads to an existing directory
    * 
    * @param path2Dir
    *        the path to a directory
    * @return <code>true</code> if there exists a directory at the given path or <code>false</code> if not
    */
   public static boolean isDirectoryAndExists(String path2Dir) {
      Path currentRelativePath = Paths.get(path2Dir);
      File file = currentRelativePath.toFile();
      return file.isDirectory();
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
