
package edu.stanford.smi.protegex.owl.swrl.bridge.xsd;

import java.net.URISyntaxException;

import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.DatatypeConversionException;

public abstract class XSDType implements Comparable
{
  private String content;
  private java.net.URI uri = null;

  public XSDType(String content) throws DatatypeConversionException
  {
    this.content = content;
    validate();
  } // XSDType

  public String getContent() { return content; }

  public String toString() { return content; }

  public java.net.URI getURI() { return uri; }

  public int compareTo(Object o)
  {
    return content.compareTo(((XSDType)o).getContent()); // Will throw a ClassCastException if o's class does not implement Comparable
  } // compareTo

  protected abstract void validate() throws DatatypeConversionException;

  protected void setURI(String uriString) throws DatatypeConversionException
  {
    try {
      uri = new java.net.URI(uriString);
    } catch (URISyntaxException e) {
      throw new DatatypeConversionException("invalid URI '" + uri + "' associated with value '" + content + "'");
    } // try
  } // setURI

} // XSDType
