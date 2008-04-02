
package edu.stanford.smi.protegex.owl.swrl.bridge.exceptions;

public class IncompatibleBuiltInClassException extends BuiltInException 
{
  public IncompatibleBuiltInClassException(String namespaceName, String className, String message) 
  { 
    super("Incompatible built-in class '" + className + "' for namespace '" + namespaceName + "." + message);
  } //IncompatibleBuiltInClassException

} // IncompatibleBuiltInClassException
