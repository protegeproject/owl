
// TODO: a lot of cut-and-paste repetition here needs to be fixed.
// TODO: replace set operators min, max with least, greatest?
// TODO: need to optimize sorted sets so that they are not resorted unnecessarily

package edu.stanford.smi.protegex.owl.swrl.bridge.builtins.sqwrl;

import edu.stanford.smi.protegex.owl.swrl.exceptions.*;

import edu.stanford.smi.protegex.owl.swrl.sqwrl.*;
import edu.stanford.smi.protegex.owl.swrl.sqwrl.impl.*;
import edu.stanford.smi.protegex.owl.swrl.sqwrl.exceptions.*;

import edu.stanford.smi.protegex.owl.swrl.bridge.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.builtins.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;

import java.math.BigDecimal;
import java.util.*;

/**
 ** Implementation library for SQWRL query built-ins. See <a href="http://protege.cim3.net/cgi-bin/wiki.pl?SQWRL">here</a> for documentation
 ** on this built-in library.
 */
public class SWRLBuiltInLibraryImpl extends AbstractSWRLBuiltInLibrary
{
  private Map<String, Set<BuiltInArgument>> sets;

  private Map<String, Integer> collectionGroupElementNumbersMap; // Collection name to number of elements in group (which may be 0)

  private Set<String> invocationPatterns;

  public SWRLBuiltInLibraryImpl() { super(SQWRLNames.SQWRLBuiltInLibraryName); }

  private ArgumentFactory argumentFactory;
  
