
package edu.stanford.smi.protegex.owl.swrl.bridge;

public class DisjointWithRestrictionInfo extends RestrictionInfo
{
  String className;
  
  public DisjointWithRestrictionInfo(String className) 
  { 
    this.className = className;
  } // DisjointWithRestrictionInfo
  
  public String getClassName() { return className; }

  public String toString() { return "DisjointWith(" + className + ")"; }
} // DisjointWithRestrictionInfo
