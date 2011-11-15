
package edu.stanford.smi.protegex.owl.swrl.portability.p3;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.stanford.smi.protegex.owl.swrl.bridge.BuiltInArgument;
import edu.stanford.smi.protegex.owl.swrl.bridge.OWLPropertyPropertyAssertionAxiomReference;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.BuiltInException;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.OWLConversionFactoryException;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.OWLFactoryException;
import edu.stanford.smi.protegex.owl.swrl.bridge.impl.OWLDataValueImpl;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLClassAssertionAxiomReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLClassPropertyAssertionAxiomReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLClassReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLDataFactory;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLDataPropertyReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLDataPropertyAssertionAxiomReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLDeclarationAxiomReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLDifferentIndividualsAxiomReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLEntityReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLLiteralReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLNamedIndividualReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLObjectPropertyReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLObjectPropertyAssertionAxiomReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLOntology;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLPropertyReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLSameIndividualAxiomReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLSomeValuesFromReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLSubClassAxiomReference;
import edu.stanford.smi.protegex.owl.swrl.portability.OWLTypedLiteralReference;
import edu.stanford.smi.protegex.owl.swrl.portability.SWRLBuiltInAtomReference;
import edu.stanford.smi.protegex.owl.swrl.portability.SWRLRuleReference;
import edu.stanford.smi.protegex.owl.swrl.sqwrl.exceptions.SQWRLException;

public class P3OWLDataFactory implements OWLDataFactory
{
	private Map<String, OWLClassReference> classes = new HashMap<String, OWLClassReference>();
	private Map<String, OWLNamedIndividualReference> individuals = new HashMap<String, OWLNamedIndividualReference>();
	private Map<String, OWLObjectPropertyReference> objectProperties = new HashMap<String, OWLObjectPropertyReference>();
	private Map<String, OWLDataPropertyReference> dataProperties = new HashMap<String, OWLDataPropertyReference>();

	private OWLOntology activeOntology;

	public P3OWLDataFactory()
	{
		activeOntology = null;
	}

	public P3OWLDataFactory(OWLOntology activeOntology)
	{
		this.activeOntology = activeOntology;
	}

	public Set<SWRLRuleReference> getSWRLRules() throws OWLFactoryException
	{
		Set<SWRLRuleReference> result = new HashSet<SWRLRuleReference>();

		if (hasActiveOntology()) {
			try {
				result = activeOntology.getSWRLRules();
			} catch (OWLConversionFactoryException e) {
				throw new OWLFactoryException("conversion exception getting SWRL rule or SQWRL query: " + e.getMessage());
			} catch (SQWRLException e) {
				throw new OWLFactoryException("SQWRL exception getting query: " + e.getMessage());
			} catch (BuiltInException e) {
				throw new OWLFactoryException("built-in exception getting SWRL rule or SQWRL query: " + e.getMessage());
			} // try
		} // if

		return result;
	}

	public SWRLRuleReference getSWRLRule(String ruleName) throws OWLFactoryException
	{
		SWRLRuleReference result = null;

		if (hasActiveOntology()) {
			try {
				result = activeOntology.getSWRLRule(ruleName);
			} catch (OWLConversionFactoryException e) {
				throw new OWLFactoryException("conversion exception getting SWRL rule or SQWRL query: " + e.getMessage());
			} // try
		} // if

		return result;
	}

	// Basic OWL entities

	public OWLClassReference getOWLClass(String classURI)
	{
		OWLClassReference owlClass = null;

		if (classes.containsKey(classURI))
			owlClass = classes.get(classURI);
		else {
			if (hasActiveOntology()) {
				try {
					owlClass = activeOntology.getOWLClass(classURI);
				} catch (OWLConversionFactoryException e) {
					owlClass = new P3OWLClassReference(classURI);
				} // try
			} else
				owlClass = new P3OWLClassReference(classURI);
			classes.put(classURI, owlClass);
		} // if

		return owlClass;
	}

	public OWLNamedIndividualReference getOWLIndividual(String individualURI)
	{
		OWLNamedIndividualReference owlIndividual = null;

		if (individuals.containsKey(individualURI))
			owlIndividual = individuals.get(individualURI);
		else {
			if (hasActiveOntology()) {
				try {
					owlIndividual = activeOntology.getOWLIndividual(individualURI);
				} catch (OWLConversionFactoryException e) {
					owlIndividual = new P3OWLNamedIndividualReference(individualURI);
				} // try
			} else
				owlIndividual = new P3OWLNamedIndividualReference(individualURI);
			individuals.put(individualURI, owlIndividual);
		} // if

		return owlIndividual;
	}

	public OWLObjectPropertyReference getOWLObjectProperty(String propertyURI)
	{
		OWLObjectPropertyReference property;

		if (objectProperties.containsKey(propertyURI))
			property = objectProperties.get(propertyURI);
		else {
			if (hasActiveOntology()) {
				try {
					property = activeOntology.getOWLObjectProperty(propertyURI);
				} catch (OWLConversionFactoryException e) {
					property = new P3OWLObjectPropertyReference(propertyURI);
				} // try
			} else
				property = new P3OWLObjectPropertyReference(propertyURI);
			objectProperties.put(propertyURI, property);
		} // if

		return property;
	} // getOWLObjectProperty

