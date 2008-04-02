
package edu.stanford.smi.protegex.owl.swrl.bridge.xsd;

import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;

public class NOTATION extends PrimitiveXSDType
{
  public NOTATION(String content) throws DatatypeConversionException { super(content); }

  protected void validate() throws DatatypeConversionException
  {
    if (getContent() == null) throw new DatatypeConversionException("Null content for NOTATION literal.");
    // TODO: validate
  }  // validate
} // NOTATION
