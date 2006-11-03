
package edu.stanford.smi.protegex.owl.swrl.bridge.exceptions;

public class IncompatibleBuiltInMethodException extends BuiltInException 
{
  public IncompatibleBuiltInMethodException(String ruleName, String namespaceName, String builtInMethodName, String message) 
  { 
    super("Incompatible built-in method '" + namespaceName + ":" + builtInMethodName + "' used in rule '" + ruleName + "': " + message);
  } //IncompatibleBuiltInMethodException

} // IncompatibleBuiltInMethodException
