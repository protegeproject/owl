
package edu.stanford.smi.protegex.owl.swrl.bridge.builtins;

import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.swrl.model.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;
import edu.stanford.smi.protegex.owl.swrl.util.SWRLOWLUtil;
import edu.stanford.smi.protegex.owl.swrl.exceptions.*;

import java.util.List;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 ** Class containing argument processing utility methods that can be used in built-in method implementations.
 */
public class SWRLBuiltInUtil
{
  public static void checkNumberOfArgumentsEqualTo(int expecting, int actual) 
    throws InvalidBuiltInArgumentNumberException
  {
    if (expecting != actual) throw new InvalidBuiltInArgumentNumberException(expecting, actual);
  } // checkNumberOfArgumentsEqualTo

  public static void checkNumberOfArgumentsAtLeast(int expectingAtLeast, int actual) 
    throws InvalidBuiltInArgumentNumberException
  {
    if (actual < expectingAtLeast) throw new InvalidBuiltInArgumentNumberException(expectingAtLeast, actual, "at least");
  } // checkNumberOfArgumentsAtLeast

  public static void checkNumberOfArgumentsAtMost(int expectingAtMost, int actual) 
    throws InvalidBuiltInArgumentNumberException
  {
    if (actual > expectingAtMost) throw new InvalidBuiltInArgumentNumberException(expectingAtMost, actual, "at most");
  } // checkNumberOfArgumentsAtMost

  public static void checkNumberOfArgumentsInRange(int expectingAtLeast, int expectingAtMost, int actual) 
    throws InvalidBuiltInArgumentNumberException
  {
    if (actual > expectingAtMost || actual < expectingAtLeast)
      throw new InvalidBuiltInArgumentNumberException(expectingAtMost, actual, expectingAtLeast + " to");
  } // checkNumberOfArgumentsInRange

  public static void checkThatAllArgumentsAreLiterals(List<Argument> arguments) throws BuiltInException
  {
    for (int argumentNumber = 0; argumentNumber < arguments.size(); argumentNumber++) 
      checkThatArgumentIsALiteral(argumentNumber, arguments);
  } // checkThatAllArgumentsAreLiterals

  public static void checkThatAllArgumentsAreNumeric(List<Argument> arguments) throws BuiltInException
  {
    for (int argumentNumber = 0; argumentNumber < arguments.size(); argumentNumber++) 
      checkThatArgumentIsNumeric(argumentNumber, arguments);
  } // checkThatAllArgumentsAreNumeric

  public static void checkThatAllArgumentsAreIntegers(List<Argument> arguments) throws BuiltInException
  {
    for (int argumentNumber = 0; argumentNumber < arguments.size(); argumentNumber++) 
      checkThatArgumentIsAnInteger(argumentNumber, arguments);
  } // checkThatAllArgumentsAreIntegers

  public static boolean areAllArgumentsShorts(List<Argument> arguments) throws BuiltInException
  {
    for (int argumentNumber = 0; argumentNumber < arguments.size(); argumentNumber++) 
      if (!isArgumentAShort(argumentNumber, arguments)) return false;
    return true;
  } // areAllArgumentsShorts

  public static boolean areAllArgumentsIntegers(List<Argument> arguments) throws BuiltInException
  {
    for (int argumentNumber = 0; argumentNumber < arguments.size(); argumentNumber++) 
      if (!isArgumentAnInteger(argumentNumber, arguments)) return false;
    return true;
  } // areAllArgumentsIntegers

  public static boolean areAllArgumentsLongs(List<Argument> arguments) throws BuiltInException
  {
    for (int argumentNumber = 0; argumentNumber < arguments.size(); argumentNumber++) 
      if (!isArgumentALong(argumentNumber, arguments)) return false;
    return true;
  } // areAllArgumentsLongs

  public static boolean areAllArgumentsFloats(List<Argument> arguments) throws BuiltInException
  {
    for (int argumentNumber = 0; argumentNumber < arguments.size(); argumentNumber++) 
      if (!isArgumentAFloat(argumentNumber, arguments)) return false;
    return true;
  } // areAllArgumentsFloats

  public static boolean areAllArgumentsDoubles(List<Argument> arguments) throws BuiltInException
  {
    for (int argumentNumber = 0; argumentNumber < arguments.size(); argumentNumber++) 
      if (!isArgumentADouble(argumentNumber, arguments)) return false;
    return true;
  } // areAllArgumentsDoubles

