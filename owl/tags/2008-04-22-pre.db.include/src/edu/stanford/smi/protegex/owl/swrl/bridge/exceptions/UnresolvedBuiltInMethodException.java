
package edu.stanford.smi.protegex.owl.swrl.bridge.exceptions;

public class UnresolvedBuiltInMethodException extends BuiltInException 
{
  public UnresolvedBuiltInMethodException(String ruleName, String prefix, String builtInName, String message) 
  { 
     super("unresolved built-in method '" + prefix + ":" + builtInName + "' in rule '" + ruleName + "'. " + message);
  } // UnresolvedBuiltInMethodException
} // UnresolvedBuiltInMethodException

