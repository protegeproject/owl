
package edu.stanford.smi.protegex.owl.swrl.sqwrl;

import edu.stanford.smi.protegex.owl.swrl.sqwrl.exceptions.InvalidAggregateFunctionNameException;

import java.util.Set;
import java.util.HashSet;

public class SQWRLNames
{
  public static String SQWRLBuiltInLibraryName = "SQWRLBuiltIns";

  public static final String SQWRLNamespace = "http://sqwrl.stanford.edu/ontologies/built-ins/3.4/sqwrl.owl#";

  public static final String Select = SQWRLNamespace + "select";
  public static final String SelectDistinct = SQWRLNamespace + "selectDistinct";
  public static final String Count = SQWRLNamespace + "count";
  public static final String CountDistinct = SQWRLNamespace + "countDistinct";
  public static final String Avg = SQWRLNamespace + "avg";
  public static final String Min = SQWRLNamespace + "min";
  public static final String Max = SQWRLNamespace + "max";
  public static final String Sum = SQWRLNamespace + "sum";
  public static final String Median = SQWRLNamespace + "median";
  public static final String OrderBy = SQWRLNamespace + "orderBy";
  public static final String OrderByDescending = SQWRLNamespace + "orderByDescending";
  public static final String ColumnNames = SQWRLNamespace + "columnNames";

  public static final String MinAggregateFunction = "min"; 
  public static final String MaxAggregateFunction = "max"; 
  public static final String SumAggregateFunction = "sum"; 
  public static final String AvgAggregateFunction = "avg"; 
  public static final String MedianAggregateFunction = "median"; 
  public static final String CountAggregateFunction = "count"; 
  public static final String CountDistinctAggregateFunction = "countDistinct"; 

  // Collection operations
  public static final String MakeSet = SQWRLNamespace + "makeSet";
  public static final String MakeBag = SQWRLNamespace + "makeBag";
  public static final String GroupBy = SQWRLNamespace + "groupBy";
  
  // Single collection operations
  public static final String Size = SQWRLNamespace + "size";
  public static final String IsEmpty = SQWRLNamespace + "isEmpty";
  public static final String NotIsEmpty = SQWRLNamespace + "notIsEmpty";
  public static final String Contains = SQWRLNamespace + "contains";
  public static final String Greatest = SQWRLNamespace + "greatest";
  public static final String GreatestN = SQWRLNamespace + "greatestN";
  public static final String NotGreatestN = SQWRLNamespace + "notGreatestN";
  public static final String Least = SQWRLNamespace + "least";
  public static final String LeastN = SQWRLNamespace + "leastN";
  public static final String NotContains = SQWRLNamespace + "notContains";

  // Multi-collection operations
  public static final String Intersection = SQWRLNamespace + "intersection";
  public static final String Union = SQWRLNamespace + "union";
  public static final String Difference = SQWRLNamespace + "difference";
  public static final String Intersects = SQWRLNamespace + "intersects";
  public static final String NotIntersects = SQWRLNamespace + "notIntersects";
  
  private static final String headBuiltInNamesArray[] 
     = { Select, SelectDistinct, Count, CountDistinct, Avg, Min, Max, Sum, OrderBy, OrderByDescending, ColumnNames };

  private static final String collectionMakeBuiltInNamesArray[] = { MakeSet, MakeBag };
  private static final String collectionGroupBuiltInNamesArray[] = { GroupBy };
 
  private static final String singleCollectionOperationBuiltInNamesArray[] = { Size, IsEmpty, Contains, 
	  Greatest, GreatestN, NotGreatestN, Least, LeastN,
      NotIsEmpty, NotContains,
      Min, Max, Sum, Avg, Median};

  private static final String multiCollectionOperationBuiltInNamesArray[] = { Intersection, Union, Difference, Intersects, NotIntersects };

  private static final String createCollectionOperationBuiltInNamesArray[] = { Intersection, Union, Difference };

  public static  final String aggregateFunctionNames[] = { MinAggregateFunction, MaxAggregateFunction, SumAggregateFunction,
                                                           AvgAggregateFunction, MedianAggregateFunction,
                                                           CountAggregateFunction, CountDistinctAggregateFunction };

