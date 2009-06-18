
package edu.stanford.smi.protegex.owl.swrl.bridge.xsd;

import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;

public class AnyURI extends PrimitiveXSDType
{
  public AnyURI(String content) throws DatatypeConversionException { super(content); }

  protected void validate() throws DatatypeConversionException
  {
    if (getContent() == null) throw new DatatypeConversionException("Null content for AnyURI literal.");
    // TODO: validate
  }  // validate
} // AnyURI
