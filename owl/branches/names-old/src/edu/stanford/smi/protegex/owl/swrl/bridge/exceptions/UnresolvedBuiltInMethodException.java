
package edu.stanford.smi.protegex.owl.swrl.bridge.exceptions;

public class UnresolvedBuiltInMethodException extends BuiltInException 
{
  public UnresolvedBuiltInMethodException(String namespaceName, String builtInMethodName, String message) 
  { 
     super("Unresolved built-in method '" + builtInMethodName + "' in namespace '" + namespaceName + "'. " + message);
  } // UnresolvedBuiltInMethodException
} // UnresolvedBuiltInMethodException

