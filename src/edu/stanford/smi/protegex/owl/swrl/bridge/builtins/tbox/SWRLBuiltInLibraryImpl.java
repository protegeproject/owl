// TODO: a lot of repetition here
// cf. http://listserv.manchester.ac.uk/cgi-bin/wa?A2=ind0611&L=dig-wg&T=0&P=754
// lca, max flow, shortest path, http://www.ifi.unizh.ch/ddis/isparql.html
// icon to show OWL expressible SWRL rules.

// How to extract axiom information:

// c = class
// i = individual
// d = data value
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
// 3.1.1 enumeration: isOWLEnumerationDescription(?cd), onIndividual(?cd, ?i)

// (3) property restriction class descriptions
// 3.1.2 cardinality restrictions = isCardinalityRestriction(?a), isMinCardinalityRestriction(?a), isMaxCardinalityRestriction(?a), hasCardinality(?a, ?d)
// 3.1.2.1.1 allValuesFromRestriction: isAllValuesFromRestriction(?a), onProperty(?a, ?p), hasValue(?a, ?{i, d})
// 3.1.2.1.2 someValuesFrom: isSomeValuesFrom(?a), onProperty(?a, ?p), hasValue(?a, ?{i,d})
// 3.1.2.1.3 hasValueRestriction: isHasValueRestriction(?a), onProperty(?a, ?p), hasValue(?a, ?{i,d})

// (4) intersection of class description
// 3.1.3.1 intersectionOf: list of owl:oneOf class descriptions: isIntersectionOfClassDescription(?cd), onDescription(?cd, ?cd)

// (5) union of class description
// 3.1.3.2 unionOf: list of owl:oneOf class descriptions: isOWLUnionOfDescription(?cd), onDescription(?cd, ?cd)

// (6) complement of class description
// 3.1.3.3 complementOf: isOWLComplementOfDescription(?cd), onDescription(?cd, ?cd)

// OWL Class axioms (which use cds)
// 3.2.1 rdfs:subClassOf: isSubClassOf(?c, ?c), isOWLSubClassAxiom(?ca), ...
// 3.2.2 owl:equivalentClass: isEquivalentClass(?c, ?c), isOWLEquivalentClassAxiom(?ca), onDescription(?ca, ?cd)
// 3.2.3 isOWLOneOfDescription? how to distinguish from 3.1.1??
// 3.2.4 owl:isDisjointWith: isDisjointWith(?c, ?c), isDisjointWithClassAxiom(?ca), onDescription(?ca, ?cd)

// OWL Property axioms:
// 4.1.1 rdfs:subPropertyOf: isSubPropertyOf(?p,?p), isDirectSubPropertyOf(?p, ?p)
// 4.1.2 rdfs:domain: isInDomainOf(?p, ?c), isInDirectDomainOf(?p, ?c), isOWLInDomainOfAxiom(?pa), onDescription(?pa, ?cd)
// 4.1.3 rdfs:range: isInRangeOf(?p, ?c), isInDirectRangeOf(?p, ?c), isOWLInRangeOfAxiom(?pa), onDescription(?pa, ?cd)
// 4.2.1 owl:equivalentProperty: isEquivalentProperty(?p, ?p), isOWLEquivalentPropertyAxiom(?pa), onProperty(?pa, ?p)
// 4.2.2 owl:inverseOf: isInverseOf(?p, ?p), isOWLInverseOfAxiom(?pa), onProperty(?pa, ?p)
// 4.3.1 owl:FunctionalProperty: isOWLFunctionalProperty(?p), isFunctionalPropertyAxiom(?pa), onProperty(?pa, ?p)
// 4.3.2 owl:InverseFunctionalProperty: isOWLInverseFunctionalProperty(?p), isInverseFunctionalPropertyAxiom(?pa), onProperty(?pa, ?p)
// 4.4.1 owl:TransitiveProperty: isOWLTransitiveProperty(?p), isTransitivePropertyAxiom(?pa), onProperty(?pa, ?p)

// OWL Individual axioms:
// 5.2.1 owl:sameAs: isOWLSameAsAxiom(?ia), onIndividual(?ia, ?i)
// 5.2.2 owl:differentFrom: isOWLDifferentFromAxiom(?ia), onIndividual(?ia, ?i)
// 5.2.3 owl:AllDifferent: isOWLAllDifferentAxiom(?ia), onIndividual(?ia, ?i)
//
// Datatype axioms:
// 6.2 Enumerated datatype: isOWLEnumeratedDatatypeAxiom(?da), onValue(?da, ?d)

package edu.stanford.smi.protegex.owl.swrl.bridge.builtins.tbox;

import java.util.List;
import java.util.Set;

import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.swrl.bridge.BuiltInArgument;
import edu.stanford.smi.protegex.owl.swrl.bridge.MultiArgument;
import edu.stanford.smi.protegex.owl.swrl.bridge.builtins.AbstractSWRLBuiltInLibrary;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.BuiltInException;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.BuiltInNotImplementedException;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.OWLConversionFactoryException;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.SWRLBuiltInLibraryException;
import edu.stanford.smi.protegex.owl.swrl.exceptions.SWRLOWLUtilException;
import edu.stanford.smi.protegex.owl.swrl.owlapi.OWLDataFactory;
import edu.stanford.smi.protegex.owl.swrl.owlapi.OWLProperty;
import edu.stanford.smi.protegex.owl.swrl.owlapi.OWLSomeValuesFrom;
import edu.stanford.smi.protegex.owl.swrl.util.SWRLOWLUtil;

