
package edu.stanford.smi.protegex.owl.swrl.bridge.builtins.swrlor;

import edu.stanford.smi.protegex.owl.swrl.ormap.Mapper;

import edu.stanford.smi.protegex.owl.swrl.bridge.BuiltInArgument;
import edu.stanford.smi.protegex.owl.swrl.bridge.MultiArgument;
import edu.stanford.smi.protegex.owl.swrl.bridge.OWLIndividual;
import edu.stanford.smi.protegex.owl.swrl.bridge.OWLClass;
import edu.stanford.smi.protegex.owl.swrl.bridge.OWLProperty;
import edu.stanford.smi.protegex.owl.swrl.bridge.OWLDatatypeValue;
import edu.stanford.smi.protegex.owl.swrl.bridge.BridgeFactory;
import edu.stanford.smi.protegex.owl.swrl.bridge.OWLObjectPropertyAssertionAxiom;
import edu.stanford.smi.protegex.owl.swrl.bridge.OWLDatatypePropertyAssertionAxiom;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.BuiltInException;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.SWRLRuleEngineBridgeException;
import edu.stanford.smi.protegex.owl.swrl.bridge.builtins.SWRLBuiltInLibrary;
import edu.stanford.smi.protegex.owl.swrl.bridge.builtins.SWRLBuiltInUtil;

import java.util.*;

/**
 */
public class SWRLBuiltInLibraryImpl extends SWRLBuiltInLibrary 
{
  private static String SWRLORLibraryName = "SWRLORBuiltIns";

  public SWRLBuiltInLibraryImpl() 
  { 
    super(SWRLORLibraryName); 
    reset();
  } // SWRLBuiltInLibraryImpl

  public void reset() 
  {
  } // reset

  public boolean mapOWLClass(List<BuiltInArgument> arguments) throws BuiltInException 
  { 
    boolean result = false, isUnboundIndividualArgument;
    OWLClass owlClass;
    OWLIndividual owlIndividual = null;
    Set<OWLIndividual> individuals = new HashSet<OWLIndividual>();
    String className;
    Mapper mapper;

    SWRLBuiltInUtil.checkNumberOfArgumentsAtLeast(2, arguments.size());

    className = SWRLBuiltInUtil.getArgumentAsAClassName(0, arguments);

    if (!getInvokingBridge().hasMapper()) return false;

    isUnboundIndividualArgument = SWRLBuiltInUtil.isUnboundArgument(1, arguments);

    if (!isUnboundIndividualArgument) throw new BuiltInException("bound arguments not yet implemented, class = '" + className + "'");

    try {
      owlClass = BridgeFactory.createOWLClass(className);    

      mapper = getInvokingBridge().getMapper();
      if (!mapper.isMapped(owlClass)) return false;

      individuals = mapper.mapOWLClass(owlClass);
      
      //if (!individuals.isEmpty()) getInvokingBridge().createOWLIndividuals(individuals);
      if (isUnboundIndividualArgument) {
        MultiArgument multiArgument = BridgeFactory.createMultiArgument(SWRLBuiltInUtil.getVariableName(1, arguments));
        for (OWLIndividual individual : individuals) multiArgument.addArgument(individual);
        arguments.set(1, multiArgument);
        result = !multiArgument.hasNoArguments();
      } else result = false;
    } catch (SWRLRuleEngineBridgeException e) {
      throw new BuiltInException("error mapping OWL class '" + className + "': " + e.getMessage());
    } // try

    return result;
  } // mapOWLClass

