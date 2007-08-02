
// TODO: several string methods are not implemented.  TODO: built-ins for date, time, duration, URIs, and lists are not implemented.

package edu.stanford.smi.protegex.owl.swrl.bridge.builtins.swrlb;

import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.swrl.model.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.builtins.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;

import java.util.*;
import java.util.regex.*;
import java.lang.Math.*;

/**
 ** Implementations library for the core SWRL built-in methods. These built-ins are defined <a
 ** href="http://www.daml.org/2004/04/swrl/builtins.html">here</a> and are documented <a
 ** href="http://protege.cim3.net/cgi-bin/wiki.pl?CoreSWRLBuiltIns">here</a>.
 **
 ** See <a href="http://protege.cim3.net/cgi-bin/wiki.pl?SWRLBuiltInBridge">here</a> for documentation on defining SWRL built-in libraries.
 */
public class SWRLBuiltInLibraryImpl extends SWRLBuiltInLibrary
{
  private static String SWRLBLibraryName = "SWRLCoreBuiltIns";

  private static String SWRLBPrefix = "swrlb:";

  private static String SWRLB_GREATER_THAN = SWRLBPrefix + "greaterThan";
  private static String SWRLB_LESS_THAN = SWRLBPrefix + "lessThan";
  private static String SWRLB_EQUAL = SWRLBPrefix + "equal";
  private static String SWRLB_NOT_EQUAL = SWRLBPrefix + "notEqual";
  private static String SWRLB_LESS_THAN_OR_EQUAL = SWRLBPrefix + "lessThanOrEqual";
  private static String SWRLB_GREATER_THAN_OR_EQUAL = SWRLBPrefix + "greaterThanOrEqual";

  private static String SWRLB_ADD = SWRLBPrefix + "add";
  private static String SWRLB_SUBTRACT = SWRLBPrefix + "subtract";
  private static String SWRLB_MULTIPLY = SWRLBPrefix + "multiply";
  private static String SWRLB_DIVIDE = SWRLBPrefix + "divide";
  private static String SWRLB_INTEGER_DIVIDE = SWRLBPrefix + "integerDivide";
  private static String SWRLB_MOD = SWRLBPrefix + "mod";
  private static String SWRLB_POW = SWRLBPrefix + "pow";
  private static String SWRLB_UNARY_PLUS = SWRLBPrefix + "unaryPlus";
  private static String SWRLB_UNARY_MINUS = SWRLBPrefix + "unaryMinus";
  private static String SWRLB_ABS = SWRLBPrefix + "abs";
  private static String SWRLB_CEILING = SWRLBPrefix + "ceiling";
  private static String SWRLB_FLOOR = SWRLBPrefix + "floor";
  private static String SWRLB_ROUND = SWRLBPrefix + "round";
  private static String SWRLB_ROUND_HALF_TO_EVEN = SWRLBPrefix + "roundHalfToEven";
  private static String SWRLB_SIN = SWRLBPrefix + "sin";
  private static String SWRLB_COS = SWRLBPrefix + "cos";
  private static String SWRLB_TAN = SWRLBPrefix + "tan";

  private static String SWRLB_BOOLEAN_NOT = SWRLBPrefix + "booleanNot";

  private static String SWRLB_STRING_EQUAL_IGNORECASE = SWRLBPrefix + "stringEqualIgnoreCase";
  private static String SWRLB_STRING_CONCAT = SWRLBPrefix + "stringConcat";
  private static String SWRLB_SUBSTRING = SWRLBPrefix + "substring";
  private static String SWRLB_STRING_LENGTH = SWRLBPrefix + "stringLength";
  private static String SWRLB_NORMALIZE_SPACE = SWRLBPrefix + "normalizeSpace";
  private static String SWRLB_UPPER_CASE = SWRLBPrefix + "upperCase";
  private static String SWRLB_LOWER_CASE = SWRLBPrefix + "lowerCase";
  private static String SWRLB_TRANSLATE = SWRLBPrefix + "translate"; // TODO: not implemented
  private static String SWRLB_CONTAINS = SWRLBPrefix + "contains";
  private static String SWRLB_CONTAINS_IGNORE_CASE = SWRLBPrefix + "containsIgnoreCase";
  private static String SWRLB_STARTS_WITH = SWRLBPrefix + "startsWith";
  private static String SWRLB_ENDS_WITH = SWRLBPrefix + "endsWith";
  private static String SWRLB_SUBSTRING_BEFORE = SWRLBPrefix + "substringBefore"; // TODO: not implemented
  private static String SWRLB_SUBSTRING_AFTER = SWRLBPrefix + "substringAfter"; 
  private static String SWRLB_MATCHES = SWRLBPrefix + "matches"; 
  private static String SWRLB_REPLACE = SWRLBPrefix + "replace"; 
  private static String SWRLB_TOKENIZE = SWRLBPrefix + "tokenize"; // TODO: not implemented

