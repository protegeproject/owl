
// TODO: a lot of repetition here
// TODO: additional methods to think about:
// cf. http://listserv.manchester.ac.uk/cgi-bin/wa?A2=ind0611&L=dig-wg&T=0&P=754
//
// isDisjointWith, isEquivalentTo for classes and properties.
// 

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
public class SWRLBuiltInLibraryImpl extends SWRLBuiltInLibrary
{
  private static String SWRLTBoxLibraryName = "SWRLTBoxBuiltIns";

  private static String SWRLTBoxPrefix = "tbox:";

  public SWRLBuiltInLibraryImpl() { super(SWRLTBoxLibraryName); }

  public void reset() {}

  /**
   ** Determine if a single property argument is an OWL property. If the argument is unbound, bind it to all OWL properties in an ontology.
   */
  public boolean isProperty(List<Argument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(1, arguments.size());
    boolean isUnboundArgument = SWRLBuiltInUtil.isUnboundArgument(0, arguments);   
    boolean result = false;

    try {
      if (isUnboundArgument) {
        MultiArgument multiArgument = new MultiArgument();
        for (OWLProperty property : SWRLOWLUtil.getUserDefinedOWLProperties(getInvokingBridge().getOWLModel()))
          multiArgument.addArgument(new PropertyInfo(property.getName()));
        arguments.set(0, multiArgument);
        result = !multiArgument.hasNoArguments();
      } else {
        String propertyName = SWRLBuiltInUtil.getArgumentAsAPropertyName(0, arguments);
        result = SWRLOWLUtil.isProperty(getInvokingBridge().getOWLModel(), propertyName, false);
      } // if
    } catch (SWRLOWLUtilException e) {
      throw new BuiltInException(e.getMessage());
    } // try

    return result;
  } // isProperty

  /**
   ** Determine if a single argument is an OWL object property. If the argument is unbound, bind it to all OWL object properties in an
   ** ontology.
   */
  public boolean isObjectProperty(List<Argument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(1, arguments.size());
    boolean isUnboundArgument = SWRLBuiltInUtil.isUnboundArgument(0, arguments);   
    boolean result = false;

    try {
      if (isUnboundArgument) {
        MultiArgument multiArgument = new MultiArgument();
        for (OWLProperty property : SWRLOWLUtil.getUserDefinedOWLObjectProperties(getInvokingBridge().getOWLModel()))
          multiArgument.addArgument(new PropertyInfo(property.getName()));
        arguments.set(0, multiArgument);
        result = !multiArgument.hasNoArguments();
      } else {
        String propertyName = SWRLBuiltInUtil.getArgumentAsAPropertyName(0, arguments);
        result = SWRLOWLUtil.isObjectProperty(getInvokingBridge().getOWLModel(), propertyName, false);
      } // if
    } catch (SWRLOWLUtilException e) {
      throw new BuiltInException(e.getMessage());
    } // try

    return result;
  } // isObjectProperty

  /**
   ** Determine if a single argument is an OWL datatype property. If the argument is unbound, bind it to all OWL datatype
   ** properties in an ontology.
   */
  public boolean isDatatypeProperty(List<Argument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(1, arguments.size());
    boolean isUnboundArgument = SWRLBuiltInUtil.isUnboundArgument(0, arguments);   
    boolean result = false;

    try {
      if (isUnboundArgument) {
        MultiArgument multiArgument = new MultiArgument();
        for (OWLProperty property : SWRLOWLUtil.getUserDefinedOWLDatatypeProperties(getInvokingBridge().getOWLModel()))
          multiArgument.addArgument(new PropertyInfo(property.getName()));
        arguments.set(0, multiArgument);
        result = !multiArgument.hasNoArguments();
      } else {
        String propertyName = SWRLBuiltInUtil.getArgumentAsAPropertyName(0, arguments);
        result = SWRLOWLUtil.isDatatypeProperty(getInvokingBridge().getOWLModel(), propertyName, false);
      } // if
    } catch (SWRLOWLUtilException e) {
      throw new BuiltInException(e.getMessage());
    } // try

    return result;
  } // isDatatypeProperty

