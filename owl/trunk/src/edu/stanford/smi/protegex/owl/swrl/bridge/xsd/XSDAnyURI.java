
package edu.stanford.smi.protegex.owl.swrl.bridge.xsd;

import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.DatatypeConversionException;

import edu.stanford.smi.protegex.owl.model.XSDNames;

public class XSDAnyURI extends XSDType
{
  public XSDAnyURI(String content) throws DatatypeConversionException 
  { 
    super(content); 

    setURI(XSDNames.ANY_URI);
  } // XSDAnyURI

  protected void validate() throws DatatypeConversionException
  {
    if (getContent() == null) throw new DatatypeConversionException("null content for AnyURI literal");
    // TODO: validate
  }  // validate
} // XSDAnyURI
