
// TODO: only integer math operations supported - use generics to make more general. 
// TODO: roundHalfToEven math operation not implemented
// TODO: several string methods are not implemented. 
// TODO: built-ins for date, time, duration, URIs and lists are not implemented.

package edu.stanford.smi.protegex.owl.swrl.bridge.builtins.swrlb;

import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.swrl.model.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.builtins.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;

import java.util.*;
import java.lang.Math.*;

/**
 ** Implementations library for the core SWRL built-in methods. These built-ins are defined <a
 ** href="http://www.w3.org/2003/11/swrlb">here</a>, and are documented <a
 ** href="http://www.daml.org/2004/04/swrl/builtins.html">here</a>. <p>
 **
 ** See <a href="http://protege.cim3.net/cgi-bin/wiki.pl?SWRLBuiltInBridge">here</a> for documentation on defining SWRL built-in libraries.
 */
public class SWRLBuiltInLibraryImpl implements SWRLBuiltInLibrary
{
  private static String SWRLB_NAMESPACE = "swrlb";

  private static String SWRLB_GREATER_THAN = SWRLB_NAMESPACE + ":" + "greaterThan";
  private static String SWRLB_LESS_THAN = SWRLB_NAMESPACE + ":" + "lessThan";
  private static String SWRLB_EQUAL = SWRLB_NAMESPACE + ":" + "equal";
  private static String SWRLB_NOT_EQUAL = SWRLB_NAMESPACE + ":" + "notEqual";
  private static String SWRLB_LESS_THAN_OR_EQUAL = SWRLB_NAMESPACE + ":" + "lessThanOrEqual";
  private static String SWRLB_GREATER_THAN_OR_EQUAL = SWRLB_NAMESPACE + ":" + "greaterThanOrEqual";

  // TODO: The following mathematical operations are only implemented for integers

  private static String SWRLB_ADD = SWRLB_NAMESPACE + ":" + "add";
  private static String SWRLB_SUBTRACT = SWRLB_NAMESPACE + ":" + "subtract";
  private static String SWRLB_MULTIPLY = SWRLB_NAMESPACE + ":" + "multiply";
  private static String SWRLB_DIVIDE = SWRLB_NAMESPACE + ":" + "divide";
  private static String SWRLB_INTEGER_DIVIDE = SWRLB_NAMESPACE + ":" + "integerDivide";
  private static String SWRLB_MOD = SWRLB_NAMESPACE + ":" + "mod";
  private static String SWRLB_POW = SWRLB_NAMESPACE + ":" + "pow";
  private static String SWRLB_UNARY_PLUS = SWRLB_NAMESPACE + ":" + "unaryPlus";
  private static String SWRLB_UNARY_MINUS = SWRLB_NAMESPACE + ":" + "unaryMinus";
  private static String SWRLB_ABS = SWRLB_NAMESPACE + ":" + "abs";
  private static String SWRLB_CEILING = SWRLB_NAMESPACE + ":" + "ceiling";
  private static String SWRLB_FLOOR = SWRLB_NAMESPACE + ":" + "floor";
  private static String SWRLB_ROUND = SWRLB_NAMESPACE + ":" + "round";
  private static String SWRLB_ROUND_HALF_TO_EVEN = SWRLB_NAMESPACE + ":" + "roundHalfToEven"; // TODO: not implemented
  private static String SWRLB_SIN = SWRLB_NAMESPACE + ":" + "sin";
  private static String SWRLB_COS = SWRLB_NAMESPACE + ":" + "cos";
  private static String SWRLB_TAN = SWRLB_NAMESPACE + ":" + "tan";

  private static String SWRLB_BOOLEAN_NOT = SWRLB_NAMESPACE + ":" + "booleanNot";

