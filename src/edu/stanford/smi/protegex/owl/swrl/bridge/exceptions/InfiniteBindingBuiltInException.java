
package edu.stanford.smi.protegex.owl.swrl.bridge.exceptions;

public class InfiniteBindingBuiltInException extends BuiltInException 
{
  public InfiniteBindingBuiltInException(String builtInName) 
  { 
    super("Infinite bindings would be required for invocation of built-in '" + builtInName + "'"); 
  } // InfiniteBindingBuiltInException

  public InfiniteBindingBuiltInException(String builtInName, String message) 
  { 
    super("Infinite bindings would be required for invoation of built-in '" + builtInName + "': " + message); 
  } // InfiniteBindingBuiltInException
} // InfiniteBindingBuiltInException
