
package edu.stanford.smi.protegex.owl.swrl.bridge;

import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;

import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.swrl.model.*;

import java.util.*;

/**
 ** Info object representing a SWRL built-in atom. 
 */
public class BuiltInAtomInfo extends AtomInfo
{
  private String builtInName;
  private List<Argument> arguments; 
  private Set<Integer> unboundArgumentNumbers = new HashSet<Integer>(); // List containing positions of any unbound arguments
  private int builtInIndex = -1; // Index of this built-in atom in rule body; left-to-right, first built-in index is 0, second in 1, and so on
  private boolean sqwrlVariablesUsed = false;
  private String sqwrlCollectionName = "";
  
  public BuiltInAtomInfo(OWLModel owlModel, SWRLBuiltinAtom atom) throws SWRLRuleEngineBridgeException
  {
    builtInName = (atom.getBuiltin() != null) ? atom.getBuiltin().getName() : null;

    if (builtInName == null) throw new SWRLRuleEngineBridgeException("empty built-in name in SWRLBuiltinAtom: " + atom);

    arguments = buildArgumentList(owlModel, atom);
  } // BuiltInAtomInfo

  public String getBuiltInName() { return builtInName; }  
  public List<Argument> getArguments() { return arguments; }
  public int getNumberOfArguments() { return arguments.size(); }
  public int getBuiltInIndex() { return builtInIndex; }
  public void setBuiltInIndex(int builtInIndex) { this.builtInIndex = builtInIndex; }

  public boolean hasUnboundArguments() { return !unboundArgumentNumbers.isEmpty(); }
  public Set<Integer> getUnboundArgumentNumbers() { return unboundArgumentNumbers; }
  public void addUnboundArgumentNumber(int argumentNumber) { unboundArgumentNumbers.add(Integer.valueOf(argumentNumber)); }
  public boolean isUnboundArgument(int argumentNumber) { return unboundArgumentNumbers.contains(Integer.valueOf(argumentNumber)); }

  public boolean usesSQWRLVariables() { return sqwrlVariablesUsed; } 
  public boolean hasSQWRLCollectionName() { return !sqwrlCollectionName.equals(""); }
  public void setSQWRLCollectionName(String collectionName) { sqwrlCollectionName = collectionName; }

  public boolean usesAtLeastOneVariableOf(Set<String> variableNames) throws SWRLRuleEngineBridgeException
  { 
    Set<String> s = new HashSet<String>(variableNames);

    s.retainAll(getArgumentsVariableNames());

    return !s.isEmpty();
  } // usesAtLeastOneVariableOf

  public Set<String> getUnboundArgumentVariableNames() throws SWRLRuleEngineBridgeException
  {  
    Set<String> result = new HashSet<String>();

    for (Integer integer : unboundArgumentNumbers) result.add(getArgumentVariableName(integer.intValue()));

    return result;
  } // getUnboundArgumentVariableNames

  public boolean isArgumentAVariable(int argumentNumber)
  {
    return (argumentNumber >= 0) && (argumentNumber < arguments.size()) && arguments.get(argumentNumber) instanceof BuiltInVariableInfo;
  } // isArgumentAVariable

  public String getArgumentVariableName(int argumentNumber) throws SWRLRuleEngineBridgeException
  {
    if (!isArgumentAVariable(argumentNumber))
      throw new SWRLRuleEngineBridgeException("expecting a variable for argument #" + argumentNumber + " to built-in '" + getBuiltInName() + "'");
    
    BuiltInVariableInfo builtInVariableInfo = (BuiltInVariableInfo)arguments.get(argumentNumber);

    return builtInVariableInfo.getVariableName();
  } // getArgumentVariableName

  public Set<String> getArgumentsVariableNames() throws SWRLRuleEngineBridgeException
  {
    Set<String> result = new HashSet<String>();

    for (Argument argument : arguments) {
      if (argument instanceof BuiltInVariableInfo) {
        BuiltInVariableInfo builtInVariableInfo = (BuiltInVariableInfo)argument;
        result.add(builtInVariableInfo.getVariableName());
      } // if
    } // for

    return result;
  } // getArgumentsVariableNames

  public void addArguments(List<Argument> additionalArguments) { arguments.addAll(additionalArguments); }

  public void setUsesSQWRLVariables() { sqwrlVariablesUsed = true; }

  private List<Argument> buildArgumentList(OWLModel owlModel, SWRLBuiltinAtom builtInAtom) throws SWRLRuleEngineBridgeException
  {
    List<Argument> result = new ArrayList<Argument>();
    RDFList rdfList = builtInAtom.getArguments();
    
    Iterator iterator = rdfList.getValues().iterator();
    while (iterator.hasNext()) {
      Object o = iterator.next();
      if (o instanceof SWRLVariable) {
        SWRLVariable variable = (SWRLVariable)o;
	BuiltInVariableInfo builtInVariableInfo = new BuiltInVariableInfo(variable);
        result.add(builtInVariableInfo);
        addReferencedVariable(variable.getName(), builtInVariableInfo);
      } else if (o instanceof OWLIndividual) {
        OWLIndividual individual = (OWLIndividual)o;
        result.add(new IndividualInfo(individual));
        addReferencedIndividualName(individual.getName());
      } else  if (o instanceof OWLNamedClass) {
        OWLNamedClass cls = (OWLNamedClass)o;
        result.add(new ClassInfo(owlModel, cls.getName()));
      } else  if (o instanceof OWLProperty) {
        OWLProperty property = (OWLProperty)o;
        result.add(new PropertyInfo(property));
      } else  if (o instanceof RDFSLiteral) result.add(new LiteralInfo(owlModel, (RDFSLiteral)o));
      else  if (o instanceof Number) result.add(new LiteralInfo((Number)o));
      else  if (o instanceof String) result.add(new LiteralInfo((String)o));
      else throw new SWRLRuleEngineBridgeException("Unknown type for parameter '" + o + "' to built-in atom '" + 
                                                   builtInAtom.getBrowserText() + "'.");
    } // while
    
    return result;
  } // buildArgumentList

} // BuiltInAtomInfo

