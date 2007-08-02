
package edu.stanford.smi.protegex.owl.swrl.bridge;

import java.util.Set;

public class UnionOfRestrictionInfo extends RestrictionInfo
{
  Set<String> classNames;
  
  public UnionOfRestrictionInfo(Set<String> classNames) 
  { 
    this.classNames = classNames;
  } // UnionOfRestrictionInfo
  
  public Set<String> getClassNames() { return classNames; }

  public String toString() { return "UnionOf(" + classNames + ")"; }
} // UnionOfRestrictionInfo
