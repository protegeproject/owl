
package edu.stanford.smi.protegex.owl.swrl.bridge;

/**
 ** Interface representing an argument to a SWRL atom
 */
public interface VariableAtomArgument extends AtomArgument
{
  String getVariableName();
  String getPrefixedVariableName();
} // VariableAtomArgument
