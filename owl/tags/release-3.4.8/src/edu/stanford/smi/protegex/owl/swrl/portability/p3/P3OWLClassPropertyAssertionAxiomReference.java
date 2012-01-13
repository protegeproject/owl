
package edu.stanford.smi.protegex.owl.swrl.portability.p3;

import edu.stanford.smi.protegex.owl.swrl.portability.OWLClassPropertyAssertionAxiomReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLClassReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLNamedIndividualReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLPropertyReference;

public class P3OWLClassPropertyAssertionAxiomReference extends P3OWLPropertyAssertionAxiomReference implements OWLClassPropertyAssertionAxiomReference
{
	private OWLClassReference object;

	public P3OWLClassPropertyAssertionAxiomReference(OWLNamedIndividualReference subject, OWLPropertyReference property, OWLClassReference object)
	{
		super(subject, property);
		this.object = object;
	}

	public OWLClassReference getObject()
	{
		return object;
	}

	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if ((obj == null) || (obj.getClass() != this.getClass()))
			return false;
		P3OWLClassPropertyAssertionAxiomReference impl = (P3OWLClassPropertyAssertionAxiomReference)obj;
		return (super.equals((P3OWLPropertyAssertionAxiomReference)impl) && (object != null && impl.object != null && object.equals(impl.object)));
	}

	public int hashCode()
	{
		int hash = 49;
		hash = hash + super.hashCode();
		hash = hash + (null == object ? 0 : object.hashCode());
		return hash;
	}

	public String toString()
	{
		return "" + getProperty() + "(" + getSubject() + ", " + object + ")";
	}
}
