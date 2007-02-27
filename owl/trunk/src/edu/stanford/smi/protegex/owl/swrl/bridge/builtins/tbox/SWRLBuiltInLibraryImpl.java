
package edu.stanford.smi.protegex.owl.swrl.bridge.builtins.tbox;

import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.swrl.model.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.builtins.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;

import edu.stanford.smi.protegex.owl.swrl.util.SWRLOWLUtil;
import edu.stanford.smi.protegex.owl.swrl.exceptions.SWRLOWLUtilException;

import java.util.*;

/**
 ** Implementations library for SWRL TBox built-in methods. See <a
 ** href="http://protege.cim3.net/cgi-bin/wiki.pl?SWRLTBoxBuiltIns">here</a> for documentation on this library.
 **
 ** See <a href="http://protege.cim3.net/cgi-bin/wiki.pl?SWRLBuiltInBridge">here</a> for documentation on defining SWRL built-in libraries.
 */
public class SWRLBuiltInLibraryImpl implements SWRLBuiltInLibrary
{
  private static String SWRLTBoxNamespace = "tbox";

  public static String SWRLTBoxIsTransitiveProperty = SWRLTBoxNamespace + ":" + "isTransitiveProperty";
  public static String SWRLTBoxIsSymmetricProperty = SWRLTBoxNamespace + ":" + "isSymmetricProperty";
  public static String SWRLTBoxIsFunctionalProperty = SWRLTBoxNamespace + ":" + "isFunctionalProperty";
  public static String SWRLTBoxIsInverseFunctionalProperty = SWRLTBoxNamespace + ":" + "isInverseFunctionalProperty";
  public static String SWRLTBoxIsAnnotationProperty = SWRLTBoxNamespace + ":" + "isAnnotationProperty";
  public static String SWRLTBoxIsDirectSubPropertyOf = SWRLTBoxNamespace + ":" + "isDirectSubPropertyOf";
  public static String SWRLTBoxIsDirectSubClassOf = SWRLTBoxNamespace + ":" + "isDirectSubClassOf";
  public static String SWRLTBoxIsDirectSuperPropertyOf = SWRLTBoxNamespace + ":" + "isDirectSuperPropertyOf";
  public static String SWRLTBoxIsDirectSuperClassOf = SWRLTBoxNamespace + ":" + "isDirectSuperClassOf";
  public static String SWRLTBoxIsSubPropertyOf = SWRLTBoxNamespace + ":" + "isSubPropertyOf";
  public static String SWRLTBoxIsSubClassOf = SWRLTBoxNamespace + ":" + "isSubClassOf";
  public static String SWRLTBoxIsSuperPropertyOf = SWRLTBoxNamespace + ":" + "isSuperPropertyOf";
  public static String SWRLTBoxIsSuperClassOf = SWRLTBoxNamespace + ":" + "isSuperClassOf";

  private SWRLRuleEngineBridge bridge;
  private OWLModel owlModel;

  public void initialize(SWRLRuleEngineBridge bridge) 
  { 
    this.bridge = bridge; 
    owlModel = bridge.getOWLModel();
  } // initialize

  /**
   ** Determine if a single property argument is transitive.
   */
  public boolean isTransitiveProperty(List<Argument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(SWRLTBoxIsTransitiveProperty, 1, arguments.size());
    String propertyName = SWRLBuiltInUtil.getArgumentAsAPropertyName(SWRLTBoxIsTransitiveProperty, 0, arguments);

    boolean result = false;
    try {
      result = SWRLOWLUtil.isTransitiveProperty(owlModel, propertyName, true);
    } catch (SWRLOWLUtilException e) {
      throw new BuiltInException(e.getMessage());
    } // try

    return result;
  } // isTransitiveProperty

  /**
   ** Determine if a single property argument is symmetric.
   */
  public boolean isSymmetricProperty(List<Argument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(SWRLTBoxIsSymmetricProperty, 1, arguments.size());
    String propertyName = SWRLBuiltInUtil.getArgumentAsAPropertyName(SWRLTBoxIsSymmetricProperty, 0, arguments);

    boolean result = false;
    try {
      result = SWRLOWLUtil.isSymmetricProperty(owlModel, propertyName, true);
    } catch (SWRLOWLUtilException e) {
      throw new BuiltInException(e.getMessage());
    } // try

    return result;
  } // isSymmetricProperty

