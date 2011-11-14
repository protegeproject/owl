
package edu.stanford.smi.protegex.owl.swrl.bridge;

import edu.stanford.smi.protegex.owl.swrl.portability.OWLLiteralReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLPropertyValueReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLTypedLiteralReference;
import edu.stanford.smi.protegex.owl.swrl.sqwrl.exceptions.DataValueConversionException;

/**
 * Convenience wrapper around OWLAPI classes OWLLiteral and OWLDataType
 */
public interface OWLDataValue extends OWLPropertyValueReference, OWLLiteralReference, OWLTypedLiteralReference 
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
  
  boolean isComparable();

  String getString() throws DataValueConversionException;
  boolean getBoolean() throws DataValueConversionException;
  int getInt() throws DataValueConversionException;
  long getLong() throws DataValueConversionException;
  float getFloat() throws DataValueConversionException;
  double getDouble() throws DataValueConversionException;
  short getShort() throws DataValueConversionException;
  byte getByte() throws DataValueConversionException;
  Number getNumber() throws DataValueConversionException;

  String toString();
  String toQuotedString();
}
