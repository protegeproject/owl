
package edu.stanford.smi.protegex.owl.swrl.bridge.builtins.temporal;

import edu.stanford.smi.protegex.owl.swrl.bridge.builtins.temporal.exceptions.*;

import edu.stanford.smi.protegex.owl.swrl.bridge.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.builtins.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;

import edu.stanford.smi.protegex.owl.swrl.util.*;
import edu.stanford.smi.protegex.owl.swrl.exceptions.*;

import edu.stanford.smi.protegex.owl.model.OWLModel;

import edu.stanford.smi.protegex.owl.swrl.bridge.xsd.DateTime;

import java.util.*;

/**
 ** Implementation library for SWRL temporal built-ins. See <a href="http://protege.cim3.net/cgi-bin/wiki.pl?SWRLTemporalBuiltIns">here</a>
 ** for documentation on this built-in library.
 */
public class SWRLBuiltInLibraryImpl extends AbstractSWRLBuiltInLibrary
{
  public static final String TemporalLibraryName = "SWRLTemporalBuiltIns";
  
  public static final String Prefix = "temporal:";
  public static final String Namespace = "http://swrl.stanford.edu/ontologies/built-ins/3.3/temporal.owl#";
  
  private static String TemporalDuration = Prefix + "duration";
  private static String TemporalDurationLessThan = Prefix + "durationLessThan";
  private static String TemporalDurationLessOrEqualTo = Prefix + "durationLessThanOrEqualTo";
  private static String TemporalDurationEqualTo = Prefix + "durationEqualTo";
  private static String TemporalDurationGreaterThan = Prefix + "durationGreaterThan";
  private static String TemporalDurationGreaterThanOrEqualTo = Prefix + "durationGreaterThanOrEqualTo";
  private static String TemporalEquals = Prefix + "equals";
  private static String TemporalAfter = Prefix + "after";
  private static String TemporalBefore = Prefix + "before";

  private static String TemporalAdd = Prefix + "add";

  private static String TemporalMeets = Prefix + "meets";
  private static String TemporalMetBy = Prefix + "metBy";
  private static String TemporalOverlaps = Prefix + "overlaps";
  private static String TemporalOverlappedBy = Prefix + "overlappedBy";
  private static String TemporalContains = Prefix + "contains";
  private static String TemporalDuring = Prefix + "during";
  private static String TemporalStarts = Prefix + "starts";
  private static String TemporalStartedBy = Prefix + "startedBy";
  private static String TemporalFinishes = Prefix + "finishes";
  private static String TemporalFinishedBy = Prefix + "finishedBy";
  private static String TemporalIntersects = Prefix + "intersects";

  private static String ValidInstantClassName = Namespace + "ValidInstant";
  private static String ValidPeriodClassName = Namespace + "ValidPeriod";
  private static String GranularityClassName = Namespace + "Granularity";
  private static String HasGranularityPropertyName = Namespace + "hasGranularity";
  private static String HasTimePropertyName = Namespace + "hasTime";
  private static String HasStartTimePropertyName = Namespace + "hasStartTime";
  private static String HasFinishTimePropertyName = Namespace + "hasFinishTime";

  private Temporal temporal;
  private ArgumentFactory argumentFactory;

  public SWRLBuiltInLibraryImpl() 
  { 
    super(TemporalLibraryName); 

    argumentFactory = ArgumentFactory.getFactory();
  } // SWRLBuiltInLibraryImpl

  public void reset()
  {
    XSDDatetimeStringProcessor d = new XSDDatetimeStringProcessor();
    temporal = new Temporal(d);
  } // reset

