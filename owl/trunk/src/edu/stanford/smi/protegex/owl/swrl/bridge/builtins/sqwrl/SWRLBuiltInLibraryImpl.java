
package edu.stanford.smi.protegex.owl.swrl.bridge.builtins.sqwrl;

import edu.stanford.smi.protegex.owl.swrl.bridge.query.*;

import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.swrl.model.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.builtins.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.query.exceptions.*;

import java.util.*;

/**
 ** Implementation library for SQWRL query built-ins. See <a href="http://protege.cim3.net/cgi-bin/wiki.pl?SQWRL">here</a> for documentation
 ** on this built-in library.
 */
public class SWRLBuiltInLibraryImpl extends SWRLBuiltInLibrary implements SQWRLQueryLibrary
{
  private static String SQWRLLibraryName = "SQWRLBuiltIns";

  //  public static final String Prefix = SQWRLNames.SQWRLPrefix + ":";
  public static final String Prefix = "sqwrl:";
  
  private static String SQWRLSelect = Prefix + "select";
  private static String SQWRLSelectDistinct = Prefix + "selectDistinct";
  private static String SQWRLCount = Prefix + "count";
  private static String SQWRLAvg = Prefix + "avg";
  private static String SQWRLMin = Prefix + "min";
  private static String SQWRLMax = Prefix + "max";
  private static String SQWRLSum = Prefix + "sum";
  private static String SQWRLOrderBy = Prefix + "orderBy";
  private static String SQWRLOrderByDescending = Prefix + "orderByDescending";
  private static String SQWRLColumnNames = Prefix + "columnNames";
  private static String SQWRLMakeSet = Prefix + "makeSet";
  private static String SQWRLMakeList = Prefix + "makeList";
  private static String SQWRLMakeOrderedList = Prefix + "makeOrderedList";
  private static String SQWRLElement = Prefix + "element";
  private static String SQWRLSize = Prefix + "size";
  private static String SQWRLEmpty = Prefix + "empty";
  private static String SQWRLIntersect = Prefix + "intersect";
  private static String SQWRLFirst = Prefix + "first";
  private static String SQWRLLast = Prefix + "last";
  private static String SQWRLNth = Prefix + "nth";
  
  private static String headBuiltInNamesArray[] = { SQWRLSelect, SQWRLSelectDistinct, 
						    SQWRLCount, SQWRLAvg, SQWRLMin, SQWRLMax, SQWRLSum,
						    SQWRLOrderBy, SQWRLOrderByDescending, 
						    SQWRLColumnNames };

  private static String collectionMakeBuiltInNamesArray[] = { SQWRLMakeSet, SQWRLMakeList, SQWRLMakeOrderedList };

  private static String collectionOperateBuiltInNamesArray[] = { SQWRLElement, SQWRLSize, 
                                                                 SQWRLFirst, SQWRLNth, SQWRLLast,
                                                                 SQWRLIntersect, SQWRLEmpty};

  private Set<String> headBuiltInNames, collectionMakeBuiltInNames, collectionOperateBuiltInNames, sqwrlBuiltInNames;

  private HashMap<String, ResultImpl> results;
  
  private HashMap<String, Set<Argument>> sets;
  private HashMap<String, List<Argument>> lists;
  private HashMap<String, List<Argument>> orderedLists;

  private HashSet<String> invocationPatterns;

  public SWRLBuiltInLibraryImpl() { super(SQWRLLibraryName); }

  /**
   ** Get a result a SQWRL query. Return null if no result is generated.
   */
  public Result getSQWRLResult(String ruleName) throws ResultException
  {
    ResultImpl result = null;
    
    if (results.containsKey(ruleName)) {
      result = results.get(ruleName);
      if (!result.isPrepared()) result.prepared();
    } // if

    return result;
  } // getSQWRLResult
  
  public void reset()
  {
    results = new HashMap<String, ResultImpl>();

    headBuiltInNames = new HashSet<String>();
    collectionMakeBuiltInNames = new HashSet<String>();
    collectionOperateBuiltInNames = new HashSet<String>();
    sqwrlBuiltInNames =  new HashSet<String>();

    for (String builtInName : headBuiltInNamesArray) headBuiltInNames.add(builtInName);
    for (String builtInName : collectionMakeBuiltInNamesArray) collectionMakeBuiltInNames.add(builtInName);
    for (String builtInName : collectionOperateBuiltInNamesArray) collectionOperateBuiltInNames.add(builtInName);

    sqwrlBuiltInNames.addAll(collectionMakeBuiltInNames); 
    sqwrlBuiltInNames.addAll(collectionOperateBuiltInNames);

    sets = new HashMap<String, Set<Argument>>();
    lists = new HashMap<String, List<Argument>>();
    orderedLists = new HashMap<String, List<Argument>>();

    invocationPatterns = new HashSet<String>();
  } // reset
  
