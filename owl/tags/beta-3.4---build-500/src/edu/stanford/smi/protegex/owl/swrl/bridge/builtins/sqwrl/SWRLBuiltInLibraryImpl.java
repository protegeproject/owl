
package edu.stanford.smi.protegex.owl.swrl.bridge.builtins.sqwrl;

import edu.stanford.smi.protegex.owl.swrl.exceptions.*;

import edu.stanford.smi.protegex.owl.swrl.sqwrl.*;
import edu.stanford.smi.protegex.owl.swrl.sqwrl.impl.*;
import edu.stanford.smi.protegex.owl.swrl.sqwrl.exceptions.*;

import edu.stanford.smi.protegex.owl.swrl.bridge.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.builtins.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;

import java.util.*;

/**
 ** Implementation library for SQWRL query built-ins. See <a href="http://protege.cim3.net/cgi-bin/wiki.pl?SQWRL">here</a> for documentation
 ** on this built-in library.
 */
public class SWRLBuiltInLibraryImpl extends AbstractSWRLBuiltInLibrary
{
  private HashMap<String, Set<BuiltInArgument>> sets;
  private HashMap<String, List<BuiltInArgument>> lists;
  private HashMap<String, SortedSet<BuiltInArgument>> sortedSets;

  private HashSet<String> invocationPatterns;

  public SWRLBuiltInLibraryImpl() { super(SQWRLNames.SQWRLBuiltInLibraryName); }

  private ArgumentFactory argumentFactory;
  
  public void reset()
  {
    sets = new HashMap<String, Set<BuiltInArgument>>();
    lists = new HashMap<String, List<BuiltInArgument>>();
    sortedSets = new HashMap<String, SortedSet<BuiltInArgument>>();

    invocationPatterns = new HashSet<String>();

    argumentFactory = ArgumentFactory.getFactory();
  } // reset
  
  public boolean select(List<BuiltInArgument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkForUnboundArguments(arguments);
    SWRLBuiltInUtil.checkNumberOfArgumentsAtLeast(1, arguments.size());

    ResultImpl result = getResult(getInvokingRuleName());

    if (!result.isRowOpen()) result.openRow();

    int argumentIndex = 0;
    for (BuiltInArgument argument : arguments) {
      if (argument instanceof DatatypeValueArgument) result.addRowData((DatatypeValue)argument);
      else if (argument instanceof IndividualArgument) result.addRowData((ObjectValue)argument);
      else if (argument instanceof ClassArgument) result.addRowData((ClassValue)argument);
      else if (argument instanceof PropertyArgument) result.addRowData((PropertyValue)argument);
      else throw new InvalidBuiltInArgumentException(argumentIndex, "unknown type '" + argument.getClass() + "'");
      argumentIndex++;
    } // for
    
    return false;
  } // select

  public boolean selectDistinct(List<BuiltInArgument> arguments) throws BuiltInException
  {
    return select(arguments);
  } // selectDistinct
  
  public boolean count(List<BuiltInArgument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkForUnboundArguments(arguments);
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(1, arguments.size());

    ResultImpl result = getResult(getInvokingRuleName());
    BuiltInArgument argument = arguments.get(0);

    if (!result.isRowOpen()) result.openRow();
    
    if (argument instanceof OWLDatatypeValue) result.addRowData((DatatypeValue)argument);
    else if (argument instanceof OWLIndividual) result.addRowData((ObjectValue)argument);
    else if (argument instanceof OWLClass) result.addRowData((ClassValue)argument);
    else if (argument instanceof OWLProperty) result.addRowData((PropertyValue)argument);
    else throw new InvalidBuiltInArgumentException(0, "unknown type '" + argument.getClass() + "'");
    
    return false;
  } // count

  public boolean countDistinct(List<BuiltInArgument> arguments) throws BuiltInException
  {
    return count(arguments);
  } // countDistinct

  public boolean min(List<BuiltInArgument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkForUnboundArguments(arguments);
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(1, arguments.size());

    ResultImpl result = getResult(getInvokingRuleName());
    BuiltInArgument argument = arguments.get(0);

    if (!result.isRowOpen()) result.openRow();
    
    if (argument instanceof OWLDatatypeValue && ((OWLDatatypeValue)argument).isNumeric()) result.addRowData((DatatypeValue)argument);
    else throw new InvalidBuiltInArgumentException(0, "expecting numeric literal, got '" + argument + "'");
    
    return false;
  } // min

  public boolean max(List<BuiltInArgument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkForUnboundArguments(arguments);
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(1, arguments.size());

    ResultImpl result = getResult(getInvokingRuleName());
    BuiltInArgument argument = arguments.get(0);
    
    if (!result.isRowOpen()) result.openRow();

    if (argument instanceof OWLDatatypeValue && ((OWLDatatypeValue)argument).isNumeric()) result.addRowData((DatatypeValue)argument);
    else throw new InvalidBuiltInArgumentException(0, "expecting numeric literal, got '" + argument + "'");
    
    return false;
  } // max

