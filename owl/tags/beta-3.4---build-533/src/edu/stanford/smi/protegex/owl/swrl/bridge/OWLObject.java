
package edu.stanford.smi.protegex.owl.swrl.bridge;

import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.SWRLRuleEngineBridgeException;

public interface OWLObject
{
  void write2OWL(OWLModel owlModel) throws SWRLRuleEngineBridgeException;
} // OWLObject
