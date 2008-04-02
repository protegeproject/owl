
package edu.stanford.smi.protegex.owl.swrl.bridge;

public class TransitivePropertyRestrictionInfo extends RestrictionInfo
{
  String propertyName;
  
  public TransitivePropertyRestrictionInfo(String propertyName) 
  { 
    this.propertyName = propertyName;
  } // TransitivePropertyRestrictionInfo
  
  public String getPropertyName() { return propertyName; }

  public String toString() { return "TransitiveProperty(" + propertyName + ")"; }
} // TransitivePropertyRestrictionInfo
