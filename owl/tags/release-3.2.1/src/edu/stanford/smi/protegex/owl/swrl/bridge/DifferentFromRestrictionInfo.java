
package edu.stanford.smi.protegex.owl.swrl.bridge;

public class DifferentFromRestrictionInfo extends RestrictionInfo
{
  String individualName1, individualName2;
  
  public DifferentFromRestrictionInfo(String individualName1, String individualName2) 
  { 
    this.individualName1 = individualName1; 
    this.individualName2 = individualName2; 
  } // DifferentFromRestrictionInfo
  
  public String getIndividualName1() { return individualName1; }
  public String getIndividualName2() { return individualName2; }
} // DifferentFromRestrictionInfo
