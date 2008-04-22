
package edu.stanford.smi.protegex.owl.swrl.bridge.impl;

import edu.stanford.smi.protegex.owl.swrl.bridge.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;

import edu.stanford.smi.protegex.owl.swrl.model.SWRLSameIndividualAtom;

/*
** Class representing a SWRL same individual atom
*/
public class SameIndividualAtomImpl extends IndividualsAtomImpl implements SameIndividualAtom
{
  public SameIndividualAtomImpl(SWRLSameIndividualAtom sameIndividualAtom) throws OWLFactoryException
  { 
    super(sameIndividualAtom); 
  } // SameIndividualAtomImpl

  public String toString() { return "sameAs(" + getArgument1() + ", " + getArgument2() + ")"; }
} // SameIndividualAtomImpl
