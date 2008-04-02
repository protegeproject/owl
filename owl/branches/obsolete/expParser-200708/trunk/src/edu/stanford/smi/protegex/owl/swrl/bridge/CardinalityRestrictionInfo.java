
package edu.stanford.smi.protegex.owl.swrl.bridge;

public class CardinalityRestrictionInfo extends RestrictionInfo
{
  String propertyName;
  int cardinality;
  
  public CardinalityRestrictionInfo(String propertyName, int cardinality) 
  { 
    this.propertyName = propertyName;
    this.cardinality = cardinality; 
  } // CardinalityRestrictionInfo
  
  public String getPropertyName() { return propertyName; }
  public int getCardinality() { return cardinality; }

  public String toString() { return propertyName + ".= " + cardinality; }
} // CardinalityRestrictionInfo
