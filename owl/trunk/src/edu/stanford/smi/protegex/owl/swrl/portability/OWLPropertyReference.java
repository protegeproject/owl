
package edu.stanford.smi.protegex.owl.swrl.portability;

import java.util.Set;

public interface OWLPropertyReference extends OWLEntityReference
{
	Set<OWLClassReference> getDomainClasses();

	Set<OWLClassReference> getRangeClasses();

	Set<OWLPropertyReference> getTypes();

	Set<OWLPropertyReference> getSuperProperties();

	Set<OWLPropertyReference> getSubProperties();

	Set<OWLPropertyReference> getEquivalentProperties();
}
