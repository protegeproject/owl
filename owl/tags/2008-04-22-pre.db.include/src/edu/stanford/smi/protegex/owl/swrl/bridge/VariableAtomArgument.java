
package edu.stanford.smi.protegex.owl.swrl.bridge;

import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;

/**
 ** Interface representing an argument to a SWRL atom
 */
public interface VariableAtomArgument extends AtomArgument
{
  String getVariableName();
} // VariableAtomArgument
