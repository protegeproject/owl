
// TODO: orderBy/orderByDescending not yet implemented

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
public class SWRLBuiltInLibraryImpl implements SWRLBuiltInLibrary, QueryLibrary
{
  public static String QueryNamespace = QueryNames.QueryNamespace;
  
  private static String QuerySelect = QueryNamespace + ":" + "select";
  private static String QueryCount = QueryNamespace + ":" + "count";
  private static String QueryAvg = QueryNamespace + ":" + "avg";
  private static String QueryMin = QueryNamespace + ":" + "min";
  private static String QueryMax = QueryNamespace + ":" + "max";
  private static String QuerySum = QueryNamespace + ":" + "sum";
  private static String QueryOrderBy = QueryNamespace + ":" + "orderBy";
  private static String QueryOrderByDescending = QueryNamespace + ":" + "orderByDescending";
  private static String QueryDisplayNames = QueryNamespace + ":" + "displayNames";
  
  private static String queryBuiltInNamesArray[] = { QuerySelect, QueryCount, QueryAvg, QueryMin, QueryMax, QuerySum,
                                                     QueryOrderBy, QueryOrderByDescending, QueryDisplayNames };
  private static Set<String> queryBuiltInNames;

  private HashMap<String, ResultImpl> results;
  private SWRLRuleEngineBridge bridge;

  public void initialize(SWRLRuleEngineBridge bridge)
  {
    this.bridge = bridge;
    results = new HashMap<String, ResultImpl>();
    queryBuiltInNames = new HashSet<String>();

    for (String builtInName : queryBuiltInNamesArray) queryBuiltInNames.add(builtInName);
  } // initialize

  /**
   ** Get a result object for a particular rule. Return null if no result generated for this rule.
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
  
  /**
   ** Clear all query results.
   */
  public void clearQueryResults()
  {
    results = new HashMap<String, ResultImpl>();
  } // clearQueryResults

  public boolean select(List<Argument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkForUnboundArguments(arguments);
    SWRLBuiltInUtil.checkNumberOfArgumentsAtLeast(1, arguments.size());
    
    ResultImpl result = getResult(bridge.getCurrentBuiltInInvokingRuleName());

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
    
    return true;
  } // select
  
  public boolean count(List<Argument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkForUnboundArguments(arguments);
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(1, arguments.size());

    ResultImpl result = getResult(bridge.getCurrentBuiltInInvokingRuleName());
    Argument argument = (Argument)arguments.get(0);
    
    if (!result.isConfigured()) throwInternalQueryException("built-in called on unconfigured result");

    if (argument instanceof LiteralInfo) result.addRowData((DatatypeValue)argument);
    else if (argument instanceof IndividualInfo) result.addRowData((ObjectValue)argument);
    else if (argument instanceof ClassInfo) result.addRowData((ClassValue)argument);
    else if (argument instanceof PropertyInfo) result.addRowData((PropertyValue)argument);
    else throw new InvalidBuiltInArgumentException(0, "unknown type '" + argument.getClass() + "'");
    
    return true;
  } // count

  public boolean min(List<Argument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkForUnboundArguments(arguments);
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(1, arguments.size());

    ResultImpl result = getResult(bridge.getCurrentBuiltInInvokingRuleName());
    Argument argument = (Argument)arguments.get(0);
    
    if (!result.isConfigured()) throwInternalQueryException("built-in called on unconfigured result");

    if (argument instanceof LiteralInfo && ((LiteralInfo)argument).isNumeric()) result.addRowData((DatatypeValue)argument);
    else throw new InvalidBuiltInArgumentException(0, "expecting numeric literal, got '" + argument + "'");
    
    return true;
  } // count

  public boolean max(List<Argument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkForUnboundArguments(arguments);
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(1, arguments.size());

    ResultImpl result = getResult(bridge.getCurrentBuiltInInvokingRuleName());
    Argument argument = (Argument)arguments.get(0);
    
    if (!result.isConfigured()) throwInternalQueryException("built-in called on unconfigured result");

    if (argument instanceof LiteralInfo && ((LiteralInfo)argument).isNumeric()) result.addRowData((DatatypeValue)argument);
    else throw new InvalidBuiltInArgumentException(0, "expecting numeric literal, got '" + argument + "'");
    
    return true;
  } // count

