
package edu.stanford.smi.protegex.owl.swrl.bridge.impl;

import edu.stanford.smi.protegex.owl.swrl.bridge.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;

import java.util.Set;

/*
** Class representing a SWRL data range atom
*/
public class DataRangeAtomImpl extends AtomImpl implements DataRangeAtom
{
  private Set<OWLDatatypeValue> values;
  
  public DataRangeAtomImpl(Set<OWLDatatypeValue> values) 
  {
    this.values = values;
  } // DataRangeAtomImpl

} // DataRangeAtomImpl
