
package edu.stanford.smi.protegex.owl.swrl.bridge.exceptions;

public class UnresolvedBuiltInClassException extends BuiltInException 
{
  public UnresolvedBuiltInClassException(String ruleName, String namespaceName, String message) 
  { 
     super("Unresolved built-in class for namespace '" + namespaceName + "' in rule '" + ruleName + "': " + message);
  } // UnresolvedBuiltInClassException
} // UnresolvedBuiltInClassException
