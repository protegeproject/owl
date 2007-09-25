
package edu.stanford.smi.protegex.owl.swrl.bridge.exceptions;

public class IncompatibleBuiltInMethodException extends BuiltInException 
{
  public IncompatibleBuiltInMethodException(String namespaceName, String builtInMethodName, String message) 
  { 
    super("Incompatible built-in method '" + builtInMethodName + "' in namespace '" + namespaceName + "." + message);
  } //IncompatibleBuiltInMethodException

} // IncompatibleBuiltInMethodException
