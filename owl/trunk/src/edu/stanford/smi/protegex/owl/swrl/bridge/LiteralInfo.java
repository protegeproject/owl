
// Info object representing an RDFS literal. 

package edu.stanford.smi.protegex.owl.swrl.bridge;

import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;

import edu.stanford.smi.protegex.owl.model.*;

public class LiteralInfo extends Info implements Argument
{
  private boolean isNumeric;
  private Object plainValue;
  
  public LiteralInfo(RDFSLiteral literal)
  {
    super("<A literal>");

    isNumeric = literal.getDatatype().isNumericDatatype();
    plainValue = literal.getPlainValue();
  } // LiteralInfo

  public LiteralInfo(Object o)
  {
    super("<A literal>");

    plainValue = o;
    isNumeric = (o instanceof Number);
  } // LiteralInfo

  public LiteralInfo(boolean value)
  {
    super("<A literal>");
    plainValue = new Boolean(value);
    isNumeric = false;
  } // LiteralInfo

  public LiteralInfo(int value)
  {
    super("<A literal>");
    plainValue = new Integer(value);
    isNumeric = true;
  } // LiteralInfo

  public LiteralInfo(float value)
  {
    super("<A literal>");
    plainValue = new Float(value);
    isNumeric = true;
  } // LiteralInfo

  public LiteralInfo(double value)
  {
    super("<A literal>");
    plainValue = new Double(value);
    isNumeric = true;
  } // LiteralInfo

  public String getValue() { return plainValue.toString(); }
  public boolean isNumeric() { return isNumeric; }

  public boolean getBoolean() { return Boolean.valueOf(getValue()); }
  public String getString() { return getValue(); }

  public String toString() { return getString(); }

  public int getInt() throws LiteralConversionException 
  {
    int result = -1;

    if (isInteger()) result = Integer.parseInt(getValue());
    else throw new LiteralConversionException("Literal with value '" + getValue() + "' cannot be converted to an int");

    return result;
  } // getInt

  public long getLong() throws LiteralConversionException 
  {
    long result = -1;

    if (isLong()) result = Long.parseLong(getValue());
    else throw new LiteralConversionException("Literal with value '" + getValue() + "' cannot be converted to an long");

    return result;
  } // getLong

  public float getFloat() throws LiteralConversionException 
  {
    float result = -1;

    if (isFloat()) result = Float.parseFloat(getValue());
    else throw new LiteralConversionException("Literal with value '" + getValue() + "' cannot be converted to a float");

    return result;
  } // getFloat

  public double getDouble() throws LiteralConversionException 
  {
    double result = -1;

    if (isDouble()) result = Double.parseDouble(getValue());
    else throw new LiteralConversionException("Literal with value '" + getValue() + "' cannot be converted to a double");

    return result;
  } // getDouble

  public boolean isInteger() { return plainValue instanceof Integer; }
  public boolean isLong() { return plainValue instanceof Long; }
  public boolean isBoolean() { return plainValue instanceof Boolean; }
  public boolean isFloat() { return plainValue instanceof Float; }
  public boolean isDouble() { return plainValue instanceof Double; }
  public boolean isString() { return plainValue instanceof String; }

  public boolean equals(Object obj)
  {
    if(this == obj) return true;
    if((obj == null) || (obj.getClass() != this.getClass())) return false;
    LiteralInfo info = (LiteralInfo)obj;
    return (getName() == info.getName() || (getName() != null && getName().equals(info.getName()))) &&
      (isNumeric == info.isNumeric()) &&
      ((plainValue == info.plainValue) || (plainValue != null && info.plainValue != null && 
                                           plainValue.toString().equals(info.plainValue.toString())));
  } // equals

  public int hashCode()
  {
    int hash = 66;
    hash = hash + (null == getName() ? 0 : getName().hashCode());
    hash = hash + (isNumeric ? 0 : 1);
    hash = hash + (null == plainValue ? 0 : plainValue.toString().hashCode());
    return hash;
  } // hashCode

} // LiteralInfo