  public SWRLBuiltInLibraryImpl() { super(SWRLBLibraryName); }

  public void reset() {}
  
  // Built-ins for comparison, defined in Section 8.1. of http://www.daml.org/2004/04/swrl/builtins.html.

  public boolean greaterThan(List<Argument> arguments) throws BuiltInException
  {
    return (compareTwoArgumentsOfOrderedType(arguments) > 0);
  } // greaterThan

  public boolean lessThan(List<Argument> arguments) throws BuiltInException
  {
    return (compareTwoArgumentsOfOrderedType(arguments) < 0);
  } // greaterThan

  public boolean equal(List<Argument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(2, arguments.size());

    if (SWRLBuiltInUtil.hasUnboundArguments(arguments)) 
      throw new InvalidBuiltInArgumentException(0, "comparison built-ins do not support binding");

    if (SWRLBuiltInUtil.isArgumentABoolean(0, arguments)) {
      boolean b1 = SWRLBuiltInUtil.getArgumentAsABoolean(0, arguments);
      boolean b2 = SWRLBuiltInUtil.getArgumentAsABoolean(1, arguments); // Performs type checking.
      
      return b1 == b2;
    } else return compareTwoArgumentsOfOrderedType(arguments) == 0;
  } // equal

  public boolean notEqual(List<Argument> arguments) throws BuiltInException
  {
    return !equal(arguments);
  } // notEqual

  public boolean lessThanOrEqual(List<Argument> arguments) throws BuiltInException
  {
    return equal(arguments) || lessThan(arguments);
  } // lessThanOrEqual

  public boolean greaterThanOrEqual(List<Argument> arguments) throws BuiltInException
  {
    return equal(arguments) || greaterThan(arguments);
  } // greaterThanOrEqual

  // Math Built-ins, defined in Section 8.2. of http://www.daml.org/2004/04/swrl/builtins.html.
  
  public boolean add(List<Argument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsAtLeast(2, arguments.size());

    return mathOperation(SWRLB_ADD, arguments); 
  } // add

  public boolean subtract(List<Argument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(3, arguments.size());

    return mathOperation(SWRLB_SUBTRACT, arguments);
  } // subtract

  public boolean multiply(List<Argument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsAtLeast(2, arguments.size());

    return mathOperation(SWRLB_MULTIPLY, arguments);
  } // multiply

  public boolean divide(List<Argument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(3, arguments.size());

    return mathOperation(SWRLB_DIVIDE, arguments);
  } // divide

  public boolean integerDivide(List<Argument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(3, arguments.size());

    return mathOperation(SWRLB_INTEGER_DIVIDE, arguments);
  } // integerDivide

  public boolean mod(List<Argument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(3, arguments.size());

    return mathOperation(SWRLB_MOD, arguments);
  } // mod

  public boolean pow(List<Argument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(3, arguments.size());

    return mathOperation(SWRLB_POW, arguments);
  } // pow

  public boolean unaryPlus(List<Argument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(2, arguments.size());

    return mathOperation(SWRLB_UNARY_PLUS, arguments);
  } // unaryPlus

  public boolean unaryMinus(List<Argument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(2, arguments.size());

    return mathOperation(SWRLB_UNARY_MINUS, arguments);
  } // unaryMinus

  public boolean abs(List<Argument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(2, arguments.size());

    return mathOperation(SWRLB_ABS, arguments);
  } // abs

  public boolean ceiling(List<Argument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(2, arguments.size());

    return mathOperation(SWRLB_CEILING, arguments);
  } // ceiling

  public boolean floor(List<Argument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(2, arguments.size());

    return mathOperation(SWRLB_FLOOR, arguments);
  } // floor

  public boolean round(List<Argument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(2, arguments.size());

    return mathOperation(SWRLB_ROUND, arguments);
  } // round