  private static String SWRLB_STRING_EQUAL_IGNORECASE = SWRLB_NAMESPACE + ":" + "stringEqualIgnoreCase";
  private static String SWRLB_STRING_CONCAT = SWRLB_NAMESPACE + ":" + "stringConcat";
  private static String SWRLB_SUBSTRING = SWRLB_NAMESPACE + ":" + "substring";
  private static String SWRLB_STRING_LENGTH = SWRLB_NAMESPACE + ":" + "stringLength";
  private static String SWRLB_NORMALIZE_SPACE = SWRLB_NAMESPACE + ":" + "normalizeSpace"; // TODO: not implemented
  private static String SWRLB_UPPER_CASE = SWRLB_NAMESPACE + ":" + "upperCase";
  private static String SWRLB_LOWER_CASE = SWRLB_NAMESPACE + ":" + "lowerCase";
  private static String SWRLB_TRANSLATE = SWRLB_NAMESPACE + ":" + "translate"; // TODO: not implemented
  private static String SWRLB_CONTAINS = SWRLB_NAMESPACE + ":" + "contains";
  private static String SWRLB_CONTAINS_IGNORE_CASE = SWRLB_NAMESPACE + ":" + "containsIgnoreCase";
  private static String SWRLB_STARTS_WITH = SWRLB_NAMESPACE + ":" + "startsWith";
  private static String SWRLB_ENDS_WITH = SWRLB_NAMESPACE + ":" + "endsWith";
  private static String SWRLB_SUBSTRING_BEFORE = SWRLB_NAMESPACE + ":" + "substringBefore"; // TODO: not implemented
  private static String SWRLB_SUBSTRING_AFTER = SWRLB_NAMESPACE + ":" + "substringAfter"; 
  private static String SWRLB_MATCHES = SWRLB_NAMESPACE + ":" + "matches"; // TODO: not implemented
  private static String SWRLB_REPLACE = SWRLB_NAMESPACE + ":" + "replace"; // TODO: not implemented
  private static String SWRLB_TOKENIZE = SWRLB_NAMESPACE + ":" + "tokenize"; // TODO: not implemented

  private SWRLRuleEngineBridge bridge;

  public void initialize(SWRLRuleEngineBridge bridge) { this.bridge = bridge; }
  
  // Built-ins for comparison, defined in Section 8.1. of http://www.daml.org/2004/04/swrl/builtins.html.

  public boolean greaterThan(List<Argument> arguments) throws BuiltInException
  {
    return (compareTwoArgumentsOfOrderedType(SWRLB_GREATER_THAN, arguments) > 0);
  } // greaterThan

  public boolean lessThan(List<Argument> arguments) throws BuiltInException
  {
    return (compareTwoArgumentsOfOrderedType(SWRLB_LESS_THAN, arguments) < 0);
  } // greaterThan

  public boolean equal(List<Argument> arguments) throws BuiltInException
  {
    return equal(SWRLB_EQUAL, arguments);
  } // equal

  public boolean notEqual(List<Argument> arguments) throws BuiltInException
  {
    return !equal(SWRLB_NOT_EQUAL, arguments);
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
    SWRLBuiltInUtil.checkNumberOfArgumentsAtLeast(SWRLB_ADD, 2, arguments.size());

    return mathOperation(SWRLB_ADD, arguments); 
  } // add

  public boolean subtract(List<Argument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(SWRLB_SUBTRACT, 3, arguments.size());

    return mathOperation(SWRLB_SUBTRACT, arguments);
  } // subtract

  public boolean multiply(List<Argument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsAtLeast(SWRLB_MULTIPLY, 2, arguments.size());

    return mathOperation(SWRLB_MULTIPLY, arguments);
  } // multiply

  public boolean divide(List<Argument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(SWRLB_DIVIDE, 3, arguments.size());

    return mathOperation(SWRLB_DIVIDE, arguments);
  } // divide

  public boolean integerDivide(List<Argument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(SWRLB_INTEGER_DIVIDE, 3, arguments.size());

    return mathOperation(SWRLB_INTEGER_DIVIDE, arguments);
  } // integerDivide

