
// Info object representing a SWRL variable. A variable can be an argument to an atom or to a built-in. It is also used to pass results back
// from a built-in.

package edu.stanford.smi.protegex.owl.swrl.bridge;

import edu.stanford.smi.protegex.owl.swrl.model.*;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLVariable;

public abstract class VariableInfo extends Info implements Argument
{
  // There is an equals methods defined for this class.
  private String variableName;

  public VariableInfo(SWRLVariable variable) 
  { 
    variableName = variable.getName();
  } // VariableInfo

  public String getVariableName() { return variableName; }

  public boolean equals(Object obj)
  {
    if(this == obj) return true;
    if((obj == null) || (obj.getClass() != this.getClass())) return false;
    VariableInfo info = (VariableInfo)obj;
    return (getVariableName() == info.getVariableName() || getVariableName() != null && getVariableName().equals(info.getVariableName()));
  } // equals

  public int hashCode()
  {
    int hash = 78;
    hash = hash + (null == getVariableName() ? 0 : getVariableName().hashCode());
    return hash;
  } // hashCode

} // VariableInfo    
