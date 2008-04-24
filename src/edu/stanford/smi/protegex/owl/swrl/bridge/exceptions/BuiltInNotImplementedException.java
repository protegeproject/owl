
package edu.stanford.smi.protegex.owl.swrl.bridge.exceptions;

public class BuiltInNotImplementedException extends BuiltInException 
{
  public BuiltInNotImplementedException(String builtInName) 
  { 
    super("builtIn " + builtInName + " not yet implemented"); 
  } // BuiltInNotImplementedException

  public BuiltInNotImplementedException(String builtInName, String message) 
  { 
    super("built-in '" + builtInName + "' not yet implemented: " + message); 
  } // BuiltInNotImplementedException
} // BuiltInNotImplementedException

