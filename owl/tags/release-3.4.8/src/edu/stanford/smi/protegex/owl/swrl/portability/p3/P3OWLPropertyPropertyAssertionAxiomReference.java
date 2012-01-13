
package edu.stanford.smi.protegex.owl.swrl.portability.p3;

import edu.stanford.smi.protegex.owl.swrl.bridge.OWLPropertyPropertyAssertionAxiomReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLNamedIndividualReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLPropertyReference;

public class P3OWLPropertyPropertyAssertionAxiomReference extends P3OWLPropertyAssertionAxiomReference implements OWLPropertyPropertyAssertionAxiomReference
{
	private OWLPropertyReference object;

	public P3OWLPropertyPropertyAssertionAxiomReference(OWLNamedIndividualReference subject, OWLPropertyReference property, OWLPropertyReference object)
	{
		super(subject, property);
		this.object = object;
	}

	public OWLPropertyReference getObject()
	{
		return object;
	}

	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if ((obj == null) || (obj.getClass() != this.getClass()))
			return false;
		P3OWLPropertyPropertyAssertionAxiomReference impl = (P3OWLPropertyPropertyAssertionAxiomReference)obj;
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