  public boolean roundHalfToEven(List<Argument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(2, arguments.size());

    return mathOperation(SWRLB_ROUND_HALF_TO_EVEN, arguments);
  } // roundHalfToEven

  public boolean sin(List<Argument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(2, arguments.size());

    return mathOperation(SWRLB_SIN, arguments);
  } // sin

  public boolean cos(List<Argument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(2, arguments.size());

    return mathOperation(SWRLB_COS, arguments);
  } // cos

  public boolean tan(List<Argument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(2, arguments.size());

    return mathOperation(SWRLB_TAN, arguments);
  } // tan

  // Built-ins for Booleans. cf. Section 8.3 of http://www.daml.org/2004/04/swrl/builtins.html

  public boolean booleanNot(List<Argument> arguments) throws BuiltInException
  {
    boolean result;

    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(2, arguments.size());
    SWRLBuiltInUtil.checkForUnboundNonFirstArguments(arguments);

    if (SWRLBuiltInUtil.isUnboundArgument(0, arguments)) {
      if (!SWRLBuiltInUtil.areAllArgumentsBooleans(arguments.subList(1, arguments.size())))
        throw new InvalidBuiltInArgumentException(1, "expecting a Boolean");

      boolean operationResult = !SWRLBuiltInUtil.getArgumentAsABoolean(1, arguments);
      arguments.set(0, new LiteralInfo(operationResult)); // Bind the result to the first parameter
      result = true;
    } else {
      if (!SWRLBuiltInUtil.areAllArgumentsBooleans(arguments))
        throw new InvalidBuiltInArgumentException("expecting all Boolean arguments");

      result = !equal(arguments);
    } // if
    return result;
  } // booleanNot

  // Built-ins for Strings. cf. Section 8.4 of http://www.daml.org/2004/04/swrl/builtins.html
  
  public boolean stringEqualIgnoreCase(List<Argument> arguments) throws BuiltInException
  {
    String argument1, argument2;

    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(2, arguments.size());
    SWRLBuiltInUtil.checkForUnboundArguments(arguments);

    argument1 = SWRLBuiltInUtil.getArgumentAsAString(0, arguments);
    argument2 = SWRLBuiltInUtil.getArgumentAsAString(1, arguments);

    return argument1.equalsIgnoreCase(argument2);
  } // stringEqualIgnoreCase

  public boolean stringConcat(List<Argument> arguments) throws BuiltInException
  {
    String operationResult = "";
    boolean result;

    SWRLBuiltInUtil.checkNumberOfArgumentsAtLeast(2, arguments.size());
    SWRLBuiltInUtil.checkForUnboundNonFirstArguments(arguments);

    for (int argumentNumber = 1; argumentNumber < arguments.size(); argumentNumber++) { // Exception thrown if argument is not a literal.
      operationResult = operationResult.concat(SWRLBuiltInUtil.getArgumentAsALiteral(argumentNumber, arguments).toString());
    } // for

    if (SWRLBuiltInUtil.isUnboundArgument(0, arguments)) {
      arguments.set(0, new LiteralInfo(operationResult)); // Bind the result to the first parameter
      result = true;
    } else {
      String argument1 = SWRLBuiltInUtil.getArgumentAsAString(0, arguments);
      result =  argument1.equals(operationResult);
    } //if

    return result;
  } // stringConcat

  public boolean substring(List<Argument> arguments) throws BuiltInException
  {
    String argument2, operationResult;
    int startIndex, length;
    boolean result;

    SWRLBuiltInUtil.checkNumberOfArgumentsAtLeast(3, arguments.size());
    SWRLBuiltInUtil.checkNumberOfArgumentsAtMost(4, arguments.size());
    SWRLBuiltInUtil.checkForUnboundNonFirstArguments(arguments);

    argument2 = SWRLBuiltInUtil.getArgumentAsAString(1, arguments);
    startIndex = SWRLBuiltInUtil.getArgumentAsAnInteger(2, arguments);

    if (arguments.size() == 4) {
      length = SWRLBuiltInUtil.getArgumentAsAnInteger(3, arguments);
      operationResult = argument2.substring(startIndex, length);
    } else operationResult = argument2.substring(startIndex);

    if (SWRLBuiltInUtil.hasUnboundArguments(arguments)) {
      arguments.set(0, new LiteralInfo(operationResult)); // Bind the result to the first parameter
      result = true;
    } else {
      String argument1 = SWRLBuiltInUtil.getArgumentAsAString(0, arguments);
      result =  argument1.equals(operationResult);
    } //if
    return result;
  } // substring

