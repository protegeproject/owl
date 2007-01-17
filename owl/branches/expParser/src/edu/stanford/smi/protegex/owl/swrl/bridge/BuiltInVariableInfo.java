
package edu.stanford.smi.protegex.owl.swrl.bridge;

import edu.stanford.smi.protegex.owl.swrl.model.*;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLVariable;

public class BuiltInVariableInfo extends VariableInfo 
{
    // There is an equals methods defined for this class.
    private Argument builtInResult = null; // Used if a built-in binds a value to a variable.

    public BuiltInVariableInfo(SWRLVariable variable) { super(variable); }

    public void setBuiltInResult(Argument builtInResult) { this.builtInResult = builtInResult; }
    public Argument getBuiltInResult() { return builtInResult; }

  public boolean equals(Object obj)
  {
    if(this == obj) return true;
    if((obj == null) || (obj.getClass() != this.getClass())) return false;
    BuiltInVariableInfo info = (BuiltInVariableInfo)obj;
    return ((getVariableName() == info.getVariableName() || getVariableName() != null && getVariableName().equals(info.getVariableName()))) &&
            ((builtInResult == info.builtInResult) || (builtInResult != null && builtInResult.equals(info.builtInResult)));
  } // equals

  public int hashCode()
  {
    int hash = 78;
    hash = hash + (null == getVariableName() ? 0 : getVariableName().hashCode());
    hash = hash + (null == builtInResult ? 0 : builtInResult.hashCode());
    return hash;
  } // hashCode
} // BuiltInVariableInfo
