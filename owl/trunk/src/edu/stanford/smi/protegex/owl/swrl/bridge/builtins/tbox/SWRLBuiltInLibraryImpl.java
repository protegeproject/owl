// TODO: a lot of repetition here
// cf. http://listserv.manchester.ac.uk/cgi-bin/wa?A2=ind0611&L=dig-wg&T=0&P=754
// lca, max flow, shortest path, http://www.ifi.unizh.ch/ddis/isparql.html
// icon to show OWL expressible SWRL rules.
// think about listener mechanism for assertion of new facts by rule engine

// How to extract axiom information:

// c = class
// i = individual
// d = datatype value
// p = property

// a = axiom
// ca = class axiom (composed of class descriptions)
// pa = property axiom
// ia = individual axiom
// da = datatype axiom

// r = restriction 

// Class descriptions: (six types: (1) URI, i.e., a c, (2) enumeration of individuals, (3) property restriction, (4) intersection, (5)
// union, (6) complement

// (1) URI, i.e., a named class
// (2) enumeration of class description
// 3.1.1 enumeration: isEnumerationClassDescription(?cd), onIndividual(?cd, ?i)

// (3) property restriction class descriptions
// 3.1.2 cardinality restrictions = isCardinalityRestriction(?a), isMinCardinalityRestriction(?a), isMaxCardinalityRestriction(?a), hasCardinality(?a, ?d)
// 3.1.2.1.1 allValuesFromRestriction: isAllValuesFromRestriction(?a), onProperty(?a, ?p), hasValue(?a, ?{i, d})
// 3.1.2.1.2 someValuesFrom: isSomeValuesFrom(?a), onProperty(?a, ?p), hasValue(?a, ?{i,d})
// 3.1.2.1.3 hasValueRestriction: isHasValueRestriction(?a), onProperty(?a, ?p), hasValue(?a, ?{i,d})

// (4) intersection of class description
// 3.1.3.1 intersectionOf: list of owl:oneOf class descriptions: isIntersectionOfClassDescription(?cd), onClassDescription(?cd, ?cd)

// (5) union of class description
// 3.1.3.2 unionOf: list of owl:oneOf class descriptions: isUnionOfClassDescription(?cd), onClassDescription(?cd, ?cd)

// (6) complement of class description
// 3.1.3.3 complementOf: isComplementOfClassDescription(?cd), onClassDescription(?cd, ?cd)

// Class axioms (which use cds)
// 3.2.1 rdfs:subClassOf: isSubClassOf(?c, ?c), isSubClassAxiom(?ca), ...
// 3.2.2 owl:equivalentClass: isEquivalentClass(?c, ?c), isEquivalentClassAxiom(?ca), onClassDescription(?ca, ?cd)
// 3.2.3 isOneOfClassDescription? how to distinguish from 3.1.1??
// 3.2.4 owl:isDisjointWith: isDisjointWith(?c, ?c), isDisjointWithClassAxiom(?ca), onClassDescription(?ca, ?cd)

// Property axioms:
// 4.1.1 rdfs:subPropertyOf: isSubPropertyOf(?p,?p), isDirectSubPropertyOf(?p, ?p)
// 4.1.2 rdfs:domain: isInDomainOf(?p, ?c), isInDirectDomainOf(?p, ?c), isInDomainOfAxiom(?pa), onClassDescription(?pa, ?cd)
// 4.1.3 rdfs:range: isInRangeOf(?p, ?c), isInDirectRangeOf(?p, ?c), isInRangeOfAxiom(?pa), onClassDescription(?pa, ?cd)
// 4.2.1 owl:equivalentProperty: isEquivalentProperty(?p, ?p), isEquivalentPropertyAxiom(?pa), onProperty(?pa, ?p)
// 4.2.2 owl:inverseOf: isInverseOf(?p, ?p), isInverseOfAxiom(?pa), onProperty(?pa, ?p)
// 4.3.1 owl:FunctionalProperty: isFunctionalProperty(?p), isFunctionalPropertyAxiom(?pa), onProperty(?pa, ?p)
// 4.3.2 owl:InverseFunctionalProperty: isInverseFunctionalProperty(?p), isInverseFunctionalPropertyAxiom(?pa), onProperty(?pa, ?p)
// 4.4.1 owl:TransitiveProperty: isTransitiveProperty(?p), isTransitivePropertyAxiom(?pa), onProperty(?pa, ?p)

// Individual axioms:
// 5.2.1 owl:sameAs: isSameAsAxiom(?ia), onIndividual(?ia, ?i)
// 5.2.2 owl:differentFrom: isDifferentFromAxiom(?ia), onIndividual(?ia, ?i)
// 5.2.3 owl:AllDifferent: isAllDifferentAxiom(?ia), onIndividual(?ia, ?i)
//
// Datatype axioms:
// 6.2 Enumerated datatype: isEnumeratedDatatypeAxiom(?da), onValue(?da, ?d)

package edu.stanford.smi.protegex.owl.swrl.bridge.builtins.tbox;

import edu.stanford.smi.protegex.owl.swrl.model.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.builtins.*;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.*;

import edu.stanford.smi.protegex.owl.swrl.util.SWRLOWLUtil;
import edu.stanford.smi.protegex.owl.swrl.exceptions.SWRLOWLUtilException;

