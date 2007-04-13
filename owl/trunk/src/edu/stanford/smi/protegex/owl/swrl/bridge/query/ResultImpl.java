
package edu.stanford.smi.protegex.owl.swrl.bridge.query;

import edu.stanford.smi.protegex.owl.swrl.bridge.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.query.exceptions.*;

import java.util.*;
import java.math.*;
import java.io.Serializable;

/**
 ** This class implements the interfaces Result and ResultGenerator. It can be used to generate a result structure and populate it with data;
 ** it can also be used to retrieve those data from the result.
 **
 ** This class operates in three phases:
 **
 ** (1) Configuration Phase: In this phase the structure of the result is defined. This phase opened by a call to the configure() method (which
 ** will also clear any existing data). In this phase the columns are defined; aggregation or ordering is also specified in this phase. This
 ** phase is closed by a call to the configured() method.
 **
 ** (2) Preparation Phase: In this phase data are added to the result. This phase is implicitly opened by the call to the configured() method. It
 ** is closed by a call to the prepared() method.
 **
 ** The interface ResultGenerator defines the calls used in these two phases.
 **
 ** (3) Processing Phase: In this phase data may be retrieved from the result. This phase is implicitly opened by the call to the closed()
 ** method.
 **
 ** The interface Result defines the calls used in the processing phase.
 **
 ** An example configuration and data generation is:
 **
 ** Result result = new ResultImpl("TestResult");
 **
 ** result.addSelectedIndividualColumn("name");
 ** result.addAggregateColumn("average", ResultGenerator.AvgAggregateFunction);
 **
 ** result.configured();
 **
 ** result.openRow();
 ** result.addData(new IndividualInfo("Fred"));
 ** result.addData(new LiteralInfo(27));
 ** result.closeRow();
 **
 ** result.openRow();
 ** result.addData(new IndividualInfo("Joe"));
 ** result.addData(new LiteralInfo(34));
 ** result.closeRow();
 **
 ** result.openRow();
 ** result.addData(new IndividualInfo("Joe"));
 ** result.addData(new LiteralInfo(21));
 ** result.closeRow();
 **
 ** result.prepared();
 **
 ** The result is now available for reading. The interface Result defines the assessor methods. A row consists of a list of objects defined
 ** by the interface ResultValue. There are four possible types of values (1) DatatypeValue, representing literals; (2) ObjectValue,
 ** representing OWL individuals; (3) ClassValue, representing OWL classes; and (4) PropertyValue, representing OWL properties.
 **
 ** while (result.hasNext()) {
 **  ObjectValue nameValue = result.getObjectValue("name");
 **  DatatypeValue averageValue = result.getDatatypeValue("average");
 **  System.out.println("Name: " + nameValue.getIndividualName());
 **  System.out.println("Average: " + averageValue.getInt());
 ** } // while
 */ 
public class ResultImpl implements ResultGenerator, Result
{
  private List<String> allColumnNames, columnDisplayNames;
  private List<Integer> selectedColumnIndexes, orderByColumnIndexes;
  private HashMap<Integer, String> aggregateColumnIndexes; // Map of (index, function) pairs
  private List<List<ResultValue>> rows; // List of List of ResultValue objects.
  private List<ResultValue> rowData; // List of ResultValue objects used when assembling a row.
  private int numberOfColumns, rowIndex, rowDataColumnIndex;
  private boolean isConfigured, isPrepared, isRowOpen, isOrdered, isAscending, isDistinct, hasAggregates;

  private static final String aggregateFunctionNames[] = { MinAggregateFunction, MaxAggregateFunction, SumAggregateFunction,
                                                           AvgAggregateFunction, CountAggregateFunction };
  public ResultImpl() 
  {
    initialize();
  } // Result
  
  // Configuration phase methods.

  public boolean isConfigured() { return isConfigured; }
  public boolean isRowOpen() { return isRowOpen; }
  public boolean isDistinct() { return isDistinct; }

  public boolean isPrepared() { return isPrepared; }
  public boolean isOrdered() { return isOrdered; }
  public boolean isAscending() { return isAscending; }

