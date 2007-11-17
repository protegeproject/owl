
// TODO: lot of repetition in methods. Clean up.

package edu.stanford.smi.protegex.owl.swrl.bridge.builtins.abox;

import edu.stanford.smi.protegex.owl.swrl.model.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.builtins.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;
import edu.stanford.smi.protegex.owl.swrl.util.SWRLOWLUtil;
import edu.stanford.smi.protegex.owl.swrl.exceptions.SWRLOWLUtilException;

import edu.stanford.smi.protegex.owl.model.RDFSLiteral;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;

import java.util.*;

/**
 ** Implementations library for SWRL ABox built-in methods. See <a
 ** href="http://protege.cim3.net/cgi-bin/wiki.pl?SWRLABoxBuiltIns">here</a> for documentation on this library.
 **
 ** See <a href="http://protege.cim3.net/cgi-bin/wiki.pl?SWRLBuiltInBridge">here</a> for documentation on defining SWRL built-in libraries.
 */
public class SWRLBuiltInLibraryImpl extends AbstractSWRLBuiltInLibrary
{
  private static String SWRLABoxLibraryName = "SWRLABoxBuiltIns";
  private static String SWRLABoxPrefix = "abox:";

  private ArgumentFactory argumentFactory;

  public SWRLBuiltInLibraryImpl() 
  { 
    super(SWRLABoxLibraryName); 

    argumentFactory = ArgumentFactory.getFactory();
  } // SWRLBuiltInLibraryImpl

  public void reset() {}

  /**
   ** Determine if a single argument is an OWL individual. If the argument is unbound, bind it to all OWL individuals in an ontology.
   */
  public boolean isIndividual(List<BuiltInArgument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(1, arguments.size());
    boolean isUnboundArgument = SWRLBuiltInUtil.isUnboundArgument(0, arguments);   
    boolean result = false;

    try {
      if (isUnboundArgument) {
        MultiArgument multiArgument = argumentFactory.createMultiArgument(SWRLBuiltInUtil.getVariableName(0, arguments));
        for (edu.stanford.smi.protegex.owl.model.OWLIndividual individual : SWRLOWLUtil.getAllIndividuals(getInvokingBridge().getOWLModel()))
          multiArgument.addArgument(argumentFactory.createIndividualArgument(individual.getName()));
        arguments.set(0, multiArgument);
        result = !multiArgument.hasNoArguments();
      } else {
        String individualName = SWRLBuiltInUtil.getArgumentAsAnIndividualName(0, arguments);
        result = SWRLOWLUtil.isIndividual(getInvokingBridge().getOWLModel(), individualName, false);
      } // if
    } catch (SWRLOWLUtilException e) {
      throw new BuiltInException(e.getMessage());
    } // try

    return result;
  } // isIndividual

