
// Implementations for SWRL built-in methods defined in for the built-ins listed in http://www.w3.org/2003/11/swrlb. These built-ins are
// documented here: http://www.daml.org/2004/04/swrl/builtins.html.
//
// TODO: Only integer math operations supported at the moment - use generics to make more general. Several string methods are not
// implemented. Built-ins for date, time, duration, URIs and lists not implemented.

package edu.stanford.smi.protegex.owl.swrl.bridge.builtins.swrlb;

import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.swrl.model.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.builtins.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;

import java.util.*;
import java.lang.Math.*;

public class SWRLBuiltInMethodsImpl implements SWRLBuiltInMethods
{
  private static String SWRLB_GREATER_THAN = "greaterThan";
  private static String SWRLB_LESS_THAN = "lessThan";
  private static String SWRLB_EQUAL = "equal";
  private static String SWRLB_NOT_EQUAL = "notEqual";
  private static String SWRLB_LESS_THAN_OR_EQUAL = "lessThanOrEqual";

  // TODO: The following math operations are implemented for integers only.

  private static String SWRLB_ADD = "add";
  private static String SWRLB_SUBTRACT = "subtract";
  private static String SWRLB_MULTIPLY = "multiply";
  private static String SWRLB_DIVIDE = "divide";
  private static String SWRLB_INTEGER_DIVIDE = "integerDivide";
  private static String SWRLB_MOD = "mod";
  private static String SWRLB_POW = "pow";
  private static String SWRLB_UNARY_PLUS = "unaryPlus";
  private static String SWRLB_UNARY_MINUS = "unaryMinus";
  private static String SWRLB_ABS = "abs";
  private static String SWRLB_CEILING = "ceiling";
  private static String SWRLB_FLOOR = "floor";
  private static String SWRLB_ROUND = "round";
  private static String SWRLB_ROUND_HALF_TO_EVEN = "roundHalfToEven"; // TODO: not implemented
  private static String SWRLB_SIN = "sin";
  private static String SWRLB_COS = "cos";
  private static String SWRLB_TAN = "tan";

  private static String SWRLB_BOOLEAN_NOT = "booleanNot";

  private static String SWRLB_STRING_EQUAL_IGNORECASE = "stringEqualIgnoreCase";
  private static String SWRLB_STRING_CONCAT = "stringConcat";
  private static String SWRLB_SUBSTRING = "substring";
  private static String SWRLB_STRING_LENGTH = "stringLength";
  private static String SWRLB_NORMALIZE_SPACE = "normalizeSpace"; // TODO: not implemented
  private static String SWRLB_UPPER_CASE = "upperCase";
  private static String SWRLB_LOWER_CASE = "lowerCase";
  private static String SWRLB_TRANSLATE = "translate"; // TODO: not implemented
  private static String SWRLB_CONTAINS = "contains";
  private static String SWRLB_CONTAINS_IGNORE_CASE = "containsIgnoreCase";
  private static String SWRLB_STARTS_WITH = "startsWith";
  private static String SWRLB_ENDS_WITH = "endsWith";
  private static String SWRLB_SUBSTRING_BEFORE = "substringBefore"; // TODO: not implemented
  private static String SWRLB_SUBSTRING_AFTER = "substringAfter"; 
  private static String SWRLB_MATCHES = "matches"; // TODO: not implemented
  private static String SWRLB_REPLACE = "replace"; // TODO: not implemented
  private static String SWRLB_TOKENIZE = "tokenize"; // TODO: not implemented
  
  // Built-ins for comparison, defined in Section 8.1. of http://www.daml.org/2004/04/swrl/builtins.html.

  public boolean greaterThan(List arguments) throws BuiltInException
  {
    return (compareTwoArgumentsOfOrderedType(SWRLB_GREATER_THAN, arguments) > 0);
  } // greaterThan

  public boolean lessThan(List arguments) throws BuiltInException
  {
    return (compareTwoArgumentsOfOrderedType(SWRLB_LESS_THAN, arguments) < 0);
  } // greaterThan

