
package edu.stanford.smi.protegex.owl.swrl.portability;

import edu.stanford.smi.protegex.owl.swrl.bridge.BuiltInArgument;

public interface SWRLIndividualArgumentReference extends BuiltInArgument, SWRLArgumentReference
{
  String getURI();
}
