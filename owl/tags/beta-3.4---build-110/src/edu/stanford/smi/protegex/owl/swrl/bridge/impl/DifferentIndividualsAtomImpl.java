
package edu.stanford.smi.protegex.owl.swrl.bridge.impl;

import edu.stanford.smi.protegex.owl.swrl.bridge.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;

import edu.stanford.smi.protegex.owl.swrl.model.SWRLDifferentIndividualsAtom;

/*
** Class representing a SWRL same individual atom
*/
public class DifferentIndividualsAtomImpl extends IndividualsAtomImpl implements DifferentIndividualsAtom
{
  public DifferentIndividualsAtomImpl(SWRLDifferentIndividualsAtom sameIndividualAtom) throws OWLFactoryException
  { 
    super(sameIndividualAtom); 
  } // DifferentIndividualsAtomImpl

  public String toString() { return "differentFrom(" + getArgument1() + ", " + getArgument2() + ")"; }
} // DifferentIndividualsAtomImpl
