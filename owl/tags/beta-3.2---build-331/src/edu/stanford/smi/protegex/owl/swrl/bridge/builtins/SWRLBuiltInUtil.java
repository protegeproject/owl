
// Utility methods that can be used in implementations of
// built-ins. cf. edu.stanford.smi.protegex.owl.swrl.builtins.swrlb.SWRLBuiltInMethodsImpl.java for example usage.
//
// TODO: This could be made significantly shorter using generics.
// TODO: change the isArgument/areAllArgument methods to not throw exceptions.

package edu.stanford.smi.protegex.owl.swrl.bridge.builtins;

import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.swrl.model.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;

import java.util.List;

public class SWRLBuiltInUtil
{
  public static void checkNumberOfArgumentsEqualTo(String builtInName, int expecting, int actual) throws InvalidBuiltInArgumentNumberException
  {
    if (expecting != actual)
      throw new InvalidBuiltInArgumentNumberException(builtInName, expecting, actual);
  } // checkNumberOfArgumentsEqualTo

  public static void checkNumberOfArgumentsAtLeast(String builtInName, int expectingAtLeast, int actual) throws InvalidBuiltInArgumentNumberException
  {
    if (actual < expectingAtLeast) 
      throw new InvalidBuiltInArgumentNumberException(builtInName, expectingAtLeast, actual, "at least");
  } // checkNumberOfArgumentsAtLeast

  public static void checkNumberOfArgumentsAtMost(String builtInName, int expectingAtMost, int actual) throws InvalidBuiltInArgumentNumberException
  {
    if (actual > expectingAtMost) 
      throw new InvalidBuiltInArgumentNumberException(builtInName, expectingAtMost, actual, "at most");
  } // checkNumberOfArgumentsAtMost

  public static void checkThatAllArgumentsAreLiterals(String builtInName, List arguments) throws InvalidBuiltInArgumentException
  {
    for (int argumentNumber = 0; argumentNumber < arguments.size(); argumentNumber++) 
      checkThatArgumentIsALiteral(builtInName, argumentNumber, arguments);
  } // checkThatAllArgumentsAreLiterals

  public static void checkThatAllArgumentsAreNumeric(String builtInName, List arguments) throws InvalidBuiltInArgumentException
  {
    for (int argumentNumber = 0; argumentNumber < arguments.size(); argumentNumber++) 
      checkThatArgumentIsNumeric(builtInName, argumentNumber, arguments);
  } // checkThatAllArgumentsAreNumeric

  public static void checkThatAllArgumentsAreIntegers(String builtInName, List arguments) throws InvalidBuiltInArgumentException
  {
    for (int argumentNumber = 0; argumentNumber < arguments.size(); argumentNumber++) 
      checkThatArgumentIsAnInteger(builtInName, argumentNumber, arguments);
  } // checkThatAllArgumentsAreIntegers

  public static boolean areAllArgumentsIntegers(String builtInName, List arguments) throws InvalidBuiltInArgumentException
  {
    for (int argumentNumber = 0; argumentNumber < arguments.size(); argumentNumber++) 
      if (!isArgumentAnInteger(argumentNumber, arguments)) return false;
    return true;
  } // areAllArgumentsIntegers

  public static boolean areAllArgumentsLongs(String builtInName, List arguments) throws InvalidBuiltInArgumentException
  {
    for (int argumentNumber = 0; argumentNumber < arguments.size(); argumentNumber++) 
      if (!isArgumentALong(argumentNumber, arguments)) return false;
    return true;
  } // areAllArgumentsLongs

  public static boolean areAllArgumentsDoubles(String builtInName, List arguments) throws InvalidBuiltInArgumentException, LiteralConversionException
  {
    for (int argumentNumber = 0; argumentNumber < arguments.size(); argumentNumber++) 
      if (!isArgumentADouble(argumentNumber, arguments)) return false;
    return true;
  } // areAllArgumentsDoubles

  public static boolean areAllArgumentsBooleans(String builtInName, List arguments) throws InvalidBuiltInArgumentException, LiteralConversionException
  {
    for (int argumentNumber = 0; argumentNumber < arguments.size(); argumentNumber++) 
      if (!isArgumentABoolean(argumentNumber, arguments)) return false;
    return true;
  } // areAllArgumentsBooleans

