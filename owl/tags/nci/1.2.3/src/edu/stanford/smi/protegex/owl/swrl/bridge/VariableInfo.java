
// Info object representing a SWRL variable.

package edu.stanford.smi.protegex.owl.swrl.bridge;

import edu.stanford.smi.protegex.owl.swrl.model.*;

// A variable can be an argument to an atom or to a built-in.

public class VariableInfo extends Info implements Argument
{
  public VariableInfo(SWRLVariable variable) { super(variable.getName()); }

} // VariableInfo    
