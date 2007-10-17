
package edu.stanford.smi.protegex.owl.swrl.bridge;

import java.util.Set;

/*
** Base class representing information about atoms in a SWRL rule
*/
public interface Atom
{
  boolean hasReferencedIndividuals();
  Set<String> getReferencedIndividualNames();
  boolean hasReferencedVariables();
  Set<String> getReferencedVariableNames();
} // Atom
