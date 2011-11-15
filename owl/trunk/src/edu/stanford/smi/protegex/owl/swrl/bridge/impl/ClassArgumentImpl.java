
package edu.stanford.smi.protegex.owl.swrl.bridge.impl;

import edu.stanford.smi.protegex.owl.swrl.bridge.BuiltInArgument;
import edu.stanford.smi.protegex.owl.swrl.bridge.ClassArgument;

public class ClassArgumentImpl extends BuiltInArgumentImpl implements ClassArgument
{
	private String classURI;

	public ClassArgumentImpl(String classURI)
	{
		this.classURI = classURI;
	}

	public String getURI()
	{
		return classURI;
	}

	public boolean isNamedClass()
	{
		return true;
	}

	public String toString()
	{
		return getURI();
	}

	public int compareTo(BuiltInArgument o)
	{
		return classURI.compareTo(((ClassArgument)o).getURI());
	}

	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if ((obj == null) || (obj.getClass() != this.getClass()))
			return false;
		ClassArgumentImpl impl = (ClassArgumentImpl)obj;
		return (getURI() == impl.getURI() || (getURI() != null && getURI().equals(impl.getURI())));
	}

	public int hashCode()
	{
		int hash = 12;
		hash = hash + (null == getURI() ? 0 : getURI().hashCode());
		return hash;
	}
}
