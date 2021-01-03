package com.adcubum.versionmapping.xlscreate.format;

import org.apache.poi.sl.usermodel.Sheet;

/**
 * Contains constants like color, font sizes etc. for formatting a {@link Sheet}. This stuff should probably be moved withina properties
 * file or similar
 * 
 * @author Dominic
 *
 */
public class XLSStyleConst {

   private XLSStyleConst() {
      // private 
   }

   /** Font name for 'calibri' */
   static final String CALIBRI = "Calibri";

   /** Font size for the header cells */
   static final short HEADER_CELL_FONT_SIZE = (short) 13;

   /** Font size for the default content cells */
   static final short DEFAULT_CELL_FONT_SIZE = (short) 11;

   /** RGB definition of the adcubum blue */
   static final byte[] ADC_BLUE = new byte[] {(byte) 0, (byte) 69, (byte) 135};
}
