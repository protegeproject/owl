
package edu.stanford.smi.protegex.owl.swrl.bridge.exceptions;

public class UnresolvedBuiltInClassException extends BuiltInException 
{
  public UnresolvedBuiltInClassException(String namespaceName, String message) 
  { 
     super("Unresolved built-in class for namespace '" + namespaceName + "'. " + message);
  } // UnresolvedBuiltInClassException
} // UnresolvedBuiltInClassException
