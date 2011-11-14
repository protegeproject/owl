
package edu.stanford.smi.protegex.owl.swrl.portability;

public interface SWRLBinaryAtomReference extends SWRLAtomReference
{
	SWRLArgumentReference getFirstArgument();

	SWRLArgumentReference getSecondArgument();
}
