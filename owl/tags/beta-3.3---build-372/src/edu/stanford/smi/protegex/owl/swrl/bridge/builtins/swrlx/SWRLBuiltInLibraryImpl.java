
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
  public static String SWRLXHasPropertyValue = SWRLXNamespace + ":" + "hasPropertyValue";
  public static String SWRLXHasNumberOfPropertyValues = SWRLXNamespace + ":" + "hasNumberOfPropertyValues";
  public static String SWRLXHasIndividuals = SWRLXNamespace + ":" + "hasIndividuals";
  public static String SWRLXHasNumberOfIndividuals = SWRLXNamespace + ":" + "hasNumberOfIndividuals";

  private SWRLRuleEngineBridge bridge;

  private HashMap<String, IndividualInfo> createInvocationMap;

  public void initialize(SWRLRuleEngineBridge bridge) 
  { 
    this.bridge = bridge; 
    createInvocationMap = new HashMap<String, IndividualInfo>();
  } // initialize

  /*
  ** For every OWL individual passed as the second argument, create an OWL individual of type OWL:Thing and bind it to the first
  ** argument. If the first argument is already bound when the built-in is called, this method returns true.
  */
  public boolean createOWLThing(List<Argument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(SWRLXCreateOWLThing, 2, arguments.size());
    SWRLBuiltInUtil.checkThatArgumentIsAnIndividual(SWRLXCreateOWLThing, 1, arguments);

    if (SWRLBuiltInUtil.isUnboundArgument(SWRLXCreateOWLThing, 0, arguments)) {
      IndividualInfo individualInfo = null;
      String createInvocationPattern = bridge.getCurrentBuiltInInvokingRuleName() + "." + bridge.getCurrentBuiltInInvokingIndex() + "." +
                                       SWRLBuiltInUtil.getArgumentAsAnIndividualName(SWRLXCreateOWLThing, 1, arguments);

      if (createInvocationMap.containsKey(createInvocationPattern)) individualInfo = createInvocationMap.get(createInvocationPattern);
      else {
        try {
          individualInfo = bridge.createIndividual();
        } catch (SWRLRuleEngineBridgeException e) {
          throw new BuiltInException("Error calling bridge to create OWL individual: " + e.getMessage());
        } // 
        createInvocationMap.put(createInvocationPattern, individualInfo);
      } // if
      arguments.set(0, individualInfo); // Bind the result to the first parameter      
    } // if
    
    return true;
  } // createOWLThing

  /*
  ** Returns true if the individual named by the first parameter has at least one value for the property named by the second parameter.
  */
  public boolean hasPropertyValue(List<Argument> arguments) throws BuiltInException
  {
    OWLModel owlModel = bridge.getOWLModel();
    String individualName, propertyName;
    boolean result = false;

    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(SWRLXHasPropertyValue, 2, arguments.size());
    SWRLBuiltInUtil.checkThatArgumentIsAnIndividual(SWRLXHasPropertyValue, 0, arguments);
    SWRLBuiltInUtil.checkThatArgumentIsAProperty(SWRLXHasPropertyValue, 1, arguments);

    individualName = SWRLBuiltInUtil.getArgumentAsAnIndividualName(SWRLXHasPropertyValue, 0, arguments);
    propertyName = SWRLBuiltInUtil.getArgumentAsAPropertyName(SWRLXHasPropertyValue, 1, arguments);

    try {
      result = SWRLOWLUtil.getNumberOfPropertyValues(owlModel, individualName, propertyName, true) != 0;
    } catch (SWRLOWLUtilException e) {
      throw new BuiltInException("Error: " + e.getMessage());
    } // try
    return result;
  } // hasPropertyValue

  /*
  ** Returns true if the individual named by the second parameter has the number of values specified by the first parameter for the
  ** property named by the third parameter. If the first parameter is unbound when the built-in is called, it is bound to the actual number
  ** of property values for the property for the specified individual.
  */
  public boolean hasNumberOfPropertyValues(List<Argument> arguments) throws BuiltInException
  {
    OWLModel owlModel = bridge.getOWLModel();
    String individualName, propertyName;
    int numberOfPropertyValues;
    boolean result = false;

    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(SWRLXHasNumberOfPropertyValues, 3, arguments.size());
    SWRLBuiltInUtil.checkThatArgumentIsAnIndividual(SWRLXHasNumberOfPropertyValues, 1, arguments);
    SWRLBuiltInUtil.checkThatArgumentIsAProperty(SWRLXHasNumberOfPropertyValues, 2, arguments);

    individualName = SWRLBuiltInUtil.getArgumentAsAnIndividualName(SWRLXHasNumberOfPropertyValues, 1, arguments);
    propertyName = SWRLBuiltInUtil.getArgumentAsAPropertyName(SWRLXHasNumberOfPropertyValues, 2, arguments);

    try {
      if (SWRLBuiltInUtil.isUnboundArgument(SWRLXHasNumberOfPropertyValues, 0, arguments)) {
        numberOfPropertyValues = SWRLOWLUtil.getNumberOfPropertyValues(owlModel, individualName, propertyName, true);
        arguments.set(0, new LiteralInfo(numberOfPropertyValues)); // Bind the result to the first parameter      
        result = true;
      } else {
        numberOfPropertyValues = SWRLBuiltInUtil.getArgumentAsAnInteger(SWRLXHasNumberOfPropertyValues, 0, arguments);
        result = (numberOfPropertyValues == SWRLOWLUtil.getNumberOfPropertyValues(owlModel, individualName, propertyName, true));
      } // if
    } catch (SWRLOWLUtilException e) {
      throw new BuiltInException("Error: " + e.getMessage());
    } // try
    return result;
  } // hasNumberOfPropertyValues

  /*
  ** Returns true if the class named by the first parameter has individuals.
  */
  public boolean hasIndividuals(List<Argument> arguments) throws BuiltInException
  {
    OWLModel owlModel = bridge.getOWLModel();
    String className;
    boolean result = false;

    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(SWRLXHasIndividuals, 1, arguments.size());
    SWRLBuiltInUtil.checkThatArgumentIsAClass(SWRLXHasIndividuals, 0, arguments);

    className = SWRLBuiltInUtil.getArgumentAsAClassName(SWRLXHasIndividuals, 0, arguments);

    try {
      result = (SWRLOWLUtil.getNumberOfClassIndividuals(owlModel, className) != 0);
    } catch (SWRLOWLUtilException e) {
      throw new BuiltInException("Error: " + e.getMessage());
    } // try
    return result;
  } // hasIndividuals

  /*
  ** Returns true if the class named by the second parameter has the number of individuals specified by the first parameter. If the first
  ** parameter is unbound when the built-in is called, it is bound to the actual number of individuals of the specified class.
  */
  public boolean hasNumberOfIndividuals(List<Argument> arguments) throws BuiltInException
  {
    OWLModel owlModel = bridge.getOWLModel();
    String className;
    int numberOfIndividuals;
    boolean result = false;

    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(SWRLXHasNumberOfIndividuals, 2, arguments.size());
    SWRLBuiltInUtil.checkThatArgumentIsAClass(SWRLXHasNumberOfIndividuals, 1, arguments);

    className = SWRLBuiltInUtil.getArgumentAsAClassName(SWRLXHasNumberOfIndividuals, 1, arguments);

    try {
      if (SWRLBuiltInUtil.isUnboundArgument(SWRLXHasNumberOfIndividuals, 0, arguments)) {
        numberOfIndividuals = SWRLOWLUtil.getNumberOfClassIndividuals(owlModel, className);
        arguments.set(0, new LiteralInfo(numberOfIndividuals)); // Bind the result to the first parameter      
        result = true;
      } else {
        numberOfIndividuals = SWRLBuiltInUtil.getArgumentAsAnInteger(SWRLXHasNumberOfIndividuals, 0, arguments);
        result = (numberOfIndividuals == SWRLOWLUtil.getNumberOfClassIndividuals(owlModel, className));
      } // if
    } catch (SWRLOWLUtilException e) {
      throw new BuiltInException("Error: " + e.getMessage());
    } // try
    return result;
  } // hasNumberOfIndividuals

} // SWRLBuiltInLibrary
