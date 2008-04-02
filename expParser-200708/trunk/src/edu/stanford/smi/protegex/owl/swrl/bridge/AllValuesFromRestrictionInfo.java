
package edu.stanford.smi.protegex.owl.swrl.bridge;

public class AllValuesFromRestrictionInfo extends RestrictionInfo
{
  String className;
  
  public AllValuesFromRestrictionInfo(String className) 
  { 
    this.className = className;
  } // AllValuesFromRestrictionInfo
  
  public String getClassName() { return className; }

  public String toString() { return "AllValuesFrom(" + className + ")"; }
} // AllValuesFromRestrictionInfo
