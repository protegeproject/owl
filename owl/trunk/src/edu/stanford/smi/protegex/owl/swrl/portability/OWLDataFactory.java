
package edu.stanford.smi.protegex.owl.swrl.portability;

import java.util.List;
import java.util.Set;

import edu.stanford.smi.protegex.owl.swrl.bridge.BuiltInArgument;
import edu.stanford.smi.protegex.owl.swrl.bridge.OWLPropertyPropertyAssertionAxiomReference;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.OWLFactoryException;

/**
 * Factory to create OWLAPI-like entities. Provides a rough starting point for a port to the OWLAPI.
 */
public interface OWLDataFactory
{
	Set<SWRLRuleReference> getSWRLRules() throws OWLFactoryException;

	SWRLRuleReference getSWRLRule(String ruleName) throws OWLFactoryException;

	SWRLBuiltInAtomReference getSWRLBuiltInAtom(String builtInURI, String builtInPrefixedName, List<BuiltInArgument> arguments);

	OWLClassReference getOWLClass(String classURI);

	OWLNamedIndividualReference getOWLIndividual(String individualURI);

	OWLObjectPropertyReference getOWLObjectProperty(String propertyURI);

	OWLDataPropertyReference getOWLDataProperty(String propertyURI);

	OWLDataPropertyAssertionAxiomReference getOWLDataPropertyAssertionAxiom(OWLNamedIndividualReference subject, OWLPropertyReference property,
																																					OWLLiteralReference object);

	OWLObjectPropertyAssertionAxiomReference getOWLObjectPropertyAssertionAxiom(OWLNamedIndividualReference subject, OWLPropertyReference property,
																																							OWLNamedIndividualReference object);

	OWLDifferentIndividualsAxiomReference getOWLDifferentIndividualsAxiom(OWLNamedIndividualReference individual1, OWLNamedIndividualReference individual2);

	OWLDifferentIndividualsAxiomReference getOWLDifferentIndividualsAxiom(Set<OWLNamedIndividualReference> individuals);

	OWLSameIndividualAxiomReference getOWLSameIndividualAxiom(OWLNamedIndividualReference individual1, OWLNamedIndividualReference individual2);

	OWLClassAssertionAxiomReference getOWLClassAssertionAxiom(OWLNamedIndividualReference individual, OWLClassReference description);

	OWLSubClassAxiomReference getOWLSubClassAxiom(OWLClassReference subClass, OWLClassReference superClass);

	OWLSomeValuesFromReference getOWLSomeValuesFrom(OWLClassReference owlClass, OWLPropertyReference onProperty, OWLClassReference someValuesFrom);

	OWLDeclarationAxiomReference getOWLDeclarationAxiom(OWLEntityReference owlEntity);

	OWLTypedLiteralReference getOWLTypedLiteral(int value);

	OWLTypedLiteralReference getOWLTypedLiteral(float value);

	OWLTypedLiteralReference getOWLTypedLiteral(double value);

	OWLTypedLiteralReference getOWLTypedLiteral(boolean value);

	OWLTypedLiteralReference getOWLTypedLiteral(String value);

	// The following do not have corresponding methods in the OWLAPI.
	OWLClassPropertyAssertionAxiomReference getOWLClassPropertyAssertionAxiom(OWLNamedIndividualReference subject, OWLPropertyReference property,
																																						OWLClassReference object);

	OWLPropertyPropertyAssertionAxiomReference getOWLPropertyPropertyAssertionAxiom(OWLNamedIndividualReference subject, OWLPropertyReference property,
																																									OWLPropertyReference object);
}
