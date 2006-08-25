
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
  
  public ClassAtomInfo(SWRLClassAtom atom) throws SWRLRuleEngineBridgeException
  {
    super(atom.getClassPredicate().getName());

    String argumentName;

    argumentName = atom.getArgument1().getName();
    
    if (atom.getArgument1() instanceof SWRLVariable) {
      SWRLVariable variable = (SWRLVariable)atom.getArgument1();
      argument1 = new VariableInfo(variable);
      addReferencedVariableName(variable.getName());
    } else if (atom.getArgument1() instanceof OWLIndividual) {
      argument1 = new IndividualInfo((OWLIndividual)atom.getArgument1());
      addReferencedIndividualName(argument1.getName());
    } else throw new SWRLRuleEngineBridgeException("Unexpected argument to class atom '" + atom.getBrowserText() + "'. Expecting variable or individual, got instance of" + atom.getArgument1().getClass() + ".");
  } // ClassAtomInfo
  
  public Argument getArgument1() { return argument1; }
} // ClassAtomInfo

