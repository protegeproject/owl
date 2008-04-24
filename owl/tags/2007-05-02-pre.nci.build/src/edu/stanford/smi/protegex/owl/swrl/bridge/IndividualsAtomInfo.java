
// Info object representing a SWRL individuals atom. 

package edu.stanford.smi.protegex.owl.swrl.bridge;

import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;

import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.swrl.model.*;

public abstract class IndividualsAtomInfo extends AtomInfo
{
  private Argument argument1, argument2;
  
  public IndividualsAtomInfo(SWRLIndividualsAtom atom) throws SWRLRuleEngineBridgeException
  {
    if (atom.getArgument1() instanceof SWRLVariable) {
      SWRLVariable variable = (SWRLVariable)atom.getArgument1();
      ObjectVariableInfo argument = new ObjectVariableInfo(variable);
      addReferencedVariable(variable.getName(), argument);
      argument1 = argument;
    } else if (atom.getArgument1() instanceof OWLIndividual) {
      IndividualInfo argument = new IndividualInfo((OWLIndividual)atom.getArgument1());
      addReferencedIndividualName(argument.getIndividualName());
      argument1 = argument;
    } else throw new SWRLRuleEngineBridgeException("Unexpected argument #1 to atom '" + atom.getBrowserText() + 
                                                   "'. Expecting variable or individual, got instance of " + atom.getArgument1().getClass() + ".");

    if (atom.getArgument2() instanceof SWRLVariable) {
      SWRLVariable variable = (SWRLVariable)atom.getArgument2();
      ObjectVariableInfo argument = new ObjectVariableInfo(variable);
      addReferencedVariable(variable.getName(), argument);
      argument2 = argument;
    } else if (atom.getArgument2() instanceof OWLIndividual) {
      IndividualInfo argument = new IndividualInfo((OWLIndividual)atom.getArgument2());
      addReferencedIndividualName(argument.getIndividualName());
      argument2 = argument;
    } else throw new SWRLRuleEngineBridgeException("Unexpected argument #2 to atom '" + atom.getBrowserText() + 
                                                   "'. Expecting variable or individual, got instance of " + atom.getArgument2().getClass() + ".");

  } // IndividualsAtomInfo
  
  public Argument getArgument1() { return argument1; }
  public Argument getArgument2() { return argument2; }
} // IndividualsAtomInfo