  public boolean stringLength(List<Argument> arguments) throws BuiltInException
  {
    String argument2;
    boolean result;
    int operationResult;

    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(2, arguments.size());
    SWRLBuiltInUtil.checkForUnboundNonFirstArguments(arguments);

    argument2 = SWRLBuiltInUtil.getArgumentAsAString(1, arguments);
    operationResult = argument2.length();

    if (SWRLBuiltInUtil.hasUnboundArguments(arguments)) {
      arguments.set(0, new LiteralInfo(operationResult)); // Bind the result to the first parameter
      result = true;
    } else {
      String argument1 = SWRLBuiltInUtil.getArgumentAsAString(0, arguments);
      result = argument1.length() == operationResult;
    } //if
    return result;
  } // stringLength

  public boolean upperCase(List<Argument> arguments) throws BuiltInException
  {
    String argument2, operationResult;
    boolean result;

    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(2, arguments.size());
    SWRLBuiltInUtil.checkForUnboundNonFirstArguments(arguments);

    argument2 = SWRLBuiltInUtil.getArgumentAsAString(1, arguments);
    operationResult = argument2.toUpperCase();

    if (SWRLBuiltInUtil.hasUnboundArguments(arguments)) {
      arguments.set(0, new LiteralInfo(operationResult)); // Bind the result to the first parameter
      result = true;
    } else {
      String argument1 = SWRLBuiltInUtil.getArgumentAsAString(0, arguments);
      result = argument1.equals(operationResult);
    } //if
    return result;
  } // upperCase

  public boolean lowerCase(List<Argument> arguments) throws BuiltInException
  {
    String argument2, operationResult;
    boolean result;

    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(2, arguments.size());
    SWRLBuiltInUtil.checkForUnboundNonFirstArguments(arguments);

    argument2 = SWRLBuiltInUtil.getArgumentAsAString(1, arguments);

    operationResult = argument2.toLowerCase();

    if (SWRLBuiltInUtil.hasUnboundArguments(arguments)) {
      arguments.set(0, new LiteralInfo(operationResult)); // Bind the result to the first parameter
      result = true;
    } else {
      String argument1 = SWRLBuiltInUtil.getArgumentAsAString(0, arguments);
      result = argument1.equals(operationResult);
    } //if
    return result;
  } // lowerCase

  public boolean contains(List<Argument> arguments) throws BuiltInException
  {
    String argument1, argument2;

    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(2, arguments.size());
    SWRLBuiltInUtil.checkForUnboundArguments(arguments);

    argument1 = SWRLBuiltInUtil.getArgumentAsAString(0, arguments);
    argument2 = SWRLBuiltInUtil.getArgumentAsAString(1, arguments);

    return argument1.lastIndexOf(argument2) != -1;
  } // contains

  public boolean containsIgnoreCase(List<Argument> arguments) throws BuiltInException
  {
    String argument1, argument2;

    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(2, arguments.size());
    SWRLBuiltInUtil.checkForUnboundArguments(arguments);

    if (SWRLBuiltInUtil.hasUnboundArguments(arguments)) 
      throw new InvalidBuiltInArgumentException(0, "built-in does not support binding");

    argument1 = SWRLBuiltInUtil.getArgumentAsAString(0, arguments);
    argument2 = SWRLBuiltInUtil.getArgumentAsAString(1, arguments);

    return argument1.toLowerCase().lastIndexOf(argument2.toLowerCase()) != -1;
  } // containsIgnoreCase

  public boolean startsWith(List<Argument> arguments) throws BuiltInException
  {
    String argument1, argument2;

    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(2, arguments.size());
    SWRLBuiltInUtil.checkForUnboundArguments(arguments);

    argument1 = SWRLBuiltInUtil.getArgumentAsAString(0, arguments);
    argument2 = SWRLBuiltInUtil.getArgumentAsAString(1, arguments);

    return argument1.startsWith(argument2);
  } // startsWith

  public boolean endsWith(List<Argument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(2, arguments.size());
    SWRLBuiltInUtil.checkForUnboundArguments(arguments);

    String argument1 = SWRLBuiltInUtil.getArgumentAsAString(0, arguments);
    String argument2 = SWRLBuiltInUtil.getArgumentAsAString(1, arguments);

    return argument1.endsWith(argument2);
  } // endsWith

