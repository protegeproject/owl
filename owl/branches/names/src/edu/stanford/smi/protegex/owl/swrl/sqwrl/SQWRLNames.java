
package edu.stanford.smi.protegex.owl.swrl.sqwrl;

import edu.stanford.smi.protegex.owl.swrl.sqwrl.exceptions.InvalidAggregateFunctionNameException;

import java.util.Set;
import java.util.HashSet;

public class SQWRLNames
{
  public static String SQWRLBuiltInLibraryName = "SQWRLBuiltIns";

  public static final String SQWRLPrefix = "sqwrl";

  public static final String MinAggregateFunction = "min"; 
  public static final String MaxAggregateFunction = "max"; 
  public static final String SumAggregateFunction = "sum"; 
  public static final String AvgAggregateFunction = "avg"; 
  public static final String CountAggregateFunction = "count"; 
  public static final String CountDistinctAggregateFunction = "countDistinct"; 

  public static final String Select = SQWRLPrefix + ":" + "select";
  public static final String SelectDistinct = SQWRLPrefix + ":" + "selectDistinct";
  public static final String Count = SQWRLPrefix + ":" + "count";
  public static final String CountDistinct = SQWRLPrefix + ":" + "countDistinct";
  public static final String Avg = SQWRLPrefix + ":" + "avg";
  public static final String Min = SQWRLPrefix + ":" + "min";
  public static final String Max = SQWRLPrefix + ":" + "max";
  public static final String Sum = SQWRLPrefix + ":" + "sum";
  public static final String OrderBy = SQWRLPrefix + ":" + "orderBy";
  public static final String OrderByDescending = SQWRLPrefix + ":" + "orderByDescending";
  public static final String ColumnNames = SQWRLPrefix + ":" + "columnNames";

  public static final String MakeSet = SQWRLPrefix + ":" + "makeSet";
  public static final String MakeList = SQWRLPrefix + ":" + "makeList";
  public static final String MakeOrderedList = SQWRLPrefix + ":" + "makeOrderedList";

  // Set operations
  public static final String Size = SQWRLPrefix + ":" + "size";
  public static final String IsEmpty = SQWRLPrefix + ":" + "isEmpty";
  public static final String Intersect = SQWRLPrefix + ":" + "intersect";
  public static final String Union = SQWRLPrefix + ":" + "union";
  public static final String Except = SQWRLPrefix + ":" + "except";
  public static final String Contains = SQWRLPrefix + ":" + "contains";
  public static final String First = SQWRLPrefix + ":" + "first";
  public static final String Last = SQWRLPrefix + ":" + "last";
  public static final String Nth = SQWRLPrefix + ":" + "nth";

  private static final String headBuiltInNamesArray[] 
     = { Select, SelectDistinct, Count, CountDistinct, Avg, Min, Max, Sum, OrderBy, OrderByDescending, ColumnNames };

  private static final String collectionMakeBuiltInNamesArray[] = { MakeSet, MakeList, MakeOrderedList };

  private static final String collectionOperationBuiltInNamesArray[] = { Size, IsEmpty, Intersect, Union, Except, Contains, First, Last, Nth };

  public static  final String aggregateFunctionNames[] = { MinAggregateFunction, MaxAggregateFunction, SumAggregateFunction,
                                                           AvgAggregateFunction, CountAggregateFunction, CountDistinctAggregateFunction };

  private static Set<String> headBuiltInNames, collectionMakeBuiltInNames, collectionOperationBuiltInNames, sqwrlBuiltInNames; 

  static {
    headBuiltInNames = new HashSet<String>();
    collectionMakeBuiltInNames = new HashSet<String>();
    collectionOperationBuiltInNames = new HashSet<String>();
    sqwrlBuiltInNames =  new HashSet<String>();

    for (String builtInName : headBuiltInNamesArray) headBuiltInNames.add(builtInName);
    for (String builtInName : collectionMakeBuiltInNamesArray) collectionMakeBuiltInNames.add(builtInName);
    for (String builtInName : collectionOperationBuiltInNamesArray) collectionOperationBuiltInNames.add(builtInName);

    sqwrlBuiltInNames.addAll(collectionMakeBuiltInNames); 
    sqwrlBuiltInNames.addAll(collectionOperationBuiltInNames);
  } // static

  public static Set<String> getHeadBuiltInNames() { return headBuiltInNames; }
  public static Set<String> getCollectionMakeBuiltInNames() { return collectionMakeBuiltInNames; }
  public static Set<String> getCollectionOperationBuiltInNames() { return collectionOperationBuiltInNames; }
  public static Set<String> getSQWRLBuiltInNames() { return sqwrlBuiltInNames; }

  public static void checkAggregateFunctionName(String aggregateFunctionName) throws InvalidAggregateFunctionNameException
  {
    boolean found = false;
    
    for (int i = 0; i < aggregateFunctionNames.length; i++) 
      if (aggregateFunctionNames[i].equalsIgnoreCase(aggregateFunctionName)) found = true;

    if (!found) throw new InvalidAggregateFunctionNameException("invalid aggregate function '" + aggregateFunctionName + "'");
  } // checkAggregateFunctionName

} // SQWRLNames
