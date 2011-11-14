
package edu.stanford.smi.protegex.owl.swrl.portability.p3;

import edu.stanford.smi.protegex.owl.swrl.portability.OWLNamedIndividualReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLSameIndividualAxiomReference;

public class P3OWLSameIndividualAxiomReference extends P3OWLNaryIndividualAxiomReference implements OWLSameIndividualAxiomReference
{
	private OWLNamedIndividualReference individual1, individual2;

	public P3OWLSameIndividualAxiomReference(OWLNamedIndividualReference individual1, OWLNamedIndividualReference individual2)
	{
		addIndividual(individual1);
		addIndividual(individual2);
		this.individual1 = individual1;
		this.individual2 = individual2;
	}

	public OWLNamedIndividualReference getIndividual1()
	{
		return individual1;
	}

	public OWLNamedIndividualReference getIndividual2()
	{
		return individual2;
	}

	public String toString()
	{
		return "sameAs" + super.toString();
	}
}