  public boolean equals(List<BuiltInArgument> arguments) throws BuiltInException { return temporalOperation(TemporalEquals, arguments); }
  public boolean before(List<BuiltInArgument> arguments) throws BuiltInException { return temporalOperation(TemporalBefore, arguments); }
  public boolean after(List<BuiltInArgument> arguments) throws BuiltInException { return temporalOperation(TemporalAfter, arguments); }
  public boolean meets(List<BuiltInArgument> arguments) throws BuiltInException { return temporalOperation(TemporalMeets, arguments); }
  public boolean metBy(List<BuiltInArgument> arguments) throws BuiltInException { return temporalOperation(TemporalMetBy, arguments); }
  public boolean overlaps(List<BuiltInArgument> arguments) throws BuiltInException { return temporalOperation(TemporalOverlaps, arguments); }
  public boolean overlappedBy(List<BuiltInArgument> arguments) throws BuiltInException { return temporalOperation(TemporalOverlappedBy, arguments); }
  public boolean contains(List<BuiltInArgument> arguments) throws BuiltInException { return temporalOperation(TemporalContains, arguments); }
  public boolean during(List<BuiltInArgument> arguments) throws BuiltInException { return temporalOperation(TemporalDuring, arguments); }
  public boolean starts(List<BuiltInArgument> arguments) throws BuiltInException { return temporalOperation(TemporalStarts, arguments); }
  public boolean startedBy(List<BuiltInArgument> arguments) throws BuiltInException { return temporalOperation(TemporalStartedBy, arguments); }
  public boolean finishes(List<BuiltInArgument> arguments) throws BuiltInException { return temporalOperation(TemporalFinishes, arguments); }
  public boolean finishedBy(List<BuiltInArgument> arguments) throws BuiltInException { return temporalOperation(TemporalFinishedBy, arguments); }

  public boolean intersects(List<BuiltInArgument> arguments) throws BuiltInException { return temporalOperation(TemporalIntersects, arguments); }
  public boolean notIntersects(List<BuiltInArgument> arguments) throws BuiltInException { return !temporalOperation(TemporalIntersects, arguments); }

  public boolean notEquals(List<BuiltInArgument> arguments) throws BuiltInException { return !temporalOperation(TemporalEquals, arguments); }
  public boolean notBefore(List<BuiltInArgument> arguments) throws BuiltInException { return !temporalOperation(TemporalBefore, arguments); }
  public boolean notAfter(List<BuiltInArgument> arguments) throws BuiltInException { return !temporalOperation(TemporalAfter, arguments); }
  public boolean notMeets(List<BuiltInArgument> arguments) throws BuiltInException { return !temporalOperation(TemporalMeets, arguments); }
  public boolean notMetBy(List<BuiltInArgument> arguments) throws BuiltInException { return !temporalOperation(TemporalMetBy, arguments); }
  public boolean notOverlaps(List<BuiltInArgument> arguments) throws BuiltInException { return !temporalOperation(TemporalOverlaps, arguments); }
  public boolean notOverlappedBy(List<BuiltInArgument> arguments) throws BuiltInException { return !temporalOperation(TemporalOverlappedBy, arguments); }
  public boolean notContains(List<BuiltInArgument> arguments) throws BuiltInException { return !temporalOperation(TemporalContains, arguments); }
  public boolean notDuring(List<BuiltInArgument> arguments) throws BuiltInException { return !temporalOperation(TemporalDuring, arguments); }
  public boolean notStarts(List<BuiltInArgument> arguments) throws BuiltInException { return !temporalOperation(TemporalStarts, arguments); }
  public boolean notStartedBy(List<BuiltInArgument> arguments) throws BuiltInException { return !temporalOperation(TemporalStartedBy, arguments); }
  public boolean notFinishes(List<BuiltInArgument> arguments) throws BuiltInException { return !temporalOperation(TemporalFinishes, arguments); }
  public boolean notFinishedBy(List<BuiltInArgument> arguments) throws BuiltInException { return !temporalOperation(TemporalFinishedBy, arguments); }

  public boolean notDurationLessThan(List<BuiltInArgument> arguments) throws BuiltInException { return !durationLessThan(arguments); }
  public boolean notDurationLessThanOrEqualTo(List<BuiltInArgument> arguments) throws BuiltInException { return !durationLessThanOrEqualTo(arguments); }
  public boolean notDurationEqualTo(List<BuiltInArgument> arguments) throws BuiltInException { return !durationEqualTo(arguments); }
  public boolean notDurationGreaterThan(List<BuiltInArgument> arguments) throws BuiltInException { return !durationGreaterThan(arguments); }
  public boolean notDurationGreaterThanOrEqualTo(List<BuiltInArgument> arguments) throws BuiltInException { return !durationGreaterThanOrEqualTo(arguments); }

