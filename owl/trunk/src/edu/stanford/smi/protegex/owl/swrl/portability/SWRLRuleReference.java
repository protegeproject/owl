
package edu.stanford.smi.protegex.owl.swrl.portability;

import java.util.List;

public interface SWRLRuleReference extends OWLAxiomReference, OWLEntityReference
{
	List<SWRLAtomReference> getHeadAtoms();

	List<SWRLAtomReference> getBodyAtoms();

	void setBodyAtoms(List<SWRLAtomReference> atom);

	void appendAtomsToBody(List<SWRLAtomReference> atom);

	String getRuleText();

	void setRuleText(String text);

	String getRuleGroupName();

	boolean isEnabled();

	void setEnabled(boolean isEnabled);
}