  public static boolean areAllArgumentLiterals(String builtInName, List arguments) throws InvalidBuiltInArgumentException
  {
    for (int argumentNumber = 0; argumentNumber < arguments.size(); argumentNumber++) 
      if (!isArgumentALiteral(argumentNumber, arguments)) return false;
    return true;
  } // areAllArgumentsIntegers

  public static boolean areAllArgumentsFloats(String builtInName, List arguments) throws InvalidBuiltInArgumentException, LiteralConversionException
  {
    for (int argumentNumber = 0; argumentNumber < arguments.size(); argumentNumber++) 
      if (!isArgumentAFloat(argumentNumber, arguments)) return false;
    return true;
  } // areAllArgumentsFloats

  public static boolean areAllArgumentsNumeric(String builtInName, List arguments) throws InvalidBuiltInArgumentException
  {
    for (int argumentNumber = 0; argumentNumber < arguments.size(); argumentNumber++) 
      if (!isArgumentNumeric(argumentNumber, arguments)) return false;
    return true;
  } // areAllArgumentsNumeric

  public static boolean areAllArgumentsStrings(String builtInName, List arguments) throws InvalidBuiltInArgumentException
  {
    for (int argumentNumber = 0; argumentNumber < arguments.size(); argumentNumber++) 
      if (!isArgumentAString(argumentNumber, arguments)) return false;
    return true;
  } // areAllArgumentsStrings

  public static boolean areAllArgumentsOfAnOrderedType(String builtInName, List arguments) throws InvalidBuiltInArgumentException
  {
    for (int argumentNumber = 0; argumentNumber < arguments.size(); argumentNumber++) 
      if (!isArgumentOfAnOrderedType(argumentNumber, arguments)) return false;
    return true;
  } // areAllArgumentsOfAnOrderedType

  public static void checkThatAllArgumentsAreFloats(String builtInName, List arguments) throws InvalidBuiltInArgumentException, LiteralConversionException
  {
    for (int argumentNumber = 0; argumentNumber < arguments.size(); argumentNumber++) 
      checkThatArgumentIsAFloat(builtInName, argumentNumber, arguments);
  } // checkThatAllArgumentsAreFloats

  public static void checkThatAllArgumentsAreStrings(String builtInName, List arguments) throws InvalidBuiltInArgumentException
  {
    for (int argumentNumber = 0; argumentNumber < arguments.size(); argumentNumber++) 
      checkThatArgumentIsAString(builtInName, argumentNumber, arguments);
  } // checkThatAllArgumentsAreStrings

  public static void checkThatAllArgumentsAreOfAnOrderedType(String builtInName, List arguments) throws InvalidBuiltInArgumentException
  {
    for (int argumentNumber = 0; argumentNumber < arguments.size(); argumentNumber++) 
      checkThatArgumentIsOfAnOrderedType(builtInName, argumentNumber, arguments);
  } // checkThatAllArgumentsAreOfAnOrderedType

  public static void checkThatArgumentIsALiteral(String builtInName, int argumentNumber, List arguments) throws InvalidBuiltInArgumentException
  {
    if (!(arguments.get(argumentNumber) instanceof LiteralInfo)) 
      throw new InvalidBuiltInArgumentException(builtInName, argumentNumber, "Expecting literal");
  } // checkThatArgumentIsALiteral

  public static void checkThatArgumentIsNumeric(String builtInName, int argumentNumber, List arguments) throws InvalidBuiltInArgumentException
  {
    if (!isArgumentNumeric(argumentNumber, arguments))
      throw new InvalidBuiltInArgumentException(builtInName, argumentNumber, "Expecting numeric literal");
  } // checkThatArgumentIsNumeric

  public static void checkThatArgumentIsOfAnOrderedType(String builtInName, int argumentNumber, List arguments) throws InvalidBuiltInArgumentException
  {
    if (!isArgumentOfAnOrderedType(argumentNumber, arguments))
      throw new InvalidBuiltInArgumentException(builtInName, argumentNumber, "Expecting ordered type");
  } // checkThatArgumentIsOfAnOrderedType

