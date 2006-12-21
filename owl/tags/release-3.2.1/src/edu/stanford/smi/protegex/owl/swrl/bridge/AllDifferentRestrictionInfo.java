
package edu.stanford.smi.protegex.owl.swrl.bridge;

import java.util.*;

public class AllDifferentRestrictionInfo extends RestrictionInfo
{
  Set<String> individualNames;
  
  public AllDifferentRestrictionInfo() 
  { 
    individualNames = new HashSet<String>();
  } // AllDifferentRestrictionInfo
  
  public Set<String> getIndividualNames() { return individualNames; }
  public void addIndividualName(String individualName) { individualNames.add(individualName); }
} // AllDifferentRestrictionInfo
