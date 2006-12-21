
// TODO: This could be made significantly shorter using generics.

package edu.stanford.smi.protegex.owl.swrl.bridge.builtins;

import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.swrl.model.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;

import java.util.List;

/**
 ** Class containing utility methods that can be used in built-in method implementations. 
 */
public class SWRLBuiltInUtil
{
  public static void checkNumberOfArgumentsEqualTo(String builtInName, int expecting, int actual) 
    throws InvalidBuiltInArgumentNumberException
  {
    if (expecting != actual) throw new InvalidBuiltInArgumentNumberException(builtInName, expecting, actual);
  } // checkNumberOfArgumentsEqualTo

  public static void checkNumberOfArgumentsAtLeast(String builtInName, int expectingAtLeast, int actual) 
    throws InvalidBuiltInArgumentNumberException
  {
    if (actual < expectingAtLeast) throw new InvalidBuiltInArgumentNumberException(builtInName, expectingAtLeast, actual, "at least");
  } // checkNumberOfArgumentsAtLeast

  public static void checkNumberOfArgumentsAtMost(String builtInName, int expectingAtMost, int actual) 
    throws InvalidBuiltInArgumentNumberException
  {
    if (actual > expectingAtMost) throw new InvalidBuiltInArgumentNumberException(builtInName, expectingAtMost, actual, "at most");
  } // checkNumberOfArgumentsAtMost

  public static void checkThatAllArgumentsAreLiterals(String builtInName, List<Argument> arguments) throws InvalidBuiltInArgumentException
  {
    for (int argumentNumber = 0; argumentNumber < arguments.size(); argumentNumber++) 
      checkThatArgumentIsALiteral(builtInName, argumentNumber, arguments);
  } // checkThatAllArgumentsAreLiterals

  public static void checkThatAllArgumentsAreNumeric(String builtInName, List<Argument> arguments) throws InvalidBuiltInArgumentException
  {
    for (int argumentNumber = 0; argumentNumber < arguments.size(); argumentNumber++) 
      checkThatArgumentIsNumeric(builtInName, argumentNumber, arguments);
  } // checkThatAllArgumentsAreNumeric

  public static void checkThatAllArgumentsAreIntegers(String builtInName, List<Argument> arguments) throws InvalidBuiltInArgumentException
  {
    for (int argumentNumber = 0; argumentNumber < arguments.size(); argumentNumber++) 
      checkThatArgumentIsAnInteger(builtInName, argumentNumber, arguments);
  } // checkThatAllArgumentsAreIntegers

  public static boolean areAllArgumentsIntegers(String builtInName, List<Argument> arguments) throws InvalidBuiltInArgumentException
  {
    for (int argumentNumber = 0; argumentNumber < arguments.size(); argumentNumber++) 
      if (!isArgumentAnInteger(argumentNumber, arguments)) return false;
    return true;
  } // areAllArgumentsIntegers

  public static boolean areAllArgumentsLongs(String builtInName, List<Argument> arguments) throws InvalidBuiltInArgumentException
  {
    for (int argumentNumber = 0; argumentNumber < arguments.size(); argumentNumber++) 
      if (!isArgumentALong(argumentNumber, arguments)) return false;
    return true;
  } // areAllArgumentsLongs

  public static boolean areAllArgumentsDoubles(String builtInName, List<Argument> arguments)
    throws InvalidBuiltInArgumentException, DatatypeConversionException
  {
    for (int argumentNumber = 0; argumentNumber < arguments.size(); argumentNumber++) 
      if (!isArgumentADouble(argumentNumber, arguments)) return false;
    return true;
  } // areAllArgumentsDoubles

  public static boolean areAllArgumentsBooleans(String builtInName, List<Argument> arguments) 
    throws InvalidBuiltInArgumentException, DatatypeConversionException
  {
    for (int argumentNumber = 0; argumentNumber < arguments.size(); argumentNumber++) 
      if (!isArgumentABoolean(argumentNumber, arguments)) return false;
    return true;
  } // areAllArgumentsBooleans

  public static boolean areAllArgumentLiterals(String builtInName, List<Argument> arguments) throws InvalidBuiltInArgumentException
  {
    for (int argumentNumber = 0; argumentNumber < arguments.size(); argumentNumber++) 
      if (!isArgumentALiteral(argumentNumber, arguments)) return false;
    return true;
  } // areAllArgumentsIntegers

  public static boolean areAllArgumentsFloats(String builtInName, List<Argument> arguments) 
    throws InvalidBuiltInArgumentException, DatatypeConversionException
  {
    for (int argumentNumber = 0; argumentNumber < arguments.size(); argumentNumber++) 
      if (!isArgumentAFloat(argumentNumber, arguments)) return false;
    return true;
  } // areAllArgumentsFloats

