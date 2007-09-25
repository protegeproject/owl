
// Base class representing information about atoms in a SWRL rule.

package edu.stanford.smi.protegex.owl.swrl.bridge;

import java.util.*;

public class AtomInfo extends Info 
{
  private Set<String> referencedIndividualNames;
  private HashMap<String, VariableInfo> referencedVariables;

  public AtomInfo() 
  { 
    referencedIndividualNames = new HashSet<String>();
    referencedVariables = new HashMap<String, VariableInfo>();
  } // ArrayList

  public boolean hasReferencedIndividuals() { return referencedIndividualNames.size() != 0; }
  public Set<String> getReferencedIndividualNames() { return referencedIndividualNames; }
  public boolean hasReferencedVariables() { return referencedVariables.size() != 0; }
  public Set<String> getReferencedVariableNames() { return referencedVariables.keySet(); }

  public Set<String> getReferencedObjectVariableNames() 
  {
    Set<String> result = new HashSet<String>();
    
    for (String variableName: getReferencedVariableNames()) {
      if (isReferencedObjectVariable(variableName)) result.add(variableName);
    } // for
    return result;
  } // getReferencedObjectVariableNames
  
  public Set<String> getReferencedDatatypeVariableNames() 
  {
    Set<String> result = new HashSet<String>();
    
    for (String variableName: getReferencedVariableNames()) {
      if (isReferencedDatatypeVariable(variableName)) result.add(variableName);
    } // while
    return result;
  } // getReferencedDatatypeVariableNames
  
  public boolean isReferencedObjectVariable(String variableName) 
  { 
    return referencedVariables.containsKey(variableName) && referencedVariables.get(variableName) instanceof ObjectVariableInfo; 
  } // isReferencedObjectVariable
  
  public boolean isReferencedDatatypeVariable(String variableName) 
  { 
    return referencedVariables.containsKey(variableName) && referencedVariables.get(variableName) instanceof DatatypeVariableInfo; 
  }
  
  protected void addReferencedIndividualName(String individualName) 
  { 
    if (!referencedIndividualNames.contains(individualName)) referencedIndividualNames.add(individualName); 
  } // addReferencedIndividualName
  
  protected void addReferencedVariable(String variableName, VariableInfo variableInfo) 
  { 
    if (!referencedVariables.containsKey(variableName)) referencedVariables.put(variableName, variableInfo); 
  } // addReferencedVariable
} // AtomInfo
