
package edu.stanford.smi.protegex.owl.swrl.bridge;

import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.xsd.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.query.*;
import edu.stanford.smi.protegex.owl.model.*;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 ** Info object to wrap Java and XML Schema primitve datatype literals.
 **
 ** For the moment we do not support (1) RDFSLiteral language tags, and (2) the types: byte[], and primitive XSD types Decimal, GDay,
 ** GYear, GMonth, GMonthDay, QName, HexBinary, and NOTATION.
 */
public class LiteralInfo extends Info implements Argument, DatatypeValue
{
  private Object value; // This value object should implement Comparable.

  /**
   ** Convert an RDFSLiteral to a LiteralInfo. 
   */
  public LiteralInfo(OWLModel owlModel, RDFSLiteral literal) throws DatatypeConversionException
  {
    RDFSDatatype datatype = literal.getDatatype();

    if ((datatype == owlModel.getXSDint()) || (datatype == owlModel.getXSDinteger()))  value = Integer.valueOf(literal.getInt());
    else if (datatype == owlModel.getXSDshort()) value = Short.valueOf(literal.getShort());
    else if (datatype == owlModel.getXSDlong()) value = Long.valueOf(literal.getLong());
    else if (datatype == owlModel.getXSDboolean()) value = Boolean.valueOf(literal.getBoolean());
    else if (datatype == owlModel.getXSDfloat()) value = Float.valueOf(literal.getFloat());
    else if (datatype == owlModel.getXSDdouble()) value = Double.valueOf(literal.getDouble());
    else if ((datatype == owlModel.getXSDstring())) value = String.valueOf(literal.getString());
    else if ((datatype == owlModel.getXSDtime())) value = new Time(literal.getString());
    else if ((datatype == owlModel.getXSDanyURI())) value = new AnyURI(literal.getString());
    else if ((datatype == owlModel.getXSDbase64Binary())) value = new Base64Binary(literal.getString());
    else if ((datatype == owlModel.getXSDbyte())) value = Byte.valueOf(literal.getString());
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
    value = Boolean.valueOf(b);
  } // LiteralInfo

  public LiteralInfo(int i)
  {
    value = Integer.valueOf(i);
  } // LiteralInfo

  public LiteralInfo(long l)
  {
    value = Long.valueOf(l);
  } // LiteralInfo

  public LiteralInfo(float f)
  {
    value = Float.valueOf(f);
  } // LiteralInfo

  public LiteralInfo(double d)
  {
    value = Double.valueOf(d);
  } // LiteralInfo

  public LiteralInfo(short s)
  {
    value = Short.valueOf(s);
  } // LiteralInfo

  public LiteralInfo(PrimitiveXSDType value)
  {
    this.value = value;
  } // LiteralInfo

  // Java String type
  public boolean isString() { return value instanceof String; }

  public boolean isBoolean() { return value instanceof Boolean; }

  // Java Number types
  public boolean isNumeric() { return value instanceof Number; }

  public boolean isInteger() { return value instanceof Integer; }
  public boolean isLong() { return value instanceof Long; }
  public boolean isFloat() { return value instanceof Float; }
  public boolean isDouble() { return value instanceof Double; }
  public boolean isShort() { return value instanceof Short; }
  public boolean isByte() { return value instanceof Byte; }
  public boolean isBigDecimal() { return value instanceof BigDecimal; }
  public boolean isBigInteger() { return value instanceof BigInteger; }
  
  // Primitive XSD types
  public boolean isPrimitiveXSDType() { return value instanceof edu.stanford.smi.protegex.owl.swrl.bridge.xsd.PrimitiveXSDType; }

