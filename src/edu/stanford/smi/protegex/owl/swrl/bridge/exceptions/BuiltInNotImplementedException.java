
package edu.stanford.smi.protegex.owl.swrl.bridge.exceptions;

public class BuiltInNotImplementedException extends BuiltInException 
{
  public BuiltInNotImplementedException(String builtInName) 
  { 
    super("BuiltIn " + builtInName + " not yet implemented"); 
  } // BuiltInNotImplementedException

  public BuiltInNotImplementedException(String builtInName, String message) 
  { 
    super("BuiltIn " + builtInName + " not yet implemented - " + message); 
  } // BuiltInNotImplementedException
} // BuiltInNotImplementedException

