
package edu.stanford.smi.protegex.owl.swrl.bridge;

public class ComplementOfRestrictionInfo extends RestrictionInfo
{
  String className;
  
  public ComplementOfRestrictionInfo(String className) 
  { 
    this.className = className;
  } // ComplementOfRestrictionInfo
  
  public String getClassName() { return className; }

  public String toString() { return "ComplementOf(" + className + ")"; }
} // ComplementOfRestrictionInfo
