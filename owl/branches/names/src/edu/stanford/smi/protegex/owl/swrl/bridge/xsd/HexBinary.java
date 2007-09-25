
package edu.stanford.smi.protegex.owl.swrl.bridge.xsd;

import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;

public class HexBinary extends PrimitiveXSDType
{
  public HexBinary(String content) throws DatatypeConversionException { super(content); }

  protected void validate() throws DatatypeConversionException
  {
    if (getContent() == null) throw new DatatypeConversionException("Null content for HexBinary literal.");
    // TODO: validate
  }  // validate
} // HexBinary
