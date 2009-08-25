
package edu.stanford.smi.protegex.owl.swrl.bridge.xsd;

import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.DatatypeConversionException;

import edu.stanford.smi.protegex.owl.model.XSDNames;

import java.util.Date;
import java.net.URI;

public class XSDTime extends XSDType
{
  public XSDTime(String content) throws DatatypeConversionException 
  { 
    super(content); 

    setURI(XSDNames.TIME);
  } // XSDTime

  public XSDTime(java.util.Date date) throws DatatypeConversionException 
  { 
    super(XSDTimeUtil.date2XSDTimeString(date)); 

    setURI(XSDNames.TIME);
  } // XSDTime

  protected void validate() throws DatatypeConversionException
  {
    if (getContent() == null) throw new DatatypeConversionException("null content for xsd:Time");

    if (!XSDTimeUtil.isValidXSDTime(getContent())) throw new DatatypeConversionException("invalid xsd:Time '" + getContent() + "'");
  } // validate

} // XSDTime

