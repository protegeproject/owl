
package edu.stanford.smi.protegex.owl.swrl.bridge;

import java.util.Set;

public class OneOfRestrictionInfo extends RestrictionInfo
{
  Set<String> individualNames;
  
  public OneOfRestrictionInfo(Set<String> individualNames) 
  { 
    this.individualNames = individualNames;
  } // OneOfRestrictionInfo
  
  public Set<String> getIndividualNames() { return individualNames; }

  public String toString() { return "OneOf(" + individualNames + ")"; }
} // OneOfRestrictionInfo