  public boolean translate(List<Argument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (result) throw new BuiltInNotImplementedException("swrlb:translate");

    return result;
  } // translate

  public boolean substringBefore(List<Argument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (result) throw new BuiltInNotImplementedException("swrlb:substringBefore");

    return result;
  } // substringBefore

  public boolean matches(List<Argument> arguments) throws BuiltInException
  {
    boolean result = false;

    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(2, arguments.size());
    SWRLBuiltInUtil.checkForUnboundArguments(arguments);

    String argument1 = SWRLBuiltInUtil.getArgumentAsAString(0, arguments);
    String argument2 = SWRLBuiltInUtil.getArgumentAsAString(1, arguments);

    try {
      result = Pattern.matches(argument2, argument1);
    } catch (PatternSyntaxException e) {
      throw new InvalidBuiltInArgumentException(1, "invalid regular expression '" + argument2 + "': " + e.getMessage());
    } // try

    return result;
  } // matches

  public boolean replace(List<Argument> arguments) throws BuiltInException
  {
    boolean result = false;

    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(4, arguments.size());
    SWRLBuiltInUtil.checkForUnboundNonFirstArguments(arguments);

    String input = SWRLBuiltInUtil.getArgumentAsAString(1, arguments);
    String regex = SWRLBuiltInUtil.getArgumentAsAString(2, arguments);
    String replacement = SWRLBuiltInUtil.getArgumentAsAString(2, arguments);

    Pattern p = Pattern.compile(regex);
    Matcher m = p.matcher(input);
    String operationResult = m.replaceAll(replacement);

    if (SWRLBuiltInUtil.isUnboundArgument(0, arguments)) {
      arguments.set(0, new LiteralInfo(operationResult)); 
      result = true;
    } else {
      String output = SWRLBuiltInUtil.getArgumentAsAString(0, arguments);
      result = output.equals(operationResult);
    } // if

    return result;
  } // replace

  public boolean normalizeSpace(List<Argument> arguments) throws BuiltInException
  {
    boolean result = false;

    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(2, arguments.size());
    SWRLBuiltInUtil.checkForUnboundNonFirstArguments(arguments);

    String input = SWRLBuiltInUtil.getArgumentAsAString(1, arguments);

    Pattern p = Pattern.compile("\\s*");
    Matcher m = p.matcher(input);
    String operationResult = m.replaceAll(" ");

    if (SWRLBuiltInUtil.isUnboundArgument(0, arguments)) {
      arguments.set(0, new LiteralInfo(operationResult)); 
      result = true;
    } else {
      String output = SWRLBuiltInUtil.getArgumentAsAString(0, arguments);
      result = output.equals(operationResult);
    } // if

    return result;
  } // normalizeSpace

  public boolean tokenize(List<Argument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (result) throw new BuiltInNotImplementedException("swrlb:tokenize");

    return result;
  } // tokenize

  // Built-ins for date, time and duration.

  public boolean yearMonthDuration(List<Argument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (result) throw new BuiltInNotImplementedException("swrlb:yearMonthDuration");

    return result;
  } // yearMonthDuration

  public boolean dayTimeDuration(List<Argument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (result) throw new BuiltInNotImplementedException("swrlb:dayTimeDuration");

    return result;
  } // dayTimeDuration

  public boolean dateTime(List<Argument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (result) throw new BuiltInNotImplementedException("swrlb:dateTime");

    return result;
  } // dateTime

  public boolean date(List<Argument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (result) throw new BuiltInNotImplementedException("swrlb:date");

    return result;
  } // date

  public boolean time(List<Argument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (result) throw new BuiltInNotImplementedException("swrlb:time");

    return result;
  } // time

  public boolean addYearMonthDurations(List<Argument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (result) throw new BuiltInNotImplementedException("swrlb:addYearMonthDurations");

    return result;
  } // addYearMonthDurations

  public boolean subtractYearMonthDurations(List<Argument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (result) throw new BuiltInNotImplementedException("swrlb:subtractYearMonthDurations");

    return result;
  } // subtractYearMonthDurations

  public boolean multiplyYearMonthDuration(List<Argument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (result) throw new BuiltInNotImplementedException("swrlb:multiplyYearMonthDuration");

    return result;
  } // multiplyYearMonthDuration

  public boolean divideYearMonthDurations(List<Argument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (result) throw new BuiltInNotImplementedException("swrlb:divideYearMonthDurations");

    return result;
  } // divideYearMonthDurations