  /**
   ** Returns true if the individual named by the first argument has a property specified by the second argument with the value specified by
   ** the third argument. If the third argument in unbound, bind it to all the values for this property for the specified individual.
   */
  public boolean hasValue(List<BuiltInArgument> arguments) throws BuiltInException
  {
    String individualName, propertyName = null;
    Object propertyValue = null;
    boolean propertyValueSupplied;
    boolean result = false, isObjectProperty;

    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(3, arguments.size());
    individualName = SWRLBuiltInUtil.getArgumentAsAnIndividualName(0, arguments);
    propertyName = SWRLBuiltInUtil.getArgumentAsAPropertyName(1, arguments);

    propertyValueSupplied = !SWRLBuiltInUtil.isUnboundArgument(2, arguments);

    try {
      isObjectProperty = SWRLOWLUtil.isObjectProperty(getInvokingBridge().getOWLModel(), propertyName);

      if (propertyValueSupplied) {
        propertyValue = SWRLBuiltInUtil.getArgumentAsAPropertyValue(2, arguments);
        if (isObjectProperty)
          result = (propertyValue == SWRLOWLUtil.getObjectPropertyValue(getInvokingBridge().getOWLModel(), individualName, propertyName, false));
        else 
          result = (propertyValue == SWRLOWLUtil.getDatavaluedPropertyValues(getInvokingBridge().getOWLModel(), individualName, propertyName, false));
      } else { // Unbound
        MultiArgument multiArgument = argumentFactory.createMultiArgument(SWRLBuiltInUtil.getVariableName(2, arguments));
        if (isObjectProperty) {
          for (Object value : SWRLOWLUtil.getObjectPropertyValues(getInvokingBridge().getOWLModel(), individualName, propertyName)) {
            if (value instanceof edu.stanford.smi.protegex.owl.model.OWLIndividual) {
              edu.stanford.smi.protegex.owl.model.OWLIndividual individual = (edu.stanford.smi.protegex.owl.model.OWLIndividual)value;
              multiArgument.addArgument(argumentFactory.createIndividualArgument(individual.getName()));
            } else if (value instanceof edu.stanford.smi.protegex.owl.model.OWLNamedClass) {
              edu.stanford.smi.protegex.owl.model.OWLNamedClass cls = (edu.stanford.smi.protegex.owl.model.OWLNamedClass)value;
              multiArgument.addArgument(argumentFactory.createClassArgument(cls.getName()));
            } else if (value instanceof edu.stanford.smi.protegex.owl.model.OWLDatatypeProperty) {
              edu.stanford.smi.protegex.owl.model.OWLDatatypeProperty owlDatatypeProperty = (edu.stanford.smi.protegex.owl.model.OWLDatatypeProperty)value;
              multiArgument.addArgument(argumentFactory.createDatatypePropertyArgument(owlDatatypeProperty.getName()));
            } else if (value instanceof edu.stanford.smi.protegex.owl.model.OWLObjectProperty) {
              edu.stanford.smi.protegex.owl.model.OWLObjectProperty owlObjectProperty = (edu.stanford.smi.protegex.owl.model.OWLObjectProperty)value;
              multiArgument.addArgument(argumentFactory.createObjectPropertyArgument(owlObjectProperty.getName()));
            } // if
          } // for
        } else { // Datatype property
          for (Object value : SWRLOWLUtil.getDatavaluedPropertyValues(getInvokingBridge().getOWLModel(), individualName, propertyName))
            if (value instanceof edu.stanford.smi.protegex.owl.model.RDFSLiteral) {
              edu.stanford.smi.protegex.owl.model.RDFSLiteral literal = (edu.stanford.smi.protegex.owl.model.RDFSLiteral)value;
              multiArgument.addArgument(argumentFactory.createDatatypeValueArgument(getInvokingBridge().getOWLModel(), literal));
            } // if
        } // if
        arguments.set(2, multiArgument);
        result = !multiArgument.hasNoArguments();
      } // if
    } catch (SWRLOWLUtilException e) {
      throw new BuiltInException(e.getMessage());
    } // try

    return result;
  } // hasValue

  /**
   ** Returns true if the individual named by the first argument has a property specified by the second argument. If the second argument in
   ** unbound, bind it to all the properties that currently have a value for this individual.
   */
  public boolean hasProperty(List<BuiltInArgument> arguments) throws BuiltInException
  {
    String individualName, propertyName = null;
    Object propertyValue = null;
    boolean hasUnboundPropertyArgument = SWRLBuiltInUtil.isUnboundArgument(1, arguments);
    boolean result = false;

    SWRLBuiltInUtil.checkNumberOfArgumentsInRange(2, 3, arguments.size());
    individualName = SWRLBuiltInUtil.getArgumentAsAnIndividualName(0, arguments);
    propertyName = SWRLBuiltInUtil.getArgumentAsAPropertyName(1, arguments);

    try {
      if (hasUnboundPropertyArgument) {
        MultiArgument multiArgument = argumentFactory.createMultiArgument(SWRLBuiltInUtil.getVariableName(1, arguments));
        for (edu.stanford.smi.protegex.owl.model.OWLProperty property : SWRLOWLUtil.getPropertiesOfIndividual(getInvokingBridge().getOWLModel(), individualName)) {
          if (property.isObjectProperty()) multiArgument.addArgument(argumentFactory.createObjectPropertyArgument(property.getName()));
          else multiArgument.addArgument(argumentFactory.createDatatypePropertyArgument(property.getName()));
        } // for
        arguments.set(1, multiArgument);
        result = !multiArgument.hasNoArguments();
      } else
        result = SWRLOWLUtil.getNumberOfPropertyValues(getInvokingBridge().getOWLModel(), individualName, propertyName, true) != 0;
    } catch (SWRLOWLUtilException e) {
      throw new BuiltInException(e.getMessage());
    } // try

    return result;
  } // hasProperty

