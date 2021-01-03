package com.adcubum.versionmapping.bash.exception;

public class BashScriptExecuteException extends RuntimeException {

   private static final long serialVersionUID = 1L;

   public BashScriptExecuteException(Exception e) {
      super(e);
   }
}
