
package edu.stanford.smi.protegex.owl.swrl.portability.p3;

import java.util.Set;

import edu.stanford.smi.protegex.owl.swrl.portability.OWLDifferentIndividualsAxiomReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLNamedIndividualReference;

public class P3OWLDifferentIndividualsAxiomReference extends P3OWLNaryIndividualAxiomReference implements OWLDifferentIndividualsAxiomReference
{
	public P3OWLDifferentIndividualsAxiomReference(Set<OWLNamedIndividualReference> individuals)
	{
		addIndividuals(individuals);
	}

	public P3OWLDifferentIndividualsAxiomReference(OWLNamedIndividualReference individual1, OWLNamedIndividualReference individual2)
	{
		addIndividual(individual1);
		addIndividual(individual2);
	}

	public String toString()
	{
		return "differentFrom" + super.toString();
	}
}
