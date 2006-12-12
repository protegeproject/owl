
// Info object representing a SWRL data range atom.

package edu.stanford.smi.protegex.owl.swrl.bridge;

import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;

import edu.stanford.smi.protegex.owl.swrl.model.*;

import java.util.List;

public class DataRangeAtomInfo extends AtomInfo
{
  private List<LiteralInfo> literals;
  
  public DataRangeAtomInfo(SWRLDataRangeAtom dataRangeAtom) throws SWRLRuleEngineBridgeException
  { 
    // TODO: and don't forget to call addReferencedVariableName, and addReferencedIndividualName if appropriate
    throw new NotImplementedException("SWRL data range atoms not implemented.");
  } // DataRangeAtomInfo

} // DataRangeAtomInfo
