
package edu.stanford.smi.protegex.owl.swrl.portability.p3;

import edu.stanford.smi.protegex.owl.swrl.portability.OWLNamedIndividualReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLPropertyReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLPropertyAssertionAxiomReference;

public abstract class P3OWLPropertyAssertionAxiomReference implements OWLPropertyAssertionAxiomReference
{
	private OWLNamedIndividualReference subject;
	private OWLPropertyReference property;

	public P3OWLPropertyAssertionAxiomReference(OWLNamedIndividualReference subject, OWLPropertyReference property)
	{
		this.subject = subject;
		this.property = property;
	}

	public OWLNamedIndividualReference getSubject()
	{
		return subject;
	}

	public OWLPropertyReference getProperty()
	{
		return property;
	}

	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if ((obj == null) || (obj.getClass() != this.getClass()))
			return false;
		P3OWLPropertyAssertionAxiomReference impl = (P3OWLPropertyAssertionAxiomReference)obj;
		return ((subject != null && impl.subject != null && subject.equals(impl.subject)) && (property != null && impl.property != null && property
				.equals(impl.property)));
	}

	public int hashCode()
	{
		int hash = 45;
		hash = hash + (null == subject ? 0 : subject.hashCode());
		hash = hash + (null == property ? 0 : property.hashCode());
		return hash;
	}

}
