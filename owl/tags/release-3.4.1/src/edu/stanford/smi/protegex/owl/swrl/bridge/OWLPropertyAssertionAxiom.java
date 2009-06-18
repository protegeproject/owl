
package edu.stanford.smi.protegex.owl.swrl.bridge;

import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.SWRLRuleEngineBridgeException;

public interface OWLPropertyAssertionAxiom extends OWLAxiom
{
  OWLIndividual getSubject();
  OWLProperty getProperty();
} // OWLPropertyAssertionAxiom