  public void initialize()
  {
    isConfigured = false;
    isPrepared = false;
    isRowOpen = false;
    
    // The following variables will not be externally valid until configured() is called. 
    allColumnNames = new ArrayList<String>();
    aggregateColumnIndexes = new HashMap<Integer, String>();
    selectedColumnIndexes = new ArrayList<Integer>();
    orderByColumnIndexes = new ArrayList<Integer>();
    columnDisplayNames = new ArrayList<String>();

    numberOfColumns = 0; isOrdered = isAscending = isDistinct = false;

    // The following variables will not be externally valid until prepared() is called.
    rowIndex = -1; // If there are no rows in the final result, it will remain at -1.
    rows = new ArrayList<List<ResultValue>>();
  } // prepare

  public void addSelectedColumn(String columnName) throws ResultException
  {
    throwExceptionIfAlreadyConfigured();

    selectedColumnIndexes.add(Integer.valueOf(numberOfColumns));
    allColumnNames.add(columnName);
    numberOfColumns++;
  } // addSelectedIndividualColumn

  public void addAggregateColumn(String columnName, String aggregateFunctionName) throws ResultException
  {
    throwExceptionIfAlreadyConfigured();

    checkAggregateFunctionName(aggregateFunctionName);

    aggregateColumnIndexes.put(Integer.valueOf(numberOfColumns), aggregateFunctionName);
    allColumnNames.add(columnName);
    numberOfColumns++;
  } // addAggregateColumn
  
  public void addOrderByColumn(int orderedColumnIndex, boolean ascending) throws ResultException
  {
    throwExceptionIfAlreadyConfigured();

    if (orderedColumnIndex < 0 || orderedColumnIndex >= allColumnNames.size()) 
      throw new ResultException("ordered column index " + orderedColumnIndex + " out of range");

    if (isOrdered && (isAscending != ascending)) {
      if (isAscending) 
        throw new ResultException("attempt to order column '" + allColumnNames.get(orderedColumnIndex) + "' ascending when descending was previously selected");
      else throw new ResultException("attempt to order column '" + allColumnNames.get(orderedColumnIndex) + "' descending when ascending was previously selected");
    } // if

    isOrdered = true;
    isAscending = ascending;

    orderByColumnIndexes.add(Integer.valueOf(orderedColumnIndex));
  } // addOrderByColumn
    
  public void addColumnDisplayName(String columnName) throws ResultException
  {
    if (columnName.length() == 0 || columnName.indexOf(',') != -1) 
      throw new ResultException("invalid column name '" + columnName + "' - no commas or empty names allowed");

    columnDisplayNames.add(columnName);
  } // addColumnDisplayName

  public void configured() throws ResultException
  {
    throwExceptionIfAlreadyConfigured();

    // We will already have checked that all ordered columns are selected or aggregated

    if (containsOneOf(selectedColumnIndexes, aggregateColumnIndexes.keySet()))
      throw new InvalidQueryException("aggregate columns cannot also be selected columns");

    hasAggregates = !aggregateColumnIndexes.isEmpty();

    isConfigured = true;
  } // configured

  // Methods used to retrieve the result structure after the result has been configured

  public void setIsDistinct() { isDistinct = true; }

  public int getNumberOfColumns() throws ResultException
  {
    throwExceptionIfNotConfigured();

    return numberOfColumns; 
  } // getNumberOfColumns
  
  public List<String> getColumnNames() throws ResultException
  {
    List<String> result = new ArrayList<String>();

    throwExceptionIfNotConfigured();
    
    if (columnDisplayNames.size() < getNumberOfColumns()) {
      result.addAll(columnDisplayNames);
      result.addAll(allColumnNames.subList(columnDisplayNames.size(), allColumnNames.size()));
    } else result.addAll(columnDisplayNames);

    return result;
  } // getColumnNames
  
  public String getColumnName(int columnIndex) throws ResultException
  {
    throwExceptionIfNotConfigured(); checkColumnIndex(columnIndex);

    if (columnIndex < columnDisplayNames.size()) return columnDisplayNames.get(columnIndex);
    else return allColumnNames.get(columnIndex);
  } // getColumnName

  // Methods used to add data after result has been configured