  public static boolean isShortMostPreciseArgument(List<Argument> arguments) throws BuiltInException
  {
    for (int argumentNumber = 0; argumentNumber < arguments.size(); argumentNumber++) 
      if (isArgumentAnInteger(argumentNumber, arguments) || isArgumentALong(argumentNumber, arguments) || 
          isArgumentAFloat(argumentNumber, arguments) || isArgumentADouble(argumentNumber, arguments)) return false;
    return true;
  } // isIntegerMostPreciseArgument

  public static boolean isIntegerMostPreciseArgument(List<Argument> arguments) throws BuiltInException
  {
    for (int argumentNumber = 0; argumentNumber < arguments.size(); argumentNumber++) 
      if (isArgumentALong(argumentNumber, arguments) || isArgumentAFloat(argumentNumber, arguments) ||
          isArgumentADouble(argumentNumber, arguments)) return false;
    return true;
  } // isIntegerMostPreciseArgument

  public static boolean isFloatMostPreciseArgument(List<Argument> arguments) throws BuiltInException
  {
    for (int argumentNumber = 0; argumentNumber < arguments.size(); argumentNumber++) 
      if (isArgumentADouble(argumentNumber, arguments) || isArgumentALong(argumentNumber, arguments)) return false;
    return true;
  } // isLongMostPreciseArgument

  public static boolean isLongMostPreciseArgument(List<Argument> arguments) throws BuiltInException
  {
    for (int argumentNumber = 0; argumentNumber < arguments.size(); argumentNumber++) 
      if (isArgumentADouble(argumentNumber, arguments)) return false;
    return true;
  } // isLongMostPreciseArgument

  public static boolean areAllArgumentsBooleans(List<Argument> arguments) throws BuiltInException
  {
    for (int argumentNumber = 0; argumentNumber < arguments.size(); argumentNumber++) 
      if (!isArgumentABoolean(argumentNumber, arguments)) return false;
    return true;
  } // areAllArgumentsBooleans

  public static boolean areAllArgumentLiterals(List<Argument> arguments) throws BuiltInException
  {
    for (int argumentNumber = 0; argumentNumber < arguments.size(); argumentNumber++) 
      if (!isArgumentALiteral(argumentNumber, arguments)) return false;
    return true;
  } // areAllArgumentsIntegers

  public static boolean areAllArgumentsNumeric(List<Argument> arguments) throws BuiltInException
  {
    for (int argumentNumber = 0; argumentNumber < arguments.size(); argumentNumber++) 
      if (!isArgumentNumeric(argumentNumber, arguments)) return false;
    return true;
  } // areAllArgumentsNumeric

  public static boolean areAllArgumentsStrings(List<Argument> arguments) throws BuiltInException
  {
    for (int argumentNumber = 0; argumentNumber < arguments.size(); argumentNumber++) 
      if (!isArgumentAString(argumentNumber, arguments)) return false;
    return true;
  } // areAllArgumentsStrings

  public static boolean areAllArgumentsOfAnOrderedType(List<Argument> arguments) throws BuiltInException
  {
    for (int argumentNumber = 0; argumentNumber < arguments.size(); argumentNumber++) 
      if (!isArgumentOfAnOrderedType(argumentNumber, arguments)) return false;
    return true;
  } // areAllArgumentsOfAnOrderedType

  public static void checkThatAllArgumentsAreFloats(List<Argument> arguments) throws BuiltInException
  {
    for (int argumentNumber = 0; argumentNumber < arguments.size(); argumentNumber++) 
      checkThatArgumentIsAFloat(argumentNumber, arguments);
  } // checkThatAllArgumentsAreFloats

  public static void checkThatAllArgumentsAreStrings(List<Argument> arguments) throws BuiltInException
  {
    for (int argumentNumber = 0; argumentNumber < arguments.size(); argumentNumber++) 
      checkThatArgumentIsAString(argumentNumber, arguments);
  } // checkThatAllArgumentsAreStrings

