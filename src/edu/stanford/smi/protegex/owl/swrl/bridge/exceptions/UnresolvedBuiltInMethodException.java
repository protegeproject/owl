
package edu.stanford.smi.protegex.owl.swrl.bridge.exceptions;

public class UnresolvedBuiltInMethodException extends BuiltInException 
{
  public UnresolvedBuiltInMethodException(String ruleName, String namespaceName, String builtInName, String message) 
  { 
     super("unresolved built-in method '" + namespaceName + ":" + builtInName + "' in rule '" + ruleName + "'. " + message);
  } // UnresolvedBuiltInMethodException
} // UnresolvedBuiltInMethodException

