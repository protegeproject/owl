
package edu.stanford.smi.protegex.owl.swrl.bridge;

import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.DatatypeConversionException;

public interface DatatypeValue extends Value
{
  // Numeric types
  boolean isInteger();
  boolean isLong();
  boolean isFloat();
  boolean isDouble();
  boolean isShort();

  int getInt() throws DatatypeConversionException;
  long getLong() throws DatatypeConversionException;
  float getFloat() throws DatatypeConversionException;
  double getDouble() throws DatatypeConversionException;
  short getShort() throws DatatypeConversionException;

  // Basic non numeric types
  boolean isBoolean();
  boolean isString();

  boolean getBoolean() throws DatatypeConversionException;
  String getString() throws DatatypeConversionException;

  // Complex XML Schema types
  boolean isTime();
  boolean isDate();
  boolean isDateTime();
  boolean isDuration();
  boolean isAnyURI();
  boolean isBase64Binary();
  boolean isDecimal();
  boolean isByte();

  edu.stanford.smi.protegex.owl.swrl.bridge.Time getTime() throws DatatypeConversionException;
  edu.stanford.smi.protegex.owl.swrl.bridge.Date getDate() throws DatatypeConversionException;
  edu.stanford.smi.protegex.owl.swrl.bridge.DateTime getDateTime() throws DatatypeConversionException;
  edu.stanford.smi.protegex.owl.swrl.bridge.Duration getDuration() throws DatatypeConversionException; 
  edu.stanford.smi.protegex.owl.swrl.bridge.AnyURI getAnyURI() throws DatatypeConversionException;
  edu.stanford.smi.protegex.owl.swrl.bridge.Base64Binary getBase64Binary() throws DatatypeConversionException;
  edu.stanford.smi.protegex.owl.swrl.bridge.Decimal getDecimal() throws DatatypeConversionException;
  edu.stanford.smi.protegex.owl.swrl.bridge.Byte getByte() throws DatatypeConversionException;

  boolean isNumeric();
  String toString();
} // DatatypeValue
