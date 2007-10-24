
package edu.stanford.smi.protegex.owl.swrl.bridge.builtins;

import edu.stanford.smi.protegex.owl.swrl.bridge.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;

import edu.stanford.smi.protegex.owl.swrl.bridge.impl.*;

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

  public static boolean isFloatMostPreciseArgument(List<BuiltInArgument> arguments) throws BuiltInException
  {
    for (int argumentNumber = 0; argumentNumber < arguments.size(); argumentNumber++) 
      if (isArgumentADouble(argumentNumber, arguments) || isArgumentALong(argumentNumber, arguments)) return false;
    return true;
  } // isLongMostPreciseArgument

  public static boolean isLongMostPreciseArgument(List<BuiltInArgument> arguments) throws BuiltInException
  {
    for (int argumentNumber = 0; argumentNumber < arguments.size(); argumentNumber++) 
      if (isArgumentADouble(argumentNumber, arguments)) return false;
    return true;
  } // isLongMostPreciseArgument

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

  public static void checkThatArgumentIsAnIndividual(int argumentNumber, List<BuiltInArgument> arguments) throws BuiltInException
  {
    if (!isArgumentAnIndividual(argumentNumber, arguments)) {
      throw new InvalidBuiltInArgumentException(argumentNumber, 
                                                makeInvalidArgumentTypeMessage(arguments.get(argumentNumber), "individual"));
    } // if
  } // checkThatArgumentIsAnIndividual

  public static String getArgumentAsAnIndividualName(int argumentNumber, List<BuiltInArgument> arguments) throws BuiltInException
  {
    checkThatArgumentIsAnIndividual(argumentNumber, arguments);

    return ((OWLIndividual)arguments.get(argumentNumber)).getIndividualName();
  } // getArgumentAsAnIndividualName

  public static String getArgumentAsAClassName(int argumentNumber, List<BuiltInArgument> arguments) throws BuiltInException
  {
    checkThatArgumentIsAClass(argumentNumber, arguments);

    return ((OWLClass)arguments.get(argumentNumber)).getClassName();
  } // getArgumentAsAClassName

  public static String getArgumentAsAResourceName(int argumentNumber, List<BuiltInArgument> arguments) throws BuiltInException
  {
    String resourceName = "";

    checkThatArgumentIsAClassPropertyOrIndividual(argumentNumber, arguments);

    if (isArgumentAClass(argumentNumber, arguments)) resourceName = ((OWLClass)arguments.get(argumentNumber)).getClassName();
    else if (isArgumentAProperty(argumentNumber, arguments)) resourceName = ((OWLProperty)arguments.get(argumentNumber)).getPropertyName();
    else if (isArgumentAnIndividual(argumentNumber, arguments)) resourceName = ((OWLIndividual)arguments.get(argumentNumber)).getIndividualName();

    return resourceName;
  } // getArgumentAsAResourceName

  public static String getArgumentAsAPropertyName(int argumentNumber, List<BuiltInArgument> arguments) throws BuiltInException
  {
    checkThatArgumentIsAProperty(argumentNumber, arguments);

    return ((OWLProperty)arguments.get(argumentNumber)).getPropertyName();
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
    if (isArgumentALiteral(argumentNumber, arguments)) 
      return (getArgumentAsALiteral(argumentNumber, arguments).isInteger());
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

  public static int getArgumentAsAShort(int argumentNumber, List<BuiltInArgument> arguments) throws BuiltInException
  {
    return getArgumentAsALiteral(argumentNumber, arguments).getInt(); // Will throw DatatypeConversionException if invalid.
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
    if (isArgumentALiteral(argumentNumber, arguments)) 
      return getArgumentAsALiteral(argumentNumber, arguments).isString();
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
      throw new BuiltInException(message + " '" + getFirstUnboundArgument(arguments) + "'");
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

  private static String makeInvalidArgumentTypeMessage(BuiltInArgument argument, String expectedTypeName) throws BuiltInException
  {
    String message = "expecting " + expectedTypeName + ", got ";
    if (argument.isUnbound()) message += "unbound argument with variable name '" + argument.getVariableName() + "'";
    else {
      if (argument instanceof ClassArgument) {
        OWLClass owlClass = (OWLClass)argument;
        message += "class with name '" + owlClass.getClassName() + "'";
      } else if (argument instanceof PropertyArgument) {
        OWLProperty property = (OWLProperty)argument;
        message += "property with name '" + property.getPropertyName() + "'";
      } else if (argument instanceof IndividualArgument) {
        OWLIndividual individual = (OWLIndividual)argument;
        message += "individual with name '" + individual.getIndividualName() + "'";
      } else if (argument instanceof DatatypeValueArgument) {
        OWLDatatypeValue literal = (OWLDatatypeValue)argument;
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
      OWLClass owlClass = (OWLClass)argument;
      result = owlClass.getClassName();
    } else if (argument instanceof PropertyArgument) {
      OWLProperty property = (OWLProperty)argument;
      result = property.getPropertyName();
    } else if (argument instanceof IndividualArgument) {
      OWLIndividual individual = (OWLIndividual)argument;
      result = individual.getIndividualName();
    } else if (argument instanceof DatatypeValueArgument) {
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
                                               List<BuiltInArgument> arguments) throws BuiltInException
  {
    String pattern = "" + invokingBridge.hashCode() + "." + invokingRuleName + "." + invokingBuiltInIndex;

    for (int i = 0; i < arguments.size(); i++) pattern += "." + getArgumentAsAPropertyValue(i, arguments);

    return pattern;
  } // createInvocationPattern

} // SWRLBuiltInUtil
