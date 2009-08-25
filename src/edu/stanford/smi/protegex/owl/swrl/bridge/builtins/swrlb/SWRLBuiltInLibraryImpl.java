
// TODO: several string methods are not implemented.  TODO: built-ins for date, time, duration, URIs, and lists are not implemented.

package edu.stanford.smi.protegex.owl.swrl.bridge.builtins.swrlb;

import edu.stanford.smi.protegex.owl.swrl.bridge.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.builtins.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;

import edu.stanford.smi.protegex.owl.swrl.bridge.xsd.*;

import org.apache.commons.lang.StringUtils;
import org.apache.axis.types.*;

import java.text.*;
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
public class SWRLBuiltInLibraryImpl extends AbstractSWRLBuiltInLibrary
{
  private static String SWRLBLibraryName = "SWRLCoreBuiltIns";

  private static String SWRLBPrefix = "swrlb:";

  private ArgumentFactory argumentFactory;

  private static String dateFormatString = "y-M-d'T'h:m:sZ";
  private DateFormat dateFormat;
  private Calendar calendar2, calendar3;

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

  public SWRLBuiltInLibraryImpl() 
  { 
    super(SWRLBLibraryName); 

    argumentFactory = ArgumentFactory.getFactory();
  } // SWRLBuiltInLibraryImpl

  public void reset() {}
  
  // Built-ins for comparison, defined in Section 8.1. of http://www.daml.org/2004/04/swrl/builtins.html.

  public boolean greaterThan(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result = false;

    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(2, arguments.size());

    if (SWRLBuiltInUtil.isArgumentAString(0, arguments)) {   
      String s1 = SWRLBuiltInUtil.getArgumentAsAString(0, arguments);
      if (SWRLBuiltInUtil.isArgumentAString(1, arguments)) {
        String s2 = SWRLBuiltInUtil.getArgumentAsAString(1, arguments);
        result = s1.compareTo(s2) > 0;
      } else throw new InvalidBuiltInArgumentException(1, "expecting string argument for comparison, got '" + SWRLBuiltInUtil.getArgumentAsAString(1, arguments) + "'");
    } else if (SWRLBuiltInUtil.isArgumentNumeric(0, arguments)) {
      if (SWRLBuiltInUtil.isArgumentNumeric(1, arguments)) result = compareTwoNumericArguments(arguments) > 0;
      else throw new InvalidBuiltInArgumentException(1, "expecting numeric argument for comparison, got '" + SWRLBuiltInUtil.getArgumentAsAString(1, arguments) + "'");
    } else throw new InvalidBuiltInArgumentException(0, "expecting string or numeric argument for comparison, got '" + SWRLBuiltInUtil.getArgumentAsAString(0, arguments) + "'");

    return result;
  } // greaterThan

  public boolean lessThan(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result = false;

    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(2, arguments.size());

    if (SWRLBuiltInUtil.isArgumentAString(0, arguments)) {   
      String s1 = SWRLBuiltInUtil.getArgumentAsAString(0, arguments);
      if (SWRLBuiltInUtil.isArgumentAString(1, arguments)) {
        String s2 = SWRLBuiltInUtil.getArgumentAsAString(1, arguments);
        result = s1.compareTo(s2) < 0;
      } else throw new InvalidBuiltInArgumentException(1, "expecting string argument for comparison, got '" + SWRLBuiltInUtil.getArgumentAsAString(1, arguments) + "'");
    } else if (SWRLBuiltInUtil.isArgumentNumeric(0, arguments)) {
      if (SWRLBuiltInUtil.isArgumentNumeric(1, arguments)) result = compareTwoNumericArguments(arguments) < 0;
      else throw new InvalidBuiltInArgumentException(1, "expecting numeric argument for comparison, got '" + SWRLBuiltInUtil.getArgumentAsAString(1, arguments) + "'");
    } else throw new InvalidBuiltInArgumentException(0, "expecting string or numeric argument for comparison, got '" + SWRLBuiltInUtil.getArgumentAsAString(0, arguments) + "'");

    return result;
  } // lessThan

  public boolean equal(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result = false;

    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(2, arguments.size());

    if (SWRLBuiltInUtil.hasUnboundArguments(arguments)) 
      throw new InvalidBuiltInArgumentException(0, "comparison built-ins do not support argument binding");

    if (SWRLBuiltInUtil.isArgumentABoolean(0, arguments)) {
      boolean b1 = SWRLBuiltInUtil.getArgumentAsABoolean(0, arguments);
      if (SWRLBuiltInUtil.isArgumentABoolean(1, arguments)) {
        boolean b2 = SWRLBuiltInUtil.getArgumentAsABoolean(1, arguments);
        result = b1 == b2;
      } else result = false;
    } else if (SWRLBuiltInUtil.isArgumentAString(0, arguments)) {   
      String s1 = SWRLBuiltInUtil.getArgumentAsAString(0, arguments);
      if (SWRLBuiltInUtil.isArgumentAString(1, arguments)) {
        String s2 = SWRLBuiltInUtil.getArgumentAsAString(1, arguments);
        result = s1.compareTo(s2) == 0;
      } else result = false;
    } else if (SWRLBuiltInUtil.isArgumentNumeric(0, arguments)) {
      if (SWRLBuiltInUtil.isArgumentNumeric(1, arguments)) result = compareTwoNumericArguments(arguments) == 0;
      else throw new InvalidBuiltInArgumentException(1, "expecting numeric argument for comparison, got '" + SWRLBuiltInUtil.getArgumentAsAString(1, arguments) + "'");
    } else throw new InvalidBuiltInArgumentException(0, "expecting string, numeric or boolean argument for comparison, got '" + SWRLBuiltInUtil.getArgumentAsAString(0, arguments) + "'");

    return result;
  } // equal