  public void openRow() throws ResultException
  {
    throwExceptionIfNotConfigured(); throwExceptionIfAlreadyPrepared(); throwExceptionIfRowOpen();

    rowDataColumnIndex = 0;
    rowData = new ArrayList<ResultValue>();

    isRowOpen = true;
  } // openRow

  public void addRowData(ResultValue value) throws ResultException
  {
    throwExceptionIfNotConfigured(); throwExceptionIfAlreadyPrepared(); throwExceptionIfRowNotOpen();

    if (rowDataColumnIndex == getNumberOfColumns()) throw new ResultStateException("Attempt to add data beyond the end of a row");

    if (aggregateColumnIndexes.containsKey(Integer.valueOf(rowDataColumnIndex)) && 
        (!aggregateColumnIndexes.get(Integer.valueOf(rowDataColumnIndex)).equals(CountAggregateFunction)) && 
        (!isNumericValue(value)))
        throw new ResultException("attempt to add non numeric value '" + value + "' to min, max, sum, or avg aggregate column '" + 
                                  allColumnNames.get(rowDataColumnIndex) + "'");
    rowData.add(value);
    rowDataColumnIndex++;

    if (rowDataColumnIndex == getNumberOfColumns()) closeRow();
  } // addData    

  public void closeRow() throws ResultException
  {
    throwExceptionIfNotConfigured(); throwExceptionIfAlreadyPrepared(); throwExceptionIfRowNotOpen();

    rows.add(rowData);

    isRowOpen = false;
  } // closeRow

  public void prepared() throws ResultException
  {
    throwExceptionIfNotConfigured(); throwExceptionIfAlreadyPrepared(); throwExceptionIfRowOpen();

    isPrepared = true;
    if (getNumberOfRows() > 0) rowIndex = 0;

    if (hasAggregates) rows = aggregate(rows, allColumnNames, aggregateColumnIndexes); // Aggregation implies killing duplicate rows
    else if (isDistinct) rows = distinct(rows);

    if (isOrdered) rows = orderBy(rows, allColumnNames, orderByColumnIndexes, isAscending);
  } // prepared

  // Methods used to retrieve data after result has been prepared

  public int getNumberOfRows() throws ResultException
  { 
    throwExceptionIfNotConfigured(); throwExceptionIfNotPrepared();

    return rows.size(); 
  } // getNumberOfRows

  public void reset() throws ResultException
  {
    throwExceptionIfNotConfigured(); throwExceptionIfNotPrepared();

    if (getNumberOfRows() > 0) rowIndex = 0;
  } // reset
  
  public void next() throws ResultException
  {
    throwExceptionIfNotConfigured(); throwExceptionIfNotPrepared(); throwExceptionIfAtEndOfResult();

    if (rowIndex != -1 && rowIndex < getNumberOfRows()) rowIndex++;
  } // next
  
  public boolean hasNext() throws ResultException
  { 
    throwExceptionIfNotConfigured(); throwExceptionIfNotPrepared();

    return (rowIndex != -1 && rowIndex < getNumberOfRows());
  } // hasNext
    
  public List<ResultValue> getRow() throws ResultException
  {
    throwExceptionIfNotConfigured(); throwExceptionIfNotPrepared(); throwExceptionIfAtEndOfResult();

    return (List<ResultValue>)rows.get(rowIndex);
  } // getRow

  public ResultValue getValue(String columnName) throws ResultException
  {
    List row;
    int columnIndex;
    
    throwExceptionIfNotConfigured(); throwExceptionIfNotPrepared(); throwExceptionIfAtEndOfResult();

    checkColumnName(columnName);
    
    columnIndex = allColumnNames.indexOf(columnName);
    
    row = (List)rows.get(rowIndex);
    return (ResultValue)row.get(columnIndex);
  } // getColumnValue
  
  public ResultValue getValue(int columnIndex) throws ResultException
  {
    List row;

    throwExceptionIfNotConfigured(); throwExceptionIfNotPrepared(); throwExceptionIfAtEndOfResult();

    checkColumnIndex(columnIndex);
    
    row = (List)rows.get(rowIndex);
    return (ResultValue)row.get(columnIndex);
  } // getColumnValue

