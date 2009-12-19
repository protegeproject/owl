
// TODO: a lot of cut-and-paste repetition here needs to be fixed.
// TODO: replace set operators min, max with least, greatest?
// TODO: need to optimize sorted sets so that they are not resorted unnecessarily

package edu.stanford.smi.protegex.owl.swrl.bridge.builtins.sqwrl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import edu.stanford.smi.protegex.owl.swrl.bridge.Argument;
import edu.stanford.smi.protegex.owl.swrl.bridge.ArgumentFactory;
import edu.stanford.smi.protegex.owl.swrl.bridge.BuiltInArgument;
import edu.stanford.smi.protegex.owl.swrl.bridge.ClassArgument;
import edu.stanford.smi.protegex.owl.swrl.bridge.DatatypeValueArgument;
import edu.stanford.smi.protegex.owl.swrl.bridge.IndividualArgument;
import edu.stanford.smi.protegex.owl.swrl.bridge.OWLClass;
import edu.stanford.smi.protegex.owl.swrl.bridge.OWLDatatypeValue;
import edu.stanford.smi.protegex.owl.swrl.bridge.OWLIndividual;
import edu.stanford.smi.protegex.owl.swrl.bridge.OWLProperty;
import edu.stanford.smi.protegex.owl.swrl.bridge.PropertyArgument;
import edu.stanford.smi.protegex.owl.swrl.bridge.builtins.AbstractSWRLBuiltInLibrary;
import edu.stanford.smi.protegex.owl.swrl.bridge.builtins.SWRLBuiltInUtil;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.BuiltInException;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.InvalidBuiltInArgumentException;
import edu.stanford.smi.protegex.owl.swrl.sqwrl.ClassValue;
import edu.stanford.smi.protegex.owl.swrl.sqwrl.DatatypeValue;
import edu.stanford.smi.protegex.owl.swrl.sqwrl.ObjectValue;
import edu.stanford.smi.protegex.owl.swrl.sqwrl.PropertyValue;
import edu.stanford.smi.protegex.owl.swrl.sqwrl.SQWRLNames;
import edu.stanford.smi.protegex.owl.swrl.sqwrl.impl.ResultImpl;

/*
 * Implementation library for SQWRL built-ins. See <a href="http://protege.cim3.net/cgi-bin/wiki.pl?SQWRL">here</a> for documentation
 * on this built-in library.
 *
 *  
 */
public class SWRLBuiltInLibraryImpl extends AbstractSWRLBuiltInLibrary
{
  private Map<String, Set<BuiltInArgument>> sets;
  private Map<String, Integer> setGroupElementNumbersMap; // Set name to number of elements in group (which may be 0)
  private ArgumentFactory argumentFactory;
  
  public SWRLBuiltInLibraryImpl() { super(SQWRLNames.SQWRLBuiltInLibraryName); }
  
  public void reset()
  {
    sets = new HashMap<String, Set<BuiltInArgument>>();
    setGroupElementNumbersMap = new HashMap<String, Integer>(); 
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
	String setID = getSetIDInMake(arguments); // Get unique ID for set; does argument checking
	String setName = getSetName(arguments, 0);
	BuiltInArgument value = arguments.get(1); // The second argument is always the value
	Set<BuiltInArgument> set;
	    
    System.err.println("sqwrl.makeSet: setID: " + setID);
    System.err.println("sqwrl.makeSet: setName: " + setName);
    System.err.println("sqwrl.makeSet: arguments: " + arguments);

    if (sets.containsKey(setID)) set = sets.get(setID);
    else {  
        set = new HashSet<BuiltInArgument>(); sets.put(setID, set); 
        System.err.println("making new set with name " + setName + ", ID " + setID);
    } // if

    set.add(value);
    System.err.println("adding value " + value + " to set " + setID);

    if (SWRLBuiltInUtil.isUnboundArgument(0, arguments)) arguments.set(0, argumentFactory.createDatatypeValueArgument(setID));

    return true;
  } // makeSet
  
  public boolean groupBy(List<BuiltInArgument> arguments) throws BuiltInException
  {
	return true; // Should never be invoked - is a directive only and is processed in SWRLRule
  } // groupBy

