
package edu.stanford.smi.protegex.owl.swrl.portability.p3;

import edu.stanford.smi.protegex.owl.swrl.bridge.BuiltInArgument;
import edu.stanford.smi.protegex.owl.swrl.bridge.impl.BuiltInArgumentImpl;
import edu.stanford.smi.protegex.owl.swrl.portability.SWRLIndividualArgumentReference;

public class P3SWRLIndividualArgumentReference extends BuiltInArgumentImpl implements SWRLIndividualArgumentReference
{
	private String individualURI;

	public P3SWRLIndividualArgumentReference(String individualURI)
	{
		this.individualURI = individualURI;
	}

	public String getURI()
	{
		return individualURI;
	}

	public String toString()
	{
		return getURI();
	}

	public int compareTo(BuiltInArgument o)
	{
		return individualURI.compareTo(((SWRLIndividualArgumentReference)o).getURI());
	}

	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if ((obj == null) || (obj.getClass() != this.getClass()))
			return false;
		P3SWRLIndividualArgumentReference impl = (P3SWRLIndividualArgumentReference)obj;
		return (getURI() == impl.getURI() || (getURI() != null && getURI().equals(impl.getURI())));
	}

	public int hashCode()
	{
		int hash = 12;
		hash = hash + (null == getURI() ? 0 : getURI().hashCode());
		return hash;
	}
}