  public ResultValue getValue(int columnIndex, int rowIndex)throws ResultException
  {
    ResultValue value = null;
    
    throwExceptionIfNotConfigured(); throwExceptionIfNotPrepared(); throwExceptionIfAtEndOfResult();

    checkColumnIndex(columnIndex); checkRowIndex(rowIndex); 
    
    return (ResultValue)((List)rows.get(rowIndex)).get(columnIndex);
  } // getValue
  
  public ObjectValue getObjectValue(String columnName) throws ResultException
  {
    if (!hasObjectValue(columnName)) 
      throw new InvalidColumnTypeException("Expecting ObjectValue type for column '" + columnName + "'");
    return (ObjectValue)getValue(columnName);
  } // getObjectValue
  
  public ObjectValue getObjectValue(int columnIndex) throws ResultException
  {
    return getObjectValue(getColumnName(columnIndex));
  } // getObjectValue
  
  public DatatypeValue getDatatypeValue(String columnName) throws ResultException
  {
    if (!hasDatatypeValue(columnName)) 
      throw new InvalidColumnTypeException("Expecting DatatypeValue type for column '" + columnName + "'");
    return (DatatypeValue)getValue(columnName);
  } // getDatatypeValue

  public ClassValue getClassValue(String columnName) throws ResultException
  {
    if (!hasClassValue(columnName)) 
      throw new InvalidColumnTypeException("Expecting ClassValue type for column '" + columnName + "'");
    return (ClassValue)getValue(columnName);
  } // getClassValue

  public ClassValue getClassValue(int columnIndex) throws ResultException
  {
    return getClassValue(getColumnName(columnIndex));
  } // getClassValue

  public PropertyValue getPropertyValue(int columnIndex) throws ResultException
  {
    return getPropertyValue(getColumnName(columnIndex));
  } // getPropertyValue

  public PropertyValue getPropertyValue(String columnName) throws ResultException
  {
    if (!hasPropertyValue(columnName)) 
      throw new InvalidColumnTypeException("Expecting PropertyValue type for column '" + columnName + "'");
    return (PropertyValue)getValue(columnName);
  } // getPropertyValue
  
  public DatatypeValue getDatatypeValue(int columnIndex) throws ResultException
  {
    return getDatatypeValue(getColumnName(columnIndex));
  } // getDatatypeValue
  
  public boolean hasObjectValue(String columnName) throws ResultException
  {
    return getValue(columnName) instanceof ObjectValue;
  } // hasObjectValue
  
  public boolean hasObjectValue(int columnIndex) throws ResultException
  {
    return getValue(columnIndex) instanceof ObjectValue;
  } // hasObjectValue
  
  public boolean hasDatatypeValue(String columnName) throws ResultException
  {
    return getValue(columnName) instanceof DatatypeValue;
  } // hasDatatypeValue
  
  public boolean hasDatatypeValue(int columnIndex) throws ResultException
  {
    return getValue(columnIndex) instanceof DatatypeValue;
  } // hasDatatypeValue

  public boolean hasClassValue(String columnName) throws ResultException
  {
    return getValue(columnName) instanceof ClassValue;
  } // hasClassValue
  
  public boolean hasClassValue(int columnIndex) throws ResultException
  {
    return getValue(columnIndex) instanceof ClassValue;
  } // hasClassValue

  public boolean hasPropertyValue(String columnName) throws ResultException
  {
    return getValue(columnName) instanceof PropertyValue;
  } // hasPropertyValue
  
  public boolean hasPropertyValue(int columnIndex) throws ResultException
  {
    return getValue(columnIndex) instanceof PropertyValue;
  } // hasPropertyValue

  public String toString()
  {
    String result = "";

    result += "[isConfigured: " + isConfigured + ", isPrepared: " + isPrepared + ", isRowOpen: " + isRowOpen +
              ", isOrdered: " + isOrdered + ", isAscending " + isAscending + ", isDistinct: " + isDistinct + 
              ", hasAggregates: " + hasAggregates + "]\n";

    for (List<ResultValue> row : rows) {
      for (ResultValue value : row) {
        result += "" + value + " ";
      } // for
      result += "\n";
    } // for

    result += "--------------------------------------------------------------------------------\n";
      
    return result;
  } // toString      
  
