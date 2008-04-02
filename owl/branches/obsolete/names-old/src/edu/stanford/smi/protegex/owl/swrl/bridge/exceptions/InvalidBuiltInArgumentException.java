
package edu.stanford.smi.protegex.owl.swrl.bridge.exceptions;

public class InvalidBuiltInArgumentException extends BuiltInException
{
  public InvalidBuiltInArgumentException(String builtInName, int argumentNumber, String message) 
  { 
    super(message + " for argument " + argumentNumber + " for built-in '" + builtInName + "'"); 
  } // InvalidBuiltInArgumentException

  public InvalidBuiltInArgumentException(String builtInName, String message) 
  { 
    super(message + " for built-in '" + builtInName + "'"); 
  } // InvalidBuiltInArgumentException

} // InvalidBuiltInArgumentException
