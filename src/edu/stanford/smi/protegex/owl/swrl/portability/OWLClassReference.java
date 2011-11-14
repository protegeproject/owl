
package edu.stanford.smi.protegex.owl.swrl.portability;

import java.util.Set;

public interface OWLClassReference extends OWLDescriptionReference, OWLEntityReference
{
	Set<OWLClassReference> getTypes();
	Set<OWLClassReference> getSuperClasses();
	Set<OWLClassReference> getSubClasses();
  Set<OWLClassReference> getEquivalentClasses();
}
