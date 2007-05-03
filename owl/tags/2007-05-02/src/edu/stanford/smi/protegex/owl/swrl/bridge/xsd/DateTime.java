
package edu.stanford.smi.protegex.owl.swrl.bridge.xsd;

import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;

public class DateTime extends PrimitiveXSDType
{
  public DateTime(String content) throws DatatypeConversionException { super(content); }
  protected void validate() throws DatatypeConversionException
  {
    if (getContent() == null) throw new DatatypeConversionException("Null content for DateTime literal.");
    // TODO: validate
  }  // validate
} // DateTime