  public static void checkThatAllArgumentsAreOfAnOrderedType(List<Argument> arguments) throws BuiltInException
  {
    for (int argumentNumber = 0; argumentNumber < arguments.size(); argumentNumber++) 
      checkThatArgumentIsOfAnOrderedType(argumentNumber, arguments);
  } // checkThatAllArgumentsAreOfAnOrderedType

  public static void checkThatArgumentIsALiteral(int argumentNumber, List<Argument> arguments) throws BuiltInException
  {
    checkArgumentNumber(argumentNumber, arguments);

    if (!(arguments.get(argumentNumber) instanceof LiteralArgument)) 
      throw new InvalidBuiltInArgumentException(argumentNumber,
                                                makeInvalidArgumentTypeMessage(arguments.get(argumentNumber), "literal"));
  } // checkThatArgumentIsALiteral

  public static void checkThatArgumentIsNumeric(int argumentNumber, List<Argument> arguments) throws BuiltInException
  {
    if (!isArgumentNumeric(argumentNumber, arguments))
      throw new InvalidBuiltInArgumentException(argumentNumber,
                                                makeInvalidArgumentTypeMessage(arguments.get(argumentNumber), "numeric"));
  } // checkThatArgumentIsNumeric

  public static void checkThatArgumentIsOfAnOrderedType(int argumentNumber, List<Argument> arguments) throws BuiltInException
  {
    if (!isArgumentOfAnOrderedType(argumentNumber, arguments))
      throw new InvalidBuiltInArgumentException(argumentNumber,
                                                makeInvalidArgumentTypeMessage(arguments.get(argumentNumber), "ordered type"));
  } // checkThatArgumentIsOfAnOrderedType

  public static boolean isArgumentOfAnOrderedType(int argumentNumber, List<Argument> arguments) throws BuiltInException
  {
    return (isArgumentNumeric(argumentNumber, arguments) || isArgumentAString(argumentNumber, arguments));
  } // isArgumentOfAnOrderedType

  public static boolean isArgumentAnIndividual(int argumentNumber, List<Argument> arguments) throws BuiltInException
  {
    checkArgumentNumber(argumentNumber, arguments);

    return arguments.get(argumentNumber) instanceof IndividualArgument;
  } // isArgumentAnIndividual

  public static void checkThatArgumentIsAnIndividual(int argumentNumber, List<Argument> arguments) throws BuiltInException
  {
    if (!isArgumentAnIndividual(argumentNumber, arguments)) {
      throw new InvalidBuiltInArgumentException(argumentNumber, 
                                                makeInvalidArgumentTypeMessage(arguments.get(argumentNumber), "individual"));
    } // if
  } // checkThatArgumentIsAnIndividual

  public static String getArgumentAsAnIndividualName(int argumentNumber, List<Argument> arguments) throws BuiltInException
  {
    checkThatArgumentIsAnIndividual(argumentNumber, arguments);

    return ((IndividualInfo)arguments.get(argumentNumber)).getIndividualName();
  } // getArgumentAsAnIndividualName

  public static String getArgumentAsAClassName(int argumentNumber, List<Argument> arguments) throws BuiltInException
  {
    checkThatArgumentIsAClass(argumentNumber, arguments);

    return ((ClassInfo)arguments.get(argumentNumber)).getClassName();
  } // getArgumentAsAClassName

  public static String getArgumentAsAResourceName(int argumentNumber, List<Argument> arguments) throws BuiltInException
  {
    String resourceName = "";

    checkThatArgumentIsAClassPropertyOrIndividual(argumentNumber, arguments);

    if (isArgumentAClass(argumentNumber, arguments)) resourceName = ((ClassInfo)arguments.get(argumentNumber)).getClassName();
    else if (isArgumentAProperty(argumentNumber, arguments)) resourceName = ((PropertyInfo)arguments.get(argumentNumber)).getPropertyName();
    else if (isArgumentAnIndividual(argumentNumber, arguments)) resourceName = ((IndividualInfo)arguments.get(argumentNumber)).getIndividualName();

    return resourceName;
  } // getArgumentAsAResourceName

  public static String getArgumentAsAPropertyName(int argumentNumber, List<Argument> arguments) throws BuiltInException
  {
    checkThatArgumentIsAProperty(argumentNumber, arguments);

    return ((PropertyInfo)arguments.get(argumentNumber)).getPropertyName();
  } // getArgumentAsAPropertyName

