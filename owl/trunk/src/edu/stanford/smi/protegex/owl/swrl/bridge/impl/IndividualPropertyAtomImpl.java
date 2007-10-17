
package edu.stanford.smi.protegex.owl.swrl.bridge.impl;

import edu.stanford.smi.protegex.owl.swrl.bridge.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;

import edu.stanford.smi.protegex.owl.swrl.model.SWRLIndividualPropertyAtom;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLVariable;

/*
** Class representing a SWRL individual property atom
*/
public class IndividualPropertyAtomImpl extends AtomImpl implements IndividualPropertyAtom
{
  private String propertyName;
  private AtomArgument argument1, argument2;

  public IndividualPropertyAtomImpl(SWRLIndividualPropertyAtom atom) throws SWRLRuleEngineBridgeException
  {
    propertyName = (atom.getPropertyPredicate() != null) ? atom.getPropertyPredicate().getName() : null;

    if (propertyName == null) throw new SWRLRuleEngineBridgeException("empty property name in SWRLIndividualPropertyAtom '" + atom + "'");
    
    if (atom.getArgument1() instanceof SWRLVariable) {
      SWRLVariable variable = (SWRLVariable)atom.getArgument1();
      AtomArgument argument = BridgeFactory.createVariableAtomArgument(variable.getName());
      addReferencedVariableName(variable.getName());
      argument1 = argument;
    } else if (atom.getArgument1() instanceof OWLIndividual) {
      edu.stanford.smi.protegex.owl.model.OWLIndividual individual = (edu.stanford.smi.protegex.owl.model.OWLIndividual)atom.getArgument1();
      argument1 = BridgeFactory.createOWLIndividual(individual.getName());
    } else throw new SWRLRuleEngineBridgeException("unexpected first argument to individual property atom '" + atom.getBrowserText() + 
                                                 "' - expecting variable or individual, got instance of " + atom.getArgument1().getClass());

    if (atom.getArgument2() instanceof SWRLVariable) {
      SWRLVariable variable = (SWRLVariable)atom.getArgument2();
      AtomArgument argument = BridgeFactory.createVariableAtomArgument(variable.getName());
      addReferencedVariableName(variable.getName());
      argument2 = argument;
    } else if (atom.getArgument2() instanceof OWLIndividual) {
      edu.stanford.smi.protegex.owl.model.OWLIndividual individual = (edu.stanford.smi.protegex.owl.model.OWLIndividual)atom.getArgument2();
      argument2 = BridgeFactory.createOWLIndividual(individual.getName());
    } else throw new SWRLRuleEngineBridgeException("unexpected second argument to individual property atom '" + atom.getBrowserText() + 
                                                   "' - expecting variable or individual, got instance of " + atom.getArgument2().getClass());

    // If argument1 or 2 is an individual, add its name to the referenced individuals list for this atom.
    if (argument1 instanceof OWLIndividual) addReferencedIndividualName(((OWLIndividual)argument1).getIndividualName());
    if (argument2 instanceof OWLIndividual) addReferencedIndividualName(((OWLIndividual)argument2).getIndividualName());
  } // IndividualPropertyAtomImpl

  public String getPropertyName() { return propertyName; }  
  public AtomArgument getArgument1() { return argument1; }
  public AtomArgument getArgument2() { return argument2; }  
} // IndividualPropertyAtomImpl
