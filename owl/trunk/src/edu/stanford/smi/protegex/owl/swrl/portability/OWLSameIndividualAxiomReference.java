
package edu.stanford.smi.protegex.owl.swrl.portability;

public interface OWLSameIndividualAxiomReference extends OWLNaryIndividualAxiomReference
{
	OWLNamedIndividualReference getIndividual1();

	OWLNamedIndividualReference getIndividual2();
}
