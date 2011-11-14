
package edu.stanford.smi.protegex.owl.swrl.portability.p3;

import edu.stanford.smi.protegex.owl.swrl.portability.SWRLArgumentReference;
import edu.stanford.smi.protegex.owl.swrl.portability.SWRLObjectPropertyAtomReference;

public class P3SWRLObjectPropertyAtomReference extends P3SWRLBinaryAtomReference implements SWRLObjectPropertyAtomReference
{
	private String propertyURI;

	public P3SWRLObjectPropertyAtomReference(String propertyURI, SWRLArgumentReference argument1, SWRLArgumentReference argument2)
	{
		super(argument1, argument2);
		this.propertyURI = propertyURI;
	}

	public P3SWRLObjectPropertyAtomReference(String propertyURI)
	{
		this.propertyURI = propertyURI;
	}

	public String getPropertyURI()
	{
		return propertyURI;
	}

	public String toString()
	{
		return getPropertyURI() + "(" + getFirstArgument() + ", " + getSecondArgument() + ")";
	}

}