  public boolean isTime() { return value instanceof edu.stanford.smi.protegex.owl.swrl.bridge.xsd.Time; }
  public boolean isDate() { return value instanceof edu.stanford.smi.protegex.owl.swrl.bridge.xsd.Date; }
  public boolean isDateTime() { return value instanceof edu.stanford.smi.protegex.owl.swrl.bridge.xsd.DateTime; }
  public boolean isDuration() { return value instanceof edu.stanford.smi.protegex.owl.swrl.bridge.xsd.Duration;}
  public boolean isAnyURI() { return value instanceof edu.stanford.smi.protegex.owl.swrl.bridge.xsd.AnyURI; }
  public boolean isBase64Binary() { return value instanceof edu.stanford.smi.protegex.owl.swrl.bridge.xsd.Base64Binary; }
  public boolean isHexBinary() { return value instanceof edu.stanford.smi.protegex.owl.swrl.bridge.xsd.HexBinary; }
  public boolean isGMonth() { return value instanceof edu.stanford.smi.protegex.owl.swrl.bridge.xsd.GMonth; }
  public boolean isGYear() { return value instanceof edu.stanford.smi.protegex.owl.swrl.bridge.xsd.GYear; }
  public boolean isGYearMonth() { return value instanceof edu.stanford.smi.protegex.owl.swrl.bridge.xsd.GYearMonth; }
  public boolean isGDay() { return value instanceof edu.stanford.smi.protegex.owl.swrl.bridge.xsd.GDay; }
  public boolean isGMonthDay() { return value instanceof edu.stanford.smi.protegex.owl.swrl.bridge.xsd.GMonthDay; }
  public boolean isNOTATION() { return value instanceof edu.stanford.smi.protegex.owl.swrl.bridge.xsd.NOTATION; }
  public boolean isQName() { return value instanceof edu.stanford.smi.protegex.owl.swrl.bridge.xsd.QName; }

  public String getString() throws DatatypeConversionException 
  { 
    if (!isString()) 
      throw new DatatypeConversionException("Cannot convert value of type '" + value.getClass().getCanonicalName() + "' to String"); 
    return (String)value; 
  } // getString

  public Number getNumber() throws DatatypeConversionException 
  { 
    if (!isNumeric()) 
      throw new DatatypeConversionException("Cannot convert value of type '" + value.getClass().getCanonicalName() + "' to Number"); 
    return (Number)value; 
  } // getNumber

  public PrimitiveXSDType getPrimitiveXSDType() throws DatatypeConversionException 
  { 
    if (!isPrimitiveXSDType()) 
      throw new DatatypeConversionException("Cannot convert value of type '" + value.getClass().getCanonicalName() + "' to PrimitiveXSDType"); 
    return (PrimitiveXSDType)value; 
  } // getPrimitiveXSDType

  public boolean getBoolean() throws DatatypeConversionException 
  { 
    if (!isBoolean()) 
      throw new DatatypeConversionException("Cannot convert value of type '" + value.getClass().getCanonicalName() + "' to boolean"); 

    return ((Boolean)value).booleanValue(); 
  } // getBoolean

  public int getInt() throws DatatypeConversionException 
  {
    int result = 0;;

    if (isInteger()) result = ((Integer)value).intValue(); 
    else if (isShort()) result = (int)((Short)value).shortValue();
    else throw new DatatypeConversionException("Cannot convert value of type '" + value.getClass().getCanonicalName() + "' to int"); 

    return result;
  } // getInt

  public long getLong() throws DatatypeConversionException 
  { 
    long result = 0;

    if (isLong()) result = ((Long)value).longValue(); 
    else if (isInteger()) result = (long)((Integer)value).intValue();
    else if (isShort()) result = (long)((Short)value).shortValue();
    else throw new DatatypeConversionException("Cannot convert value of type '" + value.getClass().getCanonicalName() + "' to long"); 

    return result;
  } // getLong

  public float getFloat() throws DatatypeConversionException 
  { 
    float result = 0;

    if (isFloat()) result = ((Float)value).floatValue(); 
    else if (isInteger()) result = (float)((Integer)value).intValue();
    else if (isShort()) result = (float)((Short)value).shortValue();
    else throw new DatatypeConversionException("Cannot convert value of type '" + value.getClass().getCanonicalName() + "' to float"); 

    return result;
  } // getFloat

  public double getDouble() throws DatatypeConversionException 
  { 
    double result = 0.0;

    if (isDouble()) result = ((Double)value).doubleValue(); 
    else if (isFloat()) result = (double)((Float)value).floatValue();
    else if (isInteger()) result = (double)((Integer)value).intValue();
    else if (isLong()) result = (double)((Long)value).longValue();
    else if (isShort()) result = (double)((Short)value).shortValue();
    else throw new DatatypeConversionException("Cannot convert value of type '" + value.getClass().getCanonicalName() + "' to double"); 

    return result;
  } // getDouble