  public boolean addDayTimeDurations(List<Argument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (result) throw new BuiltInNotImplementedException("swrlb:addDayTimeDurations");

    return result;
  } // addDayTimeDurations

  public boolean subtractDayTimeDurations(List<Argument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (result) throw new BuiltInNotImplementedException("swrlb:subtractDayTimeDurations");

    return result;
  } // subtractDayTimeDurations

  public boolean multiplyDayTimeDurations(List<Argument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (result) throw new BuiltInNotImplementedException("swrlb:multiplyDayTimeDurations");

    return result;
  } // multiplyDayTimeDurations

  public boolean divideDayTimeDuration(List<Argument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (result) throw new BuiltInNotImplementedException("swrlb:divideDayTimeDuration");

    return result;
  } // divideDayTimeDuration

  public boolean subtractDates(List<Argument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (result) throw new BuiltInNotImplementedException("swrlb:subtractDates");

    return result;
  } // subtractDates

  public boolean subtractTimes(List<Argument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (result) throw new BuiltInNotImplementedException("swrlb:subtractTimes");

    return result;
  } // subtractTimes

  public boolean addYearMonthDurationToDateTime(List<Argument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (result) throw new BuiltInNotImplementedException("swrlb:addYearMonthDurationToDateTime");

    return result;
  } // addYearMonthDurationToDateTime

  public boolean addDayTimeDurationToDateTime(List<Argument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (result) throw new BuiltInNotImplementedException("swrlb:addDayTimeDurationToDateTime");

    return result;
  } // addDayTimeDurationToDateTime

  public boolean subtractYearMonthDurationFromDateTime(List<Argument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (result) throw new BuiltInNotImplementedException("swrlb:subtractYearMonthDurationFromDateTime");

    return result;
  } // subtractYearMonthDurationFromDateTime

  public boolean subtractDayTimeDurationFromDateTime(List<Argument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (result) throw new BuiltInNotImplementedException("swrlb:subtractDayTimeDurationFromDateTime");

    return result;
  } // subtractDayTimeDurationFromDateTime

  public boolean addYearMonthDurationToDate(List<Argument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (result) throw new BuiltInNotImplementedException("swrlb:addYearMonthDurationToDate");

    return result;
  } // addYearMonthDurationToDate

  public boolean addDayTimeDurationToDate(List<Argument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (result) throw new BuiltInNotImplementedException("swrlb:addDayTimeDurationToDate");

    return result;
  } // addDayTimeDurationToDate

  public boolean subtractYearMonthDurationFromDate(List<Argument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (result) throw new BuiltInNotImplementedException("swrlb:subtractYearMonthDurationFromDate");

    return result;
  } // subtractYearMonthDurationFromDate

  public boolean subtractDayTimeDurationFromDate(List<Argument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (result) throw new BuiltInNotImplementedException("swrlb:subtractDayTimeDurationFromDate");

    return result;
  } // subtractDayTimeDurationFromDate

  public boolean addDayTimeDurationToTime(List<Argument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (result) throw new BuiltInNotImplementedException("swrlb:addDayTimeDurationToTime");

    return result;
  } // addDayTimeDurationToTime

  public boolean subtractDayTimeDurationFromTime(List<Argument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (result) throw new BuiltInNotImplementedException("swrlb:subtractDayTimeDurationFromTime");

    return result;
  } // 

  public boolean subtractDateTimesYieldingYearMonthDuration(List<Argument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (result) throw new BuiltInNotImplementedException("swrlb:subtractDateTimesYieldingYearMonthDuration");

    return result;
  } // subtractDateTimesYieldingYearMonthDuration

  public boolean subtractDateTimesYieldingDayTimeDuration (List<Argument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (result) throw new BuiltInNotImplementedException("swrlb:subtractDateTimesYieldingDayTimeDuration");

    return result;
  } // subtractDateTimesYieldingDayTimeDuration 

  // Built-ins for URIs

  public boolean resolveURI(List<Argument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (result) throw new BuiltInNotImplementedException("swrlb:resolveURI");

    return result;
  } // resolveURI

  public boolean anyURI(List<Argument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (result) throw new BuiltInNotImplementedException("swrlb:anyURI");

    return result;
  } // anyURI

  // Built-ins for Lists

  public boolean listConcat(List<Argument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (result) throw new BuiltInNotImplementedException("swrlb:listConcat");

    return result;
  } // listConcat