  // Phase verification exception throwing methods
  
  private void throwExceptionIfNotConfigured() throws ResultException
  {
    if (!isConfigured()) throw new ResultStateException("Attempt to add data to unconfigured result");
  } // throwExceptionIfNotConfigured

  private void throwExceptionIfAtEndOfResult() throws ResultException
  {
    if (!hasNext()) throw new ResultStateException("Attempt to get data after end of result reached");
  } // throwExceptionIfAtEndOfResult

  private void throwExceptionIfNotPrepared() throws ResultException
  {
    if (!isPrepared()) throw new ResultStateException("Attempt to process unprepared result");
  } // throwExceptionIfNotConfigured

  private void throwExceptionIfAlreadyConfigured() throws ResultException
  {
    if (isConfigured()) throw new ResultStateException("Attempt to configure already configured result");
  } // throwExceptionIfAlreadyConfigured

  private void throwExceptionIfAlreadyPrepared() throws ResultException
  {
    if (isPrepared()) throw new ResultStateException("Attempt to modify prepared result");
  } // throwExceptionIfAlreadyConfigured

  private void checkColumnName(String columnName) throws InvalidColumnNameException
  {
    if (!allColumnNames.contains(columnName)) throw new InvalidColumnNameException("Invalid column name: " + columnName);
  } // checkColumnName
  
  private void throwExceptionIfRowNotOpen() throws ResultException
  {
    if (!isRowOpen) throw new ResultStateException("Attempt to add data to an unopened row");
  } // throwExceptionIfRowNotOpen

  private void throwExceptionIfRowOpen() throws ResultException
  {
    if (isRowOpen) throw new ResultStateException("Attempt to process result with a partially prepared row");
  } // throwExceptionIfRowOpen

  private void checkColumnIndex(int columnIndex) throws ResultException
  {
    if (columnIndex < 0 || columnIndex >= getNumberOfColumns())
      throw new InvalidColumnIndexException("Column index " + columnIndex + " out of bounds");
  } // checkColumnIndex

  private void checkRowIndex(int rowIndex) throws ResultException
  {
    if (rowIndex < 0 || rowIndex >= getNumberOfRows())
      throw new InvalidRowIndexException("Row index " + rowIndex + " out of bounds");
  } // checkRowIndex
  
  private void checkAggregateFunctionName(String aggregateFunctionName) throws InvalidAggregateFunctionNameException
  {
    boolean found = false;
    
    for (int i = 0; i < aggregateFunctionNames.length; i++) {
      if (aggregateFunctionNames[i].equalsIgnoreCase(aggregateFunctionName)) found = true;
    }
    if (!found) throw new InvalidAggregateFunctionNameException("Invalid aggregate function: " + aggregateFunctionName);
  } // checkAggregateFunctionName

  private boolean containsOneOf(Collection collection1, Collection collection2)
  {
    Iterator iterator = collection2.iterator();

    while (iterator.hasNext()) {
      Object o = iterator.next();
      if (collection1.contains(o)) return true;
    } // while
    return false;
  } // containsOneOf

  private boolean isNumericValue(ResultValue value)
  {
    return ((value instanceof DatatypeValue) || (((DatatypeValue)value).isNumeric()));
  } // isNumericValue

  // TODO: fix - very inefficient
  private List<List<ResultValue>> distinct(List<List<ResultValue>> rows)
  {
    List<List<ResultValue>> localRows = new ArrayList<List<ResultValue>>(rows);
    List<List<ResultValue>> result = new ArrayList<List<ResultValue>>();
    RowComparator rowComparator = new RowComparator(allColumnNames, true); // Look at the entire row.

    Collections.sort(localRows, rowComparator); // binary search is expecting a sorted list
    for (List<ResultValue> row : localRows) if (Collections.binarySearch(result, row, rowComparator) < 0) result.add(row);

    return result;
  } // distinct

