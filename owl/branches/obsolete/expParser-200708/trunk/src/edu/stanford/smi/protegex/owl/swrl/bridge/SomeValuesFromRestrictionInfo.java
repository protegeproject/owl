
package edu.stanford.smi.protegex.owl.swrl.bridge;

public class SomeValuesFromRestrictionInfo extends RestrictionInfo
{
  String className;
  
  public SomeValuesFromRestrictionInfo(String className) 
  { 
    this.className = className;
  } // SomeValuesFromRestrictionInfo
  
  public String getClassName() { return className; }

  public String toString() { return "SomeValuesFrom(" + className + ")"; }
} // SomeValuesFromRestrictionInfo
