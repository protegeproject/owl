
package edu.stanford.smi.protegex.owl.swrl.bridge;

public class MinCardinalityRestrictionInfo extends RestrictionInfo
{
  String propertyName;
  int minCardinality;
  
  public MinCardinalityRestrictionInfo(String propertyName, int minCardinality) 
  { 
    this.propertyName = propertyName;
    this.minCardinality = minCardinality; 
  } // MinCardinalityRestrictionInfo
  
  public String getPropertyName() { return propertyName; }
  public int getMinCardinality() { return minCardinality; }

  public String toString() { return propertyName + ".>= " + minCardinality; }
} // MinCardinalityRestrictionInfo