  public boolean equal(List arguments) throws BuiltInException
  {
    return equal(SWRLB_EQUAL, arguments);
  } // equal

  public boolean notEqual(List arguments) throws BuiltInException
  {
    return !equal(SWRLB_NOT_EQUAL, arguments);
  } // notEqual

  public boolean lessThanOrEqual(List arguments) throws BuiltInException
  {
    return equal(SWRLB_LESS_THAN_OR_EQUAL, arguments) || lessThan(SWRLB_LESS_THAN_OR_EQUAL, arguments);
  } // lessThanOrEqual

  // Math Built-ins, defined in Section 8.2. of http://www.daml.org/2004/04/swrl/builtins.html.
  
  public boolean add(List arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsAtLeast(SWRLB_ADD, 2, arguments.size());

    return mathOperation(SWRLB_ADD, arguments); 
  } // add

  public boolean subtract(List arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(SWRLB_SUBTRACT, 3, arguments.size());

    return mathOperation(SWRLB_SUBTRACT, arguments);
  } // subtract

  public boolean multiply(List arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsAtLeast(SWRLB_MULTIPLY, 2, arguments.size());

    return mathOperation(SWRLB_MULTIPLY, arguments);
  } // multiply

  public boolean divide(List arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(SWRLB_DIVIDE, 3, arguments.size());

    return mathOperation(SWRLB_DIVIDE, arguments);
  } // divide

  public boolean integerDivide(List arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(SWRLB_INTEGER_DIVIDE, 3, arguments.size());

    return mathOperation(SWRLB_INTEGER_DIVIDE, arguments);
  } // integerDivide

  public boolean mod(List arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(SWRLB_MOD, 3, arguments.size());

    return mathOperation(SWRLB_MOD, arguments);
  } // mod

  public boolean pow(List arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(SWRLB_POW, 3, arguments.size());

    return mathOperation(SWRLB_POW, arguments);
  } // pow

  public boolean unaryPlus(List arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(SWRLB_UNARY_PLUS, 2, arguments.size());

    return mathOperation(SWRLB_UNARY_PLUS, arguments);
  } // unaryPlus

  public boolean unaryMinus(List arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(SWRLB_UNARY_MINUS, 2, arguments.size());

    return mathOperation(SWRLB_UNARY_MINUS, arguments);
  } // unaryMinus

  public boolean abs(List arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(SWRLB_ABS, 2, arguments.size());

    return mathOperation(SWRLB_ABS, arguments);
  } // abs

  public boolean ceiling(List arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(SWRLB_CEILING, 2, arguments.size());

    return mathOperation(SWRLB_CEILING, arguments);
  } // ceiling

  public boolean floor(List arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(SWRLB_FLOOR, 2, arguments.size());

    return mathOperation(SWRLB_FLOOR, arguments);
  } // floor

  public boolean round(List arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(SWRLB_ROUND, 2, arguments.size());

    return mathOperation(SWRLB_ROUND, arguments);
  } // round

  // TODO: roundHalfToEven not implemented

  public boolean sin(List arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(SWRLB_SIN, 2, arguments.size());

    return mathOperation(SWRLB_SIN, arguments);
  } // sin

  public boolean cos(List arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(SWRLB_COS, 2, arguments.size());

    return mathOperation(SWRLB_COS, arguments);
  } // cos

  public boolean tan(List arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(SWRLB_TAN, 2, arguments.size());

    return mathOperation(SWRLB_TAN, arguments);
  } // tan

  // Built-ins for Booleans. cf. Section 8.3 of http://www.daml.org/2004/04/swrl/builtins.html

  public boolean booleanNot(List arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(SWRLB_BOOLEAN_NOT, 2, arguments.size());

    return !equal(SWRLB_BOOLEAN_NOT, arguments);
  } // booleanNot

