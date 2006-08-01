
// Info object to wrap XML Schema datatype literals when passing them to and from built-in methods. Also provides a central place to valdate
// the content of complex types, such as datetimes etc.

package edu.stanford.smi.protegex.owl.swrl.bridge;

import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;

import edu.stanford.smi.protegex.owl.model.*;

public class LiteralInfo extends Info implements Argument
{
  private Object value;
  
  public LiteralInfo(OWLModel owlModel, RDFSLiteral literal)
    throws LiteralConversionException
  {
    super("<An RDFS-based literal>");

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
    else throw new LiteralConversionException("Cannot create LiteralInfo object for RDFS literal '" + literal.getString() 
                                              + "' of type " + datatype + ".");
  } // LiteralInfo

  public LiteralInfo(String s)
  {
    super("<A string literal>");
    value = s;
  } // LiteralInfo

  public LiteralInfo(Number n)
  {
    super("<A number literal>");
    value = n;
  } // LiteralInfo

  public LiteralInfo(boolean b)
  {
    super("<A boolean literal>");
    value = new Boolean(b);
  } // LiteralInfo

  public LiteralInfo(int i)
  {
    super("<An integer literal>");
    value = new Integer(i);
  } // LiteralInfo

  public LiteralInfo(float f)
  {
    super("<A float literal>");
    value = new Float(f);
  } // LiteralInfo

  public LiteralInfo(double d)
  {
    super("<A double literal>");
    value = new Double(d);
  } // LiteralInfo

  public LiteralInfo(short s)
  {
    super("<A short literal>");
    value = new Short(s);
  } // LiteralInfo

  public LiteralInfo(ComplexXSDType value)
  {
    super("<A complex XSD literal>");
    this.value = value;
  } // LiteralInfo

  public int getInt() { return isInteger() ? ((Integer)value).intValue() : null; }
  public boolean getBoolean() { return isBoolean() ? ((Boolean)value).booleanValue() : null; }
  public long getLong() { return isLong() ? ((Long)value).longValue() : null; }
  public float getFloat() { return isFloat() ? ((Float)value).floatValue() : null; }
  public double getDouble() { return isDouble() ? ((Double)value).doubleValue() : null; }
  public short getShort() { return isShort() ? ((Short)value).shortValue() : null; }
  public String getString() { return isString() ? (String)value : null; }
  public Time getTime() { return isTime() ? (Time)value : null; }
  public Date getDate() { return isDate() ? (Date)value : null; }
  public DateTime getDateTime() { return isDateTime() ? (DateTime)value : null; }
  public Duration getDuration() { return isDuration() ? (Duration)value : null; }
  public AnyURI getAnyURI() { return isAnyURI() ? (AnyURI)value : null; }
  public Base64Binary getBase64Binary() { return isBase64Binary() ? (Base64Binary)value : null; }
  public Decimal getDecimal() { return isDecimal() ? (Decimal)value : null; }
  public Byte getByte() { return isByte() ? (Byte)value : null; }

  public boolean isInteger() { return value instanceof Integer; }
  public boolean isLong() { return value instanceof Long; }
  public boolean isBoolean() { return value instanceof Boolean; }
  public boolean isFloat() { return value instanceof Float; }
  public boolean isDouble() { return value instanceof Double; }
  public boolean isShort() { return value instanceof Short; }
  public boolean isString() { return value instanceof String; }
  public boolean isTime() { return value instanceof Time; }
  public boolean isDate() { return value instanceof Date; }
  public boolean isDateTime() { return value instanceof DateTime; }
  public boolean isDuration() { return value instanceof Duration;} 
  public boolean isAnyURI() { return value instanceof AnyURI; }
  public boolean isBase64Binary() { return value instanceof Base64Binary;} 
  public boolean isDecimal() { return value instanceof Decimal; }
  public boolean isByte() { return value instanceof Byte; }

  public boolean isNumeric() { return value instanceof Number; }
  public String toString() { return value.toString(); }

  public boolean equals(Object obj)
  {
    if(this == obj) return true;
    if((obj == null) || (obj.getClass() != this.getClass())) return false;
    LiteralInfo info = (LiteralInfo)obj;
    return (getName() == info.getName() || (getName() != null && getName().equals(info.getName()))) && 
           (value != null && info.value != null && value.toString().equals(info.value.toString()));
  } // equals

  public int hashCode()
  {
    int hash = 66;
    hash = hash + (null == getName() ? 0 : getName().hashCode());
    hash = hash + (null == value ? 0 : value.toString().hashCode());
    return hash;
  } // hashCode

  public abstract class ComplexXSDType
  {
    private String content;

    public ComplexXSDType(String content) throws LiteralConversionException
    { 
      this.content = content; 
      validate();
    } // ComplexXSDType

    public String getContent() { return content; }

    protected abstract void validate() throws LiteralConversionException;
  } // ComplexXSDType

  // TODO: implement proper validate methods for these types.

  public class Time extends ComplexXSDType 
  { 
    public Time(String content) throws LiteralConversionException { super(content); } 
    protected void validate() throws LiteralConversionException 
    {
      if (getContent() == null) throw new LiteralConversionException("Null content for Time literal.");
    }  // validate
  } // Time

  public class Date extends ComplexXSDType 
  { 
    public Date(String content) throws LiteralConversionException { super(content); } 
    protected void validate() throws LiteralConversionException 
    {
      if (getContent() == null) throw new LiteralConversionException("Null content for Time literal.");
    }  // validate
  } // Date

  public class DateTime extends ComplexXSDType 
  { 
    public DateTime(String content) throws LiteralConversionException { super(content); } 
    protected void validate() throws LiteralConversionException 
    {
      if (getContent() == null) throw new LiteralConversionException("Null content for DateTime literal.");
    }  // validate
  } // DateTime

  public class Duration extends ComplexXSDType 
  { 
    public Duration(String content) throws LiteralConversionException { super(content); } 
    protected void validate() throws LiteralConversionException 
    {
      if (getContent() == null) throw new LiteralConversionException("Null content for Duration literal.");
    }  // validate
  } // Duration

  public class AnyURI extends ComplexXSDType 
  { 
    public AnyURI(String content) throws LiteralConversionException { super(content); } 
    protected void validate() throws LiteralConversionException 
    {
      if (getContent() == null) throw new LiteralConversionException("Null content for AnyURI literal.");
    }  // validate
  } // AnyURI

  public class Base64Binary extends ComplexXSDType 
  { 
    public Base64Binary(String content) throws LiteralConversionException { super(content); } 
    protected void validate() throws LiteralConversionException 
    {
      if (getContent() == null) throw new LiteralConversionException("Null content for Base64Binary literal.");
    }  // validate
  } // Base64Binary

  public class Decimal extends ComplexXSDType 
  { 
    public Decimal(String content) throws LiteralConversionException { super(content); } 
    protected void validate() throws LiteralConversionException 
    {
      if (getContent() == null) throw new LiteralConversionException("Null content for Decimal literal.");
    }  // validate
  } // Decimal

  public class Byte extends ComplexXSDType 
  { 
    public Byte(String content) throws LiteralConversionException { super(content); } 
    protected void validate() throws LiteralConversionException 
    {
      if (getContent() == null) throw new LiteralConversionException("Null content for Byte literal.");
    }  // validate
  } // Byte

} // LiteralInfo
