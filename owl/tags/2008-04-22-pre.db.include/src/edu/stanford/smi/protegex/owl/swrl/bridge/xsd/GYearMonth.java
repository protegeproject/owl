
package edu.stanford.smi.protegex.owl.swrl.bridge.xsd;

import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;

public class GYearMonth extends PrimitiveXSDType
{
  public GYearMonth(String content) throws DatatypeConversionException { super(content); }

  protected void validate() throws DatatypeConversionException
  {
    if (getContent() == null) throw new DatatypeConversionException("Null content for GYearMonth literal.");
    // TODO: validate
  }  // validate
} // GYearMonth
