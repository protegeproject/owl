
package edu.stanford.smi.protegex.owl.swrl.bridge.xsd;

import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.DatatypeConversionException;

import edu.stanford.smi.protegex.owl.model.XSDNames;
import java.net.URI;

import org.apache.axis.types.Duration;

public class XSDDuration extends XSDType
{
  public XSDDuration(String content) throws DatatypeConversionException 
  { 
    super(content); 

    setURI(XSDNames.DURATION);
  } // XSDDuration

  public XSDDuration(org.apache.axis.types.Duration duration) throws DatatypeConversionException 
  { 
    super(duration.toString()); 

    setURI(XSDNames.DURATION);
  } // XSDDuration

  protected void validate() throws DatatypeConversionException
  {
    if (getContent() == null) throw new DatatypeConversionException("null content for Duration literal");

    if (!XSDTimeUtil.isValidXSDDuration(getContent())) throw new DatatypeConversionException("invalid xsd:Duration '" + getContent() + "'");
  }  // validate

} // XSDDuration

