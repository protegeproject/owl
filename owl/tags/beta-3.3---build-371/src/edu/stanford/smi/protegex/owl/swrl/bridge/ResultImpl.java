
package edu.stanford.smi.protegex.owl.swrl.bridge;

import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.*;

import java.util.*;

/*
** This class implements the interfaces Result and ResultGenerator. It can be used to generate a result structure and populate it with data;
** it can also be used to retrieve those data from the result.
**
** This class operates in three phases:
**
** Configuration Phase: In this phase the structure of the result is defined. This phase opened by a call to the configure() method (which
** will also clear any existing data). In this phase the columns are defined; aggregation or ordering is also specified in this phase. This
** phase is closed by a call to the configured() method.
**
** Preparation Phase: In this phase data are added to the result. This phase is implicitly opened by the call to the configured() method. It
** is closed by a call to the prepared() method.
**
** The interface ResultGenerator defines the calls used in these two phases.
**
** Processing Phase: In this phase data may be retrieved from the result. This phase is implicitly opened by the call to the closed()
** method.
**
** The interface Result defines the calls used in the processing phase.
**
** An example configuration and data generation would be as follows:
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
** The result is now available for reading. The interface Result defines the accessor methods. A row consists of a sequence of
** objects defined by the interface Value. There are two possible types of values: DatatypeValue, represinting literals, and 
** ObjectValue, represening OWL individuals.
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
  private List<String> allColumnNames, selectedColumnNames, datatypeColumnNames, orderByColumnNames, displayColumnNames;
  private HashMap<String, String> aggregateColumns; // Map of (name, function) pairs
  private List<List<Value>> rows; // List of List of Value objects.
  private List<Value> rowData; // List of Value objects used when assembling a row.
  private int numberOfColumns, rowIndex, rowDataIndex;
  private boolean isConfigured, isPrepared, isRowOpen, isOrdered, isDescending, isDistinct, hasAggregates;
  private RowComparator rowComparator;

  public static String aggregateFunctionNames[] = { MinAggregateFunction, MaxAggregateFunction, SumAggregateFunction,
						    AvgAggregateFunction, CountAggregateFunction };
  public ResultImpl() 
  {
    initialize();
  } // Result
  
  // Configuration phase methods.

  public boolean isConfigured() { return isConfigured; }
  public boolean isRowOpen() { return isRowOpen; }
  public boolean isDistinct() { return isDistinct; }

  public void initialize()
  {
    isConfigured = false;
    isPrepared = false;
    isRowOpen = false;
    
    // The following variables will not be externally valid until configured() is called. 
    allColumnNames = new ArrayList<String>();
    aggregateColumns = new HashMap<String, String>();
    selectedColumnNames = new ArrayList<String>();
    datatypeColumnNames = new ArrayList<String>();
    orderByColumnNames = new ArrayList<String>();
    displayColumnNames = new ArrayList<String>();

    numberOfColumns = 0; isOrdered = isDescending = isDistinct = false;

    // The following variables will not be externally valid until prepared() is called.
    rowIndex = -1; // If there are no rows in the final result, it will remain at -1.
    rows = new ArrayList<List<Value>>();
  } // prepare

  public void setIsOrdered() throws ResultException
  {
    throwExceptionIfAlreadyConfigured();

    isOrdered = true;
  } // setIsOrdered

  public void setIsDescending() throws ResultException
  {
    throwExceptionIfAlreadyConfigured();

    isDescending = true;
  } // setIsDescending

  public void addSelectedObjectColumn(String columnName) throws ResultException
  {
    throwExceptionIfAlreadyConfigured();

    System.err.println("addSelectedObjectColumn: " + columnName);

    selectedColumnNames.add(columnName);
    allColumnNames.add(columnName);
    numberOfColumns++;
  } // addSelectedIndividualColumn

  public void addSelectedDatatypeColumn(String columnName) throws ResultException
  {
    throwExceptionIfAlreadyConfigured();
    
    System.err.println("addSelectedDatatypeColumn: " + columnName);

    selectedColumnNames.add(columnName);
    datatypeColumnNames.add(columnName);
    allColumnNames.add(columnName);
    numberOfColumns++;
  } // addSelectedIndividualColumn

  public void addAggregateColumn(String aggregateColumnName, String aggregateFunctionName) throws ResultException
  {
    throwExceptionIfAlreadyConfigured();

    checkAggregateFunctionName(aggregateFunctionName);

    System.err.println("addAggregateColumn: " + aggregateColumnName + ", function = " + aggregateFunctionName);
    
    aggregateColumns.put(aggregateColumnName, aggregateFunctionName);
    allColumnNames.add(aggregateColumnName);
    datatypeColumnNames.add(aggregateColumnName); // Can only aggregate over datatype values.
    numberOfColumns++;
  } // addAggregateColumn
  
  public void addOrderByColumn(String orderByColumnName) throws ResultException
  {
    throwExceptionIfAlreadyConfigured();

    System.err.println("addOrderByColumn: " + orderByColumnName);
    
    orderByColumnNames.add(orderByColumnName);
  } // addOrderByColumn
    
  public void setColumnDisplayName(String columnName) throws ResultException
  {
    System.err.println("setColumnDisplayName: " + columnName);

    displayColumnNames.add(columnName);
  } // setColumnDisplayName

  public void configured() throws ResultException
  {
    Set<String> aggregateColumnNames = aggregateColumns.keySet();

    throwExceptionIfAlreadyConfigured();

    if (containsOneOf(selectedColumnNames, aggregateColumnNames))
      throw new InvalidQueryException("Aggregate columns cannot also be selected columns");
    if (!selectedColumnNames.containsAll(orderByColumnNames))
      throw new InvalidQueryException("All ordered columns must be selected");
    if (containsOneOf(aggregateColumnNames, orderByColumnNames))
      throw new InvalidQueryException("Ordered columns cannot be aggregate columns");
    	    
    rowComparator = new RowComparator(allColumnNames, orderByColumnNames, isOrdered && !isDescending);

    hasAggregates = !aggregateColumnNames.isEmpty();

    isConfigured = true;
  } // configured

  // Methods used to retrieve result structure after result has been configured.

  public boolean isPrepared() { return isPrepared; }
  public boolean isOrdered() { return isOrdered; }
  public boolean isDescending() { return isDescending; }

  public int getNumberOfColumns() throws ResultException
  {
    throwExceptionIfNotConfigured();

    return numberOfColumns; 
  } // getNumberOfColumns
  
  public List getColumnNames() throws ResultException
  {
    throwExceptionIfNotConfigured();

    return allColumnNames; 
  } // getColumnNames
  
  public String getColumnName(int columnIndex) throws ResultException
  {
    throwExceptionIfNotConfigured();

    checkColumnIndex(columnIndex);
    
    return allColumnNames.get(columnIndex);
  } // getColumnName

  // Methods used to add data after result has been configured.

  public void openRow() throws ResultException
  {
    throwExceptionIfNotConfigured(); throwExceptionIfAlreadyPrepared(); throwExceptionIfRowOpen();

    rowDataIndex = 0;
    rowData = new ArrayList<Value>();

    isRowOpen = true;
  } // openRow

  public void addRowData(Value value) throws ResultException
  {
    throwExceptionIfNotConfigured(); throwExceptionIfAlreadyPrepared(); throwExceptionIfRowNotOpen();

    if (rowDataIndex == getNumberOfColumns()) throw new ResultStateException("Attempt to add data beyond the end of a row");

    System.err.println("addRowData: rowDataIndex: " + rowDataIndex + ", value: " + value);

    if (aggregateColumns.keySet().contains(allColumnNames.get(rowDataIndex))) 
      if (!isNumericValue(value))
        throw new ResultException("Attempt to add non numeric value '" + value + "' to aggregate column '" + allColumnNames.get(rowDataIndex) + "'.");

    rowData.add(value);
    rowDataIndex++;

    if (rowDataIndex == getNumberOfColumns()) closeRow();
  } // addData    

  public void closeRow() throws ResultException
  {
    throwExceptionIfNotConfigured(); throwExceptionIfAlreadyPrepared(); throwExceptionIfRowNotOpen();

    rows.add(rowData);

    System.err.println("close row: size: " + rows.size());

    isRowOpen = false;
  } // closeRow

  public void prepared() throws ResultException
  {
    throwExceptionIfNotConfigured(); throwExceptionIfAlreadyPrepared(); throwExceptionIfRowOpen();

    if (getNumberOfRows() > 0) rowIndex = 0;

    if (!orderByColumnNames.isEmpty()) Collections.sort(rows, rowComparator);

    if (hasAggregates) rows = aggregate(rows, allColumnNames, aggregateColumns); // Aggregation implies killing distinct rows.
    else if (isDistinct) rows = distinct(rows);

    isPrepared = true;
  } // prepared

  // Methods used to retrieve data after result has been prepared.

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
    throwExceptionIfNotConfigured(); throwExceptionIfNotPrepared();

    if (getNumberOfRows() == 0) throw new ResultStateException("Attempt to process empty result");
    
    if (!hasNext()) throw new ResultException("Attempt to process past end of result");

    if (rowIndex != -1 && rowIndex < getNumberOfRows() - 1) rowIndex++;
  } // next
  
  public boolean hasNext() throws ResultException
  { 
    throwExceptionIfNotConfigured(); throwExceptionIfNotPrepared();

    return (rowIndex != -1 && rowIndex < getNumberOfRows() - 1);
  } // hasNext
    
  public List<Value> getRow() throws ResultException
  {
    throwExceptionIfNotConfigured(); throwExceptionIfNotPrepared();

    return (List)rows.get(rowIndex);
  } // getRow

  public Value getValue(String columnName) throws ResultException
  {
    List row;
    int columnIndex;
    
    throwExceptionIfNotConfigured(); throwExceptionIfNotPrepared();

    checkColumnName(columnName);
    
    columnIndex = allColumnNames.indexOf(columnName);
    
    row = (List)rows.get(rowIndex);
    return (Value)row.get(columnIndex);
  } // getColumnValue
  
  public Value getValue(int columnIndex) throws ResultException
  {
    List row;

    throwExceptionIfNotConfigured(); throwExceptionIfNotPrepared();

    checkColumnIndex(columnIndex);
    
    row = (List)rows.get(rowIndex);
    return (Value)row.get(columnIndex);
  } // getColumnValue

    public Value getValue(int columnIndex, int rowIndex) 
	throws ResultException
    {
	Value value = null;

	throwExceptionIfNotConfigured(); throwExceptionIfNotPrepared();
	checkColumnIndex(columnIndex); checkRowIndex(rowIndex); 

	return (Value)((List)rows.get(rowIndex)).get(columnIndex);
    } // getValue

    public ObjectValue getObjectValue(String columnName) throws ResultException
    {
	if (!hasObjectValue(columnName)) 
	    throw new InvalidColumnTypeException("Expecting ObjectValue type for column '" + columnName + "'.");
	return (ObjectValue)getValue(columnName);
    } // getObjectValue

    public ObjectValue getObjectValue(int columnIndex) throws ResultException
    {
	return getObjectValue(getColumnName(columnIndex));
    } // getObjectValue

    public DatatypeValue getDatatypeValue(String columnName) throws ResultException
    {
	if (!hasDatatypeValue(columnName)) 
	    throw new InvalidColumnTypeException("Expecting DatatypeValue type for column '" + columnName + "'.");
	return (DatatypeValue)getValue(columnName);
    } // getDatatypeValue

    public DatatypeValue getDatatypeValue(int columnIndex) throws ResultException
    {
	return getDatatypeValue(getColumnName(columnIndex));
    } // getDatatypeValue

    public boolean hasObjectValue(String columnName) throws ResultException
    {
	return !datatypeColumnNames.contains(columnName);
    } // hasObjectValue

    public boolean hasObjectValue(int columnIndex) throws ResultException
    {
	return !datatypeColumnNames.contains(getColumnName(columnIndex));
    } // hasObjectValue

    public boolean hasDatatypeValue(String columnName) throws ResultException
    {
	return datatypeColumnNames.contains(columnName);
    } // hasDatatypeValue

    public boolean hasDatatypeValue(int columnIndex) throws ResultException
    {
	return datatypeColumnNames.contains(getColumnName(columnIndex));
    } // hasDatatypeValue

  // Phase verification exception throwing methods.
  
  private void throwExceptionIfNotConfigured() throws ResultException
  {
    if (!isConfigured()) throw new ResultStateException("Attempt to add data to unconfigured result");
  } // throwExceptionIfNotConfigured

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
    if (isPrepared()) throw new ResultStateException("Attempt to add data to prepared result");
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

  private void checkIsObjectValue(String columnName) throws ResultException
  {
    if (!hasObjectValue(columnName)) throw new InvalidColumnTypeException("Expecting ObjectValue type for column '" + columnName + "'.");
  } // checkIsObjectValue

  private void checkIsObjectValue(int columnIndex) throws ResultException
  {
    checkIsObjectValue(getColumnName(columnIndex)); // getColumnName will throw InvalidColumnIndexException if appropriate.
  } // checkIsObjectValue

  private void checkIsDatatypeValue(String columnName) throws ResultException
  {
    if (!hasDatatypeValue(columnName)) 
      throw new InvalidColumnTypeException("Expecting DatatypeValue type for column '" + columnName + "'.");
  } // checkIsDatatypeValue

  private void checkIsDatatypeValue(int columnIndex) throws ResultException
  {
    checkIsDatatypeValue(getColumnName(columnIndex)); 
  } // checkIsDatatypeValue

  private boolean isNumericValue(Value value)
  {
    return ((value instanceof DatatypeValue) || (((DatatypeValue)value).isNumeric()));
  } // isNumericValue

  private List<List<Value>> distinct(List<List<Value>> rows)
  {
    List<List<Value>> result = new ArrayList<List<Value>>();
    RowComparator rowComparator = new RowComparator(allColumnNames, true); // Look at the entire row.

    for (List<Value> row : rows) if (Collections.binarySearch(result, row, rowComparator) < 0) result.add(row);

    return result;
  } // killDuplicateRows

  private List<List<Value>> aggregate(List<List<Value>> rows, List<String> allColumnNames, HashMap<String, String> aggregateColumns)
    throws ResultException
  {
    List<List<Value>> result = new ArrayList<List<Value>>();
    RowComparator rowComparator = new RowComparator(allColumnNames, selectedColumnNames, true); 
    // Key is index of aggregated row in result, value is hash map of aggregate column name to list of original values.
    HashMap<Integer, HashMap<String, List<DatatypeValue>>> aggregatesMap = new HashMap<Integer, HashMap<String, List<DatatypeValue>>>();
    HashMap<String, List<DatatypeValue>> aggregateRowMap; // Map of column names to value lists; used to accuumulate values for aggregation.
    List<DatatypeValue> values;
    DatatypeValue value;
    int rowIndex;

    for (List<Value> row : rows) {
      rowIndex = Collections.binarySearch(result, row, rowComparator);
      if (rowIndex < 0) { // Row with same values for non aggregated columns not yet present in result.
        aggregateRowMap = new HashMap<String, List<DatatypeValue>>();

        for (String aggregateColumnName : aggregateColumns.keySet()) {
          values = new ArrayList<DatatypeValue>();
          value = (DatatypeValue)row.get(allColumnNames.indexOf(aggregateColumnName)); // We will have checked earlier to ensure its type.
          values.add(value);
          aggregateRowMap.put(aggregateColumnName, values);        
        } // for
        aggregatesMap.put(new Integer(result.size()), aggregateRowMap);
        result.add(row);
        
      } else { // We found a row that has the same values for the non aggregated columns.
        aggregateRowMap = aggregatesMap.get(new Integer(rowIndex));
        
        for (String aggregateColumnName : aggregateColumns.keySet()) {
          values = aggregateRowMap.get(aggregateColumnName);
          value = (DatatypeValue)row.get(allColumnNames.indexOf(aggregateColumnName)); // We will have checked earlier to ensure its type.
          values.add(value);
        } // for
      } // if
    } // for

    rowIndex = 0;
    for (List<Value> row : result) {
      aggregateRowMap = aggregatesMap.get(new Integer(rowIndex));

      for (String aggregateColumnName : aggregateColumns.keySet()) {
        String aggregateFunctionName = aggregateColumns.get(aggregateColumnName);
        values = aggregateRowMap.get(aggregateColumnName);

        if (aggregateFunctionName.equalsIgnoreCase(MinAggregateFunction)) value = min(values);
        else if (aggregateFunctionName.equalsIgnoreCase(MaxAggregateFunction)) value = max(values);
        else if (aggregateFunctionName.equalsIgnoreCase(SumAggregateFunction)) value = sum(values);
        else if (aggregateFunctionName.equalsIgnoreCase(AvgAggregateFunction)) value = avg(values);
        else if (aggregateFunctionName.equalsIgnoreCase(CountAggregateFunction)) value = count(values);
        else throw new InvalidAggregateFunctionNameException("Invalid aggregate function '" + aggregateFunctionName + "'.");
      } // for
      rowIndex++;
    } // for
    return result;
  } // distinct

  private DatatypeValue min(List<DatatypeValue> values) throws ResultException
  {
    return new LiteralInfo(-1); // TODO
  } // min

  private DatatypeValue max(List<DatatypeValue> values) throws ResultException
  {
    return new LiteralInfo(-1); // TODO
  } // max

  private DatatypeValue sum(List<DatatypeValue> values) throws ResultException
  {
    return new LiteralInfo(-1); // TODO
  } // sum

  private DatatypeValue avg(List<DatatypeValue> values) throws ResultException
  {
    return new LiteralInfo(-1); // TODO
  } // avg

  private DatatypeValue count(List<DatatypeValue> values) throws ResultException
  {
    return new LiteralInfo(values.size());
  } // count

  // Quick and dirty: all checking left to the Java runtime.
  private class RowComparator implements Comparator<List<Value>>
  {
    private List<Integer> orderByColumnIndexes;
    private boolean ascending;
    
    public RowComparator(List<String> allColumnNames, List<String> orderByColumnNames, boolean ascending)
    {
      this.ascending = ascending;
      for (String columnName : orderByColumnNames) orderByColumnIndexes.add(allColumnNames.indexOf(columnName));
    } // RowComparator
    
    public RowComparator(List<String> allColumnNames, boolean ascending)
    {
      this.ascending = ascending;
      for (String columnName : allColumnNames) orderByColumnIndexes.add(allColumnNames.indexOf(columnName));
    } // RowComparator

    public int compare(List<Value> row1, List<Value> row2) 
    {
      for (Integer columnIndex : orderByColumnIndexes) {
        int result = ((Comparable)row1.get(columnIndex)).compareTo(((Comparable)row2.get(columnIndex)));
        if (result != 0) if (ascending) return result; else return -result;
      } // for
      return 0;
    } // compare
  } // RowComparator

} // ResultImpl
