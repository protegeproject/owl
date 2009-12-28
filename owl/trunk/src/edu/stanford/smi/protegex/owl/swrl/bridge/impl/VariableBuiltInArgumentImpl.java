
package edu.stanford.smi.protegex.owl.swrl.bridge.impl;

import edu.stanford.smi.protegex.owl.swrl.bridge.VariableBuiltInArgument;

/**
 ** Interface representing a variable argument to a built-in
 */
public class VariableBuiltInArgumentImpl extends BuiltInArgumentImpl implements VariableBuiltInArgument
{
  public VariableBuiltInArgumentImpl(String variableName, String prefixedVariableName) { super(variableName, prefixedVariableName); }

  public String toString() 
  {
    return "?" + getPrefixedVariableName();
  } // toString      
      
} // VariableBuiltInArgumentImpl