  public boolean select(List<Argument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkForUnboundArguments(arguments);
    SWRLBuiltInUtil.checkNumberOfArgumentsAtLeast(1, arguments.size());

    ResultImpl result = getResult(getInvokingRuleName());

    if (!result.isRowOpen()) result.openRow();

    int argumentIndex = 0;
    for (Argument argument : arguments) {
      if (argument instanceof LiteralArgument) result.addRowData((DatatypeValue)argument);
      else if (argument instanceof IndividualArgument) result.addRowData((ObjectValue)argument);
      else if (argument instanceof ClassArgument) result.addRowData((ClassValue)argument);
      else if (argument instanceof PropertyArgument) result.addRowData((PropertyValue)argument);
      else throw new InvalidBuiltInArgumentException(argumentIndex, "unknown type '" + argument.getClass() + "'");
      argumentIndex++;
    } // for
    
    return false;
  } // select

  public boolean selectDistinct(List<Argument> arguments) throws BuiltInException
  {
    return select(arguments);
  } // selectDistinct
  
  public boolean count(List<Argument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkForUnboundArguments(arguments);
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(1, arguments.size());

    ResultImpl result = getResult(getInvokingRuleName());
    Argument argument = (Argument)arguments.get(0);

    if (!result.isRowOpen()) result.openRow();
    
    if (argument instanceof LiteralInfo) result.addRowData((DatatypeValue)argument);
    else if (argument instanceof IndividualInfo) result.addRowData((ObjectValue)argument);
    else if (argument instanceof ClassInfo) result.addRowData((ClassValue)argument);
    else if (argument instanceof PropertyInfo) result.addRowData((PropertyValue)argument);
    else throw new InvalidBuiltInArgumentException(0, "unknown type '" + argument.getClass() + "'");
    
    return false;
  } // count

  public boolean min(List<Argument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkForUnboundArguments(arguments);
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(1, arguments.size());

    ResultImpl result = getResult(getInvokingRuleName());
    Argument argument = (Argument)arguments.get(0);

    if (!result.isRowOpen()) result.openRow();
    
    if (argument instanceof LiteralInfo && ((LiteralInfo)argument).isNumeric()) result.addRowData((DatatypeValue)argument);
    else throw new InvalidBuiltInArgumentException(0, "expecting numeric literal, got '" + argument + "'");
    
    return false;
  } // count

  public boolean max(List<Argument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkForUnboundArguments(arguments);
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(1, arguments.size());

    ResultImpl result = getResult(getInvokingRuleName());
    Argument argument = (Argument)arguments.get(0);
    
    if (!result.isRowOpen()) result.openRow();

    if (argument instanceof LiteralInfo && ((LiteralInfo)argument).isNumeric()) result.addRowData((DatatypeValue)argument);
    else throw new InvalidBuiltInArgumentException(0, "expecting numeric literal, got '" + argument + "'");
    
    return false;
  } // count

  public boolean sum(List<Argument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkForUnboundArguments(arguments);
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(1, arguments.size());

    ResultImpl result = getResult(getInvokingRuleName());
    Argument argument = (Argument)arguments.get(0);
    
    if (!result.isRowOpen()) result.openRow();

    if (argument instanceof LiteralInfo && ((LiteralInfo)argument).isNumeric()) result.addRowData((DatatypeValue)argument);
    else throw new InvalidBuiltInArgumentException(0, "expecting numeric literal, got '" + argument + "'");
    
    return false;
  } // count

  public boolean avg(List<Argument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkForUnboundArguments(arguments);
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(1, arguments.size());

    ResultImpl result = getResult(getInvokingRuleName());
    Argument argument = arguments.get(0);
    
    if (!result.isRowOpen()) result.openRow();

    if (argument instanceof LiteralInfo && ((LiteralInfo)argument).isNumeric()) result.addRowData((DatatypeValue)argument);
    else throw new InvalidBuiltInArgumentException(0, "expecting numeric literal, got '" + argument + "'");
    
    return false;
  } // count

