
package edu.stanford.smi.protegex.owl.swrl.bridge.xsd;

import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;

public class Duration extends PrimitiveXSDType
{
  public Duration(String content) throws DatatypeConversionException { super(content); }
  protected void validate() throws DatatypeConversionException
  {
    if (getContent() == null) throw new DatatypeConversionException("Null content for Duration literal.");
    // TODO: validate
  }  // validate
} // Duration
