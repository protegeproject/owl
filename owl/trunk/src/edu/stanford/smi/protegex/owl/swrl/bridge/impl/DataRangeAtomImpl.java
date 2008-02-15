
package edu.stanford.smi.protegex.owl.swrl.bridge.impl;

import edu.stanford.smi.protegex.owl.swrl.bridge.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;

import edu.stanford.smi.protegex.owl.swrl.model.SWRLDataRangeAtom;

import java.util.Set;

/*
** Class representing a SWRL data range atom
*/
public class DataRangeAtomImpl extends AtomImpl implements DataRangeAtom
{
  private Set<OWLDatatypeValue> values;
  
  public DataRangeAtomImpl(SWRLDataRangeAtom atom) throws OWLFactoryException
  { 
    // TODO: and don't forget to call addReferencedVariableName if appropriate
    throw new OWLFactoryException("SWRL data range atoms not implemented.");
  } // DataRangeAtomImpl

} // DataRangeAtomImpl
