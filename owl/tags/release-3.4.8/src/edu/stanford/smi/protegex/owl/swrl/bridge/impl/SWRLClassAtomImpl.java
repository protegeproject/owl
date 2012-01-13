
package edu.stanford.smi.protegex.owl.swrl.bridge.impl;

import edu.stanford.smi.protegex.owl.swrl.portability.SWRLArgumentReference;
import edu.stanford.smi.protegex.owl.swrl.portability.SWRLClassAtomReference;
import edu.stanford.smi.protegex.owl.swrl.portability.p3.P3SWRLAtomReference;

public class SWRLClassAtomImpl extends P3SWRLAtomReference implements SWRLClassAtomReference
{
	private SWRLArgumentReference argument1;
	private String classURI;

	public SWRLClassAtomImpl(String classURI, SWRLArgumentReference argument1)
	{
		this.classURI = classURI;
		this.argument1 = argument1;
	}

	public SWRLClassAtomImpl(String classURI)
	{
		this.classURI = classURI;
		this.argument1 = null;
	}

	public int getNumberOfArguments()
	{
		return 1;
	}

	public void setArgument1(SWRLArgumentReference argument1)
	{
		this.argument1 = argument1;
	}

	public String getClassURI()
	{
		return classURI;
	}

	public SWRLArgumentReference getArgument1()
	{
		return argument1;
	}

	public String toString()
	{
		return getClassURI() + "(" + getArgument1() + ")";
	}
}
