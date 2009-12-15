
package edu.stanford.smi.protegex.owl.swrl.bridge.xsd;

import edu.stanford.smi.protegex.owl.model.XSDNames;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.DatatypeConversionException;

public class XSDDate extends XSDType
{
  public XSDDate(String content) throws DatatypeConversionException 
  { 
    super(content); 

    setURI(XSDNames.DATE);
  } // XSDDate

  public XSDDate(java.util.Date date) throws DatatypeConversionException 
  { 
    super(XSDTimeUtil.date2XSDDateString(date)); 

    setURI(XSDNames.DATE);
  } // XSDDate

  protected void validate() throws DatatypeConversionException
  {
    if (getContent() == null) throw new DatatypeConversionException("null content for xsd:Date");

    if (!XSDTimeUtil.isValidXSDDate(getContent())) throw new DatatypeConversionException("invalid xsd:Date '" + getContent() + "'");
  } // validate

} // XSDDate