  public static boolean areAllArgumentsNumeric(String builtInName, List<Argument> arguments) throws InvalidBuiltInArgumentException
  {
    for (int argumentNumber = 0; argumentNumber < arguments.size(); argumentNumber++) 
      if (!isArgumentNumeric(argumentNumber, arguments)) return false;
    return true;
  } // areAllArgumentsNumeric

  public static boolean areAllArgumentsStrings(String builtInName, List<Argument> arguments) throws InvalidBuiltInArgumentException
  {
    for (int argumentNumber = 0; argumentNumber < arguments.size(); argumentNumber++) 
      if (!isArgumentAString(argumentNumber, arguments)) return false;
    return true;
  } // areAllArgumentsStrings

  public static boolean areAllArgumentsOfAnOrderedType(String builtInName, List<Argument> arguments) throws InvalidBuiltInArgumentException
  {
    for (int argumentNumber = 0; argumentNumber < arguments.size(); argumentNumber++) 
      if (!isArgumentOfAnOrderedType(argumentNumber, arguments)) return false;
    return true;
  } // areAllArgumentsOfAnOrderedType

  public static void checkThatAllArgumentsAreFloats(String builtInName, List<Argument> arguments) 
    throws InvalidBuiltInArgumentException, DatatypeConversionException
  {
    for (int argumentNumber = 0; argumentNumber < arguments.size(); argumentNumber++) 
      checkThatArgumentIsAFloat(builtInName, argumentNumber, arguments);
  } // checkThatAllArgumentsAreFloats

  public static void checkThatAllArgumentsAreStrings(String builtInName, List<Argument> arguments) 
    throws DatatypeConversionException, InvalidBuiltInArgumentException
  {
    for (int argumentNumber = 0; argumentNumber < arguments.size(); argumentNumber++) 
      checkThatArgumentIsAString(builtInName, argumentNumber, arguments);
  } // checkThatAllArgumentsAreStrings

  public static void checkThatAllArgumentsAreOfAnOrderedType(String builtInName, List<Argument> arguments) 
     throws InvalidBuiltInArgumentException
  {
    for (int argumentNumber = 0; argumentNumber < arguments.size(); argumentNumber++) 
      checkThatArgumentIsOfAnOrderedType(builtInName, argumentNumber, arguments);
  } // checkThatAllArgumentsAreOfAnOrderedType

  public static void checkThatArgumentIsALiteral(String builtInName, int argumentNumber, List<Argument> arguments) 
    throws InvalidBuiltInArgumentException
  {
    if (!(arguments.get(argumentNumber) instanceof LiteralInfo)) 
      throw new InvalidBuiltInArgumentException(builtInName, argumentNumber, "Expecting literal");
  } // checkThatArgumentIsALiteral

  public static void checkThatArgumentIsNumeric(String builtInName, int argumentNumber, List<Argument> arguments) 
    throws InvalidBuiltInArgumentException
  {
    if (!isArgumentNumeric(argumentNumber, arguments))
      throw new InvalidBuiltInArgumentException(builtInName, argumentNumber, "Expecting numeric literal");
  } // checkThatArgumentIsNumeric

  public static void checkThatArgumentIsOfAnOrderedType(String builtInName, int argumentNumber, List<Argument> arguments) 
    throws InvalidBuiltInArgumentException
  {
    if (!isArgumentOfAnOrderedType(argumentNumber, arguments))
      throw new InvalidBuiltInArgumentException(builtInName, argumentNumber, "Expecting ordered type");
  } // checkThatArgumentIsOfAnOrderedType

  public static boolean isArgumentOfAnOrderedType(int argumentNumber, List<Argument> arguments) 
    throws InvalidBuiltInArgumentException
  {
    return (isArgumentNumeric(argumentNumber, arguments) || isArgumentAString(argumentNumber, arguments));
  } // isArgumentOfAnOrderedType

  public static void checkThatArgumentIsAnIndividualName(String builtInName, int argumentNumber, List<Argument> arguments) 
    throws InvalidBuiltInArgumentException
  {
    if (!(arguments.get(argumentNumber) instanceof IndividualInfo)) 
      throw new InvalidBuiltInArgumentException(builtInName, argumentNumber, "Expecting individual name");
  } // checkThatArgumentIsAnIndividualName

  public static boolean isArgumentNumeric(int argumentNumber, List<Argument> arguments) throws InvalidBuiltInArgumentException
  {
    if (isArgumentALiteral(argumentNumber, arguments)) return getArgumentAsALiteral(argumentNumber, arguments).isNumeric();
    else return false;
  } // isArgumentNumeric

