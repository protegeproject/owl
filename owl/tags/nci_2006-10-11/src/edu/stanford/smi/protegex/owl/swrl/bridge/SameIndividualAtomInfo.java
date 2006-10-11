
// Info object representing a SWRL same individual atom. 

package edu.stanford.smi.protegex.owl.swrl.bridge;

import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;

import edu.stanford.smi.protegex.owl.swrl.model.*;

public class SameIndividualAtomInfo extends IndividualsAtomInfo 
{
  public SameIndividualAtomInfo(SWRLSameIndividualAtom sameIndividualAtom) throws SWRLRuleEngineBridgeException
  { 
    super(sameIndividualAtom, "sameAs"); 
  } // SameIndividualAtomInfo
} // SameIndividualAtomInfo