  /**
   ** Accepts either three or four arguments. Returns true if the first duration argument is equal to the difference between two timestamps
   ** at the granularity specified by the final argument. The timestamps are specified as either a mixture of two ValidInstant or datetime
   ** arguments or in single ValidPeriod argument. If the duration argument is unbound, it is assigned to the time difference between the
   ** two timestamps.
   */
  public boolean duration(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result = false;
    long operationResult;
    int granularity, numberOfArguments = arguments.size();
    Period period;
    Instant i1, i2;

    SWRLBuiltInUtil.checkNumberOfArgumentsInRange(3, 4, arguments.size());
    SWRLBuiltInUtil.checkForUnboundNonFirstArguments(arguments);

    try {
      if (numberOfArguments == 3) {
        granularity = getArgumentAsAGranularity(2, arguments);
        period = getArgumentAsAPeriod(1, arguments, granularity);
        operationResult = period.duration(granularity);
      } else { // 4 arguments
        granularity = getArgumentAsAGranularity(3, arguments);
        i1 = getArgumentAsAnInstant(1, arguments, granularity);
        i2 = getArgumentAsAnInstant(2, arguments, granularity);
        operationResult = i1.duration(i2, granularity);
      } // if

      if (SWRLBuiltInUtil.isUnboundArgument(0, arguments)) {
        arguments.set(0, argumentFactory.createDatatypeValueArgument(operationResult)); // Bind the result to the first parameter
        result = true;
      } else {
        long argument1 = SWRLBuiltInUtil.getArgumentAsALong(0, arguments);
        result = (argument1 == operationResult);
      } //if
    } catch (TemporalException e) {
      throw new BuiltInException(e.getMessage());
    } catch (SWRLOWLUtilException e) {
      throw new BuiltInException(e.getMessage());
    } // try

    return result;
  } // duration

  public boolean durationLessThan(List<BuiltInArgument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsInRange(3, 4, arguments.size());
    SWRLBuiltInUtil.checkForUnboundArguments(arguments);
    long argument1, operationResult;
    List<BuiltInArgument> newArguments = SWRLBuiltInUtil.copyArguments(arguments);
    
    argument1 = SWRLBuiltInUtil.getArgumentAsALong(0, arguments);

    newArguments.get(0).setUnbound();
    duration(newArguments);
    operationResult = SWRLBuiltInUtil.getArgumentAsALong(0, newArguments);

    return argument1 < operationResult;
  } // durationLessThan    

  public boolean durationLessThanOrEqualTo(List<BuiltInArgument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsInRange(3, 4, arguments.size());
    SWRLBuiltInUtil.checkForUnboundArguments(arguments);
    long argument1, operationResult;
    List<BuiltInArgument> newArguments = SWRLBuiltInUtil.copyArguments(arguments);
    
    argument1 = SWRLBuiltInUtil.getArgumentAsALong(0, arguments);

    newArguments.get(0).setUnbound();
    duration(newArguments);
    operationResult = SWRLBuiltInUtil.getArgumentAsALong(0, newArguments);

    return argument1 <= operationResult;
  } // durationLessThanOrEqualTo

  public boolean durationEqualTo(List<BuiltInArgument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsInRange(3, 4, arguments.size());
    SWRLBuiltInUtil.checkForUnboundArguments(arguments);
    long argument1, operationResult;
    List<BuiltInArgument> newArguments = SWRLBuiltInUtil.copyArguments(arguments);
    
    argument1 = SWRLBuiltInUtil.getArgumentAsALong(0, arguments);

    newArguments.get(0).setUnbound();
    duration(newArguments);
    operationResult = SWRLBuiltInUtil.getArgumentAsALong(0, newArguments);

    return argument1 == operationResult;
  } // durationLessThan    

  public boolean durationGreaterThan(List<BuiltInArgument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsInRange(3, 4, arguments.size());
    SWRLBuiltInUtil.checkForUnboundArguments(arguments);
    long argument1, operationResult;
    List<BuiltInArgument> newArguments = SWRLBuiltInUtil.copyArguments(arguments);
    
    argument1 = SWRLBuiltInUtil.getArgumentAsALong(0, arguments);

    newArguments.get(0).setUnbound();
    duration(newArguments);
    operationResult = SWRLBuiltInUtil.getArgumentAsALong(0, newArguments);

    return argument1 > operationResult;
  } // durationGreaterThan

