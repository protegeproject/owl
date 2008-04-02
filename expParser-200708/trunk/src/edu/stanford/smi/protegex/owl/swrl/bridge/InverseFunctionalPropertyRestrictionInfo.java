
package edu.stanford.smi.protegex.owl.swrl.bridge;

public class InverseFunctionalPropertyRestrictionInfo extends RestrictionInfo
{
  String propertyName;
  
  public InverseFunctionalPropertyRestrictionInfo(String propertyName) 
  { 
    this.propertyName = propertyName;
  } // InverseFunctionalPropertyRestrictionInfo
  
  public String getPropertyName() { return propertyName; }

  public String toString() { return "InverseFunctionalProperty(" + propertyName + ")"; }
} // InverseFunctionalPropertyRestrictionInfo