  public boolean mod(List<Argument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(SWRLB_MOD, 3, arguments.size());

    return mathOperation(SWRLB_MOD, arguments);
  } // mod

  public boolean pow(List<Argument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(SWRLB_POW, 3, arguments.size());

    return mathOperation(SWRLB_POW, arguments);
  } // pow

  public boolean unaryPlus(List<Argument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(SWRLB_UNARY_PLUS, 2, arguments.size());

    return mathOperation(SWRLB_UNARY_PLUS, arguments);
  } // unaryPlus

  public boolean unaryMinus(List<Argument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(SWRLB_UNARY_MINUS, 2, arguments.size());

    return mathOperation(SWRLB_UNARY_MINUS, arguments);
  } // unaryMinus

  public boolean abs(List<Argument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(SWRLB_ABS, 2, arguments.size());

    return mathOperation(SWRLB_ABS, arguments);
  } // abs

  public boolean ceiling(List<Argument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(SWRLB_CEILING, 2, arguments.size());

    return mathOperation(SWRLB_CEILING, arguments);
  } // ceiling

  public boolean floor(List<Argument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(SWRLB_FLOOR, 2, arguments.size());

    return mathOperation(SWRLB_FLOOR, arguments);
  } // floor

  public boolean round(List<Argument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(SWRLB_ROUND, 2, arguments.size());

    return mathOperation(SWRLB_ROUND, arguments);
  } // round

  // TODO: roundHalfToEven not implemented

  public boolean sin(List<Argument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(SWRLB_SIN, 2, arguments.size());

    return mathOperation(SWRLB_SIN, arguments);
  } // sin

  public boolean cos(List<Argument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(SWRLB_COS, 2, arguments.size());

    return mathOperation(SWRLB_COS, arguments);
  } // cos

  public boolean tan(List<Argument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(SWRLB_TAN, 2, arguments.size());

    return mathOperation(SWRLB_TAN, arguments);
  } // tan

  // Built-ins for Booleans. cf. Section 8.3 of http://www.daml.org/2004/04/swrl/builtins.html

  public boolean booleanNot(List<Argument> arguments) throws BuiltInException
  {
    boolean result;

    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(SWRLB_BOOLEAN_NOT, 2, arguments.size());
    SWRLBuiltInUtil.checkForUnboundNonFirstArguments(SWRLB_BOOLEAN_NOT, arguments);

    if (SWRLBuiltInUtil.isUnboundArgument(SWRLB_BOOLEAN_NOT, 0, arguments)) {
      if (!SWRLBuiltInUtil.areAllArgumentsBooleans(SWRLB_BOOLEAN_NOT, arguments.subList(1, arguments.size())))
        throw new InvalidBuiltInArgumentException(SWRLB_BOOLEAN_NOT, 1, "Expecting a Boolean");

      boolean operationResult = !SWRLBuiltInUtil.getArgumentAsABoolean(SWRLB_BOOLEAN_NOT, 1, arguments);
      arguments.set(0, new LiteralInfo(operationResult)); // Bind the result to the first parameter
      result = true;
    } else {
      if (!SWRLBuiltInUtil.areAllArgumentsBooleans(SWRLB_BOOLEAN_NOT, arguments))
        throw new InvalidBuiltInArgumentException(SWRLB_BOOLEAN_NOT, "Expecting all Boolean arguments");

      result = !equal(SWRLB_BOOLEAN_NOT, arguments);
    } // if
    return result;
  } // booleanNot

  // Built-ins for Strings. cf. Section 8.4 of http://www.daml.org/2004/04/swrl/builtins.html
  
  public boolean stringEqualIgnoreCase(List<Argument> arguments) throws BuiltInException
  {
    String argument1, argument2;

    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(SWRLB_STRING_EQUAL_IGNORECASE, 2, arguments.size());
    SWRLBuiltInUtil.checkForUnboundArguments(SWRLB_STRING_EQUAL_IGNORECASE, arguments);

    argument1 = SWRLBuiltInUtil.getArgumentAsAString(SWRLB_STRING_EQUAL_IGNORECASE, 0, arguments);
    argument2 = SWRLBuiltInUtil.getArgumentAsAString(SWRLB_STRING_EQUAL_IGNORECASE, 1, arguments);

    return argument1.equalsIgnoreCase(argument2);
  } // stringEqualIgnoreCase

