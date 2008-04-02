
package edu.stanford.smi.protegex.owl.swrl.bridge;

import java.util.List;
import java.util.ArrayList;

public class AllDifferentRestrictionInfo extends RestrictionInfo
{
  List individualNames;
  
  public AllDifferentRestrictionInfo() 
  { 
    super("allDifferent");
    individualNames = new ArrayList();
  } // AllDifferentRestrictionInfo
  
  public List getIndividualNames() { return individualNames; }
  public void addIndividualName(String individualName) { individualNames.add(individualName); }
} // AllDifferentRestrictionInfo
