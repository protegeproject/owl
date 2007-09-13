/*
Interface OWLDescription

All Superinterfaces:
    OWLObject, OWLPropertyRange

All Known Subinterfaces: 

OWLAnonymousDescription, OWLBooleanDescription, OWLCardinalityRestriction<P,F>, OWLClass, OWLDataAllRestriction,
OWLDataCardinalityRestriction, OWLDataExactCardinalityRestriction, OWLDataMaxCardinalityRestriction, OWLDataMinCardinalityRestriction,
OWLDataSomeRestriction, OWLDataValueRestriction, OWLNaryBooleanDescription, OWLObjectAllRestriction, OWLObjectCardinalityRestriction,
OWLObjectComplementOf, OWLObjectExactCardinalityRestriction, OWLObjectIntersectionOf, OWLObjectMaxCardinalityRestriction,
OWLObjectMinCardinalityRestriction, OWLObjectOneOf, OWLObjectSelfRestriction, OWLObjectSomeRestriction, OWLObjectUnionOf,
OWLObjectValueRestriction, OWLQuantifiedRestriction<P,F>, OWLRestriction<P>, OWLValueRestriction<P,V>


AbstractOWLParser
AbstractOWLRenderer
AddAxiom
AnnotationValueShortFormProvider
AutoURIMapper
BidirectionalShortFormProvider
BidirectionalShortFormProviderAdapter
CachingBidirectionalShortFormProvider
CollectionFactory
CommonBaseURIMapper
DefaultChangeBroadcastStrategy
DefaultOntologyFormat
DLExpressivityChecker
DLExpressivityChecker.Construct
DublinCoreVocabulary
FilteringOWLOntologyChangeListener
HashCode
ImmutableOWLOntologyChangeException
Monitorable
MonitorableOWLReasoner
MonitorableOWLReasonerAdapter
NamedConjunctChecker
NamespaceOWLOntologyFormat
Namespaces
NamespaceUtil
NonMappingOntologyURIMapper
NullProgressMonitor
OWLAnnotation
OWLAnnotationAxiom
OWLAnnotationVisitor
OWLAnonymousDescription
OWLAnonymousIndividual
OWLAntiSymmetricObjectPropertyAxiom
OWLAxiom
OWLAxiomAnnotationAxiom
OWLAxiomChange
OWLAxiomFilter
OWLAxiomVisitor
OWLAxiomVisitorAdapter
OWLBooleanDescription
OWLCardinalityRestriction
OWLClass
OWLClassAssertionAxiom
OWLClassAxiom
OWLClassReasoner
OWLCommentAnnotation
OWLConsistencyChecker
OWLConstant
OWLConstantAnnotation
OWLDataAllRestriction
OWLDataCardinalityRestriction
OWLDataComplementOf
OWLDataExactCardinalityRestriction
OWLDataFactory
OWLDataMaxCardinalityRestriction
OWLDataMinCardinalityRestriction
OWLDataOneOf
OWLDataProperty
OWLDataPropertyAssertionAxiom
OWLDataPropertyAxiom
OWLDataPropertyCharacteristicAxiom
OWLDataPropertyDomainAxiom
OWLDataPropertyExpression
OWLDataPropertyRangeAxiom
OWLDataRange
OWLDataRangeFacetRestriction
OWLDataRangeRestriction
OWLDataSomeRestriction
OWLDataSubPropertyAxiom
OWLDataType
OWLDataUtil
OWLDataValueRestriction
OWLDataVisitor
OWLDeclarationAxiom
OWLDeprecatedClassAxiom
OWLDeprecatedDataPropertyAxiom
OWLDeprecatedObjectPropertyAxiom
OWLDescription
OWLDescriptionVisitor
OWLDescriptionVisitorAdapter
OWLDifferentIndividualsAxiom
OWLDisjointClassesAxiom
OWLDisjointDataPropertiesAxiom
OWLDisjointObjectPropertiesAxiom
OWLDisjointUnionAxiom
OWLEntity
OWLEntityAnnotationAxiom
OWLEntityCollectingOntologyChangeListener
OWLEntityCollector
OWLEntityRemover
OWLEntityRenamer
OWLEntitySetProvider
OWLEntityVisitor
OWLEquivalentClassesAxiom
OWLEquivalentDataPropertiesAxiom
OWLEquivalentObjectPropertiesAxiom
OWLException
OWLFunctionalDataPropertyAxiom
OWLFunctionalObjectPropertyAxiom
OWLFunctionalSyntaxOntologyFormat
OWLImportsDeclaration
OWLIndividual
OWLIndividualAxiom
OWLIndividualReasoner
OWLInverseFunctionalObjectPropertyAxiom
OWLInverseObjectPropertiesAxiom
OWLIrreflexiveObjectPropertyAxiom
OWLLabelAnnotation
OWLLogicalAxiom
OWLMutableOntology
OWLNamedObject
OWLNamedObjectVisitor
OWLNaryBooleanDescription
OWLNaryClassAxiom
OWLNaryIndividualAxiom
OWLNaryPropertyAxiom
OWLNegativeDataPropertyAssertionAxiom
OWLNegativeObjectPropertyAssertionAxiom
OWLObject
OWLObjectAllRestriction
OWLObjectAnnotation
OWLObjectCardinalityRestriction
OWLObjectComplementOf
OWLObjectDuplicator
OWLObjectExactCardinalityRestriction
OWLObjectIntersectionOf
OWLObjectMaxCardinalityRestriction
OWLObjectMinCardinalityRestriction
OWLObjectOneOf
OWLObjectProperty
OWLObjectPropertyAssertionAxiom
OWLObjectPropertyAxiom
OWLObjectPropertyChainSubPropertyAxiom
OWLObjectPropertyCharacteristicAxiom
OWLObjectPropertyDomainAxiom
OWLObjectPropertyExpression
OWLObjectPropertyInverse
OWLObjectPropertyRangeAxiom
OWLObjectSelfRestriction
OWLObjectSomeRestriction
OWLObjectSubPropertyAxiom
OWLObjectUnionOf
OWLObjectValueRestriction
OWLObjectVisitor
OWLObjectVisitorAdapter
OWLOntology
OWLOntologyAnnotationAxiom
OWLOntologyChange
OWLOntologyChangeBroadcastStrategy
OWLOntologyChangeException
OWLOntologyChangeFilter
OWLOntologyChangeListener
OWLOntologyChangeVetoException
OWLOntologyChangeVisitor
OWLOntologyChangeVisitorAdapter
OWLOntologyCreationException
OWLOntologyFactory
OWLOntologyFactory.OWLOntologyCreationHandler
OWLOntologyFactoryNotFoundException
OWLOntologyFormat
OWLOntologyImportsClosureSetProvider
OWLOntologyInputSource
OWLOntologyManager
OWLOntologyMerger
OWLOntologyResourceAccessException
OWLOntologySetProvider
OWLOntologySingletonSetProvider
OWLOntologyStorageException
OWLOntologyStorer
OWLOntologyStorerNotFoundException
OWLOntologyURIChanger
OWLOntologyURIMapper
OWLOntologyURIMappingNotFoundException
OWLParser
OWLParserException
OWLParserFactory
OWLParserFactoryRegistry
OWLParserIOException
OWLProperty
OWLPropertyAssertionAxiom
OWLPropertyAxiom
OWLPropertyDomainAxiom
OWLPropertyExpression
OWLPropertyExpressionVisitor
OWLPropertyRange
OWLPropertyRangeAxiom
OWLPropertyReasoner
OWLQuantifiedRestriction
OWLRDFVocabulary
OWLReasoner
OWLReasonerAdapter
OWLReasonerBase
OWLReasonerException
OWLReasonerMediator
OWLReflexiveObjectPropertyAxiom
OWLRenderer
OWLRendererException
OWLRendererIOException
OWLRestrictedDataRangeFacetVocabulary
OWLRestriction
OWLRuntimeException
OWLSameIndividualsAxiom
OWLSatisfiabilityChecker
OWLSubClassAxiom
OWLSubPropertyAxiom
OWLSymmetricObjectPropertyAxiom
OWLTransitiveObjectPropertyAxiom
OWLTypedConstant
OWLUnaryPropertyAxiom
OWLUntypedConstant
OWLValueRestriction
OWLXMLOntologyFormat
OWLXMLVocabulary
PhysicalURIInputSource
ProgressMonitor
QNameShortFormProvider
RDFXMLOntologyFormat
RemoveAxiom
RootClassChecker
SetOntologyURI
ShortFormProvider
SimpleRenderer
SimpleRootClassChecker
SimpleShortFormProvider
SimpleURIMapper
SpecificOntologyChangeBroadcastStrategy
StringInputSource
SWRLAtom
SWRLAtomConstantObject
SWRLAtomDObject
SWRLAtomDVariable
SWRLAtomIndividualObject
SWRLAtomIObject
SWRLAtomIVariable
SWRLAtomObject
SWRLAtomVariable
SWRLBinaryAtom
SWRLBuiltInAtom
SWRLBuiltInsVocabulary
SWRLClassAtom
SWRLDataFactory
SWRLDataRangeAtom
SWRLDataValuedPropertyAtom
SWRLDifferentFromAtom
SWRLObject
SWRLObjectPropertyAtom
SWRLObjectVisitor
SWRLRule
SWRLSameAsAtom
SWRLUnaryAtom
SWRLVocabulary
ToldClassHierarchyReasoner
UndefinedEntityException
UnknownOWLOntologyException
UnsupportedReasonerOperationException
XSDVocabulary 
*/
// TODO: a lot of repetition here
// cf. http://listserv.manchester.ac.uk/cgi-bin/wa?A2=ind0611&L=dig-wg&T=0&P=754

