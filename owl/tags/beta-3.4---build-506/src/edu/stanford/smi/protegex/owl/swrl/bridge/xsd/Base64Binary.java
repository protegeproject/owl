
package edu.stanford.smi.protegex.owl.swrl.bridge.xsd;

import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;

public class Base64Binary extends PrimitiveXSDType
{
  public Base64Binary(String content) throws DatatypeConversionException { super(content); }

  protected void validate() throws DatatypeConversionException
  {
    if (getContent() == null) throw new DatatypeConversionException("Null content for Base64Binary literal.");
    // TODO: validate
  }  // validate
} // Base64Binary
