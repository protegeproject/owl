
package edu.stanford.smi.protegex.owl.swrl.bridge.builtins.temporal;

import edu.stanford.smi.protegex.owl.swrl.bridge.builtins.temporal.exceptions.*;

import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.swrl.util.*;
import edu.stanford.smi.protegex.owl.swrl.exceptions.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.builtins.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;

import edu.stanford.smi.protegex.owl.swrl.bridge.xsd.DateTime;

import java.util.*;

/**
 ** Implementation library for SWRL temporal built-ins. See <a href="http://protege.cim3.net/cgi-bin/wiki.pl?SWRLTemporalBuiltIns">here</a>
 ** for documentation on this built-in library.
 */
public class SWRLBuiltInLibraryImpl extends SWRLBuiltInLibrary
{
  public static final String TemporalLibraryName = "SWRLTemporalBuiltIns";
  
  public static final String Prefix = "temporal:";
  
  private static String TemporalDuration = Prefix + "duration";
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

  private static String ValidInstantClassName = Prefix + "ValidInstant";
  private static String ValidPeriodClassName = Prefix + "ValidPeriod";
  private static String GranularityClassName = Prefix + "Granularity";
  private static String HasGranularityPropertyName = Prefix + "hasGranularity";
  private static String HasTimePropertyName = Prefix + "hasTime";
  private static String HasStartTimePropertyName = Prefix + "hasStartTime";
  private static String HasFinishTimePropertyName = Prefix + "hasFinishTime";

  private Temporal temporal;

  public SWRLBuiltInLibraryImpl() { super(TemporalLibraryName); }

  public void reset()
  {
    XSDDatetimeStringProcessor d = new XSDDatetimeStringProcessor();
    temporal = new Temporal(d);
  } // reset

  public boolean equals(List<Argument> arguments) throws BuiltInException { return temporalOperation(TemporalEquals, arguments); }
  public boolean before(List<Argument> arguments) throws BuiltInException { return temporalOperation(TemporalBefore, arguments); }
  public boolean after(List<Argument> arguments) throws BuiltInException { return temporalOperation(TemporalAfter, arguments); }
  public boolean meets(List<Argument> arguments) throws BuiltInException { return temporalOperation(TemporalMeets, arguments); }
  public boolean metBy(List<Argument> arguments) throws BuiltInException { return temporalOperation(TemporalMetBy, arguments); }
  public boolean overlaps(List<Argument> arguments) throws BuiltInException { return temporalOperation(TemporalOverlaps, arguments); }
  public boolean overlappedBy(List<Argument> arguments) throws BuiltInException { return temporalOperation(TemporalOverlappedBy, arguments); }
  public boolean contains(List<Argument> arguments) throws BuiltInException { return temporalOperation(TemporalContains, arguments); }
  public boolean during(List<Argument> arguments) throws BuiltInException { return temporalOperation(TemporalDuring, arguments); }
  public boolean starts(List<Argument> arguments) throws BuiltInException { return temporalOperation(TemporalStarts, arguments); }
  public boolean startedBy(List<Argument> arguments) throws BuiltInException { return temporalOperation(TemporalStartedBy, arguments); }
  public boolean finishes(List<Argument> arguments) throws BuiltInException { return temporalOperation(TemporalFinishes, arguments); }
  public boolean finishedBy(List<Argument> arguments) throws BuiltInException { return temporalOperation(TemporalFinishedBy, arguments); }

  /**
   ** Accepts either three or four arguments. Returns true if the first duration argument is equal to the difference between two timestamps
   ** at the granularity specified by the final argument. The timestamps are specified as either a mixture of two ValidInstant or datetime
   ** arguments or in single ValidPeriod argument. If the duration argument is unbound, it is assigned to the time difference between the
   ** two timestamps.
   */
  public boolean duration(List<Argument> arguments) throws BuiltInException
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
        arguments.set(0, new LiteralInfo(operationResult)); // Bind the result to the first parameter
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

  /**
   ** Returns true if the first timestamp argument is equal to the second timestamps argument plus the third count argument at the
   ** granularity specified by the fourth argument. The timestamps are specified as either a ValidInstant, or xsd:DateTime
   ** arguments. If the first argument is unbound, it is assigned the result of the addition.
   */
  public boolean add(List<Argument> arguments) throws BuiltInException
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
        arguments.set(0, new LiteralInfo(new DateTime(operationResult.toString()))); // Bind the result to the first parameter
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

  private boolean temporalOperation(String operation, List<Argument> arguments) throws BuiltInException
  {
    boolean result = false;

    SWRLBuiltInUtil.checkNumberOfArgumentsInRange(2, 3, arguments.size());
    SWRLBuiltInUtil.checkForUnboundArguments(arguments);

    try {
      int granularity = (arguments.size() == 2) ? Temporal.FINEST : getArgumentAsAGranularity(2, arguments);
      Period p1 = getArgumentAsAPeriod(0, arguments, granularity);
      Period p2 = getArgumentAsAPeriod(1, arguments, granularity);

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
      else throw new BuiltInException("internal error - unknown temporal operator '" + operation + "'");
    } catch (TemporalException e) {
      throw new BuiltInException(e.getMessage());
    } catch (SWRLOWLUtilException e) {
      throw new BuiltInException(e.getMessage());
    } // try

    return result;
  } // duration

  private Period getArgumentAsAPeriod(int argumentNumber, List<Argument> arguments, int granularity) 
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

  private Instant getArgumentAsAnInstant(int argumentNumber, List<Argument> arguments, int granularity) 
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

  private int getArgumentAsAGranularity(int argumentNumber, List<Argument> arguments) 
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
        int colonIndex = individualName.indexOf(':');
        if (colonIndex == -1) granularityName = individualName;
        else granularityName = individualName.substring(colonIndex + 1, individualName.length());
        granularity = Temporal.getIntegerGranularityRepresentation(granularityName);
      } else throw new InvalidBuiltInArgumentException(argumentNumber, "individual '" + individualName + "' is not a " +
                                                       GranularityClassName);
    } else throw new InvalidBuiltInArgumentException(argumentNumber, "expecting a " + GranularityClassName + " individual" +
                                                     ", got '" + arguments.get(argumentNumber) + "'");

    return granularity;
  } // getArgumentAsAGranularity

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