/**
 * Implementations library for SWRL TBox built-in methods. See <a
 * href="http://protege.cim3.net/cgi-bin/wiki.pl?SWRLTBoxBuiltIns">here</a> for documentation on this library.
 *
 * See <a href="http://protege.cim3.net/cgi-bin/wiki.pl?SWRLBuiltInBridge">here</a> for documentation on defining SWRL built-in libraries.
 */
public class SWRLBuiltInLibraryImpl extends AbstractSWRLBuiltInLibrary
{
  private static String SWRLTBoxLibraryName = "SWRLTBoxBuiltIns";
  
  public SWRLBuiltInLibraryImpl() 
  { 
    super(SWRLTBoxLibraryName); 
  }

  public void reset() {}

  /**
   * Is the second annotation property argument associated with the first ontology, class, property or individual argument. If the second
   * argument is unbound, bind it to the annotation properties associated with the first argument (if any exist).
   */
  public boolean hasAnnotationProperty(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } 

  /**
   * It the second integer argument equal to the cardinality specified by the first owl:Cardinality, owl:MaxCardinality, or
   * owl:MinCardinality restriction argument. If the second argument is unbound, bind it to the cardinality value.
   */
  public boolean hasCardinality(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } 

  /**
   * Is the second string argument the value specified by the first rdfs:comment annotation property argument. If the second argument is
   * unbound, bind it to the value specified by the first rdfs:comment annotation property argument.
   */
  public boolean hasRDFSComment(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } 

  /**
   * Is the second description argument associated with the first class or individual argument. If the second argument is unbound, bind it
   * to the descriptions associated with the first argument (if any exist).
   */
  public boolean hasDescription(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  }

  /**
   * Is the second resource argument the value specified by the first rdfs:isDefinedBy annotation property argument. If the second argument
   * is unbound, bind it to the value specified by the first rdfs:isDefinedBy annotation property argument.
   */
  public boolean hasRDFSIsDefinedBy(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } 

  /**
   * Is the second string argument the value specified by the first rdfs:label annotation property argument. If the second argument is
   * unbound, bind it to the value specified by the first rdfs:label annotation property argument.
   */
  public boolean hasRDFSLabel(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } 

  /**
   * Is the second resource argument the value specified by the first rdfs:seeAlso annotation property argument. If the second argument
   * is unbound, bind it to the value specified by the first rdfs:seeAlso annotation property argument.
   */
  public boolean hasRDFSSeeAlso(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } 

  /**
   * Is the second string argument the value specified by the first rdfs:versionInfo annotation property argument. If the second argument
   * is unbound, bind it to the value specified by the first rdfs:versionInfo annotation property argument.
   */
  public boolean hasVersionInfo(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } 

  /**
   * Are all individual arguments declared to be the different from each other.
   */
  public boolean isAllDifferents(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  }

  /**
   * Is the single argument an owl:AllDifferentsAxiom.
   */
  public boolean isAllDifferentsAxiom(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } 

  /**
   * Is the single argument an owl:AllValuesFrom restriction.
   */
  public boolean isAllValuesFromRestriction(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } 

  /**
   * Is the single argument an annotation property.
   */
  public boolean isAnnotationProperty(List<BuiltInArgument> arguments) throws BuiltInException
  {
    checkNumberOfArgumentsEqualTo(1, arguments.size());
    String propertyURI = getArgumentAsAPropertyURI(0, arguments);
    boolean result = false;

    try {
      result = SWRLOWLUtil.isAnnotationProperty(getOWLModel(), propertyURI, true);
    } catch (SWRLOWLUtilException e) {
      throw new BuiltInException(e.getMessage());
    } // try

    return result;
  }

  /**
   * Is the single argument an owl:Cardinality restriction.
   */
  public boolean isCardinalityRestriction(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  }

  /**
   * Determine if a single argument is an OWL named class. If the argument is unbound, bind it to all OWL named classes in an ontology.
   */
  public boolean isClass(List<BuiltInArgument> arguments) throws BuiltInException
  {
    checkNumberOfArgumentsEqualTo(1, arguments.size());
    boolean isUnboundArgument = isUnboundArgument(0, arguments);   
    boolean result = false;

    try {
      if (getIsInConsequent()) {
        if (isArgumentAString(0, arguments)) {
          String classURI = SWRLOWLUtil.getFullName(getOWLModel(), getArgumentAsAString(0, arguments));
          if (!getBuiltInBridge().isOWLClass(classURI)) getBuiltInBridge().injectOWLClassDeclaration(classURI);
        } else checkThatArgumentIsAClass(0, arguments);
        result = true;
      } else {
        if (isUnboundArgument) {
          MultiArgument multiArgument = createMultiArgument();
          for (edu.stanford.smi.protegex.owl.model.OWLNamedClass cls : SWRLOWLUtil.getUserDefinedOWLNamedClasses(getOWLModel()))
            multiArgument.addArgument(createClassArgument(cls.getURI()));
          arguments.get(0).setBuiltInResult(multiArgument);
          result = !multiArgument.hasNoArguments();
        } else {
          String classURI = getArgumentAsAClassURI(0, arguments);
          result = getBuiltInBridge().isOWLClass(classURI);
        } // if
      } // if
    } catch (SWRLOWLUtilException e) {
      throw new BuiltInException(e.getMessage());
    } // try

    return result;
  } 

