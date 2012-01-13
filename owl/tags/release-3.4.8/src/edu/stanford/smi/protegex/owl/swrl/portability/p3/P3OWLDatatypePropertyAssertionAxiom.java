
package edu.stanford.smi.protegex.owl.swrl.portability.p3;

import edu.stanford.smi.protegex.owl.swrl.portability.OWLDataPropertyAssertionAxiomReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLLiteralReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLNamedIndividualReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLPropertyReference;

public class P3OWLDatatypePropertyAssertionAxiom extends P3OWLPropertyAssertionAxiomReference implements OWLDataPropertyAssertionAxiomReference
{
	private OWLLiteralReference object;

	public P3OWLDatatypePropertyAssertionAxiom(OWLNamedIndividualReference subject, OWLPropertyReference property, OWLLiteralReference object)
	{
		super(subject, property);
		this.object = object;
	}

	public OWLLiteralReference getObject()
	{
		return object;
	}

	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if ((obj == null) || (obj.getClass() != this.getClass()))
			return false;
		P3OWLDatatypePropertyAssertionAxiom impl = (P3OWLDatatypePropertyAssertionAxiom)obj;
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
		String result = "" + getProperty() + "(" + getSubject() + ", ";

		if (object.isOWLStringLiteral())
			result += "\"" + object + "\"";
		else
			result += "" + object;

		result += ")";

		return result;
	}
}
