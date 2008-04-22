
package edu.stanford.smi.protegex.owl.swrl.bridge.impl;

import edu.stanford.smi.protegex.owl.swrl.bridge.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;

/**
 ** Interface representing a variable argument to a built-in
 */
public class VariableBuiltInArgumentImpl extends BuiltInArgumentImpl implements VariableBuiltInArgument
{
  public VariableBuiltInArgumentImpl(String variableName) { super(variableName); }

  public String toString() 
  {
    String result = "";

    try {
      result = "?" + getVariableName();
    } catch (BuiltInException e) {
      result = "INVALID VariableBuiltInArgument: " + e.getMessage();
    } // try

    return result;
  } // toString      
      
} // VariableBuiltInArgumentImpl
