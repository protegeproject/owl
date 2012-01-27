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

package org.protege.swrltab.bridge.builtins.tbox;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Set;

import org.protege.swrlapi.adapters.OWLClassAdapter;
import org.protege.swrlapi.adapters.OWLObjectAdapterFactory;
import org.protege.swrlapi.adapters.OWLObjectPropertyAdapter;
import org.protege.swrlapi.adapters.axioms.OWLClassDeclarationAxiomAdapter;
import org.protege.swrlapi.adapters.restrictions.OWLObjectSomeValuesFromAdapter;
import org.protege.swrlapi.arguments.SWRLBuiltInArgument;
import org.protege.swrlapi.arguments.SWRLClassBuiltInArgument;
import org.protege.swrlapi.arguments.SWRLDataPropertyBuiltInArgument;
import org.protege.swrlapi.arguments.SWRLMultiArgument;
import org.protege.swrlapi.arguments.SWRLObjectPropertyBuiltInArgument;
import org.protege.swrlapi.builtins.AbstractSWRLBuiltInLibrary;
import org.protege.swrlapi.exceptions.BuiltInException;
import org.protege.swrlapi.exceptions.BuiltInNotImplementedException;
import org.protege.swrlapi.exceptions.SWRLBuiltInBridgeException;

import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.swrl.util.P3OWLUtil;
import edu.stanford.smi.protegex.owl.swrl.util.P3OWLUtilException;