  /**
   ** Determine if a single argument is an OWL named class. If the argument is unbound, bind it to all OWL named classes in an ontology.
   */
  public boolean isClass(List<Argument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(1, arguments.size());
    boolean isUnboundArgument = SWRLBuiltInUtil.isUnboundArgument(0, arguments);   
    boolean result = false;

    try {
      if (isUnboundArgument) {
        MultiArgument multiArgument = new MultiArgument();
        for (OWLNamedClass cls : SWRLOWLUtil.getUserDefinedOWLNamedClasses(getInvokingBridge().getOWLModel()))
          multiArgument.addArgument(new PropertyInfo(cls.getName()));
        arguments.set(0, multiArgument);
        result = !multiArgument.hasNoArguments();
      } else {
        String className = SWRLBuiltInUtil.getArgumentAsAClassName(0, arguments);
        result = SWRLOWLUtil.isClass(getInvokingBridge().getOWLModel(), className, false);
      } // if
    } catch (SWRLOWLUtilException e) {
      throw new BuiltInException(e.getMessage());
    } // try

    return result;
  } // isClass

  /**
   ** Determine if a single property argument is transitive.
   */
  public boolean isTransitiveProperty(List<Argument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(1, arguments.size());
    String propertyName = SWRLBuiltInUtil.getArgumentAsAPropertyName(0, arguments);

    boolean result = false;
    try {
      result = SWRLOWLUtil.isTransitiveProperty(getInvokingBridge().getOWLModel(), propertyName, true);
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
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(1, arguments.size());
    String propertyName = SWRLBuiltInUtil.getArgumentAsAPropertyName(0, arguments);

    boolean result = false;
    try {
      result = SWRLOWLUtil.isSymmetricProperty(getInvokingBridge().getOWLModel(), propertyName, true);
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
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(1, arguments.size());
    String propertyName = SWRLBuiltInUtil.getArgumentAsAPropertyName(0, arguments);

    boolean result = false;
    try {
      result = SWRLOWLUtil.isFunctionalProperty(getInvokingBridge().getOWLModel(), propertyName, true);
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
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(1, arguments.size());
    String propertyName = SWRLBuiltInUtil.getArgumentAsAPropertyName(0, arguments);

    boolean result = false;
    try {
      result = SWRLOWLUtil.isAnnotationProperty(getInvokingBridge().getOWLModel(), propertyName, true);
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
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(1, arguments.size());
    String propertyName = SWRLBuiltInUtil.getArgumentAsAPropertyName(0, arguments);

    boolean result = false;
    try {
      result = SWRLOWLUtil.isInverseFunctionalProperty(getInvokingBridge().getOWLModel(), propertyName, true);
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
    throw new BuiltInNotImplementedException(SWRLTBoxPrefix + "isEquivalentTo");
  } // isEquivalentTo

  /**
   ** Determine if the two class or property arguments represent classes or properties that are disjoint with each other. If the first
   ** argument is unbound, bind it to the disjoint properties or classes of the second argument (if any exist).
   */
  public boolean isDisjointWith(List<Argument> arguments) throws BuiltInException
  {
    throw new BuiltInNotImplementedException(SWRLTBoxPrefix + "isDisjointWith");
  } // isDisjointWith

  /**
   ** Determine if the first property argument is a direct subproperty of the second property argument. If the first argument is unbound,
   ** bind it to the direct sub properties of the second argument (if any exist).
   */
  public boolean isDirectSubPropertyOf(List<Argument> arguments) throws BuiltInException
  {
    return isSubPropertyOf(arguments, false);
  } // isDirectSubPropertyOf

  /**
   ** Determine if the first property argument is a subproperty of the second property argument. If the first argument is unbound,
   ** bind it to the sub properties of the second argument (if any exist).
   */
  public boolean isSubPropertyOf(List<Argument> arguments) throws BuiltInException
  {
    return isSubPropertyOf(arguments, true);
  } // isSubPropertyOf

  /**
   ** Determine if the first property argument is a direct superproperty of the second property argument. If the first argument is unbound,
   ** bind it to the direct super properties of the second argument (if any exist).
   */
  public boolean isDirectSuperPropertyOf(List<Argument> arguments) throws BuiltInException
  {
    return isSuperPropertyOf(arguments, false);
  } // isDirectSuperPropertyOf

  /**
   ** Determine if the first property argument is a superproperty of the second property argument. If the first argument is unbound,
   ** bind it to the super properties of the second argument (if any exist).
   */
  public boolean isSuperPropertyOf(List<Argument> arguments) throws BuiltInException
  {
    return isSuperPropertyOf(arguments, true);
  } // isSuperPropertyOf

  /**
   ** Check that the first class argument is a direct subclass of the second class argument. If the first argument is unbound, bind it to
   ** the direct subclasses of the second argument (if any exist).
   */
  public boolean isDirectSubClassOf(List<Argument> arguments) throws BuiltInException
  {
    return isSubClassOf(arguments, false);
  } // isDirectSubClassOf

  /**
   ** Check that the first class argument is a subclass of the second class argument. If the first argument is unbound, bind it to
   ** the subclasses of the second argument (if any exist).
   */
  public boolean isSubClassOf(List<Argument> arguments) throws BuiltInException
  {
    return isSubClassOf(arguments, true);
  } // isSubClassOf

  /**
   ** Check that the first class argument is a direct superclass of the second class argument. If the first argument is unbound, bind it to
   ** the direct superclasses of the second argument (if any exist).
   */
  public boolean isDirectSuperClassOf(List<Argument> arguments) throws BuiltInException
  {
    return isSuperClassOf(arguments, false);
  } // isDirectSuperClassOf

  /**
   ** Check that the first class argument is a superclass of the second class argument. If the first argument is unbound, bind it to
   ** the superclasses of the second argument (if any exist).
   */
  public boolean isSuperClassOf(List<Argument> arguments) throws BuiltInException
  {
    return isSuperClassOf(arguments, true);
  } // isSuperClassOf

  private boolean isSuperClassOf(List<Argument> arguments, boolean transitive) throws BuiltInException
  {
    boolean superClassArgumentUnbound = false;
    String className;
    boolean result = false;

    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(2, arguments.size());

    superClassArgumentUnbound = SWRLBuiltInUtil.isUnboundArgument(0, arguments);
    className = SWRLBuiltInUtil.getArgumentAsAClassName(1, arguments);

    try {
      if (superClassArgumentUnbound) {
        List<OWLNamedClass> superClasses;
        if (transitive) superClasses = SWRLOWLUtil.getSuperClassesOf(getInvokingBridge().getOWLModel(), className);
        else superClasses = SWRLOWLUtil.getDirectSuperClassesOf(getInvokingBridge().getOWLModel(), className);
        if (!superClasses.isEmpty()) {
          MultiArgument multiArgument = new MultiArgument();
          for (OWLNamedClass superClass : superClasses) multiArgument.addArgument(new ClassInfo(superClass.getName()));
          arguments.set(0, multiArgument);
        result = !multiArgument.hasNoArguments();
        } // if
      } else {
        String superClassName = SWRLBuiltInUtil.getArgumentAsAClassName(0, arguments);
        if (transitive) result = SWRLOWLUtil.isSuperClassOf(getInvokingBridge().getOWLModel(), superClassName, className, true);
        else result = SWRLOWLUtil.isDirectSuperClassOf(getInvokingBridge().getOWLModel(), superClassName, className, true);
      } // if
    } catch (SWRLOWLUtilException e) {
      throw new BuiltInException(e.getMessage());
    } // try

    return result;
  } // isSuperClassOf

  private boolean isSubClassOf(List<Argument> arguments, boolean transitive) throws BuiltInException
  {
    boolean subClassArgumentUnbound = false;
    String className;
    boolean result = false;

    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(2, arguments.size());

    subClassArgumentUnbound = SWRLBuiltInUtil.isUnboundArgument(0, arguments);
    className = SWRLBuiltInUtil.getArgumentAsAClassName(1, arguments);

    try {
      if (subClassArgumentUnbound) {
        List<OWLNamedClass> subClasses;
        if (transitive) subClasses = SWRLOWLUtil.getSubClassesOf(getInvokingBridge().getOWLModel(), className);
        else subClasses = SWRLOWLUtil.getDirectSubClassesOf(getInvokingBridge().getOWLModel(), className);
        if (!subClasses.isEmpty()) {
          MultiArgument multiArgument = new MultiArgument();
          for (OWLNamedClass subClass : subClasses) multiArgument.addArgument(new ClassInfo(subClass.getName()));
          arguments.set(0, multiArgument);
        result = !multiArgument.hasNoArguments();
        } // if
      } else {
        String subClassName = SWRLBuiltInUtil.getArgumentAsAClassName(0, arguments);
        if (transitive) result = SWRLOWLUtil.isSubClassOf(getInvokingBridge().getOWLModel(), subClassName, className, true);
        else  result = SWRLOWLUtil.isDirectSubClassOf(getInvokingBridge().getOWLModel(), subClassName, className, true);
      } // if
    } catch (SWRLOWLUtilException e) {
      throw new BuiltInException(e.getMessage());
    } // try

    return result;
  } // isSubClassOf

  private boolean isSubPropertyOf(List<Argument> arguments, boolean transitive) throws BuiltInException
  {
    boolean subPropertyArgumentUnbound = false;
    String propertyName;
    boolean result = false;

    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(2, arguments.size());
    subPropertyArgumentUnbound = SWRLBuiltInUtil.isUnboundArgument(0, arguments);
    propertyName = SWRLBuiltInUtil.getArgumentAsAPropertyName(1, arguments);

    try {
      if (subPropertyArgumentUnbound) {
        List<OWLProperty> subProperties;
        if (transitive) subProperties = SWRLOWLUtil.getSubPropertiesOf(getInvokingBridge().getOWLModel(), propertyName);
        else subProperties = SWRLOWLUtil.getDirectSubPropertiesOf(getInvokingBridge().getOWLModel(), propertyName);
        if (!subProperties.isEmpty()) {
          MultiArgument multiArgument = new MultiArgument();
          for (OWLProperty subProperty : subProperties) multiArgument.addArgument(new PropertyInfo(subProperty.getName()));
          arguments.set(0, multiArgument);
          result = !multiArgument.hasNoArguments();
        } // if
      } else {
        String subPropertyName = SWRLBuiltInUtil.getArgumentAsAPropertyName(0, arguments);
        if (transitive) result = SWRLOWLUtil.isSubPropertyOf(getInvokingBridge().getOWLModel(), subPropertyName, propertyName, true);
        else result = SWRLOWLUtil.isDirectSubPropertyOf(getInvokingBridge().getOWLModel(), subPropertyName, propertyName, true);
      } // if
    } catch (SWRLOWLUtilException e) {
      throw new BuiltInException(e.getMessage());
    } // try

    return result;
  } // isSubPropertyOf

  private boolean isSuperPropertyOf(List<Argument> arguments, boolean transitive) throws BuiltInException
  {
    boolean superPropertyArgumentUnbound = false;
    String propertyName;
    boolean result = false;

    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(2, arguments.size());
    superPropertyArgumentUnbound = SWRLBuiltInUtil.isUnboundArgument(0, arguments);
    propertyName = SWRLBuiltInUtil.getArgumentAsAPropertyName(1, arguments);

    try {
      if (superPropertyArgumentUnbound) {
        List<OWLProperty> superProperties;
        if (transitive) superProperties = SWRLOWLUtil.getSuperPropertiesOf(getInvokingBridge().getOWLModel(), propertyName);
        else superProperties = SWRLOWLUtil.getDirectSuperPropertiesOf(getInvokingBridge().getOWLModel(), propertyName);
        if (!superProperties.isEmpty()) {
          MultiArgument multiArgument = new MultiArgument();
          for (OWLProperty superProperty : superProperties) multiArgument.addArgument(new PropertyInfo(superProperty.getName()));
          arguments.set(0, multiArgument);
          result = !multiArgument.hasNoArguments();
        } // if
      } else {
        String superPropertyName = SWRLBuiltInUtil.getArgumentAsAPropertyName(0, arguments);
        if (transitive) result = SWRLOWLUtil.isSuperPropertyOf(getInvokingBridge().getOWLModel(), superPropertyName, propertyName, true);
        else result = SWRLOWLUtil.isDirectSuperPropertyOf(getInvokingBridge().getOWLModel(), superPropertyName, propertyName, true);
      } // if
    } catch (SWRLOWLUtilException e) {
      throw new BuiltInException(e.getMessage());
    } // try

    return result;
  } // isSuperPropertyOf

} // SWRLBuiltInLibraryImpl
