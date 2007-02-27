
// cf. http://listserv.manchester.ac.uk/cgi-bin/wa?A2=ind0611&L=dig-wg&T=0&P=754
// TODO: additional methods to think about:
//
// isDisjointWith, isEquivalentTo for classes and properties.

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

  public static String SWRLTBoxIsDirectSuperClassOf = SWRLTBoxNamespace + ":" + "isDirectSuperClassOf";
  public static String SWRLTBoxIsSuperClassOf = SWRLTBoxNamespace + ":" + "isSuperClassOf";
  public static String SWRLTBoxIsDirectSubClassOf = SWRLTBoxNamespace + ":" + "isDirectSubClassOf";
  public static String SWRLTBoxIsSubClassOf = SWRLTBoxNamespace + ":" + "isSubClassOf";
  public static String SWRLTBoxIsDirectSubPropertyOf = SWRLTBoxNamespace + ":" + "isDirectSubPropertyOf";
  public static String SWRLTBoxIsSubPropertyOf = SWRLTBoxNamespace + ":" + "isSubPropertyOf";
  public static String SWRLTBoxIsDirectSuperPropertyOf = SWRLTBoxNamespace + ":" + "isDirectSuperPropertyOf";
  public static String SWRLTBoxIsSuperPropertyOf = SWRLTBoxNamespace + ":" + "isSuperPropertyOf";
  public static String SWRLTBoxIsDisjointWith = SWRLTBoxNamespace + ":" + "isDisjointWith";
  public static String SWRLTBoxIsEquivalentTo = SWRLTBoxNamespace + ":" + "isEquivalentTo";

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
   ** Determine if the two class or property arguments represent classes or properties that are equivalent to each other. If the first
   ** argument is unbound, bind it to the equivalent properties or classes of the second argument (if any exist).
   */
  public boolean isEquivalentTo(List<Argument> arguments) throws BuiltInException
  {
    throw new BuiltInNotImplementedException(SWRLTBoxIsEquivalentTo, "");
  } // isEquivalentTo

  /**
   ** Determine if the two class or property arguments represent classes or properties that are disjoint with each other. If the first
   ** argument is unbound, bind it to the disjoint properties or classes of the second argument (if any exist).
   */
  public boolean isDisjointWith(List<Argument> arguments) throws BuiltInException
  {
    throw new BuiltInNotImplementedException(SWRLTBoxIsDisjointWith, "");
  } // isDisjointWith

  /**
   ** Determine if the first property argument is a direct subproperty of the second property argument. If the first argument is unbound,
   ** bind it to the direct sub properties of the second argument (if any exist).
   */
  public boolean isDirectSubPropertyOf(List<Argument> arguments) throws BuiltInException
  {
    return isSubPropertyOf(SWRLTBoxIsDirectSubPropertyOf, arguments, false);
  } // isDirectSubPropertyOf

  /**
   ** Determine if the first property argument is a subproperty of the second property argument. If the first argument is unbound,
   ** bind it to the sub properties of the second argument (if any exist).
   */
  public boolean isSubPropertyOf(List<Argument> arguments) throws BuiltInException
  {
    return isSubPropertyOf(SWRLTBoxIsDirectSubPropertyOf, arguments, true);
  } // isSubPropertyOf

  /**
   ** Determine if the first property argument is a direct superproperty of the second property argument. If the first argument is unbound,
   ** bind it to the direct super properties of the second argument (if any exist).
   */
  public boolean isDirectSuperPropertyOf(List<Argument> arguments) throws BuiltInException
  {
    return isSuperPropertyOf(SWRLTBoxIsDirectSuperPropertyOf, arguments, false);
  } // isDirectSuperPropertyOf

  /**
   ** Determine if the first property argument is a superproperty of the second property argument. If the first argument is unbound,
   ** bind it to the super properties of the second argument (if any exist).
   */
  public boolean isSuperPropertyOf(List<Argument> arguments) throws BuiltInException
  {
    return isSuperPropertyOf(SWRLTBoxIsSuperPropertyOf, arguments, true);
  } // isSuperPropertyOf

  /**
   ** Check that the first class argument is a direct subclass of the second class argument. If the first argument is unbound, bind it to
   ** the direct subclasses of the second argument (if any exist).
   */
  public boolean isDirectSubClassOf(List<Argument> arguments) throws BuiltInException
  {
    return isSubClassOf(SWRLTBoxIsDirectSubClassOf, arguments, false);
  } // isDirectSubClassOf

  /**
   ** Check that the first class argument is a subclass of the second class argument. If the first argument is unbound, bind it to
   ** the subclasses of the second argument (if any exist).
   */
  public boolean isSubClassOf(List<Argument> arguments) throws BuiltInException
  {
    return isSubClassOf(SWRLTBoxIsSubClassOf, arguments, true);
  } // isSubClassOf

  /**
   ** Check that the first class argument is a direct superclass of the second class argument. If the first argument is unbound, bind it to
   ** the direct superclasses of the second argument (if any exist).
   */
  public boolean isDirectSuperClassOf(List<Argument> arguments) throws BuiltInException
  {
    return isSuperClassOf(SWRLTBoxIsDirectSuperClassOf, arguments, false);
  } // isDirectSuperClassOf

  /**
   ** Check that the first class argument is a superclass of the second class argument. If the first argument is unbound, bind it to
   ** the superclasses of the second argument (if any exist).
   */
  public boolean isSuperClassOf(List<Argument> arguments) throws BuiltInException
  {
    return isSuperClassOf(SWRLTBoxIsSuperClassOf, arguments, true);
  } // isSuperClassOf

  private boolean isSuperClassOf(String builtInName, List<Argument> arguments, boolean transitive) throws BuiltInException
  {
    boolean superClassArgumentUnbound = false;
    String className;
    boolean result = false;

    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(builtInName, 2, arguments.size());

    superClassArgumentUnbound = SWRLBuiltInUtil.isUnboundArgument(builtInName, 0, arguments);
    className = SWRLBuiltInUtil.getArgumentAsAClassName(builtInName, 1, arguments);

    try {
      if (superClassArgumentUnbound) {
        List<OWLNamedClass> superClasses;
        if (transitive) superClasses = SWRLOWLUtil.getSuperClassesOf(owlModel, className);
        else superClasses = SWRLOWLUtil.getDirectSuperClassesOf(owlModel, className);
        if (!superClasses.isEmpty()) {
          MultiArgument multiArgument = new MultiArgument();
          for (OWLNamedClass superClass : superClasses) multiArgument.addArgument(new ClassInfo(superClass.getName()));
          arguments.set(0, multiArgument);
          result = true;
        } // if
      } else {
        String superClassName = SWRLBuiltInUtil.getArgumentAsAClassName(builtInName, 0, arguments);
        if (transitive) result = SWRLOWLUtil.isSuperClassOf(owlModel, superClassName, className, true);
        else result = SWRLOWLUtil.isDirectSuperClassOf(owlModel, superClassName, className, true);
      } // if
    } catch (SWRLOWLUtilException e) {
      throw new BuiltInException(e.getMessage());
    } // try

    return result;
  } // isSuperClassOf

  private boolean isSubClassOf(String builtInName, List<Argument> arguments, boolean transitive) throws BuiltInException
  {
    boolean subClassArgumentUnbound = false;
    String className;
    boolean result = false;

    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(builtInName, 2, arguments.size());

    subClassArgumentUnbound = SWRLBuiltInUtil.isUnboundArgument(builtInName, 0, arguments);
    className = SWRLBuiltInUtil.getArgumentAsAClassName(builtInName, 1, arguments);

    try {
      if (subClassArgumentUnbound) {
        List<OWLNamedClass> subClasses;
        if (transitive) subClasses = SWRLOWLUtil.getSubClassesOf(owlModel, className);
        else subClasses = SWRLOWLUtil.getDirectSubClassesOf(owlModel, className);
        if (!subClasses.isEmpty()) {
          MultiArgument multiArgument = new MultiArgument();
          for (OWLNamedClass subClass : subClasses) multiArgument.addArgument(new ClassInfo(subClass.getName()));
          arguments.set(0, multiArgument);
          result = true;
        } // if
      } else {
        String subClassName = SWRLBuiltInUtil.getArgumentAsAClassName(builtInName, 0, arguments);
        if (transitive) result = SWRLOWLUtil.isSubClassOf(owlModel, subClassName, className, true);
        else  result = SWRLOWLUtil.isDirectSubClassOf(owlModel, subClassName, className, true);
      } // if
    } catch (SWRLOWLUtilException e) {
      throw new BuiltInException(e.getMessage());
    } // try

    return result;
  } // isSubClassOf

  private boolean isSubPropertyOf(String builtInName, List<Argument> arguments, boolean transitive) throws BuiltInException
  {
    boolean subPropertyArgumentUnbound = false;
    String propertyName;
    boolean result = false;

    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(builtInName, 2, arguments.size());
    subPropertyArgumentUnbound = SWRLBuiltInUtil.isUnboundArgument(builtInName, 0, arguments);
    propertyName = SWRLBuiltInUtil.getArgumentAsAPropertyName(builtInName, 1, arguments);

    try {
      if (subPropertyArgumentUnbound) {
        List<OWLProperty> subProperties;
        if (transitive) subProperties = SWRLOWLUtil.getSubPropertiesOf(owlModel, propertyName);
        else subProperties = SWRLOWLUtil.getDirectSubPropertiesOf(owlModel, propertyName);
        if (!subProperties.isEmpty()) {
          MultiArgument multiArgument = new MultiArgument();
          for (OWLProperty subProperty : subProperties) multiArgument.addArgument(new PropertyInfo(subProperty.getName()));
          arguments.set(0, multiArgument);
          result = true;
        } // if
      } else {
        String subPropertyName = SWRLBuiltInUtil.getArgumentAsAPropertyName(builtInName, 0, arguments);
        if (transitive) result = SWRLOWLUtil.isSubPropertyOf(owlModel, subPropertyName, propertyName, true);
        else result = SWRLOWLUtil.isDirectSubPropertyOf(owlModel, subPropertyName, propertyName, true);
      } // if
    } catch (SWRLOWLUtilException e) {
      throw new BuiltInException(e.getMessage());
    } // try

    return result;
  } // isSubPropertyOf

  private boolean isSuperPropertyOf(String builtInName, List<Argument> arguments, boolean transitive) throws BuiltInException
  {
    boolean superPropertyArgumentUnbound = false;
    String propertyName;
    boolean result = false;

    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(builtInName, 2, arguments.size());
    superPropertyArgumentUnbound = SWRLBuiltInUtil.isUnboundArgument(builtInName, 0, arguments);
    propertyName = SWRLBuiltInUtil.getArgumentAsAPropertyName(builtInName, 1, arguments);

    try {
      if (superPropertyArgumentUnbound) {
        List<OWLProperty> superProperties;
        if (transitive) superProperties = SWRLOWLUtil.getSuperPropertiesOf(owlModel, propertyName);
        else superProperties = SWRLOWLUtil.getDirectSuperPropertiesOf(owlModel, propertyName);
        if (!superProperties.isEmpty()) {
          MultiArgument multiArgument = new MultiArgument();
          for (OWLProperty superProperty : superProperties) multiArgument.addArgument(new PropertyInfo(superProperty.getName()));
          arguments.set(0, multiArgument);
          result = true;
        } // if
      } else {
        String superPropertyName = SWRLBuiltInUtil.getArgumentAsAPropertyName(builtInName, 0, arguments);
        if (transitive) result = SWRLOWLUtil.isSuperPropertyOf(owlModel, superPropertyName, propertyName, true);
        else result = SWRLOWLUtil.isDirectSuperPropertyOf(owlModel, superPropertyName, propertyName, true);
      } // if
    } catch (SWRLOWLUtilException e) {
      throw new BuiltInException(e.getMessage());
    } // try

    return result;
  } // isSuperPropertyOf

} // SWRLBuiltInLibrary
