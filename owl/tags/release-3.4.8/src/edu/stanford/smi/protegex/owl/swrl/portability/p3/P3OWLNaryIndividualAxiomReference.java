
package edu.stanford.smi.protegex.owl.swrl.portability.p3;

import java.util.HashSet;
import java.util.Set;

import edu.stanford.smi.protegex.owl.swrl.portability.OWLNamedIndividualReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLNaryIndividualAxiomReference;

public abstract class P3OWLNaryIndividualAxiomReference implements OWLNaryIndividualAxiomReference
{
	Set<OWLNamedIndividualReference> individuals;

	public P3OWLNaryIndividualAxiomReference()
	{
		individuals = new HashSet<OWLNamedIndividualReference>();
	}

	public Set<OWLNamedIndividualReference> getIndividuals()
	{
		return individuals;
	}

	void addIndividual(OWLNamedIndividualReference owlIndividual)
	{
		individuals.add(owlIndividual);
	}

	void addIndividuals(Set<OWLNamedIndividualReference> individuals)
	{
		this.individuals.addAll(individuals);
	}

	public String toString()
	{
		String result = "(";
		boolean isFirst = true;

		for (OWLNamedIndividualReference individual : individuals) {
			if (!isFirst)
				result += ", ";
			result += individual.toString();
			isFirst = false;
		} // for

		result += ")";

		return result;
	}

}