  public static boolean isArgumentNonNumeric(int argumentNumber, List<Argument> arguments) throws InvalidBuiltInArgumentException
  {
    if (isArgumentALiteral(argumentNumber, arguments))
      return !getArgumentAsALiteral(argumentNumber, arguments).isNumeric();
    else return false;
  } // isArgumentNonNumeric

  public static void checkThatArgumentIsNonNumeric(String builtInName, int argumentNumber, List<Argument> arguments) 
    throws InvalidBuiltInArgumentException
  {
    if (!isArgumentNonNumeric(argumentNumber, arguments))
      throw new InvalidBuiltInArgumentException(builtInName, argumentNumber, "Expecting non-numeric literal");
  } // checkThatArgumentIsNonNumeric

  // Integers
  
  public static void checkThatArgumentIsAnInteger(String builtInName, int argumentNumber, List<Argument> arguments) 
    throws InvalidBuiltInArgumentException
  {
    if (!isArgumentAnInteger(argumentNumber, arguments))
      throw new InvalidBuiltInArgumentException(builtInName, argumentNumber, "Expecting integer literal");
  } // checkThatArgumentIsAnInteger

  public static boolean isArgumentAnInteger(int argumentNumber, List<Argument> arguments) throws InvalidBuiltInArgumentException
  {
    if (isArgumentALiteral(argumentNumber, arguments)) return (getArgumentAsALiteral(argumentNumber, arguments).isInteger());
    else return false;
  } // isArgumentAnInteger

  public static int getArgumentAsAnInteger(String builtInName, int argumentNumber, List<Argument> arguments) 
    throws InvalidBuiltInArgumentException, DatatypeConversionException
  {
    checkThatArgumentIsAnInteger(builtInName, argumentNumber, arguments);

    return getArgumentAsALiteral(argumentNumber, arguments).getInt();
  } // getArgumentAsAnInteger

  public static boolean isArgumentALiteral(int argumentNumber, List<Argument> arguments) 
  {
    return (arguments.get(argumentNumber) instanceof LiteralInfo);
  } // checkThatArgumentIsALiteral

  public static LiteralInfo getArgumentAsALiteral(int argumentNumber, List<Argument> arguments) throws InvalidBuiltInArgumentException
  {
    return (LiteralInfo)arguments.get(argumentNumber);
  } // getArgumentAsALiteral

  // Longs

  public static void checkThatArgumentIsALong(String builtInName, int argumentNumber, List<Argument> arguments) 
    throws InvalidBuiltInArgumentException
  {
    if (!isArgumentALong(argumentNumber, arguments))
      throw new InvalidBuiltInArgumentException(builtInName, argumentNumber, "Expecting long literal");
  } // checkThatArgumentIsALong

  public static boolean isArgumentALong(int argumentNumber, List<Argument> arguments) throws InvalidBuiltInArgumentException
  {
    if (isArgumentALiteral(argumentNumber, arguments)) 
      return (getArgumentAsALiteral(argumentNumber, arguments).isLong());
    else return false;
  } // isArgumentALong

  public static long getArgumentAsALong(String builtInName, int argumentNumber, List<Argument> arguments) 
    throws InvalidBuiltInArgumentException, DatatypeConversionException
  {
    checkThatArgumentIsALong(builtInName, argumentNumber, arguments);

    return getArgumentAsALiteral(argumentNumber, arguments).getLong();
  } // getArgumentAsALong

  // Floats

  public static void checkThatArgumentIsAFloat(String builtInName, int argumentNumber, List<Argument> arguments) 
    throws InvalidBuiltInArgumentException, DatatypeConversionException
  {
    if (!isArgumentAFloat(argumentNumber, arguments))
      throw new InvalidBuiltInArgumentException(builtInName, argumentNumber, "Expecting float literal");
  } // checkThatArgumentIsAFloat

  public static boolean isArgumentAFloat(int argumentNumber, List<Argument> arguments) 
    throws InvalidBuiltInArgumentException, DatatypeConversionException
  {
    if (isArgumentALiteral(argumentNumber, arguments)) 
      return (getArgumentAsALiteral(argumentNumber, arguments).isFloat());
    else return false;
  } // isArgumentAFloat

  public static float getArgumentAsAFloat(String builtInName, int argumentNumber, List<Argument> arguments) 
    throws InvalidBuiltInArgumentException, DatatypeConversionException
  {
    checkThatArgumentIsAFloat(builtInName, argumentNumber, arguments);

    return getArgumentAsALiteral(argumentNumber, arguments).getFloat();
  } // getArgumentAsAFloat

  // Double

  public static void checkThatArgumentIsADouble(String builtInName, int argumentNumber, List<Argument> arguments) 
    throws InvalidBuiltInArgumentException, DatatypeConversionException
  {
    if (!isArgumentADouble(argumentNumber, arguments))
      throw new InvalidBuiltInArgumentException(builtInName, argumentNumber, "Expecting float literal");
  } // checkThatArgumentIsADouble

