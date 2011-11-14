
package edu.stanford.smi.protegex.owl.swrl.portability;

public interface SWRLDataPropertyAtomReference extends SWRLAtomReference
{
	String getPropertyURI();

	SWRLArgumentReference getFirstArgument();

	SWRLArgumentReference getSecondArgument();
}