/**
 * Implementations library for SWRL TBox built-in methods. See <a href="http://protege.cim3.net/cgi-bin/wiki.pl?SWRLTBoxBuiltIns">here</a> for documentation on
 * this library.
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

	public void reset()
	{
	}

	/**
	 * Is the second annotation property argument associated with the first ontology, class, property or individual argument. If the second argument is unbound,
	 * bind it to the annotation properties associated with the first argument (if any exist).
	 */
	@SuppressWarnings("unused")
	public boolean hasAnnotationProperty(List<SWRLBuiltInArgument> arguments) throws BuiltInException
	{
		throw new BuiltInNotImplementedException();
	}

	/**
	 * It the second integer argument equal to the cardinality specified by the first owl:Cardinality, owl:MaxCardinality, or owl:MinCardinality restriction
	 * argument. If the second argument is unbound, bind it to the cardinality value.
	 */
	@SuppressWarnings("unused")
	public boolean hasCardinality(List<SWRLBuiltInArgument> arguments) throws BuiltInException
	{
		throw new BuiltInNotImplementedException();
	}

	/**
	 * Is the second string argument the value specified by the first rdfs:comment annotation property argument. If the second argument is unbound, bind it to the
	 * value specified by the first rdfs:comment annotation property argument.
	 */
	@SuppressWarnings("unused")
	public boolean hasRDFSComment(List<SWRLBuiltInArgument> arguments) throws BuiltInException
	{
		throw new BuiltInNotImplementedException();
	}

	/**
	 * Is the second description argument associated with the first class or individual argument. If the second argument is unbound, bind it to the descriptions
	 * associated with the first argument (if any exist).
	 */
	@SuppressWarnings("unused")
	public boolean hasDescription(List<SWRLBuiltInArgument> arguments) throws BuiltInException
	{
		throw new BuiltInNotImplementedException();
	}

	/**
	 * Is the second resource argument the value specified by the first rdfs:isDefinedBy annotation property argument. If the second argument is unbound, bind it
	 * to the value specified by the first rdfs:isDefinedBy annotation property argument.
	 */
	@SuppressWarnings("unused")
	public boolean hasRDFSIsDefinedBy(List<SWRLBuiltInArgument> arguments) throws BuiltInException
	{
		throw new BuiltInNotImplementedException();
	}

	/**
	 * Is the second string argument the value specified by the first rdfs:label annotation property argument. If the second argument is unbound, bind it to the
	 * value specified by the first rdfs:label annotation property argument.
	 */
	@SuppressWarnings("unused")
	public boolean hasRDFSLabel(List<SWRLBuiltInArgument> arguments) throws BuiltInException
	{
		throw new BuiltInNotImplementedException();
	}

	/**
	 * Is the second resource argument the value specified by the first rdfs:seeAlso annotation property argument. If the second argument is unbound, bind it to
	 * the value specified by the first rdfs:seeAlso annotation property argument.
	 */
	@SuppressWarnings("unused")
	public boolean hasRDFSSeeAlso(List<SWRLBuiltInArgument> arguments) throws BuiltInException
	{
		throw new BuiltInNotImplementedException();
	}

	/**
	 * Is the second string argument the value specified by the first rdfs:versionInfo annotation property argument. If the second argument is unbound, bind it to
	 * the value specified by the first rdfs:versionInfo annotation property argument.
	 */
	@SuppressWarnings("unused")
	public boolean hasVersionInfo(List<SWRLBuiltInArgument> arguments) throws BuiltInException
	{
		throw new BuiltInNotImplementedException();
	}

	/**
	 * Are all individual arguments declared to be the different from each other.
	 */
	@SuppressWarnings("unused")
	public boolean isAllDifferents(List<SWRLBuiltInArgument> arguments) throws BuiltInException
	{
		throw new BuiltInNotImplementedException();
	}

	/**
	 * Is the single argument an owl:AllDifferentsAxiom.
	 */
	@SuppressWarnings("unused")
	public boolean isAllDifferentsAxiom(List<SWRLBuiltInArgument> arguments) throws BuiltInException
	{
		throw new BuiltInNotImplementedException();
	}

	/**
	 * Is the single argument an owl:AllValuesFrom restriction.
	 */
	@SuppressWarnings("unused")
	public boolean isAllValuesFromRestriction(List<SWRLBuiltInArgument> arguments) throws BuiltInException
	{
		throw new BuiltInNotImplementedException();
	}

	/**
	 * Is the single argument an annotation property.
	 */
	public boolean isAnnotationProperty(List<SWRLBuiltInArgument> arguments) throws BuiltInException
	{
		checkNumberOfArgumentsEqualTo(1, arguments.size());
		URI propertyURI = getArgumentAsAPropertyURI(0, arguments);
		boolean result = false;

		try {
			result = P3OWLUtil.isAnnotationProperty(getOWLModel(), propertyURI, true);
		} catch (P3OWLUtilException e) {
			throw new BuiltInException(e.getMessage());
		}

		return result;
	}

	/**
	 * Is the single argument an owl:Cardinality restriction.
	 */
	@SuppressWarnings("unused")
	public boolean isCardinalityRestriction(List<SWRLBuiltInArgument> arguments) throws BuiltInException
	{
		throw new BuiltInNotImplementedException();
	}

	/**
	 * Determine if a single argument is an OWL named class. If the argument is unbound, bind it to all OWL named classes in an ontology.
	 */
	public boolean isClass(List<SWRLBuiltInArgument> arguments) throws BuiltInException
	{
		checkNumberOfArgumentsEqualTo(1, arguments.size());
		boolean isUnboundArgument = isUnboundArgument(0, arguments);
		boolean result = false;

		try {
			if (getIsInConsequent()) {
				if (isArgumentAString(0, arguments)) {
					URI classURI = P3OWLUtil.getURIFromName(getOWLModel(), getArgumentAsAString(0, arguments));
					if (!getBuiltInBridge().isOWLClass(classURI))
						injectOWLClassDeclarationAxiom(classURI);
				} else
					checkThatArgumentIsAClass(0, arguments);
				result = true;
			} else {
				if (isUnboundArgument) {
					SWRLMultiArgument multiArgument = createMultiArgument();
					for (OWLNamedClass p3OWLNamedClass : P3OWLUtil.getUserDefinedOWLNamedClasses(getOWLModel()))
						multiArgument.addArgument(createClassBuiltInArgumentFromP3OWLNamedClass(p3OWLNamedClass));
					arguments.get(0).setBuiltInResult(multiArgument);
					result = !multiArgument.hasNoArguments();
				} else {
					URI classURI = getArgumentAsAClassURI(0, arguments);
					result = getBuiltInBridge().isOWLClass(classURI);
				}
			}
		} catch (P3OWLUtilException e) {
			throw new BuiltInException(e.getMessage());
		}

		return result;
	}

	/**
	 * Is the single argument an OWL class description.
	 */
	@SuppressWarnings("unused")
	public boolean isClassDescription(List<SWRLBuiltInArgument> arguments) throws BuiltInException
	{
		throw new BuiltInNotImplementedException();
	}

	/**
	 * Is the single argument an rdfs:comment annotation.
	 */
	@SuppressWarnings("unused")
	public boolean isRDFSComment(List<SWRLBuiltInArgument> arguments) throws BuiltInException
	{
		throw new BuiltInNotImplementedException();
	}

	/**
	 * Is the first class argument the complement of the second class argument. If the second argument is unbound, bind it to the complement of the first
	 * argument.
	 */
	@SuppressWarnings("unused")
	public boolean isComplementOf(List<SWRLBuiltInArgument> arguments) throws BuiltInException
	{
		throw new BuiltInNotImplementedException();
	}

	/**
	 * Is the single argument an owl:ComplementOf class description.
	 */
	@SuppressWarnings("unused")
	public boolean isComplementOfDescription(List<SWRLBuiltInArgument> arguments) throws BuiltInException
	{
		throw new BuiltInNotImplementedException();
	}

	/**
	 * Determine if the single argument is an OWL data property. If the argument is unbound, bind it to all OWL datatype properties in an ontology.
	 */
	public boolean isDataProperty(List<SWRLBuiltInArgument> arguments) throws BuiltInException
	{
		checkNumberOfArgumentsEqualTo(1, arguments.size());
		boolean isUnboundArgument = isUnboundArgument(0, arguments);
		boolean result = false;

		try {
			if (isUnboundArgument) {
				SWRLMultiArgument multiArgument = createMultiArgument();
				for (OWLProperty p3OWLProperty : P3OWLUtil.getUserDefinedOWLDatatypeProperties(getOWLModel())) {
					if (p3OWLProperty.isObjectProperty())
						multiArgument.addArgument(createObjectPropertyArgumentFromP3OWLObjectProperty(p3OWLProperty));
					else
						multiArgument.addArgument(createDataPropertyArgumentFromP3OWLDataProperty(p3OWLProperty));
				}
				arguments.get(0).setBuiltInResult(multiArgument);
				result = !multiArgument.hasNoArguments();
			} else {
				URI propertyURI = getArgumentAsAPropertyURI(0, arguments);
				result = P3OWLUtil.isOWLDataProperty(getOWLModel(), propertyURI, false);
			}
		} catch (P3OWLUtilException e) {
			throw new BuiltInException(e.getMessage());
		}

		return result;
	}

	/**
	 * Is the single argument an rdfs:isDefinedBy annotation.
	 */
	@SuppressWarnings("unused")
	public boolean isRDFSIsDefinedBy(List<SWRLBuiltInArgument> arguments) throws BuiltInException
	{
		throw new BuiltInNotImplementedException();
	}

	/**
	 * Is the single argument an owl:DifferentFrom axiom.
	 */
	@SuppressWarnings("unused")
	public boolean isDifferentFromAxiom(List<SWRLBuiltInArgument> arguments) throws BuiltInException
	{
		throw new BuiltInNotImplementedException();
	}

	/**
	 * Check that the second class argument is a direct subclass of the first class argument. If the second argument is unbound, bind it to the direct subclasses
	 * of the first argument (if any exist).
	 */
	public boolean isDirectSubClassOf(List<SWRLBuiltInArgument> arguments) throws BuiltInException
	{
		return isSubClassOf(arguments, false);
	}

	/**
	 * Determine if the second property argument is a direct subproperty of the first property argument. If the second argument is unbound, bind it to the direct
	 * sub properties of the first argument (if any exist).
	 */
	public boolean isDirectSubPropertyOf(List<SWRLBuiltInArgument> arguments) throws BuiltInException
	{
		return isSubPropertyOf(arguments, false);
	}

	/**
	 * Check that the second class argument is a direct superclass of the first class argument. If the second argument is unbound, bind it to the direct
	 * superclasses of the first argument (if any exist).
	 */
	public boolean isDirectSuperClassOf(List<SWRLBuiltInArgument> arguments) throws BuiltInException
	{
		return isSuperClassOf(arguments, false);
	}

	/**
	 * Determine if the second property argument is a direct superproperty of the first property argument. If the second argument is unbound, bind it to the
	 * direct super properties of the first argument (if any exist).
	 */
	public boolean isDirectSuperPropertyOf(List<SWRLBuiltInArgument> arguments) throws BuiltInException
	{
		return isSuperPropertyOf(arguments, false);
	}

	/**
	 * Determine if the two class arguments represent classes or properties that are disjoint with each other. If the second argument is unbound, bind it to the
	 * disjoint classes of the first argument (if any exist).
	 */
	@SuppressWarnings("unused")
	public boolean isDisjointWith(List<SWRLBuiltInArgument> arguments) throws BuiltInException
	{
		throw new BuiltInNotImplementedException();
	}

	/**
	 * Is the single argument an owl:DisjointWith axiom.
	 */
	@SuppressWarnings("unused")
	public boolean isDisjointWithAxiom(List<SWRLBuiltInArgument> arguments) throws BuiltInException
	{
		throw new BuiltInNotImplementedException();
	}

	/**
	 * Is the single argument an enumerated datatype axiom.
	 */
	@SuppressWarnings("unused")
	public boolean isEnumeratedDatatypeAxiom(List<SWRLBuiltInArgument> arguments) throws BuiltInException
	{
		throw new BuiltInNotImplementedException();
	}

	/**
	 * Determine if the two class arguments are equivalent to each other. If the second argument is unbound, bind it to the equivalent classes of the first
	 * argument (if any exist).
	 */
	@SuppressWarnings("unused")
	public boolean isEquivalentClass(List<SWRLBuiltInArgument> arguments) throws BuiltInException
	{
		throw new BuiltInNotImplementedException();
	}

	/**
	 * Is the single argument an owl:EquivalentClass axiom.
	 */
	@SuppressWarnings("unused")
	public boolean isEquivalentClassAxiom(List<SWRLBuiltInArgument> arguments) throws BuiltInException
	{
		throw new BuiltInNotImplementedException();
	}

	/**
	 * Determine if the two property arguments are equivalent to each other. If the second argument is unbound, bind it to the equivalent properties of the first
	 * argument (if any exist).
	 */
	@SuppressWarnings("unused")
	public boolean isEquivalentProperty(List<SWRLBuiltInArgument> arguments) throws BuiltInException
	{
		throw new BuiltInNotImplementedException();
	}

	/**
	 * Is the single argument an owl:EquivalentProperty axiom.
	 */
	@SuppressWarnings("unused")
	public boolean isEquivalentPropertyAxiom(List<SWRLBuiltInArgument> arguments) throws BuiltInException
	{
		throw new BuiltInNotImplementedException();
	}

	/**
	 * Determine if a single property argument is functional.
	 */
	public boolean isFunctionalProperty(List<SWRLBuiltInArgument> arguments) throws BuiltInException
	{
		checkNumberOfArgumentsEqualTo(1, arguments.size());
		URI propertyURI = getArgumentAsAPropertyURI(0, arguments);

		boolean result = false;
		try {
			result = P3OWLUtil.isOWLFunctionalProperty(getOWLModel(), propertyURI, true);
		} catch (P3OWLUtilException e) {
			throw new BuiltInException(e.getMessage());
		}

		return result;
	}

	/**
	 * Is the single argument an owl:functionalProperty axiom.
	 */
	@SuppressWarnings("unused")
	public boolean isFunctionalPropertyAxiom(List<SWRLBuiltInArgument> arguments) throws BuiltInException
	{
		throw new BuiltInNotImplementedException();
	}

	/**
	 * Is the single argument an owl:hasValue restriction
	 */
	@SuppressWarnings("unused")
	public boolean isHasValueRestriction(List<SWRLBuiltInArgument> arguments) throws BuiltInException
	{
		throw new BuiltInNotImplementedException();
	}

	/**
	 * Check that the first class argument is in the domain of the second property argument (excluding its superproperties). If the first argument is unbound and
	 * the second argument is bound, bind the first argument to the domain(s) of the second property argument (if any exist). If the first class argument is bound
	 * and the second argument is unbound, bind the second argument to the properties that have the class in their domain (if any). An error is thrown if both
	 * arguments are unbound.
	 */
	public boolean isInDirectDomainOf(List<SWRLBuiltInArgument> arguments) throws BuiltInException
	{
		return isInDomainOf(arguments, false);
	}

	/**
	 * Check that the first class argument is in the range of the second property argument excluding its superproperties. If the first argument is unbound, bind
	 * it to the range of the second argument (if any exist).
	 */
	public boolean isInDirectRangeOf(List<SWRLBuiltInArgument> arguments) throws BuiltInException
	{
		return isInRangeOf(arguments, false);
	}

	/**
	 * Check that the first class argument is in the domain of the second property argument (including its superproperties). If the first argument is unbound and
	 * the second argument is bound, bind the first argument to the domain(s) of the second property argument (if any exist). If the first class argument is bound
	 * and the second argument is unbound, bind the second argument to the properties that have the class in their domain (if any). An error is thrown if both
	 * arguments are unbound.
	 */
	public boolean isInDomainOf(List<SWRLBuiltInArgument> arguments) throws BuiltInException
	{
		return isInDomainOf(arguments, true);
	}

	/**
	 * Is the single argument an OWL domain axiom.
	 */
	@SuppressWarnings("unused")
	public boolean isInDomainOfAxiom(List<SWRLBuiltInArgument> arguments) throws BuiltInException
	{
		throw new BuiltInNotImplementedException();
	}

	/**
	 * Check that the first class argument is in the range of the second property argument (including its superproperties). If the first argument is unbound, bind
	 * it to the range of the second argument (if any exist).
	 */
	public boolean isInRangeOf(List<SWRLBuiltInArgument> arguments) throws BuiltInException
	{
		return isInRangeOf(arguments, true);
	}

	/**
	 * Is the single argument an OWL range axiom.
	 */
	@SuppressWarnings("unused")
	public boolean isInRangeOfAxiom(List<SWRLBuiltInArgument> arguments) throws BuiltInException
	{
		throw new BuiltInNotImplementedException();
	}

	/**
	 * Is the single argument an owl:InteresctionOf class description.
	 */
	@SuppressWarnings("unused")
	public boolean isIntersectionOfDescription(List<SWRLBuiltInArgument> arguments) throws BuiltInException
	{
		throw new BuiltInNotImplementedException();
	}

	/**
	 * Is the single argument an owl:InverseFunctionalProperty axiom.
	 */
	@SuppressWarnings("unused")
	public boolean isInverseFunctionalPropertyAxiom(List<SWRLBuiltInArgument> arguments) throws BuiltInException
	{
		throw new BuiltInNotImplementedException();
	}

	/**
	 * Determine if the second property argument is the inverse of the first property argument.
	 */
	@SuppressWarnings("unused")
	public boolean isInverseOf(List<SWRLBuiltInArgument> arguments) throws BuiltInException
	{
		throw new BuiltInNotImplementedException();
	}

	/**
	 * Determine if a single property argument is inverse functional.
	 */
	public boolean isInverseFunctionalProperty(List<SWRLBuiltInArgument> arguments) throws BuiltInException
	{
		checkNumberOfArgumentsEqualTo(1, arguments.size());
		URI propertyURI = getArgumentAsAPropertyURI(0, arguments);

		boolean result = false;
		try {
			result = P3OWLUtil.isInverseFunctionalProperty(getOWLModel(), propertyURI, true);
		} catch (P3OWLUtilException e) {
			throw new BuiltInException(e.getMessage());
		}

		return result;
	}

	/**
	 * Is the single argument an owl:inverseOf axiom.
	 */
	@SuppressWarnings("unused")
	public boolean isInverseOfAxiom(List<SWRLBuiltInArgument> arguments) throws BuiltInException
	{
		throw new BuiltInNotImplementedException();
	}

	/**
	 * Is the single argument an rdfs:label annotation.
	 */
	@SuppressWarnings("unused")
	public boolean isRDFSLabelAnnotation(List<SWRLBuiltInArgument> arguments) throws BuiltInException
	{
		throw new BuiltInNotImplementedException();
	}

	/**
	 * Is the single argument an owl:MaxCardinality restriction.
	 */
	@SuppressWarnings("unused")
	public boolean isMaxCardinalityRestriction(List<SWRLBuiltInArgument> arguments) throws BuiltInException
	{
		throw new BuiltInNotImplementedException();
	}

	/**
	 * Is the single argument an owl:MinCardinality restriction.
	 */
	@SuppressWarnings("unused")
	public boolean isMinCardinalityRestriction(List<SWRLBuiltInArgument> arguments) throws BuiltInException
	{
		throw new BuiltInNotImplementedException();
	}

	/**
	 * Determine if a single argument is an OWL object property. If the argument is unbound, bind it to all OWL object properties in an ontology.
	 */
	public boolean isObjectProperty(List<SWRLBuiltInArgument> arguments) throws BuiltInException
	{
		checkNumberOfArgumentsEqualTo(1, arguments.size());
		boolean isUnboundArgument = isUnboundArgument(0, arguments);
		boolean result = false;

		try {
			if (isUnboundArgument) {
				SWRLMultiArgument multiArgument = createMultiArgument();
				for (OWLProperty p3OWLProperty : P3OWLUtil.getUserDefinedOWLObjectProperties(getOWLModel())) {
					if (p3OWLProperty.isObjectProperty())
						multiArgument.addArgument(createObjectPropertyArgumentFromP3OWLObjectProperty(p3OWLProperty));
					else
						multiArgument.addArgument(createDataPropertyArgumentFromP3OWLDataProperty(p3OWLProperty));
				}
				arguments.get(0).setBuiltInResult(multiArgument);
				result = !multiArgument.hasNoArguments();
			} else {
				URI propertyURI = getArgumentAsAPropertyURI(0, arguments);
				result = P3OWLUtil.isOWLObjectProperty(getOWLModel(), propertyURI, false);
			}
		} catch (P3OWLUtilException e) {
			throw new BuiltInException(e.getMessage());
		}
		return result;
	}

	/**
	 * Is the single argument an owl:oneOf class description.
	 */
	@SuppressWarnings("unused")
	public boolean isOneOfClassDescription(List<SWRLBuiltInArgument> arguments) throws BuiltInException
	{
		throw new BuiltInNotImplementedException();
	}

	/**
	 * Is the single argument an owl:Ontology resource. If the argument is unbound, bind it to the current ontology.
	 */
	@SuppressWarnings("unused")
	public boolean isOntology(List<SWRLBuiltInArgument> arguments) throws BuiltInException
	{
		throw new BuiltInNotImplementedException();
	}

	/**
	 * Determine if a single property argument is an OWL object or data property. If the argument is unbound, bind it to all OWL properties in an ontology.
	 */
	public boolean isProperty(List<SWRLBuiltInArgument> arguments) throws BuiltInException
	{
		checkNumberOfArgumentsEqualTo(1, arguments.size());
		boolean isUnboundArgument = isUnboundArgument(0, arguments);
		boolean result = false;

		try {
			if (isUnboundArgument) {
				SWRLMultiArgument multiArgument = createMultiArgument();
				for (OWLProperty p3OWLProperty : P3OWLUtil.getUserDefinedOWLProperties(getOWLModel())) {
					if (p3OWLProperty.isObjectProperty())
						multiArgument.addArgument(createObjectPropertyArgumentFromP3OWLObjectProperty(p3OWLProperty));
					else
						multiArgument.addArgument(createDataPropertyArgumentFromP3OWLDataProperty(p3OWLProperty));
				}
				arguments.get(0).setBuiltInResult(multiArgument);
				result = !multiArgument.hasNoArguments();
			} else {
				URI propertyURI = getArgumentAsAPropertyURI(0, arguments);
				result = P3OWLUtil.isOWLProperty(getOWLModel(), propertyURI, false);
			}
		} catch (P3OWLUtilException e) {
			throw new BuiltInException(e.getMessage());
		}

		return result;
	}

	/**
	 * Is the single argument an rdfs:seeAlso annotation.
	 */
	@SuppressWarnings("unused")
	public boolean isRDFSSeeAlsoAnnotation(List<SWRLBuiltInArgument> arguments) throws BuiltInException
	{
		throw new BuiltInNotImplementedException();
	}

	/**
	 * Is the single argument an owl:SameAs axiom.
	 */
	@SuppressWarnings("unused")
	public boolean isSameAsAxiom(List<SWRLBuiltInArgument> arguments) throws BuiltInException
	{
		throw new BuiltInNotImplementedException();
	}

	/**
	 * Check that the first class argument is a subclass of the second class argument. If the first argument is unbound, bind it to the subclasses of the second
	 * argument (if any exist).
	 */
	public boolean isSubClassOf(List<SWRLBuiltInArgument> arguments) throws BuiltInException
	{
		return isSubClassOf(arguments, true);
	}

	/**
	 * Determine if the first property argument is a subproperty of the second property argument. If the first argument is unbound, bind it to the sub properties
	 * of the second argument (if any exist).
	 */
	public boolean isSubPropertyOf(List<SWRLBuiltInArgument> arguments) throws BuiltInException
	{
		return isSubPropertyOf(arguments, true);
	}

	/**
	 * Check that the first class argument is a superclass of the second class argument. If the first argument is unbound, bind it to the superclasses of the
	 * second argument (if any exist).
	 */
	public boolean isSuperClassOf(List<SWRLBuiltInArgument> arguments) throws BuiltInException
	{
		return isSuperClassOf(arguments, true);
	}

	/**
	 * Determine if the first property argument is a superproperty of the second property argument. If the first argument is unbound, bind it to the super
	 * properties of the second argument (if any exist).
	 */
	public boolean isSuperPropertyOf(List<SWRLBuiltInArgument> arguments) throws BuiltInException
	{
		return isSuperPropertyOf(arguments, true);
	}

	/**
	 * Determine if a single property argument is symmetric.
	 */
	public boolean isSymmetricProperty(List<SWRLBuiltInArgument> arguments) throws BuiltInException
	{
		checkNumberOfArgumentsEqualTo(1, arguments.size());
		URI propertyURI = getArgumentAsAPropertyURI(0, arguments);

		try {
			return P3OWLUtil.isOWLSymmetricProperty(getOWLModel(), propertyURI, true);
		} catch (P3OWLUtilException e) {
			throw new BuiltInException(e.getMessage());
		}
	}

	/**
	 * Is the single argument an owl:SymmetricProperty axiom.
	 */
	@SuppressWarnings("unused")
	public boolean isSymmetricPropertyAxiom(List<SWRLBuiltInArgument> arguments) throws BuiltInException
	{
		throw new BuiltInNotImplementedException();
	}

	/**
	 * Determine if a single property argument is a transitive property.
	 */
	public boolean isTransitiveProperty(List<SWRLBuiltInArgument> arguments) throws BuiltInException
	{
		checkNumberOfArgumentsEqualTo(1, arguments.size());
		URI propertyURI = getArgumentAsAPropertyURI(0, arguments);

		try {
			return P3OWLUtil.isOWLTransitiveProperty(getOWLModel(), propertyURI);
		} catch (P3OWLUtilException e) {
			throw new BuiltInException(e.getMessage());
		}
	}

	/**
	 * Is the single argument an owl:TransitiveProperty axiom.
	 */
	@SuppressWarnings("unused")
	public boolean isTransitivePropertyAxiom(List<SWRLBuiltInArgument> arguments) throws BuiltInException
	{
		throw new BuiltInNotImplementedException();
	}

	public boolean isRestriction(List<SWRLBuiltInArgument> arguments) throws BuiltInException
	{
		checkNumberOfArgumentsEqualTo(1, arguments.size());
		URI classURI = getArgumentAsAClassURI(0, arguments);

		try {
			return P3OWLUtil.isAnonymousResourceName(getOWLModel(), classURI);
		} catch (P3OWLUtilException e) {
			throw new BuiltInException(e.getMessage());
		}
	}

	/**
	 * Is the single argument an owl:UnionOf class description.
	 */
	@SuppressWarnings("unused")
	public boolean isUnionOfDescription(List<SWRLBuiltInArgument> arguments) throws BuiltInException
	{
		throw new BuiltInNotImplementedException();
	}

	/**
	 * Is the single argument an owl:versionInfo annotation.
	 */
	@SuppressWarnings("unused")
	public boolean isVersionInfoAnnotation(List<SWRLBuiltInArgument> arguments) throws BuiltInException
	{
		throw new BuiltInNotImplementedException();
	}

	/**
	 * It the second class description argument a subject of the first axiom argument. If the second argument is unbound, bind it to the the axiom's subject(s).
	 */
	@SuppressWarnings("unused")
	public boolean onDescription(List<SWRLBuiltInArgument> arguments) throws BuiltInException
	{
		throw new BuiltInNotImplementedException();
	}

	/**
	 * It the second individual argument a subject of the first individual axiom argument. If the second argument is unbound, bind it to the individual axiom's
	 * subject(s).
	 */
	@SuppressWarnings("unused")
	public boolean onIndividual(List<SWRLBuiltInArgument> arguments) throws BuiltInException
	{
		throw new BuiltInNotImplementedException();
	}

	/**
	 * Is the second property argument a subject of the first property axiom argument. If the second argument is unbound, bind it to the property axiom's
	 * subject(s).
	 */
	public boolean isSomeValuesFrom(List<SWRLBuiltInArgument> arguments) throws BuiltInException
	{
		boolean result = false;
		URI onPropertyURI, onClassURI;

		checkNumberOfArgumentsEqualTo(3, arguments.size());
		onPropertyURI = getArgumentAsAPropertyURI(1, arguments);

		try {
			if (getIsInConsequent()) {
				OWLObjectSomeValuesFromAdapter someValuesFrom = null;

				if (isArgumentAString(2, arguments))
					onClassURI = P3OWLUtil.getURIFromName(getOWLModel(), getArgumentAsAString(2, arguments));
				else
					onClassURI = getArgumentAsAClassURI(2, arguments);

				if (!getBuiltInBridge().isOWLClass(onClassURI))
					injectOWLClassDeclarationAxiom(onClassURI);

				if (P3OWLUtil.isOWLObjectProperty(getOWLModel(), onPropertyURI)) {
					OWLObjectPropertyAdapter onProperty = getOWLAdapterFactory().getOWLObjectProperty(onPropertyURI);
					OWLClassAdapter onClass = getOWLAdapterFactory().getOWLClass(onClassURI);
					someValuesFrom = getOWLAdapterFactory().getOWLObjectSomeValuesFrom(onProperty, onClass);
				} else { // TODO incomplete
					// OWLDataPropertyReference onProperty = owlConceptReferenceFactory.getOWLDataProperty(onPropertyURI);
					// someValuesFrom = owlConceptReferenceFactory.getOWLDataSomeValuesFrom(onProperty, owlConceptReferenceFactory.getOWLNamedClass(onClassURI));
				}

				// getInvokingBridge().injectOWLAxiom(someValuesFrom); // TODO
				if (someValuesFrom != null)
					throw new BuiltInNotImplementedException();
			} else {
				onClassURI = getArgumentAsAClassURI(2, arguments);
				throw new BuiltInNotImplementedException();
			}
		} catch (P3OWLUtilException e) {
			throw new BuiltInException(e.getMessage());
		}

		return result;
	}

	/**
	 * It the second datatype value argument a subject of the first datatype axiom argument. If the second argument is unbound, bind it to the data value axiom's
	 * subject(s).
	 */
	@SuppressWarnings("unused")
	public boolean onValue(List<SWRLBuiltInArgument> arguments) throws BuiltInException
	{
		throw new BuiltInNotImplementedException();
	}

	/**
	 * Check that the two class or property arguments refer to the same underlying entity.
	 */
	public boolean equalTo(List<SWRLBuiltInArgument> arguments) throws BuiltInException
	{
		checkNumberOfArgumentsEqualTo(2, arguments.size());

		URI resourceURI1 = getArgumentAsAURI(0, arguments);
		URI resourceURI2 = getArgumentAsAURI(1, arguments);

		return resourceURI1.equals(resourceURI2); // According to RDF specification URI names must character (and case) match.
	}

	/**
	 * Check that the two class or property arguments do not refer to the same underlying entity.
	 */
	public boolean notEqualTo(List<SWRLBuiltInArgument> arguments) throws BuiltInException
	{
		return !equalTo(arguments);
	}

	private boolean isSuperClassOf(List<SWRLBuiltInArgument> arguments, boolean transitive) throws BuiltInException
	{
		boolean superClassArgumentUnbound = false;
		URI classURI;
		boolean result = false;

		checkNumberOfArgumentsEqualTo(2, arguments.size());

		superClassArgumentUnbound = isUnboundArgument(0, arguments);
		classURI = getArgumentAsAClassURI(1, arguments);

		try {
			if (superClassArgumentUnbound) {
				List<OWLNamedClass> p3OWLSuperClasses;
				if (transitive)
					p3OWLSuperClasses = P3OWLUtil.getSuperClassesOf(getOWLModel(), classURI);
				else
					p3OWLSuperClasses = P3OWLUtil.getDirectSuperClassesOf(getOWLModel(), classURI);
				if (!p3OWLSuperClasses.isEmpty()) {
					SWRLMultiArgument multiArgument = createMultiArgument();
					for (OWLNamedClass p3OWLSuperClass : p3OWLSuperClasses)
						multiArgument.addArgument(createClassBuiltInArgumentFromP3OWLNamedClass(p3OWLSuperClass));
					arguments.get(0).setBuiltInResult(multiArgument);
					result = !multiArgument.hasNoArguments();
				}
			} else {
				URI superClassURI = getArgumentAsAClassURI(0, arguments);
				if (transitive)
					result = P3OWLUtil.isOWLSuperClassOf(getOWLModel(), superClassURI, classURI, true);
				else
					result = P3OWLUtil.isOWLDirectSuperClassOf(getOWLModel(), superClassURI, classURI, true);
			}
		} catch (P3OWLUtilException e) {
			throw new BuiltInException(e.getMessage());
		}

		return result;
	}

	private boolean isSubClassOf(List<SWRLBuiltInArgument> arguments, boolean transitive) throws BuiltInException
	{
		boolean subClassArgumentUnbound = false;
		URI classURI;
		boolean result = false;

		checkNumberOfArgumentsEqualTo(2, arguments.size());

		subClassArgumentUnbound = isUnboundArgument(0, arguments);

		try {
			if (getIsInConsequent()) {
				OWLObjectAdapterFactory owlConceptReferenceFactory = getBuiltInBridge().getOWLAdapterFactory();
				URI superClassURI;

				if (isArgumentAString(1, arguments))
					superClassURI = P3OWLUtil.getURIFromName(getOWLModel(), getArgumentAsAString(1, arguments));
				else
					superClassURI = getArgumentAsAClassURI(1, arguments);

				if (!getBuiltInBridge().isOWLClass(superClassURI))
					injectOWLClassDeclarationAxiom(superClassURI);

				if (isArgumentAString(0, arguments))
					classURI = P3OWLUtil.getURIFromName(getOWLModel(), getArgumentAsAString(0, arguments));
				else
					classURI = getArgumentAsAClassURI(0, arguments);

				getBuiltInBridge().injectOWLAxiom(
						owlConceptReferenceFactory.getOWLSubClassOfAxiom(owlConceptReferenceFactory.getOWLClass(classURI),
								owlConceptReferenceFactory.getOWLClass(superClassURI)));

				result = true;
			} else {
				classURI = getArgumentAsAClassURI(1, arguments);

				if (subClassArgumentUnbound) {
					List<OWLNamedClass> p3OWLSubClasses;
					if (transitive)
						p3OWLSubClasses = P3OWLUtil.getSubClassesOf(getOWLModel(), classURI);
					else
						p3OWLSubClasses = P3OWLUtil.getDirectSubClassesOf(getOWLModel(), classURI);
					if (!p3OWLSubClasses.isEmpty()) {
						SWRLMultiArgument multiArgument = createMultiArgument();
						for (edu.stanford.smi.protegex.owl.model.OWLNamedClass p3OWLSubClass : p3OWLSubClasses)
							multiArgument.addArgument(createClassBuiltInArgumentFromP3OWLNamedClass(p3OWLSubClass));
						arguments.get(0).setBuiltInResult(multiArgument);
						result = !multiArgument.hasNoArguments();
					}
				} else {
					URI subClassURI = getArgumentAsAClassURI(0, arguments);
					if (transitive)
						result = P3OWLUtil.isOWLSubClassOf(getOWLModel(), subClassURI, classURI, true);
					else
						result = P3OWLUtil.isOWLDirectSubClassOf(getOWLModel(), subClassURI, classURI, true);
				}
			}
		} catch (P3OWLUtilException e) {
			throw new BuiltInException(e.getMessage());
		}

		return result;
	}

	private boolean isSubPropertyOf(List<SWRLBuiltInArgument> arguments, boolean transitive) throws BuiltInException
	{
		boolean subPropertyArgumentUnbound = false;
		URI propertyURI;
		boolean result = false;

		checkNumberOfArgumentsEqualTo(2, arguments.size());
		subPropertyArgumentUnbound = isUnboundArgument(0, arguments);
		propertyURI = getArgumentAsAPropertyURI(1, arguments);

		try {
			if (subPropertyArgumentUnbound) {
				Set<OWLProperty> p3OWLSubProperties;
				if (transitive)
					p3OWLSubProperties = P3OWLUtil.getSubPropertiesOf(getOWLModel(), propertyURI);
				else
					p3OWLSubProperties = P3OWLUtil.getDirectSubPropertiesOf(getOWLModel(), propertyURI);
				if (!p3OWLSubProperties.isEmpty()) {
					SWRLMultiArgument multiArgument = createMultiArgument();
					for (edu.stanford.smi.protegex.owl.model.OWLProperty p3OWLSubProperty : p3OWLSubProperties) {
						if (p3OWLSubProperty.isObjectProperty())
							multiArgument.addArgument(createObjectPropertyArgumentFromP3OWLObjectProperty(p3OWLSubProperty));
						else
							multiArgument.addArgument(createDataPropertyArgumentFromP3OWLDataProperty(p3OWLSubProperty));
					}
					arguments.get(0).setBuiltInResult(multiArgument);
					result = !multiArgument.hasNoArguments();
				}
			} else {
				URI subPropertyURI = getArgumentAsAPropertyURI(0, arguments);
				if (transitive)
					result = P3OWLUtil.isOWLSubPropertyOf(getOWLModel(), subPropertyURI, propertyURI, true);
				else
					result = P3OWLUtil.isOWLDirectSubPropertyOf(getOWLModel(), subPropertyURI, propertyURI, true);
			}
		} catch (P3OWLUtilException e) {
			throw new BuiltInException(e.getMessage());
		}

		return result;
	}

	private boolean isSuperPropertyOf(List<SWRLBuiltInArgument> arguments, boolean transitive) throws BuiltInException
	{
		boolean superPropertyArgumentUnbound = false;
		URI propertyURI;
		boolean result = false;

		checkNumberOfArgumentsEqualTo(2, arguments.size());
		superPropertyArgumentUnbound = isUnboundArgument(0, arguments);
		propertyURI = getArgumentAsAPropertyURI(1, arguments);

		try {
			if (superPropertyArgumentUnbound) {
				Set<OWLProperty> p3OWLSuperProperties;
				if (transitive)
					p3OWLSuperProperties = P3OWLUtil.getSuperPropertiesOf(getOWLModel(), propertyURI);
				else
					p3OWLSuperProperties = P3OWLUtil.getDirectSuperPropertiesOf(getOWLModel(), propertyURI);
				if (!p3OWLSuperProperties.isEmpty()) {
					SWRLMultiArgument multiArgument = createMultiArgument();
					for (edu.stanford.smi.protegex.owl.model.OWLProperty p3OWLSuperProperty : p3OWLSuperProperties) {
						if (p3OWLSuperProperty.isObjectProperty())
							multiArgument.addArgument(createObjectPropertyArgumentFromP3OWLObjectProperty(p3OWLSuperProperty));
						else
							multiArgument.addArgument(createDataPropertyArgumentFromP3OWLDataProperty(p3OWLSuperProperty));
					}
					arguments.get(0).setBuiltInResult(multiArgument);
					result = !multiArgument.hasNoArguments();
				}
			} else {
				URI superPropertyURI = getArgumentAsAPropertyURI(0, arguments);
				if (transitive)
					result = P3OWLUtil.isOWLSuperPropertyOf(getOWLModel(), superPropertyURI, propertyURI, true);
				else
					result = P3OWLUtil.isOWLDirectSuperPropertyOf(getOWLModel(), superPropertyURI, propertyURI, true);
			}
		} catch (P3OWLUtilException e) {
			throw new BuiltInException(e.getMessage());
		}

		return result;
	}

	private boolean isInDomainOf(List<SWRLBuiltInArgument> arguments, boolean includingSuperproperties) throws BuiltInException
	{
		boolean domainClassArgumentUnbound, propertyArgumentUnbound = false;
		URI propertyURI, domainClassURI;
		boolean result = false;

		checkNumberOfArgumentsEqualTo(2, arguments.size());

		domainClassArgumentUnbound = isUnboundArgument(0, arguments);
		propertyArgumentUnbound = isUnboundArgument(1, arguments);

		if (domainClassArgumentUnbound && propertyArgumentUnbound)
			throw new BuiltInException("at least one argument must be bound");

		try {
			if (domainClassArgumentUnbound) {
				Set<OWLNamedClass> p3OWLDomainClasses;
				propertyURI = getArgumentAsAPropertyURI(1, arguments);
				if (includingSuperproperties)
					p3OWLDomainClasses = P3OWLUtil.getOWLDomainClasses(getOWLModel(), propertyURI);
				else
					p3OWLDomainClasses = P3OWLUtil.getDirectOWLDomainClasses(getOWLModel(), propertyURI);
				if (!p3OWLDomainClasses.isEmpty()) {
					SWRLMultiArgument multiArgument = createMultiArgument();
					for (OWLNamedClass p3OWLDomainClass : p3OWLDomainClasses)
						multiArgument.addArgument(createClassBuiltInArgumentFromP3OWLNamedClass(p3OWLDomainClass));
					arguments.get(0).setBuiltInResult(multiArgument);
					result = !multiArgument.hasNoArguments();
				}
			} else if (propertyArgumentUnbound) {
				Set<OWLProperty> p3OWLDomainProperties;
				domainClassURI = getArgumentAsAClassURI(0, arguments);
				p3OWLDomainProperties = P3OWLUtil.getDomainProperties(getOWLModel(), domainClassURI, includingSuperproperties);
				if (!p3OWLDomainProperties.isEmpty()) {
					SWRLMultiArgument multiArgument = createMultiArgument();
					for (edu.stanford.smi.protegex.owl.model.OWLProperty p3OWLDomainProperty : p3OWLDomainProperties) {
						if (p3OWLDomainProperty.isObjectProperty())
							multiArgument.addArgument(createObjectPropertyArgumentFromP3OWLObjectProperty(p3OWLDomainProperty));
						else
							multiArgument.addArgument(createDataPropertyArgumentFromP3OWLDataProperty(p3OWLDomainProperty));
					}
					arguments.get(1).setBuiltInResult(multiArgument);
					result = !multiArgument.hasNoArguments();
				}
			} else { // Both arguments bound
				domainClassURI = getArgumentAsAClassURI(0, arguments);
				propertyURI = getArgumentAsAPropertyURI(1, arguments);
				if (includingSuperproperties)
					result = P3OWLUtil.isInOWLPropertyDomain(getOWLModel(), propertyURI, domainClassURI, true);
				else
					result = P3OWLUtil.isInDirectOWLPropertyDomain(getOWLModel(), propertyURI, domainClassURI, true);
			}
		} catch (P3OWLUtilException e) {
			throw new BuiltInException(e.getMessage());
		}

		return result;
	}

	private boolean isInRangeOf(List<SWRLBuiltInArgument> arguments, boolean includingSuperproperties) throws BuiltInException
	{
		boolean rangeClassArgumentUnbound = false;
		URI propertyURI;
		boolean result = false;

		checkNumberOfArgumentsEqualTo(2, arguments.size());

		rangeClassArgumentUnbound = isUnboundArgument(0, arguments);
		propertyURI = getArgumentAsAPropertyURI(1, arguments);

		try {
			if (rangeClassArgumentUnbound) {
				Set<OWLNamedClass> p3OWLRangeClasses;
				if (includingSuperproperties)
					p3OWLRangeClasses = P3OWLUtil.getOWLRangeClasses(getOWLModel(), propertyURI);
				else
					p3OWLRangeClasses = P3OWLUtil.getOWLDirectRangeClasses(getOWLModel(), propertyURI);
				if (!p3OWLRangeClasses.isEmpty()) {
					SWRLMultiArgument multiArgument = createMultiArgument();
					for (OWLNamedClass p3OWLRangeClass : p3OWLRangeClasses)
						multiArgument.addArgument(createClassBuiltInArgumentFromP3OWLNamedClass(p3OWLRangeClass));
					arguments.get(0).setBuiltInResult(multiArgument);
					result = !multiArgument.hasNoArguments();
				}
			} else {
				URI rangeClassURI = getArgumentAsAClassURI(0, arguments);
				propertyURI = getArgumentAsAPropertyURI(1, arguments);
				if (includingSuperproperties)
					result = P3OWLUtil.isInPropertyRange(getOWLModel(), propertyURI, rangeClassURI, true);
				else
					result = P3OWLUtil.isInDirectPropertyRange(getOWLModel(), propertyURI, rangeClassURI, true);
			}
		} catch (P3OWLUtilException e) {
			throw new BuiltInException(e.getMessage());
		}

		return result;
	}

	private SWRLClassBuiltInArgument createClassBuiltInArgumentFromP3OWLNamedClass(OWLNamedClass p3OWLNamedClass) throws BuiltInException
	{
		return createClassArgument(getURI(p3OWLNamedClass), p3OWLNamedClass.getPrefixedName());
	}

	private SWRLObjectPropertyBuiltInArgument createObjectPropertyArgumentFromP3OWLObjectProperty(OWLProperty p3OWLProperty) throws BuiltInException
	{
		if (!p3OWLProperty.isObjectProperty())
			throw new BuiltInException("trying to convert non data property " + p3OWLProperty.getPrefixedName() + " to object property argument");

		return createObjectPropertyArgument(getURI(p3OWLProperty), p3OWLProperty.getPrefixedName());
	}

	private SWRLDataPropertyBuiltInArgument createDataPropertyArgumentFromP3OWLDataProperty(OWLProperty p3OWLProperty) throws BuiltInException
	{
		if (p3OWLProperty.isObjectProperty())
			throw new BuiltInException("trying to convert object property " + p3OWLProperty.getPrefixedName() + " to data property argument");

		return createDataPropertyArgument(getURI(p3OWLProperty), p3OWLProperty.getPrefixedName());
	}

	private URI getURI(RDFResource p3RDFResource) throws BuiltInException
	{
		if (p3RDFResource == null)
			throw new BuiltInException("internal error: null resources passed to getURI");

		try {
			return new URI(p3RDFResource.getURI());
		} catch (URISyntaxException e) {
			throw new BuiltInException("error converting " + p3RDFResource.getURI() + " to URI: " + e.getMessage());
		}
	}

	private void injectOWLClassDeclarationAxiom(URI classURI) throws SWRLBuiltInBridgeException
	{
		OWLClassAdapter cls = getOWLAdapterFactory().getOWLClass(classURI);
		OWLClassDeclarationAxiomAdapter declarationAxiom = getOWLAdapterFactory().getOWLClassDeclarationAxiom(cls);
		getBuiltInBridge().injectOWLAxiom(declarationAxiom);

	}
	
	private OWLModel getOWLModel()
	{
		return null;
	}

}