  public static boolean isArgumentOfAnOrderedType(int argumentNumber, List arguments) throws InvalidBuiltInArgumentException
  {
    return (isArgumentNumeric(argumentNumber, arguments) || isArgumentAString(argumentNumber, arguments));
  } // isArgumentOfAnOrderedType

  public static void checkThatArgumentIsAnIndividualName(String builtInName, int argumentNumber, List arguments) throws InvalidBuiltInArgumentException
  {
    if (!(arguments.get(argumentNumber) instanceof IndividualInfo)) 
      throw new InvalidBuiltInArgumentException(builtInName, argumentNumber, "Expecting individual name");
  } // checkThatArgumentIsAnIndividualName

  public static boolean isArgumentNumeric(int argumentNumber, List arguments) throws InvalidBuiltInArgumentException
  {
    if (isArgumentALiteral(argumentNumber, arguments)) return getArgumentAsALiteral(argumentNumber, arguments).isNumeric();
    else return false;
  } // isArgumentNumeric

  public static boolean isArgumentNonNumeric(int argumentNumber, List arguments) throws InvalidBuiltInArgumentException
  {
    if (isArgumentALiteral(argumentNumber, arguments))
      return !getArgumentAsALiteral(argumentNumber, arguments).isNumeric();
    else return false;
  } // isArgumentNonNumeric

  public static void checkThatArgumentIsNonNumeric(String builtInName, int argumentNumber, List arguments) throws InvalidBuiltInArgumentException
  {
    if (!isArgumentNonNumeric(argumentNumber, arguments))
      throw new InvalidBuiltInArgumentException(builtInName, argumentNumber, "Expecting non-numeric literal");
  } // checkThatArgumentIsNonNumeric

  // Integers
  
  public static void checkThatArgumentIsAnInteger(String builtInName, int argumentNumber, List arguments) throws InvalidBuiltInArgumentException
  {
    if (!isArgumentAnInteger(argumentNumber, arguments))
      throw new InvalidBuiltInArgumentException(builtInName, argumentNumber, "Expecting integer literal");
  } // checkThatArgumentIsAnInteger

  public static boolean isArgumentAnInteger(int argumentNumber, List arguments) throws InvalidBuiltInArgumentException
  {
    if (isArgumentALiteral(argumentNumber, arguments)) return (getArgumentAsALiteral(argumentNumber, arguments).isInteger());
    else return false;
  } // isArgumentAnInteger

  public static int getArgumentAsAnInteger(String builtInName, int argumentNumber, List arguments) throws InvalidBuiltInArgumentException, LiteralConversionException
  {
    checkThatArgumentIsAnInteger(builtInName, argumentNumber, arguments);

    return getArgumentAsALiteral(argumentNumber, arguments).getInt();
  } // getArgumentAsAnInteger

  public static boolean isArgumentALiteral(int argumentNumber, List arguments) 
  {
    return (arguments.get(argumentNumber) instanceof LiteralInfo);
  } // checkThatArgumentIsALiteral

  public static LiteralInfo getArgumentAsALiteral(int argumentNumber, List arguments) throws InvalidBuiltInArgumentException
  {
    return (LiteralInfo)arguments.get(argumentNumber);
  } // getArgumentAsALiteral

  // Longs

  public static void checkThatArgumentIsALong(String builtInName, int argumentNumber, List arguments) throws InvalidBuiltInArgumentException
  {
    if (!isArgumentALong(argumentNumber, arguments))
      throw new InvalidBuiltInArgumentException(builtInName, argumentNumber, "Expecting long literal");
  } // checkThatArgumentIsALong

  public static boolean isArgumentALong(int argumentNumber, List arguments) throws InvalidBuiltInArgumentException
  {
    if (isArgumentALiteral(argumentNumber, arguments)) 
      return (getArgumentAsALiteral(argumentNumber, arguments).isLong());
    else return false;
  } // isArgumentALong

  public static long getArgumentAsALong(String builtInName, int argumentNumber, List arguments) throws InvalidBuiltInArgumentException, LiteralConversionException
  {
    checkThatArgumentIsALong(builtInName, argumentNumber, arguments);

    return getArgumentAsALiteral(argumentNumber, arguments).getLong();
  } // getArgumentAsALong

  // Floats

