
package edu.stanford.smi.protegex.owl.swrl.portability;

import edu.stanford.smi.protegex.owl.swrl.bridge.BuiltInArgument;
import edu.stanford.smi.protegex.owl.swrl.sqwrl.DataValue;

public interface SWRLLiteralArgumentReference extends BuiltInArgument, SWRLArgumentReference
{
	DataValue getLiteral();
}