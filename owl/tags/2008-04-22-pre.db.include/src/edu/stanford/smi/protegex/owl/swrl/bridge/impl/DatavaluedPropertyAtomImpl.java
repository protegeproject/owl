
package edu.stanford.smi.protegex.owl.swrl.bridge.impl;

import edu.stanford.smi.protegex.owl.swrl.bridge.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;

import edu.stanford.smi.protegex.owl.swrl.model.SWRLDatavaluedPropertyAtom;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLVariable;
import edu.stanford.smi.protegex.owl.model.RDFSLiteral;
import edu.stanford.smi.protegex.owl.model.OWLModel;

/*
** Class representing a SWRL data valued property atom
*/
public class DatavaluedPropertyAtomImpl extends AtomImpl implements DatavaluedPropertyAtom
{
  private String propertyName;
  private AtomArgument argument1, argument2;
  
  public DatavaluedPropertyAtomImpl(OWLModel owlModel, SWRLDatavaluedPropertyAtom atom) throws OWLFactoryException, DatatypeConversionException
  {
    propertyName = (atom.getPropertyPredicate() != null) ? atom.getPropertyPredicate().getName() : null;

    if (propertyName == null) throw new OWLFactoryException("empty property name in SWRLDatavaluedPropertyAtom '" + atom.getBrowserText() + "'");

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
    } else throw new OWLFactoryException("unexpected argument first to datavalued property atom '" + atom.getBrowserText() + 
                                         "' - expecting variable or individual, got instance of " + atom.getArgument1().getClass());

    if (atom.getArgument2() instanceof SWRLVariable) {
      SWRLVariable variable = (SWRLVariable)atom.getArgument2();
      AtomArgument argument = OWLFactory.createVariableAtomArgument(variable.getName());
      addReferencedVariableName(variable.getName());
      argument2 = argument;
    } else if (atom.getArgument2() instanceof RDFSLiteral) argument2 = OWLFactory.createOWLDatatypeValue(owlModel, (RDFSLiteral)atom.getArgument2());
    else throw new OWLFactoryException("unexpected second to datavalued property atom '" + atom.getBrowserText()  + 
                                       "' - expecting variable or literal, got instance of " + atom.getArgument2().getClass());
  } // DatavaluedPropertyAtomImpl

  public String getPropertyName() { return propertyName; }  
  public AtomArgument getArgument1() { return argument1; }
  public AtomArgument getArgument2() { return argument2; }

  public String toString() 
  { 
    String result = "" + getPropertyName() + "(" + getArgument1() + ", ";

    if (getArgument2() instanceof OWLDatatypeValue && ((OWLDatatypeValue)getArgument2()).isString())
      result += "\"" + getArgument2() + "\"";
    else result += "" + getArgument2();

    result += ")"; 

    return result;
  } // toString
} // DatavaluedPropertyAtomImpl
