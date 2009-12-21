
package edu.stanford.smi.protegex.owl.swrl.bridge.impl;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import edu.stanford.smi.protegex.owl.swrl.bridge.OWLDatatypeValue;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.DatatypeConversionException;
import edu.stanford.smi.protegex.owl.swrl.bridge.xsd.XSDAnyURI;
import edu.stanford.smi.protegex.owl.swrl.bridge.xsd.XSDDate;
import edu.stanford.smi.protegex.owl.swrl.bridge.xsd.XSDDateTime;
import edu.stanford.smi.protegex.owl.swrl.bridge.xsd.XSDDuration;
import edu.stanford.smi.protegex.owl.swrl.bridge.xsd.XSDTime;
import edu.stanford.smi.protegex.owl.swrl.bridge.xsd.XSDType;

/**
 ** Implementation of an OWLDatatypeValue object that represents Java and XML Schema primitive data literals.
 */
public class OWLDatatypeValueImpl extends BuiltInArgumentImpl implements OWLDatatypeValue, Externalizable {
  private Object value; // This value object should implement Comparable.

  public OWLDatatypeValueImpl() { value = null; } 
  public OWLDatatypeValueImpl(String s) { value = s; } 
  public OWLDatatypeValueImpl(Number n) { value = n; }
  public OWLDatatypeValueImpl(boolean b) { value = Boolean.valueOf(b); }
  public OWLDatatypeValueImpl(int i) { value = Integer.valueOf(i); }
  public OWLDatatypeValueImpl(long l) { value = Long.valueOf(l); }
  public OWLDatatypeValueImpl(float f) { value = Float.valueOf(f); }
  public OWLDatatypeValueImpl(double d) { value = Double.valueOf(d); }
  public OWLDatatypeValueImpl(short s) { value = Short.valueOf(s); }
  public OWLDatatypeValueImpl(XSDType value) { this.value = value; }

  public OWLDatatypeValueImpl(Object o) throws DatatypeConversionException
  { 
    if (!((o instanceof Number) || (o instanceof String) || (o instanceof Boolean) || (o instanceof XSDType)))
      throw new DatatypeConversionException("cannot convert value of type '" + o.getClass().getCanonicalName() + "' to OWLDatatypeValue"); 

    value = o;
  } // OWLDatatypeValueImpl

  public boolean isString() { return value instanceof String; }
  public boolean isBoolean() { return value instanceof Boolean; }
  public boolean isNumeric() { return value instanceof Number; }
  public boolean isInteger() { return value instanceof Integer; }
  public boolean isLong() { return value instanceof Long; }
  public boolean isFloat() { return value instanceof Float; }
  public boolean isDouble() { return value instanceof Double; }
  public boolean isShort() { return value instanceof Short; }
  public boolean isByte() { return value instanceof Byte; }
  
  // XSD types
  public boolean isXSDType() { return value instanceof XSDType; }
  public boolean isXSDTime() { return value instanceof XSDTime; }
  public boolean isXSDDate() { return value instanceof XSDDate; }
  public boolean isXSDDateTime() { return value instanceof XSDDateTime; }
  public boolean isXSDDuration() { return value instanceof XSDDuration;}
  public boolean isXSDAnyURI() { return value instanceof XSDAnyURI; }
  
  public boolean isComparable() { return isNumeric() || isString() || isXSDTime() || isXSDDateTime() || isXSDDuration(); }

  public String getString() throws DatatypeConversionException 
  { 
    if (!isString()) 
      throw new DatatypeConversionException("cannot convert value of type '" + value.getClass().getCanonicalName() + "' to String"); 
    return (String)value; 
  } // getString

  public Number getNumber() throws DatatypeConversionException 
  { 
    if (!isNumeric()) 
      throw new DatatypeConversionException("cannot convert value of type '" + value.getClass().getCanonicalName() + "' to Number"); 
    return (Number)value; 
  } // getNumber

  public XSDType getSDType() throws DatatypeConversionException 
  { 
    if (!isXSDType()) 
      throw new DatatypeConversionException("cannot convert value of type '" + value.getClass().getCanonicalName() + "' to XSDType"); 
    return (XSDType)value; 
  } // getPrimitiveXSDType

