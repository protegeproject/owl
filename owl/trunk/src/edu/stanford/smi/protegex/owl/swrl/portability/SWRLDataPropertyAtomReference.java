
package edu.stanford.smi.protegex.owl.swrl.portability;

public interface SWRLDataPropertyAtomReference extends SWRLAtomReference
{
	OWLDataPropertyReference getProperty();

	SWRLArgumentReference getFirstArgument();

	SWRLArgumentReference getSecondArgument();
}
