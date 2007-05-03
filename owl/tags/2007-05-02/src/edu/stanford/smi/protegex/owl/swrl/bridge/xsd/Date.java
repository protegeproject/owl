
package edu.stanford.smi.protegex.owl.swrl.bridge.xsd;

import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;

public class Date extends PrimitiveXSDType
{
  public Date(String content) throws DatatypeConversionException { super(content); }
  protected void validate() throws DatatypeConversionException
  {
    if (getContent() == null) throw new DatatypeConversionException("Null content for Time literal.");
    // TODO: validate
  } // validate

} // Date
