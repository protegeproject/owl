
// Info object representing a SWRL individuals atom. 

package edu.stanford.smi.protegex.owl.swrl.bridge;

import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;

import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.swrl.model.*;

public abstract class IndividualsAtomInfo extends AtomInfo
{
  private IndividualInfo argument1, argument2;
  
  public IndividualsAtomInfo(SWRLIndividualsAtom individualsAtom, String name) throws SWRLRuleEngineBridgeException
  {
    super(name);

    argument1 = new IndividualInfo((OWLIndividual)individualsAtom.getArgument1());
    argument2 = new IndividualInfo((OWLIndividual)individualsAtom.getArgument2());
    
    // If argument1 or argument2 are individuals, add their names to the referenced individuals list.
    if (argument1 instanceof IndividualInfo) addReferencedIndividualName(argument1.getName());
    if (argument2 instanceof IndividualInfo) addReferencedIndividualName(argument2.getName());
  } // IndividualsAtomInfo
  
  public IndividualInfo getArgument1() { return argument1; }
  public IndividualInfo getArgument2() { return argument2; }
} // IndividualsAtomInfo