  // The use of columnNames, orderBy, orderByDescending is handled at initial processing by configureResult().
  
  public boolean columnNames(List<Argument> arguments) throws BuiltInException
  {   
    return false;
  } // columnNames

  public boolean orderBy(List<Argument> arguments) throws BuiltInException
  {   
    return false;
  } // orderBy

  public boolean orderByDescending(List<Argument> arguments) throws BuiltInException
  {   
    return false;
  } // orderByDescending

  public boolean makeSet(List<Argument> arguments) throws BuiltInException
  {
    System.err.println("makeSet1");
    String invocationPattern  = SWRLBuiltInUtil.createInvocationPattern(getInvokingBridge(), 
                                                                        getInvokingRuleName(), getInvokingBuiltInIndex(), 
                                                                        arguments.subList(1, arguments.size()));
    System.err.println("makeSet2");
    String collectionID = getCollectionIDInMake(arguments); // Does argument checking
    System.err.println("makeSet3");

    if (!invocationPatterns.contains(invocationPattern)) {
      invocationPatterns.add(invocationPattern);
      
      System.err.println("makeSet4");
      Argument value = arguments.get(1);
      Set<Argument> set;

      System.err.print("makeSet - setName: " + collectionID);
      System.err.println(", value: " + value);
      
      if (!sets.containsKey(collectionID)) sets.put(collectionID, new HashSet<Argument>());
      set = sets.get(collectionID);
      set.add(value);
    } // if

    if (SWRLBuiltInUtil.isUnboundArgument(0, arguments)) arguments.set(0, new LiteralInfo(collectionID));
    
    System.err.println("makeSet4");
    return true;
  } // makeSet

  public boolean makeList(List<Argument> arguments) throws BuiltInException
  {
    String collectionID = getCollectionIDInMake(arguments); // Does argument checking
    Argument value = arguments.get(1);
    List<Argument> list;
    
    System.err.print("makeList - collectionID: " + collectionID);
    System.err.println(", value: " + value);

    if (!lists.containsKey(collectionID)) lists.put(collectionID, new ArrayList<Argument>());
    list = lists.get(collectionID);
    list.add(value);
    
    if (SWRLBuiltInUtil.isUnboundArgument(0, arguments)) arguments.set(0, new LiteralInfo(collectionID));

    return true;
  } // makeList

  public boolean makeOrderedList(List<Argument> arguments) throws BuiltInException
  {
    String collectionID = getCollectionIDInMake(arguments); // Does argument checking
    Argument value = arguments.get(1);
    List<Argument> orderedList;
    
    System.err.print("makeOrderedList - collectionID: " + collectionID);
    System.err.println(", value: " + value);
    
    if (!orderedLists.containsKey(collectionID)) orderedLists.put(collectionID, new ArrayList<Argument>());
    orderedList = orderedLists.get(collectionID);
    orderedList.add(value);
    
    if (SWRLBuiltInUtil.isUnboundArgument(0, arguments)) arguments.set(0, new LiteralInfo(collectionID));

    return true;
  } // makeOrderedList

  public boolean empty(List<Argument> arguments) throws BuiltInException
  {
    String collectionID = getCollectionIDInOperation(arguments, 1); // Does argument checking
    boolean result = false;
    
    if (sets.containsKey(collectionID)) result = sets.get(collectionID).size() != 0;
    else if (lists.containsKey(collectionID)) result = lists.get(collectionID).size() != 0;
    else if (orderedLists.containsKey(collectionID)) result = orderedLists.get(collectionID).size() != 0;
    else throw new BuiltInException("internal error: no collection found for ID '" + collectionID + "'");

    return result;
  } // empty

  public boolean size(List<Argument> arguments) throws BuiltInException
  {
    String collectionID = getCollectionIDInOperation(arguments, 2); // Does argument checking
    long size = -1;
    boolean result;
    
    if (sets.containsKey(collectionID)) size = sets.get(collectionID).size();
    else if (lists.containsKey(collectionID)) size = lists.get(collectionID).size();
    else if (orderedLists.containsKey(collectionID)) size = orderedLists.get(collectionID).size();
    else throw new BuiltInException("internal error: no collection found for ID '" + collectionID + "'");

    if (SWRLBuiltInUtil.isUnboundArgument(0, arguments)) {
      arguments.set(0, new LiteralInfo(size)); // Bind the result to the first parameter
      result = true;
    } else {
      long argument1 = SWRLBuiltInUtil.getArgumentAsALong(0, arguments);
      result = (argument1 == size);
    } //if

    return result;
  } // size

