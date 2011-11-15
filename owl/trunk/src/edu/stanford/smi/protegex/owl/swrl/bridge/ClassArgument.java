
package edu.stanford.smi.protegex.owl.swrl.bridge;

import edu.stanford.smi.protegex.owl.swrl.portability.SWRLArgumentReference;

/**
 * Interface representing OWL named class arguments to SWRL atoms and SWRL built-ins
 */
public interface ClassArgument extends BuiltInArgument, SWRLArgumentReference
{
  String getURI();
}