  /**
   * Is the single argument an OWL class description.
   */
  public boolean isClassDescription(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result = false;
    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } 

  /**
   * Is the single argument an rdfs:comment annotation.
   */
  public boolean isRDFSComment(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } 

  /**
   * Is the first class argument the complement of the second class argument. If the second argument is unbound, bind it to the complement
   * of the first argument.
   */
  public boolean isComplementOf(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result = false;
    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } 

  /**
   * Is the single argument an owl:ComplementOf class description.
   */
  public boolean isComplementOfDescription(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result = false;
    if (!result) throw new BuiltInNotImplementedException();

    return result;
  }

  /**
   * Determine if the single argument is an OWL data property. If the argument is unbound, bind it to all OWL datatype
   * properties in an ontology.
   */
  public boolean isDataProperty(List<BuiltInArgument> arguments) throws BuiltInException
  {
    checkNumberOfArgumentsEqualTo(1, arguments.size());
    boolean isUnboundArgument = isUnboundArgument(0, arguments);   
    boolean result = false;

    try {
      if (isUnboundArgument) {
        MultiArgument multiArgument = createMultiArgument();
        for (edu.stanford.smi.protegex.owl.model.OWLProperty property : SWRLOWLUtil.getUserDefinedOWLDatatypeProperties(getOWLModel())) {
          if (property.isObjectProperty()) multiArgument.addArgument(createObjectPropertyArgument(property.getURI()));
          else multiArgument.addArgument(createDataPropertyArgument(property.getURI()));
        } // for
        arguments.get(0).setBuiltInResult(multiArgument);
        result = !multiArgument.hasNoArguments();
      } else {
        String propertyURI = getArgumentAsAPropertyURI(0, arguments);
        result = SWRLOWLUtil.isOWLDataProperty(getOWLModel(), propertyURI, false);
      } // if
    } catch (SWRLOWLUtilException e) {
      throw new BuiltInException(e.getMessage());
    } // try

    return result;
  } 

  /**
   * Is the single argument an rdfs:isDefinedBy annotation.
   */
  public boolean isRDFSIsDefinedBy(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  }

  /**
   * Is the single argument an owl:DifferentFrom axiom.
   */
  public boolean isDifferentFromAxiom(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } 

  /**
   * Check that the second class argument is a direct subclass of the first class argument. If the second argument is unbound, bind it to
   * the direct subclasses of the first argument (if any exist).
   */
  public boolean isDirectSubClassOf(List<BuiltInArgument> arguments) throws BuiltInException
  {
    return isSubClassOf(arguments, false);
  } 

  /**
   * Determine if the second property argument is a direct subproperty of the first property argument. If the second argument is unbound,
   * bind it to the direct sub properties of the first argument (if any exist).
   */
  public boolean isDirectSubPropertyOf(List<BuiltInArgument> arguments) throws BuiltInException
  {
    return isSubPropertyOf(arguments, false);
  } 

  /**
   * Check that the second class argument is a direct superclass of the first class argument. If the second argument is unbound, bind it to
   * the direct superclasses of the first argument (if any exist).
   */
  public boolean isDirectSuperClassOf(List<BuiltInArgument> arguments) throws BuiltInException
  {
    return isSuperClassOf(arguments, false);
  } 

  /**
   * Determine if the second property argument is a direct superproperty of the first property argument. If the second argument is unbound,
   * bind it to the direct super properties of the first argument (if any exist).
   */
  public boolean isDirectSuperPropertyOf(List<BuiltInArgument> arguments) throws BuiltInException
  {
    return isSuperPropertyOf(arguments, false);
  } 

  /**
   * Determine if the two class arguments represent classes or properties that are disjoint with each other. If the second argument is
   * unbound, bind it to the disjoint classes of the first argument (if any exist).
   */
  public boolean isDisjointWith(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } 

  /**
   * Is the single argument an owl:DisjointWith axiom.
   */
  public boolean isDisjointWithAxiom(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } 

  /**
   * Is the single argument an enumerated datatype axiom.
   */
  public boolean isEnumeratedDatatypeAxiom(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } 

  /**
   * Determine if the two class arguments are equivalent to each other. If the second
   * argument is unbound, bind it to the equivalent classes of the first argument (if any exist).
   */
  public boolean isEquivalentClass(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } 

  /**
   * Is the single argument an owl:EquivalentClass axiom.
   */
  public boolean isEquivalentClassAxiom(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } 

  /**
   * Determine if the two property arguments are equivalent to each other. If the second argument is unbound, bind it to the equivalent
   * properties of the first argument (if any exist).
   */
  public boolean isEquivalentProperty(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  }

  /**
   * Is the single argument an owl:EquivalentProperty axiom.
   */
  public boolean isEquivalentPropertyAxiom(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } 

  /**
   * Determine if a single property argument is functional.
   */
  public boolean isFunctionalProperty(List<BuiltInArgument> arguments) throws BuiltInException
  {
    checkNumberOfArgumentsEqualTo(1, arguments.size());
    String propertyURI = getArgumentAsAPropertyURI(0, arguments);

    boolean result = false;
    try {
      result = SWRLOWLUtil.isOWLFunctionalProperty(getOWLModel(), propertyURI, true);
    } catch (SWRLOWLUtilException e) {
      throw new BuiltInException(e.getMessage());
    } // try

    return result;
  } 