  public boolean stringConcat(List<Argument> arguments) throws BuiltInException
  {
    String operationResult = "";
    boolean result;

    SWRLBuiltInUtil.checkNumberOfArgumentsAtLeast(SWRLB_STRING_CONCAT, 2, arguments.size());
    SWRLBuiltInUtil.checkForUnboundNonFirstArguments(SWRLB_STRING_CONCAT, arguments);

    for (int argumentNumber = 1; argumentNumber < arguments.size(); argumentNumber++) { // Exception thrown if argument is not a string.
      operationResult = operationResult.concat(SWRLBuiltInUtil.getArgumentAsAString(SWRLB_STRING_CONCAT, argumentNumber, arguments));
    } // for

    if (SWRLBuiltInUtil.isUnboundArgument(SWRLB_STRING_CONCAT, 0, arguments)) {
      arguments.set(0, new LiteralInfo(operationResult)); // Bind the result to the first parameter
      result = true;
    } else {
      String argument1 = SWRLBuiltInUtil.getArgumentAsAString(SWRLB_STRING_CONCAT, 0, arguments);
      result =  argument1.equals(operationResult);
    } //if

    return result;
  } // stringConcat

  public boolean substring(List<Argument> arguments) throws BuiltInException
  {
    String argument2, operationResult;
    int startIndex, length;
    boolean result;

    SWRLBuiltInUtil.checkNumberOfArgumentsAtLeast(SWRLB_SUBSTRING, 3, arguments.size());
    SWRLBuiltInUtil.checkNumberOfArgumentsAtMost(SWRLB_SUBSTRING, 4, arguments.size());
    SWRLBuiltInUtil.checkForUnboundNonFirstArguments(SWRLB_SUBSTRING, arguments);

    argument2 = SWRLBuiltInUtil.getArgumentAsAString(SWRLB_SUBSTRING, 1, arguments);
    startIndex = SWRLBuiltInUtil.getArgumentAsAnInteger(SWRLB_SUBSTRING, 2, arguments);

    if (arguments.size() == 4) {
      length = SWRLBuiltInUtil.getArgumentAsAnInteger(SWRLB_SUBSTRING, 3, arguments);
      operationResult = argument2.substring(startIndex, length);
    } else operationResult = argument2.substring(startIndex);

    if (SWRLBuiltInUtil.hasUnboundArguments(SWRLB_SUBSTRING, arguments)) {
      arguments.set(0, new LiteralInfo(operationResult)); // Bind the result to the first parameter
      result = true;
    } else {
      String argument1 = SWRLBuiltInUtil.getArgumentAsAString(SWRLB_SUBSTRING, 0, arguments);
      result =  argument1.equals(operationResult);
    } //if
    return result;
  } // substring

  public boolean stringLength(List<Argument> arguments) throws BuiltInException
  {
    String argument2;
    boolean result;
    int operationResult;

    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(SWRLB_STRING_LENGTH, 2, arguments.size());
    SWRLBuiltInUtil.checkForUnboundNonFirstArguments(SWRLB_STRING_LENGTH, arguments);

    argument2 = SWRLBuiltInUtil.getArgumentAsAString(SWRLB_SUBSTRING, 1, arguments);
    operationResult = argument2.length();

    if (SWRLBuiltInUtil.hasUnboundArguments(SWRLB_STRING_LENGTH, arguments)) {
      arguments.set(0, new LiteralInfo(operationResult)); // Bind the result to the first parameter
      result = true;
    } else {
      String argument1 = SWRLBuiltInUtil.getArgumentAsAString(SWRLB_SUBSTRING, 0, arguments);
      result = argument1.length() == operationResult;
    } //if
    return result;
  } // stringLength

