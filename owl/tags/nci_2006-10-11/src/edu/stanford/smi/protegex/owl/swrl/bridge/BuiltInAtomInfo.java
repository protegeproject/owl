
// Info object representing a SWRL built-in atom. 

package edu.stanford.smi.protegex.owl.swrl.bridge;

import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;

import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.swrl.model.*;

import java.util.*;

public class BuiltInAtomInfo extends AtomInfo
{
  private List arguments; // List of Argument objects.
  
  public BuiltInAtomInfo(SWRLBuiltinAtom builtInAtom) throws SWRLRuleEngineBridgeException
  {
    super(builtInAtom.getBuiltin().getName());

    arguments = buildInfoList(builtInAtom.getArguments());
  } // BuiltInAtomInfo
  
  public List getArguments() { return arguments; }
  
  private List buildInfoList(RDFList rdfList) throws SWRLRuleEngineBridgeException
  {
    List result = new ArrayList();
    
    Iterator iterator = rdfList.getValues().iterator();
    while (iterator.hasNext()) {
      Object o = iterator.next();
      if (o instanceof SWRLVariable) result.add(new VariableInfo((SWRLVariable)o));
      else if (o instanceof OWLIndividual) result.add(new IndividualInfo((OWLIndividual)o));
      else result.add(new LiteralInfo(o)); // A literal.
    } // while
    
    return result;
  } // buildInfoList

} // BuiltInAtomInfo

