
// Info object representing a SWRL same individual atom. 

package edu.stanford.smi.protegex.owl.swrl.bridge;

import edu.stanford.smi.protegex.owl.swrl.model.SWRLSameIndividualAtom;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.SWRLRuleEngineBridgeException;

public class SameIndividualAtomInfo extends IndividualsAtomInfo 
{
  public SameIndividualAtomInfo(SWRLSameIndividualAtom sameIndividualAtom) throws SWRLRuleEngineBridgeException
  { 
    super(sameIndividualAtom); 
  } // SameIndividualAtomInfo
} // SameIndividualAtomInfo