  /**
   * Is the single argument an owl:functionalProperty axiom.
   */
  public boolean isFunctionalPropertyAxiom(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  }

  /**
   * Is the single argument an owl:hasValue restriction
   */
  public boolean isHasValueRestriction(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } 

  /**
   * Check that the first class argument is in the domain of the second property argument (excluding its superproperties). If the first
   * argument is unbound and the second argument is bound, bind the first argument to the domain(s) of the second property argument (if any
   * exist). If the first class argument is bound and the second argument is unbound, bind the second argument to the properties that have
   * the class in their domain (if any). An error is thrown if both arguments are unbound.
   */
  public boolean isInDirectDomainOf(List<BuiltInArgument> arguments) throws BuiltInException
  {
    return isInDomainOf(arguments, false);
  } 

  /**
   * Check that the first class argument is in the range of the second property argument excluding its superproperties. If the first
   * argument is unbound, bind it to the range of the second argument (if any exist).
   */
  public boolean isInDirectRangeOf(List<BuiltInArgument> arguments) throws BuiltInException
  {
    return isInRangeOf(arguments, false);
  } 

  /**
   * Check that the first class argument is in the domain of the second property argument (including its superproperties). If the first
   * argument is unbound and the second argument is bound, bind the first argument to the domain(s) of the second property argument (if any
   * exist). If the first class argument is bound and the second argument is unbound, bind the second argument to the properties that have
   * the class in their domain (if any). An error is thrown if both arguments are unbound.
   */
  public boolean isInDomainOf(List<BuiltInArgument> arguments) throws BuiltInException
  {
    return isInDomainOf(arguments, true);
  } 

  /**
   * Is the single argument an OWL domain axiom.
   */
  public boolean isInDomainOfAxiom(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } 

  /**
   * Check that the first class argument is in the range of the second property argument (including its superproperties). If the first
   * argument is unbound, bind it to the range of the second argument (if any exist).
   */
  public boolean isInRangeOf(List<BuiltInArgument> arguments) throws BuiltInException
  {
    return isInRangeOf(arguments, true);
  } 

  /**
   * Is the single argument an OWL range axiom.
   */
  public boolean isInRangeOfAxiom(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } 

  /**
   * Is the single argument an owl:InteresctionOf class description.
   */
  public boolean isIntersectionOfDescription(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } 

  /**
   * Is the single argument an owl:InverseFunctionalProperty axiom.
   */
  public boolean isInverseFunctionalPropertyAxiom(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } 

  /**
   * Determine if the second property argument is the inverse of the first property argument.
   */
  public boolean isInverseOf(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  }

  /**
   * Determine if a single property argument is inverse functional.
   */
  public boolean isInverseFunctionalProperty(List<BuiltInArgument> arguments) throws BuiltInException
  {
    checkNumberOfArgumentsEqualTo(1, arguments.size());
    String propertyURI = getArgumentAsAPropertyURI(0, arguments);

    boolean result = false;
    try {
      result = SWRLOWLUtil.isInverseFunctionalProperty(getOWLModel(), propertyURI, true);
    } catch (SWRLOWLUtilException e) {
      throw new BuiltInException(e.getMessage());
    } // try

    return result;
  } 

  /**
   * Is the single argument an owl:inverseOf axiom.
   */
  public boolean isInverseOfAxiom(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } 

  /**
   * Is the single argument an rdfs:label annotation.
   */
  public boolean isRDFSLabelAnnotation(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } 

  /**
   * Is the single argument an owl:MaxCardinality restriction.
   */
  public boolean isMaxCardinalityRestriction(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } 

  /**
   * Is the single argument an owl:MinCardinality restriction.
   */
  public boolean isMinCardinalityRestriction(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } 

  /**
   * Determine if a single argument is an OWL object property. If the argument is unbound, bind it to all OWL object properties in an
   * ontology.
   */
  public boolean isObjectProperty(List<BuiltInArgument> arguments) throws BuiltInException
  {
    checkNumberOfArgumentsEqualTo(1, arguments.size());
    boolean isUnboundArgument = isUnboundArgument(0, arguments);   
    boolean result = false;

    try {
      if (isUnboundArgument) {
        MultiArgument multiArgument = createMultiArgument();
        for (edu.stanford.smi.protegex.owl.model.OWLProperty property : SWRLOWLUtil.getUserDefinedOWLObjectProperties(getOWLModel())) {
          if (property.isObjectProperty()) multiArgument.addArgument(createObjectPropertyArgument(property.getURI()));
          else multiArgument.addArgument(createDataPropertyArgument(property.getURI()));
        } // for
        arguments.get(0).setBuiltInResult(multiArgument);
        result = !multiArgument.hasNoArguments();
      } else {
        String propertyURI = getArgumentAsAPropertyURI(0, arguments);
        result = SWRLOWLUtil.isOWLObjectProperty(getOWLModel(), propertyURI, false);
      } // if
    } catch (SWRLOWLUtilException e) {
      throw new BuiltInException(e.getMessage());
    } // try

    return result;
  } 

  /**
   * Is the single argument an owl:oneOf class description.
   */
  public boolean isOneOfClassDescription(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } 

  /**
   * Is the single argument an owl:Ontology resource. If the argument is unbound, bind it to the current ontology.
   */
  public boolean isOntology(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } 