  public boolean durationGreaterThanOrEqualTo(List<BuiltInArgument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsInRange(3, 4, arguments.size());
    SWRLBuiltInUtil.checkForUnboundArguments(arguments);
    long argument1, operationResult;
    List<BuiltInArgument> newArguments = SWRLBuiltInUtil.copyArguments(arguments);
    
    argument1 = SWRLBuiltInUtil.getArgumentAsALong(0, arguments);

    newArguments.get(0).setUnbound();
    duration(newArguments);
    operationResult = SWRLBuiltInUtil.getArgumentAsALong(0, newArguments);

    return argument1 >= operationResult;
  } // durationGreaterThanOrEqualTo

  /**
   ** Returns true if the first timestamp argument is equal to the second timestamps argument plus the third count argument at the
   ** granularity specified by the fourth argument. The timestamps are specified as either a ValidInstant, or xsd:DateTime
   ** arguments. If the first argument is unbound, it is assigned the result of the addition.
   */
  public boolean add(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result = false;

    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(4, arguments.size());
    SWRLBuiltInUtil.checkForUnboundNonFirstArguments(arguments);

    try {
      long granuleCount = SWRLBuiltInUtil.getArgumentAsAnInteger(2, arguments);
      int granularity = getArgumentAsAGranularity(3, arguments);
      Instant operationResult = getArgumentAsAnInstant(1, arguments, granularity);

      operationResult.addGranuleCount(granuleCount, granularity);

      if (SWRLBuiltInUtil.isUnboundArgument(0, arguments)) {
        arguments.set(0, argumentFactory.createDatatypeValueArgument(new DateTime(operationResult.toString()))); // Bind the result to the first parameter
        result = true;
      } else {
        Instant argument1 = getArgumentAsAnInstant(0, arguments, granularity);
        result = (argument1.equals(operationResult, Temporal.FINEST));
      } //if
    } catch (TemporalException e) {
      throw new BuiltInException(e.getMessage());
    } catch (SWRLOWLUtilException e) {
      throw new BuiltInException(e.getMessage());
    } // try

    return result;
  } // add

  private boolean temporalOperation(String operation, List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result = false;
    int numberOfArguments = arguments.size();

    SWRLBuiltInUtil.checkNumberOfArgumentsInRange(2, 4, numberOfArguments);
    SWRLBuiltInUtil.checkForUnboundArguments(arguments);

    try {
      boolean hasGranularityArgument = isArgumentAGranularity(numberOfArguments - 1, arguments);
      boolean has2nd3rdInstantArguments = hasGranularityArgument ? (numberOfArguments > 3) : (numberOfArguments > 2);
      int granularity = hasGranularityArgument ? getArgumentAsAGranularity(numberOfArguments - 1, arguments) : Temporal.FINEST;
      Period p1 = getArgumentAsAPeriod(0, arguments, granularity);
      Period p2 = has2nd3rdInstantArguments ? getTwoInstantArgumentsAsAPeriod(1, 2, arguments, granularity) : getArgumentAsAPeriod(1, arguments, granularity);

      if (operation.equals(TemporalEquals)) result = p1.equals(p2, granularity);
      else if (operation.equals(TemporalBefore)) result = p1.before(p2, granularity);
      else if (operation.equals(TemporalAfter)) result = p1.after(p2, granularity);
      else if (operation.equals(TemporalMeets)) result = p1.meets(p2, granularity);
      else if (operation.equals(TemporalMetBy)) result = p1.met_by(p2, granularity);
      else if (operation.equals(TemporalOverlaps)) result = p1.overlaps(p2, granularity);
      else if (operation.equals(TemporalOverlappedBy)) result = p1.overlapped_by(p2, granularity);
      else if (operation.equals(TemporalContains)) result = p1.contains(p2, granularity);
      else if (operation.equals(TemporalDuring)) result = p1.during(p2, granularity);
      else if (operation.equals(TemporalStarts)) result = p1.starts(p2, granularity);
      else if (operation.equals(TemporalStartedBy)) result = p1.started_by(p2, granularity);
      else if (operation.equals(TemporalFinishes)) result = p1.finishes(p2, granularity);
      else if (operation.equals(TemporalFinishedBy)) result = p1.finished_by(p2, granularity);
      else if (operation.equals(TemporalIntersects)) result = p1.intersects(p2, granularity);
      else throw new BuiltInException("internal error - unknown temporal operator '" + operation + "'");
    } catch (TemporalException e) {
      throw new BuiltInException(e.getMessage());
    } catch (SWRLOWLUtilException e) {
      throw new BuiltInException(e.getMessage());
    } // try

    return result;
  } // duration