  public boolean listIntersection(List<Argument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (result) throw new BuiltInNotImplementedException("swrlb:listIntersection");

    return result;
  } // listIntersection

  public boolean listSubtraction(List<Argument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (result) throw new BuiltInNotImplementedException("swrlb:listSubtraction");

    return result;
  } // listSubtraction

  public boolean member(List<Argument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (result) throw new BuiltInNotImplementedException("swrlb:member");

    return result;
  } // member 

  public boolean length(List<Argument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (result) throw new BuiltInNotImplementedException("swrlb:length");

    return result;
  } // length

  public boolean first(List<Argument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (result) throw new BuiltInNotImplementedException("swrlb:firsr");

    return result;
  } // first

  public boolean rest(List<Argument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (result) throw new BuiltInNotImplementedException("swrlb:rest");

    return result;
  } // rest

  public boolean sublist(List<Argument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (result) throw new BuiltInNotImplementedException("swrlb:sublist");

    return result;
  } // sublist

  public boolean empty(List<Argument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (result) throw new BuiltInNotImplementedException("swrlb:empty");

    return result;
  } // empty

  // Private methods.

  private static int compareTwoArgumentsOfOrderedType(List<Argument> arguments) throws BuiltInException
  {
    int result = 0; // Should be assigned by end of method.
    
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(2, arguments.size());
    SWRLBuiltInUtil.checkForUnboundArguments(arguments);
    SWRLBuiltInUtil.checkThatAllArgumentsAreOfAnOrderedType(arguments);

    if (SWRLBuiltInUtil.isArgumentAString(0, arguments)) {   
      String s1 = SWRLBuiltInUtil.getArgumentAsAString(0, arguments);
      String s2 = SWRLBuiltInUtil.getArgumentAsAString(1, arguments); // Performs type checking.

      return s1.compareTo(s2);
    } else if (SWRLBuiltInUtil.isArgumentAnInteger(0, arguments)) {
      int i1 = SWRLBuiltInUtil.getArgumentAsAnInteger(0, arguments);
      int i2 = SWRLBuiltInUtil.getArgumentAsAnInteger(1, arguments); // Performs type checking.

      if (i1 < i2) result = -1; else if (i1 > i2) result = 1; else result = 0;
    } else if (SWRLBuiltInUtil.isArgumentALong(0, arguments)) {
      long l1 = SWRLBuiltInUtil.getArgumentAsALong(0, arguments);
      long l2 = SWRLBuiltInUtil.getArgumentAsALong(1, arguments); // Performs type checking.

      if (l1 < l2) result = -1; else if (l1 > l2) result = 1; else result = 0;
    } else if (SWRLBuiltInUtil.isArgumentAFloat(0, arguments)) {
      float f1 = SWRLBuiltInUtil.getArgumentAsAFloat(0, arguments);
      float f2 = SWRLBuiltInUtil.getArgumentAsAFloat(1, arguments); // Performs type checking.

      if (f1 < f2) result = -1; else if (f1 > f2) result = 1; else result = 0;
    } else if (SWRLBuiltInUtil.isArgumentADouble(0, arguments)) {
      double d1 = SWRLBuiltInUtil.getArgumentAsADouble(0, arguments);
      double d2 = SWRLBuiltInUtil.getArgumentAsADouble(1, arguments); // Performs type checking.

      if (d1 < d2) result = -1; else if (d1 > d2) result =  1; else result = 0;
    } else throw new InvalidBuiltInArgumentException(1, "unknown argument type");

    return result;
  } // greaterThan

