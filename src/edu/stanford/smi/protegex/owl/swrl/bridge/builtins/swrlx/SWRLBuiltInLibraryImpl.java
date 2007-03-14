
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
public class SWRLBuiltInLibraryImpl implements SWRLBuiltInLibrary
{
  private static String SWRLXNamespace = "swrlx";

  public static String SWRLXCreateOWLThing = SWRLXNamespace + ":" + "createIndividual";

  private SWRLRuleEngineBridge bridge;
  private OWLModel owlModel;

  private HashMap<String, IndividualInfo> createInvocationMap;

  public void initialize(SWRLRuleEngineBridge bridge) 
  { 
    this.bridge = bridge; 
    owlModel = bridge.getOWLModel();
    createInvocationMap = new HashMap<String, IndividualInfo>();
  } // initialize

  /**
   ** For every pattern of second and subsequent arguments, create an OWL individual of type OWL:Thing and bind it to the first argument. If
   ** the first argument is already bound when the built-in is called, this method returns true.
   */
  public boolean createOWLThing(List<Argument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsAtLeast(2, arguments.size());

    if (SWRLBuiltInUtil.isUnboundArgument(0, arguments)) {
      String createInvocationPattern  = SWRLBuiltInUtil.createInvocationPattern(arguments.subList(1, arguments.size()), bridge);
      IndividualInfo individualInfo = null;

      if (createInvocationMap.containsKey(createInvocationPattern)) individualInfo = createInvocationMap.get(createInvocationPattern);
      else {
        try {
          individualInfo = bridge.createIndividual();
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