  public boolean sum(List<BuiltInArgument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkForUnboundArguments(arguments);
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(1, arguments.size());

    ResultImpl result = getResult(getInvokingRuleName());
    BuiltInArgument argument = arguments.get(0);
    
    if (!result.isRowOpen()) result.openRow();

    if (argument instanceof OWLDatatypeValue && ((OWLDatatypeValue)argument).isNumeric()) result.addRowData((DatatypeValue)argument);
    else throw new InvalidBuiltInArgumentException(0, "expecting numeric literal, got '" + argument + "'");
    
    return false;
  } // sum

  public boolean avg(List<BuiltInArgument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkForUnboundArguments(arguments);
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(1, arguments.size());

    ResultImpl result = getResult(getInvokingRuleName());
    Argument argument = arguments.get(0);
    
    if (!result.isRowOpen()) result.openRow();

    if (argument instanceof OWLDatatypeValue && ((OWLDatatypeValue)argument).isNumeric()) result.addRowData((DatatypeValue)argument);
    else throw new InvalidBuiltInArgumentException(0, "expecting numeric literal, got '" + argument + "'");
    
    return false;
  } // avg

  // The use of columnNames, orderBy, orderByDescending is handled at initial processing in the SWRLRule object.  
  public boolean columnNames(List<BuiltInArgument> arguments) throws BuiltInException { return false; } 
  public boolean orderBy(List<BuiltInArgument> arguments) throws BuiltInException { return false; } 
  public boolean orderByDescending(List<BuiltInArgument> arguments) throws BuiltInException { return false; }

  public boolean makeSet(List<BuiltInArgument> arguments) throws BuiltInException
  {
    String collectionID = getCollectionIDInMake(arguments); // Does argument checking
    BuiltInArgument value = arguments.get(1);
    Set<BuiltInArgument> set = sets.containsKey(collectionID) ? sets.get(collectionID) : new HashSet<BuiltInArgument>();
    String invocationPattern  = SWRLBuiltInUtil.createInvocationPattern(getInvokingBridge(), 
                                                                        getInvokingRuleName(), getInvokingBuiltInIndex(), getIsInConsequent(),
                                                                        arguments.subList(1, arguments.size()));
    if (!invocationPatterns.contains(invocationPattern)) {
      invocationPatterns.add(invocationPattern);
      set.add(value); if (!sets.containsKey(collectionID)) sets.put(collectionID, set);
    } // if
    
    if (SWRLBuiltInUtil.isUnboundArgument(0, arguments)) arguments.set(0, argumentFactory.createDatatypeValueArgument(collectionID));

    return true;
  } // makeSet

  public boolean makeList(List<BuiltInArgument> arguments) throws BuiltInException
  {
    String collectionID = getCollectionIDInMake(arguments); // Does argument checking
    List<BuiltInArgument> list = lists.containsKey(collectionID) ? lists.get(collectionID) : new ArrayList<BuiltInArgument>();
    BuiltInArgument value = arguments.get(1);
    String invocationPattern  = SWRLBuiltInUtil.createInvocationPattern(getInvokingBridge(), 
                                                                        getInvokingRuleName(), getInvokingBuiltInIndex(), getIsInConsequent(),
                                                                        arguments.subList(1, arguments.size()));
    if (!invocationPatterns.contains(invocationPattern)) {
      invocationPatterns.add(invocationPattern);
      list.add(value); if (!lists.containsKey(collectionID)) lists.put(collectionID, list);
    } // if
    
    if (SWRLBuiltInUtil.isUnboundArgument(0, arguments)) arguments.set(0, argumentFactory.createDatatypeValueArgument(collectionID));

    return true;
  } // makeList

  public boolean makeSortedSet(List<BuiltInArgument> arguments) throws BuiltInException
  {
    String collectionID = getCollectionIDInMake(arguments); // Does argument checking
    BuiltInArgument value = arguments.get(1);
    SortedSet<BuiltInArgument> sortedSet = sortedSets.containsKey(collectionID) ? sortedSets.get(collectionID) : new TreeSet<BuiltInArgument>();
    String invocationPattern  = SWRLBuiltInUtil.createInvocationPattern(getInvokingBridge(), 
                                                                        getInvokingRuleName(), getInvokingBuiltInIndex(), getIsInConsequent(),
                                                                        arguments.subList(1, arguments.size()));
    if (!invocationPatterns.contains(invocationPattern)) {
      invocationPatterns.add(invocationPattern);
      sortedSet.add(value); if (!sortedSets.containsKey(collectionID)) sortedSets.put(collectionID, sortedSet);
    } // if
 
    if (SWRLBuiltInUtil.isUnboundArgument(0, arguments)) arguments.set(0, argumentFactory.createDatatypeValueArgument(collectionID));

    return true;
  } // makeSortedSet