  // Built-ins for Strings. cf. Section 8.4 of http://www.daml.org/2004/04/swrl/builtins.html
  
  public boolean stringEqualIgnoreCase(List arguments) throws BuiltInException
  {
    String argument1, argument2;
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(SWRLB_STRING_EQUAL_IGNORECASE, 2, arguments.size());

    argument1 = SWRLBuiltInUtil.getArgumentAsAString(SWRLB_STRING_EQUAL_IGNORECASE, 0, arguments);
    argument2 = SWRLBuiltInUtil.getArgumentAsAString(SWRLB_STRING_EQUAL_IGNORECASE, 1, arguments);

    return argument1.equalsIgnoreCase(argument2);
  } // stringEqualIgnoreCase

  public boolean stringConcat(List arguments) throws BuiltInException
  {
    String argument1, concatenated;
    SWRLBuiltInUtil.checkNumberOfArgumentsAtLeast(SWRLB_STRING_CONCAT, 2, arguments.size());

    argument1 = SWRLBuiltInUtil.getArgumentAsAString(SWRLB_STRING_CONCAT, 0, arguments);

    concatenated = "";
    for (int argumentNumber = 1; argumentNumber <= arguments.size(); argumentNumber++) {
      concatenated = concatenated.concat(SWRLBuiltInUtil.getArgumentAsAString(SWRLB_STRING_CONCAT, argumentNumber, arguments));
    } // for

    return argument1.equals(concatenated);
  } // stringConcat

  public boolean substring(List arguments) throws BuiltInException
  {
    String argument1, argument2;
    int startIndex, length;

    SWRLBuiltInUtil.checkNumberOfArgumentsAtLeast(SWRLB_SUBSTRING, 3, arguments.size());
    SWRLBuiltInUtil.checkNumberOfArgumentsAtMost(SWRLB_SUBSTRING, 4, arguments.size());

    argument1 = SWRLBuiltInUtil.getArgumentAsAString(SWRLB_SUBSTRING, 0, arguments);
    argument2 = SWRLBuiltInUtil.getArgumentAsAString(SWRLB_SUBSTRING, 1, arguments);
    startIndex = SWRLBuiltInUtil.getArgumentAsAnInteger(SWRLB_SUBSTRING, 2, arguments);

    if (arguments.size() == 4) {
      length = SWRLBuiltInUtil.getArgumentAsAnInteger(SWRLB_SUBSTRING, 3, arguments);
      return argument1.equals(argument2.substring(startIndex, length));
    } else return argument1.equals(argument2.substring(startIndex));
  } // substring

  public boolean stringLength(List arguments) throws BuiltInException
  {
    String argument1;
    int length;

    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(SWRLB_STRING_LENGTH, 2, arguments.size());

    argument1 = SWRLBuiltInUtil.getArgumentAsAString(SWRLB_STRING_LENGTH, 0, arguments);
    length = SWRLBuiltInUtil.getArgumentAsAnInteger(SWRLB_STRING_LENGTH, 1, arguments);

    return argument1.length() == length;
  } // stringLength

  public boolean upperCase(List arguments) throws BuiltInException
  {
    String argument1, argument2;
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(SWRLB_UPPER_CASE, 2, arguments.size());

    argument1 = SWRLBuiltInUtil.getArgumentAsAString(SWRLB_UPPER_CASE, 0, arguments);
    argument2 = SWRLBuiltInUtil.getArgumentAsAString(SWRLB_UPPER_CASE, 1, arguments);

    return argument1.equals(argument2.toUpperCase());
  } // upperCase

  public boolean lowerCase(List arguments) throws BuiltInException
  {
    String argument1, argument2;
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(SWRLB_LOWER_CASE, 2, arguments.size());

    argument1 = SWRLBuiltInUtil.getArgumentAsAString(SWRLB_LOWER_CASE, 0, arguments);
    argument2 = SWRLBuiltInUtil.getArgumentAsAString(SWRLB_LOWER_CASE, 1, arguments);

    return argument1.equals(argument2.toLowerCase());
  } // lowerCase