  // TODO: not very efficient
  private List<List<ResultValue>> aggregate(List<List<ResultValue>> rows, List<String> allColumnNames, 
                                            HashMap<Integer, String> aggregateColumnIndexes)
    throws ResultException
  {
    List<List<ResultValue>> result = new ArrayList<List<ResultValue>>();
    RowComparator rowComparator = new RowComparator(allColumnNames, selectedColumnIndexes, true); 
    // Key is index of aggregated row in result, value is hash map of aggregate column index to list of original values.
    HashMap<Integer, HashMap<Integer, List<ResultValue>>> aggregatesMap = new HashMap<Integer, HashMap<Integer, List<ResultValue>>>();
    HashMap<Integer, List<ResultValue>> aggregateRowMap; // Map of column indexes to value lists; used to accumulate values for aggregation.
    List<ResultValue> values;
    ResultValue value;
    int rowIndex;

    for (List<ResultValue> row : rows) {
      rowIndex = Collections.binarySearch(result, row, rowComparator); // Find a row with the same values for non aggregated columns.

      if (rowIndex < 0) { // Row with same values for non aggregated columns not yet present in result.
        aggregateRowMap = new HashMap<Integer, List<ResultValue>>();

        for (Integer aggregateColumnIndex : aggregateColumnIndexes.keySet()) {
          values = new ArrayList<ResultValue>();
          value = row.get(aggregateColumnIndex.intValue());
          values.add(value);
          aggregateRowMap.put(aggregateColumnIndex, values);
        } // for
        aggregatesMap.put(Integer.valueOf(result.size()), aggregateRowMap);
        result.add(row);
        Collections.sort(result, rowComparator); // binary search is expecting a sorted list
      } else { // We found a row that has the same values for the non aggregated columns.
        aggregateRowMap = aggregatesMap.get(Integer.valueOf(rowIndex));
        for (Integer aggregateColumnIndex : aggregateColumnIndexes.keySet()) {
          values = aggregateRowMap.get(aggregateColumnIndex);
          value = row.get(aggregateColumnIndex); 
          values.add(value);
        } // for
      } // if
    } // for

    rowIndex = 0;
    for (List<ResultValue> row : result) {
      aggregateRowMap = aggregatesMap.get(Integer.valueOf(rowIndex));

      for (Integer aggregateColumnIndex : aggregateColumnIndexes.keySet()) {
        String aggregateFunctionName = aggregateColumnIndexes.get(aggregateColumnIndex);
        values = aggregateRowMap.get(aggregateColumnIndex);

        // We have checked in addRowData that only numeric data are added for sum, max, min, and avg
        if (aggregateFunctionName.equalsIgnoreCase(MinAggregateFunction)) value = min(values);
        else if (aggregateFunctionName.equalsIgnoreCase(MaxAggregateFunction)) value = max(values);
        else if (aggregateFunctionName.equalsIgnoreCase(SumAggregateFunction)) value = sum(values);
        else if (aggregateFunctionName.equalsIgnoreCase(AvgAggregateFunction)) value = avg(values);
        else if (aggregateFunctionName.equalsIgnoreCase(CountAggregateFunction)) value = count(values);
        else throw new InvalidAggregateFunctionNameException("Invalid aggregate function '" + aggregateFunctionName + "'");

        row.set(aggregateColumnIndex.intValue(), value);
      } // for
      rowIndex++;
    } // for

    return result;
  } // aggregate

  private List<List<ResultValue>> orderBy(List<List<ResultValue>> rows, List<String> allColumnNames, 
                                          List<Integer> orderByColumnIndexes, boolean ascending)
    throws ResultException
  {
    List<List<ResultValue>> result = new ArrayList<List<ResultValue>>(rows);
    RowComparator rowComparator = new RowComparator(allColumnNames, orderByColumnIndexes, ascending); 

    Collections.sort(result, rowComparator);

    return result;
  } // orderBy

  private DatatypeValue min(List<ResultValue> values) throws ResultException
  {
    DatatypeValue result = null, value;

    if (values.isEmpty()) throw new ResultException("empty aggregate list for '" + MinAggregateFunction + "'");

    for (ResultValue resultValue : values) {

      if (!(resultValue instanceof DatatypeValue))
        throw new ResultException("attempt to use '" + MinAggregateFunction + "' aggregate on non datatype '" + resultValue + "'");

      value = (DatatypeValue)resultValue;

      if (!value.isNumeric()) 
        throw new ResultException("attempt to use '" + MinAggregateFunction + "' aggregate on non numeric datatype '" + value + "'");

      if (result == null) result = value;
      else if (value.compareTo(result) < 0) result = value;
    } // for

    return result;
  } // min

