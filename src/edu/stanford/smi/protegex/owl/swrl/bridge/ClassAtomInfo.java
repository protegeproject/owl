
// Info object representing a SWRL class atom.

package edu.stanford.smi.protegex.owl.swrl.bridge;

import edu.stanford.smi.protegex.owl.swrl.model.SWRLClassAtom;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLVariable;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.SWRLRuleEngineBridgeException;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.InvalidResourceNameException;
import edu.stanford.smi.protegex.owl.model.OWLIndividual;

public class ClassAtomInfo extends AtomInfo
{
  private Argument argument1;
  private String className;
  
  public ClassAtomInfo(SWRLClassAtom atom) throws SWRLRuleEngineBridgeException
  {
    String argumentName = atom.getArgument1().getName();
    className = atom.getClassPredicate().getName();
    
    if (atom.getArgument1() instanceof SWRLVariable) {
      SWRLVariable variable = (SWRLVariable)atom.getArgument1();
      ObjectVariableInfo argument = new ObjectVariableInfo(variable);
      addReferencedVariable(variable.getName(), argument);
      argument1 = argument;
    } else if (atom.getArgument1() instanceof OWLIndividual) {
      IndividualInfo argument = new IndividualInfo((OWLIndividual)atom.getArgument1());
      addReferencedIndividualName(argument.getIndividualName());
      argument1 = argument;
    } else throw new SWRLRuleEngineBridgeException("Unexpected argument to class atom '" + atom.getBrowserText() + "'. Expecting variable or individual, got instance of" + atom.getArgument1().getClass() + ".");
  } // ClassAtomInfo
  
  public String getClassName() { return className; }
  public Argument getArgument1() { return argument1; }
} // ClassAtomInfo

