
// Info object representing a SWRL built-in atom. 

package edu.stanford.smi.protegex.owl.swrl.bridge;

import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;

import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.swrl.model.*;

import java.util.*;

public class BuiltInAtomInfo extends AtomInfo
{
  private List arguments; // List of Argument objects.
  
  public BuiltInAtomInfo(OWLModel owlModel, SWRLBuiltinAtom builtInAtom) throws SWRLRuleEngineBridgeException
  {
    super(builtInAtom.getBuiltin().getName());

    arguments = buildInfoList(owlModel, builtInAtom);
  } // BuiltInAtomInfo
  
  public List getArguments() { return arguments; }
  
  private List buildInfoList(OWLModel owlModel, SWRLBuiltinAtom builtInAtom) throws SWRLRuleEngineBridgeException
  {
    List result = new ArrayList();
    RDFList rdfList = builtInAtom.getArguments();
    
    Iterator iterator = rdfList.getValues().iterator();
    while (iterator.hasNext()) {
      Object o = iterator.next();
      if (o instanceof SWRLVariable) result.add(new VariableInfo((SWRLVariable)o));
      else if (o instanceof OWLIndividual) result.add(new IndividualInfo((OWLIndividual)o));
      else  if (o instanceof RDFSLiteral) result.add(new LiteralInfo(owlModel, (RDFSLiteral)o));
      else  if (o instanceof Number) result.add(new LiteralInfo((Number)o));
      else  if (o instanceof String) result.add(new LiteralInfo((String)o));
      else throw new SWRLRuleEngineBridgeException("Unknown type for parameter '" + o + "' to built-in atom '" + 
                                                   builtInAtom.getBrowserText() + "'.");
    } // while
    
    return result;
  } // buildInfoList

} // BuiltInAtomInfo

