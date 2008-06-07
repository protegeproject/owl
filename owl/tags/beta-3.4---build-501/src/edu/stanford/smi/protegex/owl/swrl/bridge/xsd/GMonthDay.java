
package edu.stanford.smi.protegex.owl.swrl.bridge.xsd;

import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;

public class GMonthDay extends PrimitiveXSDType
{
  public GMonthDay(String content) throws DatatypeConversionException { super(content); }

  protected void validate() throws DatatypeConversionException
  {
    if (getContent() == null) throw new DatatypeConversionException("Null content for GMonthDay literal.");
    // TODO: validate
  }  // validate
} // GMonthDay