  /**
   * Determine if a single property argument is an OWL object or data property. If the argument is unbound, bind it to all OWL properties in an ontology.
   */
  public boolean isProperty(List<BuiltInArgument> arguments) throws BuiltInException
  {
    checkNumberOfArgumentsEqualTo(1, arguments.size());
    boolean isUnboundArgument = isUnboundArgument(0, arguments);   
    boolean result = false;

    try {
      if (isUnboundArgument) {
        MultiArgument multiArgument = createMultiArgument();
        for (edu.stanford.smi.protegex.owl.model.OWLProperty property : SWRLOWLUtil.getUserDefinedOWLProperties(getOWLModel())) {
          if (property.isObjectProperty()) multiArgument.addArgument(createObjectPropertyArgument(property.getURI()));
          else multiArgument.addArgument(createDataPropertyArgument(property.getURI()));
        } // for
        arguments.get(0).setBuiltInResult(multiArgument);
        result = !multiArgument.hasNoArguments();
      } else {
        String propertyURI = getArgumentAsAPropertyURI(0, arguments);
        result = SWRLOWLUtil.isOWLProperty(getOWLModel(), propertyURI, false);
      } // if
    } catch (SWRLOWLUtilException e) {
      throw new BuiltInException(e.getMessage());
    } // try

    return result;
  } 

  /**
   * Is the single argument an rdfs:seeAlso annotation.
   */
  public boolean isRDFSSeeAlsoAnnotation(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } 

  /**
   * Is the single argument an owl:SameAs axiom.
   */
  public boolean isSameAsAxiom(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result = false;
    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } 

  /**
   * Check that the first class argument is a subclass of the second class argument. If the first argument is unbound, bind it to
   * the subclasses of the second argument (if any exist).
   */
  public boolean isSubClassOf(List<BuiltInArgument> arguments) throws BuiltInException
  {
    return isSubClassOf(arguments, true);
  } 

  /**
   * Determine if the first property argument is a subproperty of the second property argument. If the first argument is unbound,
   * bind it to the sub properties of the second argument (if any exist).
   */
  public boolean isSubPropertyOf(List<BuiltInArgument> arguments) throws BuiltInException
  {
    return isSubPropertyOf(arguments, true);
  }

  /**
   * Check that the first class argument is a superclass of the second class argument. If the first argument is unbound, bind it to
   * the superclasses of the second argument (if any exist).
   */
  public boolean isSuperClassOf(List<BuiltInArgument> arguments) throws BuiltInException
  {
    return isSuperClassOf(arguments, true);
  } 

  /**
   * Determine if the first property argument is a superproperty of the second property argument. If the first argument is unbound,
   * bind it to the super properties of the second argument (if any exist).
   */
  public boolean isSuperPropertyOf(List<BuiltInArgument> arguments) throws BuiltInException
  {
    return isSuperPropertyOf(arguments, true);
  } 

  /**
   * Determine if a single property argument is symmetric.
   */
  public boolean isSymmetricProperty(List<BuiltInArgument> arguments) throws BuiltInException
  {
    checkNumberOfArgumentsEqualTo(1, arguments.size());
    String propertyURI = getArgumentAsAPropertyURI(0, arguments);

    boolean result = false;
    try {
      result = SWRLOWLUtil.isOWLSymmetricProperty(getOWLModel(), propertyURI, true);
    } catch (SWRLOWLUtilException e) {
      throw new BuiltInException(e.getMessage());
    } // try

    return result;
  } 

  /**
   * Is the single argument an owl:SymmetricProperty axiom.
   */
  public boolean isSymmetricPropertyAxiom(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } 

  /**
   * Determine if a single property argument is a transitive property.
   */
  public boolean isTransitiveProperty(List<BuiltInArgument> arguments) throws BuiltInException
  {
    checkNumberOfArgumentsEqualTo(1, arguments.size());
    String propertyURI = getArgumentAsAPropertyURI(0, arguments);

    boolean result = false;
    try {
      result = SWRLOWLUtil.isOWLTransitiveProperty(getOWLModel(), propertyURI);
    } catch (SWRLOWLUtilException e) {
      throw new BuiltInException(e.getMessage());
    } // try

    return result;
  } 

  /**
   * Is the single argument an owl:TransitiveProperty axiom.
   */
  public boolean isTransitivePropertyAxiom(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  }

  public boolean isRestriction(List<BuiltInArgument> arguments) throws BuiltInException
  {
    checkNumberOfArgumentsEqualTo(1, arguments.size());
    String classURI = getArgumentAsAClassURI(0, arguments);
    boolean result = false;

    try {
      result = SWRLOWLUtil.isAnonymousResourceName(getOWLModel(), classURI);
    } catch (SWRLOWLUtilException e) {
      throw new BuiltInException(e.getMessage());
    } // try

    return result;
  }
    
  /**
   * Is the single argument an owl:UnionOf class description.
   */
  public boolean isUnionOfDescription(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  }

  /**
   * Is the single argument an owl:versionInfo annotation.
   */
  public boolean isVersionInfoAnnotation(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } 

  /**
   * It the second class description argument a subject of the first axiom argument. If the second argument is unbound, bind it to the the
   * axiom's subject(s).
   */
  public boolean onDescription(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  }

  /**
   * It the second individual argument a subject of the first individual axiom argument. If the second argument is unbound, bind it to the
   * individual axiom's subject(s).
   */
  public boolean onIndividual(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  }