  public boolean isEmpty(List<BuiltInArgument> arguments) throws BuiltInException
  {
    String setID = getSetIDInOperation(arguments, 0, 1); // Does argument checking
    boolean result = false;
    
    // System.err.println("isEmpty: arguments: " + arguments);
    // System.err.println("isEmpty: setID: " + setID);

    if (sets.containsKey(setID)) result = sets.get(setID).size() == 0;
    else throw new BuiltInException("internal error: no set found for ID " + setID);

    return result;
  } // isEmpty

  public boolean notEmpty(List<BuiltInArgument> arguments) throws BuiltInException
  {
    return !isEmpty(arguments);
  } // notEmpty

  public boolean size(List<BuiltInArgument> arguments) throws BuiltInException
  {
    String setID = getSetIDInOperation(arguments, 1, 2); // Does argument checking
    long size = getSet(setID).size(); // Checks set ID validity

    System.err.println("size: arguments: " + arguments);
    System.err.println("size: setID: " + setID);

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
      String setID = getSetIDInOperation(arguments, 1, 2); // Does argument checking
      Set<BuiltInArgument> set;
      OWLDatatypeValue minValue = null, value;
      
      set = getSet(setID); // Check setID validity
      
      if (set.isEmpty()) result = false;
      else {
        for (BuiltInArgument element : set) {
          hatElementIsNumeric(element);
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
      String setID = getSetIDInOperation(arguments, 1, 2); // Does argument checking
      Set<BuiltInArgument> set = getSet(setID); // Checks setID validity
      
      if (set.isEmpty()) result = false;
      else {
        OWLDatatypeValue maxValue = null, value;
        for (BuiltInArgument element : set) {
          hatElementIsNumeric(element);
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
      String setID = getSetIDInOperation(arguments, 1, 2); // Does argument checking
      Set<BuiltInArgument> set = getSet(setID); // Checks setID validity
      
      if (set.isEmpty()) result = false;
      else {
        float sumValue = 0, value;
        for (BuiltInArgument element : set) {
          hatElementIsNumeric(element);
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
      String setID = getSetIDInOperation(arguments, 1, 2); // Does argument checking
      Set<BuiltInArgument> set = getSet(setID); // Checks setID validity
      
      if (set.isEmpty()) result = false;
      else {
        float avgValue, sumValue = 0, value;
        for (BuiltInArgument element : set) {
          hatElementIsNumeric(element);
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
      String setID = getSetIDInOperation(arguments, 1, 2); // Does argument checking
      Set<BuiltInArgument> set = getSet(setID); // Checks setID validity
      
      if (set.isEmpty()) result = false;
      else {
        float[] valueArray = new float[set.size()];
        int count = 0, middle = set.size() / 2;
        float medianValue, value;

        for (BuiltInArgument element : set) {
          hatElementIsNumeric(element);
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

    String setIDResult = getSetIDInOperation(arguments, 0, 3); // Does argument checking
    String setName1 = getSetName(arguments, 1); 
    String setName2 = getSetName(arguments, 2);
    int set1NumberOfGroupElements = getSetNumberOfGroupElements(setName1);
    int set2NumberOfGroupElements = getSetNumberOfGroupElements(setName2);
    String setID1 = getSetIDInOperation(arguments, 1, 3, 0, set1NumberOfGroupElements); // Does argument checking
    String setID2 = getSetIDInOperation(arguments, 2, 3, set1NumberOfGroupElements, set2NumberOfGroupElements); // Does argument checking

    // System.err.println("intersection.setName1: " + setName1);
    // System.err.println("intersection.setName2: " + setName2);
    // 
    // System.err.println("intersection.set1NumberOfGroupElements: " + set1NumberOfGroupElements);
    // System.err.println("intersection.set2NumberOfGroupElements: " + set2NumberOfGroupElements);
    // 
    // System.err.println("intersection.setID1: " + setID1);
    // System.err.println("intersection.setID2: " + setID2);

    Set<BuiltInArgument> set1 = sets.get(setID1);
    // System.err.println("intersection.set1: " + set1);
    Set<BuiltInArgument> set2 = sets.get(setID2);
    // System.err.println("intersection.set2: " + set2);
    Set<BuiltInArgument> intersection = new HashSet<BuiltInArgument>(set1);

    intersection.retainAll(set2);

    if (!sets.containsKey(setIDResult)) sets.put(setIDResult, intersection);

    if (SWRLBuiltInUtil.isUnboundArgument(0, arguments)) arguments.set(0, argumentFactory.createDatatypeValueArgument(setIDResult));

    return true;
  } // intersection

  public boolean union(List<BuiltInArgument> arguments) throws BuiltInException 
  { 
    // System.err.println("union.arguments: " + arguments);

    String setIDResult = getSetIDInOperation(arguments, 0, 3); // Does argument checking
    String setName1 = getSetName(arguments, 1); 
    String setName2 = getSetName(arguments, 2);
    int set1NumberOfGroupElements = getSetNumberOfGroupElements(setName1);
    int set2NumberOfGroupElements = getSetNumberOfGroupElements(setName2);
    String setID1 = getSetIDInOperation(arguments, 1, 3, 0, set1NumberOfGroupElements); // Does argument checking
    String setID2 = getSetIDInOperation(arguments, 2, 3, 0 + set1NumberOfGroupElements, set2NumberOfGroupElements); // Does argument checking

    // System.err.println("union.setName1: " + setName1);
    // System.err.println("union.setName2: " + setName2);

    // System.err.println("union.set1NumberOfGroupElements: " + set1NumberOfGroupElements);
    // System.err.println("union.set2NumberOfGroupElements: " + set2NumberOfGroupElements);
    // 
    // System.err.println("union.setID1: " + setID1);
    // System.err.println("union.setID2: " + setID2);

    Set<BuiltInArgument> set1 = sets.get(setID1);
    // System.err.println("union.set1: " + set1);
    Set<BuiltInArgument> set2 = sets.get(setID2);
    // System.err.println("union.set2: " + set2);
    Set<BuiltInArgument> union = new HashSet<BuiltInArgument>(set1);

    union.addAll(set2);

    if (!sets.containsKey(setIDResult)) sets.put(setIDResult, union);

    if (SWRLBuiltInUtil.isUnboundArgument(0, arguments)) arguments.set(0, argumentFactory.createDatatypeValueArgument(setIDResult));

    return true;
  } // union

  public boolean difference(List<BuiltInArgument> arguments) throws BuiltInException 
  { 
    // System.err.println("difference.arguments: " + arguments);

    String setIDResult = getSetIDInOperation(arguments, 0, 3); // Does argument checking
    String setName1 = getSetName(arguments, 1); 
    String setName2 = getSetName(arguments, 2);
    int set1NumberOfGroupElements = getSetNumberOfGroupElements(setName1);
    int set2NumberOfGroupElements = getSetNumberOfGroupElements(setName2);
    String setID1 = getSetIDInOperation(arguments, 1, 3, 0, set1NumberOfGroupElements); // Does argument checking
    String setID2 = getSetIDInOperation(arguments, 2, 3, 0 + set1NumberOfGroupElements, set2NumberOfGroupElements); // Does argument checking

    // System.err.println("difference.setName1: " + setName1);
    // System.err.println("difference.setName2: " + setName2);
    // 
    // System.err.println("difference.set1NumberOfGroupElements: " + set1NumberOfGroupElements);
    // System.err.println("difference.set2NumberOfGroupElements: " + set2NumberOfGroupElements);
    // 
    // System.err.println("difference.setID1: " + setID1);
    // System.err.println("difference.setID2: " + setID2);

    Set<BuiltInArgument> set1 = sets.get(setID1);
    // System.err.println("difference.set1: " + set1);
    Set<BuiltInArgument> set2 = sets.get(setID2);
    // System.err.println("difference.set2: " + set2);
    Set<BuiltInArgument> difference = new HashSet<BuiltInArgument>(set1);

    difference.removeAll(set2);

    if (!sets.containsKey(setIDResult)) sets.put(setIDResult, difference);

    if (SWRLBuiltInUtil.isUnboundArgument(0, arguments)) arguments.set(0, argumentFactory.createDatatypeValueArgument(setIDResult));

    return true;
  } // difference

  public boolean contains(List<BuiltInArgument> arguments) throws BuiltInException 
  { 
    String setID = getSetIDInOperation(arguments, 0, 2); // Does argument checking
    boolean result = false;

    // System.err.println("sqwrl.contains: arguments: " + arguments);
    
    if (isSet(setID)) {
      BuiltInArgument element = arguments.get(1);
      // System.err.println("sqwrl.contains: element: " + element);
      result = sets.get(setID).contains(element);
    } else throw new BuiltInException("internal error: no set found for ID '" + setID + "'");
    
    return result;
  } // contains

  public boolean notContains(List<BuiltInArgument> arguments) throws BuiltInException 
  { 
    return !contains(arguments);
  } // notContains

  public boolean greatest(List<BuiltInArgument> arguments) throws BuiltInException 
  { 
    String setID = getSetIDInOperation(arguments, 1, 2); // Does argument checking
    Set<BuiltInArgument> set;
    boolean result = false;

    if (isSet(setID)) set = sets.get(setID);
      else throw new BuiltInException("internal error: no set found for ID '" + setID + "'");
    
    if (!set.isEmpty()) {
      SortedSet<BuiltInArgument> sortedSet = new TreeSet<BuiltInArgument>(set);
      BuiltInArgument greatest = sortedSet.last();

      result = SWRLBuiltInUtil.processResultArgument(arguments, 0, argumentFactory, greatest);
    } // if

    return result;
  } // greatest

  public boolean greatestN(List<BuiltInArgument> arguments) throws BuiltInException 
  { 
    String setID = getSetIDInOperation(arguments, 1, 3); // Does argument checking
    int n = SWRLBuiltInUtil.getArgumentAsAPositiveInteger(2, arguments);
    Set<BuiltInArgument> set;
    boolean result = false;

    if (isSet(setID)) set = sets.get(setID);
      else throw new BuiltInException("internal error: no set found for ID '" + setID + "'");
    
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
    String setID = getSetIDInOperation(arguments, 1, 3); // Does argument checking
    int n = SWRLBuiltInUtil.getArgumentAsAPositiveInteger(2, arguments);
    Set<BuiltInArgument> set;
    boolean result = false;

    if (isSet(setID)) set = sets.get(setID);
      else throw new BuiltInException("internal error: no set found for ID '" + setID + "'");
    
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
    String setID = getSetIDInOperation(arguments, 1, 3); // Does argument checking
    int n = SWRLBuiltInUtil.getArgumentAsAPositiveInteger(2, arguments);
    Set<BuiltInArgument> set;
    boolean result = false;

    if (isSet(setID)) set = sets.get(setID);
      else throw new BuiltInException("internal error: no set found for ID '" + setID + "'");
    
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
    String setID = getSetIDInOperation(arguments, 1, 3); // Does argument checking
    int n = SWRLBuiltInUtil.getArgumentAsAPositiveInteger(2, arguments);
    Set<BuiltInArgument> set;
    boolean result = false;

    if (isSet(setID)) set = sets.get(setID);
      else throw new BuiltInException("internal error: no set found for ID '" + setID + "'");
    
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
    String setID = getSetIDInOperation(arguments, 1, 2); // Does argument checking
    Set<BuiltInArgument> set;
    boolean result = false;

    if (isSet(setID)) set = sets.get(setID);
      else throw new BuiltInException("internal error: no set found for ID '" + setID + "'");

    if (!set.isEmpty()) {
      SortedSet<BuiltInArgument> sortedSet = new TreeSet<BuiltInArgument>(set);
      BuiltInArgument least = sortedSet.first();

      result = SWRLBuiltInUtil.processResultArgument(arguments, 0, argumentFactory, least);
    } // if

    return result;
  } // least

  // Internal methods

  private boolean isSet(String setID) { return sets.containsKey(setID); }

  private String getSetName(List<BuiltInArgument> arguments, int setArgumentNumber) throws BuiltInException
  {
    String ruleName = getInvokingRuleName();
    String setName = SWRLBuiltInUtil.getVariableName(setArgumentNumber, arguments); 
    return ruleName + ":" + setName;
  } // getSetName

  private int getSetNumberOfGroupElements(String setName) throws BuiltInException
  {
    if (!setGroupElementNumbersMap.containsKey(setName)) 
      throw new BuiltInException("internal error: invalid set name '" + setName + "'; no group element number found");

    return setGroupElementNumbersMap.get(setName);
  } // getSetNumberOfGroupElements

  private String getSetIDInMake(List<BuiltInArgument> arguments) throws BuiltInException
  {
	SWRLBuiltInUtil.checkNumberOfArgumentsAtLeast(2, arguments.size());
	
    String ruleName = getInvokingRuleName();
    String setName = SWRLBuiltInUtil.getVariableName(0, arguments); // Always the first argument for a set
    int numberOfGroupArguments = arguments.size() - 2;
    boolean hasGroupPattern  = numberOfGroupArguments != 0;
    String groupPattern = !hasGroupPattern ? "" : SWRLBuiltInUtil.createInvocationPattern(getInvokingBridge(), ruleName, 0, false,
                                                                                          arguments.subList(2, arguments.size()));
    if (hasGroupPattern) {
    	if (!setGroupElementNumbersMap.containsKey(setName)) setGroupElementNumbersMap.put(setName, numberOfGroupArguments);
    	else if (setGroupElementNumbersMap.get(setName) != numberOfGroupArguments) {
    		throw new BuiltInException("internal error: inconsistent number of group elements for set " + setName);
    	} //if
    } // if
	                           
    return ruleName + ":" + setName + ":" + groupPattern;
  } // getSetIDInMake

  private String getSetIDInOperation(List<BuiltInArgument> arguments, int setArgumentIndex, int numberOfCoreArguments) throws BuiltInException
  {
    String ruleName = getInvokingRuleName();
    String setName = SWRLBuiltInUtil.getVariableName(setArgumentIndex, arguments);
    boolean hasGroupPattern  = (arguments.size() > numberOfCoreArguments);
    String groupPattern = "";

    if (hasGroupPattern) {
    	groupPattern = SWRLBuiltInUtil.createInvocationPattern(getInvokingBridge(), ruleName, 0, false, 
    			                                               arguments.subList(numberOfCoreArguments, arguments.size()));
    } // if

    return ruleName + ":" + setName + ":" + groupPattern;
  } // getSetIDInOperation

  private String getSetIDInOperation(List<BuiltInArgument> arguments, int setArgumentNumber, 
                                     int coreArgumentNumber, int groupArgumentOffset, int numberOfRelevantGroupArguments) 
   throws BuiltInException
  {
    String ruleName = getInvokingRuleName();
    String setName = SWRLBuiltInUtil.getVariableName(setArgumentNumber, arguments);
    String groupPattern = SWRLBuiltInUtil.createInvocationPattern(getInvokingBridge(), ruleName, 0, false, 
                                                                  arguments.subList(coreArgumentNumber + groupArgumentOffset, 
                                                                  coreArgumentNumber + groupArgumentOffset + numberOfRelevantGroupArguments)); 

    return ruleName + ":" + setName + ":" + groupPattern;
  } // getCollectionIDInOperation

  
  private ResultImpl getResult(String queryName) throws BuiltInException
  {
    return getInvokingBridge().getSWRLRule(queryName).getSQWRLResult();
  } // getResult
  
  private void hatElementIsNumeric(BuiltInArgument element) throws BuiltInException
  {
    if (!(element instanceof DatatypeValue) || !((DatatypeValue)element).isNumeric()) 
      throw new BuiltInException("may only be applied to sets with numeric data values");
  } // hatElementIsNumeric

  private Set<BuiltInArgument> getSet(String setID) throws BuiltInException
  {
    if (!isSet(setID)) throw new BuiltInException("internal error: no set found for ID " + setID);
    return sets.get(setID);
  } // getSet

} // SWRLBuiltInLibraryImpl
