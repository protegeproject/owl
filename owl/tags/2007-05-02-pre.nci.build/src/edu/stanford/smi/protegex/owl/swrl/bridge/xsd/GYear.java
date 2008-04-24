
package edu.stanford.smi.protegex.owl.swrl.bridge.xsd;

import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;

public class GYear extends PrimitiveXSDType
{
  public GYear(String content) throws DatatypeConversionException { super(content); }

  protected void validate() throws DatatypeConversionException
  {
    if (getContent() == null) throw new DatatypeConversionException("Null content for GYear literal.");
    // TODO: validate
  }  // validate
} // GYear
