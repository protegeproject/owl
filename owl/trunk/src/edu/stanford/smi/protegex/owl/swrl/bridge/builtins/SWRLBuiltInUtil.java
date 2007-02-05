
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

  public static void checkThatAllArgumentsAreLiterals(String builtInName, List<Argument> arguments) throws BuiltInException
  {
    for (int argumentNumber = 0; argumentNumber < arguments.size(); argumentNumber++) 
      checkThatArgumentIsALiteral(builtInName, argumentNumber, arguments);
  } // checkThatAllArgumentsAreLiterals

  public static void checkThatAllArgumentsAreNumeric(String builtInName, List<Argument> arguments) throws BuiltInException
  {
    for (int argumentNumber = 0; argumentNumber < arguments.size(); argumentNumber++) 
      checkThatArgumentIsNumeric(builtInName, argumentNumber, arguments);
  } // checkThatAllArgumentsAreNumeric

  public static void checkThatAllArgumentsAreIntegers(String builtInName, List<Argument> arguments) throws BuiltInException
  {
    for (int argumentNumber = 0; argumentNumber < arguments.size(); argumentNumber++) 
      checkThatArgumentIsAnInteger(builtInName, argumentNumber, arguments);
  } // checkThatAllArgumentsAreIntegers

  public static boolean areAllArgumentsIntegers(String builtInName, List<Argument> arguments) throws BuiltInException
  {
    for (int argumentNumber = 0; argumentNumber < arguments.size(); argumentNumber++) 
      if (!isArgumentAnInteger(builtInName, argumentNumber, arguments)) return false;
    return true;
  } // areAllArgumentsIntegers

  public static boolean areAllArgumentsLongs(String builtInName, List<Argument> arguments) throws BuiltInException
  {
    for (int argumentNumber = 0; argumentNumber < arguments.size(); argumentNumber++) 
      if (!isArgumentALong(builtInName, argumentNumber, arguments)) return false;
    return true;
  } // areAllArgumentsLongs

  public static boolean areAllArgumentsDoubles(String builtInName, List<Argument> arguments) throws BuiltInException
  {
    for (int argumentNumber = 0; argumentNumber < arguments.size(); argumentNumber++) 
      if (!isArgumentADouble(builtInName, argumentNumber, arguments)) return false;
    return true;
  } // areAllArgumentsDoubles

  public static boolean areAllArgumentsBooleans(String builtInName, List<Argument> arguments) throws BuiltInException
  {
    for (int argumentNumber = 0; argumentNumber < arguments.size(); argumentNumber++) 
      if (!isArgumentABoolean(builtInName, argumentNumber, arguments)) return false;
    return true;
  } // areAllArgumentsBooleans

  public static boolean areAllArgumentLiterals(String builtInName, List<Argument> arguments) throws BuiltInException
  {
    for (int argumentNumber = 0; argumentNumber < arguments.size(); argumentNumber++) 
      if (!isArgumentALiteral(builtInName, argumentNumber, arguments)) return false;
    return true;
  } // areAllArgumentsIntegers

  public static boolean areAllArgumentsFloats(String builtInName, List<Argument> arguments) throws BuiltInException
  {
    for (int argumentNumber = 0; argumentNumber < arguments.size(); argumentNumber++) 
      if (!isArgumentAFloat(builtInName, argumentNumber, arguments)) return false;
    return true;
  } // areAllArgumentsFloats

  public static boolean areAllArgumentsNumeric(String builtInName, List<Argument> arguments) throws BuiltInException
  {
    for (int argumentNumber = 0; argumentNumber < arguments.size(); argumentNumber++) 
      if (!isArgumentNumeric(builtInName, argumentNumber, arguments)) return false;
    return true;
  } // areAllArgumentsNumeric

  public static boolean areAllArgumentsStrings(String builtInName, List<Argument> arguments) throws BuiltInException
  {
    for (int argumentNumber = 0; argumentNumber < arguments.size(); argumentNumber++) 
      if (!isArgumentAString(builtInName, argumentNumber, arguments)) return false;
    return true;
  } // areAllArgumentsStrings

  public static boolean areAllArgumentsOfAnOrderedType(String builtInName, List<Argument> arguments) throws BuiltInException
  {
    for (int argumentNumber = 0; argumentNumber < arguments.size(); argumentNumber++) 
      if (!isArgumentOfAnOrderedType(builtInName, argumentNumber, arguments)) return false;
    return true;
  } // areAllArgumentsOfAnOrderedType

  public static void checkThatAllArgumentsAreFloats(String builtInName, List<Argument> arguments) throws BuiltInException
  {
    for (int argumentNumber = 0; argumentNumber < arguments.size(); argumentNumber++) 
      checkThatArgumentIsAFloat(builtInName, argumentNumber, arguments);
  } // checkThatAllArgumentsAreFloats

  public static void checkThatAllArgumentsAreStrings(String builtInName, List<Argument> arguments) throws BuiltInException
  {
    for (int argumentNumber = 0; argumentNumber < arguments.size(); argumentNumber++) 
      checkThatArgumentIsAString(builtInName, argumentNumber, arguments);
  } // checkThatAllArgumentsAreStrings

  public static void checkThatAllArgumentsAreOfAnOrderedType(String builtInName, List<Argument> arguments) throws BuiltInException
  {
    for (int argumentNumber = 0; argumentNumber < arguments.size(); argumentNumber++) 
      checkThatArgumentIsOfAnOrderedType(builtInName, argumentNumber, arguments);
  } // checkThatAllArgumentsAreOfAnOrderedType

  public static void checkThatArgumentIsALiteral(String builtInName, int argumentNumber, List<Argument> arguments) throws BuiltInException
  {
    checkArgumentNumber(builtInName, argumentNumber, arguments);

    if (!(arguments.get(argumentNumber) instanceof LiteralInfo)) 
      throw new InvalidBuiltInArgumentException(builtInName, argumentNumber, "Expecting literal");
  } // checkThatArgumentIsALiteral

  public static void checkThatArgumentIsNumeric(String builtInName, int argumentNumber, List<Argument> arguments) throws BuiltInException
  {
    if (!isArgumentNumeric(builtInName, argumentNumber, arguments))
      throw new InvalidBuiltInArgumentException(builtInName, argumentNumber, "Expecting numeric literal");
  } // checkThatArgumentIsNumeric

  public static void checkThatArgumentIsOfAnOrderedType(String builtInName, int argumentNumber, List<Argument> arguments) throws BuiltInException
  {
    if (!isArgumentOfAnOrderedType(builtInName, argumentNumber, arguments))
      throw new InvalidBuiltInArgumentException(builtInName, argumentNumber, "Expecting ordered type");
  } // checkThatArgumentIsOfAnOrderedType

  public static boolean isArgumentOfAnOrderedType(String builtInName, int argumentNumber, List<Argument> arguments) throws BuiltInException
  {
    return (isArgumentNumeric(builtInName, argumentNumber, arguments) || isArgumentAString(builtInName, argumentNumber, arguments));
  } // isArgumentOfAnOrderedType

  public static boolean isArgumentAnIndividual(String builtInName, int argumentNumber, List<Argument> arguments) throws BuiltInException
  {
    checkArgumentNumber(builtInName, argumentNumber, arguments);

    return arguments.get(argumentNumber) instanceof IndividualInfo;
  } // isArgumentAnIndividual

  public static void checkThatArgumentIsAnIndividual(String builtInName, int argumentNumber, List<Argument> arguments) throws BuiltInException
  {
    if (!isArgumentAnIndividual(builtInName, argumentNumber, arguments))
      throw new InvalidBuiltInArgumentException(builtInName, argumentNumber, "Expecting individual");
  } // checkThatArgumentIsAnIndividual

  public static String getArgumentAsAnIndividualName(String builtInName, int argumentNumber, List<Argument> arguments) throws BuiltInException
  {
    checkThatArgumentIsAnIndividual(builtInName, argumentNumber, arguments);

    return ((IndividualInfo)arguments.get(argumentNumber)).getIndividualName();
  } // getArgumentAsAnIndividualName

  public static void checkArgumentNumber(String builtInName, int argumentNumber, List<Argument> arguments) throws BuiltInException
  {
    if ((argumentNumber < 0) || (argumentNumber >= arguments.size()))
      throw new BuiltInException("Argument number #" + argumentNumber + " out of bounds for built-in '" + builtInName + "'");
  } // checkArgumentNumber

  public static boolean isArgumentNumeric(String builtInName, int argumentNumber, List<Argument> arguments) throws BuiltInException
  {
    if (isArgumentALiteral(builtInName, argumentNumber, arguments)) return getArgumentAsALiteral(builtInName, argumentNumber, arguments).isNumeric();
    else return false;
  } // isArgumentNumeric

  public static boolean isArgumentNonNumeric(String builtInName, int argumentNumber, List<Argument> arguments) throws BuiltInException
  {
    if (isArgumentALiteral(builtInName, argumentNumber, arguments))
      return !getArgumentAsALiteral(builtInName, argumentNumber, arguments).isNumeric();
    else return false;
  } // isArgumentNonNumeric

  public static void checkThatArgumentIsNonNumeric(String builtInName, int argumentNumber, List<Argument> arguments) throws BuiltInException
  {
    if (!isArgumentNonNumeric(builtInName, argumentNumber, arguments))
      throw new InvalidBuiltInArgumentException(builtInName, argumentNumber, "Expecting non-numeric literal");
  } // checkThatArgumentIsNonNumeric

  // Integers
  
  public static void checkThatArgumentIsAnInteger(String builtInName, int argumentNumber, List<Argument> arguments) throws BuiltInException
  {
    if (!isArgumentAnInteger(builtInName, argumentNumber, arguments))
      throw new InvalidBuiltInArgumentException(builtInName, argumentNumber, "Expecting integer literal");
  } // checkThatArgumentIsAnInteger

  public static boolean isArgumentAnInteger(String builtInName, int argumentNumber, List<Argument> arguments) throws BuiltInException
  {
    if (isArgumentALiteral(builtInName, argumentNumber, arguments)) return (getArgumentAsALiteral(builtInName, argumentNumber, arguments).isInteger());
    else return false;
  } // isArgumentAnInteger

  public static int getArgumentAsAnInteger(String builtInName, int argumentNumber, List<Argument> arguments) throws BuiltInException
  {
    checkThatArgumentIsAnInteger(builtInName, argumentNumber, arguments);

    return getArgumentAsALiteral(builtInName, argumentNumber, arguments).getInt();
  } // getArgumentAsAnInteger

  public static boolean isArgumentALiteral(String builtInName, int argumentNumber, List<Argument> arguments) throws BuiltInException
  {
    checkThatArgumentIsBound(builtInName, argumentNumber, arguments);

    return (arguments.get(argumentNumber) instanceof LiteralInfo);
  } // checkThatArgumentIsALiteral

  public static LiteralInfo getArgumentAsALiteral(String builtInName, int argumentNumber, List<Argument> arguments) throws BuiltInException
  {
    checkThatArgumentIsALiteral(builtInName, argumentNumber, arguments);

    return (LiteralInfo)arguments.get(argumentNumber);
  } // getArgumentAsALiteral

  // Longs

  public static void checkThatArgumentIsALong(String builtInName, int argumentNumber, List<Argument> arguments) throws BuiltInException
  {
    if (!isArgumentALong(builtInName, argumentNumber, arguments))
      throw new InvalidBuiltInArgumentException(builtInName, argumentNumber, "Expecting long literal");
  } // checkThatArgumentIsALong

  public static boolean isArgumentALong(String builtInName, int argumentNumber, List<Argument> arguments) throws BuiltInException
  {
    if (isArgumentALiteral(builtInName, argumentNumber, arguments)) 
      return (getArgumentAsALiteral(builtInName, argumentNumber, arguments).isLong());
    else return false;
  } // isArgumentALong

  public static long getArgumentAsALong(String builtInName, int argumentNumber, List<Argument> arguments) throws BuiltInException
  {
    checkThatArgumentIsALong(builtInName, argumentNumber, arguments);

    return getArgumentAsALiteral(builtInName, argumentNumber, arguments).getLong();
  } // getArgumentAsALong

  // Floats

  public static void checkThatArgumentIsAFloat(String builtInName, int argumentNumber, List<Argument> arguments) throws BuiltInException
  {
    if (!isArgumentAFloat(builtInName, argumentNumber, arguments))
      throw new InvalidBuiltInArgumentException(builtInName, argumentNumber, "Expecting float literal");
  } // checkThatArgumentIsAFloat

  public static boolean isArgumentAFloat(String builtInName, int argumentNumber, List<Argument> arguments) throws BuiltInException
  {
    if (isArgumentALiteral(builtInName, argumentNumber, arguments)) 
      return (getArgumentAsALiteral(builtInName, argumentNumber, arguments).isFloat());
    else return false;
  } // isArgumentAFloat

  public static float getArgumentAsAFloat(String builtInName, int argumentNumber, List<Argument> arguments) throws BuiltInException
  {
    checkThatArgumentIsAFloat(builtInName, argumentNumber, arguments);

    return getArgumentAsALiteral(builtInName, argumentNumber, arguments).getFloat();
  } // getArgumentAsAFloat

  // Double

  public static void checkThatArgumentIsADouble(String builtInName, int argumentNumber, List<Argument> arguments) throws BuiltInException
  {
    if (!isArgumentADouble(builtInName, argumentNumber, arguments))
      throw new InvalidBuiltInArgumentException(builtInName, argumentNumber, "Expecting float literal");
  } // checkThatArgumentIsADouble

  public static boolean isArgumentADouble(String builtInName, int argumentNumber, List<Argument> arguments) throws BuiltInException
  {
    if (isArgumentALiteral(builtInName, argumentNumber, arguments)) 
      return (getArgumentAsALiteral(builtInName, argumentNumber, arguments).isDouble());
    else return false;
  } // isArgumentADouble

  public static double getArgumentAsADouble(String builtInName, int argumentNumber, List<Argument> arguments) throws BuiltInException
  {
    checkThatArgumentIsADouble(builtInName, argumentNumber, arguments);

    return getArgumentAsALiteral(builtInName, argumentNumber, arguments).getDouble();
  } // getArgumentAsADouble

  // Booleans

  public static void checkThatArgumentIsABoolean(String builtInName, int argumentNumber, List<Argument> arguments) throws BuiltInException
  {
    if (!isArgumentABoolean(builtInName, argumentNumber, arguments))
      throw new InvalidBuiltInArgumentException(builtInName, argumentNumber, "Expecting boolean literal");
  } // checkThatArgumentIsABoolean

  public static boolean isArgumentABoolean(String builtInName, int argumentNumber, List<Argument> arguments) throws BuiltInException
  {
    if (isArgumentALiteral(builtInName, argumentNumber, arguments)) return (getArgumentAsALiteral(builtInName, argumentNumber, arguments).isBoolean());
    else return false;
  } // isArgumentABoolean

  public static boolean getArgumentAsABoolean(String builtInName, int argumentNumber, List<Argument> arguments) throws BuiltInException
  {
    checkThatArgumentIsABoolean(builtInName, argumentNumber, arguments);

    return getArgumentAsALiteral(builtInName, argumentNumber, arguments).getBoolean();
  } // getArgumentAsABoolean

  // Strings

  public static void checkThatArgumentIsAString(String builtInName, int argumentNumber, List<Argument> arguments) throws BuiltInException
  {
    if (!isArgumentAString(builtInName, argumentNumber, arguments))
      throw new InvalidBuiltInArgumentException(builtInName, argumentNumber, "Expecting string literal");
  } // checkThatArgumentIsAString

  public static boolean isArgumentAString(String builtInName, int argumentNumber, List<Argument> arguments) throws BuiltInException
  {
    if (isArgumentALiteral(builtInName, argumentNumber, arguments)) 
      return getArgumentAsALiteral(builtInName, argumentNumber, arguments).isString();
    else return false;
  } // isArgumentAString

  public static String getArgumentAsAString(String builtInName, int argumentNumber, List<Argument> arguments) throws BuiltInException
  {
    checkThatArgumentIsAString(builtInName, argumentNumber, arguments);

    return getArgumentAsALiteral(builtInName, argumentNumber, arguments).getString();
  } // getArgumentAsAString

  // Unbound argument processing methods.

  public static boolean hasUnboundArguments(String builtInName, List<Argument> arguments) throws BuiltInException
  {
    return !arguments.isEmpty() && arguments.contains(null); // An argument is unbound if its value is null.
  } // hasUnboundArguments

  public static void checkThatArgumentIsBound(String builtInName, int argumentNumber, List<Argument> arguments) throws BuiltInException
  {
    if (isUnboundArgument(builtInName, argumentNumber, arguments)) 
      throw new BuiltInException("Built-in '" + builtInName + "' is not expecting an unbound argument for argument #" + argumentNumber + ".");
  } // checkThatArgumentIsBound

  public static boolean isUnboundArgument(String builtInName, int argumentNumber, List<Argument> arguments)
  {
    return (argumentNumber >= 0) && (argumentNumber < arguments.size()) && (arguments.get(argumentNumber) == null);
  } // isUnboundArgument

  /*
  ** Get 0-offset position of first unbound argument; return -1 if no unbound arguments are found.
  */
  public static int getFirstUnboundArgument(String builtInName, List<Argument> arguments) throws BuiltInException
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