  private boolean isSet(String collectionName) { return sets.containsKey(collectionName); }
  private boolean isList(String collectionName) { return lists.containsKey(collectionName); }
  private boolean isOrderedList(String collectionName) { return orderedLists.containsKey(collectionName); }

  private String getCollectionIDInMake(List<Argument> arguments) throws BuiltInException
  {
    return getCollectionIDInOperation(arguments, 2); // A make collection built-in has at least two arguments
  } // getCollectionIDInMake

  private String getCollectionIDInOperation(List<Argument> arguments, int coreArgumentNumber) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsAtLeast(coreArgumentNumber, arguments.size());
    String ruleName = getInvokingRuleName();
    int builtInIndex = getInvokingBuiltInIndex();

    String collectionName = ""; // TODO: getCollectionName(ruleName, builtInIndex); // We will already have stored the collection argument name
    boolean hasInvocationPatern  = (arguments.size() > coreArgumentNumber);
    String invocationPattern = !hasInvocationPatern ? "" : SWRLBuiltInUtil.createInvocationPattern(getInvokingBridge(), ruleName, 0, 
                                                                                                   arguments.subList(coreArgumentNumber, arguments.size()));
    return ruleName + ":" + collectionName + ":" + invocationPattern;
  } // getCollectionIDInOperation

  private ResultImpl getResult(String ruleName) throws BuiltInException
  {
    ResultImpl result;

    if (results.containsKey(ruleName)) result = results.get(ruleName);
    else {
      result = configureResult(ruleName);
      results.put(ruleName, result);
    } // else

    return result;
  } // getResult

  // TODO: clean this mess up.
  private ResultImpl configureResult(String ruleName) throws BuiltInException
  {
    Map<String, List<Argument>> makeCollectionPatternArguments = new HashMap<String, List<Argument>>(); 
    Set<String> collectionNames = new HashSet<String>();
    Set<String> collectionResultNames = new HashSet<String>();
    Set<String> cascadedUnboundVariableNames = new HashSet<String>();
    ResultImpl result;
    RuleInfo ruleInfo;

    try {
      List<String> selectedVariableNames = new ArrayList<String>();
      int builtInIndex = 0;

      result = new ResultImpl();
      ruleInfo = getInvokingBridge().getRuleInfo(ruleName);

      for (BuiltInAtomInfo builtInAtomInfo : ruleInfo.getBuiltInAtomsFromBody()) builtInAtomInfo.setBuiltInIndex(builtInIndex++);
      for (BuiltInAtomInfo builtInAtomInfo : ruleInfo.getBuiltInAtomsFromHead()) builtInAtomInfo.setBuiltInIndex(builtInIndex++);

      for (BuiltInAtomInfo builtInAtomInfo : ruleInfo.getBuiltInAtomsFromHead(headBuiltInNames)) {
        String builtInName = builtInAtomInfo.getBuiltInName();

        for (Argument argument : builtInAtomInfo.getArguments()) {
          BuiltInVariableInfo builtInVariableInfo;
          String variableName, columnName;
          int argumentIndex = 0, columnIndex;

          if (builtInName.equalsIgnoreCase(SQWRLSelect)) {
            if (argument instanceof BuiltInVariableInfo) {
              builtInVariableInfo = (BuiltInVariableInfo)argument;
              variableName = builtInVariableInfo.getVariableName();
              columnName = "?" + variableName;
              selectedVariableNames.add(variableName);
            } else columnName = "[" + argument + "]";
            result.addColumn(columnName);
          } else if (builtInName.equalsIgnoreCase(SQWRLSelectDistinct)) {
            if (argument instanceof BuiltInVariableInfo) {
              builtInVariableInfo = (BuiltInVariableInfo)argument;
              variableName = builtInVariableInfo.getVariableName();
              columnName = "count(?" + variableName + ")";
              selectedVariableNames.add(variableName);
            } else columnName = "[" + argument + "]";
            result.addColumn(columnName);
            result.setIsDistinct();
          } else if (builtInName.equalsIgnoreCase(SQWRLCount)) {
            if (argument instanceof BuiltInVariableInfo) {
              builtInVariableInfo = (BuiltInVariableInfo)argument;
              variableName = builtInVariableInfo.getVariableName();
              columnName = "count(?" + variableName + ")";
              selectedVariableNames.add(variableName);
            } else columnName = "[" + argument + "]";
            result.addAggregateColumn(columnName, ResultGenerator.CountAggregateFunction);
          } else if (builtInName.equalsIgnoreCase(SQWRLMin)) {
            if (argument instanceof BuiltInVariableInfo) {
              builtInVariableInfo = (BuiltInVariableInfo)argument;
              variableName = builtInVariableInfo.getVariableName();
              columnName = "min(?" + variableName + ")";
              selectedVariableNames.add(variableName);
            } else columnName = "min[" + argument + "]";
            result.addAggregateColumn(columnName, ResultGenerator.MinAggregateFunction);
          } else if (builtInName.equalsIgnoreCase(SQWRLMax)) {
            if (argument instanceof BuiltInVariableInfo) {
              builtInVariableInfo = (BuiltInVariableInfo)argument;
              variableName = builtInVariableInfo.getVariableName();
              columnName = "max(?" + variableName + ")";
              selectedVariableNames.add(variableName);
            } else columnName = "max[" + argument + "]";
            result.addAggregateColumn(columnName, ResultGenerator.MaxAggregateFunction);
          } else if (builtInName.equalsIgnoreCase(SQWRLSum)) {
            if (argument instanceof BuiltInVariableInfo) {
              builtInVariableInfo = (BuiltInVariableInfo)argument;
              variableName = builtInVariableInfo.getVariableName();
              columnName = "sum(?" + variableName + ")";
              selectedVariableNames.add(variableName);
            } else columnName = "sum[" + argument + "]";
            result.addAggregateColumn(columnName, ResultGenerator.SumAggregateFunction);
          } else if (builtInName.equalsIgnoreCase(SQWRLAvg)) {
            if (argument instanceof BuiltInVariableInfo) {
              builtInVariableInfo = (BuiltInVariableInfo)argument;
              variableName = builtInVariableInfo.getVariableName();
              columnName = "avg(?" + variableName + ")";
              selectedVariableNames.add(variableName);
            } else columnName = "avg[" + argument + "]";
            result.addAggregateColumn(columnName, ResultGenerator.AvgAggregateFunction);
          } else if (builtInName.equalsIgnoreCase(SQWRLOrderBy)) {
            if (!(argument instanceof BuiltInVariableInfo)) 
              throw new BuiltInException("only variables allowed for ordered columns - found '" + argument + "'");
            builtInVariableInfo = (BuiltInVariableInfo)argument;
            variableName = builtInVariableInfo.getVariableName();
            columnIndex = selectedVariableNames.indexOf(variableName);
            if (columnIndex != -1) result.addOrderByColumn(columnIndex, true);
            else throw new BuiltInException("variable ?" + variableName + " must be selected before it can be ordered");
          } else if (builtInName.equalsIgnoreCase(SQWRLOrderByDescending)) {
            if (!(argument instanceof BuiltInVariableInfo)) 
              throw new BuiltInException("only variables allowed for ordered columns - found '" + argument + "'");
            builtInVariableInfo = (BuiltInVariableInfo)argument;
            variableName = builtInVariableInfo.getVariableName();
            columnIndex = selectedVariableNames.indexOf(variableName);
            if (columnIndex != -1) result.addOrderByColumn(columnIndex, false);
            else throw new BuiltInException("variable ?" + variableName + " must be selected before it can be ordered");
          } else if (builtInName.equalsIgnoreCase(SQWRLColumnNames)) {
            if (argument instanceof LiteralInfo && ((LiteralInfo)argument).isString()) {
              LiteralInfo literalInfo = (LiteralInfo)argument;
              result.addColumnDisplayName(literalInfo.getString());
            } else throw new BuiltInException("only string literals allowed as column names - found '" + argument + "'");
	  } // if
          argumentIndex++;
        } // for
      } // for

      // Pre-process SQWRL make collection built-ins
      for (BuiltInAtomInfo builtInAtomInfo : ruleInfo.getBuiltInAtomsFromBody(collectionMakeBuiltInNames)) {
        int numberOfArguments = builtInAtomInfo.getNumberOfArguments();
        if (numberOfArguments < 2) throw new BuiltInException("all make collection built-ins must have at least two arguments");

        String collectionName = builtInAtomInfo.getArgumentVariableName(0); // First argument of the collection operators is the collection 
        System.err.println("make-collection built-in - collectionName: " + collectionName);
        boolean isFirstOccurence = !collectionNames.contains(collectionName);
        System.err.println("make-collection built-in - isFirstOccurence: " + isFirstOccurence);
        boolean hasPatternArguments = numberOfArguments > 2;
        System.err.println("make-collection built-in - hasPatternArguments: " + hasPatternArguments);
        boolean previousPatternUse = makeCollectionPatternArguments.containsKey(collectionName);
        System.err.println("make-collection built-in - previousPatternUse: " + previousPatternUse);

        ruleInfo.setIsSQWRL();

        if (isFirstOccurence) {
          collectionNames.add(collectionName);
          // Record the index of the built-in that introduces this collection
          //TODO: ruleInfo.addCollectionNameMapping(builtInAtomInfo.getBuiltInIndex(), collectionName); 
          System.err.println("make-collection built-in - index: " + builtInAtomInfo.getBuiltInIndex());
        } // if

        if (hasPatternArguments) {
          List<Argument> builtInArguments = builtInAtomInfo.getArguments();
          List<Argument> patternArguments = builtInArguments.subList(2, builtInArguments.size());
          if (previousPatternUse) throw new BuiltInException("pattern specified more than once for collection ?" + collectionName);
          makeCollectionPatternArguments.put(collectionName, patternArguments); // Append pattern arguments to built-in
          System.err.println("make-collection built-in - patternArguments: " + patternArguments);
        } // if

        if (previousPatternUse) 
          System.err.println("make-collection built-in - added previous arguments");
          builtInAtomInfo.addArguments(makeCollectionPatternArguments.get(collectionName)); // Append pattern arguments to built-in
      } // for

      // Pre-process SQWRL collection operation built-ins 
      for (BuiltInAtomInfo builtInAtomInfo : ruleInfo.getBuiltInAtomsFromBody()) {
        String builtInName = builtInAtomInfo.getBuiltInName();
        if (collectionOperateBuiltInNames.contains(builtInName)) {
          int numberOfArguments = builtInAtomInfo.getNumberOfArguments();
          if (numberOfArguments < 1) 
            throw new BuiltInException("all collection operation built-ins must have at least one argument");
          
          // Last argument of all collection operators is the collection itself
          String collectionName = builtInAtomInfo.getArgumentVariableName(builtInAtomInfo.getNumberOfArguments() - 1);
          
          if (numberOfArguments > 0) { // Check that operator is applied to known collection
            // First argument (if any) of collection operator is operation result
            String collectionResultName = builtInAtomInfo.getArgumentVariableName(0);
            
            if (!collectionNames.contains(collectionResultName)) 
              throw new BuiltInException("collection operator applied to unknown collection ?" + collectionResultName);
            
            collectionResultNames.add(collectionResultName);
          } // if
          
          // Record index of the built-in that uses this collection
          //TODO: ruleInfo.addCollectionNameMapping(builtInAtomInfo.getBuiltInIndex(), collectionName);
          
          builtInAtomInfo.addArguments(makeCollectionPatternArguments.get(collectionName)); // Append pattern arguments to built-in
          builtInAtomInfo.setUsesSQWRLVariables();
        } else if (!sqwrlBuiltInNames.contains(builtInName)) { 

          // Mark later non SQWRL built-ins that (directly or indirectly) use variables bound by collection operators
          if (builtInAtomInfo.usesAtLeastOneVariableOf(collectionResultNames) ||
              builtInAtomInfo.usesAtLeastOneVariableOf(cascadedUnboundVariableNames)) {
            builtInAtomInfo.usesSQWRLVariables();
            cascadedUnboundVariableNames.addAll(builtInAtomInfo.getUnboundArgumentVariableNames()); // Record its unbound variables
          } // if
        } // if
      } // for
      
      result.configured();
      result.openRow();
    } catch (SWRLRuleEngineBridgeException e) {
      throw new BuiltInException("error configuring result for rule '" + ruleName + "': " + e.getMessage());
    } // try

    return result;
  } // configureResult
  
  private void throwInternalSQWRLException(String message) throws BuiltInException
  {
    throw new BuiltInException("internal query exception: " + message);
  } // throwInternalSQWRLException

} // SWRLBuiltInLibraryImpl