  public boolean contains(List arguments) throws BuiltInException
  {
    String argument1, argument2;
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(SWRLB_CONTAINS, 2, arguments.size());

    argument1 = SWRLBuiltInUtil.getArgumentAsAString(SWRLB_CONTAINS, 0, arguments);
    argument2 = SWRLBuiltInUtil.getArgumentAsAString(SWRLB_CONTAINS, 1, arguments);

    return argument1.lastIndexOf(argument2) != -1;
  } // contains

  public boolean containsIgnoreCase(List arguments) throws BuiltInException
  {
    String argument1, argument2;
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(SWRLB_CONTAINS_IGNORE_CASE, 2, arguments.size());

    argument1 = SWRLBuiltInUtil.getArgumentAsAString(SWRLB_CONTAINS_IGNORE_CASE, 0, arguments);
    argument2 = SWRLBuiltInUtil.getArgumentAsAString(SWRLB_CONTAINS_IGNORE_CASE, 1, arguments);

    return argument1.toLowerCase().lastIndexOf(argument2.toLowerCase()) != -1;
  } // containsIgnoreCase

  public boolean startsWith(List arguments) throws BuiltInException
  {
    String argument1, argument2;
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(SWRLB_STARTS_WITH, 2, arguments.size());

    argument1 = SWRLBuiltInUtil.getArgumentAsAString(SWRLB_STARTS_WITH, 0, arguments);
    argument2 = SWRLBuiltInUtil.getArgumentAsAString(SWRLB_STARTS_WITH, 1, arguments);

    return argument1.startsWith(argument2);
  } // startsWith

  public boolean endsWith(List arguments) throws BuiltInException
  {
    String argument1, argument2;
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(SWRLB_ENDS_WITH, 2, arguments.size());

    argument1 = SWRLBuiltInUtil.getArgumentAsAString(SWRLB_ENDS_WITH, 0, arguments);
    argument2 = SWRLBuiltInUtil.getArgumentAsAString(SWRLB_ENDS_WITH, 1, arguments);

    return argument1.endsWith(argument2);
  } // endsWith

  // Private methods.

  private boolean equal(String builtInName, List arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(builtInName, 2, arguments.size());

    if (SWRLBuiltInUtil.isArgumentABoolean(0, arguments)) {
      boolean b1 = SWRLBuiltInUtil.getArgumentAsABoolean(builtInName, 0, arguments);
      boolean b2 = SWRLBuiltInUtil.getArgumentAsABoolean(builtInName, 1, arguments); // Performs type checking.
      
      return b1 == b2;
    } else return compareTwoArgumentsOfOrderedType(builtInName, arguments) == 0;
  } // equal

