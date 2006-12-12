
package edu.stanford.smi.protegex.owl.swrl.bridge;

import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;
import edu.stanford.smi.protegex.owl.model.*;

/*
** Info object to wrap XML Schema datatype literals. This class is used primarily when passing literals to and from built-in methods. Also
** provides a central place to validate the content of complex types, such as datetimes, etc.
*/
public class LiteralInfo extends Info implements Argument, DatatypeValue, Comparable
{
  private Object value; // This value object should implement comparable.

  public LiteralInfo(OWLModel owlModel, RDFSLiteral literal) throws DatatypeConversionException
  {
    RDFSDatatype datatype = literal.getDatatype();

    if ((datatype == owlModel.getXSDint()) || (datatype == owlModel.getXSDinteger()))  value = new Integer(literal.getInt());
    else if (datatype == owlModel.getXSDshort()) value = new Short(literal.getShort());
    else if (datatype == owlModel.getXSDlong()) value = new Long(literal.getLong());
    else if (datatype == owlModel.getXSDboolean()) value = new Boolean(literal.getBoolean());
    else if (datatype == owlModel.getXSDfloat()) value = new Float(literal.getFloat());
    else if (datatype == owlModel.getXSDdouble()) value = new Double(literal.getDouble());
    else if ((datatype == owlModel.getXSDstring())) value = new String(literal.getString());
    else if ((datatype == owlModel.getXSDtime())) value = new Time(literal.getString());
    else if ((datatype == owlModel.getXSDanyURI())) value = new AnyURI(literal.getString());
    else if ((datatype == owlModel.getXSDbase64Binary())) value = new Base64Binary(literal.getString());
    else if ((datatype == owlModel.getXSDdecimal())) value = new Decimal(literal.getString());
    else if ((datatype == owlModel.getXSDbyte())) value = new Byte(literal.getString());
    else if ((datatype == owlModel.getXSDduration())) value = new Duration(literal.getString());
    else if ((datatype == owlModel.getXSDdateTime())) value = new DateTime(literal.getString());
    else if ((datatype == owlModel.getXSDdate())) value = new Date(literal.getString());
    else throw new DatatypeConversionException("Cannot create LiteralInfo object for RDFS literal '" + literal.getString()
                                               + "' of type '" + datatype + "'.");
  } // LiteralInfo

  public LiteralInfo(String s)
  {
    value = s;
  } // LiteralInfo

  public LiteralInfo(Number n)
  {
    value = n;
  } // LiteralInfo

  public LiteralInfo(boolean b)
  {
    value = new Boolean(b);
  } // LiteralInfo

  public LiteralInfo(int i)
  {
    value = new Integer(i);
  } // LiteralInfo

  public LiteralInfo(float f)
  {
    value = new Float(f);
  } // LiteralInfo

  public LiteralInfo(double d)
  {
    value = new Double(d);
  } // LiteralInfo

  public LiteralInfo(short s)
  {
    value = new Short(s);
  } // LiteralInfo

  public LiteralInfo(ComplexXSDType value)
  {
    this.value = value;
  } // LiteralInfo

  public boolean isInteger() { return value instanceof Integer; }
  public boolean isLong() { return value instanceof Long; }
  public boolean isBoolean() { return value instanceof Boolean; }
  public boolean isFloat() { return value instanceof Float; }
  public boolean isDouble() { return value instanceof Double; }
  public boolean isShort() { return value instanceof Short; }
  public boolean isString() { return value instanceof String; }
  public boolean isTime() { return value instanceof edu.stanford.smi.protegex.owl.swrl.bridge.Time; }
  public boolean isDate() { return value instanceof edu.stanford.smi.protegex.owl.swrl.bridge.Date; }
  public boolean isDateTime() { return value instanceof edu.stanford.smi.protegex.owl.swrl.bridge.DateTime; }
  public boolean isDuration() { return value instanceof edu.stanford.smi.protegex.owl.swrl.bridge.Duration;}
  public boolean isAnyURI() { return value instanceof edu.stanford.smi.protegex.owl.swrl.bridge.AnyURI; }
  public boolean isBase64Binary() { return value instanceof edu.stanford.smi.protegex.owl.swrl.bridge.Base64Binary;}
  public boolean isDecimal() { return value instanceof edu.stanford.smi.protegex.owl.swrl.bridge.Decimal; }
  public boolean isByte() { return value instanceof edu.stanford.smi.protegex.owl.swrl.bridge.Byte; }

  public int getInt() throws DatatypeConversionException 
  {
    if (!isInteger()) 
      throw new DatatypeConversionException("Cannot convert datatype value of type '" + value.getClass().getCanonicalName() + "' to int"); 
    return ((Integer)value).intValue(); 
  } // getInt

  public boolean getBoolean() throws DatatypeConversionException 
  { 
    if (!isBoolean()) 
      throw new DatatypeConversionException("Cannot convert datatype value of type '" + value.getClass().getCanonicalName() + "' to boolean"); 
    return ((Boolean)value).booleanValue(); 
  } // getBoolean

