
package edu.stanford.smi.protegex.owl.swrl.portability;

import java.util.Set;

import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.BuiltInException;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.OWLConversionFactoryException;
import edu.stanford.smi.protegex.owl.swrl.parser.SWRLParseException;
import edu.stanford.smi.protegex.owl.swrl.sqwrl.exceptions.DataValueConversionException;
import edu.stanford.smi.protegex.owl.swrl.sqwrl.exceptions.SQWRLException;

/**
 * Interface that is very roughly equivalent to the OWLAPI interfaces OWLOntology and OWLOntologyManager. All SWRLTab code in Protege-OWL will eventually use
 * this interface to interact with an OWL ontology, thus easing the port to the OWLAPI.
 * 
 * At present, this interface is not fully aligned with the OWLAPI.
 */
public interface OWLOntology
{
	boolean containsClassInSignature(String classURI, boolean includeImportsClosure);

	boolean containsObjectPropertyInSignature(String propertyURI, boolean includeImportsClosure);

	boolean containsDataPropertyInSignature(String propertyURI, boolean includeImportsClosure);

	boolean containsIndividualInSignature(String individualURI, boolean includeImportsClosure);

	Set<OWLSameIndividualAxiomReference> getSameIndividualAxioms() throws OWLConversionFactoryException;

	Set<OWLDifferentIndividualsAxiomReference> getOWLDifferentIndividualsAxioms() throws OWLConversionFactoryException;

	// Considers sub property and equivalence relationships
	Set<OWLPropertyAssertionAxiomReference> getOWLPropertyAssertionAxioms(String individualURI, String propertyURI)
		throws OWLConversionFactoryException, DataValueConversionException;

	Set<OWLPropertyAssertionAxiomReference> getOWLPropertyAssertionAxioms(String propertyURI) throws OWLConversionFactoryException, DataValueConversionException;

	SWRLRuleReference getSWRLRule(String ruleName) throws OWLConversionFactoryException;

	OWLClassReference getOWLClass(String classURI) throws OWLConversionFactoryException;

	OWLNamedIndividualReference getOWLIndividual(String individualURI) throws OWLConversionFactoryException;

	OWLObjectPropertyReference getOWLObjectProperty(String propertyURI) throws OWLConversionFactoryException;

	OWLDataPropertyReference getOWLDataProperty(String propertyURI) throws OWLConversionFactoryException;

	Set<OWLNamedIndividualReference> getAllOWLIndividualsOfClass(String classURI) throws OWLConversionFactoryException;

	Set<SWRLRuleReference> getSWRLRules() throws OWLConversionFactoryException, SQWRLException, BuiltInException;

	boolean isOWLNamedIndividualOfClass(String individualURI, String classURI);

	boolean isSWRLBuiltIn(String builtInURI);

	boolean isOWLNamedClass(String classURI);

	boolean isValidURI(String uri);

	String uri2PrefixedName(String uri);

	String prefixedName2URI(String prefixedName);

	// Write methods
	void writeOWLClassDeclaration(OWLClassReference owlClass) throws OWLConversionFactoryException;

	void writeOWLIndividualDeclaration(OWLNamedIndividualReference owlIndividual) throws OWLConversionFactoryException;

	void writeOWLAxiom(OWLAxiomReference axiom) throws OWLConversionFactoryException;

	// Creation methods
	OWLClassReference createOWLClass();

	String createNewResourceURI(String prefix);

	SWRLRuleReference createSWRLRule(String ruleName, String ruleText) throws OWLConversionFactoryException, SWRLParseException;

	void deleteSWRLRule(String ruleURI) throws OWLConversionFactoryException;

	OWLModel getOWLModel(); // TODO: Protege-OWL dependency
}
