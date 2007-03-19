
package edu.stanford.smi.protegex.owl.swrl.bridge.builtins.temporal;

import edu.stanford.smi.protegex.owl.swrl.bridge.builtins.temporal.exceptions.*;

import java.text.SimpleDateFormat;

/**
 **  A class supporting processing of datetime string represented in the standard XML Schema datattype date format 'yyyy-MM-ddTHH:mm:ss.SZ'
 */
public class XSDDatetimeStringProcessor extends DatetimeStringProcessor
{
  private static SimpleDateFormat _dateFormat = new SimpleDateFormat("yyyy-MM-ddTHH:mm:ss.SZ");
  private static String _delimiters = " -:.TZ"; // Note the space.

  // The number of tokens (including delimeters) necessary to strip a datetime to a specified granularity.
  private  static int[] _gTokenIndex = { 1, 3, 5, 7, 9, 11, 13 }; // 1=YEARS, 3=MONTHS etc.

  // Strings to pad a partially specified datetime.
  private static String _datetimeRoundDownPadding[] = {"-01-01T00:00:00.000","-01T00:00:00.000","T00:00:00.000",":00:00.000",":00.000",".000", ""};

  // SQL Server is only accurate to 3ms and will round up X.999 to the next second. Day-in-month must be dealt with separately.
  private static String _datetimeRoundUpPadding[] = { "-12-31T23:59:59.997", "", "T23:59:59.997", ":59:59.997", ":59.997", ".997", "" };

  public XSDDatetimeStringProcessor()
  {
    super(_dateFormat, _delimiters, _gTokenIndex, _datetimeRoundDownPadding, _datetimeRoundUpPadding);
  } // XSDDatetimeStringProcessor

} // XSDDatetimeStringProcessor