  public static void checkArgumentNumber(int argumentNumber, List<Argument> arguments) throws BuiltInException
  {
    if ((argumentNumber < 0) || (argumentNumber >= arguments.size()))
      throw new BuiltInException("argument number #" + argumentNumber + " out of bounds");
  } // checkArgumentNumber

  public static boolean isArgumentNumeric(int argumentNumber, List<Argument> arguments) throws BuiltInException
  {
    if (isArgumentALiteral(argumentNumber, arguments)) return getArgumentAsALiteral(argumentNumber, arguments).isNumeric();
    else return false;
  } // isArgumentNumeric

  public static boolean isArgumentNonNumeric(int argumentNumber, List<Argument> arguments) throws BuiltInException
  {
    if (isArgumentALiteral(argumentNumber, arguments))
      return !getArgumentAsALiteral(argumentNumber, arguments).isNumeric();
    else return false;
  } // isArgumentNonNumeric

  public static void checkThatArgumentIsNonNumeric(int argumentNumber, List<Argument> arguments) throws BuiltInException
  {
    if (!isArgumentNonNumeric(argumentNumber, arguments))
      throw new InvalidBuiltInArgumentException(argumentNumber, 
                                                makeInvalidArgumentTypeMessage(arguments.get(argumentNumber), "non-numeric"));
  } // checkThatArgumentIsNonNumeric

  // Integers
  
  public static void checkThatArgumentIsAnInteger(int argumentNumber, List<Argument> arguments) throws BuiltInException
  {
    if (!isArgumentAnInteger(argumentNumber, arguments))
      throw new InvalidBuiltInArgumentException(argumentNumber,
                                                makeInvalidArgumentTypeMessage(arguments.get(argumentNumber), "integer"));
  } // checkThatArgumentIsAnInteger

  public static boolean isArgumentAnInteger(int argumentNumber, List<Argument> arguments) throws BuiltInException
  {
    if (isArgumentALiteral(argumentNumber, arguments)) 
      return (getArgumentAsALiteral(argumentNumber, arguments).isInteger());
    else return false;
  } // isArgumentAnInteger

  public static int getArgumentAsAnInteger(int argumentNumber, List<Argument> arguments) throws BuiltInException
  {
    return getArgumentAsALiteral(argumentNumber, arguments).getInt(); // Will throw DatatypeConversionException if invalid.
  } // getArgumentAsAnInteger

  // Shorts

  public static boolean isArgumentAShort(int argumentNumber, List<Argument> arguments) throws BuiltInException
  {
    if (isArgumentALiteral(argumentNumber, arguments)) 
      return (getArgumentAsALiteral(argumentNumber, arguments).isShort());
    else return false;
  } // isArgumentAShort

  public static int getArgumentAsAShort(int argumentNumber, List<Argument> arguments) throws BuiltInException
  {
    return getArgumentAsALiteral(argumentNumber, arguments).getInt(); // Will throw DatatypeConversionException if invalid.
  } // getArgumentAsAShort

  // BigDecimal

  public static BigDecimal getArgumentAsABigDecimal(int argumentNumber, List<Argument> arguments) throws BuiltInException
  {
    return getArgumentAsALiteral(argumentNumber, arguments).getBigDecimal(); // Will throw DatatypeConversionException if invalid.
  } // getArgumentAsABigDecimal

  // BigInteger

  public static BigInteger getArgumentAsABigInteger(int argumentNumber, List<Argument> arguments) throws BuiltInException
  {
    return getArgumentAsALiteral(argumentNumber, arguments).getBigInteger(); // Will throw DatatypeConversionException if invalid.
  } // getArgumentAsABigInteger

  public static boolean isArgumentALiteral(int argumentNumber, List<Argument> arguments) throws BuiltInException
  {
    checkThatArgumentIsBound(argumentNumber, arguments);

    return (arguments.get(argumentNumber) instanceof LiteralArgument);
  } // isArgumentALiteral

  public static boolean isArgumentAProperty(int argumentNumber, List<Argument> arguments) throws BuiltInException
  {
    checkThatArgumentIsBound(argumentNumber, arguments);

    return (arguments.get(argumentNumber) instanceof PropertyArgument);
  } // isArgumentAProperty

