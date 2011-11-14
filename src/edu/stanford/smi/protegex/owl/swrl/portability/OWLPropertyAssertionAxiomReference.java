
package edu.stanford.smi.protegex.owl.swrl.portability;

public interface OWLPropertyAssertionAxiomReference extends OWLAxiomReference
{
	OWLNamedIndividualReference getSubject();

	OWLPropertyReference getProperty();
}
