
package edu.stanford.smi.protegex.owl.swrl.bridge.query;

import edu.stanford.smi.protegex.owl.swrl.bridge.query.exceptions.ResultException;

import java.util.List;

/**
 ** Interface to configure a result and add data to it. See the Result class for detailed comments.
 */
public interface ResultGenerator
{
  static String MinAggregateFunction = "min"; 
  static String MaxAggregateFunction = "max"; 
  static String SumAggregateFunction = "sum"; 
  static String AvgAggregateFunction = "avg"; 
  static String CountAggregateFunction = "count"; 
  
  void initialize();
  
  void addSelectedColumn(String columnName) throws ResultException;
  void addAggregateColumn(String columnName, String aggregateFunctionName) throws ResultException;
  void addOrderByColumn(int orderedColumnIndex, boolean ascending) throws ResultException;
  
  boolean isOrdered();
  boolean isAscending();
  
  void setIsDistinct();
  
  void addColumnDisplayName(String columnName) throws ResultException;
  
  boolean isConfigured();
  void configured() throws ResultException;
  
  void openRow() throws ResultException;
  void addRowData(ResultValue value) throws ResultException;
  void closeRow() throws ResultException;
  
  boolean isRowOpen();
  boolean isPrepared();
  void prepared() throws ResultException;
} // ResultGenerator
