
package edu.stanford.smi.protegex.owl.swrl.portability;

import edu.stanford.smi.protegex.owl.swrl.bridge.Argument;

public interface SWRLArgumentReference extends Argument
{
	boolean isVariable();

	boolean isUnbound();

	boolean isBound();

	String getVariableName();

	void setVariableName(String variableName);

	void setUnbound();
}
