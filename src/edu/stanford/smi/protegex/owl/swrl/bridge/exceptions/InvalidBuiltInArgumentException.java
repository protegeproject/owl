
package edu.stanford.smi.protegex.owl.swrl.bridge.exceptions;

public class InvalidBuiltInArgumentException extends BuiltInException
{
  public InvalidBuiltInArgumentException(String builtInName, int argumentNumber, String message) 
  { 
    super(message + " for argument #" + argumentNumber + " in built-in '" + builtInName + "'"); 
  } // InvalidBuiltInArgumentException

  public InvalidBuiltInArgumentException(String builtInName, String message) 
  { 
    super(message + " for1 built-in '" + builtInName + "'"); 
  } // InvalidBuiltInArgumentException

} // InvalidBuiltInArgumentException