  public boolean notEqual(List<BuiltInArgument> arguments) throws BuiltInException
  {
    return !equal(arguments);
  } // notEqual

  public boolean lessThanOrEqual(List<BuiltInArgument> arguments) throws BuiltInException
  {
    return equal(arguments) || lessThan(arguments);
  } // lessThanOrEqual

  public boolean greaterThanOrEqual(List<BuiltInArgument> arguments) throws BuiltInException
  {
    return equal(arguments) || greaterThan(arguments);
  } // greaterThanOrEqual

  // Math Built-ins, defined in Section 8.2. of http://www.daml.org/2004/04/swrl/builtins.html.
  
  public boolean add(List<BuiltInArgument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsAtLeast(2, arguments.size());

    return mathOperation(SWRLB_ADD, arguments); 
  } // add

  public boolean subtract(List<BuiltInArgument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(3, arguments.size());

    return mathOperation(SWRLB_SUBTRACT, arguments);
  } // subtract

  public boolean multiply(List<BuiltInArgument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsAtLeast(2, arguments.size());

    return mathOperation(SWRLB_MULTIPLY, arguments);
  } // multiply

  public boolean divide(List<BuiltInArgument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(3, arguments.size());

    return mathOperation(SWRLB_DIVIDE, arguments);
  } // divide

  public boolean integerDivide(List<BuiltInArgument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(3, arguments.size());

    return mathOperation(SWRLB_INTEGER_DIVIDE, arguments);
  } // integerDivide

  public boolean mod(List<BuiltInArgument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(3, arguments.size());

    return mathOperation(SWRLB_MOD, arguments);
  } // mod

  public boolean pow(List<BuiltInArgument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(3, arguments.size());

    return mathOperation(SWRLB_POW, arguments);
  } // pow

  public boolean unaryPlus(List<BuiltInArgument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(2, arguments.size());

    return mathOperation(SWRLB_UNARY_PLUS, arguments);
  } // unaryPlus

  public boolean unaryMinus(List<BuiltInArgument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(2, arguments.size());

    return mathOperation(SWRLB_UNARY_MINUS, arguments);
  } // unaryMinus

  public boolean abs(List<BuiltInArgument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(2, arguments.size());

    return mathOperation(SWRLB_ABS, arguments);
  } // abs

  public boolean ceiling(List<BuiltInArgument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(2, arguments.size());

    return mathOperation(SWRLB_CEILING, arguments);
  } // ceiling

  public boolean floor(List<BuiltInArgument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(2, arguments.size());

    return mathOperation(SWRLB_FLOOR, arguments);
  } // floor

  public boolean round(List<BuiltInArgument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(2, arguments.size());

    return mathOperation(SWRLB_ROUND, arguments);
  } // round

  public boolean roundHalfToEven(List<BuiltInArgument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(2, arguments.size());

    return mathOperation(SWRLB_ROUND_HALF_TO_EVEN, arguments);
  } // roundHalfToEven

  public boolean sin(List<BuiltInArgument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(2, arguments.size());

    return mathOperation(SWRLB_SIN, arguments);
  } // sin

  public boolean cos(List<BuiltInArgument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(2, arguments.size());

    return mathOperation(SWRLB_COS, arguments);
  } // cos

  public boolean tan(List<BuiltInArgument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(2, arguments.size());

    return mathOperation(SWRLB_TAN, arguments);
  } // tan

  // Built-ins for Booleans. cf. Section 8.3 of http://www.daml.org/2004/04/swrl/builtins.html

  public boolean booleanNot(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result;

    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(2, arguments.size());
    SWRLBuiltInUtil.checkForUnboundNonFirstArguments(arguments);

    if (SWRLBuiltInUtil.isUnboundArgument(0, arguments)) {
      if (!SWRLBuiltInUtil.areAllArgumentsBooleans(arguments.subList(1, arguments.size())))
        throw new InvalidBuiltInArgumentException(1, "expecting a Boolean");

      boolean operationResult = !SWRLBuiltInUtil.getArgumentAsABoolean(1, arguments);
      arguments.set(0, argumentFactory.createDatatypeValueArgument(operationResult)); // Bind the result to the first parameter
      result = true;
    } else {
      if (!SWRLBuiltInUtil.areAllArgumentsBooleans(arguments))
        throw new InvalidBuiltInArgumentException("expecting all Boolean arguments");

      result = !equal(arguments);
    } // if
    return result;
  } // booleanNot

  // Built-ins for Strings. cf. Section 8.4 of http://www.daml.org/2004/04/swrl/builtins.html
  
  public boolean stringEqualIgnoreCase(List<BuiltInArgument> arguments) throws BuiltInException
  {
    String argument1, argument2;

    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(2, arguments.size());
    SWRLBuiltInUtil.checkForUnboundArguments(arguments);

    argument1 = SWRLBuiltInUtil.getArgumentAsAString(0, arguments);
    argument2 = SWRLBuiltInUtil.getArgumentAsAString(1, arguments);

    return argument1.equalsIgnoreCase(argument2);
  } // stringEqualIgnoreCase

