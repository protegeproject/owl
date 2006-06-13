
// Info object representing a SWRL variable.

package edu.stanford.smi.protegex.owl.swrl.bridge;

import edu.stanford.smi.protegex.owl.swrl.model.*;

// A variable can be an argument to an atom or to a built-in.

public class VariableInfo extends Info implements Argument
{
  public VariableInfo(SWRLVariable variable) { super(variable.getName()); }

  public boolean equals(Object obj)
  {
    if(this == obj) return true;
    if((obj == null) || (obj.getClass() != this.getClass())) return false;
    VariableInfo info = (VariableInfo)obj;
    return (getName() == info.getName() || (getName() != null && getName().equals(info.getName())));
  } // equals

  public int hashCode()
  {
    int hash = 78;
    hash = hash + (null == getName() ? 0 : getName().hashCode());
    return hash;
  } // hashCode

} // VariableInfo    