import edu.stanford.smi.protegex.owl.model.impl.OWLUtil;

import java.util.*;

/**
 ** Implementations library for SWRL TBox built-in methods. See <a
 ** href="http://protege.cim3.net/cgi-bin/wiki.pl?SWRLTBoxBuiltIns">here</a> for documentation on this library.
 **
 ** See <a href="http://protege.cim3.net/cgi-bin/wiki.pl?SWRLBuiltInBridge">here</a> for documentation on defining SWRL built-in libraries.
 */
public class SWRLBuiltInLibraryImpl extends AbstractSWRLBuiltInLibrary
{
  private static String SWRLTBoxLibraryName = "SWRLTBoxBuiltIns";

  private static String SWRLTBoxPrefix = "tbox:";

  private ArgumentFactory argumentFactory;

  public SWRLBuiltInLibraryImpl() 
  { 
    super(SWRLTBoxLibraryName); 

    argumentFactory = ArgumentFactory.getFactory();
  } // SWRLBuiltInLibraryImpl

  public void reset() {}

  /**
   ** Is the second annotation property argument associated with the first ontology, class, property or individual argument. If the second
   ** argument is unbound, bind it to the annotation properties associated with the first argument (if any exist).
   */
  public boolean hasAnnotation(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } // hasAnnotation

  /**
   ** It the second integer argument equal to the cardinality specified by the first owl:Cardinality, owl:MaxCardinality, or
   ** owl:MinCardinality restriction argument. If the second argument is unbound, bind it to the cardinality value.
   */
  public boolean hasCardinality(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } // hasCardinality

  /**
   ** Is the second string argument the value specified by the first rdfs:comment annotation property argument. If the second argument is
   ** unbound, bind it to the value specified by the first rdfs:comment annotation property argument.
   */
  public boolean hasComment(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } // hasComment

  /**
   ** Is the second description argument associated with the first class or individual argument. If the second argument is unbound, bind it
   ** to the descriptions associated with the first argument (if any exist).
   */
  public boolean hasDescription(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } // hasDescription

  /**
   ** Is the second resource argument the value specified by the first rdfs:isDefinedBy annotation property argument. If the second argument
   ** is unbound, bind it to the value specified by the first rdfs:isDefinedBy annotation property argument.
   */
  public boolean hasIsDefinedBy(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } // hasIsDefinedBy

  /**
   ** Is the second string argument the value specified by the first rdfs:label annotation property argument. If the second argument is
   ** unbound, bind it to the value specified by the first rdfs:label annotation property argument.
   */
  public boolean hasLabel(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } // hasLabel

  /**
   ** Is the second resource argument the value specified by the first rdfs:seeAlso annotation property argument. If the second argument
   ** is unbound, bind it to the value specified by the first rdfs:seeAlso annotation property argument.
   */
  public boolean hasSeeAlso(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } // hasSeeAlso

  /**
   ** Is the second string argument the value specified by the first rdfs:versionInfo annotation property argument. If the second argument
   ** is unbound, bind it to the value specified by the first rdfs:versionInfo annotation property argument.
   */
  public boolean hasVersionInfo(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } // hasVersionInfo

  /**
   ** Are all individual arguments declared to be the different from each other.
   */
  public boolean isAllDifferents(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } // isAllDifferents

  /**
   ** Is the single argument an owl:AllDifferentsAxiom.
   */
  public boolean isAllDifferentsAxiom(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } // isAllDifferents

  /**
   ** Is the single argument an owl:AllValuesFrom restriction.
   */
  public boolean isAllValuesFromRestriction(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } // isAllValuesFromRestriction