  /**
   * It the second property argument a subject of the first property axiom argument. If the second argument is unbound, bind it to the
   * property axiom's subject(s).
   */
  public boolean isSomeValuesFrom(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result = false;
    String restrictionURI, onPropertyURI, onClassURI;

    checkNumberOfArgumentsEqualTo(3, arguments.size());
    restrictionURI  = getArgumentAsAClassURI(0, arguments);
    onPropertyURI = getArgumentAsAPropertyURI(1, arguments);

    try {
      if (getIsInConsequent()) {
        OWLDataFactory owlFactory = getBuiltInBridge().getOWLDataFactory();
        OWLSomeValuesFrom someValuesFrom;
        OWLProperty onProperty;

        if (isArgumentAString(2, arguments)) {
          onClassURI = SWRLOWLUtil.getFullName(getOWLModel(), getArgumentAsAString(2, arguments));
          if (!getBuiltInBridge().isOWLClass(onClassURI)) getBuiltInBridge().injectOWLClassDeclaration(onClassURI);
        } else onClassURI = getArgumentAsAClassURI(2, arguments);

        if (SWRLOWLUtil.isOWLObjectProperty(getOWLModel(), onPropertyURI)) onProperty = owlFactory.getOWLObjectProperty(onPropertyURI);
        else onProperty = owlFactory.getOWLDataProperty(onPropertyURI);

        someValuesFrom = owlFactory.getOWLSomeValuesFrom(owlFactory.getOWLClass(restrictionURI), onProperty, owlFactory.getOWLClass(onClassURI));

        // getInvokingBridge().injectOWLAxiom(someValuesFrom); // TODO
        if (someValuesFrom != null) throw new BuiltInNotImplementedException();
      } else {
        onClassURI = getArgumentAsAClassURI(2, arguments);
        throw new BuiltInNotImplementedException();
      } // if
    } catch (OWLConversionFactoryException e) {
      throw new BuiltInException(e.getMessage());
    } // try

    return result;
  }

  /**
   * It the second datatype value argument a subject of the first datatype axiom argument. If the second argument is unbound, bind it to the
   * data value axiom's subject(s).
   */
  public boolean onValue(List<BuiltInArgument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  }

  /**
   * Check that the two class or property arguments refer to the same underlying entity.
   */
  public boolean equalTo(List<BuiltInArgument> arguments) throws BuiltInException
  {
    checkNumberOfArgumentsEqualTo(2, arguments.size());

    String resourceURI1 = getArgumentAsAURI(0, arguments);
    String resourceURI2 = getArgumentAsAURI(1, arguments);

    return resourceURI1.equals(resourceURI2); // According to RDF specification URI names must character (and case) match.
  } 

  /**
   * Check that the two class or property arguments do not refer to the same underlying entity.
   */
  public boolean notEqualTo(List<BuiltInArgument> arguments) throws BuiltInException
  {
    return !equalTo(arguments);
  } 

  private boolean isSuperClassOf(List<BuiltInArgument> arguments, boolean transitive) throws BuiltInException
  {
    boolean superClassArgumentUnbound = false;
    String classURI;
    boolean result = false;

    checkNumberOfArgumentsEqualTo(2, arguments.size());

    superClassArgumentUnbound = isUnboundArgument(0, arguments);
    classURI = getArgumentAsAClassURI(1, arguments);

    try {
      if (superClassArgumentUnbound) {
        List<edu.stanford.smi.protegex.owl.model.OWLNamedClass> superClasses;
        if (transitive) superClasses = SWRLOWLUtil.getSuperClassesOf(getOWLModel(), classURI);
        else superClasses = SWRLOWLUtil.getDirectSuperClassesOf(getOWLModel(), classURI);
        if (!superClasses.isEmpty()) {
          MultiArgument multiArgument = createMultiArgument();
          for (edu.stanford.smi.protegex.owl.model.OWLNamedClass superClass : superClasses) multiArgument.addArgument(createClassArgument(superClass.getURI()));
          arguments.get(0).setBuiltInResult(multiArgument);
        result = !multiArgument.hasNoArguments();
        } // if
      } else {
        String superClassURI = getArgumentAsAClassURI(0, arguments);
        if (transitive) result = SWRLOWLUtil.isOWLSuperClassOf(getOWLModel(), superClassURI, classURI, true);
        else result = SWRLOWLUtil.isOWLDirectSuperClassOf(getOWLModel(), superClassURI, classURI, true);
      } // if
    } catch (SWRLOWLUtilException e) {
      throw new BuiltInException(e.getMessage());
    } // try

    return result;
  }

