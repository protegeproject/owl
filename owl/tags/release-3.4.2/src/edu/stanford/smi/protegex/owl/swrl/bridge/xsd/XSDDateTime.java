
package edu.stanford.smi.protegex.owl.swrl.bridge.xsd;

import edu.stanford.smi.protegex.owl.model.XSDNames;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.DatatypeConversionException;

public class XSDDateTime extends XSDType
{
  public XSDDateTime(String content) throws DatatypeConversionException 
  { 
    super(content); 

    setURI(XSDNames.DATE_TIME);
  } // XSDDateTime

  public XSDDateTime(java.util.Date date) throws DatatypeConversionException
  { 
    super(XSDTimeUtil.date2XSDDateTimeString(date)); 

    setURI(XSDNames.DATE_TIME);
  } // XSDDateTime

  protected void validate() throws DatatypeConversionException
  {
    if (getContent() == null) throw new DatatypeConversionException("null content for xsd:DateTime");

    if (!XSDTimeUtil.isValidXSDDateTime(getContent())) throw new DatatypeConversionException("invalid xsd:DateTime '" + getContent() + "'");
  } // validate

} // XSDDateTime
