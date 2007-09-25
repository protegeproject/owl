
package edu.stanford.smi.protegex.owl.swrl.bridge;

public class MaxCardinalityRestrictionInfo extends RestrictionInfo
{
  String propertyName;
  int maxCardinality;
  
  public MaxCardinalityRestrictionInfo(String propertyName, int maxCardinality) 
  { 
    this.propertyName = propertyName;
    this.maxCardinality = maxCardinality; 
  } // MaxCardinalityRestrictionInfo
  
  public String getPropertyName() { return propertyName; }
  public int getMaxCardinality() { return maxCardinality; }

  public String toString() { return propertyName + ".<= " + maxCardinality; }
} // MaxCardinalityRestrictionInfo
