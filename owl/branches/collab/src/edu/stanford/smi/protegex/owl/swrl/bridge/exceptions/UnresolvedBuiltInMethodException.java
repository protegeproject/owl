
package edu.stanford.smi.protegex.owl.swrl.bridge.exceptions;

public class UnresolvedBuiltInMethodException extends BuiltInException 
{
  public UnresolvedBuiltInMethodException(String ruleName, String namespaceName, String builtInMethodName, String message) 
  { 
     super("Unresolved built-in method '" + namespaceName + ":" + builtInMethodName + "' in rule '" + ruleName + "'." + message);
  } // UnresolvedBuiltInMethodException
} // UnresolvedBuiltInMethodException

