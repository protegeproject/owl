
// Info object representing a SWRL variable. A variable can be an argument to an atom or to a built-in. It is also used to pass results back
// from a built-in.

package edu.stanford.smi.protegex.owl.swrl.bridge;

import edu.stanford.smi.protegex.owl.swrl.model.*;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLVariable;

public class VariableInfo extends Info implements Argument
{
  private Argument builtInResult = null; // Used if a built-in assigne a value to a variable.

  public VariableInfo(SWRLVariable variable) { super(variable.getName()); }

  public void setBuiltInResult(Argument builtInResult) { this.builtInResult = builtInResult; }
  public Argument getBuiltInResult() { return builtInResult; }

  public boolean equals(Object obj)
  {
    if(this == obj) return true;
    if((obj == null) || (obj.getClass() != this.getClass())) return false;
    VariableInfo info = (VariableInfo)obj;
    return ((getName() == info.getName() || (getName() != null && getName().equals(info.getName()))) &&
            ((builtInResult == info.builtInResult) || (builtInResult != null && builtInResult.equals(info.builtInResult))));
  } // equals

  public int hashCode()
  {
    int hash = 78;
    hash = hash + (null == getName() ? 0 : getName().hashCode());
    hash = hash + (null == builtInResult ? 0 : builtInResult.hashCode());
    return hash;
  } // hashCode

} // VariableInfo    