// lca, max flow, shortest path, http://www.ifi.unizh.ch/ddis/isparql.html

// icon to show OWL expressible SWRL rules.
//
// think about listener mechanism for assertion of new facts by rule engine
//
// which OWL axioms can be used in SWRL? All?

// RDFS - tbox:rdfsClass(?rc)?

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
// 3.1.2 cardinality restrictions = isCardinalityRestriction(?r), isMinCardinalityRestriction(?r), isMaxCardinalityRestriction(?r), hasCardinality(?r, ?d)
// 3.1.2.1.1 allValuesFromRestriction: isAllValuesFromRestriction(?r), onProperty(?r, ?p), hasValue(?r, ?{i, d})
// 3.1.2.1.2 someValuesFromRestriction: isSomeValuesFromRestriction(?r), onProperty(?r, ?p), hasValue(?r, ?{i,d})
// 3.1.2.1.3 hasValueRestriction: isHasValueRestriction(?r), onProperty(?r, ?p), hasValue(?r, ?{i,d})

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
   ** Is the second annotation property argument associated with the first ontology, class, property or individual argument. If the second
   ** argument is unbound, bind it to the annotation properties associated with the first argument (if any exist).
   */
  public boolean hasAnnotation(List<Argument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } // hasAnnotation

  /**
   ** It the second integer argument equal to the cardinality specified by the first owl:Cardinality, owl:MaxCardinality, or
   ** owl:MinCardinality restriction argument. If the second argument is unbound, bind it to the cardinality value.
   */
  public boolean hasCardinality(List<Argument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } // hasCardinality

  /**
   ** Is the second string argument the value specified by the first rdfs:comment annotation property argument. If the second argument is
   ** unbound, bind it to the value specified by the first rdfs:comment annotation property argument.
   */
  public boolean hasComment(List<Argument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } // hasComment

  /**
   ** Is the second description argument associated with the first class or individual argument. If the second argument is unbound, bind it
   ** to the descriptions associated with the first argument (if any exist).
   */
  public boolean hasDescription(List<Argument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } // hasDescription

  /**
   ** Is the second resource argument the value specified by the first rdfs:isDefinedBy annotation property argument. If the second argument
   ** is unbound, bind it to the value specified by the first rdfs:isDefinedBy annotation property argument.
   */
  public boolean hasIsDefinedBy(List<Argument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } // hasIsDefinedBy

  /**
   ** Is the second string argument the value specified by the first rdfs:label annotation property argument. If the second argument is
   ** unbound, bind it to the value specified by the first rdfs:label annotation property argument.
   */
  public boolean hasLabel(List<Argument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } // hasLabel

  /**
   ** Is the second resource argument the value specified by the first rdfs:seeAlso annotation property argument. If the second argument
   ** is unbound, bind it to the value specified by the first rdfs:seeAlso annotation property argument.
   */
  public boolean hasSeeAlso(List<Argument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } // hasSeeAlso

  /**
   ** Is the second string argument the value specified by the first rdfs:versionInfo annotation property argument. If the second argument
   ** is unbound, bind it to the value specified by the first rdfs:versionInfo annotation property argument.
   */
  public boolean hasVersionInfo(List<Argument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } // hasVersionInfo

  /**
   ** Are all individual arguments declared to be the different from each other.
   */
  public boolean isAllDifferents(List<Argument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } // isAllDifferents

  /**
   ** Is the single argument an owl:AllDifferentsAxiom.
   */
  public boolean isAllDifferentsAxiom(List<Argument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } // isAllDifferents

  /**
   ** Is the single argument an owl:AllValuesFrom restriction.
   */
  public boolean isAllValuesFromRestriction(List<Argument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } // isAllValuesFromRestriction

  /**
   ** Is the single argument an annotation property.
   */
  public boolean isAnnotation(List<Argument> arguments) throws BuiltInException
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
  public boolean isCardinalityRestriction(List<Argument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } // isCardinalityRestriction

  /**
   ** Is the single argument an OWL named class.
   */
  public boolean isClass(List<Argument> arguments) throws BuiltInException
  {
    boolean result = false;
    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } // isClass

  /**
   ** Is the single argument an OWL class description.
   */
  public boolean isClassDescription(List<Argument> arguments) throws BuiltInException
  {
    boolean result = false;
    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } // isClassDescription

  /**
   ** Is the single argument an rdfs:comment annotation.
   */
  public boolean isCommentAnnotation(List<Argument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } // isCommentAnnotation

  /**
   ** Is the first class argument the complement of the second class argument. If the second argument is unbound, bind it to the complement
   ** of the first argument.
   */
  public boolean isComplementOf(List<Argument> arguments) throws BuiltInException
  {
    boolean result = false;
    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } // isComplementOf

  /**
   ** Is the single argument an owl:ComplementOf class description.
   */
  public boolean isComplementOfClassDescription(List<Argument> arguments) throws BuiltInException
  {
    boolean result = false;
    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } // isComplementOfClassDescription

  /**
   ** Determine if the single argument is an OWL datatype property. If the argument is unbound, bind it to all OWL datatype
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
   ** Is the single argument an rdfs:isDefinedBy annotation.
   */
  public boolean isDefinedBy(List<Argument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } // isDefinedBy

  /**
   ** Are two individuals declared to be the different from each other.
   */
  public boolean isDifferentFrom(List<Argument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } // isDifferentFrom

  /**
   ** Is the single argument an owl:DifferentFrom axiom.
   */
  public boolean isDifferentFromAxiom(List<Argument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } // isDifferentFromAxiom

  /**
   ** Check that the second class argument is a direct subclass of the first class argument. If the second argument is unbound, bind it to
   ** the direct subclasses of the first argument (if any exist).
   */
  public boolean isDirectSubClassOf(List<Argument> arguments) throws BuiltInException
  {
    return isSubClassOf(arguments, false);
  } // isDirectSubClassOf

  /**
   ** Determine if the second property argument is a direct subproperty of the first property argument. If the second argument is unbound,
   ** bind it to the direct sub properties of the first argument (if any exist).
   */
  public boolean isDirectSubPropertyOf(List<Argument> arguments) throws BuiltInException
  {
    return isSubPropertyOf(arguments, false);
  } // isDirectSubPropertyOf

  /**
   ** Check that the second class argument is a direct superclass of the first class argument. If the second argument is unbound, bind it to
   ** the direct superclasses of the first argument (if any exist).
   */
  public boolean isDirectSuperClassOf(List<Argument> arguments) throws BuiltInException
  {
    return isSuperClassOf(arguments, false);
  } // isDirectSuperClassOf

  /**
   ** Determine if the second property argument is a direct superproperty of the first property argument. If the second argument is unbound,
   ** bind it to the direct super properties of the first argument (if any exist).
   */
  public boolean isDirectSuperPropertyOf(List<Argument> arguments) throws BuiltInException
  {
    return isSuperPropertyOf(arguments, false);
  } // isDirectSuperPropertyOf

  /**
   ** Determine if the two class arguments represent classes or properties that are disjoint with each other. If the second argument is
   ** unbound, bind it to the disjoint classes of the first argument (if any exist).
   */
  public boolean isDisjointWith(List<Argument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } // isDisjointWith

  /**
   ** Is the single argument an owl:DisjointWith axiom.
   */
  public boolean isDisjointWithAxiom(List<Argument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } // isDisjointWithAxiom

  /**
   ** Is the single argument an enumerated datatype axiom.
   */
  public boolean isEnumeratedDatatypeAxiom(List<Argument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } // isEnumeratedDatatypeAxiom

  /**
   ** Determine if the two class arguments are equivalent to each other. If the second
   ** argument is unbound, bind it to the equivalent classes of the first argument (if any exist).
   */
  public boolean isEquivalentClass(List<Argument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } // isEquivalentClass

  /**
   ** Is the single argument an owl:EquivalentClass axiom.
   */
  public boolean isEquivalentClassAxiom(List<Argument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } // isEquivalentClassAxiom

  /**
   ** Determine if the two property arguments are equivalent to each other. If the second argument is unbound, bind it to the equivalent
   ** properties of the first argument (if any exist).
   */
  public boolean isEquivalentProperty(List<Argument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } // isEquivalentProperty

  /**
   ** Is the single argument an owl:EquivalentProperty axiom.
   */
  public boolean isEquivalentPropertyAxiom(List<Argument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } // isEquivalentPropertyAxiom

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
   ** Is the single argument an owl:functionalProperty axiom.
   */
  public boolean isFunctionalPropertyAxiom(List<Argument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } // isFunctionalPropertyAxiom

  /**
   ** Is the single argument an owl:hasValue restriction
   */
  public boolean isHasValueRestriction(List<Argument> arguments) throws BuiltInException
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
  public boolean isInDirectDomainOf(List<Argument> arguments) throws BuiltInException
  {
    return isInDomainOf(arguments, false);
  } // isInDirectDomainOf

  /**
   ** Check that the first class argument is in the range of the second property argument excluding its superproperties. If the first
   ** argument is unbound, bind it to the range of the second argument (if any exist).
   */
  public boolean isInDirectRangeOf(List<Argument> arguments) throws BuiltInException
  {
    return isInRangeOf(arguments, false);
  } // isInDirectRangeOf

  /**
   ** Check that the first class argument is in the domain of the second property argument (including its superproperties). If the first
   ** argument is unbound and the second argument is bound, bind the first argument to the domain(s) of the second property argument (if any
   ** exist). If the first class argument is bound and the second argument is unbound, bind the second argument to the properties that have
   ** the class in their domain (if any). An error is thrown if both arguments are unbound.
   */
  public boolean isInDomainOf(List<Argument> arguments) throws BuiltInException
  {
    return isInDomainOf(arguments, true);
  } // isInDomainOf

  /**
   ** Is the single argument an OWL domain axiom.
   */
  public boolean isInDomainOfAxiom(List<Argument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } // isInDomainOfAxiom

  /**
   ** Check that the first class argument is in the range of the second property argument (including its superproperties). If the first
   ** argument is unbound, bind it to the range of the second argument (if any exist).
   */
  public boolean isInRangeOf(List<Argument> arguments) throws BuiltInException
  {
    return isInRangeOf(arguments, true);
  } // isInRangeOf


  /**
   ** Is the single argument an OWL range axiom.
   */
  public boolean isInRangeOfAxiom(List<Argument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } // isInRangeOfAxiom

  /**
   ** Is the single argument an owl:InteresctionOf class description.
   */
  public boolean isIntersectionOfDescription(List<Argument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } // isIntersectionOfDescription

  /**
   ** Is the single argument an owl:InverseFunctionalProperty axiom.
   */
  public boolean isInverseFunctionalPropertyAxiom(List<Argument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } // isInverseFunctionalPropertyAxiom

  /**
   ** Determine if the second property argument is the inverse of the first property argument.
   */
  public boolean isInverseOf(List<Argument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } // isInverseOf

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
   ** Is the single argument an owl:inverseOf axiom.
   */
  public boolean isInverseOfAxiom(List<Argument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } // isInverseOfAxiom

  /**
   ** Is the single argument an rdfs:label annotation.
   */
  public boolean isLabelAnnotation(List<Argument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } // isLabelAnnotation

  /**
   ** Is the single argument an owl:MaxCardinality restriction.
   */
  public boolean isMaxCardinalityRestriction(List<Argument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } // isMaxCardinalityRestriction

  /**
   ** Is the single argument an owl:MinCardinality restriction.
   */
  public boolean isMinCardinalityRestriction(List<Argument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } // isMinCardinalityRestriction

  /**
   ** Determine if a single argument is an OWL named class. If the argument is unbound, bind it to all OWL named classes in an ontology.
   */
  public boolean isNamedClass(List<Argument> arguments) throws BuiltInException
  {
    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(1, arguments.size());
    boolean isUnboundArgument = SWRLBuiltInUtil.isUnboundArgument(0, arguments);   
    boolean result = false;

    try {
      if (isUnboundArgument) {
        MultiArgument multiArgument = new MultiArgument();
        for (OWLNamedClass cls : SWRLOWLUtil.getUserDefinedOWLNamedClasses(getInvokingBridge().getOWLModel()))
          multiArgument.addArgument(new ClassInfo(cls.getName()));
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
  } // isNamedClass

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
   ** Is the single argument an owl:oneOf class description.
   */
  public boolean isOneOfClassDescription(List<Argument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } // isOneOfClassDescription

  /**
   ** Is the single argument an owl:Ontology resource. If the argument is unbound, bind it to the current ontology.
   */
  public boolean isOntology(List<Argument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } // isOntology

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
   ** Are two individuals declared to be the same as each other.
   */
  public boolean isSameAs(List<Argument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return false;
  } // isSameAs

  /**
   ** Is the single argument an rdfs:seeAlso annotation.
   */
  public boolean isSeeAlsoAnnotation(List<Argument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } // isSeeAlsoAnnotation

  /**
   ** Is the single argument an owl:SameAs axiom.
   */
  public boolean isSameAsAxiom(List<Argument> arguments) throws BuiltInException
  {
    boolean result = false;
    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } // isSameAsAxiom

  /**
   ** Is the single argument an owl:SomeValuesFrom restriction.
   */
  public boolean isSomeValuesFromRestriction(List<Argument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } // isSomeValuesFromRestriction

  /**
   ** Check that the first class argument is a subclass of the second class argument. If the first argument is unbound, bind it to
   ** the subclasses of the second argument (if any exist).
   */
  public boolean isSubClassOf(List<Argument> arguments) throws BuiltInException
  {
    return isSubClassOf(arguments, true);
  } // isSubClassOf

  /**
   ** Determine if the first property argument is a subproperty of the second property argument. If the first argument is unbound,
   ** bind it to the sub properties of the second argument (if any exist).
   */
  public boolean isSubPropertyOf(List<Argument> arguments) throws BuiltInException
  {
    return isSubPropertyOf(arguments, true);
  } // isSubPropertyOf

  /**
   ** Check that the first class argument is a superclass of the second class argument. If the first argument is unbound, bind it to
   ** the superclasses of the second argument (if any exist).
   */
  public boolean isSuperClassOf(List<Argument> arguments) throws BuiltInException
  {
    return isSuperClassOf(arguments, true);
  } // isSuperClassOf

  /**
   ** Determine if the first property argument is a superproperty of the second property argument. If the first argument is unbound,
   ** bind it to the super properties of the second argument (if any exist).
   */
  public boolean isSuperPropertyOf(List<Argument> arguments) throws BuiltInException
  {
    return isSuperPropertyOf(arguments, true);
  } // isSuperPropertyOf

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
   ** Is the single argument an owl:SymmetricProperty axiom.
   */
  public boolean isSymmetricPropertyAxiom(List<Argument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } // isSymmetricPropertyAxiom

  /**
   ** Determine if a single property argument is transitive.
   */
  public boolean isTransitiveProperty(List<Argument> arguments) throws BuiltInException
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
  public boolean isTransitivePropertyAxiom(List<Argument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } // isTransitivePropertyAxiom

  /**
   ** Is the single argument an owl:UnionOf class description.
   */
  public boolean isUnionOfDescription(List<Argument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } // isUnionOfDescription

  /**
   ** Is the single argument an owl:versionInfo annotation.
   */
  public boolean isVersionInfoAnnotation(List<Argument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } // isVersionInfoAnnotation

  /**
   ** It the second class description argument a subject of the first axiom argument. If the second argument is unbound, bind it to the the
   ** axiom's subject(s).
   */
  public boolean onClassDescription(List<Argument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } // onClassDescription

  /**
   ** It the second individual argument a subject of the first individual axiom argument. If the second argument is unbound, bind it to the
   ** individual axiom's subject(s).
   */
  public boolean onIndividual(List<Argument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } // onIndividual

  /**
   ** It the second property argument a subject of the first property axiom argument. If the second argument is unbound, bind it to the
   ** property axiom's subject(s).
   */
  public boolean onProperty(List<Argument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } // onProperty

  /**
   ** It the second datatype value argument a subject of the first datatype axiom argument. If the second argument is unbound, bind it to the
   ** data value axiom's subject(s).
   */
  public boolean onValue(List<Argument> arguments) throws BuiltInException
  {
    boolean result = false;

    if (!result) throw new BuiltInNotImplementedException();

    return result;
  } // onValue

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

  private boolean isInDomainOf(List<Argument> arguments, boolean includingSuperproperties) throws BuiltInException
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
        Set<OWLNamedClass> domainClasses;
        propertyName = SWRLBuiltInUtil.getArgumentAsAPropertyName(1, arguments);
        if (includingSuperproperties) domainClasses = SWRLOWLUtil.getDomainClasses(getInvokingBridge().getOWLModel(), propertyName);
        else domainClasses = SWRLOWLUtil.getDirectDomainClasses(getInvokingBridge().getOWLModel(), propertyName);
        if (!domainClasses.isEmpty()) {
          MultiArgument multiArgument = new MultiArgument();
          for (OWLNamedClass domainClass : domainClasses) multiArgument.addArgument(new ClassInfo(domainClass.getName()));
          arguments.set(0, multiArgument);
        result = !multiArgument.hasNoArguments();
        } // if
      } else if (propertyArgumentUnbound) {
        Set<OWLProperty> domainProperties;
        domainClassName = SWRLBuiltInUtil.getArgumentAsAClassName(0, arguments);
        domainProperties = SWRLOWLUtil.getDomainProperties(getInvokingBridge().getOWLModel(), domainClassName, includingSuperproperties);
        if (!domainProperties.isEmpty()) {
          MultiArgument multiArgument = new MultiArgument();
          for (OWLProperty property : domainProperties) multiArgument.addArgument(new PropertyInfo(property.getName()));
          arguments.set(1, multiArgument);
        result = !multiArgument.hasNoArguments();
        } // if
      } else { // Both arguments bound
        domainClassName = SWRLBuiltInUtil.getArgumentAsAClassName(0, arguments);
        propertyName = SWRLBuiltInUtil.getArgumentAsAPropertyName(1, arguments);
        if (includingSuperproperties) result = SWRLOWLUtil.isInPropertyDomain(getInvokingBridge().getOWLModel(), propertyName, domainClassName, true);
        else result = SWRLOWLUtil.isInDirectPropertyDomain(getInvokingBridge().getOWLModel(), propertyName, domainClassName, true);
      } // if
    } catch (SWRLOWLUtilException e) {
      throw new BuiltInException(e.getMessage());
    } // try

    return result;
  } // isInDomainOf

  private boolean isInRangeOf(List<Argument> arguments, boolean includingSuperproperties) throws BuiltInException
  {
    boolean rangeClassArgumentUnbound = false;
    String rangeClassName, propertyName;
    boolean result = false;

    SWRLBuiltInUtil.checkNumberOfArgumentsEqualTo(2, arguments.size());

    rangeClassArgumentUnbound = SWRLBuiltInUtil.isUnboundArgument(0, arguments);
    propertyName = SWRLBuiltInUtil.getArgumentAsAPropertyName(1, arguments);

    try {
      if (rangeClassArgumentUnbound) {
        Set<OWLNamedClass> rangeClasses;
        if (includingSuperproperties) rangeClasses = SWRLOWLUtil.getRangeClasses(getInvokingBridge().getOWLModel(), propertyName);
        else rangeClasses = SWRLOWLUtil.getDirectRangeClasses(getInvokingBridge().getOWLModel(), propertyName);
        if (!rangeClasses.isEmpty()) {
          MultiArgument multiArgument = new MultiArgument();
          for (OWLNamedClass rangeClass : rangeClasses) multiArgument.addArgument(new ClassInfo(rangeClass.getName()));
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
