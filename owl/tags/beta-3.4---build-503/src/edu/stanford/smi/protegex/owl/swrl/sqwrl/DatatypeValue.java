
package edu.stanford.smi.protegex.owl.swrl.sqwrl;

import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.DatatypeConversionException;

import edu.stanford.smi.protegex.owl.swrl.bridge.xsd.*;

import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFSLiteral;

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
  Number getNumber() throws DatatypeConversionException;

  RDFSLiteral asRDFSLiteral(OWLModel owlModel) throws DatatypeConversionException;

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

  Time getTime() throws DatatypeConversionException;
  Date getDate() throws DatatypeConversionException;
  Duration getDuration() throws DatatypeConversionException;
  AnyURI getAnyURI() throws DatatypeConversionException;
  Base64Binary getBase64Binary() throws DatatypeConversionException;
  HexBinary getHexBinary() throws DatatypeConversionException;
  GDay getGDay() throws DatatypeConversionException;
  GMonth getGMonth() throws DatatypeConversionException;
  GYear getGYear() throws DatatypeConversionException;
  GMonthDay getGMonthDay() throws DatatypeConversionException;
  GYearMonth getGYearMonth() throws DatatypeConversionException;
  NOTATION getNOTATION() throws DatatypeConversionException;
  QName getQName() throws DatatypeConversionException;

  String toString();
} // DatatypeValue
