
package edu.stanford.smi.protegex.owl.swrl.portability;

import java.util.Set;

public interface OWLNamedIndividualReference extends OWLEntityReference, OWLPropertyValueReference
{
	Set<OWLClassReference> getTypes();

	Set<OWLNamedIndividualReference> getSameIndividuals();

	Set<OWLNamedIndividualReference> getDifferentIndividuals();

	void addType(OWLClassReference owlClass);

	boolean hasType(String classURI);
}
