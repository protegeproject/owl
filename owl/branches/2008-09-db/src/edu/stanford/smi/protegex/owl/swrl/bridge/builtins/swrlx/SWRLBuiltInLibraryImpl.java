
package edu.stanford.smi.protegex.owl.swrl.bridge.builtins.swrlx;

import edu.stanford.smi.protegex.owl.swrl.bridge.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.builtins.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;

import java.util.*;

/**
 ** Implementations library for SWRL Extensions built-in methods. See <a
 ** href="http://protege.cim3.net/cgi-bin/wiki.pl?SWRLExtensionsBuiltIns">here</a> for documentation on this library.
 **
 ** See <a href="http://protege.cim3.net/cgi-bin/wiki.pl?SWRLBuiltInBridge">here</a> for documentation on defining SWRL built-in libraries.
 */
public class SWRLBuiltInLibraryImpl extends AbstractSWRLBuiltInLibrary
{
  private static String SWRLXLibraryName = "SWRLExtensionsBuiltIns";

  private ArgumentFactory argumentFactory;

  private HashMap<String, OWLClass> classInvocationMap;
  private HashMap<String, OWLIndividual> individualInvocationMap;

  public SWRLBuiltInLibraryImpl() 
  { 
    super(SWRLXLibraryName); 

    argumentFactory = ArgumentFactory.getFactory();
  } // SWRLBuiltInLibraryImpl

  public void reset() 
  {
    classInvocationMap = new HashMap<String, OWLClass>();
    individualInvocationMap = new HashMap<String, OWLIndividual>();
  } // reset

  /**
   ** For every pattern of second and subsequent arguments, create an OWL anonymous class and bind it to the first argument. If
   ** the first argument is already bound when the built-in is called, this method returns true.
   */
  public boolean makeOWLClass(List<BuiltInArgument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsAtLeast(2, arguments.size());

    if (SWRLBuiltInUtil.isUnboundArgument(0, arguments)) {
      OWLClass owlClass = null;
      String createInvocationPattern 
        = SWRLBuiltInUtil.createInvocationPattern(getInvokingBridge(), getInvokingRuleName(), getInvokingBuiltInIndex(), getIsInConsequent(),
                                                  arguments.subList(1, arguments.size()));

      if (classInvocationMap.containsKey(createInvocationPattern)) owlClass = classInvocationMap.get(createInvocationPattern);
      else {
        try {
          owlClass = getInvokingBridge().createOWLAnonymousClass();
        } catch (SWRLRuleEngineBridgeException e) {
          throw new BuiltInException("error calling bridge to create OWL class: " + e.getMessage());
        } // 
        classInvocationMap.put(createInvocationPattern, owlClass);
      } // if
      arguments.set(0, owlClass); // Bind the result to the first parameter      
    } // if
    
    return true;
  } // makeOWLClass

  /**
   ** For every pattern of second and subsequent arguments, create an OWL individual of type OWL:Thing and bind it to the first argument. If
   ** the first argument is already bound when the built-in is called, this method returns true.
   */
  public boolean makeOWLIndividual(List<BuiltInArgument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsAtLeast(2, arguments.size());

    if (SWRLBuiltInUtil.isUnboundArgument(0, arguments)) {
      OWLIndividual owlIndividual = null;
      String createInvocationPattern 
        = SWRLBuiltInUtil.createInvocationPattern(getInvokingBridge(), getInvokingRuleName(), getInvokingBuiltInIndex(), getIsInConsequent(),
                                                  arguments.subList(1, arguments.size()));

      if (individualInvocationMap.containsKey(createInvocationPattern)) owlIndividual = individualInvocationMap.get(createInvocationPattern);
      else {
        try {
          owlIndividual = getInvokingBridge().createOWLIndividual();
        } catch (SWRLRuleEngineBridgeException e) {
          throw new BuiltInException("error calling bridge to create OWL individual: " + e.getMessage());
        } // 
        individualInvocationMap.put(createInvocationPattern, owlIndividual);
      } // if
      arguments.set(0, owlIndividual); // Bind the result to the first parameter      
    } // if
    
    return true;
  } // makeOWLIndividual

  // For backwards compatability
  public boolean makeOWLThing(List<BuiltInArgument> arguments) throws BuiltInException
  {
    return makeOWLIndividual(arguments);
  } // createOWLThing

 // For backwards compatability
  public boolean createOWLThing(List<BuiltInArgument> arguments) throws BuiltInException
  {
    return makeOWLIndividual(arguments);
  } // createOWLThing

} // SWRLBuiltInLibraryImpl
