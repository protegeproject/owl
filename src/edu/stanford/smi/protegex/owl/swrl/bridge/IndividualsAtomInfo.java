
// Info object representing a SWRL individuals atom. 

package edu.stanford.smi.protegex.owl.swrl.bridge;

import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;

import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.swrl.model.*;

public abstract class IndividualsAtomInfo extends AtomInfo
{
  private Argument argument1, argument2;
  
  public IndividualsAtomInfo(SWRLIndividualsAtom atom, String name) throws SWRLRuleEngineBridgeException
  {
    super(name);

    if (atom.getArgument1() instanceof SWRLVariable) {
      argument1 = new VariableInfo((SWRLVariable)atom.getArgument1());
    } else if (atom.getArgument1() instanceof OWLIndividual) {
      argument1 = new IndividualInfo((OWLIndividual)atom.getArgument1());
      addReferencedIndividualName(argument1.getName());
    } else throw new InvalidResourceNameException("Invalid argument 1 " + atom.getArgument1().getName() + " passed to atom.");

    if (atom.getArgument2() instanceof SWRLVariable) {
      argument2 = new VariableInfo((SWRLVariable)atom.getArgument2());
    } else if (atom.getArgument2() instanceof OWLIndividual) {
      argument2 = new IndividualInfo((OWLIndividual)atom.getArgument2());
      addReferencedIndividualName(argument2.getName());
    } else throw new InvalidResourceNameException("Invalid argument 1 " + atom.getArgument2().getName() + " passed to atom.");

  } // IndividualsAtomInfo
  
  public Argument getArgument1() { return argument1; }
  public Argument getArgument2() { return argument2; }
} // IndividualsAtomInfo