  public boolean mapOWLObjectProperty(List<BuiltInArgument> arguments) throws BuiltInException 
  { 
    boolean result = false, hasSubject, hasObject;
    OWLProperty owlProperty;
    OWLIndividual subjectOWLIndividual = null, objectOWLIndividual = null;
    Set<OWLObjectPropertyAssertionAxiom> axioms = new HashSet<OWLObjectPropertyAssertionAxiom>();
    String propertyName;
    Mapper mapper;

    SWRLBuiltInUtil.checkNumberOfArgumentsAtLeast(1, arguments.size());
    SWRLBuiltInUtil.checkForUnboundArguments(arguments);

    propertyName = SWRLBuiltInUtil.getArgumentAsAPropertyName(0, arguments);

    if (!getInvokingBridge().hasMapper()) return false;

    hasSubject = (arguments.size() > 1);
    hasObject = (arguments.size() > 2);

    try {
      owlProperty = BridgeFactory.createOWLObjectProperty(propertyName);
      
      if (hasSubject) subjectOWLIndividual = BridgeFactory.createOWLIndividual(SWRLBuiltInUtil.getArgumentAsAnIndividualName(1, arguments));
      if (hasObject) objectOWLIndividual = BridgeFactory.createOWLIndividual(SWRLBuiltInUtil.getArgumentAsAnIndividualName(2, arguments));
      
      mapper = getInvokingBridge().getMapper();
      
      if (!mapper.isMapped(owlProperty)) return false;
      
      if (!hasSubject && !hasObject) axioms = mapper.mapOWLObjectProperty(owlProperty);
      else if (hasSubject && !hasObject) axioms = mapper.mapOWLObjectProperty(owlProperty, subjectOWLIndividual);
      else axioms = mapper.mapOWLObjectProperty(owlProperty, subjectOWLIndividual, objectOWLIndividual);
      
      if (!axioms.isEmpty()) getInvokingBridge().createOWLObjectPropertyAssertionAxioms(axioms);

    } catch (SWRLRuleEngineBridgeException e) {
      throw new BuiltInException("error mapping OWL object property '" + propertyName + "': " + e.getMessage());
    } // try

    return !axioms.isEmpty();
  } // mapOWLObjectProperty

  public boolean mapOWLDatatypeProperty(List<BuiltInArgument> arguments) throws BuiltInException 
  { 
    boolean result = false, hasSubject, hasValue;
    OWLProperty owlProperty;
    OWLIndividual subjectOWLIndividual = null;
    OWLDatatypeValue value = null;
    Set<OWLDatatypePropertyAssertionAxiom> axioms = new HashSet<OWLDatatypePropertyAssertionAxiom>();
    String propertyName;
    Mapper mapper;

    SWRLBuiltInUtil.checkNumberOfArgumentsAtLeast(1, arguments.size());
    SWRLBuiltInUtil.checkForUnboundArguments(arguments);

    propertyName = SWRLBuiltInUtil.getArgumentAsAPropertyName(0, arguments);

    if (!getInvokingBridge().hasMapper()) return false;

    hasSubject = (arguments.size() > 1) && SWRLBuiltInUtil.isArgumentAnIndividual(1, arguments);
    hasValue = (arguments.size() > 2 || (arguments.size() > 1 && SWRLBuiltInUtil.isArgumentADatatypeValue(1, arguments)));
    try {
      owlProperty = BridgeFactory.createOWLDatatypeProperty(propertyName);
      
      if (hasSubject) subjectOWLIndividual = BridgeFactory.createOWLIndividual(SWRLBuiltInUtil.getArgumentAsAnIndividualName(1, arguments));
      if (hasValue) {
        if (hasSubject) value = SWRLBuiltInUtil.getArgumentAsAnOWLDatatypeValue(2, arguments);
        else value = SWRLBuiltInUtil.getArgumentAsAnOWLDatatypeValue(1, arguments);
      } // if
      
      mapper = getInvokingBridge().getMapper();
      
      if (!mapper.isMapped(owlProperty)) return false;
      
      if (!hasSubject && !hasValue) axioms = mapper.mapOWLDatatypeProperty(owlProperty);
      else if (hasSubject && !hasValue) axioms = mapper.mapOWLDatatypeProperty(owlProperty, subjectOWLIndividual);
      else if (!hasSubject && hasValue) axioms = mapper.mapOWLDatatypeProperty(owlProperty, value);
      else axioms = mapper.mapOWLDatatypeProperty(owlProperty, subjectOWLIndividual, value);
      
      if (!axioms.isEmpty()) getInvokingBridge().createOWLDatatypePropertyAssertionAxioms(axioms);
    } catch (SWRLRuleEngineBridgeException e) {
      throw new BuiltInException("error mapping OWL datatype property '" + propertyName + "': " + e.getMessage());
    } // try

    return !axioms.isEmpty();
  } // mapOWLDatatypeProperty

} // SWRLBuiltInLibraryImpl
