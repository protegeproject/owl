
package edu.stanford.smi.protegex.owl.swrl.bridge;

import java.util.Set;

import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.BuiltInException;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.DataValueConversionException;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.OWLConversionFactoryException;
import edu.stanford.smi.protegex.owl.swrl.parser.SWRLParseException;
import edu.stanford.smi.protegex.owl.swrl.sqwrl.exceptions.SQWRLException;

/**
 * Interface to insert and get OWLAPI-like entities into and from a Protege-OWL model.
 */
public interface OWLConversionFactory
{
  OWLClass getOWLClass();
  OWLClass getOWLClass(String classURI) throws OWLConversionFactoryException;
  OWLIndividual getOWLIndividual(String individualURI) throws OWLConversionFactoryException;
  OWLObjectProperty getOWLObjectProperty(String propertyURI) throws OWLConversionFactoryException;
  OWLDataProperty getOWLDataProperty(String propertyURI) throws OWLConversionFactoryException;
  SWRLRule getSWRLRule(String ruleName) throws OWLConversionFactoryException, SQWRLException, BuiltInException;
  SWRLRule createSWRLRule(String ruleName, String ruleText) throws OWLConversionFactoryException, SQWRLException, SWRLParseException, BuiltInException;
  Set<SWRLRule> getSWRLRules() throws OWLConversionFactoryException, SQWRLException, BuiltInException;
  Set<OWLIndividual> getAllOWLIndividualsOfClass(String classURI) throws OWLConversionFactoryException;
  
  Set<OWLPropertyAssertionAxiom> getOWLPropertyAssertionAxioms(String individualURI, String propertyURI) throws OWLConversionFactoryException, DataValueConversionException;
  Set<OWLPropertyAssertionAxiom> getOWLPropertyAssertionAxioms(String propertyURI) throws OWLConversionFactoryException, DataValueConversionException;
  Set<OWLSameIndividualAxiom> getOWLSameIndividualAxioms() throws OWLConversionFactoryException;
  Set<OWLDifferentIndividualsAxiom> getOWLDifferentIndividualsAxioms() throws OWLConversionFactoryException;

  boolean isOWLClass(String classURI);
  boolean isOWLProperty(String propertyURI);
  boolean isOWLObjectProperty(String propertyURI);
  boolean isOWLDataProperty(String propertyURI);
  boolean isOWLIndividual(String individualURI);
  boolean isOWLIndividualOfClass(String individualURI, String classURI);
  
  boolean isSWRLBuiltIn(String builtInURI);
  boolean couldBeOWLNamedClass(String classURI);
  String createNewResourceName(String prefix);
  boolean isValidURI(String uri);
  void putOWLClass(OWLClass owlClass) throws OWLConversionFactoryException;
  void putOWLIndividual(OWLIndividual owlIndividual) throws OWLConversionFactoryException;
  void putOWLAxiom(OWLAxiom axiom) throws OWLConversionFactoryException;
  
  String uri2PrefixedName(String uri); 
  String prefixedName2URI(String prefixedName);
} // OWLConversionFactory
  