  public boolean stringConcat(List<BuiltInArgument> arguments) throws BuiltInException
  {
    String operationResult = "";
    boolean result;

    SWRLBuiltInUtil.checkNumberOfArgumentsAtLeast(2, arguments.size());
    SWRLBuiltInUtil.checkForUnboundNonFirstArguments(arguments);

    for (int argumentNumber = 1; argumentNumber < arguments.size(); argumentNumber++) { // Exception thrown if argument is not a literal.
      operationResult = operationResult.concat(SWRLBuiltInUtil.getArgumentAsALiteral(argumentNumber, arguments).toString());
    } // for

    if (SWRLBuiltInUtil.isUnboundArgument(0, arguments)) {
      arguments.set(0, argumentFactory.createDatatypeValueArgument(operationResult)); // Bind the result to the first parameter
      result = true;
    } else {
      String argument1 = SWRLBuiltInUtil.getArgumentAsAString(0, arguments);
      result =  argument1.equals(operationResult);
    } //if

    return result;
  } // stringConcat

  public boolean substring(List<BuiltInArgument> arguments) throws BuiltInException
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
      arguments.set(0, argumentFactory.createDatatypeValueArgument(operationResult)); // Bind the result to the first parameter
      result = true;
    } else {
      String argument1 = SWRLBuiltInUtil.getArgumentAsAString(0, arguments);
      result =  argument1.equals(operationResult);
    } //if
    return result;
  } // substring

  public boolean stringLength(List<BuiltInArgument> arguments) throws BuiltInException
  {
    String argument2;
    boolean result;
    int operationResult;

    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(2, arguments.size());
    SWRLBuiltInUtil.checkForUnboundNonFirstArguments(arguments);

    argument2 = SWRLBuiltInUtil.getArgumentAsAString(1, arguments);
    operationResult = argument2.length();

    if (SWRLBuiltInUtil.hasUnboundArguments(arguments)) {
      arguments.set(0, argumentFactory.createDatatypeValueArgument(operationResult)); // Bind the result to the first parameter
      result = true;
    } else {
      String argument1 = SWRLBuiltInUtil.getArgumentAsAString(0, arguments);
      result = argument1.length() == operationResult;
    } //if
    return result;
  } // stringLength

  public boolean upperCase(List<BuiltInArgument> arguments) throws BuiltInException
  {
    String argument2, operationResult;
    boolean result;

    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(2, arguments.size());
    SWRLBuiltInUtil.checkForUnboundNonFirstArguments(arguments);

    argument2 = SWRLBuiltInUtil.getArgumentAsAString(1, arguments);
    operationResult = argument2.toUpperCase();

    if (SWRLBuiltInUtil.hasUnboundArguments(arguments)) {
      arguments.set(0, argumentFactory.createDatatypeValueArgument(operationResult)); // Bind the result to the first parameter
      result = true;
    } else {
      String argument1 = SWRLBuiltInUtil.getArgumentAsAString(0, arguments);
      result = argument1.equals(operationResult);
    } //if
    return result;
  } // upperCase

  public boolean lowerCase(List<BuiltInArgument> arguments) throws BuiltInException
  {
    String argument2, operationResult;
    boolean result;

    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(2, arguments.size());
    SWRLBuiltInUtil.checkForUnboundNonFirstArguments(arguments);

    argument2 = SWRLBuiltInUtil.getArgumentAsAString(1, arguments);

    operationResult = argument2.toLowerCase();

    if (SWRLBuiltInUtil.hasUnboundArguments(arguments)) {
      arguments.set(0, argumentFactory.createDatatypeValueArgument(operationResult)); // Bind the result to the first parameter
      result = true;
    } else {
      String argument1 = SWRLBuiltInUtil.getArgumentAsAString(0, arguments);
      result = argument1.equals(operationResult);
    } //if
    return result;
  } // lowerCase

  public boolean contains(List<BuiltInArgument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(2, arguments.size());
    SWRLBuiltInUtil.checkForUnboundArguments(arguments);

    String argument1 = SWRLBuiltInUtil.getArgumentAsAString(0, arguments);
    String argument2 = SWRLBuiltInUtil.getArgumentAsAString(1, arguments);

    return argument1.lastIndexOf(argument2) != -1;
  } // contains

  public boolean containsIgnoreCase(List<BuiltInArgument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(2, arguments.size());
    SWRLBuiltInUtil.checkForUnboundArguments(arguments);

    String argument1 = SWRLBuiltInUtil.getArgumentAsAString(0, arguments);
    String argument2 = SWRLBuiltInUtil.getArgumentAsAString(1, arguments);

    return argument1.toLowerCase().lastIndexOf(argument2.toLowerCase()) != -1;
  } // containsIgnoreCase

  public boolean startsWith(List<BuiltInArgument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(2, arguments.size());
    SWRLBuiltInUtil.checkForUnboundArguments(arguments);

    String argument1 = SWRLBuiltInUtil.getArgumentAsAString(0, arguments);
    String argument2 = SWRLBuiltInUtil.getArgumentAsAString(1, arguments);

    return argument1.startsWith(argument2);
  } // startsWith

  public boolean endsWith(List<BuiltInArgument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(2, arguments.size());
    SWRLBuiltInUtil.checkForUnboundArguments(arguments);

    String argument1 = SWRLBuiltInUtil.getArgumentAsAString(0, arguments);
    String argument2 = SWRLBuiltInUtil.getArgumentAsAString(1, arguments);

    return argument1.endsWith(argument2);
  } // endsWith

  public boolean translate(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result;

    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(4, arguments.size());

    String argument2 = SWRLBuiltInUtil.getArgumentAsAString(1, arguments);
    String argument3 = SWRLBuiltInUtil.getArgumentAsAString(2, arguments);
    String argument4 = SWRLBuiltInUtil.getArgumentAsAString(3, arguments);
    String operationResult = StringUtils.replaceChars(argument2, argument3, argument4);

    if (SWRLBuiltInUtil.isUnboundArgument(0, arguments)) {
      arguments.set(0, argumentFactory.createDatatypeValueArgument(operationResult)); 
      result = true;
    } else {
      String argument1 = SWRLBuiltInUtil.getArgumentAsAString(0, arguments);
      result = argument1.equals(operationResult);
    } // if
    
    return result;
  } // translate

  public boolean substringAfter(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result;

    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(3, arguments.size());

    String argument2 = SWRLBuiltInUtil.getArgumentAsAString(1, arguments);
    String argument3 = SWRLBuiltInUtil.getArgumentAsAString(2, arguments);
    String operationResult = StringUtils.substringAfter(argument2, argument3);

    if (SWRLBuiltInUtil.isUnboundArgument(0, arguments)) {
      arguments.set(0, argumentFactory.createDatatypeValueArgument(operationResult)); 
      result = true;
    } else {
      String argument1 = SWRLBuiltInUtil.getArgumentAsAString(0, arguments);
      result = argument1.equals(operationResult);
    } // if
    
    return result;
  } // substringAfter

  public boolean substringBefore(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result;

    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(3, arguments.size());

    String argument2 = SWRLBuiltInUtil.getArgumentAsAString(1, arguments);
    String argument3 = SWRLBuiltInUtil.getArgumentAsAString(2, arguments);
    String operationResult = StringUtils.substringBefore(argument2, argument3);

    if (SWRLBuiltInUtil.isUnboundArgument(0, arguments)) {
      arguments.set(0, argumentFactory.createDatatypeValueArgument(operationResult)); 
      result = true;
    } else {
      String argument1 = SWRLBuiltInUtil.getArgumentAsAString(0, arguments);
      result = argument1.equals(operationResult);
    } // if

    return result;
  } // substringBefore

  public boolean matches(List<BuiltInArgument> arguments) throws BuiltInException
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

  public boolean replace(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result = false;

    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(4, arguments.size());
    SWRLBuiltInUtil.checkForUnboundNonFirstArguments(arguments);

    String input = SWRLBuiltInUtil.getArgumentAsAString(1, arguments);
    String regex = SWRLBuiltInUtil.getArgumentAsAString(2, arguments);
    String replacement = SWRLBuiltInUtil.getArgumentAsAString(3, arguments);

    Pattern p = Pattern.compile(regex);
    Matcher m = p.matcher(input);
    String operationResult = m.replaceAll(replacement);

    if (SWRLBuiltInUtil.isUnboundArgument(0, arguments)) {
      arguments.set(0, argumentFactory.createDatatypeValueArgument(operationResult)); 
      result = true;
    } else {
      String output = SWRLBuiltInUtil.getArgumentAsAString(0, arguments);
      result = output.equals(operationResult);
    } // if

    return result;
  } // replace

  public boolean normalizeSpace(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result = false;

    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(2, arguments.size());
    SWRLBuiltInUtil.checkForUnboundNonFirstArguments(arguments);

    String input = SWRLBuiltInUtil.getArgumentAsAString(1, arguments);

    Pattern p = Pattern.compile("\\s+");
    Matcher m = p.matcher(input);
    String operationResult = m.replaceAll(" ").trim();

    if (SWRLBuiltInUtil.isUnboundArgument(0, arguments)) {
      arguments.set(0, argumentFactory.createDatatypeValueArgument(operationResult)); 
      result = true;
    } else {
      String output = SWRLBuiltInUtil.getArgumentAsAString(0, arguments);
      result = output.equals(operationResult);
    } // if

    return result;
  } // normalizeSpace

  public boolean tokenize(List<BuiltInArgument> arguments) throws BuiltInException
  {
    StringTokenizer tokenizer;
    String inputString, delimeters;
    boolean result = false;

    if (!SWRLBuiltInUtil.isUnboundArgument(0, arguments)) throw new InvalidBuiltInArgumentException(0, "unexpected bound argument found");

    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(3, arguments.size());
    SWRLBuiltInUtil.checkForUnboundNonFirstArguments(arguments);

    inputString = SWRLBuiltInUtil.getArgumentAsAString(1, arguments);
    delimeters = SWRLBuiltInUtil.getArgumentAsAString(2, arguments);

    tokenizer = new StringTokenizer(inputString.trim(), delimeters);
    
    MultiArgument multiArgument = argumentFactory.createMultiArgument(SWRLBuiltInUtil.getVariableName(0, arguments), SWRLBuiltInUtil.getPrefixedVariableName(0, arguments));
    while (tokenizer.hasMoreTokens()) {
      String token = tokenizer.nextToken();
      multiArgument.addArgument(argumentFactory.createDatatypeValueArgument(token));
    } // while

    arguments.set(0, multiArgument);
    result = !multiArgument.hasNoArguments();

    return result;
  } // tokenize

  // Built-ins for date, time and duration.

  public boolean yearMonthDuration(List<BuiltInArgument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(3, arguments.size());
    int year = SWRLBuiltInUtil.getArgumentAsAnInteger(1, arguments);
    int month = SWRLBuiltInUtil.getArgumentAsAnInteger(2, arguments);
    org.apache.axis.types.Duration duration = new org.apache.axis.types.Duration();

    duration.setYears(year);
    duration.setMonths(month);

    return SWRLBuiltInUtil.processResultArgument(arguments, 0, argumentFactory, duration.toString());
  } // yearMonthDuration

  public boolean dayTimeDuration(List<BuiltInArgument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(5, arguments.size());
    int days =  SWRLBuiltInUtil.getArgumentAsAnInteger(1, arguments);
    int hours =  SWRLBuiltInUtil.getArgumentAsAnInteger(2, arguments);
    int minutes =  SWRLBuiltInUtil.getArgumentAsAnInteger(3, arguments);
    int seconds =  SWRLBuiltInUtil.getArgumentAsAnInteger(4, arguments);
    org.apache.axis.types.Duration duration = new org.apache.axis.types.Duration();

    duration.setDays(days);
    duration.setHours(hours);
    duration.setMinutes(minutes);
    duration.setSeconds(seconds);

    return SWRLBuiltInUtil.processResultArgument(arguments, 0, argumentFactory, duration.toString());
  } // dayTimeDuration

  public boolean dateTime(List<BuiltInArgument> arguments) throws BuiltInException
  {
    String operationResult;

    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(8, arguments.size());

    int year =  SWRLBuiltInUtil.getArgumentAsAnInteger(1, arguments);
    int month =  SWRLBuiltInUtil.getArgumentAsAnInteger(2, arguments);
    int days =  SWRLBuiltInUtil.getArgumentAsAnInteger(3, arguments);
    int hours =  SWRLBuiltInUtil.getArgumentAsAnInteger(4, arguments);
    int minutes =  SWRLBuiltInUtil.getArgumentAsAnInteger(5, arguments);
    int seconds =  SWRLBuiltInUtil.getArgumentAsAnInteger(6, arguments);
    String timeZone  =  SWRLBuiltInUtil.getArgumentAsAString(7, arguments);
    Calendar calendar = new GregorianCalendar();

    calendar.set(year, month, days, hours, minutes, seconds);
    calendar.setTimeZone(TimeZone.getTimeZone(timeZone));

    operationResult = "" + calendar.get(Calendar.YEAR) + "-" + calendar.get(Calendar.MONTH) + "-" + calendar.get(Calendar.DAY_OF_MONTH) + "T" +
                      calendar.get(Calendar.HOUR) + ":" + calendar.get(Calendar.MINUTE) + ":" + calendar.get(Calendar.SECOND) + calendar.getTimeZone().getID();

    return SWRLBuiltInUtil.processResultArgument(arguments, 0, argumentFactory, operationResult);
  } // dateTime

  public boolean date(List<BuiltInArgument> arguments) throws BuiltInException
  {
    String operationResult;

    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(5, arguments.size());

    int year =  SWRLBuiltInUtil.getArgumentAsAnInteger(1, arguments);
    int month =  SWRLBuiltInUtil.getArgumentAsAnInteger(2, arguments);
    int days =  SWRLBuiltInUtil.getArgumentAsAnInteger(3, arguments);
    String timeZone  =  SWRLBuiltInUtil.getArgumentAsAString(4, arguments);
    Calendar calendar = new GregorianCalendar();

    calendar.set(year, month, days);
    calendar.setTimeZone(TimeZone.getTimeZone(timeZone));

    operationResult = "" + calendar.get(Calendar.YEAR) + "-" + calendar.get(Calendar.MONTH) + "-" + calendar.get(Calendar.DAY_OF_MONTH) + calendar.getTimeZone().getID();

    return SWRLBuiltInUtil.processResultArgument(arguments, 0, argumentFactory, operationResult);
  } // date

  public boolean time(List<BuiltInArgument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(5, arguments.size());

    int hours =  SWRLBuiltInUtil.getArgumentAsAnInteger(1, arguments);
    int minutes =  SWRLBuiltInUtil.getArgumentAsAnInteger(2, arguments);
    int seconds =  SWRLBuiltInUtil.getArgumentAsAnInteger(3, arguments);
    String timeZone  =  SWRLBuiltInUtil.getArgumentAsAString(4, arguments);
    String operationResult = "" + hours + ":" + minutes + ":" + seconds + timeZone;

    return SWRLBuiltInUtil.processResultArgument(arguments, 0, argumentFactory, operationResult);
  } // time

  public boolean addYearMonthDurations(List<BuiltInArgument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsAtLeast(3, arguments.size());

    org.apache.axis.types.Duration operationDuration = new org.apache.axis.types.Duration();

    for (int i = 1; i < arguments.size(); i++) {
      org.apache.axis.types.Duration duration = getArgumentAsADuration(i, arguments);
      operationDuration = XSDTimeUtil.addYearMonthDurations(operationDuration, duration);
    } // for

    return SWRLBuiltInUtil.processResultArgument(arguments, 0, argumentFactory, operationDuration.toString());
  } // addYearMonthDurations

  public boolean subtractYearMonthDurations(List<BuiltInArgument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(3, arguments.size());

    org.apache.axis.types.Duration duration2 = getArgumentAsADuration(1, arguments);
    org.apache.axis.types.Duration duration3 = getArgumentAsADuration(2, arguments);
    org.apache.axis.types.Duration operationDuration = XSDTimeUtil.subtractYearMonthDurations(duration2, duration3);

    return SWRLBuiltInUtil.processResultArgument(arguments, 0, argumentFactory, operationDuration.toString());
  } // subtractYearMonthDurations

  public boolean multiplyYearMonthDuration(List<BuiltInArgument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(3, arguments.size());

    org.apache.axis.types.Duration duration2 = getArgumentAsADuration(1, arguments);
    org.apache.axis.types.Duration duration3 = getArgumentAsADuration(2, arguments);
    org.apache.axis.types.Duration operationDuration = XSDTimeUtil.multiplyYearMonthDurations(duration2, duration3);

    return SWRLBuiltInUtil.processResultArgument(arguments, 0, argumentFactory, operationDuration.toString());
  } // multiplyYearMonthDuration

  public boolean divideYearMonthDurations(List<BuiltInArgument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(3, arguments.size());

    org.apache.axis.types.Duration duration2 = getArgumentAsADuration(1, arguments);
    org.apache.axis.types.Duration duration3 = getArgumentAsADuration(2, arguments);
    org.apache.axis.types.Duration operationDuration = XSDTimeUtil.divideYearMonthDurations(duration2, duration3);

    return SWRLBuiltInUtil.processResultArgument(arguments, 0, argumentFactory, operationDuration.toString());
  } // divideYearMonthDurations

  public boolean addDayTimeDurations(List<BuiltInArgument> arguments) throws BuiltInException
  {
    org.apache.axis.types.Duration operationDuration = new org.apache.axis.types.Duration();

    SWRLBuiltInUtil.checkNumberOfArgumentsAtLeast(3, arguments.size());

    for (int i = 1; i < arguments.size(); i++) {
      org.apache.axis.types.Duration duration = getArgumentAsADuration(i, arguments);
      operationDuration = XSDTimeUtil.addDayTimeDurations(operationDuration, duration);      
    } // for

    return SWRLBuiltInUtil.processResultArgument(arguments, 0, argumentFactory, operationDuration.toString());
  } // addDayTimeDurations

  public boolean subtractDayTimeDurations(List<BuiltInArgument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(3, arguments.size());

    org.apache.axis.types.Duration duration2 = getArgumentAsADuration(1, arguments);
    org.apache.axis.types.Duration duration3 = getArgumentAsADuration(2, arguments);
    org.apache.axis.types.Duration operationDuration = XSDTimeUtil.subtractDayTimeDurations(duration2, duration3);

    return SWRLBuiltInUtil.processResultArgument(arguments, 0, argumentFactory, operationDuration.toString());
  } // subtractDayTimeDurations

  public boolean multiplyDayTimeDurations(List<BuiltInArgument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(3, arguments.size());

    org.apache.axis.types.Duration duration2 = getArgumentAsADuration(1, arguments);
    org.apache.axis.types.Duration duration3 = getArgumentAsADuration(2, arguments);
    org.apache.axis.types.Duration operationDuration = XSDTimeUtil.multiplyDayTimeDurations(duration2, duration3);

    return SWRLBuiltInUtil.processResultArgument(arguments, 0, argumentFactory, operationDuration.toString());
  } // multiplyDayTimeDurations

  public boolean divideDayTimeDuration(List<BuiltInArgument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(3, arguments.size());

    org.apache.axis.types.Duration duration2 = getArgumentAsADuration(1, arguments);
    org.apache.axis.types.Duration duration3 = getArgumentAsADuration(2, arguments);
    org.apache.axis.types.Duration operationDuration = XSDTimeUtil.divideDayTimeDurations(duration2, duration3);

    return SWRLBuiltInUtil.processResultArgument(arguments, 0, argumentFactory, operationDuration.toString());
  } // divideDayTimeDuration

  public boolean subtractDates(List<BuiltInArgument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(3, arguments.size());

    java.util.Date date2 = getArgumentAsADate(1, arguments);
    java.util.Date date3 = getArgumentAsADate(2, arguments);
    org.apache.axis.types.Duration operationDuration = XSDTimeUtil.subtractDates(date2, date3);

    return SWRLBuiltInUtil.processResultArgument(arguments, 0, argumentFactory, operationDuration.toString());
  } // subtractDates

  public boolean subtractTimes(List<BuiltInArgument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(3, arguments.size());

    org.apache.axis.types.Time time2 = getArgumentAsATime(1, arguments);
    org.apache.axis.types.Time time3 = getArgumentAsATime(2, arguments);
    org.apache.axis.types.Duration operationDuration = XSDTimeUtil.subtractTimes(time2, time3);

    return SWRLBuiltInUtil.processResultArgument(arguments, 0, argumentFactory, operationDuration.toString());
  } // subtractTimes

  public boolean addYearMonthDurationToDateTime(List<BuiltInArgument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(3, arguments.size());

    java.util.Date date = getArgumentAsADate(1, arguments);
    org.apache.axis.types.Duration duration = getArgumentAsADuration(2, arguments);
    java.util.Date operationDateTime = XSDTimeUtil.addYearMonthDurationToDateTime(date, duration);

    return SWRLBuiltInUtil.processResultArgument(arguments, 0, argumentFactory, XSDTimeUtil.date2XSDDateTimeString(operationDateTime));
  } // addYearMonthDurationToDateTime

  public boolean subtractYearMonthDurationFromDateTime(List<BuiltInArgument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(3, arguments.size());

    java.util.Date date = getArgumentAsADate(1, arguments);
    org.apache.axis.types.Duration duration = getArgumentAsADuration(2, arguments);
    java.util.Date operationDateTime = XSDTimeUtil.subtractYearMonthDurationFromDateTime(date, duration);
 
    return SWRLBuiltInUtil.processResultArgument(arguments, 0, argumentFactory, XSDTimeUtil.date2XSDDateTimeString(operationDateTime));
  } // subtractYearMonthDurationFromDateTime

  public boolean addDayTimeDurationToDateTime(List<BuiltInArgument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(3, arguments.size());

    java.util.Date date = getArgumentAsADate(1, arguments);
    org.apache.axis.types.Duration duration = getArgumentAsADuration(2, arguments);
    java.util.Date operationDateTime = XSDTimeUtil.addDayTimeDurationToDateTime(date, duration);
 
    return SWRLBuiltInUtil.processResultArgument(arguments, 0, argumentFactory, XSDTimeUtil.date2XSDDateTimeString(operationDateTime));
  } // addDayTimeDurationToDateTime

  public boolean subtractDayTimeDurationFromDateTime(List<BuiltInArgument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(3, arguments.size());

    java.util.Date date = getArgumentAsADate(1, arguments);
    org.apache.axis.types.Duration duration = getArgumentAsADuration(2, arguments);
    java.util.Date operationDateTime = XSDTimeUtil.subtractDayTimeDurationFromDateTime(date, duration);
 
    return SWRLBuiltInUtil.processResultArgument(arguments, 0, argumentFactory, XSDTimeUtil.date2XSDDateTimeString(operationDateTime));
  } // subtractDayTimeDurationFromDateTime

  public boolean addYearMonthDurationToDate(List<BuiltInArgument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(3, arguments.size());

    java.util.Date date = getArgumentAsADate(1, arguments);
    org.apache.axis.types.Duration duration = getArgumentAsADuration(2, arguments);
    java.util.Date operationDateTime = XSDTimeUtil.addYearMonthDurationToDate(date, duration);
 
    return SWRLBuiltInUtil.processResultArgument(arguments, 0, argumentFactory, XSDTimeUtil.date2XSDDateTimeString(operationDateTime));
  } // addYearMonthDurationToDate

  public boolean subtractYearMonthDurationFromDate(List<BuiltInArgument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(3, arguments.size());

    java.util.Date date = getArgumentAsADate(1, arguments);
    org.apache.axis.types.Duration duration = getArgumentAsADuration(2, arguments);
    java.util.Date operationDateTime = XSDTimeUtil.subtractYearMonthDurationFromDate(date, duration);
 
    return SWRLBuiltInUtil.processResultArgument(arguments, 0, argumentFactory, XSDTimeUtil.date2XSDDateTimeString(operationDateTime));
  } // subtractYearMonthDurationFromDate

  public boolean addDayTimeDurationToDate(List<BuiltInArgument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(3, arguments.size());

    java.util.Date date = getArgumentAsADate(1, arguments);
    org.apache.axis.types.Duration duration = getArgumentAsADuration(2, arguments);
    java.util.Date operationDateTime = XSDTimeUtil.addDayTimeDurationToDateTime(date, duration);
 
    return SWRLBuiltInUtil.processResultArgument(arguments, 0, argumentFactory, XSDTimeUtil.date2XSDDateTimeString(operationDateTime));
  } // addDayTimeDurationToDate

  public boolean subtractDayTimeDurationFromDate(List<BuiltInArgument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(3, arguments.size());

    java.util.Date date = getArgumentAsADate(1, arguments);
    org.apache.axis.types.Duration duration = getArgumentAsADuration(2, arguments);
    java.util.Date operationDateTime = XSDTimeUtil.subtractDayTimeDurationFromDateTime(date, duration);
 
    return SWRLBuiltInUtil.processResultArgument(arguments, 0, argumentFactory, XSDTimeUtil.date2XSDDateTimeString(operationDateTime));
  } // subtractDayTimeDurationFromDate

  public boolean addDayTimeDurationToTime(List<BuiltInArgument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(3, arguments.size());

    org.apache.axis.types.Time time = getArgumentAsATime(1, arguments);
    org.apache.axis.types.Duration duration = getArgumentAsADuration(2, arguments);
    org.apache.axis.types.Time operationTime = XSDTimeUtil.addDayTimeDurationToTime(time, duration);

    return SWRLBuiltInUtil.processResultArgument(arguments, 0, argumentFactory, operationTime.toString());
  } // addDayTimeDurationToTime

  public boolean subtractDayTimeDurationFromTime(List<BuiltInArgument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(3, arguments.size());

    org.apache.axis.types.Time time = getArgumentAsATime(1, arguments);
    org.apache.axis.types.Duration duration = getArgumentAsADuration(2, arguments);
    org.apache.axis.types.Time operationTime = XSDTimeUtil.subtractDayTimeDurationFromTime(time, duration);

    return SWRLBuiltInUtil.processResultArgument(arguments, 0, argumentFactory, operationTime.toString());
  } // subtractDayTimeDurationFromTime

  public boolean subtractDateTimesYieldingYearMonthDuration(List<BuiltInArgument> arguments) throws BuiltInException
  {
   java.util.Date date1 = getArgumentAsADate(1, arguments);
   java.util.Date date2 = getArgumentAsADate(2, arguments);
   org.apache.axis.types.Duration operationDuration = XSDTimeUtil.subtractDateTimesYieldingYearMonthDuration(date1, date2);
 
   return SWRLBuiltInUtil.processResultArgument(arguments, 0, argumentFactory, operationDuration.toString());
  } // subtractDateTimesYieldingYearMonthDuration

  public boolean subtractDateTimesYieldingDayTimeDuration(List<BuiltInArgument> arguments) throws BuiltInException
  {
    java.util.Date date1 = getArgumentAsADate(1, arguments);
    java.util.Date date2 = getArgumentAsADate(2, arguments);
    org.apache.axis.types.Duration operationDuration = XSDTimeUtil.subtractDateTimesYieldingDayTimeDuration(date1, date2);
    
   return SWRLBuiltInUtil.processResultArgument(arguments, 0, argumentFactory, operationDuration.toString());
  } // subtractDateTimesYieldingDayTimeDuration 

  // Built-ins for URIs

  public boolean resolveURI(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } // resolveURI

  public boolean anyURI(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } // anyURI

  // Built-ins for Lists

  public boolean listConcat(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } // listConcat

  public boolean listIntersection(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } // listIntersection

  public boolean listSubtraction(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } // listSubtraction

  public boolean member(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } // member 

  public boolean length(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } // length

  public boolean first(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } // first

  public boolean rest(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } // rest

  public boolean sublist(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } // sublist

  public boolean empty(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } // empty

  // Private methods

  private int compareTwoNumericArguments(List<BuiltInArgument> arguments) throws BuiltInException
  {
    int result = 0;

    SWRLBuiltInUtil.checkThatAllArgumentsAreNumeric(arguments);

    if (SWRLBuiltInUtil.isShortMostPreciseArgument(arguments)) {
      short s1 = SWRLBuiltInUtil.getArgumentAsAShort(0, arguments);
      short s2 = SWRLBuiltInUtil.getArgumentAsAShort(1, arguments);
      if (s1 < s2) result = -1; else if (s1 > s2) result =  1; else result = 0;
    } else if (SWRLBuiltInUtil.isIntegerMostPreciseArgument(arguments)) {
      int i1 = SWRLBuiltInUtil.getArgumentAsAnInteger(0, arguments);
      int i2 = SWRLBuiltInUtil.getArgumentAsAnInteger(1, arguments);
      if (i1 < i2) result = -1; else if (i1 > i2) result = 1; else result = 0;
    } else if (SWRLBuiltInUtil.isLongMostPreciseArgument(arguments)) {
      long l1 = SWRLBuiltInUtil.getArgumentAsALong(0, arguments);
      long l2 = SWRLBuiltInUtil.getArgumentAsALong(1, arguments); 
      if (l1 < l2) result = -1; else if (l1 > l2) result =  1; else result = 0;
    } else if (SWRLBuiltInUtil.isFloatMostPreciseArgument(arguments)) {
      float f1 = SWRLBuiltInUtil.getArgumentAsAFloat(0, arguments);
      float f2 = SWRLBuiltInUtil.getArgumentAsAFloat(1, arguments); 
      if (f1 < f2) result = -1; else if (f1 > f2) result = 1; else result = 0;
    } else {
      double d1 = SWRLBuiltInUtil.getArgumentAsADouble(0, arguments);
      double d2 = SWRLBuiltInUtil.getArgumentAsADouble(1, arguments); 
      if (d1 < d2) result = -1; else if (d1 > d2) result =  1; else result = 0;
    } // if

    return result;
  } // equal

  private boolean mathOperation(String builtInName, List<BuiltInArgument> arguments) throws BuiltInException
  {
    int argumentNumber;
    double argument1 = 0.0, argument2, argument3, operationResult = 0.0; 
    boolean result = false, hasUnbound1stArgument = false;

    SWRLBuiltInUtil.checkForUnboundNonFirstArguments(arguments); // Only supports binding of first argument

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
      List<BuiltInArgument> boundArguments = arguments.subList(1, arguments.size());

      if (builtInName.equalsIgnoreCase(SWRLB_SIN) ||
          builtInName.equalsIgnoreCase(SWRLB_COS) || builtInName.equalsIgnoreCase(SWRLB_TAN))
        arguments.set(0, argumentFactory.createDatatypeValueArgument(operationResult));
      else if (SWRLBuiltInUtil.isShortMostPreciseArgument(boundArguments))
        arguments.set(0, argumentFactory.createDatatypeValueArgument((short)operationResult)); 
      else if (SWRLBuiltInUtil.isIntegerMostPreciseArgument(boundArguments))
        arguments.set(0, argumentFactory.createDatatypeValueArgument((int)operationResult));
      else if (SWRLBuiltInUtil.isFloatMostPreciseArgument(boundArguments))
        arguments.set(0, argumentFactory.createDatatypeValueArgument((float)operationResult));
      else if (SWRLBuiltInUtil.isLongMostPreciseArgument(boundArguments))
        arguments.set(0, argumentFactory.createDatatypeValueArgument((long)operationResult));
      else arguments.set(0, argumentFactory.createDatatypeValueArgument(operationResult));

      result = true;
    } else result = (argument1 == operationResult);

    return result;
  } // mathOperation

  private org.apache.axis.types.Duration getArgumentAsADuration(int argumentNumber, List<BuiltInArgument> arguments) 
    throws BuiltInException
  {
    org.apache.axis.types.Duration result = null;
    String argument = "";

    try {
      argument = SWRLBuiltInUtil.getArgumentAsAString(argumentNumber, arguments);
      result = new org.apache.axis.types.Duration(argument);
    } catch (IllegalArgumentException e) {
      throw new BuiltInException("invalid xsd:duration '" + argument + "': " + e.getMessage());
    } // try

    return result;
  } // getArgumentAsADuration

  private java.util.Date getArgumentAsADate(int argumentNumber,  List<BuiltInArgument> arguments) throws BuiltInException
  {
    java.util.Date result = null;
    String argument = "";

    try {
      argument = SWRLBuiltInUtil.getArgumentAsAString(argumentNumber, arguments);
      result = dateFormat.parse(argument); 
    } catch (ParseException e) {
      throw new BuiltInException("invalid xsd:date '" + argument + "': " + e.getMessage());
    } // try

    return result;
  } // getArgumentAsADate

  private org.apache.axis.types.Time getArgumentAsATime(int argumentNumber,  List<BuiltInArgument> arguments) throws BuiltInException
  {
    org.apache.axis.types.Time  result = null;
    String argument = "";

    try {
      argument = SWRLBuiltInUtil.getArgumentAsAString(argumentNumber, arguments);
      result = new org.apache.axis.types.Time(argument); 
    } catch (NumberFormatException e) {
      throw new BuiltInException("invalid xsd:time '" + argument + "': " + e.getMessage());
    } // try

    return result;
  } // getArgumentAsATime

} // SWRLBuiltInLibraryImpl