	public OWLDataPropertyReference getOWLDataProperty(String propertyURI)
	{
		OWLDataPropertyReference property;

		if (dataProperties.containsKey(propertyURI))
			property = dataProperties.get(propertyURI);
		else {
			if (hasActiveOntology()) {
				try {
					property = activeOntology.getOWLDataProperty(propertyURI);
				} catch (OWLConversionFactoryException e) {
					property = new P3OWLDataPropertyReference(propertyURI);
				} // try
			} else
				property = new P3OWLDataPropertyReference(propertyURI);
			dataProperties.put(propertyURI, property);
		} // if

		return property;
	} // getOWLDataProperty

	// OWL axioms
	public OWLDataPropertyAssertionAxiomReference getOWLDataPropertyAssertionAxiom(OWLNamedIndividualReference subject, OWLPropertyReference property,
																																									OWLLiteralReference object)
	{
		return new P3OWLDatatypePropertyAssertionAxiom(subject, property, object);
	}

	public OWLObjectPropertyAssertionAxiomReference getOWLObjectPropertyAssertionAxiom(OWLNamedIndividualReference subject, OWLPropertyReference property,
																																											OWLNamedIndividualReference object)
	{
		return new P3OWLObjectPropertyAssertionAxiomReference(subject, property, object);
	}

	public OWLDifferentIndividualsAxiomReference getOWLDifferentIndividualsAxiom(OWLNamedIndividualReference individual1, OWLNamedIndividualReference individual2)
	{
		return new P3OWLDifferentIndividualsAxiomReference(individual1, individual2);
	}

	public OWLDifferentIndividualsAxiomReference getOWLDifferentIndividualsAxiom(Set<OWLNamedIndividualReference> individuals)
	{
		return new P3OWLDifferentIndividualsAxiomReference(individuals);
	}

	public OWLSameIndividualAxiomReference getOWLSameIndividualAxiom(OWLNamedIndividualReference individual1, OWLNamedIndividualReference individual2)
	{
		return new P3OWLSameIndividualAxiomReference(individual1, individual2);
	}

	public OWLClassPropertyAssertionAxiomReference getOWLClassPropertyAssertionAxiom(OWLNamedIndividualReference subject, OWLPropertyReference property,
																																										OWLClassReference object)
	{
		return new P3OWLClassPropertyAssertionAxiomReference(subject, property, object);
	} // OWL Full

	public OWLPropertyPropertyAssertionAxiomReference getOWLPropertyPropertyAssertionAxiom(OWLNamedIndividualReference subject, OWLPropertyReference property,
																																													OWLPropertyReference object)
	{
		return new P3OWLPropertyPropertyAssertionAxiomReference(subject, property, object);
	} // OWL Full

	public OWLClassAssertionAxiomReference getOWLClassAssertionAxiom(OWLNamedIndividualReference individual, OWLClassReference description)
	{
		return new P3OWLClassAssertionAxiomReference(individual, description);
	} // TODO: should be OWLDescription

	public OWLSubClassAxiomReference getOWLSubClassAxiom(OWLClassReference subClass, OWLClassReference superClass)
	{
		return new P3OWLSubClassAxiomReference(subClass, superClass);
	} // TODO: should be OWLDescription

	public OWLDeclarationAxiomReference getOWLDeclarationAxiom(OWLEntityReference owlEntity)
	{
		return new P3OWLDeclarationAxiomReference(owlEntity);
	}

	public OWLSomeValuesFromReference getOWLSomeValuesFrom(OWLClassReference owlClass, OWLPropertyReference onProperty, OWLClassReference someValuesFrom)
	{
		return new P3OWLSomeValuesFromReference(owlClass, onProperty, someValuesFrom);
	} 

	public SWRLBuiltInAtomReference getSWRLBuiltInAtom(String builtInURI, String builtInPrefixedName, List<BuiltInArgument> arguments)
	{
		return new P3SWRLBuiltInAtomReference(builtInURI, builtInPrefixedName, arguments);
	}

	public OWLTypedLiteralReference getOWLTypedLiteral(int value)
	{
		return new OWLDataValueImpl(value);
	}

	public OWLTypedLiteralReference getOWLTypedLiteral(float value)
	{
		return new OWLDataValueImpl(value);
	}

	public OWLTypedLiteralReference getOWLTypedLiteral(double value)
	{
		return new OWLDataValueImpl(value);
	}

	public OWLTypedLiteralReference getOWLTypedLiteral(boolean value)
	{
		return new OWLDataValueImpl(value);
	}

	public OWLTypedLiteralReference getOWLTypedLiteral(String value)
	{
		return new OWLDataValueImpl(value);
	}

	private boolean hasActiveOntology()
	{
		return activeOntology != null;
	}
}