  /**
   ** Determine if a single property argument is functional.
   */
  public boolean isFunctionalProperty(List<Argument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(SWRLTBoxIsFunctionalProperty, 1, arguments.size());
    String propertyName = SWRLBuiltInUtil.getArgumentAsAPropertyName(SWRLTBoxIsFunctionalProperty, 0, arguments);

    boolean result = false;
    try {
      result = SWRLOWLUtil.isFunctionalProperty(owlModel, propertyName, true);
    } catch (SWRLOWLUtilException e) {
      throw new BuiltInException(e.getMessage());
    } // try

    return result;
  } // isFunctionalProperty

  /**
   ** Determine if a single property argument is an annotation property.
   */
  public boolean isAnnotationProperty(List<Argument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(SWRLTBoxIsAnnotationProperty, 1, arguments.size());
    String propertyName = SWRLBuiltInUtil.getArgumentAsAPropertyName(SWRLTBoxIsAnnotationProperty, 0, arguments);

    boolean result = false;
    try {
      result = SWRLOWLUtil.isAnnotationProperty(owlModel, propertyName, true);
    } catch (SWRLOWLUtilException e) {
      throw new BuiltInException(e.getMessage());
    } // try

    return result;
  } // isAnnotationProperty

  /**
   ** Determine if a single property argument is inverse functional.
   */
  public boolean isInverseFunctionalProperty(List<Argument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(SWRLTBoxIsInverseFunctionalProperty, 1, arguments.size());
    String propertyName = SWRLBuiltInUtil.getArgumentAsAPropertyName(SWRLTBoxIsInverseFunctionalProperty, 0, arguments);

    boolean result = false;
    try {
      result = SWRLOWLUtil.isInverseFunctionalProperty(owlModel, propertyName, true);
    } catch (SWRLOWLUtilException e) {
      throw new BuiltInException(e.getMessage());
    } // try

    return result;
  } // isInverseFunctionalProperty

  /**
   ** Determine if the first property argument is a direct subproperty of the second property argument. If the first argument is unbound,
   ** bind it to the direct sub properties of the second argument (if any exist).
   */
  public boolean isDirectSubPropertyOf(List<Argument> arguments) throws BuiltInException
  {
    boolean subPropertyArgumentUnbound = false;
    String propertyName;
    boolean result = false;

    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(SWRLTBoxIsDirectSubPropertyOf, 2, arguments.size());
    subPropertyArgumentUnbound = SWRLBuiltInUtil.isUnboundArgument(SWRLTBoxIsDirectSubPropertyOf, 0, arguments);
    propertyName = SWRLBuiltInUtil.getArgumentAsAPropertyName(SWRLTBoxIsDirectSubPropertyOf, 1, arguments);

    try {
      if (subPropertyArgumentUnbound) {
        List<OWLProperty> subProperties = SWRLOWLUtil.getDirectSubPropertiesOf(owlModel, propertyName);
        if (!subProperties.isEmpty()) {
          MultiArgument multiArgument = new MultiArgument();
          for (OWLProperty subProperty : subProperties) multiArgument.addArgument(new PropertyInfo(subProperty.getName()));
          arguments.set(0, multiArgument);
          result = true;
        } // if
      } else {
        String subPropertyName = SWRLBuiltInUtil.getArgumentAsAPropertyName(SWRLTBoxIsDirectSubPropertyOf, 0, arguments);
        result = SWRLOWLUtil.isDirectSubPropertyOf(owlModel, subPropertyName, propertyName, true);
      } // if
    } catch (SWRLOWLUtilException e) {
      throw new BuiltInException(e.getMessage());
    } // try

    return result;
  } // isDirectSubPropertyOf

