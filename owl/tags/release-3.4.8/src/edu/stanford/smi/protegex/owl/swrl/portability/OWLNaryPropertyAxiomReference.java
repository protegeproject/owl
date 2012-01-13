
package edu.stanford.smi.protegex.owl.swrl.portability;

import java.util.Set;

public interface OWLNaryPropertyAxiomReference extends OWLPropertyAxiomReference
{
	Set<OWLPropertyReference> getProperties();
}
