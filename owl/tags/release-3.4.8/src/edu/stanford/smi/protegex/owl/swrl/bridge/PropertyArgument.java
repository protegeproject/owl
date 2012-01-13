
package edu.stanford.smi.protegex.owl.swrl.bridge;

import edu.stanford.smi.protegex.owl.swrl.portability.SWRLArgumentReference;

/**
 * Interface representing OWL property arguments to atoms and built-ins
 */
public interface PropertyArgument extends BuiltInArgument, SWRLArgumentReference
{
  String getURI();
}
