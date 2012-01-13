
package edu.stanford.smi.protegex.owl.swrl.portability.p3;

import edu.stanford.smi.protegex.owl.swrl.bridge.BuiltInArgument;
import edu.stanford.smi.protegex.owl.swrl.bridge.impl.BuiltInArgumentImpl;
import edu.stanford.smi.protegex.owl.swrl.portability.SWRLVariableReference;

public class P3SWRLVariableReference extends BuiltInArgumentImpl implements SWRLVariableReference
{
	public P3SWRLVariableReference(String variableName)
	{
		super(variableName);
	}

	public String toString()
	{
		return getVariableName();
	}

	public int compareTo(BuiltInArgument o)
	{
		return getVariableName().compareTo(o.getVariableName());
	}
}
