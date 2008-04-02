
package edu.stanford.smi.protegex.owl.swrl.bridge;

public class InverseOfRestrictionInfo extends RestrictionInfo
{
  String className;
  
  public InverseOfRestrictionInfo(String className) 
  { 
    this.className = className;
  } // InverseOfRestrictionInfo
  
  public String getClassName() { return className; }

  public String toString() { return "InverseOf(" + className + ")"; }
} // InverseOfRestrictionInfo
