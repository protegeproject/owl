
package edu.stanford.smi.protegex.owl.swrl.bridge;

public class HasValueRestrictionInfo extends RestrictionInfo
{
  String individualName = "";
  LiteralInfo literalInfo = null;
  
  public HasValueRestrictionInfo(String individualName) 
  { 
    this.individualName = individualName;
  } // HasValueRestrictionInfo

  public HasValueRestrictionInfo(LiteralInfo literalInfo) 
  { 
    this.literalInfo = literalInfo;
  } // HasValueRestrictionInfo

  public boolean isDatavalue() { return literalInfo != null; }
  
  public String getIndividualName() { return individualName; }
  public LiteralInfo getDatavalue() { return literalInfo; }

  public String toString() { return "HasValue(" + (isDatavalue() ? literalInfo : individualName) + ")"; }
} // HasValueRestrictionInfo
