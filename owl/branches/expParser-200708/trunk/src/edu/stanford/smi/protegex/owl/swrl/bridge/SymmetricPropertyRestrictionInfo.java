
package edu.stanford.smi.protegex.owl.swrl.bridge;

public class SymmetricPropertyRestrictionInfo extends RestrictionInfo
{
  String propertyName;
  
  public SymmetricPropertyRestrictionInfo(String propertyName) 
  { 
    this.propertyName = propertyName;
  } // SymmetricPropertyRestrictionInfo
  
  public String getPropertyName() { return propertyName; }

  public String toString() { return "SymmetricProperty(" + propertyName + ")"; }
} // SymmetricPropertyRestrictionInfo
