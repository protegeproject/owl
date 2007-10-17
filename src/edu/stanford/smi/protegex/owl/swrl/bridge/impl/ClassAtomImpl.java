
package edu.stanford.smi.protegex.owl.swrl.bridge.impl;

import edu.stanford.smi.protegex.owl.swrl.bridge.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLClassAtom;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLVariable;

/*
** Class representing a SWRL class atom
*/
public class ClassAtomImpl extends AtomImpl implements ClassAtom
{
  private AtomArgument argument1;
  private String className;
  
  public ClassAtomImpl(SWRLClassAtom atom) throws SWRLRuleEngineBridgeException
  {
    className = (atom.getClassPredicate() != null) ? atom.getClassPredicate().getName() : null;

    if (className == null) throw new SWRLRuleEngineBridgeException("Empty class name in SWRLClassAtom: " + atom);
    
    if (atom.getArgument1() instanceof SWRLVariable) {
      SWRLVariable variable = (SWRLVariable)atom.getArgument1();
      AtomArgument argument = BridgeFactory.createVariableAtomArgument(variable.getName());
      addReferencedVariableName(variable.getName());
      argument1 = argument;
    } else if (atom.getArgument1() instanceof edu.stanford.smi.protegex.owl.model.OWLIndividual) {
      OWLIndividual argument = BridgeFactory.createOWLIndividual((edu.stanford.smi.protegex.owl.model.OWLIndividual)atom.getArgument1());
      addReferencedIndividualName(argument.getIndividualName());
      argument1 = argument;
    } else throw new SWRLRuleEngineBridgeException("Unexpected argument to class atom '" + atom.getBrowserText() + "'. Expecting " +
                                                   "variable or individual, got instance of " + atom.getArgument1().getClass() + ".");
  } // ClassAtomImpl
  
  public String getClassName() { return className; }
  public AtomArgument getArgument1() { return argument1; }
} // ClassAtomImpl

