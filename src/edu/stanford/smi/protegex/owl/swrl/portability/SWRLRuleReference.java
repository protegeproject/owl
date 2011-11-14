
package edu.stanford.smi.protegex.owl.swrl.portability;

import java.util.List;

public interface SWRLRuleReference extends OWLAxiomReference, OWLEntityReference
{
	List<SWRLAtomReference> getHeadAtoms();

	List<SWRLAtomReference> getBodyAtoms();

	// TODO: these will not be in the OWLAPI.
	void setBodyAtoms(List<SWRLAtomReference> atom);

	void appendAtomsToBody(List<SWRLAtomReference> atom);

	String getRuleText();

	void setRuleText(String text);

	String getRuleGroupName();

	boolean isEnabled();

	void setEnabled(boolean isEnabled);
}