  private Period getTwoInstantArgumentsAsAPeriod(int firstArgumentNumber, int secondArgumentNumber, 
                                                 List<BuiltInArgument> arguments, int granularity) 
    throws BuiltInException, TemporalException, SWRLOWLUtilException
  {
    Instant i1, i2;
    Period result;

    if (firstArgumentNumber >= arguments.size()) throw new InvalidBuiltInArgumentException(firstArgumentNumber, "out of range");
    if (secondArgumentNumber >= arguments.size()) throw new InvalidBuiltInArgumentException(secondArgumentNumber, "out of range");

    i1 = getArgumentAsAnInstant(firstArgumentNumber, arguments, granularity);
    i2 = getArgumentAsAnInstant(secondArgumentNumber, arguments, granularity);
    result = new Period(temporal, i1, i2, granularity);

    return result;
  } // getTwoInstantArgumentsAsAPeriod

  private Period getArgumentAsAPeriod(int argumentNumber, List<BuiltInArgument> arguments, int granularity) 
    throws BuiltInException, TemporalException, SWRLOWLUtilException
  {
    OWLModel owlModel = getInvokingBridge().getOWLModel();
    Period result = null;

    if (SWRLBuiltInUtil.isArgumentALiteral(argumentNumber, arguments)) {
      String datetimeString = SWRLBuiltInUtil.getArgumentAsAString(argumentNumber, arguments);
      result = new Period(temporal, datetimeString, datetimeString, granularity);
    } else if (SWRLBuiltInUtil.isArgumentAnIndividual(argumentNumber, arguments)) {
      String individualName = SWRLBuiltInUtil.getArgumentAsAnIndividualName(argumentNumber, arguments);
      if (SWRLOWLUtil.isIndividualOfClass(owlModel, individualName, ValidInstantClassName)) {
        Instant instant = getValidInstant(owlModel, individualName, granularity);
        result = new Period(temporal, instant, granularity);
      } else if (SWRLOWLUtil.isIndividualOfClass(owlModel, individualName,  ValidPeriodClassName)) {
        result = getValidPeriod(owlModel, individualName, granularity);
      } else throw new InvalidBuiltInArgumentException(argumentNumber, "individual '" + individualName + "' is not a " +
                                                       ValidInstantClassName + " or " + ValidPeriodClassName);
    } else throw new InvalidBuiltInArgumentException(argumentNumber, "expecting an XSD datetime or " +
                                                     ValidInstantClassName + " or " + ValidPeriodClassName + " individual" +
                                                     ", got '" + arguments.get(argumentNumber) + "'");
    return result;
  } // getArgumentAsAPeriod

  private Instant getArgumentAsAnInstant(int argumentNumber, List<BuiltInArgument> arguments, int granularity) 
    throws BuiltInException, TemporalException, SWRLOWLUtilException
  {
    OWLModel owlModel = getInvokingBridge().getOWLModel();
    Instant result = null;

    if (SWRLBuiltInUtil.isArgumentALiteral(argumentNumber, arguments)) {
      String datetimeString = SWRLBuiltInUtil.getArgumentAsAString(argumentNumber, arguments);
      result = new Instant(temporal, datetimeString, granularity);
    } else if (SWRLBuiltInUtil.isArgumentAnIndividual(argumentNumber, arguments)) {
      String individualName = SWRLBuiltInUtil.getArgumentAsAnIndividualName(argumentNumber, arguments);
      if (SWRLOWLUtil.isIndividualOfClass(owlModel, individualName, ValidInstantClassName)) {
        result = getValidInstant(owlModel, individualName, granularity);
      } else throw new InvalidBuiltInArgumentException(argumentNumber, "individual '" + individualName + "' is not a " +
                                                       ValidInstantClassName);
    } else throw new InvalidBuiltInArgumentException(argumentNumber, "expecting an XSD datetime or " +
                                                     ValidInstantClassName + " individual" +
                                                     ", got '" + arguments.get(argumentNumber) + "'");
    return result;
  } // getArgumentAsAnInstant

