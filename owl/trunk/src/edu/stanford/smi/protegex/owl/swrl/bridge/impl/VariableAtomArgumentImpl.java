
package edu.stanford.smi.protegex.owl.swrl.bridge.impl;

import edu.stanford.smi.protegex.owl.swrl.bridge.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;

/**
 ** Interface representing an argument to a SWRL atom
 */
public class VariableAtomArgumentImpl implements VariableAtomArgument
{
  private String variableName, prefixedVariableName;

  public VariableAtomArgumentImpl(String variableName, String prefixedVariableName) { this.variableName = variableName; this.prefixedVariableName = prefixedVariableName; }

  public String getVariableName() { return variableName; }
  public String getPrefixedVariableName() { return prefixedVariableName; }

  public String toString() { return "?" + getPrefixedVariableName(); }
} // VariableAtomArgumentImpl