  public BigDecimal getBigDecimal() throws DatatypeConversionException 
  {
    BigDecimal result = null;

    if (isBigDecimal()) result = (BigDecimal)value;
    else if (isBigInteger()) result = new BigDecimal((BigInteger)value);
    if (isDouble()) result = new BigDecimal(getDouble());
    else if (isFloat()) result = new BigDecimal(getDouble());
    else if (isInteger()) result = new BigDecimal(getDouble());
    else if (isLong()) result = new BigDecimal(getDouble());
    else if (isShort()) result = new BigDecimal(getDouble());
    else throw new DatatypeConversionException("Cannot convert value of type '" + value.getClass().getCanonicalName() + "' to BigDecimal"); 

    return result;
  } // getBigDecimal

  public BigInteger getBigInteger() throws DatatypeConversionException 
  {
    BigInteger result = null;

    if (isBigInteger()) result = (BigInteger)value;
    else if (isInteger()) result = BigInteger.valueOf(getLong());
    else if (isLong()) result = BigInteger.valueOf(getLong());
    else if (isShort()) result = BigInteger.valueOf(getLong());
    else throw new DatatypeConversionException("Cannot convert value of type '" + value.getClass().getCanonicalName() + "' to BigInteger"); 

    return result;
  } // getBigInteger

  public short getShort() throws DatatypeConversionException 
  { 
    if (!isShort()) 
      throw new DatatypeConversionException("Cannot convert value of type '" + value.getClass().getCanonicalName() + "' to short"); 
    return ((Short)value).shortValue(); 
  } // getShort

  public byte getByte() throws DatatypeConversionException 
  {
    if (!isByte()) 
      throw new DatatypeConversionException("Cannot convert value of type '" + value.getClass().getCanonicalName() + "' to byte"); 
    return ((java.lang.Byte)value).byteValue();
  } // getByte

  // Primitive XSD Types

  public edu.stanford.smi.protegex.owl.swrl.bridge.xsd.Time getTime() throws DatatypeConversionException 
  {
    if (!isTime()) 
      throw new DatatypeConversionException("Cannot convert value of type '" + value.getClass().getName() + "' to Time"); 
    return (edu.stanford.smi.protegex.owl.swrl.bridge.xsd.Time)value;
  } // getTime

  public edu.stanford.smi.protegex.owl.swrl.bridge.xsd.Date getDate() throws DatatypeConversionException 
  {
    if (!isDate()) 
      throw new DatatypeConversionException("Cannot convert value of type '" + value.getClass().getName() + "' to Date"); 
    return (edu.stanford.smi.protegex.owl.swrl.bridge.xsd.Date)value;
  } // getDate

  public edu.stanford.smi.protegex.owl.swrl.bridge.xsd.DateTime getDateTime() throws DatatypeConversionException 
  { 
    if (!isDateTime()) 
      throw new DatatypeConversionException("Cannot convert value of type '" + value.getClass().getName() + "' to DateTime"); 

    return (edu.stanford.smi.protegex.owl.swrl.bridge.xsd.DateTime)value;
  } // getDateTime

  public edu.stanford.smi.protegex.owl.swrl.bridge.xsd.Duration getDuration() throws DatatypeConversionException
  {
    if (!isDuration()) 
      throw new DatatypeConversionException("Cannot convert value of type '" + value.getClass().getName() + "' to Duration"); 
    return  (edu.stanford.smi.protegex.owl.swrl.bridge.xsd.Duration)value; 
  } // getDuration

  public edu.stanford.smi.protegex.owl.swrl.bridge.xsd.AnyURI getAnyURI() throws DatatypeConversionException 
  {
    if (!isAnyURI()) 
      throw new DatatypeConversionException("Cannot convert value of type '" + value.getClass().getName() + "' to AnyURI"); 
    return (edu.stanford.smi.protegex.owl.swrl.bridge.xsd.AnyURI)value;
  } // getAnyURI

  public edu.stanford.smi.protegex.owl.swrl.bridge.xsd.HexBinary getHexBinary() throws DatatypeConversionException 
  {
    if (!isHexBinary()) 
      throw new DatatypeConversionException("Cannot convert value of type '" + value.getClass().getName() + "' to HexBinary"); 
    return (edu.stanford.smi.protegex.owl.swrl.bridge.xsd.HexBinary)value;
  } // getHexBinary

  public edu.stanford.smi.protegex.owl.swrl.bridge.xsd.Base64Binary getBase64Binary() throws DatatypeConversionException 
  {
    if (!isBase64Binary()) 
      throw new DatatypeConversionException("Cannot convert value of type '" + value.getClass().getName() + "' to Base64Binary"); 
    return (edu.stanford.smi.protegex.owl.swrl.bridge.xsd.Base64Binary)value;
  } // getBase64Binary

