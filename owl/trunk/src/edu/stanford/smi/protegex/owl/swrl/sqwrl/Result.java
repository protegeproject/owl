
package edu.stanford.smi.protegex.owl.swrl.sqwrl;

import edu.stanford.smi.protegex.owl.swrl.sqwrl.exceptions.ResultException;

import java.util.List;

/**
 ** Interface that defines methods to process results from a SQWRL query. See <a
 ** href="http://protege.cim3.net/cgi-bin/wiki.pl?SQWRLQueryAPI">here</a> for documentation.
 */
public interface Result
{
  List<String> getColumnNames() throws ResultException;
  int getNumberOfColumns() throws ResultException;
  String getColumnName(int columnIndex) throws ResultException;
   
  int getNumberOfRows() throws ResultException;
  void reset() throws ResultException;
  void next() throws ResultException;
  boolean hasNext() throws ResultException;
  
  boolean hasObjectValue(String columnName) throws ResultException;
  boolean hasObjectValue(int columnIndex) throws ResultException;
  boolean hasDatatypeValue(String columnName) throws ResultException;
  boolean hasDatatypeValue(int columnIndex) throws ResultException;
  boolean hasClassValue(String columnName) throws ResultException;
  boolean hasClassValue(int columnIndex) throws ResultException;
  boolean hasPropertyValue(String columnName) throws ResultException;
  boolean hasPropertyValue(int columnIndex) throws ResultException;

  List<ResultValue> getRow() throws ResultException;
  ResultValue getValue(String columnName) throws ResultException;
  ResultValue getValue(int columnIndex) throws ResultException;
  ResultValue getValue(int columnIndex, int rowIndex) throws ResultException;
  ObjectValue getObjectValue(String columnName) throws ResultException;
  ObjectValue getObjectValue(int columnIndex) throws ResultException;
  DatatypeValue getDatatypeValue(String columnName) throws ResultException;
  DatatypeValue getDatatypeValue(int columnIndex) throws ResultException;
  ClassValue getClassValue(String columnName) throws ResultException;
  ClassValue getClassValue(int columnIndex) throws ResultException;
  PropertyValue getPropertyValue(String columnName) throws ResultException;
  PropertyValue getPropertyValue(int columnIndex) throws ResultException;

  List<ResultValue> getColumn(String columnName) throws ResultException;
  List<ResultValue> getColumn(int columnIndex) throws ResultException;
} // Result
