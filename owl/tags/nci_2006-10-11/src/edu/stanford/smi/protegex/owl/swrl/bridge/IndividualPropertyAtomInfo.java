
// Info object representing a SWRL individual property atom. 

package edu.stanford.smi.protegex.owl.swrl.bridge;

import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;
import edu.stanford.smi.protegex.owl.swrl.model.*;
import edu.stanford.smi.protegex.owl.model.*;

public class IndividualPropertyAtomInfo extends AtomInfo
{
  private Argument argument1, argument2;

  public IndividualPropertyAtomInfo(SWRLIndividualPropertyAtom atom) 
    throws SWRLRuleEngineBridgeException
  {
    super(atom.getPropertyPredicate().getName());
    
    if (atom.getArgument1() instanceof SWRLVariable)
      argument1 = new VariableInfo((SWRLVariable)atom.getArgument1());
    else if (atom.getArgument1() instanceof SWRLIndividual)
      argument1 = new IndividualInfo(atom.getArgument1().getName());
    else throw new SWRLRuleEngineBridgeException("Unknown argument #1 to individual property atom " + atom.getName() + ". Expecting variable or individual.");

    if (atom.getArgument2() instanceof SWRLVariable)
      argument2 = new VariableInfo((SWRLVariable)atom.getArgument2());
    else if (atom.getArgument2() instanceof OWLIndividual)
      argument2 = new IndividualInfo((OWLIndividual)atom.getArgument2());
    else throw new SWRLRuleEngineBridgeException("Unknown argument #2 to individual property atom " + atom.getName() + ". Expecting variable or individual.");

    // If argument1 or 2 is an individual, add its name to the referenced individuals list for this atom.
    if (argument1 instanceof IndividualInfo) addReferencedIndividualName(argument1.getName());
    if (argument2 instanceof IndividualInfo) addReferencedIndividualName(argument2.getName());
  } // IndividualPropertyAtomInfo
  
  public Argument getArgument1() { return argument1; }
  public Argument getArgument2() { return argument2; }
  
} // IndividualPropertyAtomInfo