  private boolean isSubClassOf(List<BuiltInArgument> arguments, boolean transitive) throws BuiltInException
  {
    boolean subClassArgumentUnbound = false;
    String classURI;
    boolean result = false;

    checkNumberOfArgumentsEqualTo(2, arguments.size());

    subClassArgumentUnbound = isUnboundArgument(0, arguments);

    try {
      if (getIsInConsequent()) {
        OWLDataFactory owlFactory = getBuiltInBridge().getOWLDataFactory();
        String superclassURI;

        if (isArgumentAString(1, arguments)) {
          superclassURI = SWRLOWLUtil.getFullName(getOWLModel(), getArgumentAsAString(1, arguments));
          if (!getBuiltInBridge().isOWLClass(superclassURI)) getBuiltInBridge().injectOWLClassDeclaration(superclassURI);
        } else superclassURI = getArgumentAsAClassURI(1, arguments);

        if (isArgumentAString(0, arguments)) 
          classURI = SWRLOWLUtil.getFullName(getOWLModel(), getArgumentAsAString(0, arguments));
        else classURI = getArgumentAsAClassURI(0, arguments);

        getBuiltInBridge().injectOWLAxiom(owlFactory.getOWLSubClassAxiom(owlFactory.getOWLClass(classURI),owlFactory.getOWLClass(superclassURI)));

        result = true;
      } else {
        classURI = getArgumentAsAClassURI(1, arguments);

        if (subClassArgumentUnbound) {
          List<edu.stanford.smi.protegex.owl.model.OWLNamedClass> subClasses;
          if (transitive) subClasses = SWRLOWLUtil.getSubClassesOf(getOWLModel(), classURI);
          else subClasses = SWRLOWLUtil.getDirectSubClassesOf(getOWLModel(), classURI);
          if (!subClasses.isEmpty()) {
            MultiArgument multiArgument = createMultiArgument();
            for (edu.stanford.smi.protegex.owl.model.OWLNamedClass subClass : subClasses) multiArgument.addArgument(createClassArgument(subClass.getURI()));
            arguments.get(0).setBuiltInResult(multiArgument);
            result = !multiArgument.hasNoArguments();
          } // if
        } else {
          String subClassURI = getArgumentAsAClassURI(0, arguments);
          if (transitive) result = SWRLOWLUtil.isOWLSubClassOf(getOWLModel(), subClassURI, classURI, true);
          else  result = SWRLOWLUtil.isOWLDirectSubClassOf(getOWLModel(), subClassURI, classURI, true);
        } // if
      } // if
    } catch (SWRLOWLUtilException e) {
      throw new BuiltInException(e.getMessage());
    } // try

    return result;
  }

  private boolean isSubPropertyOf(List<BuiltInArgument> arguments, boolean transitive) throws BuiltInException
  {
    boolean subPropertyArgumentUnbound = false;
    String propertyURI;
    boolean result = false;

    checkNumberOfArgumentsEqualTo(2, arguments.size());
    subPropertyArgumentUnbound = isUnboundArgument(0, arguments);
    propertyURI = getArgumentAsAPropertyURI(1, arguments);

    try {
      if (subPropertyArgumentUnbound) {
        Set<edu.stanford.smi.protegex.owl.model.OWLProperty> subProperties;
        if (transitive) subProperties = SWRLOWLUtil.getSubPropertiesOf(getOWLModel(), propertyURI);
        else subProperties = SWRLOWLUtil.getDirectSubPropertiesOf(getOWLModel(), propertyURI);
        if (!subProperties.isEmpty()) {
          MultiArgument multiArgument = createMultiArgument();
          for (edu.stanford.smi.protegex.owl.model.OWLProperty subProperty : subProperties) {
            if (subProperty.isObjectProperty()) multiArgument.addArgument(createObjectPropertyArgument(subProperty.getURI()));
            else multiArgument.addArgument(createDataPropertyArgument(subProperty.getURI()));
          } // for
          arguments.get(0).setBuiltInResult(multiArgument);
          result = !multiArgument.hasNoArguments();
        } // if
      } else {
        String subPropertyURI = getArgumentAsAPropertyURI(0, arguments);
        if (transitive) result = SWRLOWLUtil.isOWLSubPropertyOf(getOWLModel(), subPropertyURI, propertyURI, true);
        else result = SWRLOWLUtil.isOWLDirectSubPropertyOf(getOWLModel(), subPropertyURI, propertyURI, true);
      } // if
    } catch (SWRLOWLUtilException e) {
      throw new BuiltInException(e.getMessage());
    } // try

    return result;
  } 

  private boolean isSuperPropertyOf(List<BuiltInArgument> arguments, boolean transitive) throws BuiltInException
  {
    boolean superPropertyArgumentUnbound = false;
    String propertyURI;
    boolean result = false;

    checkNumberOfArgumentsEqualTo(2, arguments.size());
    superPropertyArgumentUnbound = isUnboundArgument(0, arguments);
    propertyURI = getArgumentAsAPropertyURI(1, arguments);

    try {
      if (superPropertyArgumentUnbound) {
        Set<edu.stanford.smi.protegex.owl.model.OWLProperty> superProperties;
        if (transitive) superProperties = SWRLOWLUtil.getSuperPropertiesOf(getOWLModel(), propertyURI);
        else superProperties = SWRLOWLUtil.getDirectSuperPropertiesOf(getOWLModel(), propertyURI);
        if (!superProperties.isEmpty()) {
          MultiArgument multiArgument = createMultiArgument();
          for (edu.stanford.smi.protegex.owl.model.OWLProperty superProperty : superProperties) {
            if (superProperty.isObjectProperty()) multiArgument.addArgument(createObjectPropertyArgument(superProperty.getURI()));
            else multiArgument.addArgument(createDataPropertyArgument(superProperty.getURI()));
          } // for
          arguments.get(0).setBuiltInResult(multiArgument);
          result = !multiArgument.hasNoArguments();
        } // if
      } else {
        String superPropertyURI = getArgumentAsAPropertyURI(0, arguments);
        if (transitive) result = SWRLOWLUtil.isOWLSuperPropertyOf(getOWLModel(), superPropertyURI, propertyURI, true);
        else result = SWRLOWLUtil.isOWLDirectSuperPropertyOf(getOWLModel(), superPropertyURI, propertyURI, true);
      } // if
    } catch (SWRLOWLUtilException e) {
      throw new BuiltInException(e.getMessage());
    } // try

    return result;
  }

