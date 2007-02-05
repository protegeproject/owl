
// Info object representing a SWRL individual property atom. 

package edu.stanford.smi.protegex.owl.swrl.bridge;

import edu.stanford.smi.protegex.owl.swrl.model.SWRLIndividualPropertyAtom;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.SWRLRuleEngineBridgeException;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLVariable;
import edu.stanford.smi.protegex.owl.model.OWLIndividual;

public class IndividualPropertyAtomInfo extends AtomInfo
{
    private String propertyName;
    private Argument argument1, argument2;

  public IndividualPropertyAtomInfo(SWRLIndividualPropertyAtom atom) 
    throws SWRLRuleEngineBridgeException
  {
    propertyName = atom.getPropertyPredicate().getName();
    
    if (atom.getArgument1() instanceof SWRLVariable) {
      SWRLVariable variable = (SWRLVariable)atom.getArgument1();
      ObjectVariableInfo argument = new ObjectVariableInfo(variable);
      addReferencedVariable(variable.getName(), argument);
      argument1 = argument;
    } else if (atom.getArgument1() instanceof OWLIndividual) argument1 = new IndividualInfo((OWLIndividual)atom.getArgument1());
    else throw new SWRLRuleEngineBridgeException("Unexpected argument #1 to individual property atom '" + atom.getBrowserText() + "'. Expecting variable or individual, got instance of " + atom.getArgument1().getClass() + ".");

    if (atom.getArgument2() instanceof SWRLVariable) {
      SWRLVariable variable = (SWRLVariable)atom.getArgument2();
      ObjectVariableInfo argument = new ObjectVariableInfo(variable);
      addReferencedVariable(variable.getName(), argument);
      argument2 = argument;
    } else if (atom.getArgument2() instanceof OWLIndividual) argument2 = new IndividualInfo((OWLIndividual)atom.getArgument2());
    else throw new SWRLRuleEngineBridgeException("Unexpected argument #2 to individual property atom '" + atom.getBrowserText() + "'. Expecting variable or individual, got instance of " + atom.getArgument2().getClass() + ".");

    // If argument1 or 2 is an individual, add its name to the referenced individuals list for this atom.

    if (argument1 instanceof IndividualInfo) {
	IndividualInfo individualInfo = (IndividualInfo)argument1;
	addReferencedIndividualName(individualInfo.getIndividualName());
    } // if

    if (argument2 instanceof IndividualInfo) {
	IndividualInfo individualInfo = (IndividualInfo)argument2;
	addReferencedIndividualName(individualInfo.getIndividualName());
    } // if
  } // IndividualPropertyAtomInfo

  public String getPropertyName() { return propertyName; }  
  public Argument getArgument1() { return argument1; }
  public Argument getArgument2() { return argument2; }  
} // IndividualPropertyAtomInfo
