
package edu.stanford.smi.protegex.owl.swrl.portability.p3;

import edu.stanford.smi.protegex.owl.swrl.portability.OWLObjectPropertyReference;
import edu.stanford.smi.protegex.owl.swrl.portability.SWRLArgumentReference;
import edu.stanford.smi.protegex.owl.swrl.portability.SWRLObjectPropertyAtomReference;

public class P3SWRLObjectPropertyAtomReference extends P3SWRLBinaryAtomReference implements SWRLObjectPropertyAtomReference
{
	private OWLObjectPropertyReference property;

	public P3SWRLObjectPropertyAtomReference(OWLObjectPropertyReference property, SWRLArgumentReference argument1, SWRLArgumentReference argument2)
	{
		super(argument1, argument2);
		this.property = property;
	}

	public P3SWRLObjectPropertyAtomReference(OWLObjectPropertyReference property)
	{
		this.property = property;
	}

	public OWLObjectPropertyReference getProperty()
	{
		return property;
	}

	public String toString()
	{
		return getProperty().getURI() + "(" + getFirstArgument() + ", " + getSecondArgument() + ")";
	}

}
