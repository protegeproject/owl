
package edu.stanford.smi.protegex.owl.swrl.bridge.exceptions;

public class IncompatibleBuiltInClassException extends BuiltInException 
{
  public IncompatibleBuiltInClassException(String ruleName, String namespaceName, String className, String message) 
  { 
    super("incompatible built-in class '" + className + "' for namespace '" + namespaceName + "' used in rule '" + ruleName + "': " + message);
  } //IncompatibleBuiltInClassException

} // IncompatibleBuiltInClassException
