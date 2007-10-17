
package edu.stanford.smi.protegex.owl.swrl.bridge.impl;

import edu.stanford.smi.protegex.owl.swrl.bridge.*;

import java.util.*;

public abstract class OWLNaryIndividualAxiomImpl implements OWLNaryIndividualAxiom
{
  Set<OWLIndividual> individuals;
  
  public OWLNaryIndividualAxiomImpl() 
  { 
    individuals = new HashSet<OWLIndividual>();
  } // OWLNaryIndividualAxiomImpl
  
  public Set<OWLIndividual> getIndividuals() { return individuals; }

  void addIndividual(OWLIndividual owlIndividual) { individuals.add(owlIndividual); }
  void addIndividuals(Set<OWLIndividual> individuals) { this.individuals.addAll(individuals); }

} // OWLNaryIndividualAxiomImpl
