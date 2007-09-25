
package edu.stanford.smi.protegex.owl.swrl.bridge;

public class FunctionalPropertyRestrictionInfo extends RestrictionInfo
{
  String propertyName;
  
  public FunctionalPropertyRestrictionInfo(String propertyName) 
  { 
    this.propertyName = propertyName;
  } // FunctionalPropertyRestrictionInfo
  
  public String getPropertyName() { return propertyName; }

  public String toString() { return "FunctionalProperty(" + propertyName + ")"; }
} // FunctionalPropertyRestrictionInfo
