
package edu.stanford.smi.protegex.owl.swrl.portability;

public interface OWLRestrictionReference extends OWLDescriptionReference
{
	OWLClassReference asOWLClass();

	OWLPropertyReference getProperty();
}
