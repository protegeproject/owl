
package edu.stanford.smi.protegex.owl.swrl.bridge;

import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;

public class Time extends ComplexXSDType
{
  public Time(String content) throws DatatypeConversionException { super(content); }
  protected void validate() throws DatatypeConversionException
  {
    if (getContent() == null) throw new DatatypeConversionException("Null content for Time literal.");
    // TODO: validate
  }  // validate
} // Time
