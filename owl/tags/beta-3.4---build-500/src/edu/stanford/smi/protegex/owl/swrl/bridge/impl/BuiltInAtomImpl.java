
package edu.stanford.smi.protegex.owl.swrl.bridge.impl;

import edu.stanford.smi.protegex.owl.swrl.bridge.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;

import edu.stanford.smi.protegex.owl.swrl.model.SWRLBuiltinAtom;

import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.RDFList;
import edu.stanford.smi.protegex.owl.model.RDFSLiteral;

import edu.stanford.smi.protegex.owl.swrl.model.SWRLVariable;

import java.util.*;

/**
 ** Class representing a SWRL built-in atom
 */
public class BuiltInAtomImpl extends AtomImpl implements BuiltInAtom
{
  private String builtInName, builtInPrefixedName;
  private List<BuiltInArgument> arguments; 
  private int builtInIndex = -1; // Index of this built-in atom in rule body; left-to-right, first built-in index is 0, second in 1, and so on
  private boolean sqwrlVariablesUsed = false, isASQWRLMakeCollection = false;
  
  public BuiltInAtomImpl(OWLModel owlModel, SWRLBuiltinAtom atom) throws OWLFactoryException, DatatypeConversionException
  {
    builtInName = (atom.getBuiltin() != null) ? atom.getBuiltin().getName() : null;
    builtInPrefixedName = (atom.getBuiltin() != null) ? atom.getBuiltin().getPrefixedName() : null;

    if (builtInName == null) throw new OWLFactoryException("empty built-in name in SWRLBuiltinAtom: " + atom);

    arguments = buildArgumentList(owlModel, atom);
  } // BuiltInAtomImpl

  public BuiltInAtomImpl(String builtInName, String builtInPrefixedName, List<BuiltInArgument> arguments)
  {
    this.builtInName = builtInName;
    this.builtInPrefixedName = builtInPrefixedName;
    this.arguments = arguments;
  } // BuiltInArgument

  public String getBuiltInName() { return builtInName; }  
  public String getBuiltInPrefixedName() { return builtInPrefixedName; }  

  public List<BuiltInArgument> getArguments() { return arguments; }
  public int getNumberOfArguments() { return arguments.size(); }
  public int getBuiltInIndex() { return builtInIndex; }
  public void setBuiltInIndex(int builtInIndex) { this.builtInIndex = builtInIndex; }

  public boolean usesSQWRLVariables() { return sqwrlVariablesUsed; } 
  public void setUsesSQWRLVariables() { sqwrlVariablesUsed = true; }
  public boolean isSQWRLMakeCollection() { return isASQWRLMakeCollection; }
  public void setIsSQWRLMakeCollection() { isASQWRLMakeCollection = true; }

  public boolean usesAtLeastOneVariableOf(Set<String> variableNames) throws BuiltInException
  { 
    Set<String> s = new HashSet<String>(variableNames);

    s.retainAll(getArgumentsVariableNames());

    return !s.isEmpty();
  } // usesAtLeastOneVariableOf

  public boolean isArgumentAVariable(int argumentNumber) throws BuiltInException
  {
    checkArgumentNumber(argumentNumber);

    return arguments.get(argumentNumber).isVariable();
  } // isArgumentAVariable

  public boolean isArgumentUnbound(int argumentNumber) throws BuiltInException
  {
    checkArgumentNumber(argumentNumber);

    return arguments.get(argumentNumber).isUnbound();
  } // isArgumentUnbound

  public boolean hasUnboundArguments() 
  {
    for (BuiltInArgument argument: arguments) if (argument.isUnbound()) return true;
    return false;
  } // hasUnboundArguments

  public Set<String> getUnboundArgumentVariableNames() throws BuiltInException
  {  
    Set<String> result = new HashSet<String>();

    for (BuiltInArgument argument : arguments) if (argument.isUnbound()) result.add(argument.getVariableName());

    return result;
  } // getUnboundArgumentVariableNames

