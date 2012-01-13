
package edu.stanford.smi.protegex.owl.swrl.portability;

public interface OWLSubClassAxiomReference extends OWLAxiomReference
{
	OWLClassReference getSubClass();

	OWLClassReference getSuperClass();
}