  public boolean isEmpty(List<BuiltInArgument> arguments) throws BuiltInException
  {
    String collectionID = getCollectionIDInOperation(arguments, 0, 1); // Does argument checking
    boolean result = false;
    
    if (sets.containsKey(collectionID)) result = sets.get(collectionID).size() != 0;
    else if (lists.containsKey(collectionID)) result = lists.get(collectionID).size() != 0;
    else if (sortedSets.containsKey(collectionID)) result = sortedSets.get(collectionID).size() != 0;
    else throw new BuiltInException("internal error: no collection found for ID '" + collectionID + "'");

    return result;
  } // isEmpty

  public boolean size(List<BuiltInArgument> arguments) throws BuiltInException
  {
    String collectionID = getCollectionIDInOperation(arguments, 1, 2); // Does argument checking
    long size = -1;
    boolean result;
    
    if (isSet(collectionID)) size = sets.get(collectionID).size();
    else if (isSortedSet(collectionID)) size = sortedSets.get(collectionID).size();
    else if (isList(collectionID)) size = lists.get(collectionID).size();
    else throw new BuiltInException("internal error: no collection found for ID '" + collectionID + "'");

    if (SWRLBuiltInUtil.isUnboundArgument(0, arguments)) {
      arguments.set(0, argumentFactory.createDatatypeValueArgument(size)); // Bind the result to the first parameter
      result = true;
    } else {
      long argument1 = SWRLBuiltInUtil.getArgumentAsALong(0, arguments);
      result = (argument1 == size);
    } //if

    return result;
  } // size

  public boolean intersect(List<BuiltInArgument> arguments) throws BuiltInException 
  { 
    String collectionID1 = getCollectionIDInOperation(arguments, 1, 3); // Does argument checking
    String collectionID2 = getCollectionIDInOperation(arguments, 2, 3); // Does argument checking
    // Set<BuiltInArgument> elements1 = getElements(collectionID1);
    // Set<BuiltInArgument> elements2 = getElements(collectionID2);

    throw new BuiltInException("not implemented");
  } // intersect

  public boolean union(List<BuiltInArgument> arguments) throws BuiltInException 
  { 
    throw new BuiltInException("not implemented");
  } // union

  public boolean contains(List<BuiltInArgument> arguments) throws BuiltInException 
  { 
    throw new BuiltInException("not implemented");
  } // contains

  public boolean except(List<BuiltInArgument> arguments) throws BuiltInException 
  { 
    throw new BuiltInException("not implemented");
  } // except

  public boolean nth(List<BuiltInArgument> arguments) throws BuiltInException 
  { 
    throw new BuiltInException("not implemented");
  } // nth

  public boolean first(List<BuiltInArgument> arguments) throws BuiltInException 
  { 
    throw new BuiltInException("not implemented");
  } // first

  public boolean last(List<BuiltInArgument> arguments) throws BuiltInException 
  { 
    throw new BuiltInException("not implemented");
  } // last

  // Internal methods

  private boolean isSet(String collectionID) { return sets.containsKey(collectionID); }
  private boolean isList(String collectionID) { return lists.containsKey(collectionID); }
  private boolean isSortedSet(String collectionID) { return sortedSets.containsKey(collectionID); }

  private String getCollectionIDInMake(List<BuiltInArgument> arguments) throws BuiltInException
  {
    return getCollectionID(arguments, 0, 2);
  } // getCollectionIDInMake

  private String getCollectionIDInOperation(List<BuiltInArgument> arguments, int collectionArgumentNumber, int coreArgumentNumber) 
    throws BuiltInException
  {
    return getCollectionID(arguments, collectionArgumentNumber, coreArgumentNumber);
  } // getCollectionIDInOperation

  private String getCollectionID(List<BuiltInArgument> arguments, int collectionArgumentNumber, int coreArgumentNumber) throws BuiltInException
  {
    String ruleName = getInvokingRuleName();
    String collectionName = SWRLBuiltInUtil.getVariableName(collectionArgumentNumber, arguments);
    boolean hasInvocationPattern  = (arguments.size() > coreArgumentNumber);
    String invocationPattern = !hasInvocationPattern ? "" : SWRLBuiltInUtil.createInvocationPattern(getInvokingBridge(), ruleName, 0, false,
                                                                                                    arguments.subList(coreArgumentNumber, arguments.size()));
    return ruleName + ":" + collectionName + ":" + invocationPattern;
  } // getCollectionIDInOperation

  private ResultImpl getResult(String queryName) throws BuiltInException
  {
    ResultImpl result = null;

    try {
      if (getInvokingBridge().getRule(queryName) != null) result = getInvokingBridge().getRule(queryName).getSQWRLResult();
    } catch (InvalidRuleNameException e) {} // Result will be null if cannot find query

    return result;
  } // getResult
 
  private void throwInternalSQWRLException(String message) throws BuiltInException
  {
    throw new BuiltInException("internal SQWRL engine exception: " + message);
  } // throwInternalSQWRLException


} // SWRLBuiltInLibraryImpl