  public static boolean isArgumentADouble(int argumentNumber, List<Argument> arguments) 
    throws InvalidBuiltInArgumentException, DatatypeConversionException
  {
    if (isArgumentALiteral(argumentNumber, arguments)) 
      return (getArgumentAsALiteral(argumentNumber, arguments).isDouble());
    else return false;
  } // isArgumentADouble

  public static double getArgumentAsADouble(String builtInName, int argumentNumber, List<Argument> arguments) 
    throws InvalidBuiltInArgumentException, DatatypeConversionException
  {
    checkThatArgumentIsADouble(builtInName, argumentNumber, arguments);

    return getArgumentAsALiteral(argumentNumber, arguments).getDouble();
  } // getArgumentAsADouble

  // Booleans

  public static void checkThatArgumentIsABoolean(String builtInName, int argumentNumber, List<Argument> arguments) 
    throws InvalidBuiltInArgumentException
  {
    if (!isArgumentABoolean(argumentNumber, arguments))
      throw new InvalidBuiltInArgumentException(builtInName, argumentNumber, "Expecting boolean literal");
  } // checkThatArgumentIsABoolean

  public static boolean isArgumentABoolean(int argumentNumber, List<Argument> arguments) throws InvalidBuiltInArgumentException
  {
    if (isArgumentALiteral(argumentNumber, arguments)) return (getArgumentAsALiteral(argumentNumber, arguments).isBoolean());
    else return false;
  } // isArgumentABoolean

  public static boolean getArgumentAsABoolean(String builtInName, int argumentNumber, List<Argument> arguments) 
    throws DatatypeConversionException, InvalidBuiltInArgumentException
  {
    checkThatArgumentIsABoolean(builtInName, argumentNumber, arguments);

    return getArgumentAsALiteral(argumentNumber, arguments).getBoolean();
  } // getArgumentAsABoolean

  // Strings

  public static void checkThatArgumentIsAString(String builtInName, int argumentNumber, List<Argument> arguments) 
    throws DatatypeConversionException, InvalidBuiltInArgumentException
  {
    if (!isArgumentAString(argumentNumber, arguments))
      throw new InvalidBuiltInArgumentException(builtInName, argumentNumber, "Expecting string literal");
  } // checkThatArgumentIsAString

  public static boolean isArgumentAString(int argumentNumber, List<Argument> arguments) throws InvalidBuiltInArgumentException
  {
    if (isArgumentALiteral(argumentNumber, arguments)) return (getArgumentAsALiteral(argumentNumber, arguments).isString());
    else return false;
  } // isArgumentAString

  public static String getArgumentAsAString(String builtInName, int argumentNumber, List<Argument> arguments) 
    throws DatatypeConversionException, InvalidBuiltInArgumentException
  {
    checkThatArgumentIsAString(builtInName, argumentNumber, arguments);

    return getArgumentAsALiteral(argumentNumber, arguments).getString();
  } // getArgumentAsAString

  // Unbound argument processing methods.

  public static boolean hasUnboundArguments(String builtInName, List<Argument> arguments) 
  {
    return !arguments.isEmpty() && arguments.contains(null); // An argument is unbound if its value is null.
  } // hasUnboundArguments

  public static boolean isUnboundArgument(String builtInName, int argumentNumber, List<Argument> arguments)
  {
    return (argumentNumber >= 0) && (argumentNumber < arguments.size()) && (arguments.get(argumentNumber) == null);
  } // isUnboundArgument

  /*
  ** Get 0-offset position of first unbound argument; return -1 if no unbound arguments are found.
  */
  public static int getFirstUnboundArgument(String builtInName, List<Argument> arguments)
  {
    if (hasUnboundArguments(builtInName, arguments)) return arguments.indexOf(null);
    else return -1;
  } // getFirstUnboundArgument

  public static void checkForUnboundArguments(String builtInName, List<Argument> arguments) throws BuiltInException
  {
    if (hasUnboundArguments(builtInName, arguments))
      throw new BuiltInException("Built-in '" + builtInName + "' does not support variable binding. Unbound variable used for argument #" +
                                 getFirstUnboundArgument(builtInName, arguments) + ".");
  } // checkForUnboundArguments

  public static void checkForUnboundNonFirstArguments(String builtInName, List<Argument> arguments) throws BuiltInException
  {
    if (hasUnboundArguments(builtInName, arguments.subList(1, arguments.size())))
      throw new BuiltInException("Built-in '" + builtInName + "' supports variable binding only for the first argument. " +
                                 "Unbound variable used in other arguments.");
  } // checkForUnboundArguments

} // SWRLBuiltInUtil
