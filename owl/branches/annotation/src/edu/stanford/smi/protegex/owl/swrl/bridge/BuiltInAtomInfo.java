
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
  private List arguments; // List of Argument objects.
  private List unboundArgumentNumbers = new ArrayList(); // List of Integer objects containing positions of any unbound arguments.
  
  public BuiltInAtomInfo(OWLModel owlModel, SWRLBuiltinAtom builtInAtom) throws SWRLRuleEngineBridgeException
  {
    super(builtInAtom.getBuiltin().getName());

    arguments = buildInfoList(owlModel, builtInAtom);
  } // BuiltInAtomInfo
  
  public List getArguments() { return arguments; }
  public int getNumberOfArguments() { return arguments.size(); }

  public boolean hasUnboundArguments() { return !unboundArgumentNumbers.isEmpty(); }
  public List getUnboundArgumentNumbers() { return unboundArgumentNumbers; }
  public void addUnboundArgumentNumber(int argumentNumber) { unboundArgumentNumbers.add(new Integer(argumentNumber)); }

  public List getUnboundArgumentVariableNames() throws SWRLRuleEngineBridgeException
  {  
    Iterator iterator = unboundArgumentNumbers.iterator();
    List result = new ArrayList();

    while (iterator.hasNext()) {
      int argumentNumber = ((Integer)iterator.next()).intValue();
      result.add(getArgumentVariableName(argumentNumber));
    } // while
    return result;
  } // getUnboundArgumentVariableNames

  public boolean isArgumentAVariable(int argumentNumber)
  {
    return (argumentNumber >= 0) && (argumentNumber < arguments.size()) && arguments.get(argumentNumber) instanceof VariableInfo;
  } // isArgumentAVariable

  public String getArgumentVariableName(int argumentNumber) throws SWRLRuleEngineBridgeException
  {
    if (!isArgumentAVariable(argumentNumber))
      throw new SWRLRuleEngineBridgeException("Expecting a variable for argument #" + argumentNumber + " to built-in '" + getName() + "'");
    
    return ((VariableInfo)arguments.get(argumentNumber)).getName();
  } // getArgumentVariableName

  private List buildInfoList(OWLModel owlModel, SWRLBuiltinAtom builtInAtom) throws SWRLRuleEngineBridgeException
  {
    List result = new ArrayList();
    RDFList rdfList = builtInAtom.getArguments();
    
    Iterator iterator = rdfList.getValues().iterator();
    while (iterator.hasNext()) {
      Object o = iterator.next();
      if (o instanceof SWRLVariable) {
        SWRLVariable variable = (SWRLVariable)o;
        result.add(new VariableInfo(variable));
        addReferencedVariableName(variable.getName());
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
  } // buildInfoList

} // BuiltInAtomInfo