  public boolean upperCase(List<Argument> arguments) throws BuiltInException
  {
    String argument2, operationResult;
    boolean result;

    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(SWRLB_UPPER_CASE, 2, arguments.size());
    SWRLBuiltInUtil.checkForUnboundNonFirstArguments(SWRLB_UPPER_CASE, arguments);

    argument2 = SWRLBuiltInUtil.getArgumentAsAString(SWRLB_UPPER_CASE, 1, arguments);
    operationResult = argument2.toUpperCase();

    if (SWRLBuiltInUtil.hasUnboundArguments(SWRLB_UPPER_CASE, arguments)) {
      arguments.set(0, new LiteralInfo(operationResult)); // Bind the result to the first parameter
      result = true;
    } else {
      String argument1 = SWRLBuiltInUtil.getArgumentAsAString(SWRLB_UPPER_CASE, 0, arguments);
      result = argument1.equals(operationResult);
    } //if
    return result;
  } // upperCase

  public boolean lowerCase(List<Argument> arguments) throws BuiltInException
  {
    String argument2, operationResult;
    boolean result;

    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(SWRLB_LOWER_CASE, 2, arguments.size());
    SWRLBuiltInUtil.checkForUnboundNonFirstArguments(SWRLB_LOWER_CASE, arguments);

    argument2 = SWRLBuiltInUtil.getArgumentAsAString(SWRLB_LOWER_CASE, 1, arguments);

    operationResult = argument2.toLowerCase();

    if (SWRLBuiltInUtil.hasUnboundArguments(SWRLB_LOWER_CASE, arguments)) {
      arguments.set(0, new LiteralInfo(operationResult)); // Bind the result to the first parameter
      result = true;
    } else {
      String argument1 = SWRLBuiltInUtil.getArgumentAsAString(SWRLB_LOWER_CASE, 0, arguments);
      result = argument1.equals(operationResult);
    } //if
    return result;
  } // lowerCase

  public boolean contains(List<Argument> arguments) throws BuiltInException
  {
    String argument1, argument2;

    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(SWRLB_CONTAINS, 2, arguments.size());
    SWRLBuiltInUtil.checkForUnboundArguments(SWRLB_CONTAINS, arguments);

    argument1 = SWRLBuiltInUtil.getArgumentAsAString(SWRLB_CONTAINS, 0, arguments);
    argument2 = SWRLBuiltInUtil.getArgumentAsAString(SWRLB_CONTAINS, 1, arguments);

    return argument1.lastIndexOf(argument2) != -1;
  } // contains

  public boolean containsIgnoreCase(List<Argument> arguments) throws BuiltInException
  {
    String argument1, argument2;

    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(SWRLB_CONTAINS_IGNORE_CASE, 2, arguments.size());
    SWRLBuiltInUtil.checkForUnboundArguments(SWRLB_CONTAINS_IGNORE_CASE, arguments);

    if (SWRLBuiltInUtil.hasUnboundArguments(SWRLB_CONTAINS_IGNORE_CASE, arguments)) 
      throw new InvalidBuiltInArgumentException(SWRLB_CONTAINS_IGNORE_CASE, 0, "Built-in does not support binding. Unbound variable used");

    argument1 = SWRLBuiltInUtil.getArgumentAsAString(SWRLB_CONTAINS_IGNORE_CASE, 0, arguments);
    argument2 = SWRLBuiltInUtil.getArgumentAsAString(SWRLB_CONTAINS_IGNORE_CASE, 1, arguments);

    return argument1.toLowerCase().lastIndexOf(argument2.toLowerCase()) != -1;
  } // containsIgnoreCase