  public boolean getBoolean() throws DatatypeConversionException 
  { 
    if (!isBoolean()) 
      throw new DatatypeConversionException("cannot convert value of type '" + value.getClass().getCanonicalName() + "' to boolean"); 

    return ((Boolean)value).booleanValue(); 
  } // getBoolean

  public int getInt() throws DatatypeConversionException 
  {
    int result = 0;

    if (isInteger()) result = ((Integer)value).intValue(); 
    else if (isShort()) result = (int)((Short)value).shortValue();
    else throw new DatatypeConversionException("cannot convert value of type '" + value.getClass().getCanonicalName() + "' to int"); 

    return result;
  } // getInt

  public long getLong() throws DatatypeConversionException 
  { 
    long result = 0;

    if (isLong()) result = ((Long)value).longValue(); 
    else if (isInteger()) result = (long)((Integer)value).intValue();
    else if (isShort()) result = (long)((Short)value).shortValue();
    else throw new DatatypeConversionException("cannot convert value of type '" + value.getClass().getCanonicalName() + "' to long"); 

    return result;
  } // getLong

  // Some precision loss possible going from integer and long to
  // float. cf. http://www.particle.kth.se/~lindsey/JavaCourse/Book/Part1/Java/Chapter02/castsMixing.html
  public float getFloat() throws DatatypeConversionException 
  { 
    float result = 0;

    if (isFloat()) result = ((Float)value).floatValue(); 
    else if (isInteger()) result = (float)((Integer)value).intValue();
    else if (isLong()) result = (float)((Long)value).longValue();
    else if (isShort()) result = (float)((Short)value).shortValue();
    else throw new DatatypeConversionException("cannot convert value of type '" + value.getClass().getCanonicalName() + "' to float"); 

    return result;
  } // getFloat

  // Some precision loss possible going from long to
  // double. cf. http://www.particle.kth.se/~lindsey/JavaCourse/Book/Part1/Java/Chapter02/castsMixing.html
  public double getDouble() throws DatatypeConversionException 
  { 
    double result = 0.0;

    if (isDouble()) result = ((Double)value).doubleValue(); 
    else if (isFloat()) result = (double)((Float)value).floatValue();
    else if (isInteger()) result = (double)((Integer)value).intValue();
    else if (isLong()) result = (double)((Long)value).longValue();
    else if (isShort()) result = (double)((Short)value).shortValue();
    else throw new DatatypeConversionException("cannot convert value of type '" + value.getClass().getCanonicalName() + "' to double"); 

    return result;
  } // getDouble

  public short getShort() throws DatatypeConversionException 
  { 
    if (!isShort()) 
      throw new DatatypeConversionException("cannot convert value of type '" + value.getClass().getCanonicalName() + "' to short"); 
    return ((Short)value).shortValue(); 
  } // getShort

  public byte getByte() throws DatatypeConversionException 
  {
    if (!isByte()) 
      throw new DatatypeConversionException("cannot convert value of type '" + value.getClass().getCanonicalName() + "' to byte"); 
    return ((java.lang.Byte)value).byteValue();
  } // getByte

  // The caller can decide to quote or not.
  public String toString() 
  { 
    return "" + value;
  } // toString

  public String getValueClassName() { return value.getClass().getName(); }
  public Object getValue() { return value; }
  private void setValue(Object value) { this.value = value; }

  public boolean equals(Object obj)
  {
    if(this == obj) return true;
    if((obj == null) || (obj.getClass() != this.getClass())) return false;
    OWLDatatypeValueImpl info = (OWLDatatypeValueImpl)obj;
    return (value != null && info.value != null && value.toString().equals(info.value.toString()));
  } // equals

  public int hashCode()
  {
    int hash = 66;
    hash = hash + (null == value ? 0 : value.toString().hashCode());
    return hash;
  } // hashCode

  public int compareTo(Object o)
  {
    return  ((Comparable)value).compareTo(((OWLDatatypeValueImpl)o).getValue()); // Will throw a ClassCastException if o's class does not implement Comparable.
  } // compareTo

  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
  {
    setValue(in.readObject());
  } // readExternal

  public void writeExternal(ObjectOutput out) throws IOException
  {
    out.writeObject(getValue());
  } // writeExternal

} // OWLDatatypeValueImpl