  public long getLong() throws DatatypeConversionException 
  { 
    if (!isLong()) 
      throw new DatatypeConversionException("Cannot convert datatype value of type '" + value.getClass().getCanonicalName() + "' to long"); 
    return ((Long)value).longValue(); 
  } // getLong

  public float getFloat() throws DatatypeConversionException 
  { 
    if (!isFloat()) 
      throw new DatatypeConversionException("Cannot convert datatype value of type '" + value.getClass().getCanonicalName() + "' to float"); 
    return ((Float)value).floatValue(); 
  } // getFloat

  public double getDouble() throws DatatypeConversionException 
  { 
    if (!isDouble()) 
      throw new DatatypeConversionException("Cannot convert datatype value of type '" + value.getClass().getCanonicalName() + "' to fouble"); 
    return ((Double)value).doubleValue(); 
  } // getDouble

  public short getShort() throws DatatypeConversionException 
  { 
    if (!isShort()) 
      throw new DatatypeConversionException("Cannot convert datatype value of type '" + value.getClass().getCanonicalName() + "' to sqhort"); 
    return ((Short)value).shortValue(); 
  } // getShort

  public String getString() throws DatatypeConversionException 
  { 
    if (!isString()) 
      throw new DatatypeConversionException("Cannot convert datatype value of type '" + value.getClass().getCanonicalName() + "' to String"); 
    return (String)value; 
  } // getString

  public edu.stanford.smi.protegex.owl.swrl.bridge.Time getTime() throws DatatypeConversionException 
  {
    if (!isTime()) 
      throw new DatatypeConversionException("Cannot convert datatype value of type '" + value.getClass().getCanonicalName() + "' to Time"); 
    return (edu.stanford.smi.protegex.owl.swrl.bridge.Time)value;
  } // getTime

  public edu.stanford.smi.protegex.owl.swrl.bridge.Date getDate() throws DatatypeConversionException 
  {
    if (!isDate()) 
      throw new DatatypeConversionException("Cannot convert datatype value of type '" + value.getClass().getCanonicalName() + "' to Date"); 
    return (edu.stanford.smi.protegex.owl.swrl.bridge.Date)value;
  } // getDate

  public edu.stanford.smi.protegex.owl.swrl.bridge.DateTime getDateTime() throws DatatypeConversionException 
  { 
    if (!isDateTime()) 
      throw new DatatypeConversionException("Cannot convert datatype value of type '" + value.getClass().getCanonicalName() + "' to DateTime"); 

    return (edu.stanford.smi.protegex.owl.swrl.bridge.DateTime)value;
  } // getDateTime

  public edu.stanford.smi.protegex.owl.swrl.bridge.Duration getDuration() throws DatatypeConversionException
  {
    if (!isDuration()) 
      throw new DatatypeConversionException("Cannot convert datatype value of type '" + value.getClass().getCanonicalName() + "' to Duration"); 
    return  (edu.stanford.smi.protegex.owl.swrl.bridge.Duration)value; 
  } // getDuration

  public edu.stanford.smi.protegex.owl.swrl.bridge.AnyURI getAnyURI() throws DatatypeConversionException 
  {
    if (!isAnyURI()) 
      throw new DatatypeConversionException("Cannot convert datatype value of type '" + value.getClass().getCanonicalName() + "' to AnyURI"); 
    return (edu.stanford.smi.protegex.owl.swrl.bridge.AnyURI)value;
  } // getAnyURI

  public edu.stanford.smi.protegex.owl.swrl.bridge.Base64Binary getBase64Binary() throws DatatypeConversionException 
  {
    if (!isBase64Binary()) 
      throw new DatatypeConversionException("Cannot convert datatype value of type '" + value.getClass().getCanonicalName() + "' to Base64Binary"); 
    return (edu.stanford.smi.protegex.owl.swrl.bridge.Base64Binary)value;
  } // getBase64Binary

  public edu.stanford.smi.protegex.owl.swrl.bridge.Decimal getDecimal() throws DatatypeConversionException 
  {
    if (!isDecimal()) 
      throw new DatatypeConversionException("Cannot convert datatype value of type '" + value.getClass().getCanonicalName() + "' to Decimal"); 
    return (edu.stanford.smi.protegex.owl.swrl.bridge.Decimal)value;
  } // getDecimal

  public edu.stanford.smi.protegex.owl.swrl.bridge.Byte getByte() throws DatatypeConversionException 
  {
    if (!isByte()) 
      throw new DatatypeConversionException("Cannot convert datatype value of type '" + value.getClass().getCanonicalName() + "' to Byte"); 
    return (edu.stanford.smi.protegex.owl.swrl.bridge.Byte)value;
  } // getByte

  public boolean isNumeric() { return value instanceof Number; }
  public String toString() { return value.toString(); }

  public boolean equals(Object obj)
  {
    if(this == obj) return true;
    if((obj == null) || (obj.getClass() != this.getClass())) return false;
    LiteralInfo info = (LiteralInfo)obj;
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
    return  ((Comparable)o).compareTo(o); // Will throw a ClassCastException if o's class does not implement Comparable.
  } // compareTo

} // LiteralInfo
