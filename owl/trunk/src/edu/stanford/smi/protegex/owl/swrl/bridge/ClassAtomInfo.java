
// Info object representing a SWRL class atom.

package edu.stanford.smi.protegex.owl.swrl.bridge;

import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;

import edu.stanford.smi.protegex.owl.swrl.model.*;
import edu.stanford.smi.protegex.owl.model.*;

public class ClassAtomInfo extends AtomInfo
{
  private Argument argument1;
  
  public ClassAtomInfo(SWRLClassAtom atom) throws SWRLRuleEngineBridgeException
  {
    super(atom.getClassPredicate().getName());

    String argumentName;

    argumentName = atom.getArgument1().getName();
    
    if (atom.getArgument1() instanceof SWRLVariable) {
      argument1 = new VariableInfo((SWRLVariable)atom.getArgument1());
    } else if (atom.getArgument1() instanceof OWLIndividual) {
      argument1 = new IndividualInfo((OWLIndividual)atom.getArgument1());
      addReferencedIndividualName(argument1.getName());
    } else throw new InvalidResourceNameException(argumentName + " passed to class atom.");
  } // ClassAtomInfo
  
  public Argument getArgument1() { return argument1; }
} // ClassAtomInfo

