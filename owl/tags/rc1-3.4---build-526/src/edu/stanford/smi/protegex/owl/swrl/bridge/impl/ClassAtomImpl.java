
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
  private String className, prefixedClassName;
  
  public ClassAtomImpl(SWRLClassAtom atom) throws OWLFactoryException
  {
    className = (atom.getClassPredicate() != null) ? atom.getClassPredicate().getName() : null;
    prefixedClassName = (atom.getClassPredicate() != null) ? atom.getClassPredicate().getPrefixedName() : null;

    if (className == null) throw new OWLFactoryException("empty class name in SWRLClassAtom: " + atom);

    addReferencedClassName(className);
    
    if (atom.getArgument1() instanceof SWRLVariable) {
      SWRLVariable variable = (SWRLVariable)atom.getArgument1();
      AtomArgument argument = OWLFactory.createVariableAtomArgument(variable.getName(), variable.getPrefixedName());
      addReferencedVariableName(variable.getName());
      argument1 = argument;
    } else if (atom.getArgument1() instanceof edu.stanford.smi.protegex.owl.model.OWLIndividual) {
      OWLIndividual argument = OWLFactory.createOWLIndividual((edu.stanford.smi.protegex.owl.model.OWLIndividual)atom.getArgument1());
      addReferencedIndividualName(argument.getIndividualName());
      argument1 = argument;
    } else if (atom.getArgument1() instanceof edu.stanford.smi.protegex.owl.model.OWLNamedClass) {
      OWLClass argument = OWLFactory.createOWLClass((edu.stanford.smi.protegex.owl.model.OWLNamedClass)atom.getArgument1());
      addReferencedClassName(argument.getClassName());
      argument1 = argument;
    } else if (atom.getArgument1() instanceof edu.stanford.smi.protegex.owl.model.OWLObjectProperty) {
      OWLObjectProperty argument = OWLFactory.createOWLObjectProperty((edu.stanford.smi.protegex.owl.model.OWLObjectProperty)atom.getArgument1());
      addReferencedPropertyName(argument.getPropertyName());
      argument1 = argument;
    } else if (atom.getArgument1() instanceof edu.stanford.smi.protegex.owl.model.OWLDatatypeProperty) {
      OWLDatatypeProperty argument = OWLFactory.createOWLDatatypeProperty((edu.stanford.smi.protegex.owl.model.OWLDatatypeProperty)atom.getArgument1());
      addReferencedPropertyName(argument.getPropertyName());
      argument1 = argument;
    } else throw new OWLFactoryException("unexpected argument to class atom '" + atom.getBrowserText() + "'; expecting " +
                                         "variable or individual, got instance of '" + atom.getArgument1().getClass() + "'");
  } // ClassAtomImpl
  
  public String getClassName() { return className; }
  public String getPrefixedClassName() { return prefixedClassName; }
  public AtomArgument getArgument1() { return argument1; }

  public String toString() { return getPrefixedClassName() + "(" + getArgument1() + ")"; }
} // ClassAtomImpl

