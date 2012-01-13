
package edu.stanford.smi.protegex.owl.swrl.portability.p3;

import edu.stanford.smi.protegex.owl.swrl.portability.OWLPropertyReference;
import edu.stanford.smi.protegex.owl.swrl.portability.SWRLArgumentReference;
import edu.stanford.smi.protegex.owl.swrl.portability.SWRLBinaryAtomReference;

public class P3SWRLBinaryAtomReference extends P3SWRLAtomReference implements SWRLBinaryAtomReference
{
	private OWLPropertyReference property;
	private SWRLArgumentReference argument1, argument2;

	public P3SWRLBinaryAtomReference(OWLPropertyReference property)
	{
		this.property = property;
		this.argument1 = null;
		this.argument2 = null;
	}

	public P3SWRLBinaryAtomReference()
	{
		this.property = null;
		this.argument1 = null;
		this.argument2 = null;
	}

	public int getNumberOfArguments()
	{
		return 2;
	}

	public OWLPropertyReference getProperty()
	{
		return property;
	}

	public void setArgument1(SWRLArgumentReference argument1)
	{
		this.argument1 = argument1;
	}

	public void setArgument2(SWRLArgumentReference argument2)
	{
		this.argument2 = argument2;
	}

	public SWRLArgumentReference getFirstArgument()
	{
		return argument1;
	}

	public SWRLArgumentReference getSecondArgument()
	{
		return argument2;
	}

	protected P3SWRLBinaryAtomReference(SWRLArgumentReference argument1, SWRLArgumentReference argument2)
	{
		this.argument1 = argument1;
		this.argument2 = argument2;
	}
}