  public void reset()
  {
    sets = new HashMap<String, Set<BuiltInArgument>>();

    invocationPatterns = new HashSet<String>();

    collectionGroupElementNumbersMap = new HashMap<String, Integer>(); 

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

  // The use of columnNames, orderBy, orderByDescending is handled at initial processing in the SWRLRule object.  
  public boolean columnNames(List<BuiltInArgument> arguments) throws BuiltInException { return false; } 
  public boolean orderBy(List<BuiltInArgument> arguments) throws BuiltInException { return false; } 
  public boolean orderByDescending(List<BuiltInArgument> arguments) throws BuiltInException { return false; }

  public boolean makeSet(List<BuiltInArgument> arguments) throws BuiltInException
  {
    String collectionID = getCollectionIDInMake(arguments); // Get unique ID for collection; does argument checking
    String invocationPattern  = SWRLBuiltInUtil.createInvocationPattern(getInvokingBridge(), getInvokingRuleName(), 
                                                                        getInvokingBuiltInIndex(), getIsInConsequent(),
                                                                        arguments.subList(1, arguments.size()));

    //System.err.println("sqwrl.makeSet: arguments: " + arguments);
    //System.err.println("sqwrl.makeSet: invocationPattern: " + invocationPattern);
    //System.err.println("sqwrl.makeSet: collectionID: " + collectionID);

    if (!invocationPatterns.contains(invocationPattern)) {
      BuiltInArgument value = arguments.get(arguments.size() - 1); // The last argument is always the value
      Set<BuiltInArgument> set; 

      if (sets.containsKey(collectionID)) set = sets.get(collectionID);
      else { 
        String collectionName = getCollectionName(arguments, 0);
        int numberOfGroupElements = arguments.size() == 2 ? 0 : arguments.size() - 2;
        collectionGroupElementNumbersMap.put(collectionName, numberOfGroupElements);
        set = new HashSet<BuiltInArgument>(); sets.put(collectionID, set); 
        
        //System.err.println("making new set with ID '" + collectionID + "', number of group elements " + numberOfGroupElements);
      } // if

      set.add(value);

      //System.err.println("adding value '" + value + "' to set '" + collectionID + "'");

      invocationPatterns.add(invocationPattern);
    } // if
    
    if (SWRLBuiltInUtil.isUnboundArgument(0, arguments)) arguments.set(0, argumentFactory.createDatatypeValueArgument(collectionID));

    return true;
  } // makeSet

  public boolean isEmpty(List<BuiltInArgument> arguments) throws BuiltInException
  {
    String collectionID = getCollectionIDInOperation(arguments, 0, 1); // Does argument checking
    boolean result = false;
    
    // System.err.println("isEmpty: arguments: " + arguments);
    // System.err.println("isEmpty: collectionID: " + collectionID);

    if (sets.containsKey(collectionID)) result = sets.get(collectionID).size() == 0;
    else throw new BuiltInException("internal error: no collection found for ID '" + collectionID + "'");

    return result;
  } // isEmpty

  public boolean notEmpty(List<BuiltInArgument> arguments) throws BuiltInException
  {
    return !isEmpty(arguments);
  } // notEmpty

  public boolean size(List<BuiltInArgument> arguments) throws BuiltInException
  {
    String collectionID = getCollectionIDInOperation(arguments, 1, 2); // Does argument checking
    long size = getSet(collectionID).size(); // Checks set ID validity

    // System.err.println("size: arguments: " + arguments);
    // System.err.println("size: collectionID: " + collectionID);

    return SWRLBuiltInUtil.processResultArgument(arguments, 0, argumentFactory, size);
  } // size

  public boolean min(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (getIsInConsequent()) {
      SWRLBuiltInUtil.checkForUnboundArguments(arguments);
      SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(1, arguments.size());
      
      ResultImpl resultImpl = getResult(getInvokingRuleName());
      BuiltInArgument argument = arguments.get(0);
      
      if (!resultImpl.isRowOpen()) resultImpl.openRow();
      
      if (argument instanceof OWLDatatypeValue && ((OWLDatatypeValue)argument).isNumeric()) resultImpl.addRowData((DatatypeValue)argument);
      else throw new InvalidBuiltInArgumentException(0, "expecting numeric literal, got '" + argument + "'");
      
      result = true;
    } else { // Set operator
      String collectionID = getCollectionIDInOperation(arguments, 1, 2); // Does argument checking
      Set<BuiltInArgument> set;
      OWLDatatypeValue minValue = null, value;
      
      set = getSet(collectionID); // Check collectionID validity
      
      if (set.isEmpty()) result = false;
      else {
        for (BuiltInArgument element : set) {
          checkThatElementIsNumeric(element);
          value = (OWLDatatypeValue)element;
          
          if (minValue == null) minValue = value;
          else if (value.compareTo(minValue) < 0) minValue = value;
        } // for
        
        // System.err.println("setMin: set: " + set);
        // System.err.println("setMin: min: " + minValue);

        result = SWRLBuiltInUtil.processResultArgument(arguments, 0, argumentFactory, minValue);
      } // if
    } // if
    return result;
  } // min

  public boolean max(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (getIsInConsequent()) {
      SWRLBuiltInUtil.checkForUnboundArguments(arguments);
      SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(1, arguments.size());
      
      ResultImpl resultImpl = getResult(getInvokingRuleName());
      BuiltInArgument argument = arguments.get(0);
      
      if (!resultImpl.isRowOpen()) resultImpl.openRow();
      
      if (argument instanceof OWLDatatypeValue && ((OWLDatatypeValue)argument).isNumeric()) resultImpl.addRowData((DatatypeValue)argument);
      else throw new InvalidBuiltInArgumentException(0, "expecting numeric literal, got '" + argument + "'");

      result = true;
    } else { // set operator
      String collectionID = getCollectionIDInOperation(arguments, 1, 2); // Does argument checking
      Set<BuiltInArgument> set = getSet(collectionID); // Checks collectionID validity
      
      if (set.isEmpty()) result = false;
      else {
        OWLDatatypeValue maxValue = null, value;
        for (BuiltInArgument element : set) {
          checkThatElementIsNumeric(element);
          value = (OWLDatatypeValue)element;
          
          if (maxValue == null) maxValue = value;
          else if (value.compareTo(maxValue) > 0) maxValue = value;
        } // for
        
        result = SWRLBuiltInUtil.processResultArgument(arguments, 0, argumentFactory, maxValue);
      } // if
    } // if
    return result;
  } // max

  public boolean sum(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (getIsInConsequent()) {
      SWRLBuiltInUtil.checkForUnboundArguments(arguments);
      SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(1, arguments.size());
      
      ResultImpl resultImpl = getResult(getInvokingRuleName());
      BuiltInArgument argument = arguments.get(0);
      
      if (!resultImpl.isRowOpen()) resultImpl.openRow();
      
      if (argument instanceof OWLDatatypeValue && ((OWLDatatypeValue)argument).isNumeric()) resultImpl.addRowData((DatatypeValue)argument);
      else throw new InvalidBuiltInArgumentException(0, "expecting numeric literal, got '" + argument + "'");
      
      result = true;
    } else { // set operator
      String collectionID = getCollectionIDInOperation(arguments, 1, 2); // Does argument checking
      Set<BuiltInArgument> set = getSet(collectionID); // Checks collectionID validity
      
      if (set.isEmpty()) result = false;
      else {
        float sumValue = 0, value;
        for (BuiltInArgument element : set) {
          checkThatElementIsNumeric(element);
          value = SWRLBuiltInUtil.getArgumentAsAFloat(element);
          sumValue += value;
        } // for
        
        result = SWRLBuiltInUtil.processResultArgument(arguments, 0, argumentFactory, sumValue);
      } // if
    } // if

    return result;
  } // sum

  public boolean avg(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (getIsInConsequent()) {
      SWRLBuiltInUtil.checkForUnboundArguments(arguments);
      SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(1, arguments.size());
      
      ResultImpl resultImpl = getResult(getInvokingRuleName());
      Argument argument = arguments.get(0);
      
      if (!resultImpl.isRowOpen()) resultImpl.openRow();
      
      if (argument instanceof OWLDatatypeValue && ((OWLDatatypeValue)argument).isNumeric()) resultImpl.addRowData((DatatypeValue)argument);
      else throw new InvalidBuiltInArgumentException(0, "expecting numeric literal, got '" + argument + "'");
    } else { // set operator
      String collectionID = getCollectionIDInOperation(arguments, 1, 2); // Does argument checking
      Set<BuiltInArgument> set = getSet(collectionID); // Checks collectionID validity
      
      if (set.isEmpty()) result = false;
      else {
        float avgValue, sumValue = 0, value;
        for (BuiltInArgument element : set) {
          checkThatElementIsNumeric(element);
          value = SWRLBuiltInUtil.getArgumentAsAFloat(element);
          sumValue += value;
        } // for
        avgValue = sumValue / set.size();
        
        result = SWRLBuiltInUtil.processResultArgument(arguments, 0, argumentFactory, avgValue);
      } // if
    } // if

    return result;
  } // avg

  public boolean median(List<BuiltInArgument> arguments) throws BuiltInException 
  { 
    boolean result = false;;

    if (getIsInConsequent()) {
      throw new BuiltInException("not implemented");
    } else { // set operator
      String collectionID = getCollectionIDInOperation(arguments, 1, 2); // Does argument checking
      Set<BuiltInArgument> set = getSet(collectionID); // Checks collectionID validity
      
      if (set.isEmpty()) result = false;
      else {
        float[] valueArray = new float[set.size()];
        int count = 0, middle = set.size() / 2;
        float medianValue, value;

        for (BuiltInArgument element : set) {
          checkThatElementIsNumeric(element);
          value = SWRLBuiltInUtil.getArgumentAsAFloat(element);
          valueArray[count++] = value;
        } // for
        
        Arrays.sort(valueArray);

        if (set.size() % 2 == 1) medianValue = valueArray[middle];
        else medianValue = (valueArray[middle - 1] + valueArray[middle]) / 2;
        
        result = SWRLBuiltInUtil.processResultArgument(arguments, 0, argumentFactory, medianValue);
      } // if
    } // if

    return result;
  } // median

  public boolean intersection(List<BuiltInArgument> arguments) throws BuiltInException 
  { 
    // System.err.println("intersection.arguments: " + arguments);

    String collectionIDResult = getCollectionIDInOperation(arguments, 0, 3); // Does argument checking
    String collectionName1 = getCollectionName(arguments, 1); 
    String collectionName2 = getCollectionName(arguments, 2);
    int collection1NumberOfGroupElements = getCollectionNumberOfGroupElements(collectionName1);
    int collection2NumberOfGroupElements = getCollectionNumberOfGroupElements(collectionName2);
    String collectionID1 = getCollectionIDInOperation(arguments, 1, 3, 0, collection1NumberOfGroupElements); // Does argument checking
    String collectionID2 = getCollectionIDInOperation(arguments, 2, 3, 0 + collection1NumberOfGroupElements, collection2NumberOfGroupElements); // Does argument checking

    // System.err.println("intersection.collectionName1: " + collectionName1);
    // System.err.println("intersection.collectionName2: " + collectionName2);
    // 
    // System.err.println("intersection.collection1NumberOfGroupElements: " + collection1NumberOfGroupElements);
    // System.err.println("intersection.collection2NumberOfGroupElements: " + collection2NumberOfGroupElements);
    // 
    // System.err.println("intersection.collectionID1: " + collectionID1);
    // System.err.println("intersection.collectionID2: " + collectionID2);

    Set<BuiltInArgument> set1 = sets.get(collectionID1);
    // System.err.println("intersection.set1: " + set1);
    Set<BuiltInArgument> set2 = sets.get(collectionID2);
    // System.err.println("intersection.set2: " + set2);
    Set<BuiltInArgument> intersection = new HashSet<BuiltInArgument>(set1);

    intersection.retainAll(set2);

    if (!sets.containsKey(collectionIDResult)) sets.put(collectionIDResult, intersection);

    if (SWRLBuiltInUtil.isUnboundArgument(0, arguments)) arguments.set(0, argumentFactory.createDatatypeValueArgument(collectionIDResult));

    return true;
  } // intersection

  public boolean union(List<BuiltInArgument> arguments) throws BuiltInException 
  { 
    // System.err.println("union.arguments: " + arguments);

    String collectionIDResult = getCollectionIDInOperation(arguments, 0, 3); // Does argument checking
    String collectionName1 = getCollectionName(arguments, 1); 
    String collectionName2 = getCollectionName(arguments, 2);
    int collection1NumberOfGroupElements = getCollectionNumberOfGroupElements(collectionName1);
    int collection2NumberOfGroupElements = getCollectionNumberOfGroupElements(collectionName2);
    String collectionID1 = getCollectionIDInOperation(arguments, 1, 3, 0, collection1NumberOfGroupElements); // Does argument checking
    String collectionID2 = getCollectionIDInOperation(arguments, 2, 3, 0 + collection1NumberOfGroupElements, collection2NumberOfGroupElements); // Does argument checking

    // System.err.println("union.collectionName1: " + collectionName1);
    // System.err.println("union.collectionName2: " + collectionName2);

    // System.err.println("union.collection1NumberOfGroupElements: " + collection1NumberOfGroupElements);
    // System.err.println("union.collection2NumberOfGroupElements: " + collection2NumberOfGroupElements);
    // 
    // System.err.println("union.collectionID1: " + collectionID1);
    // System.err.println("union.collectionID2: " + collectionID2);

    Set<BuiltInArgument> set1 = sets.get(collectionID1);
    // System.err.println("union.set1: " + set1);
    Set<BuiltInArgument> set2 = sets.get(collectionID2);
    // System.err.println("union.set2: " + set2);
    Set<BuiltInArgument> union = new HashSet<BuiltInArgument>(set1);

    union.addAll(set2);

    if (!sets.containsKey(collectionIDResult)) sets.put(collectionIDResult, union);

    if (SWRLBuiltInUtil.isUnboundArgument(0, arguments)) arguments.set(0, argumentFactory.createDatatypeValueArgument(collectionIDResult));

    return true;
  } // union

  public boolean difference(List<BuiltInArgument> arguments) throws BuiltInException 
  { 
    // System.err.println("difference.arguments: " + arguments);

    String collectionIDResult = getCollectionIDInOperation(arguments, 0, 3); // Does argument checking
    String collectionName1 = getCollectionName(arguments, 1); 
    String collectionName2 = getCollectionName(arguments, 2);
    int collection1NumberOfGroupElements = getCollectionNumberOfGroupElements(collectionName1);
    int collection2NumberOfGroupElements = getCollectionNumberOfGroupElements(collectionName2);
    String collectionID1 = getCollectionIDInOperation(arguments, 1, 3, 0, collection1NumberOfGroupElements); // Does argument checking
    String collectionID2 = getCollectionIDInOperation(arguments, 2, 3, 0 + collection1NumberOfGroupElements, collection2NumberOfGroupElements); // Does argument checking

    // System.err.println("difference.collectionName1: " + collectionName1);
    // System.err.println("difference.collectionName2: " + collectionName2);
    // 
    // System.err.println("difference.collection1NumberOfGroupElements: " + collection1NumberOfGroupElements);
    // System.err.println("difference.collection2NumberOfGroupElements: " + collection2NumberOfGroupElements);
    // 
    // System.err.println("difference.collectionID1: " + collectionID1);
    // System.err.println("difference.collectionID2: " + collectionID2);

    Set<BuiltInArgument> set1 = sets.get(collectionID1);
    // System.err.println("difference.set1: " + set1);
    Set<BuiltInArgument> set2 = sets.get(collectionID2);
    // System.err.println("difference.set2: " + set2);
    Set<BuiltInArgument> difference = new HashSet<BuiltInArgument>(set1);

    difference.removeAll(set2);

    if (!sets.containsKey(collectionIDResult)) sets.put(collectionIDResult, difference);

    if (SWRLBuiltInUtil.isUnboundArgument(0, arguments)) arguments.set(0, argumentFactory.createDatatypeValueArgument(collectionIDResult));

    return true;
  } // difference

  public boolean contains(List<BuiltInArgument> arguments) throws BuiltInException 
  { 
    String collectionID = getCollectionIDInOperation(arguments, 0, 2); // Does argument checking
    boolean result = false;

    // System.err.println("sqwrl.contains: arguments: " + arguments);
    
    if (isSet(collectionID)) {
      BuiltInArgument element = arguments.get(1);
      // System.err.println("sqwrl.contains: element: " + element);
      result = sets.get(collectionID).contains(element);
    } else throw new BuiltInException("internal error: no collection found for ID '" + collectionID + "'");
    
    return result;
  } // contains

  public boolean notContains(List<BuiltInArgument> arguments) throws BuiltInException 
  { 
    return !contains(arguments);
  } // notContains

  public boolean greatest(List<BuiltInArgument> arguments) throws BuiltInException 
  { 
    String collectionID = getCollectionIDInOperation(arguments, 1, 2); // Does argument checking
    Set<BuiltInArgument> set;
    boolean result = false;

    if (isSet(collectionID)) set = sets.get(collectionID);
      else throw new BuiltInException("internal error: no collection found for ID '" + collectionID + "'");
    
    if (!set.isEmpty()) {
      SortedSet<BuiltInArgument> sortedSet = new TreeSet<BuiltInArgument>(set);
      BuiltInArgument greatest = sortedSet.last();

      result = SWRLBuiltInUtil.processResultArgument(arguments, 0, argumentFactory, greatest);
    } // if

    return result;
  } // greatest

  public boolean greatestN(List<BuiltInArgument> arguments) throws BuiltInException 
  { 
    String collectionID = getCollectionIDInOperation(arguments, 1, 3); // Does argument checking
    int n = SWRLBuiltInUtil.getArgumentAsAPositiveInteger(2, arguments);
    Set<BuiltInArgument> set;
    boolean result = false;

    if (isSet(collectionID)) set = sets.get(collectionID);
      else throw new BuiltInException("internal error: no collection found for ID '" + collectionID + "'");
    
    if (!set.isEmpty()) {
      SortedSet<BuiltInArgument> sortedSet = new TreeSet<BuiltInArgument>(set);
      BuiltInArgument array[] = (BuiltInArgument[])sortedSet.toArray(new BuiltInArgument[sortedSet.size()]);
      Set<BuiltInArgument> greatestNSet = new HashSet<BuiltInArgument>();

      for (int i = set.size() - 1; i >= set.size() - n && i >= 0; i--) greatestNSet.add(array[i]);

      result = SWRLBuiltInUtil.processResultArgument(arguments, 0, argumentFactory, greatestNSet);
    } // if

    return result;
  } // greatestN
 
  public boolean notGreatestN(List<BuiltInArgument> arguments) throws BuiltInException 
  { 
    String collectionID = getCollectionIDInOperation(arguments, 1, 3); // Does argument checking
    int n = SWRLBuiltInUtil.getArgumentAsAPositiveInteger(2, arguments);
    Set<BuiltInArgument> set;
    boolean result = false;

    if (isSet(collectionID)) set = sets.get(collectionID);
      else throw new BuiltInException("internal error: no collection found for ID '" + collectionID + "'");
    
    if (!set.isEmpty()) {
      SortedSet<BuiltInArgument> sortedSet = new TreeSet<BuiltInArgument>(set);
      BuiltInArgument array[] = (BuiltInArgument[])sortedSet.toArray(new BuiltInArgument[sortedSet.size()]);
      Set<BuiltInArgument> notGreatestNSet = new HashSet<BuiltInArgument>();

      for (int i = 0; i < set.size() - n; i++) notGreatestNSet.add(array[i]);

      result = SWRLBuiltInUtil.processResultArgument(arguments, 0, argumentFactory, notGreatestNSet);
    } // if

    return result;
  } // notGreatestN

  public boolean leastN(List<BuiltInArgument> arguments) throws BuiltInException 
  { 
    String collectionID = getCollectionIDInOperation(arguments, 1, 3); // Does argument checking
    int n = SWRLBuiltInUtil.getArgumentAsAPositiveInteger(2, arguments);
    Set<BuiltInArgument> set;
    boolean result = false;

    if (isSet(collectionID)) set = sets.get(collectionID);
      else throw new BuiltInException("internal error: no collection found for ID '" + collectionID + "'");
    
    if (!set.isEmpty()) {
      SortedSet<BuiltInArgument> sortedSet = new TreeSet<BuiltInArgument>(set);
      BuiltInArgument array[] = (BuiltInArgument[])sortedSet.toArray(new BuiltInArgument[sortedSet.size()]);
      Set<BuiltInArgument> leastNSet = new HashSet<BuiltInArgument>();

      for (int i = 0; i < n && i < set.size(); i++) leastNSet.add(array[i]);
      
      result = SWRLBuiltInUtil.processResultArgument(arguments, 0, argumentFactory, leastNSet);
    } // if

    return result;
  } // leastN

  public boolean notLeastN(List<BuiltInArgument> arguments) throws BuiltInException 
  { 
    String collectionID = getCollectionIDInOperation(arguments, 1, 3); // Does argument checking
    int n = SWRLBuiltInUtil.getArgumentAsAPositiveInteger(2, arguments);
    Set<BuiltInArgument> set;
    boolean result = false;

    if (isSet(collectionID)) set = sets.get(collectionID);
      else throw new BuiltInException("internal error: no collection found for ID '" + collectionID + "'");
    
    if (!set.isEmpty()) {
      SortedSet<BuiltInArgument> sortedSet = new TreeSet<BuiltInArgument>(set);
      BuiltInArgument array[] = (BuiltInArgument[])sortedSet.toArray(new BuiltInArgument[sortedSet.size()]);
      Set<BuiltInArgument> notLeastNSet = new HashSet<BuiltInArgument>();

      for (int i = n - 1; i < set.size(); i++) notLeastNSet.add(array[i]);
      
      result = SWRLBuiltInUtil.processResultArgument(arguments, 0, argumentFactory, notLeastNSet);
    } // if

    return result;
  } // leastN

  public boolean least(List<BuiltInArgument> arguments) throws BuiltInException 
  { 
    String collectionID = getCollectionIDInOperation(arguments, 1, 2); // Does argument checking
    Set<BuiltInArgument> set;
    boolean result = false;

    if (isSet(collectionID)) set = sets.get(collectionID);
      else throw new BuiltInException("internal error: no collection found for ID '" + collectionID + "'");

    if (!set.isEmpty()) {
      SortedSet<BuiltInArgument> sortedSet = new TreeSet<BuiltInArgument>(set);
      BuiltInArgument least = sortedSet.first();

      result = SWRLBuiltInUtil.processResultArgument(arguments, 0, argumentFactory, least);
    } // if

    return result;
  } // least

  // Internal methods

  private boolean isSet(String collectionID) { return sets.containsKey(collectionID); }

  private String getCollectionName(List<BuiltInArgument> arguments, int collectionArgumentNumber) throws BuiltInException
  {
    String ruleName = getInvokingRuleName();
    String collectionName = SWRLBuiltInUtil.getVariableName(collectionArgumentNumber, arguments); 
    return ruleName + ":" + collectionName;
  } // getCollectionName

  private int getCollectionNumberOfGroupElements(String collectionName) throws BuiltInException
  {
    if (!collectionGroupElementNumbersMap.containsKey(collectionName)) 
      throw new BuiltInException("internal error: invalid collection name '" + collectionName + "'; no group element number found");

    return collectionGroupElementNumbersMap.get(collectionName);
  } // getCollectionNumberOfGroupElements

  private String getCollectionIDInMake(List<BuiltInArgument> arguments) throws BuiltInException
  {
    String ruleName = getInvokingRuleName();
    String collectionName = SWRLBuiltInUtil.getVariableName(0, arguments); // Always the first argument for a collection
    boolean hasInvocationPattern  = (arguments.size() > 2);
    String invocationPattern = !hasInvocationPattern ? "" : SWRLBuiltInUtil.createInvocationPattern(getInvokingBridge(), ruleName, 0, false,
                                                                                                    arguments.subList(1, arguments.size() - 1));
    return ruleName + ":" + collectionName + ":" + invocationPattern;
  } // getCollectionIDInMake

  private String getCollectionIDInOperation(List<BuiltInArgument> arguments, int collectionArgumentNumber, int coreArgumentNumber) 
    throws BuiltInException
  {
    return getCollectionIDInOperation(arguments, collectionArgumentNumber, coreArgumentNumber, -1, 0);
  } // getCollectionIDInOperation

  // A groupArgumentStart of -1 indicates that all group arguments are relevant
  private String getCollectionIDInOperation(List<BuiltInArgument> arguments, int collectionArgumentNumber, 
                                            int coreArgumentNumber, int groupArgumentOffset, int numberOfRelevantGroupArguments) 
   throws BuiltInException
  {
    String ruleName = getInvokingRuleName();
    String collectionName = SWRLBuiltInUtil.getVariableName(collectionArgumentNumber, arguments);
    boolean hasInvocationPattern  = (arguments.size() > coreArgumentNumber);
    String invocationPattern = "";

    if (hasInvocationPattern) {
      if (groupArgumentOffset > -1)
        invocationPattern = SWRLBuiltInUtil.createInvocationPattern(getInvokingBridge(), ruleName, 0, false, 
                                                                    arguments.subList(coreArgumentNumber + groupArgumentOffset, 
                                                                                      coreArgumentNumber + groupArgumentOffset + numberOfRelevantGroupArguments));
      else
        invocationPattern = SWRLBuiltInUtil.createInvocationPattern(getInvokingBridge(), ruleName, 0, false, 
                                                                    arguments.subList(coreArgumentNumber, arguments.size()));
    } // if

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

  private void checkThatElementIsNumeric(BuiltInArgument element) throws BuiltInException
  {
    if (!(element instanceof DatatypeValue) || !((DatatypeValue)element).isNumeric()) 
      throw new BuiltInException("may only be applied to sets with numeric data values");
  } // checkThatElementIsNumeric

  private Set getSet(String collectionID) throws BuiltInException
  {
    if (!isSet(collectionID)) throw new BuiltInException("internal error: no collection found for ID '" + collectionID + "'");
    return sets.get(collectionID);
  } // checkThatCollectionIsSet

} // SWRLBuiltInLibraryImpl
