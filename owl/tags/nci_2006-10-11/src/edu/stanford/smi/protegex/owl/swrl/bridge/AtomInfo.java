
// Base class representing information about atoms in a SWRL rule.

package edu.stanford.smi.protegex.owl.swrl.bridge;

import java.util.*;

public class AtomInfo extends Info 
{
  private List referencedIndividuals;

  public AtomInfo(String name) 
  { 
    super(name); 
    referencedIndividuals = new ArrayList();
  } // ArrayList

  public boolean hasReferencedIndividuals() { return referencedIndividuals.size() != 0; }
  public List getReferencedIndividualNames() { return referencedIndividuals; }

  protected void addReferencedIndividualName(String individualName) { referencedIndividuals.add(individualName); }
} // AtomInfo