  /**
   ** Returns true if the class named by the first argument has an individual identified by the second argument. If the second argument is
   ** unbound, bind it to all individuals of the class.
   */
  public boolean hasIndividual(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean isUnboundArgument = SWRLBuiltInUtil.isUnboundArgument(1, arguments);   
    String className;
    boolean result = false;

    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(2, arguments.size());
    SWRLBuiltInUtil.checkThatArgumentIsAClass(0, arguments);

    className = SWRLBuiltInUtil.getArgumentAsAClassName(0, arguments);

    try {
      if (isUnboundArgument) {
        MultiArgument multiArgument = argumentFactory.createMultiArgument(SWRLBuiltInUtil.getVariableName(1, arguments));
        for (edu.stanford.smi.protegex.owl.model.OWLIndividual individual: SWRLOWLUtil.getIndividuals(getInvokingBridge().getOWLModel(), className)) 
          multiArgument.addArgument(argumentFactory.createIndividualArgument(individual.getName()));
        arguments.set(1, multiArgument);
        result = !multiArgument.hasNoArguments();
      } else { // Bound argument
        String individualName = SWRLBuiltInUtil.getArgumentAsAnIndividualName(1, arguments);
        result = SWRLOWLUtil.isIndividualOfClass(getInvokingBridge().getOWLModel(), individualName, className);
      } // if
    } catch (SWRLOWLUtilException e) {
      throw new BuiltInException(e.getMessage());
    } // try
    return result;
  } // hasIndividual

  /**
   ** Returns true if the OWL class, property, or individual named by the first argument has a URI identified by the second
   ** argument. If the second argument is unbound, bind it to URI of the resource.
   */
  public boolean hasURI(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean isUnboundArgument = SWRLBuiltInUtil.isUnboundArgument(1, arguments);   
    String resourceName, uri;
    boolean result = false;

    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(2, arguments.size());
    SWRLBuiltInUtil.checkThatArgumentIsAClassPropertyOrIndividual(0, arguments);

    resourceName = SWRLBuiltInUtil.getArgumentAsAResourceName(0, arguments);

    try {
      if (isUnboundArgument) {
        uri = SWRLOWLUtil.getURI(getInvokingBridge().getOWLModel(), resourceName);
        arguments.set(1, argumentFactory.createDatatypeValueArgument(uri));
        result = true;
      } else { // Bound argument
        uri = SWRLBuiltInUtil.getArgumentAsAString(1, arguments);
        result = uri.equals(SWRLOWLUtil.getURI(getInvokingBridge().getOWLModel(), resourceName));
      } // if
    } catch (SWRLOWLUtilException e) {
      throw new BuiltInException(e.getMessage());
    } // try
    return result;
  } // hasURI

  /**
   ** Returns true if the individual named by the first argument is an instance of the class identified by the second argument. If the
   ** second argument is unbound, bind it to all defining classes of the individual.
   */
  public boolean hasClass(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean isUnboundArgument = SWRLBuiltInUtil.isUnboundArgument(1, arguments);   
    String individualName;
    boolean result = false;

    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(2, arguments.size());
    SWRLBuiltInUtil.checkThatArgumentIsAnIndividual(0, arguments);

    individualName = SWRLBuiltInUtil.getArgumentAsAnIndividualName(0, arguments);

    try {
      if (isUnboundArgument) {
        MultiArgument multiArgument = argumentFactory.createMultiArgument(SWRLBuiltInUtil.getVariableName(1, arguments));
        for (OWLNamedClass cls: SWRLOWLUtil.getClassesOfIndividual(getInvokingBridge().getOWLModel(), individualName)) 
          multiArgument.addArgument(argumentFactory.createClassArgument(cls.getName()));
        arguments.set(1, multiArgument);
        result = !multiArgument.hasNoArguments();
      } else { // Bound argument
        String className = SWRLBuiltInUtil.getArgumentAsAClassName(1, arguments);
        result = SWRLOWLUtil.isIndividualOfClass(getInvokingBridge().getOWLModel(), individualName, className);
      } // if
    } catch (SWRLOWLUtilException e) {
      throw new BuiltInException(e.getMessage());
    } // try
    return result;
  } // hasClass

  /**
   ** setClass(i, c)
   */
  public boolean setClass(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } // setClass

  /**
   ** setClass(i, p, v)
   */
  public boolean setValue(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } // setValue

} // SWRLBuiltInLibraryImpl
