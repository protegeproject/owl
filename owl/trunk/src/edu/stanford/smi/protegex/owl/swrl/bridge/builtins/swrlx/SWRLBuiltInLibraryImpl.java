
package edu.stanford.smi.protegex.owl.swrl.bridge.builtins.swrlx;

import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.swrl.model.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.builtins.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;

import java.util.List;
import java.util.ArrayList;

/**
 ** Implementations library for SWRL Extensions built-in methods. <p>
 **
 ** See <a href="http://protege.cim3.net/cgi-bin/wiki.pl?SWRLBuiltInBridge">here</a> for documentation on defining SWRL built-in libraries.
 */

public class SWRLBuiltInLibraryImpl implements SWRLBuiltInLibrary
{
  private static String SWRLXNamespace = "swrlx";

  private static String SWRLXCreateOWLThing = SWRLXNamespace + ":" + "createIndividual";

  private SWRLRuleEngineBridge bridge;

  public void initialize(SWRLRuleEngineBridge bridge) 
  { 
    this.bridge = bridge; 
  } // initialize

  /*
  ** For every OWL individual passed as the second argument, create an OWL individual of type OWL:Thing and bind it to the first
  ** argument. 
  ** 
  */
  public boolean createOWLThing(List<Argument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(SWRLXCreateOWLThing, 2, arguments.size());
    SWRLBuiltInUtil.checkThatArgumentIsAnIndividual(SWRLXCreateOWLThing, 1, arguments);

    if (SWRLBuiltInUtil.isUnboundArgument(SWRLXCreateOWLThing, 0, arguments)) {
      IndividualInfo individualInfo = null;

      try {
        individualInfo = bridge.createIndividual();
      } catch (SWRLRuleEngineBridgeException e) {
        throw new BuiltInException("Error calling bridge to create OWL individual: " + e.getMessage());
      } // 
      arguments.set(0, individualInfo); // Bind the result to the first parameter      
    } // if

    return true;
  } // createOWLThing

} // SWRLBuiltInLibrary
