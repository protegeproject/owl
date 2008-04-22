
package edu.stanford.smi.protegex.owl.swrl.bridge;

import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.SWRLRuleEngineBridgeException;

import edu.stanford.smi.protegex.owl.model.OWLModel;

public interface OWLPropertyAssertionAxiom extends OWLAxiom
{
  OWLIndividual getSubject();
  OWLProperty getProperty();

  // TODO: temporary
  void write2OWL(OWLModel owlModel) throws SWRLRuleEngineBridgeException;
} // OWLPropertyAssertionAxiom
