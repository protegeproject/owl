
package edu.stanford.smi.protegex.owl.swrl.portability.p3;

import edu.stanford.smi.protegex.owl.swrl.portability.SWRLArgumentReference;
import edu.stanford.smi.protegex.owl.swrl.portability.SWRLDifferentIndividualsAtomReference;

public class P3SWRLDifferentIndividualsAtomReference extends P3SWRLBinaryAtomReference implements SWRLDifferentIndividualsAtomReference
{
	public P3SWRLDifferentIndividualsAtomReference(SWRLArgumentReference argument1, SWRLArgumentReference argument2)
	{
		super(argument1, argument2);
	}

	public P3SWRLDifferentIndividualsAtomReference()
	{
		super();
	}

	public String toString()
	{
		return "differentFrom(" + getFirstArgument() + ", " + getSecondArgument() + ")";
	}
}
