
package edu.stanford.smi.protegex.owl.swrl.portability;

public interface SWRLClassAtomReference extends SWRLAtomReference
{
	String getClassURI();

	SWRLArgumentReference getArgument1();
}
