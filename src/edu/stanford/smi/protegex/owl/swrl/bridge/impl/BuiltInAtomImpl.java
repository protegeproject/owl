
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
  private String builtInName;
  private List<BuiltInArgument> arguments; 
  private int builtInIndex = -1; // Index of this built-in atom in rule body; left-to-right, first built-in index is 0, second in 1, and so on
  private boolean sqwrlVariablesUsed = false, isASQWRLMakeCollection = false;
  
  public BuiltInAtomImpl(OWLModel owlModel, SWRLBuiltinAtom atom) throws SWRLRuleEngineBridgeException
  {
    builtInName = (atom.getBuiltin() != null) ? atom.getBuiltin().getName() : null;

    if (builtInName == null) throw new SWRLRuleEngineBridgeException("empty built-in name in SWRLBuiltinAtom: " + atom);

    arguments = buildArgumentList(owlModel, atom);
  } // BuiltInAtomImpl

  public BuiltInAtomImpl(String builtInName, List<BuiltInArgument> arguments)
  {
    this.builtInName = builtInName;
    this.arguments = arguments;
  } // BuiltInArgument

  public String getBuiltInName() { return builtInName; }  
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

  private List<BuiltInArgument> buildArgumentList(OWLModel owlModel, SWRLBuiltinAtom builtInAtom) throws SWRLRuleEngineBridgeException
  {
    List<BuiltInArgument> result = new ArrayList<BuiltInArgument>();
    RDFList rdfList = builtInAtom.getArguments();
    
    Iterator iterator = rdfList.getValues().iterator();
    while (iterator.hasNext()) {
      Object o = iterator.next();
      if (o instanceof SWRLVariable) {
        SWRLVariable variable = (SWRLVariable)o;
	BuiltInArgument builtInArgument = BridgeFactory.createVariableBuiltInArgument(variable.getName());
        result.add(builtInArgument);
        addReferencedVariableName(variable.getName());
      } else if (o instanceof edu.stanford.smi.protegex.owl.model.OWLIndividual) {
        edu.stanford.smi.protegex.owl.model.OWLIndividual individual = (edu.stanford.smi.protegex.owl.model.OWLIndividual)o;
        result.add(BridgeFactory.createOWLIndividual(individual));
        addReferencedIndividualName(individual.getName());
      } else  if (o instanceof OWLNamedClass) {
        OWLNamedClass cls = (OWLNamedClass)o;
        result.add(BridgeFactory.createOWLClass(owlModel, cls.getName()));
      } else  if (o instanceof edu.stanford.smi.protegex.owl.model.OWLProperty) {
        edu.stanford.smi.protegex.owl.model.OWLProperty property = (edu.stanford.smi.protegex.owl.model.OWLProperty)o;
        if (property.isObjectProperty()) result.add(BridgeFactory.createOWLObjectProperty(property.getName()));
        else result.add(BridgeFactory.createOWLDatatypeProperty(property.getName()));
      } else  if (o instanceof RDFSLiteral) result.add(BridgeFactory.createOWLDatatypeValue(owlModel, (RDFSLiteral)o));
      else  if (o instanceof Number) result.add(BridgeFactory.createOWLDatatypeValue((Number)o));
      else  if (o instanceof String) result.add(BridgeFactory.createOWLDatatypeValue((String)o));
      else throw new SWRLRuleEngineBridgeException("unknown type for argument '" + o + "'");
    } // while
    
    return result;
  } // buildArgumentList

} // BuiltInAtomImpl
