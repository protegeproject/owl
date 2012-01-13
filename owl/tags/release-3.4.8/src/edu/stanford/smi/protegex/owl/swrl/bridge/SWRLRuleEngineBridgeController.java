package edu.stanford.smi.protegex.owl.swrl.bridge;

import java.util.Set;

import edu.stanford.smi.protegex.owl.swrl.portability.OWLAxiomReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLNamedIndividualReference;

/**
 * This interface provides access methods to retrieve knowledge inferred by a rule engine.
 */
public interface SWRLRuleEngineBridgeController 
{
  int getNumberOfInferredOWLIndividuals();
  int getNumberOfInferredOWLAxioms();

  Set<OWLNamedIndividualReference> getInferredOWLIndividuals();
  Set<OWLAxiomReference> getInferredOWLAxioms();
}
