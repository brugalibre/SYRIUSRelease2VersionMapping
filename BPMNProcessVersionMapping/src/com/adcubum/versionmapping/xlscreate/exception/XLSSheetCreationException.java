package com.adcubum.versionmapping.xlscreate.exception;

public class XLSSheetCreationException extends RuntimeException {

   private static final long serialVersionUID = 1L;

   public XLSSheetCreationException(Exception e) {
      super(e);
   }
}
