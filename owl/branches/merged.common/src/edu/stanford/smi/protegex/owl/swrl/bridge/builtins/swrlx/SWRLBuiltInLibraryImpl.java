
package edu.stanford.smi.protegex.owl.swrl.bridge.builtins.swrlx;

import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.swrl.model.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.builtins.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;

import edu.stanford.smi.protegex.owl.swrl.util.SWRLOWLUtil;
import edu.stanford.smi.protegex.owl.swrl.exceptions.SWRLOWLUtilException;

import java.util.*;

/**
 ** Implementations library for SWRL Extensions built-in methods. See <a
 ** href="http://protege.cim3.net/cgi-bin/wiki.pl?SWRLExtensionsBuiltIns">here</a> for documentation on this library.
 **
 ** See <a href="http://protege.cim3.net/cgi-bin/wiki.pl?SWRLBuiltInBridge">here</a> for documentation on defining SWRL built-in libraries.
 */
public class SWRLBuiltInLibraryImpl extends SWRLBuiltInLibrary
{
  private static String SWRLXLibraryName = "SWRLExtensionsBuiltIns";

  private HashMap<String, IndividualInfo> createInvocationMap;

  public SWRLBuiltInLibraryImpl() { super(SWRLXLibraryName); }

  public void reset() 
  {
    createInvocationMap = new HashMap<String, IndividualInfo>();
  } // reset

  /**
   ** For every pattern of second and subsequent arguments, create an OWL individual of type OWL:Thing and bind it to the first argument. If
   ** the first argument is already bound when the built-in is called, this method returns true.
   */
  public boolean createOWLThing(List<Argument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsAtLeast(2, arguments.size());

    if (SWRLBuiltInUtil.isUnboundArgument(0, arguments)) {
      String createInvocationPattern 
        = SWRLBuiltInUtil.createInvocationPattern(getInvokingBridge(), getInvokingRuleName(), getInvokingBuiltInIndex(), 
                                                  arguments.subList(1, arguments.size()));
      IndividualInfo individualInfo = null;
      if (createInvocationMap.containsKey(createInvocationPattern)) individualInfo = createInvocationMap.get(createInvocationPattern);
      else {
        try {
          individualInfo = getInvokingBridge().createIndividual();
        } catch (SWRLRuleEngineBridgeException e) {
          throw new BuiltInException("error calling bridge to create OWL individual: " + e.getMessage());
        } // 
        createInvocationMap.put(createInvocationPattern, individualInfo);
      } // if
      arguments.set(0, individualInfo); // Bind the result to the first parameter      
    } // if
    
    return true;
  } // createOWLThing

} // SWRLBuiltInLibraryImpl
