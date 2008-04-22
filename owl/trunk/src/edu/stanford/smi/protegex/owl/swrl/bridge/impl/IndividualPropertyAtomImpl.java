
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

  public IndividualPropertyAtomImpl(SWRLIndividualPropertyAtom atom) throws OWLFactoryException
  {
    propertyName = (atom.getPropertyPredicate() != null) ? atom.getPropertyPredicate().getName() : null;

    if (propertyName == null) throw new OWLFactoryException("empty property name in SWRLIndividualPropertyAtom '" + atom + "'");
    
    addReferencedPropertyName(propertyName);

    if (atom.getArgument1() instanceof SWRLVariable) {
      SWRLVariable variable = (SWRLVariable)atom.getArgument1();
      AtomArgument argument = OWLFactory.createVariableAtomArgument(variable.getName());
      addReferencedVariableName(variable.getName());
      argument1 = argument;
    } else if (atom.getArgument1() instanceof edu.stanford.smi.protegex.owl.model.OWLIndividual) {
      edu.stanford.smi.protegex.owl.model.OWLIndividual individual = (edu.stanford.smi.protegex.owl.model.OWLIndividual)atom.getArgument1();
      OWLIndividual argument = OWLFactory.createOWLIndividual(individual);
      addReferencedIndividualName(argument.getIndividualName());
      argument1 = argument;
    } else throw new OWLFactoryException("unexpected first argument to individual property atom '" + atom.getBrowserText() + 
                                         "' - expecting variable or individual, got instance of " + atom.getArgument1().getClass());

    if (atom.getArgument2() instanceof SWRLVariable) {
      SWRLVariable variable = (SWRLVariable)atom.getArgument2();
      AtomArgument argument = OWLFactory.createVariableAtomArgument(variable.getName());
      addReferencedVariableName(variable.getName());
      argument2 = argument;
    } else if (atom.getArgument2() instanceof edu.stanford.smi.protegex.owl.model.OWLIndividual) {
      edu.stanford.smi.protegex.owl.model.OWLIndividual individual = (edu.stanford.smi.protegex.owl.model.OWLIndividual)atom.getArgument2();
      OWLIndividual argument = OWLFactory.createOWLIndividual(individual);
      addReferencedIndividualName(argument.getIndividualName());
      argument2 = argument;
    } else if (atom.getArgument2() instanceof edu.stanford.smi.protegex.owl.model.OWLNamedClass) {
      edu.stanford.smi.protegex.owl.model.OWLNamedClass cls = (edu.stanford.smi.protegex.owl.model.OWLNamedClass)atom.getArgument2();
      OWLClass argument = OWLFactory.createOWLClass(cls);
      addReferencedClassName(argument.getClassName());
      argument2 = argument;
    } else if (atom.getArgument2() instanceof edu.stanford.smi.protegex.owl.model.OWLProperty) {
      edu.stanford.smi.protegex.owl.model.OWLProperty property = (edu.stanford.smi.protegex.owl.model.OWLProperty)atom.getArgument2();
      OWLProperty argument;
      if (property.isObjectProperty()) 
        argument = OWLFactory.createOWLObjectProperty((edu.stanford.smi.protegex.owl.model.OWLObjectProperty)property);
      else 
        argument = OWLFactory.createOWLDatatypeProperty((edu.stanford.smi.protegex.owl.model.OWLDatatypeProperty)property);
      addReferencedPropertyName(argument.getPropertyName());
      argument2 = argument;
    } else throw new OWLFactoryException("unexpected second argument to individual property atom '" + atom.getBrowserText() + 
                                         "' - expecting variable or individual, got instance of " + atom.getArgument2().getClass());
  } // IndividualPropertyAtomImpl

  public String getPropertyName() { return propertyName; }  
  public AtomArgument getArgument1() { return argument1; }
  public AtomArgument getArgument2() { return argument2; }  

  public String toString() { return getPropertyName() + "(" + getArgument1() + ", " + getArgument2() + ")"; }
} // IndividualPropertyAtomImpl
