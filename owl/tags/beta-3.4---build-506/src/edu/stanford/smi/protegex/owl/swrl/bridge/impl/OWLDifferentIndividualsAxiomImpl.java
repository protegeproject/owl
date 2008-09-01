
package edu.stanford.smi.protegex.owl.swrl.bridge.impl;

import edu.stanford.smi.protegex.owl.swrl.bridge.*;

import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.SWRLRuleEngineBridgeException;

import java.util.*;

public class OWLDifferentIndividualsAxiomImpl extends OWLNaryIndividualAxiomImpl implements OWLDifferentIndividualsAxiom
{
  public OWLDifferentIndividualsAxiomImpl(Set<OWLIndividual> individuals)
  {
    addIndividuals(individuals);
  } // OWLDifferentIndividualsAxiomImpl

  public OWLDifferentIndividualsAxiomImpl(OWLIndividual individual1, OWLIndividual individual2)
  {
    addIndividual(individual1);
    addIndividual(individual2);
  } // OWLDifferentIndividualsAxiomImpl

  public void write2OWL(OWLModel owlModel) throws SWRLRuleEngineBridgeException
  {
    // TODO:
  } // write2OWL

  public String toString() { return "differentFrom" + super.toString(); }  
} // OWLDifferentIndividualsAxiomImpl