  public static void checkThatArgumentIsAProperty(int argumentNumber, List<Argument> arguments) throws BuiltInException
  {
    if (!isArgumentAProperty(argumentNumber, arguments))
      throw new InvalidBuiltInArgumentException(argumentNumber,
                                                makeInvalidArgumentTypeMessage(arguments.get(argumentNumber), "property"));
  } // checkThatArgumentIsAProperty

  public static void checkThatArgumentIsAClassPropertyOrIndividual(int argumentNumber, List<Argument> arguments) throws BuiltInException
  {
    if (!isArgumentAClassPropertyOrIndividual(argumentNumber, arguments))
      throw new InvalidBuiltInArgumentException(argumentNumber,
                                                makeInvalidArgumentTypeMessage(arguments.get(argumentNumber), "class, property, or individual"));
  } // checkThatArgumentIsAClassPropertyOrIndividual

  public static boolean isArgumentAClassPropertyOrIndividual(int argumentNumber, List<Argument> arguments) throws BuiltInException
  {
    return isArgumentAClass(argumentNumber, arguments) || isArgumentAProperty(argumentNumber, arguments) ||
           isArgumentAnIndividual(argumentNumber, arguments);
  } // isArgumentAClassPropertyOrIndividual

  public static boolean isArgumentAClass(int argumentNumber, List<Argument> arguments) throws BuiltInException
  {
    checkThatArgumentIsBound(argumentNumber, arguments);

    return (arguments.get(argumentNumber) instanceof ClassArgument);
  } // isArgumentAClass

  public static void checkThatArgumentIsAClass(int argumentNumber, List<Argument> arguments) throws BuiltInException
  {
    if (!isArgumentAClass(argumentNumber, arguments))
      throw new InvalidBuiltInArgumentException(argumentNumber,
                                                makeInvalidArgumentTypeMessage(arguments.get(argumentNumber), "class"));
  } // checkThatArgumentIsAClass

  public static LiteralInfo getArgumentAsALiteral(int argumentNumber, List<Argument> arguments) throws BuiltInException
  {
    checkThatArgumentIsALiteral(argumentNumber, arguments);

    return (LiteralInfo)arguments.get(argumentNumber);
  } // getArgumentAsALiteral

  // Longs

  public static void checkThatArgumentIsALong(int argumentNumber, List<Argument> arguments) throws BuiltInException
  {
    if (!isArgumentALong(argumentNumber, arguments))
      throw new InvalidBuiltInArgumentException(argumentNumber, 
                                                makeInvalidArgumentTypeMessage(arguments.get(argumentNumber), "long"));
  } // checkThatArgumentIsALong

  public static boolean isArgumentALong(int argumentNumber, List<Argument> arguments) throws BuiltInException
  {
    if (isArgumentALiteral(argumentNumber, arguments)) 
      return (getArgumentAsALiteral(argumentNumber, arguments).isLong());
    else return false;
  } // isArgumentALong

  public static long getArgumentAsALong(int argumentNumber, List<Argument> arguments) throws BuiltInException
  {
    return getArgumentAsALiteral(argumentNumber, arguments).getLong(); // Will throw DatatypeConversionException if invalid.
  } // getArgumentAsALong

  // Floats

  public static void checkThatArgumentIsAFloat(int argumentNumber, List<Argument> arguments) throws BuiltInException
  {
    if (!isArgumentAFloat(argumentNumber, arguments))
      throw new InvalidBuiltInArgumentException(argumentNumber,
                                                makeInvalidArgumentTypeMessage(arguments.get(argumentNumber), "float"));
  } // checkThatArgumentIsAFloat

  public static boolean isArgumentAFloat(int argumentNumber, List<Argument> arguments) throws BuiltInException
  {
    if (isArgumentALiteral(argumentNumber, arguments)) 
      return (getArgumentAsALiteral(argumentNumber, arguments).isFloat());
    else return false;
  } // isArgumentAFloat

  public static float getArgumentAsAFloat(int argumentNumber, List<Argument> arguments) throws BuiltInException
  {
    return getArgumentAsALiteral(argumentNumber, arguments).getFloat(); // Will throw DatatypeConversionException if invalid.
  } // getArgumentAsAFloat

  // Double