  public boolean startsWith(List<Argument> arguments) throws BuiltInException
  {
    String argument1, argument2;

    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(SWRLB_STARTS_WITH, 2, arguments.size());
    SWRLBuiltInUtil.checkForUnboundArguments(SWRLB_STARTS_WITH, arguments);

    argument1 = SWRLBuiltInUtil.getArgumentAsAString(SWRLB_STARTS_WITH, 0, arguments);
    argument2 = SWRLBuiltInUtil.getArgumentAsAString(SWRLB_STARTS_WITH, 1, arguments);

    return argument1.startsWith(argument2);
  } // startsWith

  public boolean endsWith(List<Argument> arguments) throws BuiltInException
  {
    String argument1, argument2;

    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(SWRLB_ENDS_WITH, 2, arguments.size());
    SWRLBuiltInUtil.checkForUnboundArguments(SWRLB_ENDS_WITH, arguments);

    argument1 = SWRLBuiltInUtil.getArgumentAsAString(SWRLB_ENDS_WITH, 0, arguments);
    argument2 = SWRLBuiltInUtil.getArgumentAsAString(SWRLB_ENDS_WITH, 1, arguments);

    return argument1.endsWith(argument2);
  } // endsWith

  // Private methods.

  private boolean equal(String builtInName, List<Argument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(builtInName, 2, arguments.size());

    if (SWRLBuiltInUtil.hasUnboundArguments(builtInName, arguments)) 
      throw new InvalidBuiltInArgumentException(builtInName, 0, "Comparison built-ins do not support binding. Unbound variable used");

    if (SWRLBuiltInUtil.isArgumentABoolean(builtInName, 0, arguments)) {
      boolean b1 = SWRLBuiltInUtil.getArgumentAsABoolean(builtInName, 0, arguments);
      boolean b2 = SWRLBuiltInUtil.getArgumentAsABoolean(builtInName, 1, arguments); // Performs type checking.
      
      return b1 == b2;
    } else return compareTwoArgumentsOfOrderedType(builtInName, arguments) == 0;
  } // equal

  private static int compareTwoArgumentsOfOrderedType(String builtInName, List<Argument> arguments) throws BuiltInException
  {
    int result = 0; // Should be assigned by end of method.
    
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(builtInName, 2, arguments.size());
    SWRLBuiltInUtil.checkForUnboundArguments(builtInName, arguments);
    SWRLBuiltInUtil.checkThatAllArgumentsAreOfAnOrderedType(builtInName, arguments);

    if (SWRLBuiltInUtil.isArgumentAString(builtInName, 0, arguments)) {   
      String s1 = SWRLBuiltInUtil.getArgumentAsAString(builtInName, 0, arguments);
      String s2 = SWRLBuiltInUtil.getArgumentAsAString(builtInName, 1, arguments); // Performs type checking.

      return s1.compareTo(s2);
    } else if (SWRLBuiltInUtil.isArgumentAnInteger(builtInName, 0, arguments)) {
      int i1 = SWRLBuiltInUtil.getArgumentAsAnInteger(builtInName, 0, arguments);
      int i2 = SWRLBuiltInUtil.getArgumentAsAnInteger(builtInName, 1, arguments); // Performs type checking.

      if (i1 < i2) result = -1; else if (i1 > i2) result = 1; else result = 0;
    } else if (SWRLBuiltInUtil.isArgumentALong(builtInName, 0, arguments)) {
      long l1 = SWRLBuiltInUtil.getArgumentAsALong(builtInName, 0, arguments);
      long l2 = SWRLBuiltInUtil.getArgumentAsALong(builtInName, 1, arguments); // Performs type checking.

      if (l1 < l2) result = -1; else if (l1 > l2) result = 1; else result = 0;
    } else if (SWRLBuiltInUtil.isArgumentAFloat(builtInName, 0, arguments)) {
      float f1 = SWRLBuiltInUtil.getArgumentAsAFloat(builtInName, 0, arguments);
      float f2 = SWRLBuiltInUtil.getArgumentAsAFloat(builtInName, 1, arguments); // Performs type checking.

      if (f1 < f2) result = -1; else if (f1 > f2) result = 1; else result = 0;
    } else if (SWRLBuiltInUtil.isArgumentADouble(builtInName, 0, arguments)) {
      double d1 = SWRLBuiltInUtil.getArgumentAsADouble(builtInName, 0, arguments);
      double d2 = SWRLBuiltInUtil.getArgumentAsADouble(builtInName, 1, arguments); // Performs type checking.

      if (d1 < d2) result = -1; else if (d1 > d2) result =  1; else result = 0;
    } else throw new InvalidBuiltInArgumentException(builtInName, 1, "Unknown argument type");

    return result;
  } // greaterThan

