
package edu.stanford.smi.protegex.owl.swrl.bridge;

import java.util.List;
import java.util.Set;

import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.DatatypeConversionException;
import edu.stanford.smi.protegex.owl.swrl.bridge.exceptions.OWLFactoryException;
import edu.stanford.smi.protegex.owl.swrl.bridge.xsd.XSDType;

/**
 ** Factory to create OWLAPI-like entities. Provides a rough starting point for a port to the OWLAPI. 
 */
public interface OWLFactory
{
  Set<SWRLRule> getSWRLRules() throws OWLFactoryException;
  BuiltInAtom getSWRLBuiltInAtom(String builtInURI, String builtInPrefixedName, List<BuiltInArgument> arguments);

  OWLClass getOWLClass();
  OWLClass getOWLClass(String classURI);
  
  OWLIndividual getOWLIndividual(String individualURI);
  
  OWLObjectProperty getOWLObjectProperty(String propertyURI);
  OWLDatatypeProperty getOWLDataProperty(String propertyURI);
  
  OWLDatatypeValue getOWLDataValue(Object o) throws DatatypeConversionException; // TODO: get rid of this
  OWLDatatypeValue getOWLDataValue(String s);
  OWLDatatypeValue getOWLDataValue(Number n);
  OWLDatatypeValue getOWLDataValue(boolean b);
  OWLDatatypeValue getOWLDataValue(int i);
  OWLDatatypeValue getOWLDataValue(long l);
  OWLDatatypeValue getOWLDataValue(float f);
  OWLDatatypeValue getOWLDataValue(double d);
  OWLDatatypeValue getOWLDataValue(short s);
  OWLDatatypeValue getOWLDataValue(Byte b);
  OWLDatatypeValue getOWLDataValue(XSDType xsd);

  OWLDatatypePropertyAssertionAxiom getOWLDataPropertyAssertionAxiom(OWLIndividual subject, OWLProperty property, OWLDatatypeValue object);
  OWLObjectPropertyAssertionAxiom getOWLObjectPropertyAssertionAxiom(OWLIndividual subject, OWLProperty property, OWLIndividual object);
  OWLDifferentIndividualsAxiom getOWLDifferentIndividualsAxiom(OWLIndividual individual1, OWLIndividual individual2);
  OWLDifferentIndividualsAxiom getOWLDifferentIndividualsAxiom(Set<OWLIndividual> individuals);
  OWLSameIndividualsAxiom getOWLSameIndividualsAxiom(OWLIndividual individual1, OWLIndividual individual2);
  OWLClassPropertyAssertionAxiom getOWLClassPropertyAssertionAxiom(OWLIndividual subject, OWLProperty property, OWLClass object);
  OWLPropertyPropertyAssertionAxiom getOWLPropertyPropertyAssertionAxiom(OWLIndividual subject, OWLProperty property, OWLProperty object);
  OWLClassAssertionAxiom getOWLClassAssertionAxiom(OWLIndividual individual, OWLClass description);
  OWLSubClassAxiom getOWLSubClassAxiom(OWLClass subClass, OWLClass superClass);
  OWLSomeValuesFrom getOWLSomeValuesFrom(OWLClass owlClass, OWLProperty onProperty, OWLClass someValuesFrom);
  OWLDeclarationAxiom getOWLDeclarationAxiom(OWLEntity owlEntity);

  VariableAtomArgument getSWRLVariableAtomArgument(String variableURI, String prefixedVariableName);
  VariableBuiltInArgument getSWRLVariableBuiltInArgument(String variableURI, String prefixedVariableName);
  BuiltInArgument getSWRLBuiltInArgument(String variableURI, String prefixedVariableName);
} // OWLFactory