  private static int compareTwoArgumentsOfOrderedType(String builtInName, List arguments) throws BuiltInException
  {
    int result = 0; // Should be assigned by end of method.

    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(builtInName, 2, arguments.size());

    SWRLBuiltInUtil.checkThatAllArgumentsAreOfAnOrderedType(builtInName, arguments);

    if (SWRLBuiltInUtil.isArgumentAString(0, arguments)) {
   
      String s1 = SWRLBuiltInUtil.getArgumentAsAString(builtInName, 0, arguments);
      String s2 = SWRLBuiltInUtil.getArgumentAsAString(builtInName, 1, arguments); // Performs type checking.

      return s1.compareTo(s2);
    } else if (SWRLBuiltInUtil.isArgumentAnInteger(0, arguments)) {

      int i1 = SWRLBuiltInUtil.getArgumentAsAnInteger(builtInName, 0, arguments);
      int i2 = SWRLBuiltInUtil.getArgumentAsAnInteger(builtInName, 1, arguments); // Performs type checking.

      if (i1 < i2) result = -1; else if (i1 > i2) result = 1; else result = 0;
    } else if (SWRLBuiltInUtil.isArgumentALong(0, arguments)) {

      long l1 = SWRLBuiltInUtil.getArgumentAsALong(builtInName, 0, arguments);
      long l2 = SWRLBuiltInUtil.getArgumentAsALong(builtInName, 1, arguments); // Performs type checking.

      if (l1 < l2) result = -1; else if (l1 > l2) result = 1; else result = 0;
    } else if (SWRLBuiltInUtil.isArgumentAFloat(0, arguments)) {
      float f1 = SWRLBuiltInUtil.getArgumentAsAFloat(builtInName, 0, arguments);
      float f2 = SWRLBuiltInUtil.getArgumentAsAFloat(builtInName, 1, arguments); // Performs type checking.

      if (f1 < f2) result = -1; else if (f1 > f2) result = 1; else result = 0;
    } else if (SWRLBuiltInUtil.isArgumentADouble(0, arguments)) {
      double d1 = SWRLBuiltInUtil.getArgumentAsADouble(builtInName, 0, arguments);
      double d2 = SWRLBuiltInUtil.getArgumentAsADouble(builtInName, 1, arguments); // Performs type checking.

      if (d1 < d2) result = -1; else if (d1 > d2) result =  1; else result = 0;
    } else throw new InvalidBuiltInArgumentException(builtInName, 1, "Unknown argument type");

    return result;

  } // greaterThan

  private boolean lessThan(String builtInName, List arguments) throws BuiltInException
  {
    return (compareTwoArgumentsOfOrderedType(builtInName, arguments) < 0);
  } // lessThan

  // TODO: this only supports integers at the moment. Need to rewrite this using generics so it will support all types.