  private int getArgumentAsAGranularity(int argumentNumber, List<BuiltInArgument> arguments) 
    throws TemporalException, BuiltInException, SWRLOWLUtilException
  {
    OWLModel owlModel = getInvokingBridge().getOWLModel();
    String granularityName;
    int granularity = -1;

    if (SWRLBuiltInUtil.isArgumentALiteral(argumentNumber, arguments)) {
      granularityName = SWRLBuiltInUtil.getArgumentAsAString(argumentNumber, arguments);
      granularity = Temporal.getIntegerGranularityRepresentation(granularityName);
    } else if (SWRLBuiltInUtil.isArgumentAnIndividual(argumentNumber, arguments)) {
      String individualName = SWRLBuiltInUtil.getArgumentAsAnIndividualName(argumentNumber, arguments);
      if (SWRLOWLUtil.isIndividualOfClass(owlModel, individualName, GranularityClassName)) {
        int hashIndex = individualName.indexOf('#');
        if (hashIndex == -1) granularityName = individualName;
        else granularityName = individualName.substring(hashIndex + 1, individualName.length());
        granularity = Temporal.getIntegerGranularityRepresentation(granularityName);
      } else throw new InvalidBuiltInArgumentException(argumentNumber, "individual '" + individualName + "' is not a " +
                                                       GranularityClassName);
    } else throw new InvalidBuiltInArgumentException(argumentNumber, "expecting a " + GranularityClassName + " individual" +
                                                     ", got '" + arguments.get(argumentNumber) + "'");

    return granularity;
  } // getArgumentAsAGranularity

  private boolean isArgumentAGranularity(int argumentNumber, List<BuiltInArgument> arguments) throws BuiltInException, SWRLOWLUtilException
  {
    OWLModel owlModel = getInvokingBridge().getOWLModel();
    String granularityName;
    boolean result = false;

    if (SWRLBuiltInUtil.isArgumentALiteral(argumentNumber, arguments)) {
      granularityName = SWRLBuiltInUtil.getArgumentAsAString(argumentNumber, arguments);
      result = Temporal.isValidGranularityString(granularityName);
    } else if (SWRLBuiltInUtil.isArgumentAnIndividual(argumentNumber, arguments)) {
      String individualName = SWRLBuiltInUtil.getArgumentAsAnIndividualName(argumentNumber, arguments);
      result = SWRLOWLUtil.isIndividualOfClass(owlModel, individualName, GranularityClassName);
    } // if

    return result;
  } // isArgumentAGranularity

  private Instant getValidInstant(OWLModel owlModel, String individualName, int granularity) 
    throws BuiltInException, TemporalException, SWRLOWLUtilException
  {
    String datetimeString = SWRLOWLUtil.getDatavaluedPropertyValueAsString(owlModel, individualName, HasTimePropertyName);

    return new Instant(temporal, datetimeString, granularity);
  } // getValidInstant

  private Period getValidPeriod(OWLModel owlModel, String individualName, int granularity) 
    throws BuiltInException, TemporalException, SWRLOWLUtilException
  {
    String startDatetimeString = SWRLOWLUtil.getDatavaluedPropertyValueAsString(owlModel, individualName, HasStartTimePropertyName);
    String finishDatetimeString = SWRLOWLUtil.getDatavaluedPropertyValueAsString(owlModel, individualName, HasFinishTimePropertyName);

    return new Period(temporal, startDatetimeString, finishDatetimeString, granularity);
  } // getValidPeriod

  private int getGranularity(OWLModel owlModel, String individualName) 
    throws BuiltInException, TemporalException, SWRLOWLUtilException
  {
    return SWRLOWLUtil.getDatavaluedPropertyValueAsInteger(owlModel, individualName, HasGranularityPropertyName);
  } // getGranularity

} // SWRLBuiltInLibraryImpl

