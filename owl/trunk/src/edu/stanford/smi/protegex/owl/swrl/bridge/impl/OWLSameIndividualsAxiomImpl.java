
package edu.stanford.smi.protegex.owl.swrl.bridge.impl;

import edu.stanford.smi.protegex.owl.swrl.bridge.*;

public class OWLSameIndividualsAxiomImpl extends OWLNaryIndividualAxiomImpl implements OWLSameIndividualsAxiom
{
  private OWLIndividual individual1, individual2;

  public OWLSameIndividualsAxiomImpl(OWLIndividual individual1, OWLIndividual individual2)
  {
    addIndividual(individual1);
    addIndividual(individual2);
    this.individual1 = individual1;
    this.individual2 = individual2;
  } // OWLSameIndividualsAxiomImpl

  public OWLIndividual getIndividual1() { return individual1; }
  public OWLIndividual getIndividual2() { return individual2; }
} // OWLSameIndividualsAxiomImpl
