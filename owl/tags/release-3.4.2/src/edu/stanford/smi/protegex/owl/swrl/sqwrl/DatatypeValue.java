
package edu.stanford.smi.protegex.owl.swrl.sqwrl;

import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.DatatypeConversionException;

public interface DatatypeValue extends ResultValue
{
  // Java types
  boolean isString();

  boolean isBoolean();

  boolean isNumeric();

  boolean isInteger();
  boolean isLong();
  boolean isFloat();
  boolean isDouble();
  boolean isShort();
  boolean isByte();
  
  boolean isXSDType();
  boolean isXSDTime();
  boolean isXSDDate();
  boolean isXSDDateTime();
  boolean isXSDDuration();
  boolean isXSDAnyURI();

  String getString() throws DatatypeConversionException;

  boolean getBoolean() throws DatatypeConversionException;

  int getInt() throws DatatypeConversionException;
  long getLong() throws DatatypeConversionException;
  float getFloat() throws DatatypeConversionException;
  double getDouble() throws DatatypeConversionException;
  short getShort() throws DatatypeConversionException;
  byte getByte() throws DatatypeConversionException;
  Number getNumber() throws DatatypeConversionException;

  String toString();
} // DatatypeValue