  private boolean mathOperation(String builtInName, List arguments) throws BuiltInException
  {
    Iterator iterator;
    int argumentNumber, argument1, argument2, argument3;
    boolean result;

    if (!SWRLBuiltInUtil.areAllArgumentsIntegers(builtInName, arguments))
      throw new BuiltInNotImplementedException(builtInName, "for non integer types");

    // Argument number checking will have been performed by invoking method.

    argument1 = SWRLBuiltInUtil.getArgumentAsAnInteger(builtInName, 0, arguments);

    if (builtInName.equalsIgnoreCase(SWRLB_ADD)) {
      int sum = 0;
      for (argumentNumber = 1; argumentNumber <= arguments.size(); argumentNumber++) {
        sum += SWRLBuiltInUtil.getArgumentAsAnInteger(builtInName, argumentNumber, arguments);
      } // for

      return (argument1 == sum);
    } else if (builtInName.equalsIgnoreCase(SWRLB_MULTIPLY)) {
      int product = 0;
      for (argumentNumber = 1; argumentNumber <= arguments.size(); argumentNumber++) {
        product *= SWRLBuiltInUtil.getArgumentAsAnInteger(builtInName, argumentNumber, arguments);
      } // for

      return (argument1 == product);
    } else if (builtInName.equalsIgnoreCase(SWRLB_SUBTRACT)) {
      argument2 = SWRLBuiltInUtil.getArgumentAsAnInteger(builtInName, 1, arguments);
      argument3 = SWRLBuiltInUtil.getArgumentAsAnInteger(builtInName, 2, arguments);

      result = (argument1 == (argument2 - argument3));
    } else if (builtInName.equalsIgnoreCase(SWRLB_DIVIDE)) {
      argument2 = SWRLBuiltInUtil.getArgumentAsAnInteger(builtInName, 1, arguments);
      argument3 = SWRLBuiltInUtil.getArgumentAsAnInteger(builtInName, 2, arguments);

      result = (argument1 == (argument2 / argument3));
    } else if (builtInName.equalsIgnoreCase(SWRLB_INTEGER_DIVIDE)) {
      argument2 = SWRLBuiltInUtil.getArgumentAsAnInteger(builtInName, 1, arguments);
      argument3 = SWRLBuiltInUtil.getArgumentAsAnInteger(builtInName, 2, arguments);

      if (argument3 == 0) result = false;
      else if (argument3 >= 0) result = (argument1 == (argument2 + argument3 + 1 / argument3));
      else result = (argument1 == (argument2 / argument3));
    } else if (builtInName.equalsIgnoreCase(SWRLB_MOD)) {
      argument2 = SWRLBuiltInUtil.getArgumentAsAnInteger(builtInName, 1, arguments);
      argument3 = SWRLBuiltInUtil.getArgumentAsAnInteger(builtInName, 2, arguments);

      result = (argument1 == (argument2 % argument3));
    } else if (builtInName.equalsIgnoreCase(SWRLB_POW)) {
      argument2 = SWRLBuiltInUtil.getArgumentAsAnInteger(builtInName, 1, arguments);
      argument3 = SWRLBuiltInUtil.getArgumentAsAnInteger(builtInName, 2, arguments);

      result = (argument1 == (int)java.lang.Math.pow(argument2, argument3));
    } else if (builtInName.equalsIgnoreCase(SWRLB_UNARY_PLUS)) {
      argument2 = SWRLBuiltInUtil.getArgumentAsAnInteger(builtInName, 1, arguments);

      result = (argument1 == argument2);
    } else if (builtInName.equalsIgnoreCase(SWRLB_UNARY_MINUS)) {
      argument2 = SWRLBuiltInUtil.getArgumentAsAnInteger(builtInName, 1, arguments);

      result = (argument1 == -argument2);
    } else if (builtInName.equalsIgnoreCase(SWRLB_ABS)) {
      argument2 = SWRLBuiltInUtil.getArgumentAsAnInteger(builtInName, 1, arguments);

      result = (argument1 == java.lang.Math.abs(argument2));
    } else if (builtInName.equalsIgnoreCase(SWRLB_CEILING)) {
      argument2 = SWRLBuiltInUtil.getArgumentAsAnInteger(builtInName, 1, arguments);

      result = (argument1 == (int)java.lang.Math.ceil(argument2));
    } else if (builtInName.equalsIgnoreCase(SWRLB_FLOOR)) {
      argument2 = SWRLBuiltInUtil.getArgumentAsAnInteger(builtInName, 1, arguments);

      result = (argument1 == (int)java.lang.Math.floor(argument2));
    } else if (builtInName.equalsIgnoreCase(SWRLB_ROUND)) {
      argument2 = SWRLBuiltInUtil.getArgumentAsAnInteger(builtInName, 1, arguments);

      result = (argument1 == (int)java.lang.Math.rint(argument2));
    } else if (builtInName.equalsIgnoreCase(SWRLB_ROUND_HALF_TO_EVEN)) {
      argument2 = SWRLBuiltInUtil.getArgumentAsAnInteger(builtInName, 1, arguments);

      result = false; // TODO
      throw new BuiltInNotImplementedException(SWRLB_ROUND_HALF_TO_EVEN);
    } else if (builtInName.equalsIgnoreCase(SWRLB_SIN)) {
      argument2 = SWRLBuiltInUtil.getArgumentAsAnInteger(builtInName, 1, arguments);

      result = (argument1 == (int)java.lang.Math.sin(argument2));
    } else if (builtInName.equalsIgnoreCase(SWRLB_COS)) {
      argument2 = SWRLBuiltInUtil.getArgumentAsAnInteger(builtInName, 1, arguments);

      result = (argument1 == (int)java.lang.Math.cos(argument2));
    } else if (builtInName.equalsIgnoreCase(SWRLB_TAN)) {
      argument2 = SWRLBuiltInUtil.getArgumentAsAnInteger(builtInName, 1, arguments);

      result = (argument1 == (int)java.lang.Math.tan(argument2));
    } else throw new InvalidBuiltInNameException(builtInName);

    return result;
  } // mathOperation

} // SWRLBuiltInMethods
