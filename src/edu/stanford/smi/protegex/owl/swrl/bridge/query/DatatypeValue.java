
package edu.stanford.smi.protegex.owl.swrl.bridge.query;

import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.DatatypeConversionException;
import edu.stanford.smi.protegex.owl.swrl.bridge.xsd.*;

import java.math.BigDecimal;
import java.math.BigInteger;

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
  boolean isBigDecimal();
  boolean isBigInteger();

  String getString() throws DatatypeConversionException;

  boolean getBoolean() throws DatatypeConversionException;

  int getInt() throws DatatypeConversionException;
  long getLong() throws DatatypeConversionException;
  float getFloat() throws DatatypeConversionException;
  double getDouble() throws DatatypeConversionException;
  short getShort() throws DatatypeConversionException;
  byte getByte() throws DatatypeConversionException;
  BigDecimal getBigDecimal() throws DatatypeConversionException;
  BigInteger getBigInteger() throws DatatypeConversionException;

  // Primitive XML Schema types not covered by Java types.
  boolean isPrimitiveXSDType();

  boolean isTime();
  boolean isDate();
  boolean isDuration();
  boolean isDateTime();
  boolean isAnyURI();
  boolean isBase64Binary();
  boolean isGDay();
  boolean isGMonth();
  boolean isGYear();
  boolean isHexBinary();
  boolean isGMonthDay();
  boolean isGYearMonth();
  boolean isNOTATION();
  boolean isQName();

  edu.stanford.smi.protegex.owl.swrl.bridge.xsd.Time getTime() throws DatatypeConversionException;
  edu.stanford.smi.protegex.owl.swrl.bridge.xsd.Date getDate() throws DatatypeConversionException;
  edu.stanford.smi.protegex.owl.swrl.bridge.xsd.Duration getDuration() throws DatatypeConversionException;
  edu.stanford.smi.protegex.owl.swrl.bridge.xsd.AnyURI getAnyURI() throws DatatypeConversionException;
  edu.stanford.smi.protegex.owl.swrl.bridge.xsd.Base64Binary getBase64Binary() throws DatatypeConversionException;
  edu.stanford.smi.protegex.owl.swrl.bridge.xsd.HexBinary getHexBinary() throws DatatypeConversionException;
  edu.stanford.smi.protegex.owl.swrl.bridge.xsd.GDay getGDay() throws DatatypeConversionException;
  edu.stanford.smi.protegex.owl.swrl.bridge.xsd.GMonth getGMonth() throws DatatypeConversionException;
  edu.stanford.smi.protegex.owl.swrl.bridge.xsd.GYear getGYear() throws DatatypeConversionException;
  edu.stanford.smi.protegex.owl.swrl.bridge.xsd.GMonthDay getGMonthDay() throws DatatypeConversionException;
  edu.stanford.smi.protegex.owl.swrl.bridge.xsd.GYearMonth getGYearMonth() throws DatatypeConversionException;
  edu.stanford.smi.protegex.owl.swrl.bridge.xsd.NOTATION getNOTATION() throws DatatypeConversionException;
  edu.stanford.smi.protegex.owl.swrl.bridge.xsd.QName getQName() throws DatatypeConversionException;

  String toString();
} // DatatypeValue