  private DatatypeValue max(List<ResultValue> values) throws ResultException
  {
    DatatypeValue result = null, value;

    if (values.isEmpty()) throw new ResultException("empty aggregate list for '" + MaxAggregateFunction + "'");

    for (ResultValue resultValue : values) {

      if (!(resultValue instanceof DatatypeValue))
        throw new ResultException("attempt to use '" + MaxAggregateFunction + "' aggregate on non datatype '" + resultValue + "'");

      value = (DatatypeValue)resultValue;

      if (!value.isNumeric()) 
        throw new ResultException("attempt to use '" + MaxAggregateFunction + "' aggregate on non numeric datatype '" + value + "'");

      if (result == null) result = value;
      else if (value.compareTo(result) > 0) result = value;
    } // for

    return result;
  } // max

  // We return a BigDecimal object for the moment.
  private DatatypeValue sum(List<ResultValue> values) throws ResultException
  {
    BigDecimal sum = new BigDecimal(0), value;

    if (values.isEmpty()) throw new ResultException("empty aggregate list for '" + SumAggregateFunction + "'");

    for (ResultValue resultValue : values) {

      if (!(resultValue instanceof DatatypeValue))
        throw new ResultException("attempt to use '" + SumAggregateFunction + "' aggregate on non datatype '" + resultValue + "'");

      try {
        value = new BigDecimal(((DatatypeValue)resultValue).toString());
      } catch (NumberFormatException e) {
        throw new ResultException("attempt to use '" + SumAggregateFunction + "' aggregate on non numeric datatype '" + resultValue + "'");
      } // try

      sum = sum.add(value);
    } // for

    return new LiteralInfo(sum);
  } // sum

  // We return a BigDecimal object for the moment.
  private DatatypeValue avg(List<ResultValue> values) throws ResultException
  {
    BigDecimal sum = new BigDecimal(0), value;
    int count = 0;

    if (values.isEmpty()) throw new ResultException("empty aggregate list for '" + AvgAggregateFunction + "'");

    for (ResultValue resultValue : values) {

      if (!(resultValue instanceof DatatypeValue))
        throw new ResultException("attempt to use '" + AvgAggregateFunction + "' aggregate on non datatype '" + resultValue + "'");

      try {
        value = new BigDecimal(((DatatypeValue)resultValue).toString());
      } catch (NumberFormatException e) {
        throw new ResultException("attempt to use '" + AvgAggregateFunction + "' aggregate on non numeric datatype '" + resultValue + "'");
      } // try

      count++;
      sum = sum.add(value);
    } // for

    return new LiteralInfo(sum.divide(new BigDecimal(count), BigDecimal.ROUND_DOWN));
  } // sum

  private DatatypeValue count(List<ResultValue> values) throws ResultException
  {
    return new LiteralInfo(values.size());
  } // count

  // Quick and dirty: all checking left to the Java runtime.
  private static class RowComparator implements Comparator<List<ResultValue>>, Serializable
  {
    private List<Integer> orderByColumnIndexes;
    private boolean ascending;
    
    public RowComparator(List<String> allColumnNames, List<Integer> orderByColumnIndexes, boolean ascending)
    {
      this.ascending = ascending;
      this.orderByColumnIndexes = orderByColumnIndexes;
    } // RowComparator
    
    public RowComparator(List<String> allColumnNames, boolean ascending)
    {
      this.ascending = ascending;
      orderByColumnIndexes = new ArrayList<Integer>();

      for (String columnName : allColumnNames) orderByColumnIndexes.add(allColumnNames.indexOf(columnName));
    } // RowComparator

    public int compare(List<ResultValue> row1, List<ResultValue> row2) 
    {
      for (Integer columnIndex : orderByColumnIndexes) {
        int result = ((Comparable)row1.get(columnIndex)).compareTo(((Comparable)row2.get(columnIndex)));
        if (result != 0) if (ascending) return result; else return -result;
      } // for

      return 0;
    } // compare
  } // RowComparator

} // ResultImpl