  public static void checkThatArgumentIsADouble(int argumentNumber, List<Argument> arguments) throws BuiltInException
  {
    if (!isArgumentADouble(argumentNumber, arguments))
      throw new InvalidBuiltInArgumentException(argumentNumber, 
                                                makeInvalidArgumentTypeMessage(arguments.get(argumentNumber), "double"));
  } // checkThatArgumentIsADouble

  public static boolean isArgumentADouble(int argumentNumber, List<Argument> arguments) throws BuiltInException
  {
    if (isArgumentALiteral(argumentNumber, arguments)) 
      return (getArgumentAsALiteral(argumentNumber, arguments).isDouble());
    else return false;
  } // isArgumentADouble

  public static double getArgumentAsADouble(int argumentNumber, List<Argument> arguments) throws BuiltInException
  {
    return getArgumentAsALiteral(argumentNumber, arguments).getDouble(); // Will throw DatatypeConversionException if invalid.
  } // getArgumentAsADouble

  // Booleans

  public static void checkThatArgumentIsABoolean(int argumentNumber, List<Argument> arguments) throws BuiltInException
  {
    if (!isArgumentABoolean(argumentNumber, arguments))
      throw new InvalidBuiltInArgumentException(argumentNumber,
                                                makeInvalidArgumentTypeMessage(arguments.get(argumentNumber), "boolean"));
  } // checkThatArgumentIsABoolean

  public static boolean isArgumentABoolean(int argumentNumber, List<Argument> arguments) throws BuiltInException
  {
    if (isArgumentALiteral(argumentNumber, arguments)) 
      return (getArgumentAsALiteral(argumentNumber, arguments).isBoolean());
    else return false;
  } // isArgumentABoolean

  public static boolean getArgumentAsABoolean(int argumentNumber, List<Argument> arguments) throws BuiltInException
  {
    checkThatArgumentIsABoolean(argumentNumber, arguments);

    return getArgumentAsALiteral(argumentNumber, arguments).getBoolean();
  } // getArgumentAsABoolean

  // Strings

  public static void checkThatArgumentIsAString(int argumentNumber, List<Argument> arguments) throws BuiltInException
  {
    if (!isArgumentAString(argumentNumber, arguments))
      throw new InvalidBuiltInArgumentException(argumentNumber, 
                                                makeInvalidArgumentTypeMessage(arguments.get(argumentNumber), "string"));
  } // checkThatArgumentIsAString

  public static boolean isArgumentAString(int argumentNumber, List<Argument> arguments) throws BuiltInException
  {
    if (isArgumentALiteral(argumentNumber, arguments)) 
      return getArgumentAsALiteral(argumentNumber, arguments).isString();
    else return false;
  } // isArgumentAString

  public static String getArgumentAsAString(int argumentNumber, List<Argument> arguments) throws BuiltInException
  {
    checkThatArgumentIsAString(argumentNumber, arguments);

    return getArgumentAsALiteral(argumentNumber, arguments).getString();
  } // getArgumentAsAString

  // Unbound argument processing methods.

  public static boolean hasUnboundArguments(List<Argument> arguments) throws BuiltInException
  {
    return !arguments.isEmpty() && arguments.contains(null); // An argument is unbound if its value is null.
  } // hasUnboundArguments

  public static void checkThatAllArgumentsAreBound(List<Argument> arguments) throws BuiltInException
  {
    if (hasUnboundArguments(arguments)) throw new BuiltInException("all arguments must be bound");
  } // checkThatAllArgumentsAreBound

  public static void checkThatArgumentIsBound(int argumentNumber, List<Argument> arguments) throws BuiltInException
  {
    if (isUnboundArgument(argumentNumber, arguments)) 
      throw new BuiltInException("not expecting an unbound argument for argument #" + argumentNumber);
  } // checkThatArgumentIsBound

  public static boolean isUnboundArgument(int argumentNumber, List<Argument> arguments)
  {
    return (argumentNumber >= 0) && (argumentNumber < arguments.size()) && (arguments.get(argumentNumber) == null);
  } // isUnboundArgument

  /**
   ** Get 0-offset position of first unbound argument; return -1 if no unbound arguments are found.
   */
  public static int getFirstUnboundArgument(List<Argument> arguments) throws BuiltInException
  {
    if (hasUnboundArguments(arguments)) return arguments.indexOf(null);
    else return -1;
  } // getFirstUnboundArgument