  public static void checkThatArgumentIsAFloat(String builtInName, int argumentNumber, List arguments) throws InvalidBuiltInArgumentException, LiteralConversionException
  {
    if (!isArgumentAFloat(argumentNumber, arguments))
      throw new InvalidBuiltInArgumentException(builtInName, argumentNumber, "Expecting float literal");
  } // checkThatArgumentIsAFloat

  public static boolean isArgumentAFloat(int argumentNumber, List arguments) throws InvalidBuiltInArgumentException, LiteralConversionException
  {
    if (isArgumentALiteral(argumentNumber, arguments)) 
      return (getArgumentAsALiteral(argumentNumber, arguments).isFloat());
    else return false;
  } // isArgumentAFloat

  public static float getArgumentAsAFloat(String builtInName, int argumentNumber, List arguments) throws InvalidBuiltInArgumentException, LiteralConversionException
  {
    checkThatArgumentIsAFloat(builtInName, argumentNumber, arguments);

    return getArgumentAsALiteral(argumentNumber, arguments).getFloat();
  } // getArgumentAsAFloat

  // Double

  public static void checkThatArgumentIsADouble(String builtInName, int argumentNumber, List arguments) throws InvalidBuiltInArgumentException, LiteralConversionException
  {
    if (!isArgumentADouble(argumentNumber, arguments))
      throw new InvalidBuiltInArgumentException(builtInName, argumentNumber, "Expecting float literal");
  } // checkThatArgumentIsADouble

  public static boolean isArgumentADouble(int argumentNumber, List arguments) throws InvalidBuiltInArgumentException, LiteralConversionException
  {
    if (isArgumentALiteral(argumentNumber, arguments)) 
      return (getArgumentAsALiteral(argumentNumber, arguments).isDouble());
    else return false;
  } // isArgumentADouble

  public static double getArgumentAsADouble(String builtInName, int argumentNumber, List arguments) throws InvalidBuiltInArgumentException, LiteralConversionException
  {
    checkThatArgumentIsADouble(builtInName, argumentNumber, arguments);

    return getArgumentAsALiteral(argumentNumber, arguments).getDouble();
  } // getArgumentAsADouble

  // Booleans

  public static void checkThatArgumentIsABoolean(String builtInName, int argumentNumber, List arguments) throws InvalidBuiltInArgumentException
  {
    if (!isArgumentABoolean(argumentNumber, arguments))
      throw new InvalidBuiltInArgumentException(builtInName, argumentNumber, "Expecting boolean literal");
  } // checkThatArgumentIsABoolean

  public static boolean isArgumentABoolean(int argumentNumber, List arguments) throws InvalidBuiltInArgumentException
  {
    if (isArgumentALiteral(argumentNumber, arguments)) 
      return (getArgumentAsALiteral(argumentNumber, arguments).isBoolean());
    else return false;
  } // isArgumentABoolean

  public static boolean getArgumentAsABoolean(String builtInName, int argumentNumber, List arguments) throws InvalidBuiltInArgumentException
  {
    checkThatArgumentIsABoolean(builtInName, argumentNumber, arguments);

    return getArgumentAsALiteral(argumentNumber, arguments).getBoolean();
  } // getArgumentAsABoolean

  // Strings

  public static void checkThatArgumentIsAString(String builtInName, int argumentNumber, List arguments) throws InvalidBuiltInArgumentException
  {
    if (!isArgumentAString(argumentNumber, arguments))
      throw new InvalidBuiltInArgumentException(builtInName, argumentNumber, "Expecting string literal");
  } // checkThatArgumentIsAString

  public static boolean isArgumentAString(int argumentNumber, List arguments) throws InvalidBuiltInArgumentException
  {
    if (isArgumentALiteral(argumentNumber, arguments)) 
      return (getArgumentAsALiteral(argumentNumber, arguments).isString());
    else return false;
  } // isArgumentAString

  public static String getArgumentAsAString(String builtInName, int argumentNumber, List arguments) throws InvalidBuiltInArgumentException
  {
    checkThatArgumentIsAString(builtInName, argumentNumber, arguments);

    return getArgumentAsALiteral(argumentNumber, arguments).getString();
  } // getArgumentAsAString

} // SWRLBuiltInUtil
