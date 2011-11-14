
package edu.stanford.smi.protegex.owl.swrl.portability.p3;

import edu.stanford.smi.protegex.owl.swrl.portability.SWRLArgumentReference;
import edu.stanford.smi.protegex.owl.swrl.portability.SWRLSameIndividualAtomReference;

public class P3SWRLSameIndividualAtomReference extends P3SWRLBinaryAtomReference implements SWRLSameIndividualAtomReference
{
	public P3SWRLSameIndividualAtomReference(SWRLArgumentReference argument1, SWRLArgumentReference argument2)
	{
		super(argument1, argument2);
	}

	public P3SWRLSameIndividualAtomReference()
	{
		super();
	}

	public String toString()
	{
		return "sameAs(" + getFirstArgument() + ", " + getSecondArgument() + ")";
	}
}