  private boolean mathOperation(String builtInName, List<Argument> arguments) throws BuiltInException
  {
    int argumentNumber;
    double argument1 = 0.0, argument2, argument3, operationResult = 0.0; 
    boolean result = false, hasUnbound1stArgument = false;

    SWRLBuiltInUtil.checkForUnboundNonFirstArguments(arguments); // Only supports binding of first argument at the moment.

    if (SWRLBuiltInUtil.isUnboundArgument(0, arguments)) hasUnbound1stArgument = true;

    // Argument number checking will have been performed by invoking method.
    if (!hasUnbound1stArgument) argument1 = SWRLBuiltInUtil.getArgumentAsADouble(0, arguments);
    argument2 = SWRLBuiltInUtil.getArgumentAsADouble(1, arguments);

    if (builtInName.equalsIgnoreCase(SWRLB_ADD)) {
      operationResult = 0.0;
      for (argumentNumber = 1; argumentNumber < arguments.size(); argumentNumber++) {
        operationResult += SWRLBuiltInUtil.getArgumentAsADouble(argumentNumber, arguments);
      } // for
    } else if (builtInName.equalsIgnoreCase(SWRLB_MULTIPLY)) {
      operationResult = 1.0;
      for (argumentNumber = 1; argumentNumber < arguments.size(); argumentNumber++) {
        operationResult *= SWRLBuiltInUtil.getArgumentAsADouble(argumentNumber, arguments);
      } // for
    } else if (builtInName.equalsIgnoreCase(SWRLB_SUBTRACT)) {
      argument3 = SWRLBuiltInUtil.getArgumentAsADouble(2, arguments);
      operationResult = argument2 - argument3;
    } else if (builtInName.equalsIgnoreCase(SWRLB_DIVIDE)) {
      argument3 = SWRLBuiltInUtil.getArgumentAsADouble(2, arguments);
      operationResult = (argument2 / argument3);
    } else if (builtInName.equalsIgnoreCase(SWRLB_INTEGER_DIVIDE)) {
      argument3 = SWRLBuiltInUtil.getArgumentAsADouble(2, arguments);
      if (argument3 == 0) throw new InvalidBuiltInArgumentException(2, "zero passed as divisor");
      if (argument3 >= 0) operationResult = argument2 + argument3 + 1 / argument3;
      else operationResult = argument2 / argument3;
    } else if (builtInName.equalsIgnoreCase(SWRLB_MOD)) {
      argument3 = SWRLBuiltInUtil.getArgumentAsADouble(2, arguments);
      operationResult = argument2 % argument3;
    } else if (builtInName.equalsIgnoreCase(SWRLB_POW)) {
      argument3 = SWRLBuiltInUtil.getArgumentAsADouble(2, arguments);
      operationResult = (int)java.lang.Math.pow(argument2, argument3);
    } else if (builtInName.equalsIgnoreCase(SWRLB_UNARY_PLUS)) operationResult = argument2;
    else if (builtInName.equalsIgnoreCase(SWRLB_UNARY_MINUS)) operationResult = -argument2;
    else if (builtInName.equalsIgnoreCase(SWRLB_ABS)) operationResult = java.lang.Math.abs(argument2);
    else if (builtInName.equalsIgnoreCase(SWRLB_CEILING)) operationResult = java.lang.Math.ceil(argument2);
    else if (builtInName.equalsIgnoreCase(SWRLB_FLOOR)) operationResult = java.lang.Math.floor(argument2);
    else if (builtInName.equalsIgnoreCase(SWRLB_ROUND)) operationResult = java.lang.Math.rint(argument2);
    else if (builtInName.equalsIgnoreCase(SWRLB_ROUND_HALF_TO_EVEN)) operationResult = java.lang.Math.rint(argument2);
    else if (builtInName.equalsIgnoreCase(SWRLB_SIN)) operationResult = java.lang.Math.sin(argument2);
    else if (builtInName.equalsIgnoreCase(SWRLB_COS)) operationResult = java.lang.Math.cos(argument2);
    else if (builtInName.equalsIgnoreCase(SWRLB_TAN)) operationResult = java.lang.Math.tan(argument2);
    else throw new InvalidBuiltInNameException(builtInName);
    
    if (hasUnbound1stArgument) { // Bind the result to the first argument.
      if (SWRLBuiltInUtil.isShortMostPreciseArgument(arguments.subList(1, arguments.size()))) 
        arguments.set(0, new LiteralInfo((short)operationResult)); 
      else if (SWRLBuiltInUtil.isIntegerMostPreciseArgument(arguments.subList(1, arguments.size()))) 
        arguments.set(0, new LiteralInfo((int)operationResult));
      else if (SWRLBuiltInUtil.isFloatMostPreciseArgument(arguments.subList(1, arguments.size()))) 
        arguments.set(0, new LiteralInfo((float)operationResult));
      else if (SWRLBuiltInUtil.isLongMostPreciseArgument(arguments.subList(1, arguments.size()))) 
        arguments.set(0, new LiteralInfo((long)operationResult));
      else arguments.set(0, new LiteralInfo((double)operationResult));
      result = true;
    } else result = (argument1 == operationResult);

    return result;
  } // mathOperation

} // SWRLBuiltInLibraryImpl