  private boolean lessThan(String builtInName, List<Argument> arguments) throws BuiltInException
  {
    return (compareTwoArgumentsOfOrderedType(builtInName, arguments) < 0);
  } // lessThan

  // TODO: This method only supports integers at the moment. Need to rewrite this using generics so it will support all types.
  private boolean mathOperation(String builtInName, List<Argument> arguments) throws BuiltInException
  {
    int argumentNumber, argument1 = 0, argument2, argument3;
    int operationResult = -1; 
    boolean result = false, hasUnbound1stArgument = false;

    SWRLBuiltInUtil.checkForUnboundNonFirstArguments(builtInName, arguments); // Only supports binding of first argument at the moment.

    if (SWRLBuiltInUtil.isUnboundArgument(builtInName, 0, arguments)) hasUnbound1stArgument = true;

    if (hasUnbound1stArgument) {
      if (!SWRLBuiltInUtil.areAllArgumentsIntegers(builtInName, arguments.subList(1, arguments.size())))
        throw new BuiltInNotImplementedException(builtInName, "Only built-ins with all integer arguments are supported at the moment");
    } else {
      if (!SWRLBuiltInUtil.areAllArgumentsIntegers(builtInName, arguments)) 
        throw new BuiltInNotImplementedException(builtInName, "Only built-ins with all integer arguments are supported at the moment");
    } // if

    // Argument number checking will have been performed by invoking method.
    if (!hasUnbound1stArgument) argument1 = SWRLBuiltInUtil.getArgumentAsAnInteger(builtInName, 0, arguments);

    if (builtInName.equalsIgnoreCase(SWRLB_ADD)) {
      operationResult = 0;
      for (argumentNumber = 1; argumentNumber < arguments.size(); argumentNumber++) {
        operationResult += SWRLBuiltInUtil.getArgumentAsAnInteger(builtInName, argumentNumber, arguments);
      } // for
    } else if (builtInName.equalsIgnoreCase(SWRLB_MULTIPLY)) {
      operationResult = 1;
      for (argumentNumber = 1; argumentNumber < arguments.size(); argumentNumber++) {
        operationResult *= SWRLBuiltInUtil.getArgumentAsAnInteger(builtInName, argumentNumber, arguments);
      } // for
    } else if (builtInName.equalsIgnoreCase(SWRLB_SUBTRACT)) {
      argument2 = SWRLBuiltInUtil.getArgumentAsAnInteger(builtInName, 1, arguments);
      argument3 = SWRLBuiltInUtil.getArgumentAsAnInteger(builtInName, 2, arguments);
      operationResult = argument2 - argument3;
    } else if (builtInName.equalsIgnoreCase(SWRLB_DIVIDE)) {
      argument2 = SWRLBuiltInUtil.getArgumentAsAnInteger(builtInName, 1, arguments);
      argument3 = SWRLBuiltInUtil.getArgumentAsAnInteger(builtInName, 2, arguments);
      operationResult = (argument2 / argument3);
    } else if (builtInName.equalsIgnoreCase(SWRLB_INTEGER_DIVIDE)) {
      argument2 = SWRLBuiltInUtil.getArgumentAsAnInteger(builtInName, 1, arguments);
      argument3 = SWRLBuiltInUtil.getArgumentAsAnInteger(builtInName, 2, arguments);
      if (argument3 == 0) throw new InvalidBuiltInArgumentException(builtInName, 2, "Zero passed as divisor");
      if (argument3 >= 0) operationResult = argument2 + argument3 + 1 / argument3;
      else operationResult = argument2 / argument3;
    } else if (builtInName.equalsIgnoreCase(SWRLB_MOD)) {
      argument2 = SWRLBuiltInUtil.getArgumentAsAnInteger(builtInName, 1, arguments);
      argument3 = SWRLBuiltInUtil.getArgumentAsAnInteger(builtInName, 2, arguments);
      operationResult = argument2 % argument3;
    } else if (builtInName.equalsIgnoreCase(SWRLB_POW)) {
      argument2 = SWRLBuiltInUtil.getArgumentAsAnInteger(builtInName, 1, arguments);
      argument3 = SWRLBuiltInUtil.getArgumentAsAnInteger(builtInName, 2, arguments);
      operationResult = (int)java.lang.Math.pow(argument2, argument3);
    } else if (builtInName.equalsIgnoreCase(SWRLB_UNARY_PLUS)) {
      argument2 = SWRLBuiltInUtil.getArgumentAsAnInteger(builtInName, 1, arguments);
      operationResult = argument2;
    } else if (builtInName.equalsIgnoreCase(SWRLB_UNARY_MINUS)) {
      argument2 = SWRLBuiltInUtil.getArgumentAsAnInteger(builtInName, 1, arguments);
      operationResult = -argument2;
    } else if (builtInName.equalsIgnoreCase(SWRLB_ABS)) {
      argument2 = SWRLBuiltInUtil.getArgumentAsAnInteger(builtInName, 1, arguments);
      operationResult = java.lang.Math.abs(argument2);
    } else if (builtInName.equalsIgnoreCase(SWRLB_CEILING)) {
      argument2 = SWRLBuiltInUtil.getArgumentAsAnInteger(builtInName, 1, arguments);
      operationResult = (int)java.lang.Math.ceil(argument2);
    } else if (builtInName.equalsIgnoreCase(SWRLB_FLOOR)) {
      argument2 = SWRLBuiltInUtil.getArgumentAsAnInteger(builtInName, 1, arguments);
      operationResult = (int)java.lang.Math.floor(argument2);
    } else if (builtInName.equalsIgnoreCase(SWRLB_ROUND)) {
      argument2 = SWRLBuiltInUtil.getArgumentAsAnInteger(builtInName, 1, arguments);
      operationResult = (int)java.lang.Math.rint(argument2);
    } else if (builtInName.equalsIgnoreCase(SWRLB_ROUND_HALF_TO_EVEN)) {
      argument2 = SWRLBuiltInUtil.getArgumentAsAnInteger(builtInName, 1, arguments);
      throw new BuiltInNotImplementedException(SWRLB_ROUND_HALF_TO_EVEN);
    } else if (builtInName.equalsIgnoreCase(SWRLB_SIN)) {
      argument2 = SWRLBuiltInUtil.getArgumentAsAnInteger(builtInName, 1, arguments);
      operationResult = (int)java.lang.Math.sin(argument2);
    } else if (builtInName.equalsIgnoreCase(SWRLB_COS)) {
      argument2 = SWRLBuiltInUtil.getArgumentAsAnInteger(builtInName, 1, arguments);
      operationResult = (int)java.lang.Math.cos(argument2);
    } else if (builtInName.equalsIgnoreCase(SWRLB_TAN)) {
      argument2 = SWRLBuiltInUtil.getArgumentAsAnInteger(builtInName, 1, arguments);
      operationResult = (int)java.lang.Math.tan(argument2);
    } else throw new InvalidBuiltInNameException(builtInName);
    
    if (hasUnbound1stArgument) {
      arguments.set(0, new LiteralInfo(operationResult)); // Bind the result to the first argument.
      result = true;
    } else result = (argument1 == operationResult);

    return result;
  } // mathOperation

} // SWRLBuiltInLibraryImpl
