package edu.stanford.smi.protegex.owl.swrl.bridge.impl;

import edu.stanford.smi.protegex.owl.swrl.bridge.BuiltInArgument;
import edu.stanford.smi.protegex.owl.swrl.bridge.CollectionArgument;

public class CollectionArgumentImpl extends BuiltInArgumentImpl implements CollectionArgument
{
	private String collectionName, collectionGroupID;

	public CollectionArgumentImpl(String collectionName, String collectionGroupID)
	{
		this.collectionName = collectionName;
		this.collectionGroupID = collectionGroupID;
	}

	public String getGroupID()
	{
		return collectionGroupID;
	}

	public String getName()
	{
		return collectionName;
	}

	public String toString()
	{
		return getName() + "@" + getGroupID();
	}

	public int compareTo(BuiltInArgument ca)
	{
		return toString().compareTo(((CollectionArgument)ca).toString());
	}

	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if ((obj == null) || (obj.getClass() != this.getClass()))
			return false;
		CollectionArgumentImpl impl = (CollectionArgumentImpl)obj;
		return (getName() == impl.getName() || (getName() != null && getName().equals(impl.getName())))
				&& (getGroupID() == impl.getGroupID() || (getGroupID() != null && getGroupID().equals(impl.getGroupID())));
	}

	public int hashCode()
	{
		int hash = 12;
		hash = hash + (null == getName() ? 0 : getName().hashCode());
		hash = hash + (null == getGroupID() ? 0 : getGroupID().hashCode());
		return hash;
	}
}