  public String getArgumentVariableName(int argumentNumber) throws BuiltInException
  {
    checkArgumentNumber(argumentNumber);

    if (!arguments.get(argumentNumber).isVariable())
      throw new BuiltInException("expecting a variable for (0-offset) argument #" + argumentNumber);
    
    return arguments.get(argumentNumber).getVariableName();
  } // getArgumentVariableName

  public Set<String> getArgumentsVariableNames() throws BuiltInException
  {
    Set<String> result = new HashSet<String>();

    for (BuiltInArgument argument : arguments) if (argument.isVariable()) result.add(argument.getVariableName());

    return result;
  } // getArgumentsVariableNames

  public void addArguments(List<BuiltInArgument> additionalArguments) 
  { 
    if (arguments == null) System.err.println("dddddadjskjd");
    if (additionalArguments == null) System.err.println("adjskjd");

    arguments.addAll(additionalArguments); 
  }

  private void checkArgumentNumber(int argumentNumber) throws BuiltInException
  {
    if (argumentNumber < 0 || argumentNumber > arguments.size()) throw new BuiltInException("invalid (0-offset) argument #" + argumentNumber);
  } // checkArgumentNumber  

  private List<BuiltInArgument> buildArgumentList(OWLModel owlModel, SWRLBuiltinAtom builtInAtom) throws OWLFactoryException, DatatypeConversionException
  {
    List<BuiltInArgument> result = new ArrayList<BuiltInArgument>();
    RDFList rdfList = builtInAtom.getArguments();
    
    Iterator iterator = rdfList.getValues().iterator();
    while (iterator.hasNext()) {
      Object o = iterator.next();
      if (o instanceof SWRLVariable) {
        SWRLVariable variable = (SWRLVariable)o;
	BuiltInArgument builtInArgument = OWLFactory.createVariableBuiltInArgument(variable.getName(), variable.getPrefixedName());
        result.add(builtInArgument);
        addReferencedVariableName(variable.getName());
      } else if (o instanceof edu.stanford.smi.protegex.owl.model.OWLIndividual) {
        edu.stanford.smi.protegex.owl.model.OWLIndividual individual = (edu.stanford.smi.protegex.owl.model.OWLIndividual)o;
        result.add(OWLFactory.createOWLIndividual(individual));
        addReferencedIndividualName(individual.getName());
      } else  if (o instanceof OWLNamedClass) {
        OWLNamedClass cls = (OWLNamedClass)o;
        result.add(OWLFactory.createOWLClass(owlModel, cls.getName()));
      } else  if (o instanceof edu.stanford.smi.protegex.owl.model.OWLProperty) {
        edu.stanford.smi.protegex.owl.model.OWLProperty property = (edu.stanford.smi.protegex.owl.model.OWLProperty)o;
        if (property.isObjectProperty()) result.add(OWLFactory.createOWLObjectProperty((edu.stanford.smi.protegex.owl.model.OWLObjectProperty)property));
        else result.add(OWLFactory.createOWLDatatypeProperty((edu.stanford.smi.protegex.owl.model.OWLDatatypeProperty)property));
      } else  if (o instanceof RDFSLiteral) result.add(OWLFactory.createOWLDatatypeValue(owlModel, (RDFSLiteral)o));
      else  if (o instanceof Number) result.add(OWLFactory.createOWLDatatypeValue((Number)o));
      else  if (o instanceof String) result.add(OWLFactory.createOWLDatatypeValue((String)o));
      else throw new OWLFactoryException("unknown type for argument '" + o + "'");
    } // while
    
    return result;
  } // buildArgumentList

  public String toString() 
  {
    String result = builtInPrefixedName + "(";
    boolean isFirst = true;

    for (BuiltInArgument argument : getArguments()) {
      if (!isFirst) result += ", ";
      if (argument instanceof OWLDatatypeValue && ((OWLDatatypeValue)argument).isString())
        result += "\"" + argument + "\"";
      else result += "" + argument;
      isFirst = false;
    } // for

    result += ")";

    return result;
  } // toString

} // BuiltInAtomImpl