  public static void checkForUnboundArguments(List<Argument> arguments) throws BuiltInException
  {
    if (hasUnboundArguments(arguments))
      throw new BuiltInException("built-in does not support variable binding - unbound variable used for argument #" +
                                 getFirstUnboundArgument(arguments));
  } // checkForUnboundArguments

  public static void checkForUnboundNonFirstArguments(List<Argument> arguments) throws BuiltInException
  {
    if (hasUnboundArguments(arguments.subList(1, arguments.size())))
      throw new BuiltInException("built-in supports variable binding only for the first argument - " +
                                 "unbound variable used in other arguments");
  } // checkForUnboundArguments

  private static String makeInvalidArgumentTypeMessage(Argument argument, String expectedTypeName)
  {
    String message = "expecting " + expectedTypeName + ", got ";
    if (argument == null) message += "unbound argument";
    else {
      if (argument instanceof ClassArgument) {
        ClassInfo classInfo = (ClassInfo)argument;
        message += "class with name '" + classInfo.getClassName() + "'";
      } else if (argument instanceof PropertyArgument) {
        PropertyInfo propertyInfo = (PropertyInfo)argument;
        message += "property with name '" + propertyInfo.getPropertyName() + "'";
      } else if (argument instanceof IndividualArgument) {
        IndividualInfo individualInfo = (IndividualInfo)argument;
        message += "individual with name '" + individualInfo.getIndividualName() + "'";
      } else if (argument instanceof LiteralArgument) {
        LiteralInfo literalInfo = (LiteralInfo)argument;
        message += "literal with value '" + literalInfo.toString() + "' of type '" + literalInfo.getValueClassName() + "'";
      } else message += "unknown type '" + argument.getClass().getName() + "'";
    } // if
    return message;
  } // makeInvalidArgumentTypeMessage

  /**
   ** Take an bound Argument object with types ClassArgument, PropertyArgument, IndividualArgument, or LiteralArgument and return it as a
   ** property value representation. Class, property and individual argument are represented by strings containing their class, property or
   ** individual names, respectively; literal objects are represented by the appropriate Java type. Primitive XSD datatypes that do not have
   ** a corresponding Java type are not yet supported.
   */
  public static Object getArgumentAsAPropertyValue(int argumentNumber, List<Argument> arguments) throws BuiltInException
  {
    Argument argument;
    Object result = null;

    SWRLBuiltInUtil.checkThatArgumentIsBound(argumentNumber, arguments);

    argument = arguments.get(argumentNumber);

    if (argument instanceof ClassArgument) {
      ClassInfo classInfo = (ClassInfo)argument;
      result = classInfo.getClassName();
    } else if (argument instanceof PropertyArgument) {
      PropertyInfo propertyInfo = (PropertyInfo)argument;
      result = propertyInfo.getPropertyName();
    } else if (argument instanceof IndividualArgument) {
      IndividualInfo individualInfo = (IndividualInfo)argument;
      result = individualInfo.getIndividualName();
    } else if (argument instanceof LiteralArgument) {
      LiteralInfo literalInfo = (LiteralInfo)argument;
      if (literalInfo.isNumeric()) result = literalInfo.getNumber();
      else if (literalInfo.isString()) result = literalInfo.getString();
      else throw new BuiltInException("LiteralInfo of type '" + literalInfo.getValueClassName() + "' with value '" + 
                                      literalInfo.toString() + "' not supported - only strings and number literals supported");
    } else throw new BuiltInException("Argument '" + argument + "' of unknown type '" + argument.getClass().getName() + "'");

    return result;
  } // getArgumentAsAPropertyValue

  /**
   ** Create a string that represents a unique invocation pattern for a built-in for a bridge/rule/built-in/argument combination.  
   */
  public static String createInvocationPattern(SWRLRuleEngineBridge invokingBridge, String invokingRuleName, int invokingBuiltInIndex,
                                               List<Argument> arguments) throws BuiltInException
  {
    String pattern = "" + invokingBridge.hashCode() + "." + invokingRuleName + "." + invokingBuiltInIndex;

    for (int i = 0; i < arguments.size(); i++) pattern += "." + getArgumentAsAPropertyValue(i, arguments);

    return pattern;
  } // createInvocationPattern

} // SWRLBuiltInUtil
