
package edu.stanford.smi.protegex.owl.swrl.bridge.builtins.query;

import edu.stanford.smi.protegex.owl.swrl.bridge.query.*;

import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.swrl.model.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.builtins.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.query.exceptions.*;

import java.util.*;

/**
 ** Implementation library for SWRL query built-ins. See <a href="http://protege.cim3.net/cgi-bin/wiki.pl?SWRLQueryBuiltIns">here</a> for
 ** documentation on this built-in library.
 */
public class SWRLBuiltInLibraryImpl extends SWRLBuiltInLibrary implements QueryLibrary
{
  private static String QueryLibraryName = "SWRLQueryBuiltIns";

  public static final String Prefix = QueryNames.QueryPrefix + ":";
  
  private static String QuerySelect = Prefix + "select";
  private static String QuerySelectDistinct = Prefix + "selectDistinct";
  private static String QueryCount = Prefix + "count";
  private static String QueryAvg = Prefix + "avg";
  private static String QueryMin = Prefix + "min";
  private static String QueryMax = Prefix + "max";
  private static String QuerySum = Prefix + "sum";
  private static String QueryOrderBy = Prefix + "orderBy";
  private static String QueryOrderByDescending = Prefix + "orderByDescending";
  private static String QueryColumnNames = Prefix + "columnNames";
  
  private static String queryBuiltInNamesArray[] = { QuerySelect, QuerySelectDistinct, QueryCount, QueryAvg, QueryMin, QueryMax, QuerySum,
                                                     QueryOrderBy, QueryOrderByDescending, QueryColumnNames };
  private Set<String> queryBuiltInNames;

  private HashMap<String, ResultImpl> results;

  public SWRLBuiltInLibraryImpl() { super(QueryLibraryName); }

  public void reset()
  {
    results = new HashMap<String, ResultImpl>();

    queryBuiltInNames = new HashSet<String>();
    for (String builtInName : queryBuiltInNamesArray) queryBuiltInNames.add(builtInName);
  } // reset

  /**
   ** Get a result object for a particular rule. Return null if no result is generated for this rule.
   */
  public Result getQueryResult(String ruleName) throws ResultException
  {
    ResultImpl result = null;
    
    if (results.containsKey(ruleName)) {
      result = results.get(ruleName);
      if (!result.isPrepared()) result.prepared();
    } // if

    return result;
  } // getQueryResult
  
