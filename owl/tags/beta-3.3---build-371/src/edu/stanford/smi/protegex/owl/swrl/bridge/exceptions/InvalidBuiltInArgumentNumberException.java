
package edu.stanford.smi.protegex.owl.swrl.bridge.exceptions;

public class InvalidBuiltInArgumentNumberException extends BuiltInException 
{
  public InvalidBuiltInArgumentNumberException(String builtInName, int expecting, int actual) 
  { 
    super("Invalid number of arguments for built-in " + builtInName + " - expecting " + expecting + " argument(s), got " + actual);
  } // InvalidBuiltInArgumentNumberException

  public InvalidBuiltInArgumentNumberException(String builtInName, int expecting, int actual, String message) 
  { 
    super("Invalid number of arguments for built-in '" + builtInName + "' - expecting " + message + " " + expecting + " argument(s), got " + actual);
  } // InvalidBuiltInArgumentNumberException

} // InvalidBuiltInArgumentNumberException
