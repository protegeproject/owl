
package edu.stanford.smi.protegex.owl.swrl.portability.p3;

import edu.stanford.smi.protegex.owl.swrl.portability.OWLClassAssertionAxiomReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLClassReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLNamedIndividualReference;

public class P3OWLClassAssertionAxiomReference implements OWLClassAssertionAxiomReference
{
	private OWLNamedIndividualReference individual;
	private OWLClassReference description;

	public P3OWLClassAssertionAxiomReference(OWLNamedIndividualReference individual, OWLClassReference description)
	{
		this.individual = individual;
		this.description = description;
	}

	public OWLClassReference getDescription()
	{
		return description;
	}

	public OWLNamedIndividualReference getIndividual()
	{
		return individual;
	}

	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if ((obj == null) || (obj.getClass() != this.getClass()))
			return false;
		P3OWLClassAssertionAxiomReference impl = (P3OWLClassAssertionAxiomReference)obj;
		return (super.equals((P3OWLClassAssertionAxiomReference)impl) && (description != null && impl.description != null && description.equals(impl.description)) && (individual != null
				&& impl.individual != null && individual.equals(impl.individual)));
	}

	public int hashCode()
	{
		int hash = 49;
		hash = hash + super.hashCode();
		hash = hash + (null == description ? 0 : description.hashCode());
		hash = hash + (null == individual ? 0 : individual.hashCode());
		return hash;
	}

	public String toString()
	{
		return "" + getDescription() + "(" + getIndividual() + ")";
	}
}
