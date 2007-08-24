
package edu.stanford.smi.protegex.owl.swrl.bridge;

import java.util.Set;

public class IntersectionOfRestrictionInfo extends RestrictionInfo
{
  Set<String> classNames;
  
  public IntersectionOfRestrictionInfo(Set<String> classNames) 
  { 
    this.classNames = classNames;
  } // IntersectionOfRestrictionInfo
  
  public Set<String> getClassNames() { return classNames; }

  public String toString() { return "IntersectionOf(" + classNames + ")"; }
} // IntersectionOfRestrictionInfo