  public edu.stanford.smi.protegex.owl.swrl.bridge.xsd.GDay getGDay() throws DatatypeConversionException 
  {
    if (!isGDay()) 
      throw new DatatypeConversionException("Cannot convert value of type '" + value.getClass().getName() + "' to GDay"); 
    return (edu.stanford.smi.protegex.owl.swrl.bridge.xsd.GDay)value;
  } // getGDay

  public edu.stanford.smi.protegex.owl.swrl.bridge.xsd.GYearMonth getGYearMonth() throws DatatypeConversionException 
  {
    if (!isGYearMonth()) 
      throw new DatatypeConversionException("Cannot convert value of type '" + value.getClass().getName() + "' to GYearMonth"); 
    return (edu.stanford.smi.protegex.owl.swrl.bridge.xsd.GYearMonth)value;
  } // getGYearMonth

  public edu.stanford.smi.protegex.owl.swrl.bridge.xsd.GMonth getGMonth() throws DatatypeConversionException 
  {
    if (!isGMonth()) 
      throw new DatatypeConversionException("Cannot convert value of type '" + value.getClass().getName() + "' to GMonth"); 
    return (edu.stanford.smi.protegex.owl.swrl.bridge.xsd.GMonth)value;
  } // getGMonth

  public edu.stanford.smi.protegex.owl.swrl.bridge.xsd.GMonthDay getGMonthDay() throws DatatypeConversionException 
  {
    if (!isGMonthDay()) 
      throw new DatatypeConversionException("Cannot convert value of type '" + value.getClass().getName() + "' to GMonthDay"); 
    return (edu.stanford.smi.protegex.owl.swrl.bridge.xsd.GMonthDay)value;
  } // getGMonthDay

  public edu.stanford.smi.protegex.owl.swrl.bridge.xsd.GYear getGYear() throws DatatypeConversionException 
  {
    if (!isGYear()) 
      throw new DatatypeConversionException("Cannot convert value of type '" + value.getClass().getName() + "' to GYear"); 
    return (edu.stanford.smi.protegex.owl.swrl.bridge.xsd.GYear)value;
  } // getGYear

  public edu.stanford.smi.protegex.owl.swrl.bridge.xsd.NOTATION getNOTATION() throws DatatypeConversionException 
  {
    if (!isNOTATION()) 
      throw new DatatypeConversionException("Cannot convert value of type '" + value.getClass().getName() + "' to NOTATION"); 
    return (edu.stanford.smi.protegex.owl.swrl.bridge.xsd.NOTATION)value;
  } // getNOTATION

  public edu.stanford.smi.protegex.owl.swrl.bridge.xsd.QName getQName() throws DatatypeConversionException 
  {
    if (!isQName()) 
      throw new DatatypeConversionException("Cannot convert value of type '" + value.getClass().getName() + "' to QName"); 
    return (edu.stanford.smi.protegex.owl.swrl.bridge.xsd.QName)value;
  } // getQName

  public String toString() { return value.toString(); }

  public Object getValueClassName() { return value.getClass().getName(); }
  public Object getValue() { return value; }

  public RDFSLiteral asRDFSLiteral(OWLModel owlModel) throws DatatypeConversionException
  {
    RDFSLiteral literal = null;

    if (isString()) literal = owlModel.asRDFSLiteral(getString());
    else if (isInteger()) literal = owlModel.asRDFSLiteral(getInt());
    else if (isLong()) literal = owlModel.asRDFSLiteral(getLong());
    else if (isBoolean()) literal = owlModel.asRDFSLiteral(getBoolean());
    else if (isFloat()) literal = owlModel.asRDFSLiteral(getFloat());
    else if (isDouble()) literal = owlModel.asRDFSLiteral(getDouble());
    else if (isShort()) literal = owlModel.asRDFSLiteral(getShort());
    else if (isBigDecimal()) literal = owlModel.asRDFSLiteral(getBigDecimal());
    else if (isBigInteger()) literal = owlModel.asRDFSLiteral(getBigInteger());
    else if (isByte()) literal = owlModel.asRDFSLiteral(getByte());
    else throw new DatatypeConversionException("Cannot convert LiteralInfo with value '" + value + "' to RDFSLiteral.");

    return literal;
  } // asRDFSLiteral

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
    return  ((Comparable)value).compareTo(((LiteralInfo)o).getValue()); // Will throw a ClassCastException if o's class does not implement Comparable.
  } // compareTo

} // LiteralInfo