  public boolean sum(List<Argument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkForUnboundArguments(arguments);
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(1, arguments.size());

    ResultImpl result = getResult(bridge.getCurrentBuiltInInvokingRuleName());
    Argument argument = (Argument)arguments.get(0);
    
    if (!result.isConfigured()) throwInternalQueryException("built-in called on unconfigured result");

    if (argument instanceof LiteralInfo && ((LiteralInfo)argument).isNumeric()) result.addRowData((DatatypeValue)argument);
    else throw new InvalidBuiltInArgumentException(0, "expecting numeric literal, got '" + argument + "'");
    
    return true;
  } // count

  public boolean avg(List<Argument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkForUnboundArguments(arguments);
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(1, arguments.size());

    ResultImpl result = getResult(bridge.getCurrentBuiltInInvokingRuleName());
    Argument argument = (Argument)arguments.get(0);
    
    if (!result.isConfigured()) throwInternalQueryException("built-in called on unconfigured result");

    if (argument instanceof LiteralInfo && ((LiteralInfo)argument).isNumeric()) result.addRowData((DatatypeValue)argument);
    else throw new InvalidBuiltInArgumentException(0, "expecting numeric literal, got '" + argument + "'");
    
    return true;
  } // count
  
  public boolean displayNames(List<Argument> arguments) throws BuiltInException
  {   
    return true;
  } // displayNames

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
      result = new ResultImpl();
      ruleInfo = bridge.getRuleInfo(ruleName);

      for (BuiltInAtomInfo builtInAtomInfo : ruleInfo.getBuiltInAtomsFromHead(queryBuiltInNames)) {
        String builtInName = builtInAtomInfo.getBuiltInName();

        for (Argument argument :  builtInAtomInfo.getArguments()) {
          BuiltInVariableInfo builtInVariableInfo;
          String columnName;
          int argumentIndex = 0;

          if (builtInName.equalsIgnoreCase(QuerySelect)) {
            if (argument instanceof BuiltInVariableInfo) {
              builtInVariableInfo = (BuiltInVariableInfo)argument;
              columnName = "?" + builtInVariableInfo.getVariableName();
            } else columnName = "[" + argument + "]";
            result.addSelectedColumn(columnName);
          } else if (builtInName.equalsIgnoreCase(QueryCount)) {
            if (argument instanceof BuiltInVariableInfo) {
              builtInVariableInfo = (BuiltInVariableInfo)argument;
              columnName = "count(?" + builtInVariableInfo.getVariableName() + ")";
            } else columnName = "[" + argument + "]";
            result.addAggregateColumn(columnName, ResultGenerator.CountAggregateFunction);
          } else if (builtInName.equalsIgnoreCase(QueryMin)) {
            if (argument instanceof BuiltInVariableInfo) {
              builtInVariableInfo = (BuiltInVariableInfo)argument;
              columnName = "min(?" + builtInVariableInfo.getVariableName() + ")";
            } else columnName = "min[" + argument + "]";
            result.addAggregateColumn(columnName, ResultGenerator.MinAggregateFunction);
          } else if (builtInName.equalsIgnoreCase(QueryMax)) {
            if (argument instanceof BuiltInVariableInfo) {
              builtInVariableInfo = (BuiltInVariableInfo)argument;
              columnName = "max(?" + builtInVariableInfo.getVariableName() + ")";
            } else columnName = "max[" + argument + "]";
            result.addAggregateColumn(columnName, ResultGenerator.MaxAggregateFunction);
          } else if (builtInName.equalsIgnoreCase(QuerySum)) {
            if (argument instanceof BuiltInVariableInfo) {
              builtInVariableInfo = (BuiltInVariableInfo)argument;
              columnName = "sum(?" + builtInVariableInfo.getVariableName() + ")";
            } else columnName = "sum[" + argument + "]";
            result.addAggregateColumn(columnName, ResultGenerator.SumAggregateFunction);
          } else if (builtInName.equalsIgnoreCase(QueryAvg)) {
            if (argument instanceof BuiltInVariableInfo) {
              builtInVariableInfo = (BuiltInVariableInfo)argument;
              columnName = "avg(?" + builtInVariableInfo.getVariableName() + ")";
            } else columnName = "avg[" + argument + "]";
            result.addAggregateColumn(columnName, ResultGenerator.AvgAggregateFunction);
          } else if (builtInName.equalsIgnoreCase(QueryDisplayNames)) {
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
