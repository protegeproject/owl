
// Base class representing information about atoms in a SWRL rule.

package edu.stanford.smi.protegex.owl.swrl.bridge;

import java.util.*;

public class AtomInfo extends Info 
{
  private List referencedIndividualNames;
  private List referencedVariableNames;

  public AtomInfo(String name) 
  { 
    super(name); 
    referencedIndividualNames = new ArrayList();
    referencedVariableNames = new ArrayList();
  } // ArrayList

  public boolean hasReferencedIndividuals() { return referencedIndividualNames.size() != 0; }
  public List getReferencedIndividualNames() { return referencedIndividualNames; }
  public boolean hasReferencedVariables() { return referencedVariableNames.size() != 0; }
  public List getReferencedVariableNames() { return referencedVariableNames; }

  protected void addReferencedIndividualName(String individualName) 
  { 
    if (!referencedIndividualNames.contains(individualName)) referencedIndividualNames.add(individualName); 
  } // addReferencedIndividualName

  protected void addReferencedVariableName(String variableName) 
  { 
    if (!referencedIndividualNames.contains(variableName)) referencedVariableNames.add(variableName); 
  } // addReferencedVariableName

} // AtomInfo
