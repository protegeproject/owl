
package edu.stanford.smi.protegex.owl.swrl.bridge.exceptions;

public class InvalidBuiltInNameException extends BuiltInException 
{
  public InvalidBuiltInNameException(String builtInName) 
  { 
    super("Unknown built-in: " + builtInName);
  } // InvalidBuiltInNameException
} // InvalidBuiltInNameException

