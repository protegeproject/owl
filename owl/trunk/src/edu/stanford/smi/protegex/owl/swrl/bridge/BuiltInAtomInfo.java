
package edu.stanford.smi.protegex.owl.swrl.bridge;

import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;

import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.swrl.model.*;

import java.util.*;

/*
** Info object representing a SWRL built-in atom. 
*/
public class BuiltInAtomInfo extends AtomInfo
{
  private String builtInName;
  private List<Argument> arguments; 
  private Collection<Integer> unboundArgumentNumbers = new ArrayList<Integer>(); // List containing positions of any unbound arguments.
  
  public BuiltInAtomInfo(OWLModel owlModel, SWRLBuiltinAtom builtInAtom) throws SWRLRuleEngineBridgeException
  {
    builtInName = builtInAtom.getBuiltin().getName();

    arguments = buildArgumentList(owlModel, builtInAtom);
  } // BuiltInAtomInfo

  public String getBuiltInName() { return builtInName; }  
  public List<Argument> getArguments() { return arguments; }
  public int getNumberOfArguments() { return arguments.size(); }

  public boolean hasUnboundArguments() { return !unboundArgumentNumbers.isEmpty(); }
  public Collection<Integer> getUnboundArgumentNumbers() { return unboundArgumentNumbers; }
  public void addUnboundArgumentNumber(int argumentNumber) { unboundArgumentNumbers.add(new Integer(argumentNumber)); }
  public boolean isUnboundArgument(int argumentNumber) { return unboundArgumentNumbers.contains(new Integer(argumentNumber)); }

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
      throw new SWRLRuleEngineBridgeException("Expecting a variable for argument #" + argumentNumber + " to built-in '" + getBuiltInName() + "'");
    
    BuiltInVariableInfo builtInVariableInfo = (BuiltInVariableInfo)arguments.get(argumentNumber);

    return builtInVariableInfo.getVariableName();
  } // getArgumentVariableName

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
      } else  if (o instanceof RDFSLiteral) result.add(new LiteralInfo(owlModel, (RDFSLiteral)o));
      else  if (o instanceof Number) result.add(new LiteralInfo((Number)o));
      else  if (o instanceof String) result.add(new LiteralInfo((String)o));
      else throw new SWRLRuleEngineBridgeException("Unknown type for parameter '" + o + "' to built-in atom '" + 
                                                   builtInAtom.getBrowserText() + "'.");
    } // while
    
    return result;
  } // buildArgumentList

} // BuiltInAtomInfo

