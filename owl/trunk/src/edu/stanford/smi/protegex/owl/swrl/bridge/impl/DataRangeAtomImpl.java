
package edu.stanford.smi.protegex.owl.swrl.bridge.impl;

import edu.stanford.smi.protegex.owl.swrl.bridge.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;

import edu.stanford.smi.protegex.owl.swrl.model.SWRLDataRangeAtom;

import java.util.List;

/*
** Class representing a SWRL data range atom
*/
public class DataRangeAtomImpl extends AtomImpl implements DataRangeAtom
{
  private List<OWLDatatypeValue> values;
  
  public DataRangeAtomImpl(SWRLDataRangeAtom atom) throws SWRLRuleEngineBridgeException
  { 
    // TODO: and don't forget to call addReferencedVariableName, and addReferencedIndividualName if appropriate
    throw new NotImplementedException("SWRL data range atoms not implemented.");
  } // DataRangeAtomImpl

} // DataRangeAtomImpl