  private boolean isInDomainOf(List<BuiltInArgument> arguments, boolean includingSuperproperties) throws BuiltInException
  {
    boolean domainClassArgumentUnbound, propertyArgumentUnbound = false;
    String propertyURI, domainClassURI;
    boolean result = false;

    checkNumberOfArgumentsEqualTo(2, arguments.size());

    domainClassArgumentUnbound = isUnboundArgument(0, arguments);
    propertyArgumentUnbound = isUnboundArgument(1, arguments);

    if (domainClassArgumentUnbound && propertyArgumentUnbound) throw new BuiltInException("at least one argument must be bound");

    try {
      if (domainClassArgumentUnbound) {
        Set<edu.stanford.smi.protegex.owl.model.OWLNamedClass> domainClasses;
        propertyURI = getArgumentAsAPropertyURI(1, arguments);
        if (includingSuperproperties) domainClasses = SWRLOWLUtil.getOWLDomainClasses(getOWLModel(), propertyURI);
        else domainClasses = SWRLOWLUtil.getDirectOWLDomainClasses(getOWLModel(), propertyURI);
        if (!domainClasses.isEmpty()) {
          MultiArgument multiArgument = createMultiArgument();
          for (edu.stanford.smi.protegex.owl.model.OWLNamedClass domainClass : domainClasses) multiArgument.addArgument(createClassArgument(domainClass.getURI()));
          arguments.get(0).setBuiltInResult(multiArgument);
        result = !multiArgument.hasNoArguments();
        } // if
      } else if (propertyArgumentUnbound) {
        Set<edu.stanford.smi.protegex.owl.model.OWLProperty> domainProperties;
        domainClassURI = getArgumentAsAClassURI(0, arguments);
        domainProperties = SWRLOWLUtil.getDomainProperties(getOWLModel(), domainClassURI, includingSuperproperties);
        if (!domainProperties.isEmpty()) {
          MultiArgument multiArgument = createMultiArgument();
          for (edu.stanford.smi.protegex.owl.model.OWLProperty property : domainProperties) {
            if (property.isObjectProperty()) multiArgument.addArgument(createObjectPropertyArgument(property.getURI()));
            else multiArgument.addArgument(createDataPropertyArgument(property.getURI()));
          } // for
          arguments.get(1).setBuiltInResult(multiArgument);
        result = !multiArgument.hasNoArguments();
        } // if
      } else { // Both arguments bound
        domainClassURI = getArgumentAsAClassURI(0, arguments);
        propertyURI = getArgumentAsAPropertyURI(1, arguments);
        if (includingSuperproperties) 
          result = SWRLOWLUtil.isInOWLPropertyDomain(getOWLModel(), propertyURI, domainClassURI, true);
        else 
          result = SWRLOWLUtil.isInDirectOWLPropertyDomain(getOWLModel(), propertyURI, domainClassURI, true);
      } // if
    } catch (SWRLOWLUtilException e) {
      throw new BuiltInException(e.getMessage());
    } // try

    return result;
  }

  private boolean isInRangeOf(List<BuiltInArgument> arguments, boolean includingSuperproperties) throws BuiltInException
  {
    boolean rangeClassArgumentUnbound = false;
    String rangeClassURI, propertyURI;
    boolean result = false;

    checkNumberOfArgumentsEqualTo(2, arguments.size());

    rangeClassArgumentUnbound = isUnboundArgument(0, arguments);
    propertyURI = getArgumentAsAPropertyURI(1, arguments);

    try {
      if (rangeClassArgumentUnbound) {
        Set<edu.stanford.smi.protegex.owl.model.OWLNamedClass> rangeClasses;
        if (includingSuperproperties) rangeClasses = SWRLOWLUtil.getOWLRangeClasses(getOWLModel(), propertyURI);
        else rangeClasses = SWRLOWLUtil.getOWLDirectRangeClasses(getOWLModel(), propertyURI);
        if (!rangeClasses.isEmpty()) {
          MultiArgument multiArgument = createMultiArgument();
          for (edu.stanford.smi.protegex.owl.model.OWLNamedClass rangeClass : rangeClasses) 
            multiArgument.addArgument(createClassArgument(rangeClass.getURI()));
          arguments.get(0).setBuiltInResult(multiArgument);
          result = !multiArgument.hasNoArguments();
        } // if
      } else {
        propertyURI = getArgumentAsAPropertyURI(1, arguments);
        rangeClassURI = getArgumentAsAClassURI(0, arguments);
        if (includingSuperproperties) result = SWRLOWLUtil.isInPropertyRange(getOWLModel(), propertyURI, rangeClassURI, true);
        else result = SWRLOWLUtil.isInDirectPropertyRange(getOWLModel(), propertyURI, rangeClassURI, true);
      } // if
    } catch (SWRLOWLUtilException e) {
      throw new BuiltInException(e.getMessage());
    } // try

    return result;
  }
  
  private OWLModel getOWLModel() throws SWRLBuiltInLibraryException { return getBuiltInBridge().getActiveOntology().getOWLModel(); }
}