  /**
   ** Is the single argument an annotation property.
   */
  public boolean isAnnotation(List<BuiltInArgument> arguments) throws BuiltInException
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
  } // isAnnotation

  /**
   ** Is the single argument an owl:Cardinality restriction.
   */
  public boolean isCardinalityRestriction(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } // isCardinalityRestriction

  /**
   ** Determine if a single argument is an OWL named class. If the argument is unbound, bind it to all OWL named classes in an ontology.
   */
  public boolean isClass(List<BuiltInArgument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(1, arguments.size());
    boolean isUnboundArgument = SWRLBuiltInUtil.isUnboundArgument(0, arguments);   
    boolean result = false;

    try {
      if (getIsInConsequent()) {
        if (SWRLBuiltInUtil.isArgumentAString(0, arguments)) {
          String className = SWRLOWLUtil.getFullName(getInvokingBridge().getOWLModel(), SWRLBuiltInUtil.getArgumentAsAString(0, arguments));
          if (!getInvokingBridge().isClass(className)) getInvokingBridge().createOWLClass(className);
        } else SWRLBuiltInUtil.checkThatArgumentIsAClass(0, arguments);
        result = true;
      } else {
        if (isUnboundArgument) {
          MultiArgument multiArgument = argumentFactory.createMultiArgument(SWRLBuiltInUtil.getVariableName(0, arguments), SWRLBuiltInUtil.getPrefixedVariableName(0, arguments));
          for (edu.stanford.smi.protegex.owl.model.OWLNamedClass cls : SWRLOWLUtil.getUserDefinedOWLNamedClasses(getInvokingBridge().getOWLModel()))
            multiArgument.addArgument(argumentFactory.createClassArgument(cls.getName()));
          arguments.set(0, multiArgument);
          result = !multiArgument.hasNoArguments();
        } else {
          String className = SWRLBuiltInUtil.getArgumentAsAClassName(0, arguments);
          result = SWRLOWLUtil.isClass(getInvokingBridge().getOWLModel(), className, false);
        } // if
      } // if
    } catch (SWRLOWLUtilException e) {
      throw new BuiltInException(e.getMessage());
    } catch (SWRLRuleEngineBridgeException e) {
      throw new BuiltInException(e.getMessage());
    } // try

    return result;
  } // isClass

  /**
   ** Is the single argument an OWL class description.
   */
  public boolean isClassDescription(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result = false;
    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } // isClassDescription

  /**
   ** Is the single argument an rdfs:comment annotation.
   */
  public boolean isCommentAnnotation(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } // isCommentAnnotation

  /**
   ** Is the first class argument the complement of the second class argument. If the second argument is unbound, bind it to the complement
   ** of the first argument.
   */
  public boolean isComplementOf(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result = false;
    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } // isComplementOf

  /**
   ** Is the single argument an owl:ComplementOf class description.
   */
  public boolean isComplementOfClassDescription(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result = false;
    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } // isComplementOfClassDescription

  /**
   ** Determine if the single argument is an OWL datatype property. If the argument is unbound, bind it to all OWL datatype
   ** properties in an ontology.
   */
  public boolean isDatatypeProperty(List<BuiltInArgument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(1, arguments.size());
    boolean isUnboundArgument = SWRLBuiltInUtil.isUnboundArgument(0, arguments);   
    boolean result = false;

    try {
      if (isUnboundArgument) {
        MultiArgument multiArgument = argumentFactory.createMultiArgument(SWRLBuiltInUtil.getVariableName(0, arguments), SWRLBuiltInUtil.getPrefixedVariableName(0, arguments));
        for (edu.stanford.smi.protegex.owl.model.OWLProperty property : SWRLOWLUtil.getUserDefinedOWLDatatypeProperties(getInvokingBridge().getOWLModel())) {
          if (property.isObjectProperty()) multiArgument.addArgument(argumentFactory.createObjectPropertyArgument(property.getName()));
          else multiArgument.addArgument(argumentFactory.createDatatypePropertyArgument(property.getName()));
        } // for
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
   ** Is the single argument an rdfs:isDefinedBy annotation.
   */
  public boolean isDefinedBy(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } // isDefinedBy

  /**
   ** Is the single argument an owl:DifferentFrom axiom.
   */
  public boolean isDifferentFromAxiom(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } // isDifferentFromAxiom

  /**
   ** Check that the second class argument is a direct subclass of the first class argument. If the second argument is unbound, bind it to
   ** the direct subclasses of the first argument (if any exist).
   */
  public boolean isDirectSubClassOf(List<BuiltInArgument> arguments) throws BuiltInException
  {
    return isSubClassOf(arguments, false);
  } // isDirectSubClassOf

  /**
   ** Determine if the second property argument is a direct subproperty of the first property argument. If the second argument is unbound,
   ** bind it to the direct sub properties of the first argument (if any exist).
   */
  public boolean isDirectSubPropertyOf(List<BuiltInArgument> arguments) throws BuiltInException
  {
    return isSubPropertyOf(arguments, false);
  } // isDirectSubPropertyOf

  /**
   ** Check that the second class argument is a direct superclass of the first class argument. If the second argument is unbound, bind it to
   ** the direct superclasses of the first argument (if any exist).
   */
  public boolean isDirectSuperClassOf(List<BuiltInArgument> arguments) throws BuiltInException
  {
    return isSuperClassOf(arguments, false);
  } // isDirectSuperClassOf

  /**
   ** Determine if the second property argument is a direct superproperty of the first property argument. If the second argument is unbound,
   ** bind it to the direct super properties of the first argument (if any exist).
   */
  public boolean isDirectSuperPropertyOf(List<BuiltInArgument> arguments) throws BuiltInException
  {
    return isSuperPropertyOf(arguments, false);
  } // isDirectSuperPropertyOf

  /**
   ** Determine if the two class arguments represent classes or properties that are disjoint with each other. If the second argument is
   ** unbound, bind it to the disjoint classes of the first argument (if any exist).
   */
  public boolean isDisjointWith(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } // isDisjointWith

  /**
   ** Is the single argument an owl:DisjointWith axiom.
   */
  public boolean isDisjointWithAxiom(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } // isDisjointWithAxiom

  /**
   ** Is the single argument an enumerated datatype axiom.
   */
  public boolean isEnumeratedDatatypeAxiom(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } // isEnumeratedDatatypeAxiom

  /**
   ** Determine if the two class arguments are equivalent to each other. If the second
   ** argument is unbound, bind it to the equivalent classes of the first argument (if any exist).
   */
  public boolean isEquivalentClass(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } // isEquivalentClass

  /**
   ** Is the single argument an owl:EquivalentClass axiom.
   */
  public boolean isEquivalentClassAxiom(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } // isEquivalentClassAxiom

  /**
   ** Determine if the two property arguments are equivalent to each other. If the second argument is unbound, bind it to the equivalent
   ** properties of the first argument (if any exist).
   */
  public boolean isEquivalentProperty(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } // isEquivalentProperty

  /**
   ** Is the single argument an owl:EquivalentProperty axiom.
   */
  public boolean isEquivalentPropertyAxiom(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } // isEquivalentPropertyAxiom

  /**
   ** Determine if a single property argument is functional.
   */
  public boolean isFunctionalProperty(List<BuiltInArgument> arguments) throws BuiltInException
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
   ** Is the single argument an owl:functionalProperty axiom.
   */
  public boolean isFunctionalPropertyAxiom(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } // isFunctionalPropertyAxiom

  /**
   ** Is the single argument an owl:hasValue restriction
   */
  public boolean isHasValueRestriction(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } // isHasValueRestriction

  /**
   ** Check that the first class argument is in the domain of the second property argument (excluding its superproperties). If the first
   ** argument is unbound and the second argument is bound, bind the first argument to the domain(s) of the second property argument (if any
   ** exist). If the first class argument is bound and the second argument is unbound, bind the second argument to the properties that have
   ** the class in their domain (if any). An error is thrown if both arguments are unbound.
   */
  public boolean isInDirectDomainOf(List<BuiltInArgument> arguments) throws BuiltInException
  {
    return isInDomainOf(arguments, false);
  } // isInDirectDomainOf

  /**
   ** Check that the first class argument is in the range of the second property argument excluding its superproperties. If the first
   ** argument is unbound, bind it to the range of the second argument (if any exist).
   */
  public boolean isInDirectRangeOf(List<BuiltInArgument> arguments) throws BuiltInException
  {
    return isInRangeOf(arguments, false);
  } // isInDirectRangeOf

  /**
   ** Check that the first class argument is in the domain of the second property argument (including its superproperties). If the first
   ** argument is unbound and the second argument is bound, bind the first argument to the domain(s) of the second property argument (if any
   ** exist). If the first class argument is bound and the second argument is unbound, bind the second argument to the properties that have
   ** the class in their domain (if any). An error is thrown if both arguments are unbound.
   */
  public boolean isInDomainOf(List<BuiltInArgument> arguments) throws BuiltInException
  {
    return isInDomainOf(arguments, true);
  } // isInDomainOf

  /**
   ** Is the single argument an OWL domain axiom.
   */
  public boolean isInDomainOfAxiom(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } // isInDomainOfAxiom

  /**
   ** Check that the first class argument is in the range of the second property argument (including its superproperties). If the first
   ** argument is unbound, bind it to the range of the second argument (if any exist).
   */
  public boolean isInRangeOf(List<BuiltInArgument> arguments) throws BuiltInException
  {
    return isInRangeOf(arguments, true);
  } // isInRangeOf


  /**
   ** Is the single argument an OWL range axiom.
   */
  public boolean isInRangeOfAxiom(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } // isInRangeOfAxiom

  /**
   ** Is the single argument an owl:InteresctionOf class description.
   */
  public boolean isIntersectionOfDescription(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } // isIntersectionOfDescription

  /**
   ** Is the single argument an owl:InverseFunctionalProperty axiom.
   */
  public boolean isInverseFunctionalPropertyAxiom(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } // isInverseFunctionalPropertyAxiom

  /**
   ** Determine if the second property argument is the inverse of the first property argument.
   */
  public boolean isInverseOf(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } // isInverseOf

  /**
   ** Determine if a single property argument is inverse functional.
   */
  public boolean isInverseFunctionalProperty(List<BuiltInArgument> arguments) throws BuiltInException
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
   ** Is the single argument an owl:inverseOf axiom.
   */
  public boolean isInverseOfAxiom(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } // isInverseOfAxiom

  /**
   ** Is the single argument an rdfs:label annotation.
   */
  public boolean isLabelAnnotation(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } // isLabelAnnotation

  /**
   ** Is the single argument an owl:MaxCardinality restriction.
   */
  public boolean isMaxCardinalityRestriction(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } // isMaxCardinalityRestriction

  /**
   ** Is the single argument an owl:MinCardinality restriction.
   */
  public boolean isMinCardinalityRestriction(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } // isMinCardinalityRestriction

  /**
   ** Determine if a single argument is an OWL object property. If the argument is unbound, bind it to all OWL object properties in an
   ** ontology.
   */
  public boolean isObjectProperty(List<BuiltInArgument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(1, arguments.size());
    boolean isUnboundArgument = SWRLBuiltInUtil.isUnboundArgument(0, arguments);   
    boolean result = false;

    try {
      if (isUnboundArgument) {
        MultiArgument multiArgument = argumentFactory.createMultiArgument(SWRLBuiltInUtil.getVariableName(0, arguments), SWRLBuiltInUtil.getPrefixedVariableName(0, arguments));
        for (edu.stanford.smi.protegex.owl.model.OWLProperty property : SWRLOWLUtil.getUserDefinedOWLObjectProperties(getInvokingBridge().getOWLModel())) {
          if (property.isObjectProperty()) multiArgument.addArgument(argumentFactory.createObjectPropertyArgument(property.getName()));
          else multiArgument.addArgument(argumentFactory.createDatatypePropertyArgument(property.getName()));
        } // for
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
   ** Is the single argument an owl:oneOf class description.
   */
  public boolean isOneOfClassDescription(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } // isOneOfClassDescription

  /**
   ** Is the single argument an owl:Ontology resource. If the argument is unbound, bind it to the current ontology.
   */
  public boolean isOntology(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } // isOntology

  /**
   ** Determine if a single property argument is an OWL property. If the argument is unbound, bind it to all OWL properties in an ontology.
   */
  public boolean isProperty(List<BuiltInArgument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(1, arguments.size());
    boolean isUnboundArgument = SWRLBuiltInUtil.isUnboundArgument(0, arguments);   
    boolean result = false;

    try {
      if (isUnboundArgument) {
        MultiArgument multiArgument = argumentFactory.createMultiArgument(SWRLBuiltInUtil.getVariableName(0, arguments), SWRLBuiltInUtil.getPrefixedVariableName(0, arguments));
        for (edu.stanford.smi.protegex.owl.model.OWLProperty property : SWRLOWLUtil.getUserDefinedOWLProperties(getInvokingBridge().getOWLModel())) {
          if (property.isObjectProperty()) multiArgument.addArgument(argumentFactory.createObjectPropertyArgument(property.getName()));
          else multiArgument.addArgument(argumentFactory.createDatatypePropertyArgument(property.getName()));
        } // for
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
   ** Is the single argument an rdfs:seeAlso annotation.
   */
  public boolean isSeeAlsoAnnotation(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } // isSeeAlsoAnnotation

  /**
   ** Is the single argument an owl:SameAs axiom.
   */
  public boolean isSameAsAxiom(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result = false;
    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } // isSameAsAxiom

  /**
   ** Check that the first class argument is a subclass of the second class argument. If the first argument is unbound, bind it to
   ** the subclasses of the second argument (if any exist).
   */
  public boolean isSubClassOf(List<BuiltInArgument> arguments) throws BuiltInException
  {
    return isSubClassOf(arguments, true);
  } // isSubClassOf

  /**
   ** Determine if the first property argument is a subproperty of the second property argument. If the first argument is unbound,
   ** bind it to the sub properties of the second argument (if any exist).
   */
  public boolean isSubPropertyOf(List<BuiltInArgument> arguments) throws BuiltInException
  {
    return isSubPropertyOf(arguments, true);
  } // isSubPropertyOf

  /**
   ** Check that the first class argument is a superclass of the second class argument. If the first argument is unbound, bind it to
   ** the superclasses of the second argument (if any exist).
   */
  public boolean isSuperClassOf(List<BuiltInArgument> arguments) throws BuiltInException
  {
    return isSuperClassOf(arguments, true);
  } // isSuperClassOf

  /**
   ** Determine if the first property argument is a superproperty of the second property argument. If the first argument is unbound,
   ** bind it to the super properties of the second argument (if any exist).
   */
  public boolean isSuperPropertyOf(List<BuiltInArgument> arguments) throws BuiltInException
  {
    return isSuperPropertyOf(arguments, true);
  } // isSuperPropertyOf

  /**
   ** Determine if a single property argument is symmetric.
   */
  public boolean isSymmetricProperty(List<BuiltInArgument> arguments) throws BuiltInException
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
   ** Is the single argument an owl:SymmetricProperty axiom.
   */
  public boolean isSymmetricPropertyAxiom(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } // isSymmetricPropertyAxiom

  /**
   ** Determine if a single property argument is transitive.
   */
  public boolean isTransitiveProperty(List<BuiltInArgument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(1, arguments.size());
    String propertyName = SWRLBuiltInUtil.getArgumentAsAPropertyName(0, arguments);

    boolean result = false;
    try {
      result = SWRLOWLUtil.isTransitiveProperty(getInvokingBridge().getOWLModel(), propertyName);
    } catch (SWRLOWLUtilException e) {
      throw new BuiltInException(e.getMessage());
    } // try

    return result;
  } // isTransitiveProperty

  /**
   ** Is the single argument an owl:TransitiveProperty axiom.
   */
  public boolean isTransitivePropertyAxiom(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } // isTransitivePropertyAxiom

  public boolean isRestriction(List<BuiltInArgument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(1, arguments.size());
    String className = SWRLBuiltInUtil.getArgumentAsAClassName(0, arguments);
    boolean result = false;

    try {
      result = SWRLOWLUtil.isAnonymousResourceName(getInvokingBridge().getOWLModel(), className);
    } catch (SWRLOWLUtilException e) {
      throw new BuiltInException(e.getMessage());
    } // try

    return result;
  } // isRestriction
    
  /**
   ** Is the single argument an owl:UnionOf class description.
   */
  public boolean isUnionOfDescription(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } // isUnionOfDescription

  /**
   ** Is the single argument an owl:versionInfo annotation.
   */
  public boolean isVersionInfoAnnotation(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } // isVersionInfoAnnotation

  /**
   ** It the second class description argument a subject of the first axiom argument. If the second argument is unbound, bind it to the the
   ** axiom's subject(s).
   */
  public boolean onClassDescription(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } // onClassDescription

  /**
   ** It the second individual argument a subject of the first individual axiom argument. If the second argument is unbound, bind it to the
   ** individual axiom's subject(s).
   */
  public boolean onIndividual(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } // onIndividual

  /**
   ** It the second property argument a subject of the first property axiom argument. If the second argument is unbound, bind it to the
   ** property axiom's subject(s).
   */
  public boolean isSomeValuesFrom(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result = false;
    String restrictionName, onPropertyName, onClassName;

    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(3, arguments.size());
    restrictionName  = SWRLBuiltInUtil.getArgumentAsAClassName(0, arguments);
    onPropertyName = SWRLBuiltInUtil.getArgumentAsAPropertyName(1, arguments);

    try {
      if (getIsInConsequent()) {
        OWLSomeValuesFrom someValuesFrom;
        OWLProperty onProperty;

        if (SWRLBuiltInUtil.isArgumentAString(2, arguments)) {
          onClassName = SWRLOWLUtil.getFullName(getInvokingBridge().getOWLModel(), SWRLBuiltInUtil.getArgumentAsAString(2, arguments));
          if (!getInvokingBridge().isClass(onClassName)) getInvokingBridge().createOWLClass(onClassName);
        } else onClassName = SWRLBuiltInUtil.getArgumentAsAClassName(2, arguments);

        System.err.println("isSomeValuesFrom: restrictionName: " + restrictionName + ", propertyName: " + onPropertyName + ", className: " + onClassName);

        if (SWRLOWLUtil.isObjectProperty(getInvokingBridge().getOWLModel(), onPropertyName)) onProperty = OWLFactory.createOWLObjectProperty(onPropertyName);
        else onProperty = OWLFactory.createOWLDatatypeProperty(onPropertyName);

        someValuesFrom = OWLFactory.createOWLSomeValuesFrom(OWLFactory.createOWLClass(restrictionName), onProperty, OWLFactory.createOWLClass(onClassName));

        getInvokingBridge().createOWLRestriction(someValuesFrom);
      } else {
        onClassName = SWRLBuiltInUtil.getArgumentAsAClassName(2, arguments);
        throw new BuiltInNotImplementedException();
      } // if
    } catch (OWLFactoryException e) {
      throw new BuiltInException(e.getMessage());
    } catch (SWRLOWLUtilException e) {
      throw new BuiltInException(e.getMessage());
    } catch (SWRLRuleEngineBridgeException e) {
      throw new BuiltInException(e.getMessage());
    } // try

    return result;
  } // onProperty

  /**
   ** It the second datatype value argument a subject of the first datatype axiom argument. If the second argument is unbound, bind it to the
   ** data value axiom's subject(s).
   */
  public boolean onValue(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } // onValue

  /**
   ** Check that the two class or property arguments refer to the same underlying entity.
   */
  public boolean equalTo(List<BuiltInArgument> arguments) throws BuiltInException
  {
    throw new BuiltInNotImplementedException();
  } // equalTo

  /**
   ** Check that the two class or property arguments do not refer to the same underlying entity.
   */
  public boolean notEqualTo(List<BuiltInArgument> arguments) throws BuiltInException
  {
    throw new BuiltInNotImplementedException();
  } // notEqualTo

  private boolean isSuperClassOf(List<BuiltInArgument> arguments, boolean transitive) throws BuiltInException
  {
    boolean superClassArgumentUnbound = false;
    String className;
    boolean result = false;

    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(2, arguments.size());

    superClassArgumentUnbound = SWRLBuiltInUtil.isUnboundArgument(0, arguments);
    className = SWRLBuiltInUtil.getArgumentAsAClassName(1, arguments);

    try {
      if (superClassArgumentUnbound) {
        List<edu.stanford.smi.protegex.owl.model.OWLNamedClass> superClasses;
        if (transitive) superClasses = SWRLOWLUtil.getSuperClassesOf(getInvokingBridge().getOWLModel(), className);
        else superClasses = SWRLOWLUtil.getDirectSuperClassesOf(getInvokingBridge().getOWLModel(), className);
        if (!superClasses.isEmpty()) {
          MultiArgument multiArgument = argumentFactory.createMultiArgument(SWRLBuiltInUtil.getVariableName(0, arguments), SWRLBuiltInUtil.getPrefixedVariableName(0, arguments));
          for (edu.stanford.smi.protegex.owl.model.OWLNamedClass superClass : superClasses) multiArgument.addArgument(argumentFactory.createClassArgument(superClass.getName()));
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

  private boolean isSubClassOf(List<BuiltInArgument> arguments, boolean transitive) throws BuiltInException
  {
    boolean subClassArgumentUnbound = false;
    String className;
    boolean result = false;

    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(2, arguments.size());

    subClassArgumentUnbound = SWRLBuiltInUtil.isUnboundArgument(0, arguments);

    try {
      if (getIsInConsequent()) {
        String superclassName;

        if (SWRLBuiltInUtil.isArgumentAString(1, arguments)) {
          superclassName = SWRLOWLUtil.getFullName(getInvokingBridge().getOWLModel(), SWRLBuiltInUtil.getArgumentAsAString(1, arguments));
          if (!getInvokingBridge().isClass(superclassName)) getInvokingBridge().createOWLClass(superclassName);
        } else superclassName = SWRLBuiltInUtil.getArgumentAsAClassName(1, arguments);

        if (SWRLBuiltInUtil.isArgumentAString(0, arguments)) 
          className = SWRLOWLUtil.getFullName(getInvokingBridge().getOWLModel(), SWRLBuiltInUtil.getArgumentAsAString(0, arguments));
        else className = SWRLBuiltInUtil.getArgumentAsAClassName(0, arguments);

        if (!getInvokingBridge().isClass(className)) getInvokingBridge().createOWLClass(className, superclassName);
        result = true;
      } else {
        className = SWRLBuiltInUtil.getArgumentAsAClassName(1, arguments);

        if (subClassArgumentUnbound) {
          List<edu.stanford.smi.protegex.owl.model.OWLNamedClass> subClasses;
          if (transitive) subClasses = SWRLOWLUtil.getSubClassesOf(getInvokingBridge().getOWLModel(), className);
          else subClasses = SWRLOWLUtil.getDirectSubClassesOf(getInvokingBridge().getOWLModel(), className);
          if (!subClasses.isEmpty()) {
            MultiArgument multiArgument = argumentFactory.createMultiArgument(SWRLBuiltInUtil.getVariableName(0, arguments), SWRLBuiltInUtil.getPrefixedVariableName(0, arguments));
            for (edu.stanford.smi.protegex.owl.model.OWLNamedClass subClass : subClasses) multiArgument.addArgument(argumentFactory.createClassArgument(subClass.getName()));
            arguments.set(0, multiArgument);
            result = !multiArgument.hasNoArguments();
          } // if
        } else {
          String subClassName = SWRLBuiltInUtil.getArgumentAsAClassName(0, arguments);
          if (transitive) result = SWRLOWLUtil.isSubClassOf(getInvokingBridge().getOWLModel(), subClassName, className, true);
          else  result = SWRLOWLUtil.isDirectSubClassOf(getInvokingBridge().getOWLModel(), subClassName, className, true);
        } // if
      } // if
    } catch (SWRLOWLUtilException e) {
      throw new BuiltInException(e.getMessage());
    } catch (SWRLRuleEngineBridgeException e) {
      throw new BuiltInException(e.getMessage());
    } // try

    return result;
  } // isSubClassOf

  private boolean isSubPropertyOf(List<BuiltInArgument> arguments, boolean transitive) throws BuiltInException
  {
    boolean subPropertyArgumentUnbound = false;
    String propertyName;
    boolean result = false;

    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(2, arguments.size());
    subPropertyArgumentUnbound = SWRLBuiltInUtil.isUnboundArgument(0, arguments);
    propertyName = SWRLBuiltInUtil.getArgumentAsAPropertyName(1, arguments);

    try {
      if (subPropertyArgumentUnbound) {
        List<edu.stanford.smi.protegex.owl.model.OWLProperty> subProperties;
        if (transitive) subProperties = SWRLOWLUtil.getSubPropertiesOf(getInvokingBridge().getOWLModel(), propertyName);
        else subProperties = SWRLOWLUtil.getDirectSubPropertiesOf(getInvokingBridge().getOWLModel(), propertyName);
        if (!subProperties.isEmpty()) {
          MultiArgument multiArgument = argumentFactory.createMultiArgument(SWRLBuiltInUtil.getVariableName(0, arguments), SWRLBuiltInUtil.getPrefixedVariableName(0, arguments));
          for (edu.stanford.smi.protegex.owl.model.OWLProperty subProperty : subProperties) {
            if (subProperty.isObjectProperty()) multiArgument.addArgument(argumentFactory.createObjectPropertyArgument(subProperty.getName()));
            else multiArgument.addArgument(argumentFactory.createDatatypePropertyArgument(subProperty.getName()));
          } // for
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

  private boolean isSuperPropertyOf(List<BuiltInArgument> arguments, boolean transitive) throws BuiltInException
  {
    boolean superPropertyArgumentUnbound = false;
    String propertyName;
    boolean result = false;

    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(2, arguments.size());
    superPropertyArgumentUnbound = SWRLBuiltInUtil.isUnboundArgument(0, arguments);
    propertyName = SWRLBuiltInUtil.getArgumentAsAPropertyName(1, arguments);

    try {
      if (superPropertyArgumentUnbound) {
        List<edu.stanford.smi.protegex.owl.model.OWLProperty> superProperties;
        if (transitive) superProperties = SWRLOWLUtil.getSuperPropertiesOf(getInvokingBridge().getOWLModel(), propertyName);
        else superProperties = SWRLOWLUtil.getDirectSuperPropertiesOf(getInvokingBridge().getOWLModel(), propertyName);
        if (!superProperties.isEmpty()) {
          MultiArgument multiArgument = argumentFactory.createMultiArgument(SWRLBuiltInUtil.getVariableName(0, arguments), SWRLBuiltInUtil.getPrefixedVariableName(0, arguments));
          for (edu.stanford.smi.protegex.owl.model.OWLProperty superProperty : superProperties) {
            if (superProperty.isObjectProperty()) multiArgument.addArgument(argumentFactory.createObjectPropertyArgument(superProperty.getName()));
            else multiArgument.addArgument(argumentFactory.createDatatypePropertyArgument(superProperty.getName()));
          } // for
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

  private boolean isInDomainOf(List<BuiltInArgument> arguments, boolean includingSuperproperties) throws BuiltInException
  {
    boolean domainClassArgumentUnbound, propertyArgumentUnbound = false;
    String propertyName, domainClassName;
    boolean result = false;

    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(2, arguments.size());

    domainClassArgumentUnbound = SWRLBuiltInUtil.isUnboundArgument(0, arguments);
    propertyArgumentUnbound = SWRLBuiltInUtil.isUnboundArgument(1, arguments);

    if (domainClassArgumentUnbound && propertyArgumentUnbound) throw new BuiltInException("at least one argument must be bound");

    try {
      if (domainClassArgumentUnbound) {
        Set<edu.stanford.smi.protegex.owl.model.OWLNamedClass> domainClasses;
        propertyName = SWRLBuiltInUtil.getArgumentAsAPropertyName(1, arguments);
        if (includingSuperproperties) domainClasses = SWRLOWLUtil.getDomainClasses(getInvokingBridge().getOWLModel(), propertyName);
        else domainClasses = SWRLOWLUtil.getDirectDomainClasses(getInvokingBridge().getOWLModel(), propertyName);
        if (!domainClasses.isEmpty()) {
          MultiArgument multiArgument = argumentFactory.createMultiArgument(SWRLBuiltInUtil.getVariableName(0, arguments), SWRLBuiltInUtil.getPrefixedVariableName(0, arguments));
          for (edu.stanford.smi.protegex.owl.model.OWLNamedClass domainClass : domainClasses) multiArgument.addArgument(argumentFactory.createClassArgument(domainClass.getName()));
          arguments.set(0, multiArgument);
        result = !multiArgument.hasNoArguments();
        } // if
      } else if (propertyArgumentUnbound) {
        Set<edu.stanford.smi.protegex.owl.model.OWLProperty> domainProperties;
        domainClassName = SWRLBuiltInUtil.getArgumentAsAClassName(0, arguments);
        domainProperties = SWRLOWLUtil.getDomainProperties(getInvokingBridge().getOWLModel(), domainClassName, includingSuperproperties);
        if (!domainProperties.isEmpty()) {
          MultiArgument multiArgument = argumentFactory.createMultiArgument(SWRLBuiltInUtil.getVariableName(1, arguments), SWRLBuiltInUtil.getPrefixedVariableName(1, arguments));
          for (edu.stanford.smi.protegex.owl.model.OWLProperty property : domainProperties) {
            if (property.isObjectProperty()) multiArgument.addArgument(argumentFactory.createObjectPropertyArgument(property.getName()));
            else multiArgument.addArgument(argumentFactory.createDatatypePropertyArgument(property.getName()));
          } // for
          arguments.set(1, multiArgument);
        result = !multiArgument.hasNoArguments();
        } // if
      } else { // Both arguments bound
        domainClassName = SWRLBuiltInUtil.getArgumentAsAClassName(0, arguments);
        propertyName = SWRLBuiltInUtil.getArgumentAsAPropertyName(1, arguments);
        if (includingSuperproperties) 
          result = SWRLOWLUtil.isInPropertyDomain(getInvokingBridge().getOWLModel(), propertyName, domainClassName, true);
        else 
          result = SWRLOWLUtil.isInDirectPropertyDomain(getInvokingBridge().getOWLModel(), propertyName, domainClassName, true);
      } // if
    } catch (SWRLOWLUtilException e) {
      throw new BuiltInException(e.getMessage());
    } // try

    return result;
  } // isInDomainOf

  private boolean isInRangeOf(List<BuiltInArgument> arguments, boolean includingSuperproperties) throws BuiltInException
  {
    boolean rangeClassArgumentUnbound = false;
    String rangeClassName, propertyName;
    boolean result = false;

    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(2, arguments.size());

    rangeClassArgumentUnbound = SWRLBuiltInUtil.isUnboundArgument(0, arguments);
    propertyName = SWRLBuiltInUtil.getArgumentAsAPropertyName(1, arguments);

    try {
      if (rangeClassArgumentUnbound) {
        Set<edu.stanford.smi.protegex.owl.model.OWLNamedClass> rangeClasses;
        if (includingSuperproperties) rangeClasses = SWRLOWLUtil.getRangeClasses(getInvokingBridge().getOWLModel(), propertyName);
        else rangeClasses = SWRLOWLUtil.getDirectRangeClasses(getInvokingBridge().getOWLModel(), propertyName);
        if (!rangeClasses.isEmpty()) {
          MultiArgument multiArgument = argumentFactory.createMultiArgument(SWRLBuiltInUtil.getVariableName(0, arguments), SWRLBuiltInUtil.getPrefixedVariableName(0, arguments));
          for (edu.stanford.smi.protegex.owl.model.OWLNamedClass rangeClass : rangeClasses) 
            multiArgument.addArgument(argumentFactory.createClassArgument(rangeClass.getName()));
          arguments.set(0, multiArgument);
          result = !multiArgument.hasNoArguments();
        } // if
      } else {
        propertyName = SWRLBuiltInUtil.getArgumentAsAPropertyName(1, arguments);
        rangeClassName = SWRLBuiltInUtil.getArgumentAsAClassName(0, arguments);
        if (includingSuperproperties) result = SWRLOWLUtil.isInPropertyRange(getInvokingBridge().getOWLModel(), propertyName, rangeClassName, true);
        else result = SWRLOWLUtil.isInDirectPropertyRange(getInvokingBridge().getOWLModel(), propertyName, rangeClassName, true);
      } // if
    } catch (SWRLOWLUtilException e) {
      throw new BuiltInException(e.getMessage());
    } // try

    return result;
  } // isInRangeOf

} // SWRLBuiltInLibraryImpl
