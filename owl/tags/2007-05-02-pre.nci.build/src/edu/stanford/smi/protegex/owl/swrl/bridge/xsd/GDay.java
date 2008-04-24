
package edu.stanford.smi.protegex.owl.swrl.bridge.xsd;

import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;

public class GDay extends PrimitiveXSDType
{
  public GDay(String content) throws DatatypeConversionException { super(content); }

  protected void validate() throws DatatypeConversionException
  {
    if (getContent() == null) throw new DatatypeConversionException("Null content for GDay literal.");
    // TODO: validate
  }  // validate
} // GDay