  private static Set<String> headBuiltInNames, collectionMakeBuiltInNames, collectionGroupBuiltInNames, collectionMakeAndGroupBuiltInNames, 
    collectionOperationBuiltInNames, singleCollectionOperationBuiltInNames,
    multiCollectionOperationBuiltInNames, createCollectionOperationBuiltInNames, sqwrlBuiltInNames; 

  static {
    headBuiltInNames = new HashSet<String>();
    collectionMakeBuiltInNames = new HashSet<String>();
    collectionGroupBuiltInNames = new HashSet<String>();
    collectionMakeAndGroupBuiltInNames = new HashSet<String>();
    singleCollectionOperationBuiltInNames = new HashSet<String>();
    multiCollectionOperationBuiltInNames = new HashSet<String>();
    collectionOperationBuiltInNames = new HashSet<String>();
    createCollectionOperationBuiltInNames = new HashSet<String>();
    sqwrlBuiltInNames =  new HashSet<String>();

    for (String builtInName : headBuiltInNamesArray) headBuiltInNames.add(builtInName);
    for (String builtInName : collectionMakeBuiltInNamesArray) collectionMakeBuiltInNames.add(builtInName);
    for (String builtInName : collectionGroupBuiltInNamesArray) collectionGroupBuiltInNames.add(builtInName);
    
    collectionMakeAndGroupBuiltInNames.addAll(collectionMakeBuiltInNames);
    collectionMakeAndGroupBuiltInNames.addAll(collectionGroupBuiltInNames);

    for (String builtInName : singleCollectionOperationBuiltInNamesArray) {
        singleCollectionOperationBuiltInNames.add(builtInName);
        collectionOperationBuiltInNames.add(builtInName);
      } // for 

    for (String builtInName : multiCollectionOperationBuiltInNamesArray) {
        multiCollectionOperationBuiltInNames.add(builtInName);
        collectionOperationBuiltInNames.add(builtInName);
    } // for 

    for (String builtInName : createCollectionOperationBuiltInNamesArray) {
      createCollectionOperationBuiltInNames.add(builtInName);
      collectionOperationBuiltInNames.add(builtInName);
    } // for

    sqwrlBuiltInNames.addAll(collectionMakeBuiltInNames);
    sqwrlBuiltInNames.addAll(collectionGroupBuiltInNames);
    sqwrlBuiltInNames.addAll(collectionOperationBuiltInNames);
  } // static

  public static Set<String> getSQWRLBuiltInNames() { return sqwrlBuiltInNames; }
  public static Set<String> getHeadBuiltInNames() { return headBuiltInNames; }
  public static Set<String> getCollectionMakeBuiltInNames() { return collectionMakeBuiltInNames; }
  public static Set<String> getCollectionGroupBuiltInNames() { return collectionGroupBuiltInNames; }
  public static Set<String> getCollectionMakeAndGroupBuiltInNames() { return collectionMakeAndGroupBuiltInNames; }
  public static Set<String> getCollectionOperationBuiltInNames() { return collectionOperationBuiltInNames; }

  public static boolean isSQWRLCollectionOperationBuiltIn(String builtInName) { return collectionOperationBuiltInNames.contains(builtInName); }
  public static boolean isSQWRLCollectionMakeBuiltIn(String builtInName) { return collectionMakeBuiltInNames.contains(builtInName); }
  public static boolean isSQWRLCollectionGroupBuiltIn(String builtInName) { return collectionGroupBuiltInNames.contains(builtInName); }
  public static boolean isSQWRLBuiltIn(String builtInName) { return sqwrlBuiltInNames.contains(builtInName); }

  public static void checkAggregateFunctionName(String aggregateFunctionName) throws InvalidAggregateFunctionNameException
  {
    boolean found = false;
    
    for (int i = 0; i < aggregateFunctionNames.length; i++) 
      if (aggregateFunctionNames[i].equalsIgnoreCase(aggregateFunctionName)) found = true;

    if (!found) throw new InvalidAggregateFunctionNameException("invalid aggregate function '" + aggregateFunctionName + "'");
  } // checkAggregateFunctionName

} // SQWRLNames
