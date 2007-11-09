
package edu.stanford.smi.protegex.owl.swrl.bridge.xsd;

import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;

public abstract class PrimitiveXSDType implements Comparable
{
  private String content;

  public PrimitiveXSDType(String content) throws DatatypeConversionException
  {
    this.content = content;
    validate();
  } // ComplexXSDType

  public String getContent() { return content; }

  public String toString() { return content; }

  public int compareTo(Object o)
  {
    return content.compareTo(((PrimitiveXSDType)o).getContent()); // Will throw a ClassCastException if o's class does not implement Comparable
  } // compareTo

  protected abstract void validate() throws DatatypeConversionException;
} // PrimitiveXSDType
