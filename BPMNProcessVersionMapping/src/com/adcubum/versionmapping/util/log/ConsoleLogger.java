package com.adcubum.versionmapping.util.log;

public class ConsoleLogger {

   private ConsoleLogger() {
      // private
   }

   /**
    * Wrapper fürs simple Logging auf die Konsole
    * 
    * @param msg
    *        die Nachricht
    */
   public static void log(String msg) {
      System.out.println(msg);
   }
}
