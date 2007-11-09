
package edu.stanford.smi.protegex.owl.swrl.bridge.impl;

import edu.stanford.smi.protegex.owl.swrl.bridge.*;

import java.util.Set;
import java.util.HashSet;

/*
** Base class representing information about atoms in a SWRL rule
*/
public class AtomImpl implements Atom
{
  private Set<String> referencedIndividualNames;
  private Set<String> referencedVariableNames;

  public AtomImpl() 
  { 
    referencedIndividualNames = new HashSet<String>();
    referencedVariableNames = new HashSet<String>();
  } // AtomImpl

  public boolean hasReferencedIndividuals() { return referencedIndividualNames.size() != 0; }
  public Set<String> getReferencedIndividualNames() { return referencedIndividualNames; }
  public boolean hasReferencedVariables() { return referencedVariableNames.size() != 0; }
  public Set<String> getReferencedVariableNames() { return referencedVariableNames; }
  
  protected void addReferencedIndividualName(String individualName) 
  { 
    if (!referencedIndividualNames.contains(individualName)) referencedIndividualNames.add(individualName); 
  } // addReferencedIndividualName
  
  protected void addReferencedVariableName(String variableName) 
  { 
    if (!referencedVariableNames.contains(variableName)) referencedVariableNames.add(variableName); 
  } // addReferencedVariableName
} // AtomImpl
