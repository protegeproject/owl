
package edu.stanford.smi.protegex.owl.swrl.portability;

public interface OWLClassAssertionAxiomReference extends OWLAxiomReference
{
	OWLClassReference getDescription();

	OWLNamedIndividualReference getIndividual();
}
