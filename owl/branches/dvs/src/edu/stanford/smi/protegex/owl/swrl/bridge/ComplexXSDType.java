
package edu.stanford.smi.protegex.owl.swrl.bridge;

import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;

public abstract class ComplexXSDType implements Comparable
{
  private String content;

  public ComplexXSDType(String content) throws DatatypeConversionException
  {
    this.content = content;
    validate();
  } // ComplexXSDType

  public String getContent() { return content; }

  public int compareTo(Object o)
  {
    return content.compareTo(((ComplexXSDType)o).getContent()); // Will throw a ClassCastException if o's class does not implement Comparable
  } // compareTo

  protected abstract void validate() throws DatatypeConversionException;
} // ComplexXSDType
