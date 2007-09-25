
package edu.stanford.smi.protegex.owl.swrl.bridge;

public class SameAsRestrictionInfo extends RestrictionInfo
{
  String individualName1, individualName2;
  
  public SameAsRestrictionInfo(String individualName1, String individualName2) 
  { 
    super("sameAs");
    this.individualName1 = individualName1; 
    this.individualName2 = individualName2; 
  } // SameAsRestrictionInfo
  
  public String getIndividualName1() { return individualName1; }
  public String getIndividualName2() { return individualName2; }
} // SameAsRestrictionInfo