  /**
   ** Determine if the first property argument is a direct superproperty of the second property argument. If the first argument is unbound,
   ** bind it to the direct super properties of the second argument (if any exist).
   */
  public boolean isDirectSuperPropertyOf(List<Argument> arguments) throws BuiltInException
  {
    boolean superPropertyArgumentUnbound = false;
    String propertyName;
    boolean result = false;

    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(SWRLTBoxIsDirectSuperPropertyOf, 2, arguments.size());
    superPropertyArgumentUnbound = SWRLBuiltInUtil.isUnboundArgument(SWRLTBoxIsDirectSuperPropertyOf, 0, arguments);
    propertyName = SWRLBuiltInUtil.getArgumentAsAPropertyName(SWRLTBoxIsDirectSuperPropertyOf, 1, arguments);

    try {
      if (superPropertyArgumentUnbound) {
        List<OWLProperty> superProperties = SWRLOWLUtil.getDirectSuperPropertiesOf(owlModel, propertyName);
        if (!superProperties.isEmpty()) {
          MultiArgument multiArgument = new MultiArgument();
          for (OWLProperty superProperty : superProperties) multiArgument.addArgument(new PropertyInfo(superProperty.getName()));
          arguments.set(0, multiArgument);
          result = true;
        } // if
      } else {
        String superPropertyName = SWRLBuiltInUtil.getArgumentAsAPropertyName(SWRLTBoxIsDirectSuperPropertyOf, 0, arguments);
        result = SWRLOWLUtil.isDirectSuperPropertyOf(owlModel, superPropertyName, propertyName, true);
      } // if
    } catch (SWRLOWLUtilException e) {
      throw new BuiltInException(e.getMessage());
    } // try

    return result;
  } // isDirectSuperPropertyOf

  /**
   ** Check that the first class argument is a direct subclass of the second class argument. If the first argument is unbound, bind it to
   ** the direct subclasses of the second argument (if any exist).
   */
  public boolean isDirectSubClassOf(List<Argument> arguments) throws BuiltInException
  {
    boolean subClassArgumentUnbound = false;
    String className;
    boolean result = false;

    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(SWRLTBoxIsDirectSubClassOf, 2, arguments.size());

    subClassArgumentUnbound = SWRLBuiltInUtil.isUnboundArgument(SWRLTBoxIsDirectSubClassOf, 0, arguments);
    className = SWRLBuiltInUtil.getArgumentAsAClassName(SWRLTBoxIsDirectSubClassOf, 1, arguments);

    try {
      if (subClassArgumentUnbound) {
        List<OWLNamedClass> subClasses = SWRLOWLUtil.getDirectSubClassesOf(owlModel, className);
        if (!subClasses.isEmpty()) {
          MultiArgument multiArgument = new MultiArgument();
          for (OWLNamedClass subClass : subClasses) multiArgument.addArgument(new ClassInfo(subClass.getName()));
          arguments.set(0, multiArgument);
          result = true;
        } // if
      } else {
        String subClassName = SWRLBuiltInUtil.getArgumentAsAClassName(SWRLTBoxIsDirectSubClassOf, 0, arguments);
        result = SWRLOWLUtil.isDirectSubClassOf(owlModel, subClassName, className, true);
      } // if
    } catch (SWRLOWLUtilException e) {
      throw new BuiltInException(e.getMessage());
    } // try

    return result;
  } // isDirectSubClassOf

  /**
   ** Check that the first class argument is a direct superclass of the second class argument. If the first argument is unbound, bind it to
   ** the direct superclasses of the second argument (if any exist).
   */
  public boolean isDirectSuperClassOf(List<Argument> arguments) throws BuiltInException
  {
    boolean superClassArgumentUnbound = false;
    String className;
    boolean result = false;

    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(SWRLTBoxIsDirectSuperClassOf, 2, arguments.size());

    superClassArgumentUnbound = SWRLBuiltInUtil.isUnboundArgument(SWRLTBoxIsDirectSuperClassOf, 0, arguments);
    className = SWRLBuiltInUtil.getArgumentAsAClassName(SWRLTBoxIsDirectSuperClassOf, 1, arguments);

    try {
      if (superClassArgumentUnbound) {
        List<OWLNamedClass> superClasses = SWRLOWLUtil.getDirectSuperClassesOf(owlModel, className);
        if (!superClasses.isEmpty()) {
          MultiArgument multiArgument = new MultiArgument();
          for (OWLNamedClass superClass : superClasses) multiArgument.addArgument(new ClassInfo(superClass.getName()));
          arguments.set(0, multiArgument);
          result = true;
        } // if
      } else {
        String superClassName = SWRLBuiltInUtil.getArgumentAsAClassName(SWRLTBoxIsDirectSuperClassOf, 0, arguments);
        result = SWRLOWLUtil.isDirectSuperClassOf(owlModel, superClassName, className, true);
      } // if
    } catch (SWRLOWLUtilException e) {
      throw new BuiltInException(e.getMessage());
    } // try

    return result;
  } // isDirectSuperClassOf

} // SWRLBuiltInLibrary
