
package edu.stanford.smi.protegex.owl.swrl.bridge.builtins;

import edu.stanford.smi.protegex.owl.swrl.bridge.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;

import edu.stanford.smi.protegex.owl.swrl.bridge.impl.*;

import java.util.*;
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

  public static void checkThatAllArgumentsAreLiterals(List<BuiltInArgument> arguments) throws BuiltInException
  {
    for (int argumentNumber = 0; argumentNumber < arguments.size(); argumentNumber++) 
      checkThatArgumentIsALiteral(argumentNumber, arguments);
  } // checkThatAllArgumentsAreLiterals

  public static void checkThatAllArgumentsAreNumeric(List<BuiltInArgument> arguments) throws BuiltInException
  {
    for (int argumentNumber = 0; argumentNumber < arguments.size(); argumentNumber++) 
      checkThatArgumentIsNumeric(argumentNumber, arguments);
  } // checkThatAllArgumentsAreNumeric

  public static void checkThatAllArgumentsAreIntegers(List<BuiltInArgument> arguments) throws BuiltInException
  {
    for (int argumentNumber = 0; argumentNumber < arguments.size(); argumentNumber++) 
      checkThatArgumentIsAnInteger(argumentNumber, arguments);
  } // checkThatAllArgumentsAreIntegers

  public static boolean areAllArgumentsShorts(List<BuiltInArgument> arguments) throws BuiltInException
  {
    for (int argumentNumber = 0; argumentNumber < arguments.size(); argumentNumber++) 
      if (!isArgumentAShort(argumentNumber, arguments)) return false;
    return true;
  } // areAllArgumentsShorts

  public static boolean areAllArgumentsIntegers(List<BuiltInArgument> arguments) throws BuiltInException
  {
    for (int argumentNumber = 0; argumentNumber < arguments.size(); argumentNumber++) 
      if (!isArgumentAnInteger(argumentNumber, arguments)) return false;
    return true;
  } // areAllArgumentsIntegers

  public static boolean areAllArgumentsLongs(List<BuiltInArgument> arguments) throws BuiltInException
  {
    for (int argumentNumber = 0; argumentNumber < arguments.size(); argumentNumber++) 
      if (!isArgumentALong(argumentNumber, arguments)) return false;
    return true;
  } // areAllArgumentsLongs

  public static boolean areAllArgumentsFloats(List<BuiltInArgument> arguments) throws BuiltInException
  {
    for (int argumentNumber = 0; argumentNumber < arguments.size(); argumentNumber++) 
      if (!isArgumentAFloat(argumentNumber, arguments)) return false;
    return true;
  } // areAllArgumentsFloats

  public static boolean areAllArgumentsDoubles(List<BuiltInArgument> arguments) throws BuiltInException
  {
    for (int argumentNumber = 0; argumentNumber < arguments.size(); argumentNumber++) 
      if (!isArgumentADouble(argumentNumber, arguments)) return false;
    return true;
  } // areAllArgumentsDoubles

  public static boolean isArgumentConvertableToDouble(int argumentNumber, List<BuiltInArgument> arguments) throws BuiltInException
  {
    return (isArgumentNumeric(argumentNumber, arguments));
  } // isArgumentConvertableToDouble

  public static boolean isArgumentConvertableToFloat(int argumentNumber, List<BuiltInArgument> arguments) throws BuiltInException
  {
    return (isArgumentNumeric(argumentNumber, arguments) && isArgumentAShort(argumentNumber, arguments) &&
            isArgumentAnInteger(argumentNumber, arguments) && isArgumentALong(argumentNumber, arguments) &&
            isArgumentAFloat(argumentNumber, arguments));
  } // isArgumentConvertableToFloat

  public static boolean isArgumentConvertableToLong(int argumentNumber, List<BuiltInArgument> arguments) throws BuiltInException
  {
    return (isArgumentNumeric(argumentNumber, arguments) && isArgumentAShort(argumentNumber, arguments) &&
            isArgumentAnInteger(argumentNumber, arguments) && isArgumentALong(argumentNumber, arguments));
  } // isArgumentConvertableToLong

  public static boolean isArgumentConvertableToInteger(int argumentNumber, List<BuiltInArgument> arguments) throws BuiltInException
  {
    return (isArgumentNumeric(argumentNumber, arguments) && isArgumentAShort(argumentNumber, arguments) &&
            isArgumentAnInteger(argumentNumber, arguments));
  } // isArgumentConvertableToInteger

  public static boolean isArgumentConvertableToShort(int argumentNumber, List<BuiltInArgument> arguments) throws BuiltInException
  {
    return (isArgumentNumeric(argumentNumber, arguments) && isArgumentAShort(argumentNumber, arguments));
  } // isArgumentConvertableToShort

  public static boolean isShortMostPreciseArgument(List<BuiltInArgument> arguments) throws BuiltInException
  {
    for (int argumentNumber = 0; argumentNumber < arguments.size(); argumentNumber++) 
      if (isArgumentAnInteger(argumentNumber, arguments) || isArgumentALong(argumentNumber, arguments) || 
          isArgumentAFloat(argumentNumber, arguments) || isArgumentADouble(argumentNumber, arguments)) return false;
    return true;
  } // isShortMostPreciseArgument

  public static boolean isIntegerMostPreciseArgument(List<BuiltInArgument> arguments) throws BuiltInException
  {
    for (int argumentNumber = 0; argumentNumber < arguments.size(); argumentNumber++) 
      if (isArgumentALong(argumentNumber, arguments) || isArgumentAFloat(argumentNumber, arguments) ||
          isArgumentADouble(argumentNumber, arguments)) return false;
    return true;
  } // isIntegerMostPreciseArgument

  public static boolean isLongMostPreciseArgument(List<BuiltInArgument> arguments) throws BuiltInException
  {
    for (int argumentNumber = 0; argumentNumber < arguments.size(); argumentNumber++) 
      if (isArgumentADouble(argumentNumber, arguments)|| isArgumentAFloat(argumentNumber, arguments)) return false;
    return true;
  } // isLongMostPreciseArgument

  public static boolean isFloatMostPreciseArgument(List<BuiltInArgument> arguments) throws BuiltInException
  {
    for (int argumentNumber = 0; argumentNumber < arguments.size(); argumentNumber++) 
      if (isArgumentADouble(argumentNumber, arguments)) return false;
    return true;
  } // isFloatMostPreciseArgument

  public static boolean areAllArgumentsBooleans(List<BuiltInArgument> arguments) throws BuiltInException
  {
    for (int argumentNumber = 0; argumentNumber < arguments.size(); argumentNumber++) 
      if (!isArgumentABoolean(argumentNumber, arguments)) return false;
    return true;
  } // areAllArgumentsBooleans

  public static boolean areAllArgumentLiterals(List<BuiltInArgument> arguments) throws BuiltInException
  {
    for (int argumentNumber = 0; argumentNumber < arguments.size(); argumentNumber++) 
      if (!isArgumentALiteral(argumentNumber, arguments)) return false;
    return true;
  } // areAllArgumentsIntegers

  public static boolean areAllArgumentsNumeric(List<BuiltInArgument> arguments) throws BuiltInException
  {
    for (int argumentNumber = 0; argumentNumber < arguments.size(); argumentNumber++) 
      if (!isArgumentNumeric(argumentNumber, arguments)) return false;
    return true;
  } // areAllArgumentsNumeric

  public static boolean areAllArgumentsStrings(List<BuiltInArgument> arguments) throws BuiltInException
  {
    for (int argumentNumber = 0; argumentNumber < arguments.size(); argumentNumber++) 
      if (!isArgumentAString(argumentNumber, arguments)) return false;
    return true;
  } // areAllArgumentsStrings

  public static boolean areAllArgumentsOfAnOrderedType(List<BuiltInArgument> arguments) throws BuiltInException
  {
    for (int argumentNumber = 0; argumentNumber < arguments.size(); argumentNumber++) 
      if (!isArgumentOfAnOrderedType(argumentNumber, arguments)) return false;
    return true;
  } // areAllArgumentsOfAnOrderedType

  public static void checkThatAllArgumentsAreFloats(List<BuiltInArgument> arguments) throws BuiltInException
  {
    for (int argumentNumber = 0; argumentNumber < arguments.size(); argumentNumber++) 
      checkThatArgumentIsAFloat(argumentNumber, arguments);
  } // checkThatAllArgumentsAreFloats

  public static void checkThatAllArgumentsAreStrings(List<BuiltInArgument> arguments) throws BuiltInException
  {
    for (int argumentNumber = 0; argumentNumber < arguments.size(); argumentNumber++) 
      checkThatArgumentIsAString(argumentNumber, arguments);
  } // checkThatAllArgumentsAreStrings

  public static void checkThatAllArgumentsAreOfAnOrderedType(List<BuiltInArgument> arguments) throws BuiltInException
  {
    for (int argumentNumber = 0; argumentNumber < arguments.size(); argumentNumber++) 
      checkThatArgumentIsOfAnOrderedType(argumentNumber, arguments);
  } // checkThatAllArgumentsAreOfAnOrderedType

  public static void checkThatArgumentIsALiteral(int argumentNumber, List<BuiltInArgument> arguments) throws BuiltInException
  {
    checkArgumentNumber(argumentNumber, arguments);

    if (!(arguments.get(argumentNumber) instanceof DatatypeValueArgument)) 
      throw new InvalidBuiltInArgumentException(argumentNumber,
                                                makeInvalidArgumentTypeMessage(arguments.get(argumentNumber), "literal"));
  } // checkThatArgumentIsALiteral

  public static void checkThatArgumentIsALiteral(BuiltInArgument argument) throws BuiltInException
  {

    if (!(argument instanceof DatatypeValueArgument)) throw new InvalidBuiltInArgumentException(makeInvalidArgumentTypeMessage(argument, "literal"));
  } // checkThatArgumentIsALiteral

  public static void checkThatArgumentIsNumeric(int argumentNumber, List<BuiltInArgument> arguments) throws BuiltInException
  {
    if (!isArgumentNumeric(argumentNumber, arguments))
      throw new InvalidBuiltInArgumentException(argumentNumber,
                                                makeInvalidArgumentTypeMessage(arguments.get(argumentNumber), "numeric"));
  } // checkThatArgumentIsNumeric

  public static void checkThatArgumentIsOfAnOrderedType(int argumentNumber, List<BuiltInArgument> arguments) throws BuiltInException
  {
    if (!isArgumentOfAnOrderedType(argumentNumber, arguments))
      throw new InvalidBuiltInArgumentException(argumentNumber,
                                                makeInvalidArgumentTypeMessage(arguments.get(argumentNumber), "ordered type"));
  } // checkThatArgumentIsOfAnOrderedType

  public static boolean isArgumentOfAnOrderedType(int argumentNumber, List<BuiltInArgument> arguments) throws BuiltInException
  {
    return (isArgumentNumeric(argumentNumber, arguments) || isArgumentAString(argumentNumber, arguments));
  } // isArgumentOfAnOrderedType

  public static boolean isArgumentAnIndividual(int argumentNumber, List<BuiltInArgument> arguments) throws BuiltInException
  {
    checkArgumentNumber(argumentNumber, arguments);

    return arguments.get(argumentNumber) instanceof IndividualArgument;
  } // isArgumentAnIndividual

  public static boolean isArgumentADatatypeValue(int argumentNumber, List<BuiltInArgument> arguments) throws BuiltInException
  {
    checkArgumentNumber(argumentNumber, arguments);

    return arguments.get(argumentNumber) instanceof DatatypeValueArgument;
  } // isArgumentADatatypeValue

  public static void checkThatArgumentIsAnIndividual(int argumentNumber, List<BuiltInArgument> arguments) throws BuiltInException
  {
    if (!isArgumentAnIndividual(argumentNumber, arguments)) {
      throw new InvalidBuiltInArgumentException(argumentNumber, 
                                                makeInvalidArgumentTypeMessage(arguments.get(argumentNumber), "individual"));
    } // if
  } // checkThatArgumentIsAnIndividual

  public static void checkThatArgumentIsAnOWLDatatypeValue(int argumentNumber, List<BuiltInArgument> arguments) throws BuiltInException
  {
    if (!isArgumentADatatypeValue(argumentNumber, arguments)) {
      throw new InvalidBuiltInArgumentException(argumentNumber, 
                                                makeInvalidArgumentTypeMessage(arguments.get(argumentNumber), "datatype value"));
    } // if
  } // checkThatArgumentIsAnOWLDatatypeValue

  public static String getArgumentAsAnIndividualName(int argumentNumber, List<BuiltInArgument> arguments) throws BuiltInException
  {
    checkThatArgumentIsAnIndividual(argumentNumber, arguments);

    return ((IndividualArgument)arguments.get(argumentNumber)).getIndividualName();
  } // getArgumentAsAnIndividualName

  public static OWLIndividual getArgumentAsAnOWLIndividual(int argumentNumber, List<BuiltInArgument> arguments) throws BuiltInException
  {
    checkThatArgumentIsAnIndividual(argumentNumber, arguments);

    return (OWLIndividual)arguments.get(argumentNumber);
  } // getArgumentAsAnIndividualName

  public static String getArgumentAsAClassName(int argumentNumber, List<BuiltInArgument> arguments) throws BuiltInException
  {
    checkThatArgumentIsAClass(argumentNumber, arguments);

    return ((ClassArgument)arguments.get(argumentNumber)).getClassName();
  } // getArgumentAsAClassName

  public static OWLClass getArgumentAsAnOWLClass(int argumentNumber, List<BuiltInArgument> arguments) throws BuiltInException
  {
    checkThatArgumentIsAClass(argumentNumber, arguments);

    return (OWLClass)arguments.get(argumentNumber);
  } // getArgumentAsAnOWLClass

  public static OWLProperty getArgumentAsAnOWLProperty(int argumentNumber, List<BuiltInArgument> arguments) throws BuiltInException
  {
    checkThatArgumentIsAProperty(argumentNumber, arguments);

    return (OWLProperty)arguments.get(argumentNumber);
  } // getArgumentAsAnOWLProperty

  public static OWLDatatypeValue getArgumentAsAnOWLDatatypeValue(int argumentNumber, List<BuiltInArgument> arguments) throws BuiltInException
  {
    checkThatArgumentIsAnOWLDatatypeValue(argumentNumber, arguments);

    return (OWLDatatypeValue)arguments.get(argumentNumber);
  } // getArgumentAsAnOWLDatatypeValue

  public static String getArgumentAsAResourceName(int argumentNumber, List<BuiltInArgument> arguments) throws BuiltInException
  {
    String resourceName = "";

    checkThatArgumentIsAClassPropertyOrIndividual(argumentNumber, arguments);

    if (isArgumentAClass(argumentNumber, arguments)) resourceName = ((ClassArgument)arguments.get(argumentNumber)).getClassName();
    else if (isArgumentAProperty(argumentNumber, arguments)) resourceName = ((PropertyArgument)arguments.get(argumentNumber)).getPropertyName();
    else if (isArgumentAnIndividual(argumentNumber, arguments)) resourceName = ((IndividualArgument)arguments.get(argumentNumber)).getIndividualName();

    return resourceName;
  } // getArgumentAsAResourceName

  public static String getArgumentAsAPropertyName(int argumentNumber, List<BuiltInArgument> arguments) throws BuiltInException
  {
    checkThatArgumentIsAProperty(argumentNumber, arguments);

    return ((PropertyArgument)arguments.get(argumentNumber)).getPropertyName();
  } // getArgumentAsAPropertyName

  public static void checkArgumentNumber(int argumentNumber, List<BuiltInArgument> arguments) throws BuiltInException
  {
    if ((argumentNumber < 0) || (argumentNumber >= arguments.size()))
      throw new BuiltInException("(0-offset) argument number #" + argumentNumber + " is out of bounds");
  } // checkArgumentNumber

  public static boolean isArgumentNumeric(int argumentNumber, List<BuiltInArgument> arguments) throws BuiltInException
  {
    if (isArgumentALiteral(argumentNumber, arguments)) return getArgumentAsALiteral(argumentNumber, arguments).isNumeric();
    else return false;
  } // isArgumentNumeric

  public static boolean isArgumentNonNumeric(int argumentNumber, List<BuiltInArgument> arguments) throws BuiltInException
  {
    if (isArgumentALiteral(argumentNumber, arguments))
      return !getArgumentAsALiteral(argumentNumber, arguments).isNumeric();
    else return false;
  } // isArgumentNonNumeric

  public static void checkThatArgumentIsNonNumeric(int argumentNumber, List<BuiltInArgument> arguments) throws BuiltInException
  {
    if (!isArgumentNonNumeric(argumentNumber, arguments))
      throw new InvalidBuiltInArgumentException(argumentNumber, 
                                                makeInvalidArgumentTypeMessage(arguments.get(argumentNumber), "non-numeric"));
  } // checkThatArgumentIsNonNumeric

  // Integers
  
  public static void checkThatArgumentIsAnInteger(int argumentNumber, List<BuiltInArgument> arguments) throws BuiltInException
  {
    if (!isArgumentAnInteger(argumentNumber, arguments))
      throw new InvalidBuiltInArgumentException(argumentNumber,
                                                makeInvalidArgumentTypeMessage(arguments.get(argumentNumber), "integer"));
  } // checkThatArgumentIsAnInteger

  public static boolean isArgumentAnInteger(int argumentNumber, List<BuiltInArgument> arguments) throws BuiltInException
  {
    if (isArgumentALiteral(argumentNumber, arguments)) return (getArgumentAsALiteral(argumentNumber, arguments).isInteger());
    else return false;
  } // isArgumentAnInteger

  public static int getArgumentAsAnInteger(int argumentNumber, List<BuiltInArgument> arguments) throws BuiltInException
  {
    return getArgumentAsALiteral(argumentNumber, arguments).getInt(); // Will throw DatatypeConversionException if invalid.
  } // getArgumentAsAnInteger

  // Shorts

  public static boolean isArgumentAShort(int argumentNumber, List<BuiltInArgument> arguments) throws BuiltInException
  {
    if (isArgumentALiteral(argumentNumber, arguments)) 
      return (getArgumentAsALiteral(argumentNumber, arguments).isShort());
    else return false;
  } // isArgumentAShort

  public static short getArgumentAsAShort(int argumentNumber, List<BuiltInArgument> arguments) throws BuiltInException
  {
    return getArgumentAsALiteral(argumentNumber, arguments).getShort(); // Will throw DatatypeConversionException if invalid.
  } // getArgumentAsAShort

  // BigDecimal

  public static BigDecimal getArgumentAsABigDecimal(int argumentNumber, List<BuiltInArgument> arguments) throws BuiltInException
  {
    return getArgumentAsALiteral(argumentNumber, arguments).getBigDecimal(); // Will throw DatatypeConversionException if invalid.
  } // getArgumentAsABigDecimal

  // BigInteger

  public static BigInteger getArgumentAsABigInteger(int argumentNumber, List<BuiltInArgument> arguments) throws BuiltInException
  {
    return getArgumentAsALiteral(argumentNumber, arguments).getBigInteger(); // Will throw DatatypeConversionException if invalid.
  } // getArgumentAsABigInteger

  public static boolean isArgumentALiteral(int argumentNumber, List<BuiltInArgument> arguments) throws BuiltInException
  {
    checkThatArgumentIsBound(argumentNumber, arguments);

    return (arguments.get(argumentNumber) instanceof DatatypeValueArgument);
  } // isArgumentALiteral

  public static boolean isArgumentAProperty(int argumentNumber, List<BuiltInArgument> arguments) throws BuiltInException
  {
    checkThatArgumentIsBound(argumentNumber, arguments);

    return (arguments.get(argumentNumber) instanceof PropertyArgument);
  } // isArgumentAProperty

  public static void checkThatArgumentIsAProperty(int argumentNumber, List<BuiltInArgument> arguments) throws BuiltInException
  {
    if (!isArgumentAProperty(argumentNumber, arguments))
      throw new InvalidBuiltInArgumentException(argumentNumber,
                                                makeInvalidArgumentTypeMessage(arguments.get(argumentNumber), "property"));
  } // checkThatArgumentIsAProperty

  public static void checkThatArgumentIsAClassPropertyOrIndividual(int argumentNumber, List<BuiltInArgument> arguments) throws BuiltInException
  {
    if (!isArgumentAClassPropertyOrIndividual(argumentNumber, arguments))
      throw new InvalidBuiltInArgumentException(argumentNumber,
                                                makeInvalidArgumentTypeMessage(arguments.get(argumentNumber), "class, property, or individual"));
  } // checkThatArgumentIsAClassPropertyOrIndividual

  public static boolean isArgumentAClassPropertyOrIndividual(int argumentNumber, List<BuiltInArgument> arguments) throws BuiltInException
  {
    return isArgumentAClass(argumentNumber, arguments) || isArgumentAProperty(argumentNumber, arguments) ||
           isArgumentAnIndividual(argumentNumber, arguments);
  } // isArgumentAClassPropertyOrIndividual

  public static boolean isArgumentAClass(int argumentNumber, List<BuiltInArgument> arguments) throws BuiltInException
  {
    checkThatArgumentIsBound(argumentNumber, arguments);

    return (arguments.get(argumentNumber) instanceof ClassArgument);
  } // isArgumentAClass

  public static void checkThatArgumentIsAClass(int argumentNumber, List<BuiltInArgument> arguments) throws BuiltInException
  {
    if (!isArgumentAClass(argumentNumber, arguments))
      throw new InvalidBuiltInArgumentException(argumentNumber,
                                                makeInvalidArgumentTypeMessage(arguments.get(argumentNumber), "class"));
  } // checkThatArgumentIsAClass

  public static OWLDatatypeValue getArgumentAsALiteral(int argumentNumber, List<BuiltInArgument> arguments) throws BuiltInException
  {
    checkThatArgumentIsALiteral(argumentNumber, arguments);

    return (OWLDatatypeValue)arguments.get(argumentNumber);
  } // getArgumentAsALiteral

  public static OWLDatatypeValue getArgumentAsALiteral(BuiltInArgument argument) throws BuiltInException
  {
    checkThatArgumentIsALiteral(argument);

    return (OWLDatatypeValue)argument;
  } // getArgumentAsALiteral

  // Longs

  public static void checkThatArgumentIsALong(int argumentNumber, List<BuiltInArgument> arguments) throws BuiltInException
  {
    if (!isArgumentALong(argumentNumber, arguments))
      throw new InvalidBuiltInArgumentException(argumentNumber, 
                                                makeInvalidArgumentTypeMessage(arguments.get(argumentNumber), "long"));
  } // checkThatArgumentIsALong

  public static boolean isArgumentALong(int argumentNumber, List<BuiltInArgument> arguments) throws BuiltInException
  {
    if (isArgumentALiteral(argumentNumber, arguments)) 
      return (getArgumentAsALiteral(argumentNumber, arguments).isLong());
    else return false;
  } // isArgumentALong

  public static long getArgumentAsALong(int argumentNumber, List<BuiltInArgument> arguments) throws BuiltInException
  {
    return getArgumentAsALiteral(argumentNumber, arguments).getLong(); // Will throw DatatypeConversionException if invalid.
  } // getArgumentAsALong

  // Floats

  public static void checkThatArgumentIsAFloat(int argumentNumber, List<BuiltInArgument> arguments) throws BuiltInException
  {
    if (!isArgumentAFloat(argumentNumber, arguments))
      throw new InvalidBuiltInArgumentException(argumentNumber,
                                                makeInvalidArgumentTypeMessage(arguments.get(argumentNumber), "float"));
  } // checkThatArgumentIsAFloat

  public static boolean isArgumentAFloat(int argumentNumber, List<BuiltInArgument> arguments) throws BuiltInException
  {
    if (isArgumentALiteral(argumentNumber, arguments)) 
      return (getArgumentAsALiteral(argumentNumber, arguments).isFloat());
    else return false;
  } // isArgumentAFloat

  public static float getArgumentAsAFloat(int argumentNumber, List<BuiltInArgument> arguments) throws BuiltInException
  {
    return getArgumentAsALiteral(argumentNumber, arguments).getFloat(); // Will throw DatatypeConversionException if invalid.
  } // getArgumentAsAFloat

  // Double

  public static void checkThatArgumentIsADouble(int argumentNumber, List<BuiltInArgument> arguments) throws BuiltInException
  {
    if (!isArgumentADouble(argumentNumber, arguments))
      throw new InvalidBuiltInArgumentException(argumentNumber, 
                                                makeInvalidArgumentTypeMessage(arguments.get(argumentNumber), "double"));
  } // checkThatArgumentIsADouble

  public static boolean isArgumentADouble(int argumentNumber, List<BuiltInArgument> arguments) throws BuiltInException
  {
    if (isArgumentALiteral(argumentNumber, arguments)) 
      return (getArgumentAsALiteral(argumentNumber, arguments).isDouble());
    else return false;
  } // isArgumentADouble

  public static double getArgumentAsADouble(int argumentNumber, List<BuiltInArgument> arguments) throws BuiltInException
  {
    checkArgumentNumber(argumentNumber, arguments);

    return getArgumentAsADouble(arguments.get(argumentNumber));
  } // getArgumentAsADouble

  public static double getArgumentAsADouble(BuiltInArgument argument) throws BuiltInException
  {
    return getArgumentAsALiteral(argument).getDouble(); // Will throw DatatypeConversionException if invalid.
  } // getArgumentAsADouble

  // Booleans

  public static void checkThatArgumentIsABoolean(int argumentNumber, List<BuiltInArgument> arguments) throws BuiltInException
  {
    if (!isArgumentABoolean(argumentNumber, arguments))
      throw new InvalidBuiltInArgumentException(argumentNumber,
                                                makeInvalidArgumentTypeMessage(arguments.get(argumentNumber), "boolean"));
  } // checkThatArgumentIsABoolean

  public static boolean isArgumentABoolean(int argumentNumber, List<BuiltInArgument> arguments) throws BuiltInException
  {
    if (isArgumentALiteral(argumentNumber, arguments)) 
      return (getArgumentAsALiteral(argumentNumber, arguments).isBoolean());
    else return false;
  } // isArgumentABoolean

  public static boolean getArgumentAsABoolean(int argumentNumber, List<BuiltInArgument> arguments) throws BuiltInException
  {
    checkThatArgumentIsABoolean(argumentNumber, arguments);

    return getArgumentAsALiteral(argumentNumber, arguments).getBoolean();
  } // getArgumentAsABoolean

  // Strings

  public static void checkThatArgumentIsAString(int argumentNumber, List<BuiltInArgument> arguments) throws BuiltInException
  {
    if (!isArgumentAString(argumentNumber, arguments))
      throw new InvalidBuiltInArgumentException(argumentNumber, 
                                                makeInvalidArgumentTypeMessage(arguments.get(argumentNumber), "string"));
  } // checkThatArgumentIsAString

  public static boolean isArgumentAString(int argumentNumber, List<BuiltInArgument> arguments) throws BuiltInException
  {
    if (isArgumentALiteral(argumentNumber, arguments)) return getArgumentAsALiteral(argumentNumber, arguments).isString();
    else return false;
  } // isArgumentAString

  public static String getArgumentAsAString(int argumentNumber, List<BuiltInArgument> arguments) throws BuiltInException
  {
   checkThatArgumentIsAString(argumentNumber, arguments);

    return getArgumentAsALiteral(argumentNumber, arguments).getString();
  } // getArgumentAsAString

  // Unbound argument processing methods.

  public static boolean hasUnboundArguments(List<BuiltInArgument> arguments) throws BuiltInException
  {
    for (BuiltInArgument argument : arguments) if (argument.isUnbound()) return true;

    return false;
  } // hasUnboundArguments

  public static void checkThatAllArgumentsAreBound(List<BuiltInArgument> arguments) throws BuiltInException
  {
    if (hasUnboundArguments(arguments)) throw new BuiltInException("all arguments must be bound");
  } // checkThatAllArgumentsAreBound

  public static void checkThatArgumentIsBound(int argumentNumber, List<BuiltInArgument> arguments) throws BuiltInException
  {
    if (isUnboundArgument(argumentNumber, arguments)) 
      throw new BuiltInException("not expecting an unbound argument for (0-offset) argument #" + argumentNumber);
  } // checkThatArgumentIsBound

  public static boolean isUnboundArgument(int argumentNumber, List<BuiltInArgument> arguments) throws BuiltInException
  {
    checkArgumentNumber(argumentNumber, arguments);

    return arguments.get(argumentNumber).isUnbound();
  } // isUnboundArgument

  /**
   ** Get 0-offset position of first unbound argument; return -1 if no unbound arguments are found.
   */
  public static int getFirstUnboundArgument(List<BuiltInArgument> arguments) throws BuiltInException
  {
    for (int index = 0; index < arguments.size(); index++) if (arguments.get(index).isUnbound()) return index;

    return -1;
  } // getFirstUnboundArgument

  public static void checkForUnboundArguments(List<BuiltInArgument> arguments) throws BuiltInException
  {
    checkForUnboundArguments(arguments, "built-in does not support variable binding - unbound argument '" +
                             getFirstUnboundArgument(arguments) + "'");
  } // checkForUnboundArguments

  public static void checkForUnboundArguments(List<BuiltInArgument> arguments, String message) throws BuiltInException
  {
    if (hasUnboundArguments(arguments))
      throw new BuiltInException(message + " (0-offset) argument #" + getFirstUnboundArgument(arguments));
  } // checkForUnboundArguments

  public static void checkForNonVariableArguments(List<BuiltInArgument> arguments, String message) throws BuiltInException
  {
    for (BuiltInArgument argument : arguments) if (!argument.isVariable()) throw new BuiltInException(message + " '" + argument + "'");
  } // checkForNonVariableArguments

  public static void checkForUnboundNonFirstArguments(List<BuiltInArgument> arguments) throws BuiltInException
  {
    if (hasUnboundArguments(arguments.subList(1, arguments.size())))
      throw new BuiltInException("built-in supports variable binding only for the first argument - " +
                                 "unbound variables used as other arguments");
  } // checkForUnboundArguments

  public static String getVariableName(int argumentNumber, List<BuiltInArgument> arguments) throws BuiltInException
  {
    checkArgumentNumber(argumentNumber, arguments);

    return arguments.get(argumentNumber).getVariableName(); // Will throw an exception if it does not contain a variable name
  } // getVariableName

  public static String getPrefixedVariableName(int argumentNumber, List<BuiltInArgument> arguments) throws BuiltInException
  {
    checkArgumentNumber(argumentNumber, arguments);

    return arguments.get(argumentNumber).getPrefixedVariableName(); // Will throw an exception if it does not contain a variable name
  } // getPrefixedVariableName

  private static String makeInvalidArgumentTypeMessage(BuiltInArgument argument, String expectedTypeName) throws BuiltInException
  {
    String message = "expecting " + expectedTypeName + ", got ";
    if (argument.isUnbound()) message += "unbound argument with variable name '" + argument.getVariableName() + "'";
    else {
      if (argument instanceof ClassArgument) {
        ClassArgument classArgument = (ClassArgument)argument;
        message += "class with name '" + classArgument.getClassName() + "'";
      } else if (argument instanceof PropertyArgument) {
        PropertyArgument propertyArgument = (PropertyArgument)argument;
        message += "property with name '" + propertyArgument.getPropertyName() + "'";
      } else if (argument instanceof IndividualArgument) {
        IndividualArgument individualArgument = (IndividualArgument)argument;
        message += "individual with name '" + individualArgument.getIndividualName() + "'";
      } else if (argument instanceof DatatypeValueArgument) {
        DatatypeValueArgument literal = (DatatypeValueArgument)argument;
        message += "literal with value '" + literal.toString() + "'";
      } else message += "unknown type '" + argument.getClass().getName() + "'";
    } // if
    return message;
  } // makeInvalidArgumentTypeMessage

  /**
   ** Take an bound Argument object with types ClassArgument, PropertyArgument, IndividualArgument, or DatatypeValueArgument and return it as a
   ** property value representation. Class, property and individual argument are represented by strings containing their class, property or
   ** individual names, respectively; literal objects are represented by the appropriate Java type. Primitive XSD datatypes that do not have
   ** a corresponding Java type are not yet supported.
   */
  public static Object getArgumentAsAPropertyValue(int argumentNumber, List<BuiltInArgument> arguments) throws BuiltInException
  {
    Argument argument;
    Object result = null;

    SWRLBuiltInUtil.checkThatArgumentIsBound(argumentNumber, arguments);

    argument = arguments.get(argumentNumber);

    if (argument instanceof ClassArgument) {
      ClassArgument classArgument = (ClassArgument)argument;
      result = classArgument.getClassName();
    } else if (argument instanceof PropertyArgument) {
      PropertyArgument propertyArgument = (PropertyArgument)argument;
      result = propertyArgument.getPropertyName();
    } else if (argument instanceof IndividualArgument) {
      IndividualArgument individualArgument = (IndividualArgument)argument;
      result = individualArgument.getIndividualName();
    } else if (argument instanceof OWLDatatypeValue) {
      OWLDatatypeValue literal = (OWLDatatypeValue)argument;
      if (literal.isNumeric()) result = literal.getNumber();
      else if (literal.isString()) result = literal.getString();
      else throw new BuiltInException("literal with value '" + literal.toString() + "' not supported - strings and number literals only");
    } else throw new BuiltInException("argument '" + argument + "' of unknown type '" + argument.getClass().getName() + "'");

    return result;
  } // getArgumentAsAPropertyValue

  /**
   ** Create a string that represents a unique invocation pattern for a built-in for a bridge/rule/built-in/argument combination.  
   */
  public static String createInvocationPattern(SWRLRuleEngineBridge invokingBridge, String invokingRuleName, int invokingBuiltInIndex,
                                               boolean isInConsequent, List<BuiltInArgument> arguments) throws BuiltInException
  {
    String pattern = "" + invokingBridge.hashCode() + "." + invokingRuleName + "." + invokingBuiltInIndex + "." + isInConsequent;

    for (int i = 0; i < arguments.size(); i++) pattern += "." + getArgumentAsAPropertyValue(i, arguments);

    return pattern;
  } // createInvocationPattern

  public static void checkForUnboundArguments(String ruleName, String builtInName, List<BuiltInArgument> arguments) throws BuiltInException
  {
    int argumentNumber = 0;

    for (BuiltInArgument argument : arguments) {
      if (argument.isUnbound())  throw new BuiltInException("built-in '" + builtInName + "' in rule '" + ruleName + "' " +
                                                            "returned with unbound argument ?" + argument.getVariableName());
      else if (argument instanceof MultiArgument && ((MultiArgument)argument).hasNoArguments())
        throw new BuiltInException("built-in '" + builtInName + "' in rule '" + ruleName + "' " +
                                   "returned with empty multi-argument ?" + argument.getVariableName());
      argumentNumber++;
    } // for
  } // checkForUnboundArguments

  public static void generateBuiltInBindings(SWRLRuleEngineBridge bridge, String ruleName, String builtInName, int builtInIndex,
                                             List<BuiltInArgument> arguments)
    throws BuiltInException
  {
    List<Integer> multiArgumentIndexes = getMultiArgumentIndexes(arguments);
    
    if (multiArgumentIndexes.isEmpty()) 
      bridge.generateBuiltInBinding(ruleName, builtInName, builtInIndex, arguments); // No multi-arguments - do a simple bind
    else {
      List<Integer> multiArgumentCounts = new ArrayList<Integer>();
      List<Integer> multiArgumentSizes = new ArrayList<Integer>();
      List<BuiltInArgument> argumentsPattern;

      for (int i = 0; i < multiArgumentIndexes.size(); i++) multiArgumentCounts.add(Integer.valueOf(0));
      for (int i = 0; i < multiArgumentIndexes.size(); i++) {
        MultiArgument multiArgument = (MultiArgument)arguments.get(multiArgumentIndexes.get(i).intValue());
        multiArgumentSizes.add(Integer.valueOf(multiArgument.getNumberOfArguments()));
      } // for

      do {
        argumentsPattern = generateArgumentsPattern(arguments, multiArgumentCounts);
        bridge.generateBuiltInBinding(ruleName, builtInName, builtInIndex, argumentsPattern); // Call the rule engine method.
      } while (!nextMultiArgumentCounts(multiArgumentCounts, multiArgumentSizes));
    } // if
  } // generateBuiltInBindings

  public static List<BuiltInArgument> copyArguments(List<BuiltInArgument> arguments) throws BuiltInException
  {
    return new ArrayList<BuiltInArgument>(arguments);
  } // copyArguments

  private static boolean nextMultiArgumentCounts(List<Integer> multiArgumentCounts, List<Integer> multiArgumentSizes)
  {
    if (multiArgumentSizes.isEmpty()) return true;
    
    if (nextMultiArgumentCounts(multiArgumentCounts.subList(1, multiArgumentCounts.size()), 
                                multiArgumentSizes.subList(1, multiArgumentSizes.size()))) {
      // No more permutations of rest of list so increment this count and if we are not at the end set rest of the list to begin at 0 again.
      int count = multiArgumentCounts.get(0).intValue();
      int size = multiArgumentSizes.get(0).intValue();
      
      if (++count == size) return true;

      multiArgumentCounts.set(0, Integer.valueOf(count));

      for (int i = 1; i < multiArgumentCounts.size(); i++) multiArgumentCounts.set(i, Integer.valueOf(0));
    } // if
    return false;
  } // nextMultiArgumentCounts

  private static List<BuiltInArgument> generateArgumentsPattern(List<BuiltInArgument> arguments, List<Integer> multiArgumentCounts)
  {
    List<BuiltInArgument> result = new ArrayList<BuiltInArgument>();
    int multiArgumentIndex = 0;

    for (BuiltInArgument argument: arguments) {
      if (argument instanceof MultiArgument) {
        MultiArgument multiArgument = (MultiArgument)argument;
        result.add(multiArgument.getArguments().get((multiArgumentCounts.get(multiArgumentIndex).intValue())));
        multiArgumentIndex++;
      } else result.add(argument);
    } // for

    return result;
  } // generateArgumentsPattern
    
  private static List<Integer> getMultiArgumentIndexes(List<BuiltInArgument> arguments)
  {
    List<Integer> result = new ArrayList<Integer>();

    for (int i = 0; i < arguments.size(); i++) 
      if (arguments.get(i) instanceof MultiArgument) result.add(Integer.valueOf(i));

    return result;
  } // getMultiArgumentIndexes

} // SWRLBuiltInUtil
