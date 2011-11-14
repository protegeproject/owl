
package edu.stanford.smi.protegex.owl.swrl.portability.p3;

import java.util.Set;

import edu.stanford.smi.protegex.owl.swrl.bridge.OWLDataValue;
import edu.stanford.smi.protegex.owl.swrl.portability.SWRLDataRangeAtomReference;

public class P3SWRLDataRangeAtomReference extends P3SWRLAtomReference implements SWRLDataRangeAtomReference
{
	private Set<OWLDataValue> values;

	public P3SWRLDataRangeAtomReference(Set<OWLDataValue> values)
	{
		this.values = values;
	}

	public int getNumberOfArguments()
	{
		return values.size();
	}
}
