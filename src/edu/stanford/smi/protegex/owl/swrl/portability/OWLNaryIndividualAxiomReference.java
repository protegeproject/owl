
package edu.stanford.smi.protegex.owl.swrl.portability;

import java.util.Set;

public interface OWLNaryIndividualAxiomReference extends OWLIndividualAxiomReference
{
  Set<OWLNamedIndividualReference> getIndividuals();
}
