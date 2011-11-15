
package edu.stanford.smi.protegex.owl.swrl.portability.p3;

import edu.stanford.smi.protegex.owl.swrl.portability.OWLNamedIndividualReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLObjectPropertyAssertionAxiomReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLPropertyReference;

public class P3OWLObjectPropertyAssertionAxiomReference extends P3OWLPropertyAssertionAxiomReference implements OWLObjectPropertyAssertionAxiomReference
{
	private OWLNamedIndividualReference object;

	public P3OWLObjectPropertyAssertionAxiomReference(OWLNamedIndividualReference subject, OWLPropertyReference property, OWLNamedIndividualReference object)
	{
		super(subject, property);
		this.object = object;
	} 

	public OWLNamedIndividualReference getObject()
	{
		return object;
	}

	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if ((obj == null) || (obj.getClass() != this.getClass()))
			return false;
		P3OWLObjectPropertyAssertionAxiomReference impl = (P3OWLObjectPropertyAssertionAxiomReference)obj;
		return (super.equals((P3OWLPropertyAssertionAxiomReference)impl) && (object != null && impl.object != null && object.equals(impl.object)));
	} 

	public int hashCode()
	{
		int hash = 45;
		hash = hash + super.hashCode();
		hash = hash + (null == object ? 0 : object.hashCode());
		return hash;
	} 

	public String toString()
	{
		return "" + getProperty() + "(" + getSubject() + ", " + object + ")";
	}
}