  public boolean select(List<Argument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkForUnboundArguments(arguments);
    SWRLBuiltInUtil.checkNumberOfArgumentsAtLeast(1, arguments.size());

    ResultImpl result = getResult(getInvokingRuleName());

    if (!result.isRowOpen()) result.openRow();

    int argumentIndex = 0;
    for (Argument argument : arguments) {
      if (argument instanceof LiteralInfo) result.addRowData((DatatypeValue)argument);
      else if (argument instanceof IndividualInfo) result.addRowData((ObjectValue)argument);
      else if (argument instanceof ClassInfo) result.addRowData((ClassValue)argument);
      else if (argument instanceof PropertyInfo) result.addRowData((PropertyValue)argument);
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
    Argument argument = (Argument)arguments.get(0);
    
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

  // TODO: clean this up.
  private ResultImpl configureResult(String ruleName) throws BuiltInException
  {
    ResultImpl result;
    RuleInfo ruleInfo;

    try {
      List<String> selectedVariableNames = new ArrayList<String>();
      result = new ResultImpl();
      ruleInfo = getInvokingBridge().getRuleInfo(ruleName);

      for (BuiltInAtomInfo builtInAtomInfo : ruleInfo.getBuiltInAtomsFromHead(queryBuiltInNames)) {
        String builtInName = builtInAtomInfo.getBuiltInName();

        for (Argument argument :  builtInAtomInfo.getArguments()) {
          BuiltInVariableInfo builtInVariableInfo;
          String variableName, columnName;
          int argumentIndex = 0, columnIndex;

          if (builtInName.equalsIgnoreCase(QuerySelect)) {
            if (argument instanceof BuiltInVariableInfo) {
              builtInVariableInfo = (BuiltInVariableInfo)argument;
              variableName = builtInVariableInfo.getVariableName();
              columnName = "?" + variableName;
              selectedVariableNames.add(variableName);
            } else columnName = "[" + argument + "]";
            result.addColumn(columnName);
          } else if (builtInName.equalsIgnoreCase(QuerySelectDistinct)) {
            if (argument instanceof BuiltInVariableInfo) {
              builtInVariableInfo = (BuiltInVariableInfo)argument;
              variableName = builtInVariableInfo.getVariableName();
              columnName = "count(?" + variableName + ")";
              selectedVariableNames.add(variableName);
            } else columnName = "[" + argument + "]";
            result.addColumn(columnName);
            result.setIsDistinct();
          } else if (builtInName.equalsIgnoreCase(QueryCount)) {
            if (argument instanceof BuiltInVariableInfo) {
              builtInVariableInfo = (BuiltInVariableInfo)argument;
              variableName = builtInVariableInfo.getVariableName();
              columnName = "count(?" + variableName + ")";
              selectedVariableNames.add(variableName);
            } else columnName = "[" + argument + "]";
            result.addAggregateColumn(columnName, ResultGenerator.CountAggregateFunction);
          } else if (builtInName.equalsIgnoreCase(QueryMin)) {
            if (argument instanceof BuiltInVariableInfo) {
              builtInVariableInfo = (BuiltInVariableInfo)argument;
              variableName = builtInVariableInfo.getVariableName();
              columnName = "min(?" + variableName + ")";
              selectedVariableNames.add(variableName);
            } else columnName = "min[" + argument + "]";
            result.addAggregateColumn(columnName, ResultGenerator.MinAggregateFunction);
          } else if (builtInName.equalsIgnoreCase(QueryMax)) {
            if (argument instanceof BuiltInVariableInfo) {
              builtInVariableInfo = (BuiltInVariableInfo)argument;
              variableName = builtInVariableInfo.getVariableName();
              columnName = "max(?" + variableName + ")";
              selectedVariableNames.add(variableName);
            } else columnName = "max[" + argument + "]";
            result.addAggregateColumn(columnName, ResultGenerator.MaxAggregateFunction);
          } else if (builtInName.equalsIgnoreCase(QuerySum)) {
            if (argument instanceof BuiltInVariableInfo) {
              builtInVariableInfo = (BuiltInVariableInfo)argument;
              variableName = builtInVariableInfo.getVariableName();
              columnName = "sum(?" + variableName + ")";
              selectedVariableNames.add(variableName);
            } else columnName = "sum[" + argument + "]";
            result.addAggregateColumn(columnName, ResultGenerator.SumAggregateFunction);
          } else if (builtInName.equalsIgnoreCase(QueryAvg)) {
            if (argument instanceof BuiltInVariableInfo) {
              builtInVariableInfo = (BuiltInVariableInfo)argument;
              variableName = builtInVariableInfo.getVariableName();
              columnName = "avg(?" + variableName + ")";
              selectedVariableNames.add(variableName);
            } else columnName = "avg[" + argument + "]";
            result.addAggregateColumn(columnName, ResultGenerator.AvgAggregateFunction);
          } else if (builtInName.equalsIgnoreCase(QueryOrderBy)) {
            if (!(argument instanceof BuiltInVariableInfo)) 
              throw new BuiltInException("only variables allowed for ordered columns - found '" + argument + "'");
            builtInVariableInfo = (BuiltInVariableInfo)argument;
            variableName = builtInVariableInfo.getVariableName();
            columnIndex = selectedVariableNames.indexOf(variableName);
            if (columnIndex != -1) result.addOrderByColumn(columnIndex, true);
            else throw new BuiltInException("variable ?" + variableName + " must be selected before it can be ordered");
          } else if (builtInName.equalsIgnoreCase(QueryOrderByDescending)) {
            if (!(argument instanceof BuiltInVariableInfo)) 
              throw new BuiltInException("only variables allowed for ordered columns - found '" + argument + "'");
            builtInVariableInfo = (BuiltInVariableInfo)argument;
            variableName = builtInVariableInfo.getVariableName();
            columnIndex = selectedVariableNames.indexOf(variableName);
            if (columnIndex != -1) result.addOrderByColumn(columnIndex, false);
            else throw new BuiltInException("variable ?" + variableName + " must be selected before it can be ordered");
          } else if (builtInName.equalsIgnoreCase(QueryColumnNames)) {
            if (argument instanceof LiteralInfo && ((LiteralInfo)argument).isString()) {
              LiteralInfo literalInfo = (LiteralInfo)argument;
              result.addColumnDisplayName(literalInfo.getString());
            } else throw new BuiltInException("only string literals allowed as column names - found '" + argument + "'");
          } // if
          argumentIndex++;
        } // for
      } // for
      result.configured();
      result.openRow();
    } catch (SWRLRuleEngineBridgeException e) {
      throw new BuiltInException("error configuring result for rule '" + ruleName + "': " + e.getMessage());
    } // try

    return result;
  } // configureResult
  
  private void throwInternalQueryException(String message) throws BuiltInException
  {
    throw new BuiltInException("internal query exception: " + message);
  } // throwInternalQueryException

} // SWRLBuiltInLibraryImpl
